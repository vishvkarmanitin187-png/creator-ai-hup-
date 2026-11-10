package com.example.data.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@IgnoreExtraProperties
data class UserProfile(
  @DocumentId val id: String = "",
  val userId: String = "",
  val displayName: String = "",
  val email: String = "",
  val photoUrl: String? = null,
  val credits: Long = 100L,
  val plan: String = "free", // "free", "creator", "pro"
  val storageUsedBytes: Long = 0L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Project(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val description: String? = null,
  val category: String? = "general",
  val type: String = "video", // "video", "image", "script", "audio", "editor"
  val status: String = "active",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GeneratedVideo(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val prompt: String = "",
  val script: String? = null,
  val style: String? = "cinematic",
  val aspectRatio: String = "9:16", // "9:16", "16:9", "1:1"
  val resolution: String = "1080p",
  val videoUrl: String? = null,
  val thumbnailUrl: String? = null,
  val status: String = "completed", // "pending", "processing", "completed", "failed"
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GeneratedImage(
  @DocumentId val id: String = "",
  val userId: String = "",
  val prompt: String = "",
  val style: String? = "Cyberpunk",
  val aspectRatio: String? = "1:1",
  val imageUrl: String? = null,
  val base64Preview: String? = null,
  val status: String = "completed",
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class GeneratedAudio(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val text: String = "",
  val language: String = "en", // "en", "hi", "hinglish"
  val voiceName: String = "Zephyr (Male)",
  val speed: Double = 1.0,
  val pitch: Double = 1.0,
  val audioUrl: String? = null,
  val durationSeconds: Long = 15L,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class EditingProject(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "Untitled Edit",
  val durationSeconds: Long = 30L,
  val tracksJson: String? = null,
  val status: String = "draft",
  val exportedUrl: String? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Subscription(
  @DocumentId val id: String = "",
  val userId: String = "",
  val planId: String = "free", // "free", "creator", "pro"
  val status: String = "active",
  val renewalDate: String? = null,
  @ServerTimestamp val createdAt: Timestamp? = null,
  @ServerTimestamp val updatedAt: Timestamp? = null
)

@IgnoreExtraProperties
data class Transaction(
  @DocumentId val id: String = "",
  val userId: String = "",
  val amount: Double = 0.0,
  val creditsAdded: Long = 0L,
  val type: String = "welcome_bonus",
  val status: String = "completed",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class AiUsage(
  @DocumentId val id: String = "",
  val userId: String = "",
  val operation: String = "", // "video_gen", "image_gen", "script_gen", "tts", "chat"
  val creditsDeducted: Long = 0L,
  val model: String = "gemini-3.8-flash",
  @ServerTimestamp val createdAt: Timestamp? = null
)

@IgnoreExtraProperties
data class NotificationItem(
  @DocumentId val id: String = "",
  val userId: String = "",
  val title: String = "",
  val message: String = "",
  val type: String = "info",
  val isRead: Boolean = false,
  @ServerTimestamp val createdAt: Timestamp? = null
)
