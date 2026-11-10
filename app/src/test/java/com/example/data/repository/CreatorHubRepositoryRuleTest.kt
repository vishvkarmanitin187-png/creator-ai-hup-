package com.example.data.repository

import com.example.base.FirestoreEmulatorTestBase
import com.example.data.model.Project
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import java.util.UUID

class CreatorHubRepositoryRuleTest : FirestoreEmulatorTestBase() {

  @Test
  fun createProject_authenticatedUser_createsDocumentSuccessfully() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CreatorHubRepository(firestore)

    val projId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createProject(
        userId = uid,
        title = "Cyberpunk Reel #1",
        type = "video",
        description = "Viral tech teaser"
      )
    }

    assertTrue(projId.isNotEmpty())
  }

  @Test
  fun observeProjects_authenticatedOwner_emitsCreatedProject() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CreatorHubRepository(firestore)

    val uniqueTitle = "Project-" + UUID.randomUUID().toString().take(8)
    val projId = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.createProject(
        userId = uid,
        title = uniqueTitle,
        type = "image",
        description = "AI Thumbnail"
      )
    }

    val emittedList = withTimeout(FLOW_TIMEOUT_MS) {
      repository.observeProjects(uid).first { list ->
        list.any { it.id == projId }
      }
    }

    assertTrue(emittedList.any { it.id == projId && it.title == uniqueTitle })
  }

  @Test
  fun observeProjects_unauthenticatedUser_failsWithPermissionDenied() = runBlocking {
    auth.signOut()
    val repository = CreatorHubRepository(firestore)

    try {
      withTimeout(FLOW_TIMEOUT_MS) {
        repository.observeProjects("unauth_user_id").first()
      }
      fail("Expected exception when querying unauthenticated")
    } catch (e: Exception) {
      val firestoreException = e.unwrapFirestoreException()
      assertNotNull("Expected root FirebaseFirestoreException", firestoreException)
      assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }
  }

  @Test
  fun userProfile_ensureAndCreditsManagement() = runBlocking {
    val uid = signInTestUser(ALICE_EMAIL)
    val repository = CreatorHubRepository(firestore)

    val profile = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.ensureUserProfile(
        userId = uid,
        displayName = "Alice Creator",
        email = ALICE_EMAIL,
        photoUrl = null
      )
    }

    assertEquals(150L, profile.credits)

    val deducted = withTimeout(DEFAULT_TIMEOUT_MS) {
      repository.deductCredits(uid, 25L, "video_gen")
    }
    assertTrue(deducted)
  }

  private companion object {
    const val ALICE_EMAIL = "alice.creator@test.com"
    const val DEFAULT_TIMEOUT_MS = 6000L
    const val FLOW_TIMEOUT_MS = 5000L
  }
}
