package com.example.ui.image

import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
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
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

private val IMAGE_MODES = listOf("Text-to-Image", "AI Thumbnail", "Poster Maker", "Logo Generator")
private val ART_STYLES = listOf("Cyberpunk Neon", "3D Unreal Render", "Cinematic Photoreal", "Anime / Manga", "Minimalist Vector")

@Composable
fun ImageStudioScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedMode by remember { mutableStateOf("Text-to-Image") }
  var promptText by remember { mutableStateOf("") }
  var selectedStyle by remember { mutableStateOf("Cyberpunk Neon") }
  var selectedRatio by remember { mutableStateOf("1:1") }
  val ratios = listOf("1:1", "16:9", "9:16", "4:3")

  var isGenerating by remember { mutableStateOf(false) }
  var generatedBitmap by remember { mutableStateOf<Bitmap?>(null) }
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
          text = "AI IMAGE STUDIO",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        CreditChip(credits = credits)
      }
    }

    // Studio Mode
    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(IMAGE_MODES) { mode ->
          val isSelected = mode == selectedMode
          Surface(
            color = if (isSelected) NeonPurple.copy(alpha = 0.25f) else CyberSurfaceVariant.copy(alpha = 0.4f),
            shape = RoundedCornerShape(20.dp),
            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.2.dp, NeonPurple) else null,
            modifier = Modifier.clickable { selectedMode = mode }
          ) {
            Text(
              text = mode,
              color = if (isSelected) NeonPurple else TextSecondary,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
              fontSize = 13.sp,
              modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
            )
          }
        }
      }
    }

    // Prompt Card
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CyberHeader(
            title = "Image Prompt",
            subtitle = "Describe the subject, lighting, environment, and details",
            badgeText = "COST: 15 CREDITS"
          )
          CyberTextField(
            value = promptText,
            onValueChange = { promptText = it },
            label = when (selectedMode) {
              "AI Thumbnail" -> "Describe high-CTR YouTube thumbnail subject & text..."
              "Logo Generator" -> "Describe brand icon, monogram, or mascot..."
              else -> "Enter image prompt..."
            },
            placeholder = "e.g. Cybernetic cyborg warrior holding glowing katana in rainy neon alleyway, cinematic lighting",
            singleLine = false,
            maxLines = 4
          )

          // Style Selection
          Text("Artistic Style:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(ART_STYLES) { style ->
              val isSelected = style == selectedStyle
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) NeonPurple.copy(alpha = 0.2f) else CyberSurfaceElevated)
                  .border(1.dp, if (isSelected) NeonPurple else CyberBorder, RoundedCornerShape(10.dp))
                  .clickable { selectedStyle = style }
                  .padding(horizontal = 12.dp, vertical = 8.dp)
              ) {
                Text(
                  text = style,
                  color = if (isSelected) NeonPurple else TextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }

          // Aspect Ratio Selection
          Text("Aspect Ratio:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
          Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            ratios.forEach { ratio ->
              val isSelected = ratio == selectedRatio
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(10.dp))
                  .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated)
                  .border(1.dp, if (isSelected) NeonCyan else CyberBorder, RoundedCornerShape(10.dp))
                  .clickable { selectedRatio = ratio }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = ratio,
                  color = if (isSelected) NeonCyan else TextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
              }
            }
          }
        }
      }
    }

    // Action button
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
        text = if (isGenerating) "Synthesizing Image with Gemini (30-60s)..." else "Generate AI Image",
        icon = Icons.Default.Palette,
        isLoading = isGenerating,
        enabled = promptText.isNotBlank() && credits >= 15 && !isGenerating,
        containerColor = NeonPurple,
        contentColor = Color.White,
        onClick = {
          scope.launch {
            errorMessage = null
            isGenerating = true
            try {
              val deducted = repository.deductCredits(userId, 15L, "image_gen", "gemini-nano-banana-2.1")
              if (!deducted) {
                errorMessage = "Insufficient credits. Please top-up."
                isGenerating = false
                return@launch
              }

              val bitmap = aiService.generateImage(promptText, selectedStyle)
              if (bitmap != null) {
                generatedBitmap = bitmap
                repository.saveImage(
                  userId = userId,
                  prompt = promptText,
                  style = selectedStyle,
                  aspectRatio = selectedRatio
                )
                repository.createProject(
                  userId = userId,
                  title = "Image: " + promptText.take(30),
                  type = "image",
                  description = "Style: $selectedStyle"
                )
                Toast.makeText(context, "Image generated & saved to library!", Toast.LENGTH_SHORT).show()
              } else {
                errorMessage = "No image returned by the model. Try refining your prompt."
              }
            } catch (e: Exception) {
              errorMessage = "Generation notice: ${e.localizedMessage ?: "Image synthesis timed out or failed."}"
            } finally {
              isGenerating = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Progress State
    if (isGenerating) {
      item {
        CyberCard(modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            CircularProgressIndicator(
              color = NeonPurple,
              strokeWidth = 3.dp,
              modifier = Modifier.size(44.dp)
            )
            Spacer(modifier = Modifier.height(14.dp))
            Text("Neural Image Synthesis in Progress...", color = TextPrimary, fontWeight = FontWeight.Bold)
            Text("Gemini is rendering pixels, textures, and lighting (30-60s)", color = TextMuted, fontSize = 12.sp)
          }
        }
      }
    }

    // Generated Image Preview & Download
    if (generatedBitmap != null) {
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
              CyberBadge(text = "GENERATED • $selectedRatio", color = NeonGreen)
              IconButton(
                onClick = {
                  Toast.makeText(context, "Saving PNG to device gallery...", Toast.LENGTH_SHORT).show()
                }
              ) {
                Icon(imageVector = Icons.Default.Download, contentDescription = "Download", tint = NeonGreen)
              }
            }

            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.Black),
              contentAlignment = Alignment.Center
            ) {
              Image(
                bitmap = generatedBitmap!!.asImageBitmap(),
                contentDescription = "Generated AI Art",
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Fit
              )
            }

            Text(
              text = "PROMPT: $promptText",
              color = TextSecondary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Medium
            )
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
