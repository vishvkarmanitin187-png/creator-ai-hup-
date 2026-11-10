package com.example.ui.scripts

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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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

private val SCRIPT_CATEGORIES = listOf(
  "YouTube Shorts (Viral Hook)",
  "Instagram Reel",
  "Full YouTube Video",
  "Movie Facts & Mysteries",
  "Educational / How-To",
  "Storytelling & Drama",
  "Product Ad & Sale",
  "Call-to-Action Generator"
)

private val LANGUAGES = listOf("English", "Hindi", "Hinglish")
private val DURATIONS = listOf("30 Seconds", "60 Seconds", "3 Minutes", "5 Minutes", "10 Minutes")
private val TONES = listOf("Engaging & Viral", "Dramatic & Cinematic", "Humorous & Sarcastic", "Educational & Authoritative", "Hype / Urgency")

@Composable
fun ScriptWriterScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedCategory by remember { mutableStateOf(SCRIPT_CATEGORIES[0]) }
  var topicText by remember { mutableStateOf("") }
  var selectedLanguage by remember { mutableStateOf("English") }
  var selectedDuration by remember { mutableStateOf("60 Seconds") }
  var selectedTone by remember { mutableStateOf("Engaging & Viral") }

  var isGenerating by remember { mutableStateOf(false) }
  var generatedScript by remember { mutableStateOf("") }
  var isEditingScript by remember { mutableStateOf(false) }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var copied by remember { mutableStateOf(false) }

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
          text = "AI SCRIPT WRITER",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        CreditChip(credits = credits)
      }
    }

    // Category Selector
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          CyberHeader(
            title = "Script Format & Platform",
            subtitle = "Target algorithm-optimized script structures",
            badgeText = "COST: 5 CREDITS"
          )
          LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(SCRIPT_CATEGORIES) { cat ->
              val isSelected = cat == selectedCategory
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(12.dp))
                  .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated)
                  .border(1.dp, if (isSelected) NeonCyan else CyberBorder, RoundedCornerShape(12.dp))
                  .clickable { selectedCategory = cat }
                  .padding(horizontal = 14.dp, vertical = 8.dp)
              ) {
                Text(
                  text = cat,
                  color = if (isSelected) NeonCyan else TextSecondary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 12.sp
                )
              }
            }
          }
        }
      }
    }

    // Script Topic & Settings
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          CyberTextField(
            value = topicText,
            onValueChange = { topicText = it },
            label = "Enter video topic or hook idea...",
            placeholder = "e.g. 5 hidden AI tools nobody knows about in 2026",
            singleLine = false,
            maxLines = 3
          )

          // Language selection (English, Hindi, Hinglish)
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Script Language:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              LANGUAGES.forEach { lang ->
                val isSelected = lang == selectedLanguage
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonPurple.copy(alpha = 0.25f) else CyberSurfaceElevated)
                    .border(1.dp, if (isSelected) NeonPurple else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedLanguage = lang }
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                  Text(text = lang, color = if (isSelected) NeonPurple else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
              }
            }
          }

          // Duration selection
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Target Video Length:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(DURATIONS) { dur ->
                val isSelected = dur == selectedDuration
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedDuration = dur }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(text = dur, color = if (isSelected) NeonGreen else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }

          // Tone selection
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Delivery Tone & Energy:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(TONES) { tone ->
                val isSelected = tone == selectedTone
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isSelected) NeonPink.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (isSelected) NeonPink else CyberBorder, RoundedCornerShape(10.dp))
                    .clickable { selectedTone = tone }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(text = tone, color = if (isSelected) NeonPink else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }

    // Generate Button
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
        text = if (isGenerating) "Generating Viral Script with Gemini..." else "Write AI Script",
        icon = Icons.Default.AutoAwesome,
        isLoading = isGenerating,
        enabled = topicText.isNotBlank() && credits >= 5 && !isGenerating,
        containerColor = NeonCyan,
        contentColor = CyberDark,
        onClick = {
          scope.launch {
            errorMessage = null
            isGenerating = true
            try {
              val deducted = repository.deductCredits(userId, 5L, "script_gen")
              if (!deducted) {
                errorMessage = "Insufficient credits for script generation."
                isGenerating = false
                return@launch
              }

              val script = aiService.generateScript(
                topic = topicText,
                category = selectedCategory,
                language = selectedLanguage,
                duration = selectedDuration,
                tone = selectedTone
              )

              generatedScript = script
              repository.createProject(
                userId = userId,
                title = "Script: " + topicText.take(30),
                type = "script",
                description = "$selectedCategory ($selectedLanguage, $selectedDuration)"
              )

              Toast.makeText(context, "Script created and saved to projects!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
              errorMessage = "Script generation notice: ${e.localizedMessage ?: "Failed to generate script."}"
            } finally {
              isGenerating = false
            }
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }

    // Generated Script Output with Copy, Edit, and Save
    if (generatedScript.isNotBlank()) {
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
              CyberBadge(text = "$selectedLanguage • $selectedDuration", color = NeonGreen)
              Row {
                IconButton(
                  onClick = {
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    val clip = ClipData.newPlainText("AI Script", generatedScript)
                    clipboard.setPrimaryClip(clip)
                    copied = true
                    Toast.makeText(context, "Script copied to clipboard!", Toast.LENGTH_SHORT).show()
                  }
                ) {
                  Icon(
                    imageVector = if (copied) Icons.Default.Check else Icons.Default.ContentCopy,
                    contentDescription = "Copy Script",
                    tint = NeonGreen
                  )
                }
                IconButton(onClick = { isEditingScript = !isEditingScript }) {
                  Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Script",
                    tint = NeonCyan
                  )
                }
              }
            }

            if (isEditingScript) {
              OutlinedTextField(
                value = generatedScript,
                onValueChange = { generatedScript = it },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                  focusedBorderColor = NeonCyan,
                  unfocusedBorderColor = CyberBorder,
                  focusedTextColor = TextPrimary,
                  unfocusedTextColor = TextPrimary,
                  focusedContainerColor = CyberSurfaceElevated,
                  unfocusedContainerColor = CyberSurfaceElevated
                ),
                shape = RoundedCornerShape(10.dp)
              )
            } else {
              Surface(
                color = CyberSurfaceElevated,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = generatedScript,
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
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
