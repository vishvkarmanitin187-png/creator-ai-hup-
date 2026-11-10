package com.example.ui.chat

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.AiService
import com.example.data.repository.CreatorHubRepository
import com.example.ui.components.CreditChip
import com.example.ui.components.CyberBadge
import com.example.ui.theme.CyberBorder
import com.example.ui.theme.CyberDark
import com.example.ui.theme.CyberSurface
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

private val QUICK_PROMPTS = listOf(
  "How to double my audience retention?",
  "Give me 5 viral Shorts hooks for tech",
  "Audit my video thumbnail ideas",
  "Explain YouTube search algorithm in 2026",
  "How to repurpose 1 long video into 10 clips"
)

data class ChatMessage(
  val role: String, // "user" or "model"
  val text: String
)

@Composable
fun CreatorChatScreen(
  userId: String,
  credits: Long,
  aiService: AiService,
  repository: CreatorHubRepository,
  onBack: () -> Unit
) {
  BackHandler { onBack() }
  val scope = rememberCoroutineScope()
  val listState = rememberLazyListState()

  val messages = remember {
    mutableStateListOf(
      ChatMessage(
        "model",
        "Hello creator! I'm your Creator AI Assistant. Ask me anything about viral scripts, YouTube algorithm growth, high-CTR thumbnails, video pacing, or monetization strategies."
      )
    )
  }

  var inputText by remember { mutableStateOf("") }
  var isStreaming by remember { mutableStateOf(false) }

  LaunchedEffect(messages.size) {
    if (messages.isNotEmpty()) {
      listState.animateScrollToItem(messages.size - 1)
    }
  }

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(CyberDark)
  ) {
    // Top Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = onBack) {
          Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
        }
        Spacer(modifier = Modifier.width(6.dp))
        Column {
          Text("CREATOR AI ASSISTANT", color = TextPrimary, fontWeight = FontWeight.Black, fontSize = 16.sp)
          Text("Online • Gemini 3.8 Flash Streaming", color = NeonGreen, fontSize = 11.sp)
        }
      }
      Row(verticalAlignment = Alignment.CenterVertically) {
        CreditChip(credits = credits)
        IconButton(
          onClick = {
            messages.clear()
            messages.add(
              ChatMessage(
                "model",
                "Chat cleared! How can I help you produce your next viral masterpiece today?"
              )
            )
          }
        ) {
          Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear Chat", tint = TextSecondary)
        }
      }
    }

    // Quick Prompts Row
    LazyRow(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      items(QUICK_PROMPTS) { prompt ->
        Surface(
          color = CyberSurfaceElevated,
          shape = RoundedCornerShape(16.dp),
          modifier = Modifier.clickable { inputText = prompt }
        ) {
          Text(
            text = prompt,
            color = NeonCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
          )
        }
      }
    }

    // Messages List
    LazyColumn(
      state = listState,
      modifier = Modifier
        .weight(1f)
        .fillMaxWidth()
        .padding(horizontal = 20.dp),
      verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
      items(messages) { msg ->
        val isUser = msg.role == "user"
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
        ) {
          if (!isUser) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonPurple.copy(alpha = 0.2f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = NeonPurple, modifier = Modifier.size(16.dp))
            }
            Spacer(modifier = Modifier.width(8.dp))
          }

          Box(
            modifier = Modifier
              .widthIn(max = 290.dp)
              .clip(
                RoundedCornerShape(
                  topStart = 16.dp,
                  topEnd = 16.dp,
                  bottomStart = if (isUser) 16.dp else 4.dp,
                  bottomEnd = if (isUser) 4.dp else 16.dp
                )
              )
              .background(if (isUser) NeonCyan.copy(alpha = 0.2f) else CyberSurfaceVariant)
              .padding(14.dp)
          ) {
            Text(
              text = msg.text,
              color = TextPrimary,
              fontSize = 13.sp,
              lineHeight = 19.sp
            )
          }
        }
      }

      if (isStreaming) {
        item {
          Row(verticalAlignment = Alignment.CenterVertically) {
            CircularProgressIndicator(
              modifier = Modifier.size(18.dp),
              color = NeonCyan,
              strokeWidth = 2.dp
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text("Creator AI is thinking...", color = NeonCyan, fontSize = 12.sp)
          }
        }
      }
    }

    // Input Bar
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(CyberSurface)
        .padding(horizontal = 16.dp, vertical = 12.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      OutlinedTextField(
        value = inputText,
        onValueChange = { inputText = it },
        placeholder = { Text("Ask Creator AI...", color = TextMuted) },
        colors = OutlinedTextFieldDefaults.colors(
          focusedBorderColor = NeonCyan,
          unfocusedBorderColor = CyberBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary,
          focusedContainerColor = CyberSurfaceElevated,
          unfocusedContainerColor = CyberSurfaceElevated
        ),
        shape = RoundedCornerShape(24.dp),
        modifier = Modifier.weight(1f),
        maxLines = 3
      )

      Spacer(modifier = Modifier.width(10.dp))

      IconButton(
        onClick = {
          val query = inputText.trim()
          if (query.isNotBlank() && !isStreaming) {
            inputText = ""
            messages.add(ChatMessage("user", query))
            isStreaming = true

            scope.launch {
              try {
                // Deduct 2 credits for chat
                repository.deductCredits(userId, 2L, "chat")

                val historyPairs = messages.map { it.role to it.text }
                var accumulated = ""
                // Add a placeholder message for streaming
                val modelMsgIndex = messages.size
                messages.add(ChatMessage("model", ""))

                aiService.chatStream(historyPairs.dropLast(1), query).collect { chunk ->
                  accumulated = chunk
                  messages[modelMsgIndex] = ChatMessage("model", accumulated)
                }
              } catch (e: Exception) {
                messages.add(ChatMessage("model", "Connection notice: ${e.message}"))
              } finally {
                isStreaming = false
              }
            }
          }
        },
        enabled = inputText.isNotBlank() && !isStreaming
      ) {
        Box(
          modifier = Modifier
            .size(44.dp)
            .clip(CircleShape)
            .background(if (inputText.isNotBlank() && !isStreaming) NeonCyan else CyberSurfaceVariant),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.AutoMirrored.Filled.Send,
            contentDescription = "Send",
            tint = if (inputText.isNotBlank() && !isStreaming) CyberDark else TextMuted
          )
        }
      }
    }
  }
}
