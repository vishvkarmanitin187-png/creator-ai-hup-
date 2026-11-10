package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberBorderPurple
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberGlass
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

@Composable
fun CyberCard(
  modifier: Modifier = Modifier,
  borderColor: Color = NeonCyan.copy(alpha = 0.25f),
  backgroundColor: Color = CyberSurface.copy(alpha = 0.85f),
  shape: RoundedCornerShape = RoundedCornerShape(16.dp),
  onClick: (() -> Unit)? = null,
  content: @Composable () -> Unit
) {
  val cardModifier = if (onClick != null) {
    modifier
      .clip(shape)
      .clickable(onClick = onClick)
      .border(BorderStroke(1.dp, borderColor), shape)
      .background(backgroundColor, shape)
  } else {
    modifier
      .clip(shape)
      .border(BorderStroke(1.dp, borderColor), shape)
      .background(backgroundColor, shape)
  }

  Box(modifier = cardModifier.padding(16.dp)) {
    content()
  }
}

@Composable
fun CyberGradientCard(
  modifier: Modifier = Modifier,
  gradient: Brush = Brush.horizontalGradient(
    colors = listOf(
      NeonPurple.copy(alpha = 0.15f),
      NeonCyan.copy(alpha = 0.15f)
    )
  ),
  borderBrush: Brush = Brush.horizontalGradient(
    colors = listOf(NeonPurple, NeonCyan)
  ),
  content: @Composable () -> Unit
) {
  Box(
    modifier = modifier
      .clip(RoundedCornerShape(18.dp))
      .border(BorderStroke(1.2.dp, borderBrush), RoundedCornerShape(18.dp))
      .background(gradient)
      .padding(18.dp)
  ) {
    content()
  }
}

@Composable
fun CyberButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  enabled: Boolean = true,
  isLoading: Boolean = false,
  containerColor: Color = NeonCyan,
  contentColor: Color = CyberDark
) {
  Button(
    onClick = onClick,
    enabled = enabled && !isLoading,
    colors = ButtonDefaults.buttonColors(
      containerColor = containerColor,
      contentColor = contentColor,
      disabledContainerColor = CyberSurfaceVariant,
      disabledContentColor = TextMuted
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = modifier
      .height(50.dp)
      .shadow(8.dp, RoundedCornerShape(12.dp), ambientColor = containerColor, spotColor = containerColor)
  ) {
    if (isLoading) {
      Text(
        text = "Processing...",
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp
      )
    } else {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
      ) {
        if (icon != null) {
          Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
        }
        Text(
          text = text,
          fontWeight = FontWeight.Bold,
          fontSize = 15.sp,
          letterSpacing = 0.5.sp
        )
      }
    }
  }
}

@Composable
fun CyberOutlinedButton(
  text: String,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  icon: ImageVector? = null,
  borderColor: Color = NeonCyan,
  textColor: Color = NeonCyan
) {
  Box(
    modifier = modifier
      .height(48.dp)
      .clip(RoundedCornerShape(12.dp))
      .border(1.dp, borderColor, RoundedCornerShape(12.dp))
      .clickable(onClick = onClick)
      .padding(horizontal = 16.dp),
    contentAlignment = Alignment.Center
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      if (icon != null) {
        Icon(imageVector = icon, contentDescription = null, tint = textColor, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
      }
      Text(text = text, color = textColor, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
    }
  }
}

@Composable
fun CyberBadge(
  text: String,
  color: Color = NeonCyan,
  modifier: Modifier = Modifier
) {
  Surface(
    color = color.copy(alpha = 0.15f),
    shape = RoundedCornerShape(20.dp),
    border = BorderStroke(1.dp, color.copy(alpha = 0.4f)),
    modifier = modifier
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold,
      modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
      letterSpacing = 0.5.sp
    )
  }
}

@Composable
fun CreditChip(
  credits: Long,
  modifier: Modifier = Modifier,
  onClick: (() -> Unit)? = null
) {
  val transition = rememberInfiniteTransition(label = "pulse")
  val alpha by transition.animateFloat(
    initialValue = 0.7f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseAlpha"
  )

  Surface(
    color = NeonAmber.copy(alpha = 0.12f),
    shape = RoundedCornerShape(24.dp),
    border = BorderStroke(1.dp, NeonAmber.copy(alpha = alpha * 0.6f)),
    modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Bolt,
        contentDescription = "Credits",
        tint = NeonAmber,
        modifier = Modifier.size(16.dp)
      )
      Spacer(modifier = Modifier.width(4.dp))
      Text(
        text = "$credits Credits",
        color = NeonAmber,
        fontSize = 12.sp,
        fontWeight = FontWeight.ExtraBold
      )
    }
  }
}

@Composable
fun CyberHeader(
  title: String,
  subtitle: String? = null,
  badgeText: String? = null,
  modifier: Modifier = Modifier
) {
  Column(modifier = modifier) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Text(
        text = title,
        color = TextPrimary,
        fontSize = 22.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 0.5.sp
      )
      if (badgeText != null) {
        Spacer(modifier = Modifier.width(10.dp))
        CyberBadge(text = badgeText, color = NeonCyan)
      }
    }
    if (subtitle != null) {
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = subtitle,
        color = TextSecondary,
        fontSize = 13.sp
      )
    }
  }
}

@Composable
fun CyberTextField(
  value: String,
  onValueChange: (String) -> Unit,
  label: String,
  modifier: Modifier = Modifier,
  placeholder: String? = null,
  singleLine: Boolean = true,
  maxLines: Int = 1,
  leadingIcon: (@Composable () -> Unit)? = null
) {
  OutlinedTextField(
    value = value,
    onValueChange = onValueChange,
    label = { Text(label, color = TextSecondary) },
    placeholder = if (placeholder != null) { { Text(placeholder, color = TextMuted) } } else null,
    singleLine = singleLine,
    maxLines = maxLines,
    leadingIcon = leadingIcon,
    colors = OutlinedTextFieldDefaults.colors(
      focusedBorderColor = NeonCyan,
      unfocusedBorderColor = CyberBorder,
      focusedTextColor = TextPrimary,
      unfocusedTextColor = TextPrimary,
      focusedContainerColor = CyberSurfaceVariant.copy(alpha = 0.5f),
      unfocusedContainerColor = CyberSurfaceVariant.copy(alpha = 0.3f),
      cursorColor = NeonCyan
    ),
    shape = RoundedCornerShape(12.dp),
    modifier = modifier.fillMaxWidth()
  )
}
