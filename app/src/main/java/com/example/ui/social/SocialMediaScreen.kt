package com.example.ui.social

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiService
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CreditChip
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
import com.example.ui.components.CyberTextField
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val PLATFORMS = listOf("Instagram", "YouTube Shorts", "Facebook", "TikTok", "X / Twitter", "LinkedIn")

private val CALENDAR_DAYS = listOf(
  Triple("MON", "YouTube Long-form", "AI Tools Deep Dive"),
  Triple("TUE", "Instagram Reel", "Hook Breakdown"),
  Triple("WED", "YouTube Short", "Behind the Scenes"),
  Triple("THU", "X Thread", "Creator Monetization Blueprint"),
  Triple("FRI", "Instagram Carousel", "5 Prompt Templates"),
  Triple("SAT", "Weekly Recap", "Community Q&A")
)

@Composable
fun SocialMediaScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedPlatform by remember { mutableStateOf("Instagram") }
  var contentIdea by remember { mutableStateOf("") }
  var isGenerating by remember { mutableStateOf(false) }
  var generatedSuite by remember { mutableStateOf("") }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberDark)
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Spacer(modifier = Modifier.height(10.dp))
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        IconButton(onClick = onBack) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = TextPrimary
          )
        }
        Text(
          text = "SOCIAL MEDIA CREATOR SUITE",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        CreditChip(credits = credits)
      }
    }

    // Platform selector
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CyberHeader(
            title = "Target Platform",
            subtitle = "Customizes formatting, hashtags & character limits",
            badgeText = "COST: 5 CREDITS"
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(PLATFORMS) { platform ->
              val isSelected = platform == selectedPlatform
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) NeonAmber.copy(alpha = 0.2f) else CyberSurfaceElevated)
                  .border(1.dp, if (isSelected) NeonAmber else CyberBorder, RoundedCornerShape(10.dp))
                  .clickable { selectedPlatform = platform }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = platform,
                  color = if (isSelected) NeonAmber else TextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }

    // Idea Input
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CyberTextField(
            value = contentIdea,
            onValueChange = { contentIdea = it },
            label = "Enter post idea, transcript, or draft...",
            placeholder = "e.g. Announcing the launch of our new AI Creator app with 150 free credits",
            singleLine = false,
            maxLines = 4
          )
        }
      }
    }

    // Action Button
    item {
      AnimatedVisibility(visible = errorMessage != null) {
        CyberCard(
          borderColor = MaterialTheme.colorScheme.error,
          backgroundColor = MaterialTheme.colorScheme.error.copy(alpha = 0.15f)
        ) {
          Text(text = errorMessage.orEmpty(), color = MaterialTheme.colorScheme.error, fontSize = 12.sp)
        }
      }

      CyberButton(
        text = if (isGenerating) "Generating Optimized Post..." else "Generate $selectedPlatform Suite",
        icon = Icons.Default.Share,
        isLoading = isGenerating,
        enabled = contentIdea.isNotBlank() && credits >= 5 && !isGenerating,
        containerColor = NeonAmber,
        contentColor = CyberDark,
        onClick = {
          scope.launch {
            errorMessage = null
            isGenerating = true
            try {
              val deducted = repository.deductCredits(userId, 5L, "social_suite")
              if (!deducted) {
                errorMessage = "Insufficient credits."
                isGenerating = false
                return@launch
              }

              val result = aiService.generateSocialSuite(contentIdea, selectedPlatform)
              generatedSuite = result

              repository.createProject(
                userId = userId,
                title = "$selectedPlatform: " + contentIdea.take(30),
                type = "script",
                description = "Platform Post & Hashtags"
              )

              Toast.makeText(context, "Social post generated!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
              errorMessage = "Generation error: ${e.message}"
            } finally {
              isGenerating = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Generated Suite Output
    if (generatedSuite.isNotBlank()) {
      item {
        CyberCard(
          modifier = Modifier.fillMaxWidth(),
          borderColor = NeonGreen.copy(alpha = 0.4f)
        ) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              CyberBadge(text = "$selectedPlatform POST SUITE", color = NeonGreen)
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("Social Post", generatedSuite)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "Copied to clipboard!", Toast.LENGTH_SHORT).show()
                }
              ) {
                Icon(imageVector = Icons.Default.ContentCopy, contentDescription = "Copy", tint = NeonGreen)
              }
            }

            Surface(
              color = CyberSurfaceElevated,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = generatedSuite,
                color = TextPrimary,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                modifier = Modifier.padding(14.dp)
              )
            }
          }
        }
      }
    }

    // Creator Content Calendar
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.CalendarToday, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("Weekly Content Publishing Calendar", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            CALENDAR_DAYS.forEach { (day, platform, topic) ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(8.dp))
                  .background(CyberSurfaceElevated)
                  .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  CyberBadge(text = day, color = NeonPurple)
                  Spacer(modifier = Modifier.width(10.dp))
                  Text(text = topic, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
                Text(text = platform, color = NeonCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
