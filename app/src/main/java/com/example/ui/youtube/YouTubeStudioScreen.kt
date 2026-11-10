package com.example.ui.youtube

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.TrendingUp
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
import androidx.compose.ui.graphics.Color
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
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun YouTubeStudioScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var videoTopic by remember { mutableStateOf("") }
  var targetAudience by remember { mutableStateOf("Tech Enthusiasts & Creators") }

  var isGenerating by remember { mutableStateOf(false) }
  var generatedPackage by remember { mutableStateOf("") }
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
          text = "YOUTUBE GROWTH STUDIO",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        CreditChip(credits = credits)
      }
    }

    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          CyberHeader(
            title = "Video Algorithm Optimizer",
            subtitle = "Generate High-CTR Titles, SEO Descriptions, Thumbnail Prompts & Tags",
            badgeText = "COST: 5 CREDITS"
          )
          CyberTextField(
            value = videoTopic,
            onValueChange = { videoTopic = it },
            label = "Enter your video concept or title...",
            placeholder = "e.g. How I Built a Million Dollar Mobile App with AI"
          )
          CyberTextField(
            value = targetAudience,
            onValueChange = { targetAudience = it },
            label = "Target Audience Niche...",
            placeholder = "e.g. Young programmers, SaaS founders, student creators"
          )
        }
      }
    }

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
        text = if (isGenerating) "Optimizing for YouTube Algorithm..." else "Generate YouTube SEO Package",
        icon = Icons.Default.TrendingUp,
        isLoading = isGenerating,
        enabled = videoTopic.isNotBlank() && credits >= 5 && !isGenerating,
        containerColor = Color(0xFFFF4B4B),
        contentColor = Color.White,
        onClick = {
          scope.launch {
            errorMessage = null
            isGenerating = true
            try {
              val deducted = repository.deductCredits(userId, 5L, "youtube_seo")
              if (!deducted) {
                errorMessage = "Insufficient credits for SEO optimization."
                isGenerating = false
                return@launch
              }

              val result = aiService.generateYouTubeOptimization(videoTopic, targetAudience)
              generatedPackage = result

              repository.createProject(
                userId = userId,
                title = "YouTube SEO: " + videoTopic.take(30),
                type = "script",
                description = "High-CTR Titles & SEO Package"
              )

              Toast.makeText(context, "Optimization generated!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
              errorMessage = "Optimization error: ${e.message}"
            } finally {
              isGenerating = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }

    if (generatedPackage.isNotBlank()) {
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
              CyberBadge(text = "GROWTH PACKAGE READY", color = NeonGreen)
              IconButton(
                onClick = {
                  val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                  val clip = ClipData.newPlainText("YouTube SEO", generatedPackage)
                  clipboard.setPrimaryClip(clip)
                  Toast.makeText(context, "SEO package copied!", Toast.LENGTH_SHORT).show()
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
                text = generatedPackage,
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

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
