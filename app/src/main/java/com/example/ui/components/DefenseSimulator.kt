package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.math.max

// Server Node represent in cluster
data class DeviceNodeModel(
    val id: String,
    val name: String,
    val role: String,
    val ip: String,
    var integrity: Int = 100, // 0 to 100%
    var activeThreat: String = "NONE", // "NONE", "DDOS", "BRUTEFORCE", "INJECTION", "RANSOMWARE"
    var stateLabel: String = "OPERATIONAL"
)

// Mitigation command option
data class MitigationOption(
    val name: String,
    val description: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector,
    val countersThreat: String // Will purge this threat type on matching node
)

@Composable
fun DefenseSimulator(
    modifier: Modifier = Modifier,
    onSimSuccess: () -> Unit = {}
) {
    val context = LocalContext.current
    
    // Core Game States: "IDLE", "WAVE_1", "WAVE_2", "WAVE_3", "FAILED", "VICTORY"
    var gameState by remember { mutableStateOf("IDLE") }
    var waveNum by remember { mutableStateOf(1) }
    var scorePoints by remember { mutableStateOf(0) }
    var elapsedSeconds by remember { mutableStateOf(0) }
    var currentLogText by remember { mutableStateOf("Ready to initiate active hardening simulation drills.") }

    // Init the server nodes
    val nodes = remember {
        mutableStateListOf<DeviceNodeModel>(
            DeviceNodeModel("NODE-A", "Ubuntu Web Gateway", "IIS & Reverse Proxy", "192.168.1.10"),
            DeviceNodeModel("NODE-B", "Crown AD Controller", "Active Directory Server", "192.168.1.50"),
            DeviceNodeModel("NODE-C", "Production SQL Core", "Secure Database Host", "192.168.1.120")
        )
    }

    // List of active mitigations
    val tools = remember {
        listOf(
            MitigationOption("Deploy Cloudflare WAF", "Filters HTTP packets and Web injection scripts", Icons.Default.CloudQueue, "DDOS"),
            MitigationOption("Enable Sysadmin MFA", "Halts stolen credential brute forcing on domain", Icons.Default.VpnKey, "BRUTEFORCE"),
            MitigationOption("Isolate Target SQL Input", "Clamps down SQL queries & parameterized variables", Icons.Default.Source, "INJECTION"),
            MitigationOption("Execute Process Kill PID", "Forces termination of illegitimate shell malware", Icons.Default.Cancel, "RANSOMWARE"),
            MitigationOption("Block Source IP via UFW", "Halts all raw TCP requests from suspicious networks", Icons.Default.Security, "ALL")
        )
    }

    // Real-time wave telemetry loop
    LaunchedEffect(gameState) {
        if (gameState != "IDLE" && gameState != "FAILED" && gameState != "VICTORY") {
            while (gameState == "WAVE_1" || gameState == "WAVE_2" || gameState == "WAVE_3") {
                delay(1000)
                elapsedSeconds++
                
                // Decay matching threat systems integrity
                var anyActiveThreats = false
                nodes.forEachIndexed { i, n ->
                    if (n.activeThreat != "NONE") {
                        anyActiveThreats = true
                        val decay = if (gameState == "WAVE_1") 4 else if (gameState == "WAVE_2") 6 else 9
                        val nextIntegrity = max(0, n.integrity - decay)
                        nodes[i] = n.copy(
                            integrity = nextIntegrity,
                            stateLabel = if (nextIntegrity <= 0) "COMPROMISED" else if (nextIntegrity < 40) "CRITICAL WARNING" else "UNDER ATTACK"
                        )
                        
                        // Game Over Check
                        if (nextIntegrity <= 0) {
                            gameState = "FAILED"
                            currentLogText = "Critical security failure! ${n.name} has fell to 0% system integrity. Threat payloads fully deployed."
                        }
                    } else {
                        // Slowly repair healed nodes
                        if (n.integrity < 100) {
                            val healed = (n.integrity + 2).coerceAtMost(100)
                            nodes[i] = n.copy(
                                integrity = healed,
                                stateLabel = if (healed == 100) "SECURED" else "RECOVERING"
                            )
                        }
                    }
                }

                // Wave transition check: If no threats remain in current wave, level up!
                if (!anyActiveThreats && gameState != "FAILED" && gameState != "VICTORY") {
                    when (gameState) {
                        "WAVE_1" -> {
                            scorePoints += 150
                            waveNum = 2
                            gameState = "WAVE_2"
                            triggerAttackWave(2, nodes)
                            currentLogText = "WAVE 1 CLEARED! Initiating WAVE 2: SQL Injection & Lateral credential movement detected."
                        }
                        "WAVE_2" -> {
                            scorePoints += 250
                            waveNum = 3
                            gameState = "WAVE_3"
                            triggerAttackWave(3, nodes)
                            currentLogText = "WAVE 2 CLEARED! WARNING: Initiating WAVE 3: Extreme ransomware encryption attack in bounds!"
                        }
                        "WAVE_3" -> {
                            scorePoints += 500
                            gameState = "VICTORY"
                            onSimSuccess()
                            currentLogText = "CONGRATS SECURE CHIEF! All defensive training waves neutralized successfully! Network secured."
                        }
                    }
                }
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Option 3 Header card
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(UbuntuOrange.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.CastConnected, contentDescription = "Sim", tint = UbuntuOrange, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SOC REAL-TIME ATTACK DEFENDER",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Wave-based incident defense system simulating server breaches.",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF131415))
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Score: $scorePoints XP | Timer: ${elapsedSeconds}s",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = UbuntuOrange,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }

        // Active State Display Control Panel
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = MacOSDarkSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "WAVE DRILL STATUS: ${gameState.uppercase()}",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold,
                        color = when (gameState) {
                            "IDLE" -> TextSecondary
                            "VICTORY" -> SuccessGreen
                            "FAILED" -> ErrorRed
                            else -> WarningYellow
                        }
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = currentLogText,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextLightBody,
                        lineHeight = 15.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                if (gameState == "IDLE" || gameState == "FAILED" || gameState == "VICTORY") {
                    Button(
                        onClick = {
                            // Reset state & initiate simulation
                            elapsedSeconds = 0
                            scorePoints = 0
                            waveNum = 1
                            gameState = "WAVE_1"
                            nodes[0] = DeviceNodeModel("NODE-A", "Ubuntu Web Gateway", "PROXY & Web Controller", "192.168.1.10", 100, "DDOS", "DDoS Spike")
                            nodes[1] = DeviceNodeModel("NODE-B", "Crown AD Controller", "Active Directory Server", "192.168.1.50", 100, "NONE", "OPERATIONAL")
                            nodes[2] = DeviceNodeModel("NODE-C", "Production SQL Core", "Secure Database Host", "192.168.1.120", 100, "NONE", "OPERATIONAL")
                            currentLogText = "Wave 1 initiated. 10Gbps DDoS flooding hit Ubuntu Web Gateway! Mitigate immediately."
                            Toast.makeText(context, "Initiating simulation drill! Web router under stress.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = UbuntuOrange, contentColor = Color.White),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(text = if (gameState == "IDLE") "START OPERATION" else "RESET SIMULATOR", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Text(
            text = "ENTERPRISE CLUSTER ENVIRONMENT TELEMETRY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary
        )

        // Cluster node widgets list
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            nodes.forEach { node ->
                val healthColor = if (node.integrity > 70) SuccessGreen else if (node.integrity > 35) WarningYellow else ErrorRed
                val hasThreat = node.activeThreat != "NONE"

                Card(
                    modifier = Modifier
                        .weight(1f)
                        .border(1.dp, if (hasThreat) healthColor.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(10.dp)),
                    colors = CardDefaults.cardColors(containerColor = if (hasThreat) healthColor.copy(alpha = 0.08f) else Color(0x12FFFFFF)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Header IP info
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(if (hasThreat) healthColor else SuccessGreen)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(text = node.id, fontSize = 8.sp, fontWeight = FontWeight.Bold, color = TextSecondary, fontFamily = FontFamily.Monospace)
                            }
                            Text(text = node.ip, fontSize = 8.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                        }

                        // Node titles
                        Text(text = node.name, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = TextPrimary, maxLines = 1)
                        Text(text = node.role, fontSize = 8.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(4.dp))

                        // Integrity progress meter
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp), modifier = Modifier.fillMaxWidth()) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "INTEGRITY", fontSize = 8.sp, color = TextSecondary)
                                Text(text = "${node.integrity}%", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = healthColor, fontFamily = FontFamily.Monospace)
                            }
                            LinearProgressIndicator(
                                progress = { node.integrity / 100f },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(CircleShape),
                                color = healthColor,
                                trackColor = Color(0xFF222524)
                            )
                        }

                        // Threat Alert Info Box
                        if (hasThreat) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(healthColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "ATTACK: ${node.activeThreat}",
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = healthColor,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFF131415))
                                    .padding(horizontal = 6.dp, vertical = 4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = node.stateLabel,
                                    fontSize = 8.sp,
                                    color = SuccessGreen,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = "SECOPS POLICY COMMAND CONTROLLER (APPLY REMEDIATION)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Actions tool lists
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            tools.forEach { tool ->
                val isSimulating = gameState != "IDLE" && gameState != "FAILED" && gameState != "VICTORY"

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(enabled = isSimulating) {
                            // Find matching node facing the threat
                            val targetNodeIndex = nodes.indexOfFirst { n ->
                                n.activeThreat == tool.countersThreat || (tool.countersThreat == "ALL" && n.activeThreat != "NONE")
                            }

                            if (targetNodeIndex != -1) {
                                val targetNode = nodes[targetNodeIndex]
                                val scoreBonus = if (targetNode.integrity > 80) 100 else 50
                                scorePoints += scoreBonus
                                currentLogText = "[MITIGATED] Handled incident trigger successfully on ${targetNode.name}! System state repaired."
                                Toast.makeText(context, "${tool.name} applied! Threat eliminated.", Toast.LENGTH_SHORT).show()
                                
                                nodes[targetNodeIndex] = targetNode.copy(
                                    activeThreat = "NONE",
                                    stateLabel = "SECURED"
                                )
                            } else {
                                Toast.makeText(context, "No active target node fits policy ${tool.name} currently.", Toast.LENGTH_SHORT).show()
                            }
                        },
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0x23FFFFFF)),
                    border = BorderStroke(1.dp, GlassBorder)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(UbuntuOrange.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = tool.icon, contentDescription = null, tint = UbuntuOrange, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = tool.name, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold, color = TextPrimary)
                            Text(text = tool.description, fontSize = 9.sp, color = TextSecondary)
                        }
                        
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF131415))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "DEFEATS: ${tool.countersThreat}",
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace,
                                color = UbuntuOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Handler functions for active attack triggers per wave level
private fun triggerAttackWave(wave: Int, nodes: MutableList<DeviceNodeModel>) {
    when (wave) {
        2 -> {
            // Web Gateway and SQL active attacks
            nodes[0] = nodes[0].copy(activeThreat = "DDOS", stateLabel = "ATTACK INBOUND")
            nodes[2] = nodes[2].copy(activeThreat = "INJECTION", stateLabel = "SQL EXPLOITING")
        }
        3 -> {
            // Enterprise Active Directory gets Ransomware, SQL Server gets brute-forced
            nodes[1] = nodes[1].copy(activeThreat = "RANSOMWARE", stateLabel = "MALWARE LOCKDOWN")
            nodes[2] = nodes[2].copy(activeThreat = "BRUTEFORCE", stateLabel = "CREDENTIAL HAMMER")
        }
    }
}
