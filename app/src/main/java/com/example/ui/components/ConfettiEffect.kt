package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import com.example.ui.theme.MacOSBlue
import com.example.ui.theme.UbuntuOrange
import kotlinx.coroutines.delay
import java.util.Random

data class ConfettiParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var size: Float,
    var rotation: Float,
    var rotationSpeed: Float
)

@Composable
fun ConfettiEffect(
    trigger: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!trigger) return

    val particles = remember { mutableStateListOf<ConfettiParticle>() }
    val random = remember { Random() }

    LaunchedEffect(trigger) {
        particles.clear()
        // Generate 70 colorful particles from top/middle
        for (i in 0..120) {
            val isOrange = random.nextBoolean()
            particles.add(
                ConfettiParticle(
                    x = random.nextFloat() * 1000f, // updated on first draw to match screen width safely
                    y = -50f - random.nextFloat() * 200f,
                    vx = (random.nextFloat() - 0.5f) * 8f,
                    vy = 5f + random.nextFloat() * 12f,
                    color = if (isOrange) UbuntuOrange else MacOSBlue,
                    size = 10f + random.nextFloat() * 15f,
                    rotation = random.nextFloat() * 360f,
                    rotationSpeed = (random.nextFloat() - 0.5f) * 10f
                )
            )
        }

        // Loop 150 frames (~5 seconds) then stop
        for (frame in 0..150) {
            delay(32) // ~30 fps update
            particles.forEach { particle ->
                particle.x += particle.vx
                particle.y += particle.vy
                particle.vy += 0.2f // gravity simulate
                particle.rotation += particle.rotationSpeed
            }
        }
        onFinished()
    }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        particles.forEach { p ->
            // Adjust X boundaries if needed
            if (p.x < 0) p.x = width
            if (p.x > width) p.x = 0f

            if (p.y < height) {
                // Draw a small colorful rectangle/rotated dash representing confetti
                drawRect(
                    color = p.color,
                    topLeft = Offset(p.x, p.y),
                    size = androidx.compose.ui.geometry.Size(p.size, p.size * 0.5f)
                )
            }
        }
    }
}
