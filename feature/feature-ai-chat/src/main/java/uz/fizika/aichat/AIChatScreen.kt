package uz.fizika.aichat

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import uz.fizika.aichat.repository.AIRepository
import uz.fizika.core.ui.components.*
import uz.fizika.core.ui.theme.NeonColors
import javax.inject.Inject

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val id: Long = System.currentTimeMillis()
)

@Composable
fun AIChatScreen(
    navController: NavController,
    repo: AIRepository = hiltViewModel<AIChatViewModel>().repo
) {
    var messages by remember { mutableStateOf(listOf(ChatMessage("Привет! Я твой ИИ-ассистент по физике. Чем могу помочь сегодня?", false))) }
    var inputText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var showApiKeyDialog by remember { mutableStateOf(!repo.hasApiKey()) }
    var apiKeyInput by remember { mutableStateOf("") }
    
    val coroutineScope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    if (showApiKeyDialog) {
        ApiKeyDialog(
            value = apiKeyInput,
            onValueChange = { apiKeyInput = it },
            onConfirm = {
                repo.saveApiKey(apiKeyInput)
                showApiKeyDialog = false
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(NeonColors.Background)
    ) {
        NeonTopBar(
            title = "ИИ Ассистент",
            subtitle = "Gemini 2.0 Flash",
            accentColor = NeonColors.Secondary
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(msg)
            }
            if (isTyping) {
                item {
                    Text(
                        "ИИ печатает...",
                        color = NeonColors.OnSurfaceVariant,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        // Input Area
        Surface(
            color = NeonColors.Surface,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .navigationBarsPadding()
                    .imePadding(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Задайте вопрос...", color = NeonColors.OnSurfaceVariant) },
                    shape = RoundedCornerShape(24.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonColors.Secondary,
                        unfocusedBorderColor = NeonColors.Outline,
                        focusedContainerColor = NeonColors.SurfaceVariant,
                        unfocusedContainerColor = NeonColors.SurfaceVariant,
                        focusedTextColor = NeonColors.OnBackground,
                        unfocusedTextColor = NeonColors.OnBackground
                    )
                )
                
                IconButton(
                    onClick = {
                        if (inputText.isBlank()) return@IconButton
                        val userMsg = ChatMessage(inputText, true)
                        messages = messages + userMsg
                        val query = inputText
                        inputText = ""
                        isTyping = true
                        
                        coroutineScope.launch {
                            listState.animateScrollToItem(messages.size - 1)
                            
                            var aiResponseText = ""
                            repo.sendMessageStream(query)?.collect { chunk ->
                                aiResponseText += chunk
                                if (messages.last().isUser) {
                                    messages = messages + ChatMessage(aiResponseText, false)
                                } else {
                                    messages = messages.dropLast(1) + ChatMessage(aiResponseText, false)
                                }
                                listState.scrollToItem(messages.size - 1)
                            }
                            isTyping = false
                        }
                    },
                    modifier = Modifier
                        .background(NeonColors.Secondary, CircleShape)
                ) {
                    Icon(Icons.Default.Send, null, tint = NeonColors.OnSecondary)
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.End else Alignment.Start
    val bgColor = if (message.isUser) NeonColors.PrimaryContainer else NeonColors.SurfaceVariant
    val textColor = if (message.isUser) NeonColors.Primary else NeonColors.OnSurface
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 0.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 0.dp)
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = alignment
    ) {
        Surface(
            color = bgColor,
            shape = shape,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun ApiKeyDialog(value: String, onValueChange: (String) -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = { },
        containerColor = NeonColors.SurfaceContainer,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.VpnKey, null, tint = NeonColors.Secondary)
                Spacer(Modifier.width(8.dp))
                Text("Требуется API Ключ", color = NeonColors.OnBackground)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Для работы ИИ необходимо ввести бесплатный ключ Google Gemini. Получить его можно на aistudio.google.com",
                    style = MaterialTheme.typography.bodySmall,
                    color = NeonColors.OnSurfaceVariant
                )
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("AI_API_KEY", color = NeonColors.OnSurfaceVariant) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonColors.Secondary,
                        unfocusedBorderColor = NeonColors.Outline
                    )
                )
            }
        },
        confirmButton = {
            NeonButton(text = "Сохранить", onClick = onConfirm, enabled = value.isNotBlank())
        }
    )
}

// Simple ViewModel for Hilt injection
@dagger.hilt.android.lifecycle.HiltViewModel
class AIChatViewModel @Inject constructor(
    val repo: AIRepository
) : androidx.lifecycle.ViewModel()
