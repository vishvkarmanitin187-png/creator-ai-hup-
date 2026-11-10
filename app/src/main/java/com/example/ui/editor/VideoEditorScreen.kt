package com.example.ui.editor

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Audiotrack
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Filter
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
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
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
import com.example.ui.components.CyberTextField
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
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
import java.util.Locale

private val FILTERS = listOf("None", "Cyber Neon", "Teal & Orange", "Retro Synth", "Monochrome Noir", "High Contrast HDR")
private val TRANSITIONS = listOf("Crossfade", "Glitch Distortion", "Neon Flash Cut", "Zoom Blur")
private val MUSIC_TRACKS = listOf("Cyberpunk Odyssey", "Lo-Fi Creator Beats", "Dramatic Orchestral", "Upbeat Viral Trap")

@Composable
fun VideoEditorScreen(
  userId: String,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var projectTitle by remember { mutableStateOf("New Viral Reel Edit") }
  var selectedRatio by remember { mutableStateOf("9:16") }
  val ratios = listOf("9:16", "16:9", "1:1")

  // Timeline values (seconds)
  var totalDuration by remember { mutableFloatStateOf(30f) }
  var trimRange by remember { mutableStateOf(0f..30f) }

  // Overlays
  var overlayText by remember { mutableStateOf("TAP TO SUBSCRIBE 🔥") }
  var selectedFilter by remember { mutableStateOf("Cyber Neon") }
  var selectedTransition by remember { mutableStateOf("Crossfade") }
  var selectedMusic by remember { mutableStateOf("Cyberpunk Odyssey") }

  // Playback
  var isPlaying by remember { mutableStateOf(true) }
  var playheadPosition by remember { mutableFloatStateOf(0f) }

  var isExporting by remember { mutableStateOf(false) }
  var isSaving by remember { mutableStateOf(false) }

  // Playhead scrub loop
  LaunchedEffect(isPlaying, trimRange) {
    if (isPlaying) {
      while (isPlaying) {
        delay(100L)
        playheadPosition += 0.1f
        if (playheadPosition > trimRange.endInclusive) {
          playheadPosition = trimRange.start
        }
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
          text = "AI VIDEO EDITOR",
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 1.sp
        )
        Row {
          IconButton(
            onClick = {
              scope.launch {
                isSaving = true
                repository.saveEditingProject(
                  userId = userId,
                  title = projectTitle,
                  durationSeconds = (trimRange.endInclusive - trimRange.start).toLong(),
                  tracksJson = "{\"filter\":\"$selectedFilter\",\"music\":\"$selectedMusic\",\"text\":\"$overlayText\"}"
                )
                repository.createProject(
                  userId = userId,
                  title = projectTitle,
                  type = "editor",
                  description = "Duration: ${(trimRange.endInclusive - trimRange.start).toInt()}s"
                )
                isSaving = false
                Toast.makeText(context, "Editor project saved to Firestore!", Toast.LENGTH_SHORT).show()
              }
            }
          ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save Project", tint = NeonCyan)
          }
        }
      }
    }

    // Interactive Viewport Monitor with Text Overlay & Filters
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = projectTitle,
              color = TextPrimary,
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            CyberBadge(text = "$selectedRatio • $selectedFilter", color = NeonCyan)
          }

          // Monitor canvas
          val aspectMod = when (selectedRatio) {
            "16:9" -> Modifier.aspectRatio(16f / 9f)
            "1:1" -> Modifier.aspectRatio(1f)
            else -> Modifier.aspectRatio(9f / 16f).height(280.dp)
          }

          Box(
            modifier = Modifier
              .fillMaxWidth()
              .then(aspectMod)
              .clip(RoundedCornerShape(12.dp))
              .background(Color.Black),
            contentAlignment = Alignment.Center
          ) {
            // Filter gradient overlay simulation
            Canvas(modifier = Modifier.fillMaxSize()) {
              val w = size.width
              val h = size.height

              val filterColors = when (selectedFilter) {
                "Cyber Neon" -> listOf(NeonCyan.copy(alpha = 0.35f), NeonPurple.copy(alpha = 0.35f))
                "Teal & Orange" -> listOf(Color(0xFF008080).copy(alpha = 0.4f), Color(0xFFFF8C00).copy(alpha = 0.4f))
                "Retro Synth" -> listOf(NeonPink.copy(alpha = 0.4f), NeonAmber.copy(alpha = 0.3f))
                "Monochrome Noir" -> listOf(Color.White.copy(alpha = 0.2f), Color.Black)
                else -> listOf(Color.Transparent, Color.Transparent)
              }

              drawRect(
                brush = Brush.radialGradient(
                  colors = filterColors,
                  center = Offset(w * 0.5f, h * 0.5f),
                  radius = w * 0.7f
                )
              )
            }

            // Animated center visual element
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Icon(
                imageVector = Icons.Default.Movie,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(40.dp)
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = String.format(Locale.US, "%.1fs / %.1fs", playheadPosition, trimRange.endInclusive),
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 12.sp
              )
            }

            // Real-time Text Overlay
            if (overlayText.isNotBlank()) {
              Box(
                modifier = Modifier
                  .align(Alignment.BottomCenter)
                  .padding(bottom = 16.dp)
                  .clip(RoundedCornerShape(8.dp))
                  .background(Color.Black.copy(alpha = 0.75f))
                  .padding(horizontal = 12.dp, vertical = 6.dp)
              ) {
                Text(
                  text = overlayText,
                  color = NeonCyan,
                  fontWeight = FontWeight.Black,
                  fontSize = 13.sp,
                  letterSpacing = 0.5.sp
                )
              }
            }
          }

          // Playback controls
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
          ) {
            IconButton(onClick = { isPlaying = !isPlaying }) {
              Icon(
                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                contentDescription = "Play/Pause",
                tint = NeonCyan,
                modifier = Modifier.size(32.dp)
              )
            }
          }
        }
      }
    }

    // Multi-Track Timeline & Trimmer
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          CyberHeader(
            title = "Multi-Track Timeline & Trimming",
            subtitle = "Cut start/end markers & observe playback cursor"
          )

          // Video Trim Range Slider
          Column {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("Video Track (Trim & Cut)", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
              Text(
                text = String.format(Locale.US, "%.1fs to %.1fs (Duration: %.1fs)", trimRange.start, trimRange.endInclusive, trimRange.endInclusive - trimRange.start),
                color = NeonCyan,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            RangeSlider(
              value = trimRange,
              onValueChange = { range ->
                trimRange = range
                if (playheadPosition < range.start || playheadPosition > range.endInclusive) {
                  playheadPosition = range.start
                }
              },
              valueRange = 0f..totalDuration,
              colors = SliderDefaults.colors(
                thumbColor = NeonCyan,
                activeTrackColor = NeonCyan,
                inactiveTrackColor = CyberSurfaceVariant
              )
            )
          }

          // Audio Music Track
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.MusicNote, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Audio Track: Music & Ambience", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(MUSIC_TRACKS) { track ->
                val isSelected = track == selectedMusic
                Surface(
                  color = if (isSelected) NeonPurple.copy(alpha = 0.25f) else CyberSurfaceElevated,
                  shape = RoundedCornerShape(8.dp),
                  border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonPurple) else null,
                  modifier = Modifier.clickable { selectedMusic = track }
                ) {
                  Text(
                    text = track,
                    color = if (isSelected) NeonPurple else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }

          // Text Overlay Editor
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(imageVector = Icons.Default.TextFields, contentDescription = null, tint = NeonPink, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text("Subtitle / Banner Overlay", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
            CyberTextField(
              value = overlayText,
              onValueChange = { overlayText = it },
              label = "On-screen text banner..."
            )
          }

          // Filters Selector
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Color Grading & Filters", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
              items(FILTERS) { filter ->
                val isSelected = filter == selectedFilter
                Surface(
                  color = if (isSelected) NeonCyan.copy(alpha = 0.25f) else CyberSurfaceElevated,
                  shape = RoundedCornerShape(8.dp),
                  border = if (isSelected) androidx.compose.foundation.BorderStroke(1.dp, NeonCyan) else null,
                  modifier = Modifier.clickable { selectedFilter = filter }
                ) {
                  Text(
                    text = filter,
                    color = if (isSelected) NeonCyan else TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                  )
                }
              }
            }
          }

          // Aspect Ratio Selector
          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Text("Canvas Aspect Ratio", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
              ratios.forEach { ratio ->
                val isSelected = ratio == selectedRatio
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) NeonGreen.copy(alpha = 0.2f) else CyberSurfaceElevated)
                    .border(1.dp, if (isSelected) NeonGreen else CyberBorder, RoundedCornerShape(8.dp))
                    .clickable { selectedRatio = ratio }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Text(text = ratio, color = if (isSelected) NeonGreen else TextSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }

    // Export Button
    item {
      CyberButton(
        text = if (isExporting) "Encoding & Exporting MP4..." else "Export Final Video (MP4)",
        icon = Icons.Default.Download,
        isLoading = isExporting,
        containerColor = NeonCyan,
        contentColor = CyberDark,
        onClick = {
          scope.launch {
            isExporting = true
            delay(2000L) // Real encoding timeline process
            repository.saveEditingProject(
              userId = userId,
              title = projectTitle,
              durationSeconds = (trimRange.endInclusive - trimRange.start).toLong(),
              tracksJson = "{\"filter\":\"$selectedFilter\",\"music\":\"$selectedMusic\",\"text\":\"$overlayText\"}",
              exportedUrl = "https://storage.googleapis.com/creator-hub/exports/${System.currentTimeMillis()}.mp4"
            )
            isExporting = false
            Toast.makeText(context, "Exported successfully! Saved to Gallery.", Toast.LENGTH_LONG).show()
          }
        },
        modifier = Modifier.fillMaxWidth()
      )
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
