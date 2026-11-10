package com.example.ui.admin

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.ui.theme.TextMuted
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberHeader
import com.example.ui.components.CyberTextField
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

@Composable
fun AdminPanelScreen(
  userId: String,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var videoGenActive by remember { mutableStateOf(true) }
  var imageGenActive by remember { mutableStateOf(true) }
  var ttsGenActive by remember { mutableStateOf(true) }
  var chatAssistantActive by remember { mutableStateOf(true) }

  var grantTargetUserId by remember { mutableStateOf(userId) }
  var grantCreditAmount by remember { mutableStateOf("500") }

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
          text = "ADMINISTRATOR CONSOLE",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        CyberBadge(text = "ROOT ACCESS", color = NeonPink)
      }
    }

    // Platform KPI Metrics
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
          CyberHeader(
            title = "Platform Health & Metrics",
            subtitle = "Live backend stats and AI throughput telemetry"
          )

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            MetricBox(
              icon = Icons.Default.People,
              label = "Total Creators",
              value = "12,480",
              color = NeonCyan,
              modifier = Modifier.weight(1f)
            )
            MetricBox(
              icon = Icons.Default.Bolt,
              label = "AI Calls Today",
              value = "94,120",
              color = NeonAmber,
              modifier = Modifier.weight(1f)
            )
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
          ) {
            MetricBox(
              icon = Icons.Default.Movie,
              label = "Videos Rendered",
              value = "3,892",
              color = NeonPurple,
              modifier = Modifier.weight(1f)
            )
            MetricBox(
              icon = Icons.Default.Palette,
              label = "Images Created",
              value = "28,450",
              color = NeonGreen,
              modifier = Modifier.weight(1f)
            )
          }
        }
      }
    }

    // API & Infrastructure Status
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = NeonCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("API & Service Integrations", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          }

          ServiceStatusRow(name = "Firebase Firestore Enterprise", status = "Operational (Lat: 42ms)", isOk = true)
          ServiceStatusRow(name = "Firebase App Check Attestation", status = "Active & Verified", isOk = true)
          ServiceStatusRow(name = "Gemini 3.8 Flash (Text & Logic)", status = "Healthy (Zero Quota Drift)", isOk = true)
          ServiceStatusRow(name = "Gemini Nano Banana (Image Studio)", status = "Ready (1K/2K Support)", isOk = true)
          ServiceStatusRow(name = "Android Multilingual TTS Engine", status = "Available (Hi/En)", isOk = true)
        }
      }
    }

    // Feature Toggles
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Feature Gate Management", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)

          FeatureToggleRow("AI Video Generation Pipeline", videoGenActive) { videoGenActive = it }
          FeatureToggleRow("AI Image Generation (Gemini)", imageGenActive) { imageGenActive = it }
          FeatureToggleRow("Multilingual Voice Studio (TTS)", ttsGenActive) { ttsGenActive = it }
          FeatureToggleRow("Creator AI Streaming Assistant", chatAssistantActive) { chatAssistantActive = it }
        }
      }
    }

    // Manual Credit Granting
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Text("Admin Credit Grant Tool", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
          CyberTextField(
            value = grantTargetUserId,
            onValueChange = { grantTargetUserId = it },
            label = "Target User ID (UID)..."
          )
          CyberTextField(
            value = grantCreditAmount,
            onValueChange = { grantCreditAmount = it },
            label = "Credits to Add..."
          )
          CyberButton(
            text = "Grant Credits to Creator",
            containerColor = NeonPurple,
            contentColor = androidx.compose.ui.graphics.Color.White,
            onClick = {
              val amt = grantCreditAmount.toLongOrNull() ?: 100L
              scope.launch {
                repository.addCredits(grantTargetUserId, amt, "admin_grant")
                Toast.makeText(context, "Granted $amt credits to $grantTargetUserId!", Toast.LENGTH_SHORT).show()
              }
            },
            modifier = Modifier.fillMaxWidth()
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}

@Composable
private fun MetricBox(
  icon: androidx.compose.ui.graphics.vector.ImageVector,
  label: String,
  value: String,
  color: androidx.compose.ui.graphics.Color,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(12.dp))
      .background(CyberSurfaceElevated)
      .padding(14.dp)
  ) {
    Column {
      Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
      Spacer(modifier = Modifier.height(8.dp))
      Text(text = value, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
      Text(text = label, color = TextSecondary, fontSize = 11.sp)
    }
  }
}

@Composable
private fun ServiceStatusRow(name: String, status: String, isOk: Boolean) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = name, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    Row(verticalAlignment = Alignment.CenterVertically) {
      Box(
        modifier = Modifier
          .size(8.dp)
          .clip(CircleShape)
          .background(if (isOk) NeonGreen else NeonPink)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(text = status, color = if (isOk) NeonGreen else NeonPink, fontSize = 11.sp)
    }
  }
}

@Composable
private fun FeatureToggleRow(name: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 4.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = name, color = TextSecondary, fontSize = 13.sp)
    Switch(
      checked = checked,
      onCheckedChange = onCheckedChange,
      colors = SwitchDefaults.colors(
        checkedThumbColor = NeonCyan,
        checkedTrackColor = NeonCyan.copy(alpha = 0.3f),
        uncheckedThumbColor = TextMuted,
        uncheckedTrackColor = CyberSurfaceElevated
      )
    )
  }
}
