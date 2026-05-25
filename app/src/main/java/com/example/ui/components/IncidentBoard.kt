package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Incident Data Model
data class IncidentModel(
    val id: String,
    val title: String,
    val severity: String, // "LOW", "MEDIUM", "HIGH", "CRITICAL"
    val timestamp: String,
    val hostSystem: String,
    val description: String,
    var status: String, // "TRIAGE", "CONTAINMENT", "REMEDIATED"
    var isIsolated: Boolean = false,
    var isProcessTerminated: Boolean = false,
    var isCredentialsRevoked: Boolean = false,
    val recommendedPlaybook: List<String>
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun IncidentBoard(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    
    // Core incident tickets list
    val masterIncidents = remember {
        mutableStateListOf<IncidentModel>(
            IncidentModel(
                id = "INC-1092",
                title = "SQL Injection Probe Attempt - Web Gateway",
                severity = "HIGH",
                timestamp = "21:55:12",
                hostSystem = "Websrv-Pool-03",
                description = "Automated sensor triggered multiple single quote queries targeting parameter payload injection in search fields.",
                status = "TRIAGE",
                recommendedPlaybook = listOf("Enable WAF IP blocker rule", "Inspect raw access.log parameters", "Audit DB query formatting files")
            ),
            IncidentModel(
                id = "INC-1093",
                title = "SSH Brute-Force Auth Flooding",
                severity = "MEDIUM",
                timestamp = "21:54:40",
                hostSystem = "Edge-Bastion-Linux",
                description = "Authentication daemon logged 340+ failed credential attempts from unidentified external subnet over Port 22.",
                status = "TRIAGE",
                recommendedPlaybook = listOf("Isolate Edge-Bastion from general access", "Null-route attacker IP via iptables-cmd", "Rotate Bastion SSH key pairs")
            ),
            IncidentModel(
                id = "INC-1094",
                title = "Ransomware 'ShadowCrypt' Process Active",
                severity = "CRITICAL",
                timestamp = "21:52:19",
                hostSystem = "DB-Finance-Prod",
                description = "High rate of file system encryption noticed matching extension signatures with known bad indicators of compromise.",
                status = "CONTAINMENT",
                isIsolated = true, // already isolated
                recommendedPlaybook = listOf("Isolate host VM network interface immediately", "SIGKILL shadowcrypt binary daemon process", "Check offline volume storage snapshots")
            ),
            IncidentModel(
                id = "INC-1090",
                title = "Phishing Email Anchor Link click",
                severity = "LOW",
                timestamp = "21:42:01",
                hostSystem = "User-Workstation-45",
                description = "Employee reports accidental clicks on a targeted macro phishing payload. Security system quarantined the executable file.",
                status = "REMEDIATED",
                isIsolated = true,
                isProcessTerminated = true,
                isCredentialsRevoked = true,
                recommendedPlaybook = listOf("Quarantine downloaded macro xls", "Kill outlook process", "Enforce credential rotation")
            )
        )
    }

    // Active ticket detail selection
    var selectedIncidentId by remember { mutableStateOf<String?>("INC-1092") }
    
    // Virtual security operations logs feed
    val socLogsStream = remember {
        mutableStateListOf<String>().apply {
            add("[SYSTEM READY] Incident response console listening for active SIEM/SOC triggers...")
            add("[ALERT INTEL] Active brute-force threshold scanner daemon loaded on Gateway 2.")
        }
    }

    val selectedIncident = masterIncidents.find { it.id == selectedIncidentId }

    // Helper functions
    fun logEvent(msg: String) {
        socLogsStream.add("[${System.currentTimeMillis() % 100000000 / 10000}] $msg")
    }

    fun generateRandomIncident() {
        val idNum = (1095..1200).random()
        val hosts = listOf("Domain-Controller-01", "Storage-San-Secured", "Web-Apache-01", "HR-Payroll-Database", "API-Core-Endpoint")
        val incidentTypes = listOf(
            Triple("Malicious PowerShell Script Invocation", "CRITICAL", listOf("Disable local WinRM service", "Extract script bytes", "Disable user account")),
            Triple("Suspicious Port Probe Scan Received", "LOW", listOf("Verify iptables state", "Whitelist trusted subnet", "Audit edge firewall configs")),
            Triple("Tor Onion Exit-Node Connection active", "HIGH", listOf("Contain node networking interface", "Kill Tor Proxy daemon ID", "Analyze outbound stream session")),
            Triple("Database Credential Dumping via backup dump", "CRITICAL", listOf("Disable db server network access", "Kill rogue mysqldump process ID", "Rotate major sysadmin keys"))
        )
        
        val chosen = incidentTypes.random()
        val newInc = IncidentModel(
            id = "INC-$idNum",
            title = chosen.first,
            severity = chosen.second,
            timestamp = "21:57:04",
            hostSystem = hosts.random(),
            description = "Intrusion behavioral intelligence agent identified high risk anomalous action on host system.",
            status = "TRIAGE",
            recommendedPlaybook = chosen.third
        )

        masterIncidents.add(0, newInc)
        selectedIncidentId = newInc.id
        logEvent("SIEM SYSLOG TRIGGER: New incident '${newInc.title}' assigned to triage pipeline!")
        Toast.makeText(context, "New Alert ticket received! ${newInc.id}", Toast.LENGTH_SHORT).show()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Incident header controls
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "INCIDENT RESPONSE & TRIAGE BOARD",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Contain, eradicate, and restore live security incidents instantly.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { generateRandomIncident() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UbuntuOrange,
                        contentColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.AddAlert, contentDescription = "Simulate", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("SIMULATE INCIDENT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Kanban Pipeline Statistics Cards Row
        val triageCount = masterIncidents.count { it.status == "TRIAGE" }
        val containmentCount = masterIncidents.count { it.status == "CONTAINMENT" }
        val remediatedCount = masterIncidents.count { it.status == "REMEDIATED" }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            StatsPipelineCard(label = "ACTIVE TRIAGE", count = triageCount, color = ErrorRed, modifier = Modifier.weight(1f))
            StatsPipelineCard(label = "CONTAINMENT", count = containmentCount, color = WarningYellow, modifier = Modifier.weight(1f))
            StatsPipelineCard(label = "REMEDIATED", count = remediatedCount, color = SuccessGreen, modifier = Modifier.weight(1f))
        }

        // Incident Board Section: Split list of tickets and Playbook action card
        Row(
            modifier = Modifier.fillMaxWidth().height(480.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left list of incidents
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252627))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "SECURITY EVENTS QUEUE [${masterIncidents.size}]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(masterIncidents, key = { it.id }) { incident ->
                        val isSelected = selectedIncidentId == incident.id
                        val sevColor = when (incident.severity) {
                            "CRITICAL" -> ErrorRed
                            "HIGH" -> UbuntuOrange
                            "MEDIUM" -> WarningYellow
                            else -> SuccessGreen
                        }

                        val statusColor = when (incident.status) {
                            "REMEDIATED" -> SuccessGreen
                            "CONTAINMENT" -> WarningYellow
                            else -> ErrorRed
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (isSelected) Color(0x22FFFFFF) else Color.Transparent)
                                .clickable { selectedIncidentId = incident.id }
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    // Custom severity bullet dot
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(sevColor)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = incident.id,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                // Status text badge label
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(statusColor.copy(alpha = 0.15f))
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = incident.status,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = statusColor,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            Text(
                                text = incident.title,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Host: ${incident.hostSystem}",
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )

                                Text(
                                    text = incident.timestamp,
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(top = 10.dp))
                        }
                    }
                }
            }

            // Right active playbook actions box
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(Color(0xFF131415))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF1E1E1E))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "PLAYBOOK EXECUTOR DAEMON",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacOSBlue,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (selectedIncident == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Select an active incident from event queue to launch playbook.", fontSize = 11.sp, color = TextSecondary, textAlign = TextAlign.Center, modifier = Modifier.padding(16.dp))
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header details of focused incident
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedIncident.id,
                                    fontSize = 14.sp,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(3.dp))
                                        .background(
                                            when (selectedIncident.severity) {
                                                "CRITICAL" -> ErrorRed.copy(alpha = 0.2f)
                                                else -> UbuntuOrange.copy(alpha = 0.2f)
                                            }
                                        )
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = selectedIncident.severity,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selectedIncident.severity == "CRITICAL") ErrorRed else UbuntuOrange
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = selectedIncident.title,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x1AFFFFFF)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text("ALERT METRIC SYNOPSIS:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedIncident.description,
                                    fontSize = 10.sp,
                                    color = TextLightBody
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Target Host: ${selectedIncident.hostSystem}",
                                    fontSize = 10.sp,
                                    color = UbuntuOrange,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Playbook Checkboxes
                        Text(
                            text = "PRESCRIBED MITIGATION WORKFLOWS:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        // Action 1: Network Isolation
                        PlaybookActionSelector(
                            label = "Isolate Network Segment [${selectedIncident.hostSystem}]",
                            isChecked = selectedIncident.isIsolated,
                            onToggle = { checked ->
                                val listIndex = masterIncidents.indexOfFirst { it.id == selectedIncident.id }
                                if (listIndex != -1) {
                                    val currentInc = masterIncidents[listIndex]
                                    val updated = currentInc.copy(isIsolated = checked)
                                    masterIncidents[listIndex] = updated
                                    
                                    if (checked) {
                                        logEvent("CONTAINMENT SUITE: Host ${selectedIncident.hostSystem} virtual NIC isolated from internal subnets.")
                                        // Auto triage progression
                                        if (updated.status == "TRIAGE") {
                                            masterIncidents[listIndex] = updated.copy(status = "CONTAINMENT", isIsolated = checked)
                                        }
                                        Toast.makeText(context, "${selectedIncident.hostSystem} ISOLATED!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        logEvent("CONTAINMENT SUITE: Host ${selectedIncident.hostSystem} network connectivity restored.")
                                    }
                                }
                            }
                        )

                        // Action 2: Process Termination
                        PlaybookActionSelector(
                            label = "Terminate Suspect Executable Process PID",
                            isChecked = selectedIncident.isProcessTerminated,
                            onToggle = { checked ->
                                val listIndex = masterIncidents.indexOfFirst { it.id == selectedIncident.id }
                                if (listIndex != -1) {
                                    val currentInc = masterIncidents[listIndex]
                                    masterIncidents[listIndex] = currentInc.copy(isProcessTerminated = checked)
                                    if (checked) {
                                        logEvent("ERADICATION SUITE: SIGKILL sent to offending binary daemon IDs on ${selectedIncident.hostSystem}.")
                                        Toast.makeText(context, "Malicious daemon terminated!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        logEvent("ERADICATION SUITE: Daemon kill flag retracted.")
                                    }
                                }
                            }
                        )

                        // Action 3: Credential Revocation
                        PlaybookActionSelector(
                            label = "Audit & Revoke compromised API keys/keys",
                            isChecked = selectedIncident.isCredentialsRevoked,
                            onToggle = { checked ->
                                val listIndex = masterIncidents.indexOfFirst { it.id == selectedIncident.id }
                                if (listIndex != -1) {
                                    val currentInc = masterIncidents[listIndex]
                                    masterIncidents[listIndex] = currentInc.copy(isCredentialsRevoked = checked)
                                    if (checked) {
                                        logEvent("RECOVERY SUITE: System credentials suspended. Multi-factor force challenge triggered.")
                                        Toast.makeText(context, "System tokens revoked/rotated!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        logEvent("RECOVERY SUITE: Keys enabled.")
                                    }
                                }
                            }
                        )

                        // Final Remediate Complete button
                        val isPlaybookFinished = selectedIncident.isIsolated && selectedIncident.isProcessTerminated && selectedIncident.isCredentialsRevoked
                        
                        Button(
                            onClick = {
                                val listIndex = masterIncidents.indexOfFirst { it.id == selectedIncident.id }
                                if (listIndex != -1) {
                                    val currentInc = masterIncidents[listIndex]
                                    masterIncidents[listIndex] = currentInc.copy(status = "REMEDIATED")
                                    logEvent("RESOLVED INCIDENT: ${selectedIncident.id} successfully shut down and marked REMEDIATED.")
                                    Toast.makeText(context, "Incident closed securely! Great work and positive threat score logged.", Toast.LENGTH_LONG).show()
                                }
                            },
                            enabled = isPlaybookFinished && selectedIncident.status != "REMEDIATED",
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SuccessGreen,
                                contentColor = Color.Black
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().height(40.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (selectedIncident.status == "REMEDIATED") "CLOSED & ARCHIVED" else "CLOSE INCIDENT & RESOLVE",
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Live SOC logs terminal view at bottom
        GlassCard {
            Text(
                text = "SOC SECURITY ORCHESTRATION & EVENT LOGGER",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color.Black)
                    .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                    .clip(RoundedCornerShape(8.dp))
                    .padding(8.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    reverseLayout = true // Show latest first or bottom-loaded
                ) {
                    items(socLogsStream.toList().asReversed()) { logLine ->
                        Text(
                            text = logLine,
                            color = if (logLine.contains("TRIGGER") || logLine.contains("CRITICAL")) ErrorRed else if (logLine.contains("CLOSED") || logLine.contains("RESOLVED")) SuccessGreen else TextSecondary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatsPipelineCard(
    label: String,
    count: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(CardGlassSurface)
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
            .padding(14.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Column {
            Text(
                text = label,
                fontSize = 9.sp,
                color = TextSecondary,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "$count",
                    fontSize = 22.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun PlaybookActionSelector(
    label: String,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp))
            .background(if (isChecked) Color(0x1F30D158) else Color(0x0AFFFFFF))
            .border(1.dp, if (isChecked) SuccessGreen.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(6.dp))
            .clickable { onToggle(!isChecked) }
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = if (isChecked) Icons.Default.CheckBox else Icons.Default.CheckBoxOutlineBlank,
                contentDescription = null,
                tint = if (isChecked) SuccessGreen else TextSecondary,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                color = if (isChecked) SuccessGreen else TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        Icon(
            imageVector = if (isChecked) Icons.Default.CheckCircle else Icons.Default.PlayArrow,
            contentDescription = null,
            tint = if (isChecked) SuccessGreen else TextSecondary,
            modifier = Modifier.size(12.dp)
        )
    }
}

val TextLightBody = Color(0xFFD1D1D6)
