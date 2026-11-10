package com.example.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.credentials.CredentialManager
import com.example.data.model.NotificationItem
import com.example.data.model.Project
import com.example.data.model.UserProfile
import com.example.ui.auth.signOutUser
import com.example.ui.components.CreditChip
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberGradientCard
import com.example.ui.components.CyberHeader
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.CoroutineScope

enum class HubDestination {
  DASHBOARD,
  VIDEO_STUDIO,
  IMAGE_STUDIO,
  VOICE_STUDIO,
  VIDEO_EDITOR,
  SCRIPT_WRITER,
  YOUTUBE_STUDIO,
  SOCIAL_STUDIO,
  AI_CHAT,
  PROJECTS,
  PRICING,
  ADMIN
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
  userProfile: UserProfile?,
  projects: List<Project>,
  notifications: List<NotificationItem>,
  onNavigate: (HubDestination) -> Unit,
  onSignOut: () -> Unit,
  scope: CoroutineScope
) {
  val context = LocalContext.current
  val credentialManager = remember { CredentialManager.create(context) }
  var showProfileDialog by remember { mutableStateOf(false) }
  var showNotificationsDialog by remember { mutableStateOf(false) }

  val unreadCount = notifications.count { !it.isRead }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberDark),
    contentPadding = PaddingValues(bottom = 96.dp)
  ) {
    // 1. Top Bar with User Info & Badges
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.clickable { showProfileDialog = true }
        ) {
          Box(
            modifier = Modifier
              .size(44.dp)
              .clip(CircleShape)
              .border(1.5.dp, Brush.linearGradient(listOf(NeonCyan, NeonPurple)), CircleShape)
              .background(CyberSurfaceElevated),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Person,
              contentDescription = "Profile",
              tint = NeonCyan,
              modifier = Modifier.size(24.dp)
            )
          }
          Spacer(modifier = Modifier.width(12.dp))
          Column {
            Text(
              text = userProfile?.displayName?.ifBlank { "Creator" } ?: "Creator",
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 16.sp
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
              CyberBadge(
                text = (userProfile?.plan ?: "free").uppercase() + " PLAN",
                color = when (userProfile?.plan) {
                  "pro" -> NeonPink
                  "creator" -> NeonPurple
                  else -> NeonCyan
                }
              )
            }
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          CreditChip(
            credits = userProfile?.credits ?: 0L,
            onClick = { onNavigate(HubDestination.PRICING) }
          )
          Spacer(modifier = Modifier.width(8.dp))
          IconButton(onClick = { showNotificationsDialog = true }) {
            BadgedBox(
              badge = {
                if (unreadCount > 0) {
                  Badge(containerColor = NeonPink) { Text("$unreadCount") }
                }
              }
            ) {
              Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = "Notifications",
                tint = TextPrimary
              )
            }
          }
        }
      }
    }

    // 2. Futuristic Welcome Hero Card
    item {
      Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        CyberGradientCard(
          modifier = Modifier.fillMaxWidth()
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CyberBadge(text = "AI ENGINE ONLINE", color = NeonGreen)
              Text(
                text = "v2.5 PRO",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = "CREATE ANYTHING.\nGROW EVERYWHERE.",
              color = TextPrimary,
              fontSize = 20.sp,
              fontWeight = FontWeight.Black,
              lineHeight = 26.sp,
              letterSpacing = 0.5.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
              text = "Generate cinematic videos, hyper-realistic images, viral scripts, and voiceovers powered by real AI.",
              color = TextSecondary,
              fontSize = 13.sp,
              lineHeight = 18.sp
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              CyberButton(
                text = "New Video",
                icon = Icons.Default.Movie,
                onClick = { onNavigate(HubDestination.VIDEO_STUDIO) },
                modifier = Modifier.weight(1f)
              )
              CyberButton(
                text = "AI Script",
                icon = Icons.Default.AutoAwesome,
                containerColor = NeonPurple,
                contentColor = Color.White,
                onClick = { onNavigate(HubDestination.SCRIPT_WRITER) },
                modifier = Modifier.weight(1f)
              )
            }
          }
        }
      }
    }

    // 3. Quick AI Studios Grid
    item {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
        CyberHeader(
          title = "AI Studios & Tools",
          subtitle = "Production-grade generative AI modules"
        )
        Spacer(modifier = Modifier.height(14.dp))

        // Grid rows of 2 cards
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          StudioCard(
            title = "AI Video Studio",
            subtitle = "Text & Script to Video",
            icon = Icons.Default.Movie,
            accentColor = NeonCyan,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.VIDEO_STUDIO) }
          )
          StudioCard(
            title = "AI Image Studio",
            subtitle = "Art & Thumbnails",
            icon = Icons.Default.Palette,
            accentColor = NeonPurple,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.IMAGE_STUDIO) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          StudioCard(
            title = "AI Voice Studio",
            subtitle = "TTS Hindi & English",
            icon = Icons.Default.Mic,
            accentColor = NeonGreen,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.VOICE_STUDIO) }
          )
          StudioCard(
            title = "AI Video Editor",
            subtitle = "Trim, Cuts & Filters",
            icon = Icons.Default.ContentCut,
            accentColor = NeonPink,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.VIDEO_EDITOR) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          StudioCard(
            title = "YouTube Growth",
            subtitle = "Titles, SEO & Tags",
            icon = Icons.Default.TrendingUp,
            accentColor = Color(0xFFFF4B4B),
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.YOUTUBE_STUDIO) }
          )
          StudioCard(
            title = "Social Suite",
            subtitle = "Captions & Calendar",
            icon = Icons.Default.Share,
            accentColor = NeonAmber,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.SOCIAL_STUDIO) }
          )
        }

        Spacer(modifier = Modifier.height(12.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          StudioCard(
            title = "AI Assistant",
            subtitle = "Creator Chatbot",
            icon = Icons.Default.Chat,
            accentColor = NeonCyan,
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.AI_CHAT) }
          )
          StudioCard(
            title = "Project Library",
            subtitle = "Saved Works & History",
            icon = Icons.Default.Folder,
            accentColor = Color(0xFF60A5FA),
            modifier = Modifier.weight(1f),
            onClick = { onNavigate(HubDestination.PROJECTS) }
          )
        }
      }
    }

    // 4. Quick Storage & Stats Section
    item {
      Box(modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp)) {
        CyberCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = CyberBorder
        ) {
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.CloudUpload,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Cloud Project Storage",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
              }
              Text(
                text = "120 MB / 5 GB",
                color = NeonCyan,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
              )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
              progress = { 0.05f },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = NeonCyan,
              trackColor = CyberSurfaceVariant
            )
            Spacer(modifier = Modifier.height(12.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              StatPill(label = "Total Projects", value = "${projects.size}")
              StatPill(label = "Plan Status", value = (userProfile?.plan ?: "free").uppercase())
              StatPill(label = "Credits Left", value = "${userProfile?.credits ?: 0}")
            }
          }
        }
      }
    }

    // 5. Recent Projects
    item {
      Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          CyberHeader(title = "Recent Projects", subtitle = "Your generated assets and drafts")
          TextButton(onClick = { onNavigate(HubDestination.PROJECTS) }) {
            Text("View All", color = NeonCyan, fontWeight = FontWeight.Bold)
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (projects.isEmpty()) {
          CyberCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = CyberSurfaceVariant.copy(alpha = 0.3f)
          ) {
            Column(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 20.dp),
              horizontalAlignment = Alignment.CenterHorizontally
            ) {
              Icon(
                imageVector = Icons.Default.VideoLibrary,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text("No projects yet", color = TextSecondary, fontWeight = FontWeight.Bold)
              Text("Tap any studio above to create your first content asset", color = TextMuted, fontSize = 12.sp)
            }
          }
        } else {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            projects.take(4).forEach { proj ->
              CyberCard(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onNavigate(HubDestination.PROJECTS) }
              ) {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                          when (proj.type) {
                            "video" -> NeonCyan.copy(alpha = 0.2f)
                            "image" -> NeonPurple.copy(alpha = 0.2f)
                            "voice" -> NeonGreen.copy(alpha = 0.2f)
                            else -> NeonPink.copy(alpha = 0.2f)
                          }
                        ),
                      contentAlignment = Alignment.Center
                    ) {
                      Icon(
                        imageVector = when (proj.type) {
                          "video" -> Icons.Default.Movie
                          "image" -> Icons.Default.Palette
                          "voice" -> Icons.Default.Mic
                          else -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        tint = when (proj.type) {
                          "video" -> NeonCyan
                          "image" -> NeonPurple
                          "voice" -> NeonGreen
                          else -> NeonPink
                        },
                        modifier = Modifier.size(20.dp)
                      )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                      Text(
                        text = proj.title,
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                      )
                      Text(
                        text = proj.type.uppercase(),
                        color = TextSecondary,
                        fontSize = 11.sp
                      )
                    }
                  }
                  CyberBadge(
                    text = proj.status.uppercase(),
                    color = if (proj.status == "completed") NeonGreen else NeonCyan
                  )
                }
              }
            }
          }
        }
      }
    }
  }

  // Profile Dialog
  if (showProfileDialog) {
    AlertDialog(
      onDismissRequest = { showProfileDialog = false },
      containerColor = CyberSurface,
      shape = RoundedCornerShape(18.dp),
      title = {
        Text("Creator Account", color = TextPrimary, fontWeight = FontWeight.Black)
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          Text("Name: ${userProfile?.displayName ?: "Creator"}", color = TextSecondary)
          Text("Email: ${userProfile?.email ?: "N/A"}", color = TextSecondary)
          Text("Credits: ${userProfile?.credits ?: 0}", color = NeonAmber, fontWeight = FontWeight.Bold)
          Text("Plan: ${(userProfile?.plan ?: "free").uppercase()}", color = NeonCyan, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(10.dp))
          CyberButton(
            text = "Upgrade Plan / Buy Credits",
            onClick = {
              showProfileDialog = false
              onNavigate(HubDestination.PRICING)
            },
            modifier = Modifier.fillMaxWidth()
          )
        }
      },
      confirmButton = {
        TextButton(
          onClick = {
            showProfileDialog = false
            signOutUser(credentialManager, scope) {
              onSignOut()
            }
          }
        ) {
          Text("Sign Out", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { showProfileDialog = false }) {
          Text("Close", color = TextSecondary)
        }
      }
    )
  }

  // Notifications Dialog
  if (showNotificationsDialog) {
    AlertDialog(
      onDismissRequest = { showNotificationsDialog = false },
      containerColor = CyberSurface,
      shape = RoundedCornerShape(18.dp),
      title = {
        Text("Notifications", color = TextPrimary, fontWeight = FontWeight.Black)
      },
      text = {
        if (notifications.isEmpty()) {
          Text("No notifications right now.", color = TextSecondary)
        } else {
          Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            notifications.take(5).forEach { notif ->
              CyberCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CyberSurfaceVariant
              ) {
                Column {
                  Text(text = notif.title, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                  Text(text = notif.message, color = TextPrimary, fontSize = 12.sp)
                }
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showNotificationsDialog = false }) {
          Text("Done", color = NeonCyan)
        }
      }
    )
  }
}

@Composable
private fun StudioCard(
  title: String,
  subtitle: String,
  icon: ImageVector,
  accentColor: Color,
  modifier: Modifier = Modifier,
  onClick: () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(16.dp))
      .border(BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)), RoundedCornerShape(16.dp))
      .background(CyberSurface.copy(alpha = 0.9f))
      .clickable(onClick = onClick)
      .padding(14.dp)
  ) {
    Column {
      Box(
        modifier = Modifier
          .size(38.dp)
          .clip(RoundedCornerShape(10.dp))
          .background(accentColor.copy(alpha = 0.15f)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = accentColor,
          modifier = Modifier.size(20.dp)
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = title,
        color = TextPrimary,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp
      )
      Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 11.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
      )
    }
  }
}

@Composable
private fun StatPill(label: String, value: String) {
  Column(horizontalAlignment = Alignment.CenterHorizontally) {
    Text(text = value, color = TextPrimary, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
    Text(text = label, color = TextSecondary, fontSize = 11.sp)
  }
}
