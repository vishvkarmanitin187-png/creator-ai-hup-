package com.example.data.repository

import android.content.Context
import com.example.R
import com.example.data.model.AiUsage
import com.example.data.model.EditingProject
import com.example.data.model.GeneratedAudio
import com.example.data.model.GeneratedImage
import com.example.data.model.GeneratedVideo
import com.example.data.model.NotificationItem
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.example.util.OperationType
import com.example.util.handleFirestoreError
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class CreatorHubRepository(val db: FirebaseFirestore) {

  constructor(context: Context) : this(
    FirebaseFirestore.getInstance(
      context.applicationContext.getString(R.string.firestore_database_id)
    )
  )

  private val auth get() = Firebase.auth

  private fun requireUserId(): String {
    return auth.currentUser?.uid
      ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
  }

  // --- USER PROFILE & CREDITS ---

  fun observeUserProfile(userId: String): Flow<UserProfile?> = flow {
    val docRef = db.collection("users").document(userId)
    emitAll(
      docRef.snapshots()
        .map { it.toObject(UserProfile::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.GET, docRef.path)
          }
          throw error
        }
    )
  }

  suspend fun ensureUserProfile(
    userId: String,
    displayName: String,
    email: String,
    photoUrl: String?
  ): UserProfile {
    val docRef = db.collection("users").document(userId)
    val snap = docRef.get().await()
    if (snap.exists()) {
      return snap.toObject(UserProfile::class.java) ?: UserProfile(userId = userId, displayName = displayName, email = email)
    }

    val initialProfile = mapOf(
      "userId" to userId,
      "displayName" to displayName.ifBlank { "Creator" },
      "email" to email,
      "photoUrl" to photoUrl,
      "credits" to 150L, // 150 welcome bonus credits!
      "plan" to "free",
      "storageUsedBytes" to 0L,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )

    try {
      docRef.set(initialProfile).await()
      // Create initial welcome notification
      createNotification(
        userId = userId,
        title = "Welcome to Creator AI Hub!",
        message = "You received 150 free AI credits to build scripts, videos, and images.",
        type = "credit"
      )
    } catch (e: Exception) {
      handleFirestoreError(e, OperationType.CREATE, docRef.path)
      throw e
    }

    return UserProfile(
      userId = userId,
      displayName = displayName,
      email = email,
      photoUrl = photoUrl,
      credits = 150L,
      plan = "free"
    )
  }

  suspend fun deductCredits(
    userId: String,
    amount: Long,
    operation: String,
    modelName: String = "gemini-3.8-flash"
  ): Boolean {
    val docRef = db.collection("users").document(userId)
    val userSnap = docRef.get().await()
    val currentCredits = userSnap.getLong("credits") ?: 0L
    if (currentCredits < amount) {
      return false
    }

    // Atomic update
    docRef.update(
      "credits", FieldValue.increment(-amount),
      "updatedAt", FieldValue.serverTimestamp()
    ).await()

    // Log AI Usage
    val usageRef = db.collection("ai_usage").document()
    usageRef.set(
      mapOf(
        "userId" to userId,
        "operation" to operation,
        "creditsDeducted" to amount,
        "model" to modelName,
        "createdAt" to FieldValue.serverTimestamp()
      )
    ).await()

    return true
  }

  suspend fun addCredits(userId: String, amount: Long, type: String) {
    val docRef = db.collection("users").document(userId)
    docRef.update(
      "credits", FieldValue.increment(amount),
      "updatedAt", FieldValue.serverTimestamp()
    ).await()

    val transRef = db.collection("transactions").document()
    transRef.set(
      mapOf(
        "userId" to userId,
        "creditsAdded" to amount,
        "type" to type,
        "status" to "completed",
        "createdAt" to FieldValue.serverTimestamp()
      )
    ).await()

    createNotification(
      userId = userId,
      title = "Credits Added",
      message = "Successfully added $amount AI credits to your balance.",
      type = "credit"
    )
  }

  suspend fun updatePlan(userId: String, plan: String) {
    val docRef = db.collection("users").document(userId)
    docRef.update(
      "plan", plan,
      "updatedAt", FieldValue.serverTimestamp()
    ).await()

    createNotification(
      userId = userId,
      title = "Plan Updated",
      message = "Your membership has been upgraded to $plan plan.",
      type = "system"
    )
  }

  // --- PROJECTS ---

  fun observeProjects(userId: String): Flow<List<Project>> = flow {
    val path = "projects"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(Project::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun createProject(
    userId: String,
    title: String,
    type: String,
    description: String? = null,
    category: String? = "general"
  ): String {
    val docRef = db.collection("projects").document()
    val payload = mapOf(
      "userId" to userId,
      "title" to title,
      "type" to type,
      "description" to description,
      "category" to category,
      "status" to "active",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  suspend fun deleteProject(projectId: String) {
    db.collection("projects").document(projectId).delete().await()
  }

  // --- GENERATED VIDEOS ---

  fun observeVideos(userId: String): Flow<List<GeneratedVideo>> = flow {
    val path = "generated_videos"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(GeneratedVideo::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveVideo(
    userId: String,
    title: String,
    prompt: String,
    aspectRatio: String,
    resolution: String = "1080p",
    style: String = "cinematic",
    script: String? = null,
    thumbnailUrl: String? = null,
    videoUrl: String? = null
  ): String {
    val docRef = db.collection("generated_videos").document()
    val payload = mapOf(
      "userId" to userId,
      "title" to title,
      "prompt" to prompt,
      "script" to script,
      "style" to style,
      "aspectRatio" to aspectRatio,
      "resolution" to resolution,
      "status" to "completed",
      "thumbnailUrl" to thumbnailUrl,
      "videoUrl" to videoUrl,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  // --- GENERATED IMAGES ---

  fun observeImages(userId: String): Flow<List<GeneratedImage>> = flow {
    val path = "generated_images"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(GeneratedImage::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveImage(
    userId: String,
    prompt: String,
    style: String,
    aspectRatio: String,
    imageUrl: String? = null,
    base64Preview: String? = null
  ): String {
    val docRef = db.collection("generated_images").document()
    val payload = mapOf(
      "userId" to userId,
      "prompt" to prompt,
      "style" to style,
      "aspectRatio" to aspectRatio,
      "imageUrl" to imageUrl,
      "base64Preview" to base64Preview,
      "status" to "completed",
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  // --- GENERATED AUDIO ---

  fun observeAudios(userId: String): Flow<List<GeneratedAudio>> = flow {
    val path = "generated_audio"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(GeneratedAudio::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveAudio(
    userId: String,
    title: String,
    text: String,
    language: String,
    voiceName: String,
    speed: Double,
    pitch: Double,
    durationSeconds: Long = 15L,
    audioUrl: String? = null
  ): String {
    val docRef = db.collection("generated_audio").document()
    val payload = mapOf(
      "userId" to userId,
      "title" to title,
      "text" to text,
      "language" to language,
      "voiceName" to voiceName,
      "speed" to speed,
      "pitch" to pitch,
      "durationSeconds" to durationSeconds,
      "audioUrl" to audioUrl,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  // --- EDITING PROJECTS ---

  fun observeEditingProjects(userId: String): Flow<List<EditingProject>> = flow {
    val path = "editing_projects"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(EditingProject::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun saveEditingProject(
    userId: String,
    title: String,
    durationSeconds: Long,
    tracksJson: String,
    exportedUrl: String? = null
  ): String {
    val docRef = db.collection("editing_projects").document()
    val payload = mapOf(
      "userId" to userId,
      "title" to title,
      "durationSeconds" to durationSeconds,
      "tracksJson" to tracksJson,
      "status" to if (exportedUrl != null) "exported" else "draft",
      "exportedUrl" to exportedUrl,
      "createdAt" to FieldValue.serverTimestamp(),
      "updatedAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  // --- NOTIFICATIONS ---

  fun observeNotifications(userId: String): Flow<List<NotificationItem>> = flow {
    val path = "notifications"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(NotificationItem::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }

  suspend fun createNotification(
    userId: String,
    title: String,
    message: String,
    type: String = "info"
  ): String {
    val docRef = db.collection("notifications").document()
    val payload = mapOf(
      "userId" to userId,
      "title" to title,
      "message" to message,
      "type" to type,
      "isRead" to false,
      "createdAt" to FieldValue.serverTimestamp()
    )
    docRef.set(payload).await()
    return docRef.id
  }

  suspend fun markNotificationRead(notifId: String) {
    db.collection("notifications").document(notifId).update("isRead", true).await()
  }

  // --- AI USAGE LOGS ---

  fun observeUsage(userId: String): Flow<List<AiUsage>> = flow {
    val path = "ai_usage"
    emitAll(
      db.collection(path)
        .whereEqualTo("userId", userId)
        .snapshots()
        .map { it.toObjects(AiUsage::class.java) }
        .catch { error ->
          val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
          if (firestoreError == null && error is CancellationException) throw error
          if (firestoreError != null && auth.currentUser?.uid == userId) {
            handleFirestoreError(firestoreError, OperationType.LIST, path)
          }
          throw error
        }
    )
  }
}
