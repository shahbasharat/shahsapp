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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import java.util.regex.Pattern

// Threat Intelligence News Data
data class ThreatIntelNews(
    val title: String,
    val cve: String,
    val severity: String, // "CRITICAL", "HIGH", "MEDIUM"
    val group: String, // e.g. APT-28, Lazarus, SilkRoad
    val body: String,
    val date: String
)

// Extracted IOC score info
data class ExtractedIoc(
    val indicator: String,
    val type: String, // "IP", "DOMAIN", "HASH"
    val repScore: Int, // 0 to 100 (where 80+ is dangerous)
    val malwareFamily: String,
    val country: String
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ThreatIntelFeed(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    
    // Extracted Indicator parameters
    var parserInputText by remember { 
        mutableStateOf(
            "Anomalous outbound queries observed on host DB-04 contacting CNC server at 185.190.21.34 and pulling payloads from maliciosdomain.ru/x.bin with checksum file checksum md5: d8578edf8458ce06fbc5bb76a58c5ca4. Run lookup analysis."
        ) 
    }
    
    val parsedIndicatorsList = remember { mutableStateListOf<ExtractedIoc>() }

    // Hardcoded parsed intelligence DB values
    val intelGroupsFeed = remember {
        listOf(
            ThreatIntelNews(
                title = "Lazarus Group Deploying 'KaosRAT' via dynamic SSH side-channels",
                cve = "CVE-2026-10243",
                severity = "CRITICAL",
                group = "APT-38 / Lazarus",
                body = "Recent telemetry indicates Lazarus deploying new KaosRAT variants that rewrite socket libraries and inject root certificates via insecure bash utilities.",
                date = "TODAY"
            ),
            ThreatIntelNews(
                title = "LockBit 4.0 'BlackByte' ransomware targeting enterprise SAN volumes",
                cve = "CVE-2026-92140",
                severity = "CRITICAL",
                group = "LockBit Syndicate",
                body = "BlackByte exploits a zero-day RPC vulnerability to compromise hypervisors and encrypt disk arrays simultaneously, ignoring system state backups.",
                date = "YESTERDAY"
            ),
            ThreatIntelNews(
                title = "Volt Typhoon Active probing edge router routing protocols",
                cve = "CVE-2025-40919",
                severity = "HIGH",
                group = "Volt Typhoon",
                body = "Exploiting command injections inside ancient boundary firewall firmwares to inject passive proxies that forward internal syslog telemetry streams.",
                date = "3 DAYS AGO"
            ),
            ThreatIntelNews(
                title = "Emerging Active Directory elevation exploit disclosed",
                cve = "CVE-2026-11850",
                severity = "HIGH",
                group = "APT-29 / Cozy Bear",
                body = "A privilege escalation vulnerability in Kerberos tickets configuration. Allows domain guest accounts to execute full administrator system routines.",
                date = "4 DAYS AGO"
            )
        )
    }

    // Function to run fast IOC parser extraction
    fun runIocParser(text: String) {
        parsedIndicatorsList.clear()
        
        // Regex patterns
        val ipPattern = Pattern.compile("\\b(?:[0-9]{1,3}\\.){3}[0-9]{1,3}\\b")
        val domainPattern = Pattern.compile("(?i)\\b[A-Za-z0-9.-]+\\.(?:com|org|net|ru|biz|xyz|cn|local)\\b")
        val hashPattern = Pattern.compile("\\b[a-fA-F0-9]{32,64}\\b")

        val ipsMatch = ipPattern.matcher(text)
        while (ipsMatch.find()) {
            val ip = ipsMatch.group()
            val score = if (ip.startsWith("192.") || ip.startsWith("10.")) 5 else (75..98).random()
            parsedIndicatorsList.add(
                ExtractedIoc(
                    indicator = ip,
                    type = "IP",
                    repScore = score,
                    malwareFamily = if (score > 50) listOf("BruteForce Botnet", "KaosRAT CNC", "DDoS Node").random() else "Clean Local Subnet",
                    country = if (score > 50) listOf("RU", "CN", "KP", "NL").random() else "US"
                )
            )
        }

        val domainsMatch = domainPattern.matcher(text)
        while (domainsMatch.find()) {
            val dom = domainsMatch.group()
            if (dom.lowercase() != "com.local" && dom.lowercase() != "x.bin") {
                val score = if (dom.contains("malicios") || dom.contains("hacker") || dom.endsWith(".ru")) 95 else (10..40).random()
                parsedIndicatorsList.add(
                    ExtractedIoc(
                        indicator = dom,
                        type = "DOMAIN",
                        repScore = score,
                        malwareFamily = if (score > 50) "Phishing C2 Hostname" else "Legitimate DNS Gateway",
                        country = if (score > 50) "RU" else "US"
                    )
                )
            }
        }

        val hashMatch = hashPattern.matcher(text)
        while (hashMatch.find()) {
            val hash = hashMatch.group()
            parsedIndicatorsList.add(
                ExtractedIoc(
                    indicator = hash,
                    type = "HASH",
                    repScore = 99,
                    malwareFamily = listOf("LockBit Payload", "ShadowCrypt Encrypter", "Wiper Injector").random(),
                    country = "Unknown Origin"
                )
            )
        }

        val totalMatches = parsedIndicatorsList.size
        if (totalMatches > 0) {
            Toast.makeText(context, "Success: Extracted $totalMatches threat IOCs!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No matches found. Try entering valid IPs or Hashes.", Toast.LENGTH_SHORT).show()
        }
    }

    // Run automatically on load for first time representation
    LaunchedEffect(Unit) {
        runIocParser(parserInputText)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Main header
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(ErrorRed.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Default.Public, contentDescription = null, tint = ErrorRed, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ADVANCED THREAT INTELLIGENCE FEED",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Aggregated active indicators, active hacker syndicates, and CVE scorecards.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        // Section Split: Left parsed parser IOC utility & Right scrolling syndicates feed
        Row(
            modifier = Modifier.fillMaxWidth().height(480.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left pane: Fast IOC manual extractor parsing tool
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(Color(0xFF0F1110))
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
                        text = "INDICATOR OF COMPROMISE (IOC) PARSER",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = WarningYellow,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "PASTE SYSTEM / AUDIT SEC LOGS:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    TextField(
                        value = parserInputText,
                        onValueChange = { parserInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.1f)
                            .clip(RoundedCornerShape(6.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFF131415),
                            unfocusedContainerColor = Color(0xFF171819),
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary,
                            focusedIndicatorColor = WarningYellow,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontSize = 10.sp),
                        placeholder = { Text("Paste unstructured SSH access logs, email links, checksum outputs containing targets...", fontSize = 10.sp, color = TextSecondary) }
                    )

                    Button(
                        onClick = { runIocParser(parserInputText) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = WarningYellow,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Icon(imageVector = Icons.Default.Troubleshoot, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("EXTRACT & EVALUATE IOCS", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        text = "PARSED TELEMETRY RESULTS [${parsedIndicatorsList.size} IOCS]:",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )

                    // Box containing the parsed lists
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1.3f)
                    ) {
                        if (parsedIndicatorsList.isEmpty()) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("No indicators extracted details yet.", fontSize = 10.sp, color = TextSecondary)
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.spacedBy(5.dp),
                                contentPadding = PaddingValues(bottom = 6.dp)
                            ) {
                                items(parsedIndicatorsList.toList()) { ioc ->
                                    val isMalicious = ioc.repScore >= 70
                                    val tokenColor = if (isMalicious) ErrorRed else SuccessGreen

                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF181A1B))
                                            .border(1.dp, tokenColor.copy(alpha = 0.25f), RoundedCornerShape(4.dp))
                                            .clickable {
                                                clipboard.setText(AnnotatedString(ioc.indicator))
                                                Toast.makeText(context, "Copied IOC: ${ioc.indicator}", Toast.LENGTH_SHORT).show()
                                            }
                                            .padding(6.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(2.dp))
                                                        .background(tokenColor.copy(alpha = 0.15f))
                                                        .padding(horizontal = 4.dp)
                                                ) {
                                                    Text(
                                                        text = ioc.type,
                                                        fontSize = 8.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = tokenColor,
                                                        fontFamily = FontFamily.Monospace
                                                    )
                                                }
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = ioc.indicator,
                                                    fontSize = 11.sp,
                                                    fontWeight = FontWeight.SemiBold,
                                                    color = TextPrimary,
                                                    fontFamily = FontFamily.Monospace,
                                                    maxLines = 1,
                                                    overflow = TextOverflow.Ellipsis
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(2.dp))

                                            Text(
                                                text = "Sign: ${ioc.malwareFamily} | Country: ${ioc.country}",
                                                fontSize = 9.sp,
                                                color = TextSecondary,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }

                                        // Threat score Badge
                                        Column(horizontalAlignment = Alignment.End) {
                                            Text(
                                                text = "${ioc.repScore}/100",
                                                color = tokenColor,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = FontFamily.Monospace
                                            )
                                            Text(
                                                text = if (isMalicious) "HIGH RISK" else "CLEAN",
                                                color = tokenColor,
                                                fontSize = 7.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Right pane: Real-time RSS Intelligence syndicates feed
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .background(Color(0xFF131415))
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
                        text = "SIEM VULNERABILITY ADVISORIES",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacOSBlue,
                        fontFamily = FontFamily.Monospace
                    )
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(intelGroupsFeed) { news ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x0EFFFFFF)),
                            border = BorderStroke(1.dp, GlassBorder),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = news.cve,
                                        fontSize = 10.sp,
                                        color = WarningYellow,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                    
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(3.dp))
                                            .background(
                                                if (news.severity == "CRITICAL") ErrorRed.copy(alpha = 0.2f) else UbuntuOrange.copy(alpha = 0.2f)
                                            )
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = news.severity,
                                            fontSize = 7.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (news.severity == "CRITICAL") ErrorRed else UbuntuOrange
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = news.title,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = news.body,
                                    fontSize = 10.sp,
                                    color = TextSecondary,
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Actor: ${news.group}",
                                        fontSize = 9.sp,
                                        color = MacOSBlue,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )

                                    Text(
                                        text = news.date,
                                        fontSize = 8.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // APT Cyber attack timeline log at the bottom
        GlassCard {
            Text(
                text = "HISTORICAL CYBERSECURITY DISRUPTIONS CHRONOLOGY",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            // Timeline graphic representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1110))
                    .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                TimelineNode(year = "2020", name = "SolarWinds SWIFT", desc = "Sophisticated Supply-chain backdoors bypass network perimeter.")
                TimelineNode(year = "2021", name = "Log4Shell CVE", desc = "Zero-day lookup command injection triggers worldwide scan.")
                TimelineNode(year = "2023", name = "MOVEit Breach", desc = "SQL query injection unlocks transfer buffers for multiple servers.")
            }
        }
    }
}

@Composable
fun RowScope.TimelineNode(year: String, name: String, desc: String) {
    Column(
        modifier = Modifier.weight(1f),
        horizontalAlignment = Alignment.Start
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(3.dp))
                    .background(UbuntuOrange)
                    .padding(horizontal = 4.dp)
            ) {
                Text(text = year, color = TextPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = name, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 10.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = desc, color = TextSecondary, fontSize = 9.sp, lineSize = 11.sp, maxLines = 2, overflow = TextOverflow.Ellipsis)
    }
}

// Custom typography helper
@Composable
fun Text(text: String, color: Color, fontSize: androidx.compose.ui.unit.TextUnit, lineSize: androidx.compose.ui.unit.TextUnit, maxLines: Int, overflow: TextOverflow, modifier: Modifier = Modifier) {
    Text(
        text = text,
        color = color,
        fontSize = fontSize,
        lineHeight = lineSize,
        maxLines = maxLines,
        overflow = overflow,
        modifier = modifier
    )
}
