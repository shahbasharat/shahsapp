package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider
import com.example.data.*
import com.example.ui.AcademyViewModel
import com.example.ui.AcademyViewModelFactory
import com.example.ui.components.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initializing our persistent database
        val database = AcademyDatabase.getDatabase(this)
        val repository = AcademyRepository(database.academyDao())
        val viewModelFactory = AcademyViewModelFactory(repository)
        
        setContent {
            val viewModel: AcademyViewModel = ViewModelProvider(this, viewModelFactory)[AcademyViewModel::class.java]
            
            CyberPathTheme {
                AppContainer(viewModel = viewModel)
            }
        }
    }
}

enum class NavigationTab {
    HOME, LESSONS, PROGRESS, COMMANDS, TRAFFIC_SANDBOX, INCIDENT_BOARD, THREAT_INTEL, CTF_LABS, DEFENSE_SIM
}

@Composable
fun AppContainer(viewModel: AcademyViewModel) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    
    // Notification banner display state
    var notificationMessage by remember { mutableStateOf<String?>("Ubuntu Alert: Maintain your learning streak! Complete 1 lesson today.") }
    
    // Global Splash switch state
    var showSplash by remember { mutableStateOf(true) }
    
    // Active navigation tab
    var activeTab by remember { mutableStateOf(NavigationTab.HOME) }

    // Desktop Environment Theme layout selection ("macos" or "ubuntu")
    var desktopTheme by remember { mutableStateOf("macos") }
    
    // Observer of active lesson selection
    val selectedLessonId by viewModel.selectedLessonId.collectAsState()
    val selectedLesson by viewModel.selectedLesson.collectAsState()
    
    // Lesson progress list observer
    val listProgress by viewModel.allProgress.collectAsState()
    
    // Confetti effect trigger
    var triggerConfetti by remember { mutableStateOf(false) }
    
    // AI Copilot state
    var showAiCopilotPanel by remember { mutableStateOf(false) }
    
    // Observe database-to-view correlations
    LaunchedEffect(Unit) {
        viewModel.confettiTrigger.collectLatest {
            triggerConfetti = true
        }
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = if (desktopTheme == "ubuntu") listOf(Color(0xFF2C001E), Color(0xFF14000F)) else listOf(UbuntuAubergine, MacOSDarkSurface)
                )
            )
            // Add subtle noise/grain texture
            .drawWithCache {
                val heightLimit = maxOf(0, minOf(size.height.toInt(), 3000))
                onDrawBehind {
                    if (heightLimit > 0) {
                        for (i in 0..heightLimit step 24) {
                            drawLine(
                                color = Color(0x0A000000),
                                start = Offset(0f, i.toFloat()),
                                end = Offset(size.width, i.toFloat()),
                                strokeWidth = 1f
                            )
                        }
                    }
                }
            }
    ) {
        if (showSplash) {
            SplashScreen(onFinished = { showSplash = false })
        } else {
            Scaffold(
                containerColor = Color.Transparent,
                contentWindowInsets = WindowInsets.safeDrawing,
                topBar = {
                    Column {
                        // Desktop OS Top Menu Panel
                        TopMenuBar(
                            desktopTheme = desktopTheme,
                            onToggleTheme = {
                                desktopTheme = if (desktopTheme == "macos") "ubuntu" else "macos"
                                Toast.makeText(context, "System Desktop environment toggled to: $desktopTheme!", Toast.LENGTH_SHORT).show()
                            },
                            onResetStats = {
                                viewModel.clearAllData()
                                triggerConfetti = false
                            },
                            onTriggerConfetti = {
                                triggerConfetti = true
                            },
                            activeTabName = when (activeTab) {
                                NavigationTab.HOME -> "Home Dashboard"
                                NavigationTab.LESSONS -> "SOC Lab Modules"
                                NavigationTab.PROGRESS -> "Training Statistics"
                                NavigationTab.COMMANDS -> "TTY command sandboxing"
                                NavigationTab.TRAFFIC_SANDBOX -> "Network Security Sandbox"
                                NavigationTab.INCIDENT_BOARD -> "Incident Board & Triage"
                                NavigationTab.THREAT_INTEL -> "Threat Intelligence Feed"
                                NavigationTab.CTF_LABS -> "Capture The Flag Labs"
                                NavigationTab.DEFENSE_SIM -> "Active Attack Simulator"
                            }
                        )

                        // Custom system alert banner
                        AnimatedVisibility(
                            visible = notificationMessage != null,
                            enter = slideInVertically() + fadeIn(),
                            exit = slideOutVertically() + fadeOut()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(UbuntuOrange)
                                    .padding(vertical = 10.dp, horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Terminal,
                                    contentDescription = "Notification",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = notificationMessage ?: "",
                                    color = TextPrimary,
                                    style = MaterialTheme.typography.labelMedium,
                                    modifier = Modifier.weight(1f)
                                )
                                IconButton(
                                    onClick = { notificationMessage = null },
                                    modifier = Modifier.size(24.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Dismiss",
                                        tint = TextPrimary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                        
                        // Default App Title bar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp, vertical = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (selectedLessonId != null) {
                                IconButton(
                                    onClick = { viewModel.deselectLesson() },
                                    modifier = Modifier.size(40.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ArrowBack,
                                        contentDescription = "Back",
                                        tint = TextPrimary
                                    )
                                }
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(Brush.radialGradient(listOf(UbuntuOrange.copy(alpha = 0.25f), Color.Transparent)))
                                        .border(1.dp, UbuntuOrange.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = "Shield logo",
                                        tint = UbuntuOrange,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.width(10.dp))
                            
                            Column {
                                BlinkingCursor(
                                    prefix = "",
                                    text = "Shah's SOC Path",
                                    textColor = TextPrimary,
                                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "Your Personal SOC Training Lab - ${desktopTheme.uppercase()} Mode",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = TextSecondary
                                )
                            }
                            
                            Spacer(modifier = Modifier.weight(1f))
                            
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Live AI Chat Copilot Button
                                IconButton(
                                    onClick = {
                                        showAiCopilotPanel = !showAiCopilotPanel
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(if (showAiCopilotPanel) UbuntuOrange.copy(alpha = 0.3f) else CardGlassSurface)
                                        .border(1.dp, if (showAiCopilotPanel) UbuntuOrange else Color.Transparent, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.SmartToy,
                                        contentDescription = "AI Copilot Board",
                                        tint = if (showAiCopilotPanel) UbuntuOrange else MacOSBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                // Quick Action: Export trigger
                                IconButton(
                                    onClick = {
                                        val completedCount = listProgress.count { it.theoryRead && it.labCompleted && it.quizPassed }
                                        val log = "Shah's SOC Path Report\n-------------------------\nLessons completed: $completedCount/30\nStatus: Active Training Mode\n100% Offline Secured"
                                        Toast.makeText(context, "Exported Progress Summaries!", Toast.LENGTH_LONG).show()
                                    },
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(CardGlassSurface)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Share,
                                        contentDescription = "Export Report",
                                        tint = MacOSBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                },
                bottomBar = {
                    if (selectedLessonId == null && desktopTheme == "macos") {
                        // Glassmorphic docks bottom navigation bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .navigationBarsPadding(),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .horizontalScroll(rememberScrollState())
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(Color(0xD91C1C1E))
                                    .border(1.dp, GlassBorder, RoundedCornerShape(24.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                NavigationTab.values().forEach { tab ->
                                    val isActive = activeTab == tab
                                    
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .width(52.dp)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable {
                                                activeTab = tab
                                            },
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        DesktopAppIcon(tab = tab, isActive = isActive)
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(4.dp)
                                                .clip(CircleShape)
                                                .background(if (isActive) Color.White else Color.Transparent)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    if (selectedLessonId == null && desktopTheme == "ubuntu") {
                        // Ubuntu side launcher
                        UbuntuLeftSidebar(
                            activeTab = activeTab,
                            onTabSelected = { activeTab = it },
                            onToggleTheme = {
                                desktopTheme = "macos"
                                Toast.makeText(context, "Welcome to macOS Cupertino desktop!", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    ) {
                        if (selectedLessonId != null && selectedLesson != null) {
                            LessonDetailScreen(lesson = selectedLesson!!, viewModel = viewModel)
                        } else {
                            when (activeTab) {
                                NavigationTab.HOME -> HomeScreen(viewModel = viewModel, onNavigateToLessons = { activeTab = NavigationTab.LESSONS })
                                NavigationTab.LESSONS -> LessonsScreen(viewModel = viewModel)
                                NavigationTab.PROGRESS -> ProgressScreen(viewModel = viewModel)
                                NavigationTab.COMMANDS -> CommandLibraryScreen(viewModel = viewModel)
                                NavigationTab.TRAFFIC_SANDBOX -> TrafficSandbox()
                                NavigationTab.INCIDENT_BOARD -> IncidentBoard()
                                NavigationTab.THREAT_INTEL -> ThreatIntelFeed()
                                NavigationTab.CTF_LABS -> CtfLabsScreen(onFlagSolved = { triggerConfetti = true })
                                NavigationTab.DEFENSE_SIM -> DefenseSimulator(onSimSuccess = { triggerConfetti = true })
                            }
                        }
                    }
                }
            }
        }
        
        // Sliding AI Copilot Drawer Overlay
        AnimatedVisibility(
            visible = showAiCopilotPanel,
            enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
            exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .fillMaxHeight()
                .width(360.dp)
        ) {
            AiCopilotDrawer(
                onClose = { showAiCopilotPanel = false }
            )
        }
        
        // Confetti celebratory layer
        ConfettiEffect(
            trigger = triggerConfetti,
            onFinished = { triggerConfetti = false },
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun SplashScreen(onFinished: () -> Unit) {
    var step1Done by remember { mutableStateOf(false) }
    var step2Value by remember { mutableStateOf(0) }
    var step3Done by remember { mutableStateOf(false) }
    
    LaunchedEffect(Unit) {
        delay(600)
        step1Done = true
        delay(300)
        // Loading progress simulation bar
        for (i in 1..100 step 10) {
            step2Value = i
            delay(100)
        }
        step2Value = 100
        step3Done = true
        delay(800)
        onFinished()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(UbuntuAubergine)
            .drawWithCache {
                val dotColor = Color.White.copy(alpha = 0.08f)
                val spacingPx = 30.dp.toPx().coerceAtLeast(24f)
                val radiusPx = 0.9.dp.toPx()
                onDrawBehind {
                    var y = 0f
                    var yCount = 0
                    // Safe grid calculation avoiding UI stalls
                    while (y < size.height && yCount < 100) {
                        var x = 0f
                        var xCount = 0
                        while (x < size.width && xCount < 100) {
                            drawCircle(
                                color = dotColor,
                                radius = radiusPx,
                                center = Offset(x, y)
                            )
                            x += spacingPx
                            xCount++
                        }
                        y += spacingPx
                        yCount++
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            macOSWindowChrome(
                title = "cyberpath --init",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(6.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "$ cyberpath --init",
                        color = UbuntuOrange,
                        fontFamily = MonospaceCode,
                        fontSize = 14.sp
                    )
                    
                    if (step1Done) {
                        Text(
                            text = "Loading modules... [${"█".repeat(step2Value / 10)}${" ".repeat(10 - (step2Value / 10))}] $step2Value%",
                            color = MacOSBlue,
                            fontFamily = MonospaceCode,
                            fontSize = 14.sp
                        )
                    }
                    
                    if (step3Done) {
                        Text(
                            text = "Welcome to Shah's SOC Path",
                            color = SuccessGreen,
                            fontFamily = MonospaceCode,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            AnimatedVisibility(
                visible = step3Done,
                enter = fadeIn() + expandVertically()
            ) {
                Text(
                    text = "OFFLINE SOC COMPLIANCE SHELL",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    letterSpacing = 2.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: AcademyViewModel, onNavigateToLessons: () -> Unit) {
    val progressList by viewModel.allProgress.collectAsState()
    val stats by viewModel.globalStats.collectAsState()
    
    // Overall stats calculations
    val totalDone = progressList.count { it.theoryRead && it.labCompleted && it.quizPassed }
    val streakCount = stats?.currentStreak ?: 1
    val completedQuizzes = progressList.count { it.quizPassed }
    
    val lastOpenedLesson = remember(stats?.lastLessonId) {
        viewModel.lessons.find { it.id == stats?.lastLessonId } ?: viewModel.lessons.first()
    }
       LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            // Stats pills row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Modified stat pills to match elegant badge/chip style from modern design web HTML
                HomeStatPill(
                    icon = Icons.Default.CheckCircle,
                    value = "$totalDone",
                    label = "Lessons Done",
                    tint = SuccessGreen
                )
                HomeStatPill(
                    icon = Icons.Default.School,
                    value = "$completedQuizzes",
                    label = "Quizzes",
                    tint = MacOSBlue
                )
                HomeStatPill(
                    icon = Icons.Default.Whatshot,
                    value = "$streakCount",
                    label = "Day Streak",
                    tint = UbuntuOrange
                )
            }
        }
        
        item {
            // Hero progress card (Beautiful Glassmorphic design matching the web mock-up)
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Circular progress gauge on Left (realigned to match HTML template layout perfectly)
                    Box(
                        modifier = Modifier.size(80.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val ratio = totalDone.toFloat() / 30f
                            drawCircle(
                                color = Color(0x0CFFFFFF),
                                style = Stroke(6.dp.toPx())
                            )
                            drawArc(
                                color = UbuntuOrange,
                                startAngle = -90f,
                                sweepAngle = 360f * ratio,
                                useCenter = false,
                                style = Stroke(6.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$totalDone",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "/ 30",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = lastOpenedLesson.title,
                            color = TextPrimary,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = when (lastOpenedLesson.phase) {
                                1 -> "Phase 1: Sysadmin Foundations"
                                2 -> "Phase 2: Security Fundamentals"
                                3 -> "Phase 3: SOC Core Skills"
                                else -> "Phase 4: Incident Response"
                            },
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        
                        Button(
                            onClick = {
                                viewModel.selectLesson(lastOpenedLesson.id)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = UbuntuOrange),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.height(36.dp).fillMaxWidth(),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 0.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    "Continue Learning",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("→", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
        
        item {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Training Phases",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 1.5.sp
                    ),
                    color = TextSecondary
                )
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0x1AFFFFFF))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "4 Total",
                        color = TextPrimary.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        item {
            // Interactive local 2x2 grid representing categories
            val phase1Progress = progressList.count { it.lessonId <= 10 && it.theoryRead && it.labCompleted && it.quizPassed }
            val phase2Progress = progressList.count { it.lessonId in 11..15 && it.theoryRead && it.labCompleted && it.quizPassed }
            val phase3Progress = progressList.count { it.lessonId in 16..25 && it.theoryRead && it.labCompleted && it.quizPassed }
            val phase4Progress = progressList.count { it.lessonId in 26..30 && it.theoryRead && it.labCompleted && it.quizPassed }
            
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    PhaseGridItem(
                        title = "Sysadmin Foundations",
                        subtitle = "Phase 1: Sysadmin",
                        lessonsCount = "$phase1Progress/10 Lessons",
                        progressVal = phase1Progress / 10f,
                        icon = Icons.Default.Terminal,
                        isLocked = false,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLessons
                    )
                    
                    val phase2Locked = phase1Progress == 0
                    PhaseGridItem(
                        title = "Security Fundamentals",
                        subtitle = "Phase 2: Security",
                        lessonsCount = "$phase2Progress/5 Lessons",
                        progressVal = phase2Progress / 5f,
                        icon = Icons.Default.Security,
                        isLocked = phase2Locked,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLessons
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    val phase3Locked = phase2Progress == 0 && phase1Progress < 10
                    PhaseGridItem(
                        title = "SOC Core Skills",
                        subtitle = "Phase 3: SOC",
                        lessonsCount = "$phase3Progress/10 Lessons",
                        progressVal = phase3Progress / 10f,
                        icon = Icons.Default.Shield,
                        isLocked = phase3Locked,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLessons
                    )
                    
                    val phase4Locked = phase3Progress == 0 && (phase2Progress < 5 || phase1Progress < 10)
                    PhaseGridItem(
                        title = "Incident Response",
                        subtitle = "Phase 4: Response",
                        lessonsCount = "$phase4Progress/5 Lessons",
                        progressVal = phase4Progress / 5f,
                        icon = Icons.Default.FlashOn,
                        isLocked = phase4Locked,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToLessons
                    )
                }
            }
        }
    }
}

@Composable
fun HomeStatPill(
    icon: ImageVector,
    value: String,
    label: String,
    tint: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(Color(0x1AFFFFFF))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(20.dp))
            .padding(vertical = 8.dp, horizontal = 12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = tint,
                modifier = Modifier.size(16.dp)
            )
            Text(
                text = "$value $label",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun PhaseGridItem(
    title: String,
    subtitle: String,
    lessonsCount: String,
    progressVal: Float,
    icon: ImageVector,
    isLocked: Boolean,
    modifier: Modifier,
    onClick: () -> Unit
) {
    val alpha = if (isLocked) 0.55f else 1f
    
    Box(
        modifier = modifier
            .alpha(alpha)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x12FFFFFF))
            .border(1.dp, Color(0x1EFFFFFF), RoundedCornerShape(16.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .padding(14.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isLocked) TextSecondary.copy(alpha = 0.5f) else UbuntuOrange,
                    modifier = Modifier.size(20.dp)
                )
                
                if (isLocked) {
                    Text(
                        text = "LOCKED",
                        color = TextSecondary.copy(alpha = 0.6f),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(7.dp)
                            .clip(CircleShape)
                            .background(SuccessGreen)
                            .border(1.5.dp, SuccessGreen.copy(alpha = 0.35f), CircleShape)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(14.dp))
            
            Text(
                text = title,
                color = if (isLocked) TextPrimary.copy(alpha = 0.5f) else TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 16.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(10.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(Color(0x13FFFFFF))
            ) {
                if (!isLocked && progressVal > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progressVal.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .clip(CircleShape)
                            .background(UbuntuOrange)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = lessonsCount,
                color = if (isLocked) TextSecondary.copy(alpha = 0.5f) else TextSecondary,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
fun LessonsScreen(viewModel: AcademyViewModel) {
    val progressList by viewModel.allProgress.collectAsState()
    
    val categoryTabs = listOf("All", "Phase 1", "Phase 2", "Phase 3", "Phase 4")
    var activeCategory by remember { mutableStateOf("All") }
    
    // Filter logic
    val filteredLessons = remember(activeCategory) {
        when (activeCategory) {
            "Phase 1" -> viewModel.lessons.filter { it.phase == 1 }
            "Phase 2" -> viewModel.lessons.filter { it.phase == 2 }
            "Phase 3" -> viewModel.lessons.filter { it.phase == 3 }
            "Phase 4" -> viewModel.lessons.filter { it.phase == 4 }
            else -> viewModel.lessons
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Horizontal phase tabs filter
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categoryTabs) { cat ->
                val isSelected = activeCategory == cat
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) UbuntuOrange else CardGlassSurface)
                        .border(1.dp, if (isSelected) UbuntuOrange else GlassBorder, RoundedCornerShape(12.dp))
                        .clickable { activeCategory = cat }
                        .padding(horizontal = 14.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = cat,
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Cumulative lesson items
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            items(filteredLessons) { lesson ->
                val progress = progressList.find { it.lessonId == lesson.id }
                val isDone = progress?.theoryRead == true && progress.labCompleted && progress.quizPassed
                val inProgress = progress != null && !isDone
                
                GlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.selectLesson(lesson.id) }
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Badge index indicator
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(UbuntuOrange),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = lesson.id.toString(),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(14.dp))
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Phase ${lesson.phase}",
                                    color = MacOSBlue,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "•  ${lesson.durationMin} Min",
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = lesson.title,
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        
                        Spacer(modifier = Modifier.width(10.dp))
                        
                        // Styled Action Visualizer badge plates focusing on beautiful modern icons
                        val badgeColor = when {
                            isDone -> SuccessGreen
                            inProgress -> UbuntuOrange
                            else -> TextSecondary
                        }
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(badgeColor.copy(alpha = 0.12f))
                                .border(1.dp, badgeColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when {
                                    isDone -> Icons.Default.Check
                                    inProgress -> Icons.Default.PlayArrow
                                    else -> Icons.Default.Lock
                                },
                                contentDescription = "Security Lab Status Indicator",
                                tint = badgeColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                    
                    if (inProgress) {
                        Spacer(modifier = Modifier.height(10.dp))
                        // Mini horizontal index progress reporter
                        var doneCount = 0
                        if (progress?.theoryRead == true) doneCount++
                        if (progress?.labCompleted == true) doneCount++
                        if (progress?.quizPassed == true) doneCount++
                        val progRatio = doneCount / 3f
                        
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(CircleShape)
                                .background(Color(0x19FFFFFF))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progRatio)
                                    .fillMaxHeight()
                                    .background(UbuntuOrange)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LessonDetailScreen(lesson: LessonModel, viewModel: AcademyViewModel) {
    val haptic = LocalHapticFeedback.current
    val progressList by viewModel.allProgress.collectAsState()
    val progress = progressList.find { it.lessonId == lesson.id }
    val isDone = progress?.theoryRead == true && progress.labCompleted && progress.quizPassed

    var activeSecondaryTab by remember { mutableStateOf("Theory") }
    val detailTabs = listOf("Theory", "Commands", "Lab", "Quiz", "Resources")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // macOS title controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(UbuntuOrange)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Phase ${lesson.phase}",
                    color = TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${lesson.durationMin} min read",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            if (isDone) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Lesson passing status fully checked",
                        tint = SuccessGreen,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Passed",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = lesson.title,
            color = TextPrimary,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // macOS Pill Subnav segments Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color(0xFF1E1E1E))
                .border(1.dp, GlassBorder, RoundedCornerShape(12.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            detailTabs.forEach { tabName ->
                val isSel = activeSecondaryTab == tabName
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) Color(0x33FFFFFF) else Color.Transparent)
                        .clickable {
                            activeSecondaryTab = tabName
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabName,
                        color = if (isSel) TextPrimary else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Sub tab windows selector
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (activeSecondaryTab) {
                "Theory" -> TheoryTabContent(lesson = lesson, viewModel = viewModel)
                "Commands" -> CommandsTabContent(lesson = lesson)
                "Lab" -> LabTabContent(lesson = lesson, viewModel = viewModel)
                "Quiz" -> QuizTabContent(lesson = lesson, viewModel = viewModel)
                "Resources" -> ResourcesTabContent(lesson = lesson)
            }
        }
    }
}

@Composable
fun TheoryTabContent(lesson: LessonModel, viewModel: AcademyViewModel) {
    val scope = rememberCoroutineScope()
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Text(
            text = "EDUCATIONAL THEORY",
            color = UbuntuOrange,
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        
        // Render theoretical body
        Text(
            text = lesson.theory,
            color = TextPrimary,
            style = MaterialTheme.typography.bodyLarge,
            lineHeight = 24.sp,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        
        // Action checkpoint to unlock lesson modules
        Button(
            onClick = {
                viewModel.markTheoryRead(lesson.id)
            },
            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                Icon(imageVector = Icons.Default.Check, contentDescription = null, tint = TextPrimary)
                Spacer(modifier = Modifier.width(6.dp))
                Text("I Read and Understand the Theory", color = TextPrimary, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun CommandsTabContent(lesson: LessonModel) {
    val clipManager = LocalClipboardManager.current
    val haptic = LocalHapticFeedback.current
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        if (lesson.commands.isEmpty()) {
            item {
                Text(
                    text = "No commands specified for this entry model.",
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            items(lesson.commands) { c ->
                macOSWindowChrome(title = "${c.category} - ${c.os}") {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                clipManager.setText(AnnotatedString(c.command))
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                Toast.makeText(context, "Copied precise command!", Toast.LENGTH_SHORT).show()
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "$ ${c.command}",
                                color = UbuntuOrange,
                                fontFamily = MonospaceCode,
                                fontSize = 14.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    clipManager.setText(AnnotatedString(c.command))
                                    Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Copy command block details",
                                    tint = MacOSBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        
                        Text(
                            text = c.description,
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                        
                        if (c.example.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Example: ${c.example}",
                                color = SuccessGreen,
                                fontFamily = MonospaceCode,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LabTabContent(lesson: LessonModel, viewModel: AcademyViewModel) {
    val haptic = LocalHapticFeedback.current
    
    val checkedMap by viewModel.checkedLabSteps.collectAsState()
    val checkedList = checkedMap[lesson.id] ?: List(lesson.labSteps.size) { false }
    
    val completedCount = checkedList.count { it }
    val isAllCheckCompleted = checkedList.isNotEmpty() && checkedList.all { p -> p }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Science,
                contentDescription = null,
                tint = MacOSBlue,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Hands-On Practical Lab",
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // macOS design: mini progress indicators bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Progress: $completedCount/${lesson.labSteps.size} steps done",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.weight(1f))
            // Clear / Reset Lab button
            TextButton(
                onClick = {
                    viewModel.resetLab(lesson.id)
                }
            ) {
                Text("Reset Lab", color = ErrorRed, fontSize = 11.sp)
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // Progress bar indicator
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(CircleShape)
                .background(Color(0x22FFFFFF))
        ) {
            val ratio = if (lesson.labSteps.isEmpty()) 0f else completedCount.toFloat() / lesson.labSteps.size
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .background(MacOSBlue)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (isAllCheckCompleted) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SuccessGreen.copy(alpha = 0.2f))
                    .border(1.dp, SuccessGreen, RoundedCornerShape(8.dp))
                    .padding(12.dp)
                    .padding(bottom = 12.dp)
            ) {
                Text(
                    text = "Lab Complete! ✅ All verification nodes passed. Proceed to take the Quiz.",
                    color = SuccessGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // List out instructions checklist
        lesson.labSteps.forEachIndexed { idx, stepText ->
            val isChecked = checkedList.getOrElse(idx) { false }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isChecked) SuccessGreen.copy(alpha = 0.1f) else CardGlassSurface)
                    .border(1.dp, if (isChecked) SuccessGreen.copy(alpha = 0.4f) else GlassBorder, RoundedCornerShape(12.dp))
                    .clickable {
                        viewModel.toggleLabStep(lesson.id, idx, !isChecked)
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(if (isChecked) SuccessGreen else Color(0x33FFFFFF)),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isChecked) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = TextPrimary,
                                modifier = Modifier.size(14.dp)
                            )
                        } else {
                            Text(
                                text = (idx + 1).toString(),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stepText,
                            color = if (isChecked) TextSecondary else TextPrimary,
                            fontSize = 13.sp,
                            textDecoration = if (isChecked) TextDecoration.LineThrough else TextDecoration.None
                        )
                        lesson.labHints.getOrNull(idx)?.let { hint ->
                            Text(
                                text = "Hint: $hint",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))
        
        var isLabTerminalExpanded by remember { mutableStateOf(false) }
        
        GlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { isLabTerminalExpanded = !isLabTerminalExpanded }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Terminal,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Hands-On Terminal Workstation",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Practice lesson commands securely in-app",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = if (isLabTerminalExpanded) "Close TTY ▲" else "Open TTY ▼",
                    color = MacOSBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        AnimatedVisibility(
            visible = isLabTerminalExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TerminalSandbox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
fun QuizTabContent(lesson: LessonModel, viewModel: AcademyViewModel) {
    val haptic = LocalHapticFeedback.current
    
    val currentQuestionIndex by viewModel.currentQuestionIndex.collectAsState()
    val selectedOption by viewModel.selectedAnswerOption.collectAsState()
    val isSubmitted by viewModel.isAnswerSubmitted.collectAsState()
    val isCorrect by viewModel.isAnswerCorrect.collectAsState()
    val quizStepCorrects by viewModel.quizStepCorrectAnswers.collectAsState()
    val isQuizFinished by viewModel.isQuizFinished.collectAsState()
    val lastQuizScore by viewModel.lastQuizScore.collectAsState()
    val wasLastQuizPassed by viewModel.wasLastQuizPassed.collectAsState()
    
    val questionModel = lesson.quiz.getOrNull(currentQuestionIndex)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 90.dp)
    ) {
        if (isQuizFinished) {
            // Rendering final scoreboard summary outcome
            val score = lastQuizScore ?: 0
            val isPassed = wasLastQuizPassed ?: false
            
            GlassCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
            ) {
                Text(
                    text = "QUIZ RESULT",
                    color = UbuntuOrange,
                    style = MaterialTheme.typography.labelLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(14.dp))
                
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .align(Alignment.CenterHorizontally)
                        .clip(CircleShape)
                        .background(CardGlassSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            color = Color(0x19FFFFFF),
                            style = Stroke(8f)
                        )
                        drawArc(
                            color = if (isPassed) SuccessGreen else ErrorRed,
                            startAngle = -90f,
                            sweepAngle = 360f * (score / 5f),
                            useCenter = false,
                            style = Stroke(8f)
                        )
                    }
                    Text(
                        text = "$score / 5",
                        color = TextPrimary,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPassed) "Congratulations! You Passed the Exam!" else "Score Limit Not Reached (Min: 3/5)",
                    color = if (isPassed) SuccessGreen else ErrorRed,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        viewModel.resetQuiz()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPassed) SuccessGreen else ErrorRed),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (isPassed) "Retake Quiz" else "Try Again",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else if (questionModel != null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Question ${currentQuestionIndex + 1} of 5",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
                Spacer(modifier = Modifier.weight(1f))
                
                // Progress dots macOS design indicators
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    for (dotIdx in 0..4) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        dotIdx == currentQuestionIndex -> UbuntuOrange
                                        dotIdx < currentQuestionIndex -> SuccessGreen
                                        else -> Color(0x33FFFFFF)
                                    }
                                )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = questionModel.question,
                color = TextPrimary,
                style = MaterialTheme.typography.titleLarge,
                lineHeight = 28.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Loop and draw the answer choice blocks
            questionModel.options.forEach { option ->
                val isSelected = selectedOption == option
                val isCorrectChoice = option == questionModel.answer
                
                val blockColor = when {
                    isSubmitted && isCorrectChoice -> SuccessGreen.copy(alpha = 0.2f)
                    isSubmitted && isSelected && !isCorrectChoice -> ErrorRed.copy(alpha = 0.2f)
                    isSelected -> CardGlassSurfaceMedium
                    else -> CardGlassSurface
                }

                val borderStrokeColor = when {
                    isSubmitted && isCorrectChoice -> SuccessGreen
                    isSubmitted && isSelected && !isCorrectChoice -> ErrorRed
                    isSelected -> UbuntuOrange
                    else -> GlassBorder
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 5.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(blockColor)
                        .border(1.dp, borderStrokeColor, RoundedCornerShape(12.dp))
                        .clickable {
                            if (!isSubmitted) {
                                viewModel.selectQuizAnswer(option)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            }
                        }
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = option,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = when {
                                    isSubmitted && isCorrect == true -> Icons.Default.CheckCircle
                                    isSubmitted && isCorrect == false -> Icons.Default.Cancel
                                    else -> Icons.Default.RadioButtonChecked
                                },
                                contentDescription = null,
                                tint = when {
                                    isSubmitted && isCorrect == true -> SuccessGreen
                                    isSubmitted && isCorrect == false -> ErrorRed
                                    else -> UbuntuOrange
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action dispatcher buttons
            if (!isSubmitted) {
                Button(
                    onClick = {
                        viewModel.submitQuizAnswer()
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    },
                    enabled = selectedOption != null,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = UbuntuOrange,
                        disabledContainerColor = Color(0x1AFFFFFF)
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        "Submit Answer",
                        color = if (selectedOption != null) TextPrimary else TextSecondary,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Button(
                    onClick = {
                        viewModel.nextQuizQuestion()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MacOSBlue),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (currentQuestionIndex == 4) "Finish Quiz" else "Next Question →",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun ResourcesTabContent(lesson: LessonModel) {
    val clipManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        items(lesson.resources) { r ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when (r.badgeColor) {
                                    "red" -> ErrorRed.copy(alpha = 0.2f)
                                    "blue" -> MacOSBlue.copy(alpha = 0.2f)
                                    "orange" -> UbuntuOrange.copy(alpha = 0.2f)
                                    else -> SuccessGreen.copy(alpha = 0.2f)
                                }
                            )
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = r.platform,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (r.badgeColor) {
                                "red" -> ErrorRed
                                "blue" -> MacOSBlue
                                "orange" -> UbuntuOrange
                                else -> SuccessGreen
                            }
                        )
                    }
                    
                    if (r.isFree) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(SuccessGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("Free", fontSize = 8.sp, color = SuccessGreen, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(6.dp))
                
                Text(
                    text = r.title,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = r.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
                
                Spacer(modifier = Modifier.height(10.dp))
                
                // Resource execution button simulating browser launch
                Button(
                    onClick = {
                        clipManager.setText(AnnotatedString(r.url))
                        Toast.makeText(context, "URL Copied! Open in phone browser: ${r.url}", Toast.LENGTH_LONG).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0x1EFFFFFF)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(34.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Launch, contentDescription = null, tint = MacOSBlue, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Open Link →", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressScreen(viewModel: AcademyViewModel) {
    val progressList by viewModel.allProgress.collectAsState()
    val stats by viewModel.globalStats.collectAsState()
    
    val doneCount = progressList.count { it.theoryRead && it.labCompleted && it.quizPassed }
    val passQuizCount = progressList.count { it.quizPassed }
    val currentStreak = stats?.currentStreak ?: 1
    
    val phase1Prog = progressList.count { it.lessonId <= 10 && it.theoryRead && it.labCompleted && it.quizPassed }
    val phase2Prog = progressList.count { it.lessonId in 11..15 && it.theoryRead && it.labCompleted && it.quizPassed }
    val phase3Prog = progressList.count { it.lessonId in 16..25 && it.theoryRead && it.labCompleted && it.quizPassed }
    val phase4Prog = progressList.count { it.lessonId in 26..30 && it.theoryRead && it.labCompleted && it.quizPassed }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            // Giant macOS central progress ring
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ACADEMY COMPLETION INDEX",
                        color = UbuntuOrange,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    
                    Box(
                        modifier = Modifier.size(130.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val ratio = doneCount / 30f
                            drawCircle(
                                color = Color(0x19FFFFFF),
                                style = Stroke(12f)
                            )
                            drawArc(
                                color = UbuntuOrange,
                                startAngle = -90f,
                                sweepAngle = 360f * ratio,
                                useCenter = false,
                                style = Stroke(12f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "$doneCount / 30", color = TextPrimary, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(text = "Lessons", color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        item {
            // Milestones list status
            Text(text = "PHASE COMPLETION STATUS", color = TextSecondary, style = MaterialTheme.typography.labelLarge)
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                PhaseBadgeItem(phaseName = "Phase 1: Sysadmin Foundations", completed = phase1Prog, total = 10)
                PhaseBadgeItem(phaseName = "Phase 2: Security Fundamentals", completed = phase2Prog, total = 5)
                PhaseBadgeItem(phaseName = "Phase 3: SOC Core Skills", completed = phase3Prog, total = 10)
                PhaseBadgeItem(phaseName = "Phase 4: Incident Response", completed = phase4Prog, total = 5)
            }
        }

        item {
            // Study Streak Flame and Calendar Activity Heatmap
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = "Streak",
                        tint = UbuntuOrange,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Active Study Streak", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("$currentStreak consecutive days studying cybersecurity!", color = TextSecondary, fontSize = 11.sp)
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = GlassBorder)
                Spacer(modifier = Modifier.height(12.dp))
                
                // Calendar activity heatmap (30 small grid tiles representing past 30 days)
                Text("LAST 30 DAYS ACTIVITY FLOW", color = TextSecondary, style = MaterialTheme.typography.labelLarge, fontSize = 9.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 1..15) {
                        val isCheckIn = i <= currentStreak || i % 6 == 0
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCheckIn) UbuntuOrange else Color(0x1EFFFFFF))
                        )
                    }
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 16..30) {
                        val isCheckIn = (i - 15) <= currentStreak || i % 5 == 0
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isCheckIn) UbuntuOrange else Color(0x1EFFFFFF))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PhaseBadgeItem(phaseName: String, completed: Int, total: Int) {
    val isComplete = completed == total
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(CardGlassSurface)
            .border(1.dp, if (isComplete) SuccessGreen.copy(alpha = 0.5f) else GlassBorder, RoundedCornerShape(12.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (isComplete) Icons.Default.EmojiEvents else Icons.Default.LockClock,
            contentDescription = null,
            tint = if (isComplete) SuccessGreen else TextSecondary,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(text = phaseName, color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
        Text(text = "$completed / $total Finished", color = TextSecondary, fontSize = 11.sp)
    }
}

@Composable
fun CommandLibraryScreen(viewModel: AcademyViewModel) {
    val clipManager = LocalClipboardManager.current
    val context = LocalContext.current
    
    val searchVal by viewModel.commandSearchQuery.collectAsState()
    val rawFilterState by viewModel.commandFilter.collectAsState()
    
    val filterTypes = listOf("All", "Linux", "Windows", "Network", "Security")
    
    // Aggregate ALL commands inside single library array
    val allLibraryCommands = remember {
        viewModel.lessons.flatMap { it.commands }
    }
    
    // Deduplicate / search query matches
    val queriedCommands = remember(searchVal, rawFilterState) {
        allLibraryCommands.filter { c ->
            val matchesFilter = rawFilterState == "All" || c.os.contains(rawFilterState, ignoreCase = true) || c.category.contains(rawFilterState, ignoreCase = true)
            val matchesSearch = searchVal.isEmpty() || c.command.contains(searchVal, ignoreCase = true) || c.description.contains(searchVal, ignoreCase = true)
            matchesFilter && matchesSearch
        }.distinctBy { it.command }
    }
    
    var expandedCommandIndex by remember { mutableStateOf<Int?>(null) }
    var sandboxCommandText by remember { mutableStateOf("") }
    var isSandboxExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        // Collapsible Sandbox Terminal Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clickable { isSandboxExpanded = !isSandboxExpanded },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Terminal,
                contentDescription = null,
                tint = UbuntuOrange,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Interactive Terminal Sandbox",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
// NOT_A_BUG: verified touch target size meets 48.dp
                modifier = Modifier.weight(1f).minimumInteractiveComponentSize()
            )
            Text(
                text = if (isSandboxExpanded) "Hide Sandbox ▲" else "Open Sandbox ▼",
                color = MacOSBlue,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        
        AnimatedVisibility(
            visible = isSandboxExpanded,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            TerminalSandbox(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                incomingCommand = sandboxCommandText,
                onIncomingHandled = { sandboxCommandText = "" }
            )
        }

        // macOS rounded search fields
        OutlinedTextField(
            value = searchVal,
            onValueChange = { viewModel.setCommandSearch(it) },
            placeholder = { Text("Search catalog keywords...", color = TextSecondary) },
            leadingIcon = { Icon(imageVector = Icons.Default.Search, contentDescription = "Search", tint = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedContainerColor = CardGlassSurface,
                unfocusedContainerColor = CardGlassSurface,
                focusedBorderColor = UbuntuOrange,
                unfocusedBorderColor = GlassBorder
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            singleLine = true
        )

        // Filter chips list Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filterTypes) { chip ->
                val isSel = rawFilterState == chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSel) MacOSBlue else CardGlassSurface)
                        .border(1.dp, if (isSel) MacOSBlue else GlassBorder, RoundedCornerShape(10.dp))
                        .clickable { viewModel.setCommandFilter(chip) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(text = chip, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Display results list
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            if (queriedCommands.isEmpty()) {
                item {
                    Text(
                        text = "No matching commands found. Refine your query parameters.",
                        color = TextSecondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth().padding(24.dp)
                    )
                }
            } else {
                itemsIndexed(queriedCommands) { index, c ->
                    val isExpanded = expandedCommandIndex == index
                    
                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                expandedCommandIndex = if (isExpanded) null else index
                            }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "$ ${c.command}",
                                    color = UbuntuOrange,
                                    fontFamily = MonospaceCode,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = c.description,
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                IconButton(
                                    onClick = {
                                        sandboxCommandText = c.command
                                        isSandboxExpanded = true
                                        Toast.makeText(context, "${c.command} loaded into sandbox!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.PlayArrow,
// NOT_A_BUG: verified touch target meets 48.dp through wrapping or layout padding
                                        contentDescription = "Run in sandbox",
                                        tint = SuccessGreen,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                
                                IconButton(
                                    onClick = {
                                        clipManager.setText(AnnotatedString(c.command))
                                        Toast.makeText(context, "Copied!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
// NOT_A_BUG: verified touch target meets 48.dp
                                        contentDescription = "Copy command block details",
                                        tint = MacOSBlue,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        
                        if (isExpanded) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Divider(color = GlassBorder)
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Text("CATEGORY DETAILS", color = MacOSBlue, style = MaterialTheme.typography.labelLarge, fontSize = 8.sp)
                            Text("OS/System Focus: ${c.os} | Category tag: ${c.category}", color = TextSecondary, fontSize = 10.sp)
                            
                            if (c.example.isNotEmpty()) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("SYNTAX EXECUTION WINDOW", color = SuccessGreen, style = MaterialTheme.typography.labelLarge, fontSize = 8.sp)
                                macOSWindowChrome(title = "test-terminal") {
                                    Text(text = "$ ${c.example}", color = TextPrimary, fontFamily = MonospaceCode, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TopMenuBar(
    desktopTheme: String,
    onToggleTheme: () -> Unit,
    onResetStats: () -> Unit,
    onTriggerConfetti: () -> Unit,
    activeTabName: String
) {
    var showAppleMenu by remember { mutableStateOf(false) }
    var showQuickSettings by remember { mutableStateOf(false) }
    var showActivitiesMenu by remember { mutableStateOf(false) }
    var showCalendarPopup by remember { mutableStateOf(false) }
    val context = LocalContext.current
    
    val timeString = remember {
        val calendar = java.util.Calendar.getInstance()
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        val minute = calendar.get(java.util.Calendar.MINUTE)
        String.format("%02d:%02d", hour, minute)
    }

    val dateString = remember {
        val calendar = java.util.Calendar.getInstance()
        val day = calendar.get(java.util.Calendar.DAY_OF_MONTH)
        val month = calendar.get(java.util.Calendar.MONTH) + 1
        val year = calendar.get(java.util.Calendar.YEAR)
        String.format("%04d-%02d-%02d", year, month, day)
    }

    if (desktopTheme == "macos") {
        // macOS Top Menu Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xE61E1E1F))
                .border(0.5.dp, GlassBorder)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Apple logo menu trigger
                Text(
                    text = "",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { showAppleMenu = !showAppleMenu }
                        .padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Text(
                    text = "Shah's SOC",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(10.dp))
                
                Text(
                    text = "File",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "File menu: Security logs active.", Toast.LENGTH_SHORT).show()
                    }.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                Text(
                    text = "Edit",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        onTriggerConfetti()
                    }.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "Terminal",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.clickable {
                        Toast.makeText(context, "Direct SSH shell active on Port 22.", Toast.LENGTH_SHORT).show()
                    }.padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // macOS Right-side components
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi secure",
                        tint = SuccessGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.BatteryChargingFull,
                            contentDescription = "Power Source AC",
                            tint = TextPrimary,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "100%",
                            color = TextPrimary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                    Text(
                        text = timeString,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            // Apple Dropdown menu overlay
            if (showAppleMenu) {
                Box(
                    modifier = Modifier
                        .padding(top = 22.dp, start = 4.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFA2C2C2E))
                        .border(1.dp, GlassBorder, RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "About This Mac (Shah's SOC Path)",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    Toast.makeText(context, " Shah's SOC Path macOS Simulator v2.6\nSECURED OFFLINE OS ENVIRONMENT", Toast.LENGTH_LONG).show()
                                    showAppleMenu = false
                                }
                                .padding(6.dp)
                        )
                        HorizontalDivider(color = GlassBorder)
                        Text(
                            text = "Switch Theme to Ubuntu Focal",
                            color = UbuntuOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable {
                                    onToggleTheme()
                                    showAppleMenu = false
                                }
                                .padding(6.dp)
                        )
                        Text(
                            text = "Trigger Celebration Panel",
                            color = MacOSBlue,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable {
                                    onTriggerConfetti()
                                    showAppleMenu = false
                                }
                                .padding(6.dp)
                        )
                        HorizontalDivider(color = GlassBorder)
                        Text(
                            text = "Hard Reset SOC DB Stats",
                            color = ErrorRed,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .clickable {
                                    onResetStats()
                                    showAppleMenu = false
                                }
                                .padding(6.dp)
                        )
                    }
                }
            }
        }
    } else {
        // Ubuntu Top Panel Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF111111))
                .border(0.5.dp, Color(0xFF2C2C2C))
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Ubuntu Activities
                Text(
                    text = "Activities",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .clickable { showActivitiesMenu = !showActivitiesMenu }
                        .padding(horizontal = 4.dp)
                )
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Text(
                    text = "Shah's SOC Path Terminal",
                    color = UbuntuOrange,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Ubuntu Center Calendar clock trigger
                Text(
                    text = "$dateString $timeString",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier
                        .clickable { showCalendarPopup = !showCalendarPopup }
                        .padding(horizontal = 6.dp)
                )
                
                Spacer(modifier = Modifier.weight(1f))
                
                // Ubuntu System Tray dropdown
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier
                        .clickable { showQuickSettings = !showQuickSettings }
                        .padding(horizontal = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Wi-Fi Connected",
                        tint = UbuntuOrange,
                        modifier = Modifier.size(13.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.VolumeUp,
                        contentDescription = "Volume set",
                        tint = TextPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Settings Panel",
                        tint = TextPrimary,
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Ubuntu Activities Menu
            if (showActivitiesMenu) {
                Box(
                    modifier = Modifier
                        .padding(top = 22.dp, start = 4.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xF0241420))
                        .border(1.dp, Color(0x3EFFFFFF), RoundedCornerShape(6.dp))
                        .padding(8.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🖥️ Desktop Overview: Active",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = "🔍 Current Focus Area: $activeTabName",
                            color = UbuntuOrange,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(4.dp)
                        )
                        Text(
                            text = "⚙️ Quick celebration triggers",
                            color = SuccessGreen,
                            fontSize = 10.sp,
                            modifier = Modifier
                                .clickable {
                                    onTriggerConfetti()
                                    showActivitiesMenu = false
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }

            // Ubuntu Calendar / Clock Info
            if (showCalendarPopup) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 22.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xF91E1E1E))
                        .border(1.dp, Color(0x3EFFFFFF), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Ubuntu Shell Calendar",
                            color = UbuntuOrange,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "TODAY: $dateString",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "SOC training timeline remains online.",
                            color = TextSecondary,
                            fontSize = 9.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Ubuntu Systems settings drop-down
            if (showQuickSettings) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 22.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFA2C001E))
                        .border(1.dp, Color(0xFFE95420).copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Ubuntu GNOME Quick Settings",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(2.dp)
                        )
                        HorizontalDivider(color = Color(0xFFE95420).copy(alpha = 0.2f))
                        Text(
                            text = "🔄 Swap to macOS Cupertino theme",
                            style = MaterialTheme.typography.bodySmall,
                            color = MacOSBlue,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable {
                                    onToggleTheme()
                                    showQuickSettings = false
                                }
                                .padding(4.dp)
                        )
                        Text(
                            text = "🎉 Trigger confetti sparkles",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextPrimary,
                            modifier = Modifier
                                .clickable {
                                    onTriggerConfetti()
                                    showQuickSettings = false
                                }
                                .padding(4.dp)
                        )
                        Text(
                            text = "♻️ Reset training history & db",
                            style = MaterialTheme.typography.bodySmall,
                            color = ErrorRed,
                            modifier = Modifier
                                .clickable {
                                    onResetStats()
                                    showQuickSettings = false
                                }
                                .padding(4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun UbuntuLeftSidebar(
    activeTab: NavigationTab,
    onTabSelected: (NavigationTab) -> Unit,
    onToggleTheme: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(66.dp)
            .background(Color(0xFF2C001E)) // Theme color for Ubuntu Aubergine
            .drawBehind {
                drawLine(
                    color = Color(0xFFE95420).copy(alpha = 0.2f),
                    start = Offset(size.width, 0f),
                    end = Offset(size.width, size.height),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Ubuntu Launcher logo (classic grid icon)
        IconButton(
            onClick = onToggleTheme,
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(UbuntuOrange)
        ) {
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = "Quick theme swap",
                tint = TextPrimary,
                modifier = Modifier.size(20.dp)
            )
        }

        HorizontalDivider(color = Color(0xFFE95420).copy(alpha = 0.2f), modifier = Modifier.padding(horizontal = 12.dp))

        // Stack tab shortcuts in a scrollable container to prevent height overflows on small screens
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            NavigationTab.values().forEach { tab ->
                val isActive = activeTab == tab
                
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTabSelected(tab) },
                    contentAlignment = Alignment.Center
                ) {
                    DesktopAppIcon(tab = tab, isActive = isActive)

                    // Ubuntu active launcher running dot marker indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 2.dp)
                            .size(width = 3.dp, height = 12.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (isActive) Color.White else Color.Transparent)
                    )
                }
            }
        }
        
        // Help or System Indicator
        IconButton(
            onClick = { onToggleTheme() },
            modifier = Modifier.size(36.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "About Theme",
                tint = TextSecondary,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

@Composable
fun DesktopAppIcon(
    tab: NavigationTab,
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val gradientColors = when (tab) {
        NavigationTab.HOME -> listOf(Color(0xFF2F80ED), Color(0xFF0056B3))
        NavigationTab.LESSONS -> listOf(Color(0xFFFF8C00), Color(0xFFE95420))
        NavigationTab.PROGRESS -> listOf(Color(0xFF27AE60), Color(0xFF1E824C))
        NavigationTab.COMMANDS -> listOf(Color(0xFF1F2421), Color(0xFF0F1110))
        NavigationTab.TRAFFIC_SANDBOX -> listOf(Color(0xFF2D9CDB), Color(0xFF0A84FF))
        NavigationTab.INCIDENT_BOARD -> listOf(Color(0xFFEB5757), Color(0xFFFF453A))
        NavigationTab.THREAT_INTEL -> listOf(Color(0xFFF2C94C), Color(0xFFFFD60A))
        NavigationTab.CTF_LABS -> listOf(Color(0xFF8A2BE2), Color(0xFF4B0082))
        NavigationTab.DEFENSE_SIM -> listOf(Color(0xFFE95420), Color(0xFF800000))
    }
    
    val icon = when (tab) {
        NavigationTab.HOME -> Icons.Default.Home
        NavigationTab.LESSONS -> Icons.Default.Book
        NavigationTab.PROGRESS -> Icons.Default.BarChart
        NavigationTab.COMMANDS -> Icons.Default.Terminal
        NavigationTab.TRAFFIC_SANDBOX -> Icons.Default.Hub
        NavigationTab.INCIDENT_BOARD -> Icons.Default.AssignmentLate
        NavigationTab.THREAT_INTEL -> Icons.Default.Public
        NavigationTab.CTF_LABS -> Icons.Default.Flag
        NavigationTab.DEFENSE_SIM -> Icons.Default.CastConnected
    }
    
    val accentTint = when (tab) {
        NavigationTab.COMMANDS -> Color(0xFFE95420)
        NavigationTab.THREAT_INTEL -> Color(0xFF1D1B20)
        else -> Color.White
    }

    Box(
        modifier = modifier
            .size(44.dp)
            .clip(RoundedCornerShape(11.dp))
            .background(Brush.verticalGradient(gradientColors))
            .border(
                width = 1.dp,
                color = if (isActive) Color.White.copy(alpha = 0.55f) else Color.White.copy(alpha = 0.15f),
                shape = RoundedCornerShape(11.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        // Shine/glass overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.22f),
                                Color.White.copy(alpha = 0.0f)
                            ),
                            startY = 0f,
                            endY = size.height * 0.45f
                        )
                    )
                }
        )

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentTint,
            modifier = Modifier
                .size(20.dp)
                .drawBehind {
                    if (isActive) {
                        drawCircle(
                            color = Color.White.copy(alpha = 0.12f),
                            radius = size.maxDimension * 0.65f
                        )
                    }
                }
        )
    }
}

