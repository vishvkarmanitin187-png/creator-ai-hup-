package com.example.ui.video

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private val VIDEO_TEMPLATES = listOf(
  "Cyberpunk Neon City Chase" to "High-speed hovering vehicle pursuit through neon-lit skyscrapers with rain and reflections",
  "Viral Tech Review Hook" to "Dynamic close-up product reveal of futuristic transparent smartphone with hologram UI",
  "Motivation Reel Climax" to "Athletic sprint up mountain peak at sunrise with dynamic cinematic drone camera orbit",
  "Product Advertisement" to "Luxury smartwatch floating in zero gravity with neon laser highlights and water splashes"
)

@Composable
fun VideoStudioScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var videoType by remember { mutableStateOf("Text-to-Video") }
  val videoTypes = listOf("Text-to-Video", "Script-to-Video", "AI Shorts (9:16)", "AI Reels", "Product Ad")

  var promptText by remember { mutableStateOf("") }
  var selectedRatio by remember { mutableStateOf("9:16") }
  val ratios = listOf("9:16", "16:9", "1:1")

  var selectedResolution by remember { mutableStateOf("1080p") }
  val resolutions = listOf("720p", "1080p", "4K Ultra")

  var selectedStyle by remember { mutableStateOf("Cinematic") }
  val styles = listOf("Cinematic", "Cyberpunk 3D", "Anime", "Photorealistic", "Surreal")

  var isGenerating by remember { mutableStateOf(false) }
  var progressStage by remember { mutableStateOf("") }
  var progressPercent by remember { mutableFloatStateOf(0f) }

  var generatedResult by remember { mutableStateOf<String?>(null) }
  var isPlayingPreview by remember { mutableStateOf(true) }
  var currentPlayFrame by remember { mutableIntStateOf(0) }
  var errorMessage by remember { mutableStateOf<String?>(null) }

  // Animated preview frames loop
  LaunchedEffect(isGenerating, generatedResult, isPlayingPreview) {
    if (generatedResult != null && isPlayingPreview) {
      while (true) {
        delay(80L)
        currentPlayFrame = (currentPlayFrame + 1) % 100
      }
    }
  }

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
          text = "AI VIDEO STUDIO",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        CreditChip(credits = credits)
      }
    }

    // Studio Mode Selector
    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(videoTypes) { type ->
          val isSelected = type == videoType
          Surface(
            color = if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(20.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, NeonCyan) else null,
            modifier = Modifier.clickable { videoType = type }
          ) {
            Text(
              text = type,
              color = if (isSelected) NeonCyan else TextSecondary,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Video Prompt Input
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CyberHeader(
            title = "Video Prompt & Story",
            subtitle = "Describe scene dynamics, camera movements, and subjects",
            badgeText = "COST: 25 CREDITS"
          )
          CyberTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = "Enter prompt or script concept...",
            placeholder = "e.g. Cyberpunk sports car drifting in neon Tokyo rain with slow-motion reflections",
            singleLine = false,
            maxLines = 4
          )

          // Templates quick pick
          Text("Quick AI Templates:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(VIDEO_TEMPLATES) { (title, prompt) ->
              Surface(
                color = CyberSurfaceElevated,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.clickable { promptText = prompt }
              ) {
                Text(
                  text = title,
                  color = NeonPurple,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
              }
            }
          }
        }
      }
    }

    // Video Parameters (Aspect Ratio, Resolution, Style)
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          Text("Video Configuration", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

          // Aspect Ratio
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Aspect Ratio", color = TextSecondary, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              ratios.forEach { ratio ->
                val selected = ratio == selectedRatio
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (selected) NeonCyan else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedRatio = ratio }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                  Text(text = ratio, color = if (selected) NeonCyan else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }

          // Resolution
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Resolution Quality", color = TextSecondary, fontSize = 12.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              resolutions.forEach { res ->
                val selected = res == selectedResolution
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) NeonPurple.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (selected) NeonPurple else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedResolution = res }
                    .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                  Text(text = res, color = if (selected) NeonPurple else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }

          // Style
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Visual Aesthetic Style", color = TextSecondary, fontSize = 12.sp)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(styles) { style ->
                val selected = style == selectedStyle
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (selected) NeonPink.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (selected) NeonPink else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedStyle = style }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                  Text(text = style, color = if (selected) NeonPink else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
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
        text = if (isGenerating) "Synthesizing Video Scenes..." else "Generate AI Video",
        icon = Icons.Default.Movie,
        isLoading = isGenerating,
        enabled = promptText.isNotBlank() && credits >= 25 && !isGenerating,
        onClick = {
          scope.launch {
            errorMessage = null
            isGenerating = true
            progressPercent = 0.1f
            progressStage = "Analyzing Prompt & Director Script..."

            try {
              // Deduct credits
              val deducted = repository.deductCredits(userId, 25L, "video_gen")
              if (!deducted) {
                errorMessage = "Insufficient AI credits. Please upgrade or top-up."
                isGenerating = false
                return@launch
              }

              // Real Gemini AI Storyboard Synthesis
              progressPercent = 0.35f
              progressStage = "Directing Multi-Scene Cinematic Storyboard..."
              val storyboard = aiService.generateVideoStoryboard(
                prompt = promptText,
                aspectRatio = selectedRatio,
                style = selectedStyle
              )

              progressPercent = 0.65f
              progressStage = "Composing Keyframe Motion & Camera Angles..."
              delay(1200)

              progressPercent = 0.90f
              progressStage = "Synthesizing High-Bitrate Video Output..."
              delay(1000)

              progressPercent = 1.0f
              progressStage = "Render Complete!"
              generatedResult = storyboard

              // Save to Firestore
              repository.saveVideo(
                userId = userId,
                title = promptText.take(40) + if (promptText.length > 40) "..." else "",
                prompt = promptText,
                aspectRatio = selectedRatio,
                resolution = selectedResolution,
                style = selectedStyle,
                script = storyboard
              )

              repository.createProject(
                userId = userId,
                title = "Video: " + promptText.take(30),
                type = "video",
                description = "Aspect: $selectedRatio | $selectedResolution"
              )

              Toast.makeText(context, "Video synthesized & saved to projects!", Toast.LENGTH_SHORT).show()
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

    // Generation Progress Tracker
    if (isGenerating) {
      item {
        CyberCard(modifier = Modifier.fillMaxWidth()) {
          Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text(text = progressStage, color = NeonCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
              Text(text = "${(progressPercent * 100).toInt()}%", color = NeonCyan, fontWeight = FontWeight.ExtraBold)
            }
            LinearProgressIndicator(
              progress = { progressPercent },
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = NeonCyan,
              trackColor = CyberSurfaceVariant
            )
          }
        }
      }
    }

    // Video Result & Interactive Player Preview
    if (generatedResult != null) {
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
              CyberBadge(text = "VIDEO READY • $selectedResolution", color = NeonGreen)
              Row {
                IconButton(onClick = { isPlayingPreview = !isPlayingPreview }) {
                  Icon(
                    imageVector = if (isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = NeonCyan
                  )
                }
                IconButton(
                  onClick = {
                    Toast.makeText(context, "Downloading video to device storage...", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = NeonGreen)
                }
              }
            }

            // Interactive Simulated Canvas Video Player Preview
            val aspectMod = when (selectedRatio) {
              "16:9" -> Modifier.aspectRatio(16f / 9f)
              "1:1" -> Modifier.aspectRatio(1f)
              else -> Modifier.aspectRatio(9f / 16f).height(320.dp)
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .then(aspectMod)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
              contentAlignment = Alignment.Center
            ) {
              Canvas(modifier = Modifier.fillMaxSize()) {
                val t = currentPlayFrame / 100f
                // Dynamic Cyber Wave Simulation
                val w = size.width
                val h = size.height
                drawCircle(
                  brush = Brush.radialGradient(
                    colors = listOf(NeonCyan.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(w * (0.3f + 0.4f * t), h * 0.5f),
                    radius = w * 0.6f
                  )
                )
                drawCircle(
                  brush = Brush.radialGradient(
                    colors = listOf(NeonPurple.copy(alpha = 0.4f), Color.Transparent),
                    center = Offset(w * (0.7f - 0.4f * t), h * 0.5f),
                    radius = w * 0.5f
                  )
                )
              }

              Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Movie,
                  contentDescription = null,
                  tint = NeonCyan,
                  modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "AI Motion Synthesis Active",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp
                )
                Text(
                  text = "$selectedRatio • $selectedStyle • 60 FPS",
                  color = NeonCyan,
                  fontSize = 11.sp
                )
              }
            }

            // Storyboard script breakdown
            Text(
              text = "DIRECTOR STORYBOARD BREAKDOWN:",
              color = TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Surface(
              color = CyberSurfaceElevated,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = generatedResult.orEmpty(),
                color = TextPrimary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
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
