package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.foundation.BorderStroke
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.AppLanguage
import com.example.data.Exercise
import com.example.data.RewardSystem
import com.example.ui.GmMainViewModel
import com.example.ui.NavigationScreen
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.GmSystemHudWidget
import com.example.ui.components.MuscleRecoveryRadarCard
import com.example.ui.components.QuickStatCard
import com.example.ui.components.SecurityArmorDialog
import com.example.ui.components.SocialAccountLinkingDialog
import com.example.ui.components.WeeklyChallengesCard
import java.util.Locale
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun HomeScreen(
    viewModel: GmMainViewModel,
    onStartWorkout: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val caloriesBurnedToday by viewModel.caloriesBurnedToday.collectAsState()
    val liveSteps by viewModel.liveSteps.collectAsState()
    val liveActiveMinutes by viewModel.liveActiveMinutes.collectAsState()
    val isTrackerConnected by viewModel.isTrackerConnected.collectAsState()
    val todayWaterTotalMl by viewModel.todayWaterTotalMl.collectAsState()
    val gpsSession by viewModel.gpsSession.collectAsState()
    val userDailyStepLimit by viewModel.userDailyStepLimit.collectAsState()

    val locationInfo by viewModel.userLocationInfo.collectAsState()
    val socialState by viewModel.socialAccountState.collectAsState()
    val securityReport by viewModel.securityAuditReport.collectAsState()

    var showSocialModal by remember { mutableStateOf(false) }
    var showSecurityModal by remember { mutableStateOf(false) }

    val bodyType = viewModel.getBodyTypeEnum()
    val macros = viewModel.calculateMacros()
    val totalCaloriesConsumed = todayMeals.sumOf { it.calories }
    val totalPoints = profile?.totalPoints ?: 0f
    val currentLevel = RewardSystem.getCurrentLevel(totalPoints)

    val recommendedWorkouts = viewModel.getFilteredExercises().take(4)
    val dailyWaterGoalMl = viewModel.calculateDailyWaterGoalMl()
    val currentWaterMl = todayWaterTotalMl ?: (profile?.waterGlasses?.times(250) ?: 0)

    if (showSocialModal) {
        SocialAccountLinkingDialog(
            socialState = socialState,
            onLinkGoogle = { email -> viewModel.linkGoogleAccount(email) },
            onUnlinkGoogle = { viewModel.unlinkGoogleAccount() },
            onLinkFacebook = { name -> viewModel.linkFacebookAccount(name) },
            onUnlinkFacebook = { viewModel.unlinkFacebookAccount() },
            onLinkInstagram = { handle -> viewModel.linkInstagramAccount(handle) },
            onUnlinkInstagram = { viewModel.unlinkInstagramAccount() },
            onDismiss = { showSocialModal = false }
        )
    }

    if (showSecurityModal) {
        SecurityArmorDialog(
            auditReport = securityReport,
            isPinEnabled = profile?.isAppLockEnabled ?: false,
            isBiometricEnabled = profile?.isBiometricEnabled ?: false,
            isScreenShieldEnabled = profile?.isScreenShieldEnabled ?: false,
            currentPin = profile?.pinCodeHash ?: "1234",
            onTogglePin = { viewModel.togglePinLock(it) },
            onToggleBiometrics = { viewModel.toggleBiometricLock(it) },
            onToggleScreenShield = { viewModel.toggleScreenShield(it) },
            onChangePin = { viewModel.changePinCode(it) },
            onRunAuditScan = { viewModel.runSecurityScan() },
            onDismiss = { showSecurityModal = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // UI SYSTEM HUD WIDGET: Real-Time Steps & Consumed Water in Liters + GPS Shortcut
        item {
            GmSystemHudWidget(
                liveSteps = liveSteps,
                stepDailyLimit = userDailyStepLimit,
                consumedWaterMl = currentWaterMl,
                targetWaterMl = dailyWaterGoalMl,
                gpsSession = gpsSession,
                locationInfo = locationInfo,
                socialAccountState = socialState,
                securityReport = securityReport,
                onQuickAddWater = { amountMl -> viewModel.logWater(amountMl) },
                onQuickRemoveWater = { amountMl -> viewModel.removeWater(amountMl) },
                onOpenGpsTracker = { viewModel.navigateTo(NavigationScreen.GPS_TRACKER) },
                onOpenLimitSettings = { viewModel.navigateTo(NavigationScreen.GPS_TRACKER) },
                onOpenSocialLinking = { showSocialModal = true },
                onOpenSecurityArmor = { showSecurityModal = true }
            )
        }

        // Hero Athletic Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(170.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    Image(
                        painter = painterResource(id = R.drawable.hero_banner_gym),
                        contentDescription = "Gm Athletic Fitness",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.85f),
                                        Color.Black.copy(alpha = 0.45f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(FlameOrange)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = "Gm Fitness & Bodybuilding",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            if (isTrackerConnected) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(EmeraldNeon.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Watch,
                                        contentDescription = null,
                                        tint = EmeraldNeon,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = viewModel.getString("tracker_connected"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = EmeraldNeon,
                                        fontSize = 10.sp
                                    )
                                }
                            }
                        }

                        Column {
                            Text(
                                text = "${viewModel.getString("welcome_back")}, ${profile?.name ?: "Champion"}!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${viewModel.getString("custom_for_body_type")} ${bodyType.getLocalizedName(currentLang)} • 0.1-3.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // Level & Points Progress Bar Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = currentLevel.iconEmoji,
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (currentLang == AppLanguage.ARABIC) currentLevel.nameArabic else if (currentLang == AppLanguage.INDONESIAN) currentLevel.nameIndonesian else currentLevel.nameEnglish,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = currentLevel.perksArabic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%.1f pts".format(totalPoints),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold
                            )
                            Text(
                                text = "Max: %.0f".format(currentLevel.maxPoints),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    val progress = ((totalPoints - currentLevel.minPoints) / (currentLevel.maxPoints - currentLevel.minPoints))
                        .coerceIn(0f, 1f)

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = AmberGold,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                }
            }
        }

        // Milestone Journey Roadmap Preview Card
        item {
            val allMilestones = viewModel.getMilestones()
            val nextMilestone = allMilestones.firstOrNull { !it.isUnlocked } ?: allMilestones.firstOrNull()
            val unlockedCount = allMilestones.count { it.isUnlocked }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NavigationScreen.REWARDS) }
                    .testTag("home_milestone_journey_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f)),
                border = BorderStroke(1.dp, FlameOrange.copy(alpha = 0.35f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(FlameOrange.copy(alpha = 0.12f), AmberGold.copy(alpha = 0.08f))
                            )
                        )
                        .padding(14.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("🗺️", fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "مسار المحطات الرياضية (Milestones)",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "$unlockedCount من ${allMilestones.size} محطة مكتملة • القوة والتحول والالتزام",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = FlameOrange.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "عرض المسار ➔",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameOrange,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        if (nextMilestone != null) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(nextMilestone.iconEmoji, fontSize = 16.sp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "المحطة التالية: ${nextMilestone.titleArabic}",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = AmberGold
                                    )
                                }
                                Text(
                                    text = "${nextMilestone.progressPercent}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (nextMilestone.isUnlocked) EmeraldNeon else FlameOrange
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { nextMilestone.progressRatio },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = if (nextMilestone.isUnlocked) EmeraldNeon else FlameOrange,
                                trackColor = MaterialTheme.colorScheme.surface
                            )
                        }
                    }
                }
            }
        }

        // Quick Stats Row 1: Calories & Workout Burn
        item {
            Text(
                text = viewModel.getString("today_activity"),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = viewModel.getString("consumed_calories"),
                    value = "$totalCaloriesConsumed",
                    unit = "/ ${macros.dailyCalories} kcal",
                    icon = Icons.Default.Restaurant,
                    accentColor = FlameOrange,
                    modifier = Modifier.weight(1f),
                    subtext = viewModel.getString("target_label")
                )

                QuickStatCard(
                    title = viewModel.getString("calories_burned"),
                    value = "${(caloriesBurnedToday ?: 0) + (liveSteps * 0.04).toInt()}",
                    unit = "kcal",
                    icon = Icons.Default.LocalFireDepartment,
                    accentColor = EmeraldNeon,
                    modifier = Modifier.weight(1f),
                    subtext = "+$liveActiveMinutes min active"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Quick Stats Row 2: Water Tracker & Steps
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                QuickStatCard(
                    title = viewModel.getString("water_tracker_title"),
                    value = "${String.format(Locale.US, "%.2f", currentWaterMl / 1000f)} L",
                    unit = "/ ${String.format(Locale.US, "%.1f", dailyWaterGoalMl / 1000f)} L",
                    icon = Icons.Default.LocalDrink,
                    accentColor = CyanEnergy,
                    modifier = Modifier.weight(1f),
                    subtext = "${(currentWaterMl.toFloat() / dailyWaterGoalMl * 100).toInt()}% ${viewModel.getString("achieved")}"
                )

                QuickStatCard(
                    title = viewModel.getString("steps"),
                    value = String.format(Locale.US, "%,d", liveSteps),
                    unit = "خطوة",
                    icon = Icons.Default.DirectionsWalk,
                    accentColor = AmberGold,
                    modifier = Modifier.weight(1f),
                    subtext = "هدف: ${String.format(Locale.US, "%,d", userDailyStepLimit)}"
                )
            }
        }

        // Weekly Progress Trends Preview Card
        item {
            val weeklySummary = remember(profile, viewModel.weightEntries.collectAsState().value, viewModel.allWaterLogs.collectAsState().value, viewModel.workoutLogs.collectAsState().value, liveSteps) {
                viewModel.getWeeklyTrendsSummary()
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.navigateTo(NavigationScreen.EVOLUTION) },
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📈", fontSize = 18.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = viewModel.getString("weekly_dashboard_title"),
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "تتبع تغير الوزن، شرب الماء، وحرق السعرات",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanEnergy.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "عرض الرسوم ➔",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanEnergy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 3 Mini Trend Pills
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Weight Delta
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("تغير الوزن", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                Text(
                                    text = "${if (weeklySummary.weightDeltaKg > 0) "+" else ""}${weeklySummary.weightDeltaKg} kg",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = if (weeklySummary.weightDeltaKg > 0) CyanEnergy else EmeraldNeon
                                )
                            }
                        }

                        // 2. Water Avg
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("معدل الماء", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                Text(
                                    text = "${String.format(Locale.US, "%.1f", weeklySummary.averageDailyWaterMl / 1000f)} L/d",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanEnergy
                                )
                            }
                        }

                        // 3. Weekly Calories
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(8.dp)
                        ) {
                            Column {
                                Text("حرق الأسبوع", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                                Text(
                                    text = "${weeklySummary.totalCaloriesBurned} kcal",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = FlameOrange
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weekly Fitness Challenges & Rewards
        item {
            WeeklyChallengesCard(viewModel = viewModel)
        }

        // Smart Muscle Recovery Radar
        item {
            MuscleRecoveryRadarCard(viewModel = viewModel)
        }

        // Recommended Daily Workouts for Body Type
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "${viewModel.getString("suggested_workouts")} (${bodyType.getLocalizedName(currentLang)})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = bodyType.workoutFocus,
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameOrange
                    )
                }

                Text(
                    text = viewModel.getString("see_all"),
                    style = MaterialTheme.typography.labelMedium,
                    color = CyanEnergy,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(NavigationScreen.WORKOUTS) }
                        .padding(4.dp)
                )
            }
        }

        items(recommendedWorkouts) { exercise ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onStartWorkout(exercise) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = exercise.getName(currentLang),
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${exercise.environment.arabicTitle} • ${exercise.category.getLocalizedTitle(currentLang)} • ${exercise.defaultSets} ${if (currentLang == AppLanguage.ARABIC) "مجموعات" else "Sets"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            DifficultyBadge(difficultyRating = exercise.difficultyRating)

                            Text(
                                text = "~${exercise.estimatedCaloriesBurned} kcal",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(FlameOrange),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Start Workout",
                            tint = Color.White
                        )
                    }
                }
            }
        }

        // Quick Navigation Buttons
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo(NavigationScreen.NUTRITION) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.Restaurant, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(viewModel.getString("nutrition_nav"), color = MaterialTheme.colorScheme.onSurface)
                }

                Button(
                    onClick = { viewModel.navigateTo(NavigationScreen.GPS_TRACKER) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Icon(Icons.Default.DirectionsRun, contentDescription = null, tint = CyanEnergy, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تتبع GPS والجري", color = MaterialTheme.colorScheme.onSurface)
                }
            }
        }
    }
}
