package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.data.GeminiCopilotService
import kotlinx.coroutines.launch

// Shared code fonts helper
val MonospaceCoder = FontFamily.Monospace

@Composable
fun TerminalSandbox(
    modifier: Modifier = Modifier,
    incomingCommand: String = "",
    onIncomingHandled: () -> Unit = {}
) {
    val context = LocalContext.current
    val haptic = LocalHapticFeedback.current
    val clipboard = LocalClipboardManager.current
    val coroutineScope = rememberCoroutineScope()

    // Terminal session states
    val consoleLog = remember {
        mutableStateListOf<String>().apply {
            add("======================================")
            add("   CYBERPATH TERMINAL SHELL v2.6.5   ")
            add("======================================")
            add("System loaded safely. Status: Operational.")
            add("Type 'help' to verify list of standard utility commands.")
            add("")
        }
    }
    
    // Command input state
    var inputText by remember { mutableStateOf("") }
    
    // Virtual working filesystem directory tracking: "/" (root), "/etc", "/var/log", "/home"
    var currentDirState by remember { mutableStateOf("/") }
    
    // Auto-scroll logic for terminal lines
    val scrollState = rememberScrollState()
    
    // Synchronize clicked incoming commands from library cards
    LaunchedEffect(incomingCommand) {
        if (incomingCommand.isNotEmpty()) {
            inputText = incomingCommand
            onIncomingHandled()
        }
    }
    
    // Trigger scroll downward on layout modification
    LaunchedEffect(consoleLog.size) {
        scrollState.animateScrollTo(scrollState.maxValue.coerceAtLeast(0))
    }

    // Function to parse and run command inputs
    fun processCommand(cmdLine: String) {
        val trimmed = cmdLine.trim()
        if (trimmed.isEmpty()) return
        
        consoleLog.add("guest@cyberpath:${currentDirState}$ $trimmed")
        
        val parts = trimmed.split(" ")
        val mainCmd = parts[0].lowercase()
        val arg = if (parts.size > 1) parts[1] else ""
        
        when (mainCmd) {
            "help" -> {
                consoleLog.add("Available sandboxed commands:")
                consoleLog.add("  help               - Displays this helper guide")
                consoleLog.add("  whoami             - Show current active user identity")
                consoleLog.add("  ls                 - List directories, logs, or keys")
                consoleLog.add("  cd <path>          - Move inside virtual system paths")
                consoleLog.add("  cat <file>         - Display file contents in buffer")
                consoleLog.add("  nmap <host>        - Port-scan active target host systems")
                consoleLog.add("  ping <host>        - Diagnose baseline packet latency replies")
                consoleLog.add("  ufw status         - Check local host firewall tables")
                consoleLog.add("  copilot <query>    - Ask Shah's Live AI SOC Copilot anything!")
                consoleLog.add("  ai <query>         - Alias for 'copilot' command")
                consoleLog.add("  clear              - Reset and clear console history")
            }
            "whoami" -> {
                consoleLog.add("guest-sandbox")
            }
            "ls" -> {
                when (currentDirState) {
                    "/" -> {
                        consoleLog.add("etc/   var/   home/   flag.txt")
                    }
                    "/etc" -> {
                        consoleLog.add("passwd   shadow   hosts   login.defs")
                    }
                    "/var/log" -> {
                        consoleLog.add("auth.log   syslog   nginx/")
                    }
                    "/home" -> {
                        consoleLog.add("analyst/   id_rsa")
                    }
                    else -> {
                        consoleLog.add("(directory empty)")
                    }
                }
            }
            "cd" -> {
                if (arg.isEmpty() || arg == "/" || arg == "~") {
                    currentDirState = "/"
                } else if (arg == "..") {
                    currentDirState = when (currentDirState) {
                        "/etc", "/var", "/home" -> "/"
                        "/var/log" -> "/var"
                        else -> "/"
                    }
                } else {
                    val target = arg.removeSuffix("/")
                    when {
                        currentDirState == "/" && target == "etc" -> currentDirState = "/etc"
                        currentDirState == "/" && target == "var" -> currentDirState = "/var"
                        currentDirState == "/" && target == "home" -> currentDirState = "/home"
                        currentDirState == "/var" && target == "log" -> currentDirState = "/var/log"
                        else -> {
                            consoleLog.add("cd: no such file or directory: $arg")
                        }
                    }
                }
            }
            "cat" -> {
                if (arg.isEmpty()) {
                    consoleLog.add("cat: missing operand file tag")
                } else {
                    val target = arg.removePrefix("/etc/").removePrefix("/var/log/").removePrefix("/home/").removePrefix("/")
                    
                    // Route files based on name + current directory matching
                    when {
                        target == "passwd" && (currentDirState == "/etc" || trimmed.contains("/etc/passwd")) -> {
                            consoleLog.add("root:x:0:0:root:/root:/bin/bash")
                            consoleLog.add("daemon:x:1:1:daemon:/usr/sbin:/usr/sbin/nologin")
                            consoleLog.add("analyst:x:1000:1000:security-analyst:/home/analyst:/bin/bash")
                            consoleLog.add("guest:x:1001:1001:guest-sandbox:/bin/sh")
                        }
                        target == "shadow" && (currentDirState == "/etc" || trimmed.contains("/etc/shadow")) -> {
                            consoleLog.add("root:\$6\$SAlt\$RogueHashPassword1234::0:99999:7:::")
                            consoleLog.add("analyst:\$6\$pAsSWdHash\$4321abcd::0:99999:7:::")
                        }
                        target == "flag.txt" && (currentDirState == "/" || trimmed.contains("flag.txt")) -> {
                            consoleLog.add("CYBERPATH_ACADEMY{CONGRATS_TERMINAL_CHAMP_2026} 🏆")
                        }
                        target == "auth.log" && (currentDirState == "/var/log" || trimmed.contains("auth.log")) -> {
                            consoleLog.add("2026-05-24T21:05:01Z cyberpath sshd[512]: Failed password for invalid user admin from 192.168.1.18 port 49122 ssh2")
                            consoleLog.add("2026-05-24T21:05:05Z cyberpath sshd[512]: Failed password for invalid user admin from 192.168.1.18 port 49122 ssh2")
                            consoleLog.add("2026-05-24T21:05:09Z cyberpath sshd[512]: Failed password for invalid user root from 192.168.1.18 port 49124 ssh2")
                            consoleLog.add("2026-05-24T21:07:11Z cyberpath sudo: guest : TTY=pts/0 ; PWD=/home/guest ; USER=root ; COMMAND=/usr/bin/apt-get upgrade")
                        }
                        target == "syslog" && (currentDirState == "/var/log" || trimmed.contains("syslog")) -> {
                            consoleLog.add("2026-05-24T21:00:10Z cyberpath kernel: [   0.000000] Booting Linux kernel on physical CPU 0x0")
                            consoleLog.add("2026-05-24T21:01:25Z cyberpath ufw: [UFW BLOCK] IN=eth0 OUT= MAC=01:00:5e:00:00:fc SRC=192.168.1.1 DST=224.0.0.252 LEN=60")
                            consoleLog.add("2026-05-24T21:12:44Z cyberpath cron[101]: (CRON) info (running /usr/sbin/logrotate)")
                        }
                        target == "id_rsa" && (currentDirState == "/home" || trimmed.contains("id_rsa")) -> {
                            consoleLog.add("-----BEGIN OPENSSH PRIVATE KEY-----")
                            consoleLog.add("b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtcn")
                            consoleLog.add("NhAAAAAwEAAQAAAQEA4PZ+h6gP3G/9Fv2pZf1gU2wJkWQ0Xo9mR6CpxoW3k8JNDV8VvFmU")
                            consoleLog.add("-----END OPENSSH PRIVATE KEY-----")
                        }
                        else -> {
                            consoleLog.add("cat: $arg: No such file or directory in this scope path")
                        }
                    }
                }
            }
            "nmap" -> {
                if (arg.isEmpty()) {
                    consoleLog.add("nmap: missing host parameter. E.g. nmap localhost")
                } else {
                    consoleLog.add("Starting Nmap 7.80 ( https://nmap.org ) at 2026-05-24 UTC")
                    consoleLog.add("Nmap scan report for $arg (127.0.0.1)")
                    consoleLog.add("Host is up (0.00052s latency).")
                    consoleLog.add("")
                    consoleLog.add("PORT     STATE SERVICE")
                    consoleLog.add("22/tcp   open  ssh (Vulnerable Version: OpenSSH 7.2)")
                    consoleLog.add("80/tcp   open  http")
                    consoleLog.add("443/tcp  open  https (TLS 1.3 enforced)")
                    consoleLog.add("3389/tcp open  ms-wbt-server (RDP - Active)")
                    consoleLog.add("")
                    consoleLog.add("Nmap done: 1 IP address (1 host up) scanned in 0.14 seconds")
                }
            }
            "ping" -> {
                if (arg.isEmpty()) {
                    consoleLog.add("ping: missing destination address")
                } else {
                    consoleLog.add("PING $arg ($arg) 56(84) bytes of data.")
                    consoleLog.add("64 bytes from $arg: icmp_seq=1 ttl=115 time=12.4 ms")
                    consoleLog.add("64 bytes from $arg: icmp_seq=2 ttl=115 time=14.1 ms")
                    consoleLog.add("64 bytes from $arg: icmp_seq=3 ttl=115 time=11.9 ms")
                    consoleLog.add("")
                    consoleLog.add("--- $arg ping statistics ---")
                    consoleLog.add("3 packets transmitted, 3 received, 0% packet loss, time 2002ms")
                    consoleLog.add("rtt min/avg/max/mdev = 11.9/12.8/14.1/0.957 ms")
                }
            }
            "ufw" -> {
                if (arg == "status") {
                    consoleLog.add("Status: active")
                    consoleLog.add("")
                    consoleLog.add("To                         Action      From")
                    consoleLog.add("--                         ------      ----")
                    consoleLog.add("22/tcp                     ALLOW       Anywhere                  ")
                    consoleLog.add("80/tcp                     ALLOW       Anywhere                  ")
                    consoleLog.add("443/tcp                    ALLOW       Anywhere                  ")
                } else {
                    consoleLog.add("Usage: ufw status")
                }
            }
            "copilot", "ai" -> {
                val query = if (parts.size > 1) trimmed.substringAfter(parts[0]).trim() else ""
                if (query.isEmpty()) {
                    consoleLog.add("Usage: $mainCmd <ask any cybersecurity / lab question here>")
                } else {
                    consoleLog.add("⚡ [Copilot] Querying Gemini AI...")
                    coroutineScope.launch {
                        val response = GeminiCopilotService.generateResponse(
                            prompt = query,
                            systemInstruction = "You are Shah's SOC Path AI Copilot, a helpful, brilliant Senior SOC Analyst guiding a Security Operations Center student. Answer custom cyber security questions, write standard Linux terminal guides, help explain syslog or auth.log records, and solve CTF tasks. Keep answers beautifully formatted, clear, educational, and relatively concise (not overly verbose) to look clean in terminal displays."
                        )
                        response.split("\n").forEach { line ->
                            consoleLog.add(line)
                        }
                    }
                }
            }
            "clear" -> {
                consoleLog.clear()
            }
            else -> {
                consoleLog.add("bash: command not found: $mainCmd")
                consoleLog.add("Type 'help' to review valid operations for this sandbox.")
            }
        }
        consoleLog.add("")
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, GlassBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = MacOSDarkSurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar macOS style
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF252526))
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // OS window operations bullets
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(modifier = Modifier.size(11.dp).clip(RoundedCornerShape(6.dp)).background(TrafficLightRed))
                    Box(modifier = Modifier.size(11.dp).clip(RoundedCornerShape(6.dp)).background(TrafficLightYellow))
                    Box(modifier = Modifier.size(11.dp).clip(RoundedCornerShape(6.dp)).background(TrafficLightGreen))
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "cyberpath@sandbox-tty1: ~${currentDirState}",
                    color = TextSecondary,
                    fontFamily = MonospaceCoder,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Copy Console text utility
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy text buffer",
                    tint = TextSecondary,
                    modifier = Modifier
                        .size(14.dp)
                        .clickable {
                            val fullString = consoleLog.joinToString("\n")
                            clipboard.setText(AnnotatedString(fullString))
                            Toast.makeText(context, "Terminal logs copied to clipboard!", Toast.LENGTH_SHORT).show()
                            haptic.performHapticFeedback(androidx.compose.ui.hapticfeedback.HapticFeedbackType.LongPress)
                        }
                )
            }

            // Console output logs scroll layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(Color(0xFF0D0D10))
                    .padding(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    consoleLog.forEach { line ->
                        Text(
                            text = line,
                            color = when {
                                line.startsWith("guest@cyberpath:") -> SuccessGreen
                                line.contains("CYBERPATH_ACADEMY") -> WarningYellow
                                line.startsWith("bash: ") -> ErrorRed
                                else -> TextPrimary
                            },
                            fontFamily = MonospaceCoder,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
            
            HorizontalDivider(color = GlassBorder)

            // Dynamic bottom input layer
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141416))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "guest@cyberpath:${currentDirState}$ ",
                    color = SuccessGreen,
                    fontFamily = MonospaceCoder,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = "type 'help' or commands here...",
                            color = TextSecondary.copy(alpha = 0.5f),
                            fontFamily = MonospaceCoder,
                            fontSize = 11.sp
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = MonospaceCoder,
                        fontSize = 11.sp
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            if (inputText.isNotEmpty()) {
                                processCommand(inputText)
                                inputText = ""
                            }
                        }
                    )
                )

                // Run firing button
                IconButton(
                    onClick = {
                        if (inputText.isNotEmpty()) {
                            processCommand(inputText)
                            inputText = ""
                        }
                    },
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Execute commands",
                        tint = SuccessGreen,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
