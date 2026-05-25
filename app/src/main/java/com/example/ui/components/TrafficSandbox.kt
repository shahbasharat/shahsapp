package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlin.random.Random

// Packet Data Structure
data class PacketModel(
    val id: Int,
    val timestamp: String,
    val proto: String,
    val srcIp: String,
    val dstIp: String,
    val length: Int,
    val srcPort: Int,
    val dstPort: Int,
    val flags: String,
    val info: String,
    val payload: String,
    val threatLevel: String, // "CLEAN", "WARNING", "MALICIOUS"
    val threatDetails: String,
    var isBlocked: Boolean = false
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TrafficSandbox(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val clipboard = LocalClipboardManager.current
    
    // Core states
    var isCapturing by remember { mutableStateOf(true) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedProtocolFilter by remember { mutableStateOf("ALL") }
    var selectedPacketId by remember { mutableStateOf<Int?>(1) }
    
    // Registered firewall blocked IPs list
    val blockedIps = remember { mutableStateListOf<String>("192.168.1.199") }
    
    // Persistent master list of simulated packets (begins with some historic data)
    val masterPackets = remember {
        mutableStateListOf<PacketModel>(
            PacketModel(1, "21:54:02", "TCP", "10.0.0.12", "192.168.1.52", 64, 51322, 443, "SYN", "Client Handshake Request [SYN]", "00 1E 0F C3 4A 2B 49 DA ...", "CLEAN", "Standard secure HTTPS TLS client connection negotiation."),
            PacketModel(2, "21:54:03", "TCP", "192.168.1.52", "10.0.0.12", 60, 443, 51322, "SYN-ACK", "Server Handshake Respond [SYN, ACK]", "52 4A 1B DA EF C2 33 AA ...", "CLEAN", "Standard handshake confirmation from hosting network gateway."),
            PacketModel(3, "21:54:08", "DNS", "192.168.1.15", "8.8.8.8", 78, 53, 53, "QR", "Query lookup: mybank.com.local", "00 01 A1 80 00 01 00 00 ...", "WARNING", "Local dynamic DNS resolution requested for external mapping."),
            PacketModel(4, "21:54:12", "UDP", "192.168.1.144", "185.220.101.4", 1024, 6092, 1080, "NONE", "Syslog dynamic telemetry transfer payload", "2E 55 4E 49 4F 4E 20 53 ...", "MALICIOUS", "Packet payload signature matches high-risk script injection or command execution structure."),
            PacketModel(5, "21:54:15", "ICMP", "203.0.113.15", "192.168.1.1", 32, 0, 0, "ECHO", "Ping Request (ICMP Keepalive)", "08 00 F8 3B 00 01 00 02 ...", "CLEAN", "Normal ICMP utility routing echo diagnostic."),
            PacketModel(6, "21:54:19", "TCP", "198.51.100.82", "192.168.1.20", 1204, 80, 80, "PSH-ACK", "POST /admin/api/upload.php HTTP/1.1", "55 4e 49 4f 4e 20 53 45 ...", "MALICIOUS", "Payload contains suspect path traversal syntax 'UNION SELECT'. Security threshold breached.")
        )
    }

    // Interactive Packet Generator Coroutine Loop
    LaunchedEffect(isCapturing) {
        if (isCapturing) {
            val randomIps = listOf(
                "192.168.1.45", "10.0.0.8", "72.14.213.10", "185.190.21.3", 
                "192.168.1.199", "10.20.14.88", "8.8.4.4", "198.51.100.9"
            )
            val destinations = listOf("192.168.1.1", "192.168.1.20", "10.0.0.12", "172.16.8.5")
            val protocols = listOf("TCP", "UDP", "ICMP", "DNS")
            
            var packetCounter = (masterPackets.maxOfOrNull { it.id } ?: 0) + 1
            while (true) {
                delay(3000) // Emit dynamic telemetry every 3 seconds
                
                val proto = protocols.random()
                val src = randomIps.random()
                val dst = destinations.random()
                val isBlocked = blockedIps.contains(src)
                
                // Determine malicious behaviors
                val threatRand = Random.nextFloat()
                val (threat, details, info, payload) = when {
                    isBlocked -> Quadruple(
                        "MALICIOUS", 
                        "BLOCKED packet trigger! IP restricted on host security config.", 
                        "DENIED: Connection dropped by local Firewall policy.",
                        "FF FF FF FF FF FF FF FF FF FF FF FF"
                    )
                    threatRand < 0.20f -> Quadruple(
                        "MALICIOUS", 
                        "Alert! SQL Injection attempt injection trigger detected: ' OR '1'='1' payload.", 
                        "POST /login.php [SUSPECT SQL INJECTION]",
                        "27 20 4F 52 20 31 3D 31 20 2D 2D A3"
                    )
                    threatRand < 0.40f -> Quadruple(
                        "WARNING", 
                        "Port scanner diagnostic suspected: Rapid port allocation query.", 
                        "TCP SYN Scanner Probe on port 8080",
                        "00 00 00 00 00 00 00 00 00 00 00 00"
                    )
                    else -> Quadruple(
                        "CLEAN", 
                        "Standard diagnostic system metric socket write.", 
                        if (proto == "TCP") "TCP Handshake segment [ACK]" else if (proto == "DNS") "DNS Standard query reply A" else "$proto Packet transmission",
                        "AB CD EF 12 34 56 78 90 FE DC BA 98"
                    )
                }

                val hours = Random.nextInt(12, 23).toString().padStart(2, '0')
                val mins = Random.nextInt(10, 59).toString().padStart(2, '0')
                val secs = Random.nextInt(10, 59).toString().padStart(2, '0')
                
                val newPacket = PacketModel(
                    id = packetCounter++,
                    timestamp = "$hours:$mins:$secs",
                    proto = proto,
                    srcIp = src,
                    dstIp = dst,
                    length = Random.nextInt(40, 1500),
                    srcPort = Random.nextInt(1024, 65535),
                    dstPort = if (proto == "DNS") 53 else listOf(80, 443, 22, 8080).random(),
                    flags = if (proto == "TCP") "ACK" else "NONE",
                    info = info,
                    payload = payload,
                    threatLevel = threat,
                    threatDetails = details,
                    isBlocked = isBlocked
                )
                
                masterPackets.add(0, newPacket) // Add to the top of list
                
                // Cap list size
                if (masterPackets.size > 100) {
                    masterPackets.removeAt(masterPackets.size - 1)
                }
            }
        }
    }

    // Filter logic
    val filteredPackets = masterPackets.filter { p ->
        val matchesProto = selectedProtocolFilter == "ALL" || p.proto.equals(selectedProtocolFilter, ignoreCase = true)
        val matchesSearch = searchQuery.isEmpty() || 
                p.srcIp.contains(searchQuery, ignoreCase = true) || 
                p.dstIp.contains(searchQuery, ignoreCase = true) || 
                p.proto.contains(searchQuery, ignoreCase = true) || 
                p.info.contains(searchQuery, ignoreCase = true)
        matchesProto && matchesSearch
    }

    val selectedPacketMap = masterPackets.find { it.id == selectedPacketId }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top status and controls panel
        GlassCard {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(10.dp),
                            shape = CircleShape,
                            color = if (isCapturing) SuccessGreen else TextSecondary
                        ) {}
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ETH0: LIVE PACKET SNIFFER",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                    Text(
                        text = if (isCapturing) "Status: Sniffing packet frames ... (${masterPackets.size} total captured)" else "Status: Live Capture Paused",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            isCapturing = !isCapturing
                            Toast.makeText(context, if (isCapturing) "Toggled Eth0 Packet Capture!" else "Sniffer Capture Paused.", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCapturing) WarningYellow else SuccessGreen,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = if (isCapturing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Sniff Toggle",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isCapturing) "PAUSE" else "CAPTURE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            masterPackets.clear()
                            selectedPacketId = null
                            Toast.makeText(context, "Cleaned Sniffer Buffers!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = TextPrimary),
                        border = BorderStroke(1.dp, GlassBorder)
                    ) {
                        Icon(imageVector = Icons.Default.DeleteSweep, contentDescription = "Clear", modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("CLEAR", fontSize = 11.sp)
                    }
                }
            }
        }

        // Graphical Packets Pathway Network Canvas
        GlassCard {
            Text(
                text = "NETWORK TOPOLOGY & LIVE TRANSMISSION PATHWAYS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            // Custom topology paint sandbox
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(Color(0xFF0F1110))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                // Interactive dynamic pulse indicator parameters
                val animationProgress = rememberInfiniteTransition().animateFloat(
                    initialValue = 0f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(2200, easing = LinearEasing),
                        repeatMode = RepeatMode.Restart
                    )
                )

                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height
                    
                    val hostX = width * 0.15f
                    val firewallX = width * 0.50f
                    val serverX = width * 0.85f
                    val centerY = height * 0.50f

                    // Draw connecting cables/buses
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(hostX, centerY),
                        end = Offset(firewallX, centerY),
                        strokeWidth = 3f
                    )
                    drawLine(
                        color = Color.White.copy(alpha = 0.2f),
                        start = Offset(firewallX, centerY),
                        end = Offset(serverX, centerY),
                        strokeWidth = 3f
                    )

                    // Pulse packets moving along line
                    val progress = animationProgress.value
                    if (isCapturing) {
                        val pulseX1 = hostX + (firewallX - hostX) * progress
                        drawCircle(
                            color = UbuntuOrange,
                            radius = 6f,
                            center = Offset(pulseX1, centerY)
                        )
                        val pulseX2 = firewallX + (serverX - firewallX) * progress
                        drawCircle(
                            color = MacOSBlue,
                            radius = 6f,
                            center = Offset(pulseX2, centerY)
                        )
                    }

                    // Host Circle Node
                    drawCircle(
                        color = Color(0xFF1C1C1E),
                        radius = 20f,
                        center = Offset(hostX, centerY)
                    )
                    drawCircle(
                        color = MacOSBlue,
                        radius = 20f,
                        center = Offset(hostX, centerY),
                        style = Stroke(width = 2f)
                    )

                    // Firewall Node
                    drawCircle(
                        color = Color(0xFF1C1C1E),
                        radius = 22f,
                        center = Offset(firewallX, centerY)
                    )
                    drawCircle(
                        color = WarningYellow,
                        radius = 22f,
                        center = Offset(firewallX, centerY),
                        style = Stroke(width = 2f)
                    )

                    // Main server Node
                    drawCircle(
                        color = Color(0xFF1C1C1E),
                        radius = 20f,
                        center = Offset(serverX, centerY)
                    )
                    drawCircle(
                        color = SuccessGreen,
                        radius = 20f,
                        center = Offset(serverX, centerY),
                        style = Stroke(width = 2f)
                    )
                }

                // Node text labels
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Computer, contentDescription = null, tint = MacOSBlue, modifier = Modifier.size(16.dp))
                        Text("INTERNAL LAN", fontSize = 8.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        Text("10.0.0.12", fontSize = 7.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.offset(x = (-4).dp)) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = WarningYellow, modifier = Modifier.size(16.dp))
                        Text("IPS FIREWALL", fontSize = 8.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        Text("Active Filtering", fontSize = 7.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(imageVector = Icons.Default.Dns, contentDescription = null, tint = SuccessGreen, modifier = Modifier.size(16.dp))
                        Text("DMZ SERVER", fontSize = 8.sp, color = TextPrimary, fontFamily = FontFamily.Monospace)
                        Text("192.168.1.20", fontSize = 7.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
                    }
                }
            }
        }

        // Search and protocol toggles
        GlassCard {
            TextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search by IP, Protocol, Info command...", fontSize = 13.sp, color = TextSecondary) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color(0x32000000),
                    unfocusedContainerColor = Color(0x18000000),
                    focusedIndicatorColor = UbuntuOrange,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Protocol chips list Row
            Text(
                text = "FILTER PROTOCOL PROFILE:",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 6.dp)
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                listOf("ALL", "TCP", "UDP", "ICMP", "DNS").forEach { proto ->
                    val isSelected = selectedProtocolFilter == proto
                    val color = when (proto) {
                        "TCP" -> MacOSBlue
                        "UDP" -> UbuntuOrange
                        "ICMP" -> WarningYellow
                        "DNS" -> SuccessGreen
                        else -> TextPrimary
                    }
                    
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) color.copy(alpha = 0.25f) else CardGlassSurface)
                            .border(1.dp, if (isSelected) color else GlassBorder, RoundedCornerShape(6.dp))
                            .clickable { selectedProtocolFilter = proto }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = proto,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) color else TextPrimary
                        )
                    }
                }
            }
        }

        // Layout Split: Packet List & Packet Inspector Detail Panel
        Row(
            modifier = Modifier.fillMaxWidth().height(480.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left pane: Packet Feed scroll list
            Column(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .background(Color(0x0CFFFFFF))
                    .border(1.dp, GlassBorder, RoundedCornerShape(10.dp))
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF252627))
                        .padding(horizontal = 10.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "CAP CHRONO-FEED [${filteredPackets.size} displayed]",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (filteredPackets.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(imageVector = Icons.Default.CloudQueue, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(32.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No packets match filter", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                } else {
                    LazyColumn(
                        state = rememberLazyListState(),
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 12.dp)
                    ) {
                        items(filteredPackets, key = { it.id }) { packet ->
                            val isSelected = selectedPacketId == packet.id
                            val protoColor = when (packet.proto) {
                                "TCP" -> MacOSBlue
                                "UDP" -> UbuntuOrange
                                "ICMP" -> WarningYellow
                                "DNS" -> SuccessGreen
                                else -> TextPrimary
                            }
                            
                            val threatColor = when (packet.threatLevel) {
                                "MALICIOUS" -> ErrorRed
                                "WARNING" -> WarningYellow
                                else -> Color.Transparent
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isSelected) Color(0x22FFFFFF) else Color.Transparent)
                                    .border(
                                        width = if (threatColor != Color.Transparent) 1.dp else 0.dp,
                                        color = if (threatColor != Color.Transparent) threatColor.copy(alpha = 0.4f) else Color.Transparent
                                    )
                                    .clickable { selectedPacketId = packet.id }
                                    .padding(horizontal = 8.dp, vertical = 7.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Protocol label Tag
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(protoColor.copy(alpha = 0.15f))
                                                .padding(horizontal = 4.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = packet.proto,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = protoColor,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "${packet.srcIp} -> ${packet.dstIp}",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = TextPrimary,
                                            fontFamily = FontFamily.Monospace,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                    
                                    // Timestamp
                                    Text(
                                        text = packet.timestamp,
                                        fontSize = 9.sp,
                                        color = TextSecondary,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = packet.info,
                                        fontSize = 10.sp,
                                        color = if (packet.threatLevel == "MALICIOUS") ErrorRed else TextSecondary,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.weight(1f)
                                    )
                                    
                                    if (packet.threatLevel == "MALICIOUS") {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(3.dp))
                                                .background(ErrorRed)
                                                .padding(horizontal = 4.dp)
                                        ) {
                                            Text("SPOOFED/CVE", fontSize = 8.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                HorizontalDivider(color = GlassBorder, modifier = Modifier.padding(top = 7.dp))
                            }
                        }
                    }
                }
            }

            // Right pane: Deep Packet Inspector Box
            Column(
                modifier = Modifier
                    .weight(1f)
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
                        text = "DEEP PACKET INSPECTOR (HEX)",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MacOSBlue,
                        fontFamily = FontFamily.Monospace
                    )
                }

                if (selectedPacketMap == null) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                            Icon(imageVector = Icons.Default.Search, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(36.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("Select a packet on left timeline to analyze headers and raw payload telemetry", fontSize = 11.sp, textAlign = TextAlign.Center, color = TextSecondary)
                        }
                    }
                } else {
                    // Packet Detail Body Content
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(10.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Section Header Details
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Packet ID: #${selectedPacketMap.id}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp, fontFamily = FontFamily.Monospace)
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(
                                        when (selectedPacketMap.threatLevel) {
                                            "MALICIOUS" -> ErrorRed.copy(alpha = 0.2f)
                                            "WARNING" -> WarningYellow.copy(alpha = 0.2f)
                                            else -> SuccessGreen.copy(alpha = 0.2f)
                                        }
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = selectedPacketMap.threatLevel,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = when (selectedPacketMap.threatLevel) {
                                        "MALICIOUS" -> ErrorRed
                                        "WARNING" -> WarningYellow
                                        else -> SuccessGreen
                                    }
                                )
                            }
                        }

                        // Threat analysis box
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0x18FFFFFF)),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text("IDS RISK ANALYSIS SCORECASE:", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = selectedPacketMap.threatDetails,
                                    fontSize = 10.sp,
                                    color = TextPrimary
                                )
                            }
                        }

                        // Structured parsed headers values
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            HeaderRow(label = "Source Socket:", value = "${selectedPacketMap.srcIp}:${selectedPacketMap.srcPort}")
                            HeaderRow(label = "Dest Socket:", value = "${selectedPacketMap.dstIp}:${selectedPacketMap.dstPort}")
                            HeaderRow(label = "Frame Sizes:", value = "${selectedPacketMap.length} bytes")
                            HeaderRow(label = "Segment Cls:", value = selectedPacketMap.flags)
                        }

                        Spacer(modifier = Modifier.height(2.dp))

                        // Programmable Hex Dump Display (Highly realistic SOC emulator)
                        Text(
                            text = "HEXADECIMAL TELEMETRY DUMP:",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextSecondary
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.Black)
                                .border(1.dp, GlassBorder, RoundedCornerShape(4.dp))
                                .padding(6.dp)
                        ) {
                            val hexRepresentation = remember(selectedPacketMap.payload) {
                                buildString {
                                    val safePayloadBytes = selectedPacketMap.payload.split(" ")
                                    append("0000  ")
                                    safePayloadBytes.forEachIndexed { i, byte ->
                                        if (i > 0 && i % 8 == 0) append(" ")
                                        append("$byte ")
                                    }
                                    append(" .parsed_raw")
                                }
                            }
                            Text(
                                text = hexRepresentation,
                                color = SuccessGreen,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        // Firewall Block Actions rule toggle
                        val isSourceBlocked = blockedIps.contains(selectedPacketMap.srcIp)
                        Button(
                            onClick = {
                                if (isSourceBlocked) {
                                    blockedIps.remove(selectedPacketMap.srcIp)
                                    Toast.makeText(context, "${selectedPacketMap.srcIp} removed from firewall blocklist!", Toast.LENGTH_SHORT).show()
                                } else {
                                    blockedIps.add(selectedPacketMap.srcIp)
                                    Toast.makeText(context, "${selectedPacketMap.srcIp} successfully blocked at Gateway!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSourceBlocked) SuccessGreen else ErrorRed,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.fillMaxWidth().height(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isSourceBlocked) Icons.Default.CheckCircle else Icons.Default.Block,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSourceBlocked) "UNBLOCK SOURCE IP" else "BLOCK SOURCE IP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun HeaderRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 10.sp, color = TextSecondary, fontFamily = FontFamily.Monospace)
        Text(text = value, fontSize = 10.sp, color = TextPrimary, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
    }
}

// Private helper quadruple structure class
data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)
