package com.example

import android.Manifest
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessibilityNew
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.ui.ActiveWorkoutSession
import com.example.ui.GmMainViewModel
import com.example.ui.NavigationScreen
import com.example.ui.components.ActiveWorkoutPlayerScreen
import com.example.ui.components.AppLockOverlay
import com.example.ui.components.GmAppTopHeader
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.EvolutionScreen
import com.example.ui.screens.GpsTrackerScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NutritionScreen
import com.example.ui.screens.RewardsScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.screens.WorkoutsScreen
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    private val viewModel: GmMainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val currentLang by viewModel.currentLanguage.collectAsState()
            val layoutDir = if (currentLang == AppLanguage.ARABIC) LayoutDirection.Rtl else LayoutDirection.Ltr

            MyApplicationTheme {
                CompositionLocalProvider(LocalLayoutDirection provides layoutDir) {
                    GmMainApp(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun GmMainApp(viewModel: GmMainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val activeSession by viewModel.activeSession.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val currentLang by viewModel.currentLanguage.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    val gpsSession by viewModel.gpsSession.collectAsState()
    val savedTracks by viewModel.gpsTracks.collectAsState()
    val userDailyStepLimit by viewModel.userDailyStepLimit.collectAsState()
    val userDailyDistanceLimitKm by viewModel.userDailyDistanceLimitKm.collectAsState()

    // Security & App Lock State
    val isAppLocked by viewModel.isAppLocked.collectAsState()
    val failedAttempts by viewModel.failedAttempts.collectAsState()
    val lockoutUntilTimestamp by viewModel.lockoutUntilTimestamp.collectAsState()

    // Location Permission Handler (invoked on demand)
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocation = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocation = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        if (fineLocation || coarseLocation) {
            viewModel.refreshLocation()
        }
    }

    val bodyType = viewModel.getBodyTypeEnum()
    val totalPoints = profile?.totalPoints ?: 0f
    val streakDays = profile?.streakDays ?: 0
    val isCloudSynced = profile?.isCloudSynced ?: true

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (activeSession != null) {
            // Active Workout Live Player Overlay
            ActiveWorkoutPlayerScreen(
                session = activeSession!!,
                onPauseResume = { viewModel.pauseResumeWorkout() },
                onCompleteSet = { weight, reps -> viewModel.completeCurrentSet(weight, reps) },
                onSkipRest = { viewModel.skipRest() },
                onAdjustRest = { delta -> viewModel.adjustRestTime(delta) },
                onFinishWorkout = { viewModel.completeActiveWorkout() },
                onClose = { viewModel.closeWorkoutPlayer() },
                onNextRoutineExercise = { viewModel.advanceToNextRoutineExercise() }
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .navigationBarsPadding(),
                snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
                topBar = {
                    GmAppTopHeader(
                        userName = profile?.name ?: "Champion",
                        totalPoints = totalPoints,
                        streakDays = streakDays,
                        bodyType = bodyType,
                        isCloudSynced = isCloudSynced,
                        onNavigateToRewards = { viewModel.navigateTo(NavigationScreen.REWARDS) }
                    )
                },
                bottomBar = {
                    GmBottomNavigationBar(
                        currentScreen = currentScreen,
                        viewModel = viewModel,
                        onSelectScreen = { viewModel.navigateTo(it) }
                    )
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "screen_transition"
                    ) { screen ->
                        when (screen) {
                            NavigationScreen.HOME -> HomeScreen(
                                viewModel = viewModel,
                                onStartWorkout = { viewModel.startWorkout(it) }
                            )
                            NavigationScreen.WORKOUTS -> WorkoutsScreen(
                                viewModel = viewModel,
                                onStartWorkout = { viewModel.startWorkout(it) }
                            )
                            NavigationScreen.GPS_TRACKER -> GpsTrackerScreen(
                                gpsSession = gpsSession,
                                savedTracks = savedTracks,
                                userDailyStepLimit = userDailyStepLimit,
                                userDailyDistanceLimitKm = userDailyDistanceLimitKm,
                                onStartTracking = { viewModel.startGpsTracking(it) },
                                onPauseTracking = { viewModel.pauseGpsTracking() },
                                onResumeTracking = { viewModel.resumeGpsTracking() },
                                onFinishAndSave = { viewModel.finishAndSaveGpsTracking() },
                                onResetTracking = { viewModel.resetGpsTracking() },
                                onUpdateDailyLimits = { steps, distance -> viewModel.updateDailyLimits(steps, distance) }
                            )
                            NavigationScreen.NUTRITION -> NutritionScreen(
                                viewModel = viewModel
                            )
                            NavigationScreen.EVOLUTION -> EvolutionScreen(
                                viewModel = viewModel
                            )
                            NavigationScreen.COMMUNITY -> CommunityScreen(
                                viewModel = viewModel
                            )
                            NavigationScreen.REWARDS -> RewardsScreen(
                                viewModel = viewModel
                            )
                            NavigationScreen.SETTINGS -> SettingsScreen(
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }

        // App Lock Screen Overlay (PIN / Biometrics Protection)
        AppLockOverlay(
            isLocked = isAppLocked,
            onVerifyPin = { pin -> viewModel.verifyPinCode(pin) },
            onBiometricUnlock = { viewModel.unlockBiometrics() },
            failedAttempts = failedAttempts,
            lockoutUntilTimestamp = lockoutUntilTimestamp
        )
    }
}

data class NavItem(
    val screen: NavigationScreen,
    val icon: ImageVector,
    val stringKey: String
)

@Composable
fun GmBottomNavigationBar(
    currentScreen: NavigationScreen,
    viewModel: GmMainViewModel,
    onSelectScreen: (NavigationScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val navItems = listOf(
        NavItem(NavigationScreen.HOME, Icons.Default.Home, "home_nav"),
        NavItem(NavigationScreen.WORKOUTS, Icons.Default.FitnessCenter, "workouts_nav"),
        NavItem(NavigationScreen.GPS_TRACKER, Icons.Default.LocationOn, "gps_nav"),
        NavItem(NavigationScreen.NUTRITION, Icons.Default.Restaurant, "nutrition_nav"),
        NavItem(NavigationScreen.EVOLUTION, Icons.Default.DirectionsRun, "evolution_nav"),
        NavItem(NavigationScreen.COMMUNITY, Icons.Default.Group, "community_nav"),
        NavItem(NavigationScreen.SETTINGS, Icons.Default.Settings, "settings_nav")
    )

    NavigationBar(
        modifier = modifier.testTag("bottom_navigation_bar"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        navItems.forEach { item ->
            val isSelected = currentScreen == item.screen
            val localizedLabel = if (item.stringKey == "gps_nav") "تتبع GPS" else viewModel.getString(item.stringKey)
            NavigationBarItem(
                selected = isSelected,
                onClick = { onSelectScreen(item.screen) },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = localizedLabel,
                        modifier = Modifier.size(20.dp)
                    )
                },
                label = {
                    Text(
                        text = localizedLabel,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.Normal
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FlameOrange,
                    selectedTextColor = FlameOrange,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor = FlameOrange.copy(alpha = 0.15f)
                )
            )
        }
    }
}
