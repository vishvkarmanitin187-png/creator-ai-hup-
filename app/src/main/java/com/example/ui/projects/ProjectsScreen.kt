package com.example.ui.projects

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Project
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CyberBadge
import com.example.ui.components.CyberCard
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

private val FILTER_TYPES = listOf("All", "video", "image", "script", "audio", "editor")

@Composable
fun ProjectsScreen(
  userId: String,
  projects: List<Project>,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val context = LocalContext.current
  val scope = rememberCoroutineScope()

  var selectedType by remember { mutableStateOf("All") }
  var searchQuery by remember { mutableStateOf("") }
  var projectToDelete by remember { mutableStateOf<Project?>(null) }

  val filteredProjects = projects.filter { proj ->
    val matchesType = if (selectedType == "All") true else proj.type.equals(selectedType, ignoreCase = true)
    val matchesSearch = if (searchQuery.isBlank()) true else proj.title.contains(searchQuery, ignoreCase = true) || (proj.description?.contains(searchQuery, ignoreCase = true) == true)
    matchesType && matchesSearch
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberDark)
      .padding(horizontal = 20.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
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
          text = "CREATOR ASSET LIBRARY",
          color = TextPrimary,
          fontSize = 17.sp,
          fontWeight = FontWeight.Black,
          letterSpacing = 0.5.sp
        )
        CyberBadge(text = "${projects.size} ITEMS", color = NeonCyan)
      }
    }

    // Search and Filter Row
    item {
      CyberTextField(
        value = searchQuery,
        onValueChange = { searchQuery = it },
        label = "Search projects & generated assets...",
        leadingIcon = {
          Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary)
        }
      )
    }

    item {
      LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        items(FILTER_TYPES) { type ->
          val isSelected = type == selectedType
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(12.dp))
              .background(if (isSelected) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceElevated)
              .border(1.dp, if (isSelected) NeonCyan else CyberBorder, RoundedCornerShape(12.dp))
              .clickable { selectedType = type }
              .padding(horizontal = 14.dp, vertical = 8.dp)
          ) {
            Text(
              text = type.replaceFirstChar { it.uppercase() },
              color = if (isSelected) NeonCyan else TextSecondary,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }

    // Projects List
    if (filteredProjects.isEmpty()) {
      item {
        CyberCard(modifier = Modifier.fillMaxWidth()) {
          Column(
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 36.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Icon(imageVector = Icons.Default.Folder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(44.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text("No projects found", color = TextSecondary, fontWeight = FontWeight.Bold)
            Text("Create assets in Video, Image, Voice or Script studios", color = TextMuted, fontSize = 12.sp)
          }
        }
      }
    } else {
      items(filteredProjects) { proj ->
        CyberCard(modifier = Modifier.fillMaxWidth()) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(
              verticalAlignment = Alignment.CenterVertically,
              modifier = Modifier.weight(1f)
            ) {
              Box(
                modifier = Modifier
                  .size(42.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(
                    when (proj.type) {
                      "video" -> NeonCyan.copy(alpha = 0.2f)
                      "image" -> NeonPurple.copy(alpha = 0.2f)
                      "audio" -> NeonGreen.copy(alpha = 0.2f)
                      "editor" -> NeonPink.copy(alpha = 0.2f)
                      else -> Color(0xFF3B82F6).copy(alpha = 0.2f)
                    }
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = when (proj.type) {
                    "video" -> Icons.Default.Movie
                    "image" -> Icons.Default.Palette
                    "audio" -> Icons.Default.Mic
                    "editor" -> Icons.Default.ContentCut
                    else -> Icons.Default.AutoAwesome
                  },
                  contentDescription = null,
                  tint = when (proj.type) {
                    "video" -> NeonCyan
                    "image" -> NeonPurple
                    "audio" -> NeonGreen
                    "editor" -> NeonPink
                    else -> Color(0xFF3B82F6)
                  },
                  modifier = Modifier.size(22.dp)
                )
              }
              Spacer(modifier = Modifier.width(14.dp))
              Column {
                Text(
                  text = proj.title,
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 14.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
                Text(
                  text = proj.description ?: proj.type.uppercase(),
                  color = TextSecondary,
                  fontSize = 12.sp,
                  maxLines = 1,
                  overflow = TextOverflow.Ellipsis
                )
              }
            }

            IconButton(onClick = { projectToDelete = proj }) {
              Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted)
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(30.dp))
    }
  }

  // Delete Confirmation Dialog
  projectToDelete?.let { proj ->
    AlertDialog(
      onDismissRequest = { projectToDelete = null },
      containerColor = CyberSurfaceVariant,
      title = { Text("Delete Project?", color = TextPrimary) },
      text = { Text("Are you sure you want to delete '${proj.title}'? This cannot be undone.", color = TextSecondary) },
      confirmButton = {
        TextButton(
          onClick = {
            scope.launch {
              repository.deleteProject(proj.id)
              projectToDelete = null
              Toast.makeText(context, "Project deleted.", Toast.LENGTH_SHORT).show()
            }
          }
        ) {
          Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold)
        }
      },
      dismissButton = {
        TextButton(onClick = { projectToDelete = null }) {
          Text("Cancel", color = TextSecondary)
        }
      }
    )
  }
}
