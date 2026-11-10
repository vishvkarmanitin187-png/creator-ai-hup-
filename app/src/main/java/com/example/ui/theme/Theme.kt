package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
  primary = NeonCyan,
  onPrimary = Color(0xFF002024),
  primaryContainer = Color(0xFF004F58),
  onPrimaryContainer = Color(0xFF82F4FF),

  secondary = NeonPurple,
  onSecondary = Color(0xFF290044),
  secondaryContainer = Color(0xFF5A0090),
  onSecondaryContainer = Color(0xFFF1D4FF),

  tertiary = NeonPink,
  onTertiary = Color(0xFF45001E),
  tertiaryContainer = Color(0xFF7E003E),
  onTertiaryContainer = Color(0xFFFFD9E4),

  background = CyberDark,
  onBackground = TextPrimary,

  surface = CyberSurface,
  onSurface = TextPrimary,
  surfaceVariant = CyberSurfaceVariant,
  onSurfaceVariant = TextSecondary,

  outline = CyberBorder,
  outlineVariant = Color(0xFF233256),

  error = Color(0xFFFF5252),
  onError = Color(0xFF410002)
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = CyberColorScheme,
    typography = Typography,
    content = content
  )
}
