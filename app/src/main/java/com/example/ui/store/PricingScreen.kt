package com.example.ui.store

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserProfile
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CreditChip
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberButton
import com.example.ui.components.CyberCard
import com.example.ui.components.CyberGradientCard
import com.example.ui.components.CyberHeader
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonGreen
import com.example.ui.theme.NeonPink
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

data class PlanItem(
  val id: String,
  val name: String,
  val price: String,
  val creditsText: String,
  val badge: String?,
  val color: Color,
  val features: List<String>
)

private val PLANS = listOf(
  PlanItem(
    id = "free",
    name = "Free Tier",
    price = "$0 / month",
    creditsText = "150 Welcome Credits",
    badge = "CURRENT",
    color = NeonCyan,
    features = listOf(
      "Access to all 8 AI Studios",
      "Standard text & script generation",
      "720p Video generation preview",
      "5 GB Cloud project storage"
    )
  ),
  PlanItem(
    id = "creator",
    name = "Creator Pass",
    price = "$19 / month",
    creditsText = "1,000 Credits / month",
    badge = "MOST POPULAR",
    color = NeonPurple,
    features = listOf(
      "1,000 Monthly AI Generation Credits",
      "1080p Full HD Video Exports",
      "Gemini Nano Banana 2.1 Image Studio",
      "Unlimited AI Script & Hook Writer",
      "Priority AI Queue Processing",
      "25 GB Cloud project storage"
    )
  ),
  PlanItem(
    id = "pro",
    name = "Pro Studio Ultra",
    price = "$49 / month",
    creditsText = "3,000 Credits / month",
    badge = "ULTIMATE",
    color = NeonPink,
    features = listOf(
      "3,000 Monthly AI Generation Credits",
      "4K Ultra High-Bitrate Video Export",
      "Custom Brand Voices & Multilingual TTS",
      "Direct Social Media Scheduler Integration",
      "Dedicated High-Speed GPU Pipeline",
      "100 GB Cloud project storage"
    )
  )
)

private val CREDIT_PACKS = listOf(
  Triple("Starter Boost", "200 Credits", "$4.99"),
  Triple("Creator Surge", "600 Credits", "$9.99"),
  Triple("Mega Power Pack", "1,500 Credits", "$19.99")
)

@Composable
fun PricingScreen(
  userId: String,
  userProfile: UserProfile?,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()
  val currentPlan = userProfile?.plan ?: "free"

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
          text = "SUBSCRIPTIONS & CREDITS",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        CreditChip(credits = userProfile?.credits ?: 0L)
      }
    }

    // Header info
    item {
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
          CyberHeader(
            title = "Upgrade Creator Power",
            subtitle = "Switch plans or top-up credit packs anytime. Zero lock-in."
          )
          Text(
            text = "Active Plan: ${currentPlan.uppercase()} • Current Credits: ${userProfile?.credits ?: 0}",
            color = NeonCyan,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    // Subscription Plans
    items(PLANS) { plan ->
      val isCurrent = plan.id == currentPlan

      CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isCurrent) plan.color else CyberBorder,
        backgroundColor = if (isCurrent) plan.color.copy(alpha = 0.08f) else CyberSurfaceElevated
      ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = plan.name, color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 18.sp)
              Text(text = plan.price, color = plan.color, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
            }
            if (plan.badge != null) {
              CyberBadge(text = plan.badge, color = plan.color)
            }
          }

          Text(text = plan.creditsText, color = NeonAmber, fontWeight = FontWeight.Bold, fontSize = 13.sp)

          Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            plan.features.forEach { feat ->
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = plan.color,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = feat, color = TextSecondary, fontSize = 12.sp)
              }
            }
          }

          if (isCurrent) {
            CyberBadge(text = "CURRENT ACTIVE PLAN", color = NeonGreen)
          } else {
            CyberButton(
              text = "Switch to ${plan.name}",
              containerColor = plan.color,
              contentColor = if (plan.id == "pro") Color.White else CyberDark,
              onClick = {
                scope.launch {
                  repository.updatePlan(userId, plan.id)
                  val addedCredits = when (plan.id) {
                    "creator" -> 1000L
                    "pro" -> 3000L
                    else -> 0L
                  }
                  if (addedCredits > 0) {
                    repository.addCredits(userId, addedCredits, "subscription_upgrade")
                  }
                  Toast.makeText(context, "Upgraded to ${plan.name}!", Toast.LENGTH_SHORT).show()
                }
              },
              modifier = Modifier.fillMaxWidth()
            )
          }
        }
      }
    }

    // Top-Up Credit Packs
    item {
      CyberHeader(
        title = "Top-Up Credit Packs",
        subtitle = "Instant credits that never expire"
      )
    }

    items(CREDIT_PACKS) { (packName, creditAmount, price) ->
      CyberCard(modifier = Modifier.fillMaxWidth()) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(NeonAmber.copy(alpha = 0.15f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.Bolt, contentDescription = null, tint = NeonAmber, modifier = Modifier.size(22.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(text = packName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = creditAmount, color = NeonAmber, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
            }
          }

          CyberButton(
            text = "Buy $price",
            containerColor = NeonAmber,
            contentColor = CyberDark,
            onClick = {
              scope.launch {
                val amount = when {
                  creditAmount.contains("200") -> 200L
                  creditAmount.contains("600") -> 600L
                  else -> 1500L
                }
                repository.addCredits(userId, amount, "credit_pack_purchase")
                Toast.makeText(context, "$amount Credits added successfully!", Toast.LENGTH_SHORT).show()
              }
            }
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }
}
