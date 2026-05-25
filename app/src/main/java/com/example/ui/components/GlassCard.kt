package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.TextStyle
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    borderWidth: Dp = 1.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(CardGlassSurface)
            .border(borderWidth, GlassBorder, RoundedCornerShape(cornerRadius))
            .padding(16.dp),
        content = content
    )
}

@Composable
fun GlassCardCustom(
    modifier: Modifier = Modifier,
    cornerRadius: Dp = 16.dp,
    backgroundColor: Color = CardGlassSurface,
    borderColor: Color = GlassBorder,
    borderWidth: Dp = 1.dp,
    padding: Dp = 16.dp,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(cornerRadius))
            .background(backgroundColor)
            .border(borderWidth, borderColor, RoundedCornerShape(cornerRadius))
            .padding(padding),
        content = content
    )
}

@Composable
fun macOSWindowChrome(
    modifier: Modifier = Modifier,
    title: String = "bash",
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1E1E1E))
            .border(1.dp, Color(0xFF333333), RoundedCornerShape(12.dp))
    ) {
        // macOS Traffic Lights Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2D2D2D))
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Traffic light dots
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(TrafficLightRed))
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(TrafficLightYellow))
                Box(modifier = Modifier.size(10.dp).clip(RoundedCornerShape(5.dp)).background(TrafficLightGreen))
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                text = title,
                color = TextSecondary,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = MonospaceCode
            )
            
            Spacer(modifier = Modifier.weight(1.3f))
        }
        
        // Terminal Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            content = content
        )
    }
}

@Composable
fun BlinkingCursor(
    prefix: String = "",
    text: String,
    textColor: Color = TextPrimary,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    modifier: Modifier = Modifier
) {
    var cursorVisible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        while (true) {
            delay(550)
            cursorVisible = !cursorVisible
        }
    }
    
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$prefix$text",
            color = textColor,
            style = style
        )
        Text(
            text = "_",
            color = UbuntuOrange,
            style = style,
            modifier = Modifier.alpha(if (cursorVisible) 1f else 0f)
        )
    }
}
