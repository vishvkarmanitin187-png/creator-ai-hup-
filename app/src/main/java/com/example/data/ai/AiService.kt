package com.example.data.ai

import android.graphics.Bitmap
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.ImagePart
import com.google.firebase.ai.type.ResponseModality
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "AiService"

class AiService {

  // Lazy evaluation prevents premature crash before App Check configuration
  private val textModel by lazy {
    Firebase.ai.generativeModel(
      modelName = "gemini-3.8-flash"
    )
  }

  private val imageModel by lazy {
    val config = generationConfig {
      responseModalities = listOf(ResponseModality.IMAGE)
    }
    Firebase.ai.generativeModel(
      modelName = "gemini-nano-banana-2.1",
      generationConfig = config
    )
  }

  // --- 1. SCRIPT GENERATION ---
  suspend fun generateScript(
    topic: String,
    category: String,
    language: String, // "English", "Hindi", "Hinglish"
    duration: String, // "30 Seconds", "60 Seconds", "3 Minutes", "5 Minutes"
    tone: String // "Engaging & Viral", "Dramatic", "Humorous", "Educational"
  ): String {
    val prompt = """
      You are the world's top content creator scriptwriter for YouTube, Reels, and TikTok.
      Write an ultra-engaging, high-retention video script.
      
      Topic: $topic
      Category: $category
      Target Duration: $duration
      Language: $language (If Hinglish, write natural Hindi in Latin/English script with catchy English punchlines. If Hindi, use conversational Hindi. If English, high-energy modern English).
      Tone: $tone
      
      Format the output with these clear sections:
      [VIRAL HOOK (0-5s)]
      - Hook lines and visual action directive
      
      [CONTENT BEATS & STORY]
      - Scene 1 [Visual directive] -> Spoken narration
      - Scene 2 [Visual directive] -> Spoken narration
      - Scene 3 [Visual directive] -> Spoken narration
      
      [PUNCHLINE / CLIMAX]
      
      [CALL TO ACTION (CTA)]
      - Exact engaging CTA to like, subscribe, or comment
      
      [SEO KEYWORDS & HASHTAGS]
      - 5 trending tags
    """.trimIndent()

    return withTimeout(60_000L) {
      val response = textModel.generateContent(prompt)
      response.text ?: throw IllegalStateException("Model returned empty script.")
    }
  }

  // --- 2. STREAMING CHAT (Creator AI Assistant) ---
  fun chatStream(
    conversation: List<Pair<String, String>>, // role to message
    userPrompt: String
  ): Flow<String> = flow {
    val historyContent = conversation.map { (role, msg) ->
      content(if (role == "user") "user" else "model") { text(msg) }
    }

    val chat = textModel.startChat(history = historyContent)
    val promptWithPersona = """
      You are 'Creator AI Assistant', an elite YouTube and social media strategist, SEO architect, and production director for Creator AI Hub.
      Give direct, actionable, tactical advice for creators to maximize retention, CTR, algorithm growth, and production value.
      User question: $userPrompt
    """.trimIndent()

    val responseStream = chat.sendMessageStream(promptWithPersona)
    val accumulated = StringBuilder()

    withTimeout(60_000L) {
      responseStream.collect { chunk ->
        val text = chunk.text.orEmpty()
        accumulated.append(text)
        emit(accumulated.toString())
      }
    }
  }

  // --- 3. YOUTUBE GROWTH OPTIMIZATION ---
  suspend fun generateYouTubeOptimization(
    topic: String,
    targetAudience: String = "Global Creators"
  ): String {
    val prompt = """
      You are a YouTube Algorithm and SEO Specialist. Generate a complete growth package for this video topic:
      Topic: $topic
      Target Audience: $targetAudience
      
      Provide the following:
      1. 5 HIGH-CTR TITLES (with estimated click-through-rate appeal and psychological trigger used: Curiosity, Fear of Missing Out, Direct Benefit, Shock)
      2. 3 THUMBNAIL CONCEPTS (Describe exact visual composition, colors, text overlay with max 3 words, facial expressions)
      3. SEARCH-OPTIMIZED DESCRIPTION (With intro hook, 3 chapter timestamps, keyword cluster)
      4. 15 HIGH-SEARCH-VOLUME TAGS (comma separated)
      5. 5 TOP RELEVANT HASHTAGS
    """.trimIndent()

    return withTimeout(60_000L) {
      val response = textModel.generateContent(prompt)
      response.text ?: throw IllegalStateException("Failed to generate YouTube SEO data.")
    }
  }

  // --- 4. SOCIAL MEDIA SUITE ---
  suspend fun generateSocialSuite(
    contentInput: String,
    platform: String // "Instagram", "YouTube Shorts", "Facebook", "TikTok/X"
  ): String {
    val prompt = """
      Create an optimized social media distribution post for platform: $platform
      Original Content / Idea:
      $contentInput
      
      Generate:
      1. CAPTION with strong first-line hook and appropriate line spacing
      2. ENGAGEMENT QUESTION at the end to boost comments
      3. 20 CAREFULLY CATEGORIZED HASHTAGS (High volume, Niche specific, Trending)
      4. CONTENT REPURPOSING TIP: How to turn this into 2 other media formats
    """.trimIndent()

    return withTimeout(60_000L) {
      val response = textModel.generateContent(prompt)
      response.text ?: throw IllegalStateException("Failed to generate social media suite.")
    }
  }

  // --- 5. AI VIDEO STORYBOARD & SCENE BREAKDOWN ---
  suspend fun generateVideoStoryboard(
    prompt: String,
    aspectRatio: String,
    style: String
  ): String {
    val aiPrompt = """
      You are an AI Video Director. Create a multi-scene cinematic production storyboard for AI video synthesis:
      Prompt: $prompt
      Aspect Ratio: $aspectRatio
      Aesthetic Style: $style
      
      Breakdown into 4 precise 5-second scenes (Total 20s):
      - Scene 1: Camera angle, lighting, visual dynamics, audio sound effect
      - Scene 2: Subject progression, motion trajectory, lighting shift
      - Scene 3: Climax visual, high-impact movement, focal transition
      - Scene 4: Concluding visual, branding outro element, fade-out note
      
      Include audio design cues and voiceover cues for each scene.
    """.trimIndent()

    return withTimeout(60_000L) {
      val response = textModel.generateContent(aiPrompt)
      response.text ?: throw IllegalStateException("Failed to generate video storyboard.")
    }
  }

  // --- 6. REAL IMAGE GENERATION VIA GEMINI ---
  suspend fun generateImage(
    promptText: String,
    style: String
  ): Bitmap? {
    val fullPrompt = "$promptText, style: $style, ultra-detailed 8k, futuristic neon lighting, award-winning cinematic digital art composition"
    return withTimeout(60_000L) {
      try {
        val response = imageModel.generateContent(fullPrompt)
        val imagePart = response.candidates.firstOrNull()
          ?.content?.parts?.filterIsInstance<ImagePart>()
          ?.firstOrNull()
        imagePart?.image
      } catch (e: Exception) {
        Log.e(TAG, "Image generation error: ${e.message}", e)
        throw e
      }
    }
  }
}
