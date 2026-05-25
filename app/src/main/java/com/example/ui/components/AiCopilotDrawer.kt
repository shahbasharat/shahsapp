package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.GeminiCopilotService
import com.example.ui.theme.*
import kotlinx.coroutines.launch

data class CopilotMessage(
    val sender: String, // "user" or "copilot"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCopilotDrawer(
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    var inputText by remember { mutableStateOf("") }
    var isGenerating by remember { mutableStateOf(false) }

    val messages = remember {
        mutableStateListOf<CopilotMessage>().apply {
            add(
                CopilotMessage(
                    sender = "copilot",
                    text = "Hello! I am Shah's Security Operations Center (SOC) Path AI Copilot. 🔐\n\nI can help you review simulated firewall rules, inspect system log entries, decode Base64 hashes, explain intrusion detection concepts, or provide helpful pointers with lab questions.\n\nWhat would you like to build or analyze today?"
                )
            )
        }
    }

    // Scroll to bottom on new message
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    fun handleSendMessage(query: String) {
        if (query.trim().isEmpty() || isGenerating) return
        
        messages.add(CopilotMessage(sender = "user", text = query))
        inputText = ""
        isGenerating = true

        scope.launch {
            val response = GeminiCopilotService.generateResponse(
                prompt = query,
                systemInstruction = "You are Shah's SOC Path AI Copilot, an expert, brilliant Senior SOC Analyst guiding a Security Operations Center student. Guide the user step by step through security tasks, Linux syslog/auth.log parsing, CTF hints, block intrusions, and defender tools. Keep answers visually beautiful, well-structured, clear, professional, and relatively concise so they fit beautifully in mobile layout displays."
            )
            messages.add(CopilotMessage(sender = "copilot", text = response))
            isGenerating = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(Color(0xE616161A)) // Dark Obsidian frost background
            .border(1.dp, GlassBorder, RoundedCornerShape(topStart = 16.dp, bottomStart = 16.dp))
            .padding(14.dp)
            .testTag("ai_copilot_drawer")
    ) {
        // Drawer Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(UbuntuOrange.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.SmartToy,
                    contentDescription = "AI Copilot Avatar",
                    tint = UbuntuOrange,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(10.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Shah's SOC AI Copilot",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(if (isGenerating) WarningYellow else SuccessGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isGenerating) "Analyzing Security Context..." else "Online - Ready to assist",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            IconButton(
                onClick = onClose,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("close_copilot_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Drawer",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Divider(color = GlassBorder, thickness = 1.dp, modifier = Modifier.padding(bottom = 12.dp))

        // Chats Thread
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(messages) { message ->
                val isCopilot = message.sender == "copilot"
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isCopilot) Arrangement.Start else Arrangement.End
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .clip(
                                RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isCopilot) 2.dp else 14.dp,
                                    bottomEnd = if (isCopilot) 14.dp else 2.dp
                                )
                            )
                            .background(
                                if (isCopilot) Color(0xFF242429) else UbuntuOrange.copy(alpha = 0.85f)
                            )
                            .border(
                                width = 1.dp,
                                color = if (isCopilot) GlassBorder else Color.Transparent,
                                shape = RoundedCornerShape(
                                    topStart = 14.dp,
                                    topEnd = 14.dp,
                                    bottomStart = if (isCopilot) 2.dp else 14.dp,
                                    bottomEnd = if (isCopilot) 14.dp else 2.dp
                                )
                            )
                            .padding(12.dp)
                    ) {
                        Column {
                            // Text bubble
                            Text(
                                text = message.text,
                                color = TextPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                lineHeight = 18.sp
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            // Small timestamp alignment
                            Text(
                                text = if (isCopilot) "AI Copilot" else "Active Analyst",
                                color = if (isCopilot) TextSecondary.copy(alpha = 0.8f) else TextPrimary.copy(alpha = 0.7f),
                                fontSize = 9.sp,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = if (isCopilot) TextAlign.Start else TextAlign.End
                            )
                        }
                    }
                }
            }

            if (isGenerating) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF242429)),
                            modifier = Modifier
                                .border(1.dp, GlassBorder, RoundedCornerShape(14.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                CircularProgressIndicator(
                                    color = UbuntuOrange,
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 1.5.dp
                                )
                                Text(
                                    text = "Analyzing Threat Matrix...",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Preset Cyber Prompts Chips
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Suggested SOC Scenarios",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 6.dp)
        )
        
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            val presetQuestions = listOf(
                "Explain syslog security risks",
                "How to scan vuln ports with nmap?",
                "Suggest SSH Log filter commands",
                "Give a SOC career tip"
            )
            
            presetQuestions.forEach { preset ->
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CardGlassSurface)
                        .clickable(enabled = !isGenerating) {
                            handleSendMessage(preset)
                        }
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = preset,
                        color = MacOSBlue,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Chat Input Area
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = {
                    Text(
                        text = "Ask Shah's Copilot...",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_copilot_input")
                    .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                    .clip(RoundedCornerShape(12.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFF1E1E22),
                    unfocusedContainerColor = Color(0xFF1E1E22),
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 12.sp),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Text,
                    imeAction = ImeAction.Send
                ),
                keyboardActions = KeyboardActions(
                    onSend = { handleSendMessage(inputText) }
                )
            )

            IconButton(
                onClick = { handleSendMessage(inputText) },
                enabled = inputText.isNotEmpty() && !isGenerating,
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (inputText.isNotEmpty() && !isGenerating) UbuntuOrange else CardGlassSurface)
                    .testTag("ai_copilot_send_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send Message",
                    tint = if (inputText.isNotEmpty() && !isGenerating) TextPrimary else TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
