package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiService
import com.example.data.model.NotificationItem
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.example.data.repository.CreatorHubRepository
import com.example.ui.admin.AdminPanelScreen
import com.example.ui.auth.AuthScreen
import com.example.ui.chat.CreatorChatScreen
import com.example.ui.dashboard.DashboardScreen
import com.example.ui.dashboard.HubDestination
import com.example.ui.editor.VideoEditorScreen
import com.example.ui.image.ImageStudioScreen
import com.example.ui.projects.ProjectsScreen
import com.example.ui.scripts.ScriptWriterScreen
import com.example.ui.social.SocialMediaScreen
import com.example.ui.store.PricingScreen
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.video.VideoStudioScreen
import com.example.ui.voice.VoiceStudioScreen
import com.example.ui.youtube.YouTubeStudioScreen
import com.example.util.configureAppCheck
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    // MANDATORY: Configure App Check with launch intent before any UI or Firebase operations
    configureAppCheck(this, intent)

    enableEdgeToEdge()

    setContent {
      MyApplicationTheme {
        Surface(
          modifier = Modifier.fillMaxSize(),
          color = CyberDark
        ) {
          AppRoot()
        }
      }
    }
  }
}

@Composable
fun AppRoot() {
  var currentUser by remember { mutableStateOf(Firebase.auth.currentUser) }

  DisposableEffect(Unit) {
    val listener = FirebaseAuth.AuthStateListener { auth ->
      currentUser = auth.currentUser
    }
    Firebase.auth.addAuthStateListener(listener)
    onDispose {
      Firebase.auth.removeAuthStateListener(listener)
    }
  }

  if (currentUser == null) {
    AuthScreen(
      onAuthSuccess = {
        currentUser = Firebase.auth.currentUser
      }
    )
  } else {
    val uid = currentUser!!.uid
    key(uid) {
      CreatorHubMain(
        userId = uid,
        displayName = currentUser?.displayName ?: "Creator",
        email = currentUser?.email ?: "",
        photoUrl = currentUser?.photoUrl?.toString(),
        onSignOut = {
          currentUser = null
        }
      )
    }
  }
}

@Composable
fun CreatorHubMain(
  userId: String,
  displayName: String,
  email: String,
  photoUrl: String?,
  onSignOut: () -> Unit
) {
  val context = androidx.compose.ui.platform.LocalContext.current
  val scope = rememberCoroutineScope()

  val repository = remember { CreatorHubRepository(context) }
  val aiService = remember { AiService() }

  // Initial user profile check
  LaunchedEffect(userId) {
    repository.ensureUserProfile(
      userId = userId,
      displayName = displayName,
      email = email,
      photoUrl = photoUrl
    )
  }

  // Real-time state observation
  val userProfile by repository.observeUserProfile(userId).collectAsState(initial = null)
  val projects by repository.observeProjects(userId).collectAsState(initial = emptyList())
  val notifications by repository.observeNotifications(userId).collectAsState(initial = emptyList())

  var currentDestination by remember { mutableStateOf(HubDestination.DASHBOARD) }

  Scaffold(
    bottomBar = {
      // Bottom navigation bar for fast switching between primary studios
      CyberBottomNav(
        current = currentDestination,
        onSelect = { currentDestination = it }
      )
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      AnimatedContent(
        targetState = currentDestination,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "HubNavigation"
      ) { target ->
        when (target) {
          HubDestination.DASHBOARD -> DashboardScreen(
            userProfile = userProfile,
            projects = projects,
            notifications = notifications,
            onNavigate = { currentDestination = it },
            onSignOut = onSignOut,
            scope = scope
          )
          HubDestination.VIDEO_STUDIO -> VideoStudioScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.IMAGE_STUDIO -> ImageStudioScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.VOICE_STUDIO -> VoiceStudioScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.VIDEO_EDITOR -> VideoEditorScreen(
            userId = userId,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.SCRIPT_WRITER -> ScriptWriterScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.YOUTUBE_STUDIO -> YouTubeStudioScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.SOCIAL_STUDIO -> SocialMediaScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.AI_CHAT -> CreatorChatScreen(
            userId = userId,
            credits = userProfile?.credits ?: 0L,
            aiService = aiService,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.PROJECTS -> ProjectsScreen(
            userId = userId,
            projects = projects,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.PRICING -> PricingScreen(
            userId = userId,
            userProfile = userProfile,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
          HubDestination.ADMIN -> AdminPanelScreen(
            userId = userId,
            repository = repository,
            onBack = { currentDestination = HubDestination.DASHBOARD }
          )
        }
      }
    }
  }
}

@Composable
fun CyberBottomNav(
  current: HubDestination,
  onSelect: (HubDestination) -> Unit
) {
  val navItems = listOf(
    Triple(HubDestination.DASHBOARD, "Dashboard", Icons.Default.Home),
    Triple(HubDestination.VIDEO_STUDIO, "Video", Icons.Default.Movie),
    Triple(HubDestination.IMAGE_STUDIO, "Image", Icons.Default.Palette),
    Triple(HubDestination.VOICE_STUDIO, "Voice", Icons.Default.Mic),
    Triple(HubDestination.SCRIPT_WRITER, "Scripts", Icons.Default.AutoAwesome),
    Triple(HubDestination.AI_CHAT, "Assistant", Icons.Default.Chat)
  )

  NavigationBar(
    containerColor = CyberSurface,
    contentColor = TextPrimary,
    tonalElevation = 8.dp,
    modifier = Modifier
      .height(68.dp)
      .border(androidx.compose.foundation.BorderStroke(1.dp, CyberBorder))
  ) {
    navItems.forEach { (dest, label, icon) ->
      val selected = current == dest
      NavigationBarItem(
        selected = selected,
        onClick = { onSelect(dest) },
        icon = {
          Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(20.dp),
            tint = if (selected) NeonCyan else TextMuted
          )
        },
        label = {
          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
            color = if (selected) NeonCyan else TextMuted
          )
        },
        colors = NavigationBarItemDefaults.colors(
          indicatorColor = NeonCyan.copy(alpha = 0.15f)
        )
      )
    }
  }
}
