package com.autolearn.ar.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.autolearn.ar.ai.ChatbotService
import com.autolearn.ar.data.TaskTheory
import kotlinx.coroutines.launch


// ==========================================
// COLOR PALETTE
// ==========================================
private val ChatBg = Color(0xFF0B0F19)
private val ChatCard = Color(0xFF161E2E)
private val ChatBorder = Color(0xFF26334D)
private val ChatCyan = Color(0xFF38BDF8)
private val ChatGreen = Color(0xFF34D399)
private val ChatTextPrimary = Color(0xFFF8FAFC)
private val ChatTextSecondary = Color(0xFF94A3B8)
private val ChatUserBubble = Color(0xFF1E3A5F)
private val ChatAiBubble = Color(0xFF1A2332)
private val ChatInputBg = Color(0xFF131B2B)


data class ChatMessage(
    val role: String,   // "user" or "model"
    val text: String,
    val isLoading: Boolean = false
)


@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChatbotScreen(
    theory: TaskTheory,
    chatbotService: ChatbotService,
    onBack: () -> Unit
) {
    val messages = remember { mutableStateListOf<ChatMessage>() }
    var inputText by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    val keyboardController = LocalSoftwareKeyboardController.current

    // Auto-scroll to bottom when new message arrives
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    // Add welcome message on first load
    LaunchedEffect(Unit) {
        if (messages.isEmpty()) {
            messages.add(
                ChatMessage(
                    role = "model",
                    text = "Hello! I'm your AutoLearn AR assistant. I'm here to help you understand " +
                            "**${theory.title}**.\n\nYou can ask me anything about this maintenance task — " +
                            "procedure details, safety questions, or why certain steps matter. " +
                            "Try one of the suggested questions below, or type your own!"
                )
            )
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank() || isLoading) return

        val userMsg = text.trim()
        inputText = ""
        messages.add(ChatMessage(role = "user", text = userMsg))
        isLoading = true

        // Build conversation history for context
        val history = messages
            .filter { !it.isLoading }
            .map { it.role to it.text }

        coroutineScope.launch {
            val reply = chatbotService.sendMessage(
                theory = theory,
                conversationHistory = history.dropLast(1), // exclude the just-added user message
                userMessage = userMsg
            )

            messages.add(ChatMessage(role = "model", text = reply))
            isLoading = false
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ChatBg)
            .imePadding()
    ) {

        // ==========================================
        // TOP BAR
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ChatCard)
                .padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = ChatTextPrimary
                )
            }

            Spacer(modifier = Modifier.width(4.dp))

            // AI indicator dot
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(ChatGreen)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "AI Chatbot",
                    color = ChatTextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = theory.title,
                    color = ChatTextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            // Online badge
            Box(
                modifier = Modifier
                    .background(ChatCyan.copy(alpha = 0.10f), RoundedCornerShape(12.dp))
                    .border(0.5.dp, ChatCyan.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "ONLINE",
                    color = ChatCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))
        }


        // ==========================================
        // MESSAGES LIST
        // ==========================================
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            state = listState,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 12.dp)
        ) {
            items(messages) { message ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 2 }
                ) {
                    ChatBubble(message = message)
                }
            }

            // Loading indicator
            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .background(ChatAiBubble, RoundedCornerShape(16.dp))
                                .border(0.5.dp, ChatBorder, RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    color = ChatCyan,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Thinking...",
                                    color = ChatTextSecondary,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }


        // ==========================================
        // SUGGESTED QUESTIONS (only if no user messages yet)
        // ==========================================
        val hasUserMessages = messages.any { it.role == "user" }

        if (!hasUserMessages && theory.sampleQA.isNotEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 8.dp)
            ) {
                Text(
                    text = "SUGGESTED QUESTIONS",
                    color = ChatTextSecondary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    theory.sampleQA.forEach { (question, _) ->
                        Box(
                            modifier = Modifier
                                .background(ChatCyan.copy(alpha = 0.08f), RoundedCornerShape(12.dp))
                                .border(0.5.dp, ChatCyan.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .clickable { sendMessage(question) }
                                .padding(horizontal = 12.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = question,
                                color = ChatCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }


        // ==========================================
        // INPUT BAR
        // ==========================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ChatInputBg)
                .border(
                    width = 0.5.dp,
                    color = ChatBorder,
                    shape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp)
                )
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                placeholder = {
                    Text(
                        text = "Ask about ${theory.title.lowercase()}...",
                        color = ChatTextSecondary.copy(alpha = 0.5f),
                        fontSize = 13.sp
                    )
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = ChatTextPrimary,
                    unfocusedTextColor = ChatTextPrimary,
                    cursorColor = ChatCyan,
                    focusedBorderColor = ChatCyan.copy(alpha = 0.4f),
                    unfocusedBorderColor = ChatBorder
                ),
                shape = RoundedCornerShape(14.dp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        sendMessage(inputText)
                        keyboardController?.hide()
                    }
                ),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Send button
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        if (inputText.isNotBlank() && !isLoading)
                            ChatCyan
                        else
                            ChatCyan.copy(alpha = 0.2f)
                    )
                    .clickable(enabled = inputText.isNotBlank() && !isLoading) {
                        sendMessage(inputText)
                        keyboardController?.hide()
                    },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank() && !isLoading)
                        Color(0xFF0B0F19) else ChatTextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}


// ==========================================
// CHAT BUBBLE
// ==========================================

@Composable
private fun ChatBubble(message: ChatMessage) {
    val isUser = message.role == "user"

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = 300.dp)
                .background(
                    if (isUser) ChatUserBubble else ChatAiBubble,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .border(
                    0.5.dp,
                    if (isUser) ChatCyan.copy(alpha = 0.15f) else ChatBorder,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Column {
                if (!isUser) {
                    Text(
                        text = "AutoLearn AI",
                        color = ChatGreen,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }

                Text(
                    text = message.text,
                    color = ChatTextPrimary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            }
        }
    }
}
