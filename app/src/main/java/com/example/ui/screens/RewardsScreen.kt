package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BadgeCategory
import com.example.data.BadgeTier
import com.example.data.RewardBadge
import com.example.data.RewardSystem
import com.example.data.StreakDetails
import com.example.ui.GmMainViewModel
import com.example.ui.components.BadgeDetailModal
import com.example.ui.components.CelebrationModal
import com.example.ui.components.MilestoneJourneyRoadmapView
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun RewardsScreen(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val totalPoints = profile?.totalPoints ?: 0f
    val currentLevel = RewardSystem.getCurrentLevel(totalPoints)
    val badges = viewModel.getUnlockedBadges()
    val streakDetails = viewModel.getStreakDetails()
    val selectedBadge by viewModel.selectedBadgeForDetail.collectAsState()
    var selectedScreenTab by remember { mutableIntStateOf(0) }
    val celebrationMessage by viewModel.celebrationRewardMessage.collectAsState()
    val milestones = viewModel.getMilestones()

    var selectedCategoryFilter by remember { mutableStateOf<BadgeCategory?>(null) }

    val filteredBadges = remember(badges, selectedCategoryFilter) {
        if (selectedCategoryFilter == null) badges
        else badges.filter { it.category == selectedCategoryFilter }
    }

    if (celebrationMessage != null) {
        CelebrationModal(
            message = celebrationMessage ?: "",
            onDismiss = { viewModel.dismissCelebrationModal() }
        )
    }

    if (selectedBadge != null) {
        BadgeDetailModal(
            badge = selectedBadge!!,
            onDismiss = { viewModel.selectBadgeForDetail(null) }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("rewards_screen")
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Title & Slogan
        Column {
            Text(
                text = "المحطات وسلاسل التحفيز (Gamification)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "ارفع مستواك الرياضي واجتز محطات القوة والتحول وضاعف نقاطك بالشعلة 🔥",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Navigation Tabs for Milestone Journey vs Streaks & Badges
        TabRow(
            selectedTabIndex = selectedScreenTab,
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            contentColor = FlameOrange,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedScreenTab]),
                    color = FlameOrange,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .testTag("rewards_screen_tabs")
        ) {
            Tab(
                selected = selectedScreenTab == 0,
                onClick = { selectedScreenTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("مسار المحطات", fontWeight = if (selectedScreenTab == 0) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                    }
                },
                selectedContentColor = FlameOrange,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("tab_milestones")
            )
            Tab(
                selected = selectedScreenTab == 1,
                onClick = { selectedScreenTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("الأوسمة والشعلة", fontWeight = if (selectedScreenTab == 1) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                    }
                },
                selectedContentColor = FlameOrange,
                unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.testTag("tab_badges_streaks")
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        if (selectedScreenTab == 0) {
            MilestoneJourneyRoadmapView(
                milestones = milestones,
                onClaimMilestone = { viewModel.claimMilestone(it) },
                modifier = Modifier.fillMaxSize()
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("badges_streaks_list"),
                contentPadding = PaddingValues(top = 4.dp, bottom = 40.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

        // Current Rank & Level Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    FlameOrange.copy(alpha = 0.15f),
                                    AmberGold.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentLevel.iconEmoji, fontSize = 38.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentLevel.nameArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "المستوى ${currentLevel.levelNumber} من ${RewardSystem.levels.size}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = FlameOrange,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "%.1f".format(totalPoints),
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Black,
                                    color = AmberGold
                                )
                                Text("إجمالي نقاط الصعوبة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        val progress = ((totalPoints - currentLevel.minPoints) / (currentLevel.maxPoints - currentLevel.minPoints))
                            .coerceIn(0f, 1f)

                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(RoundedCornerShape(5.dp)),
                            color = AmberGold,
                            trackColor = MaterialTheme.colorScheme.surface
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الحد الأدنى: %.0f".format(currentLevel.minPoints),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "المستوى التالي: %.0f نقطة".format(currentLevel.maxPoints),
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Motivational Streaks Engine Card (The Flame)
        item {
            StreakEngineCard(
                streak = streakDetails,
                onToggleShield = { viewModel.toggleStreakShield() }
            )
        }

        // Difficulty Points Guide Explainer (0.1 to 3.0)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Info, contentDescription = null, tint = CyanEnergy, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "نظام نقاط صعوبة التمارين (0.1 إلى 3.0)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        DifficultyTierBox(
                            pointsRange = "0.1 - 0.9",
                            label = "خفيف / إحماء",
                            color = EmeraldNeon,
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyTierBox(
                            pointsRange = "1.0 - 1.9",
                            label = "متوسط القوة",
                            color = AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                        DifficultyTierBox(
                            pointsRange = "2.0 - 3.0",
                            label = "شديد / أوزان قصوى",
                            color = FlameOrange,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Badges Section Header & Category Filters
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "خزانة الأوسمة (${badges.count { it.isUnlocked }} من ${badges.size} مفتوح)",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اضغط لعرض التفاصيل",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = selectedCategoryFilter == null,
                            onClick = { selectedCategoryFilter = null },
                            label = { Text("الكل (${badges.size})", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberGold.copy(alpha = 0.2f),
                                selectedLabelColor = AmberGold
                            )
                        )
                    }
                    items(BadgeCategory.entries) { cat ->
                        FilterChip(
                            selected = selectedCategoryFilter == cat,
                            onClick = { selectedCategoryFilter = cat },
                            label = { Text("${cat.iconEmoji} ${cat.arabicTitle}", fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AmberGold.copy(alpha = 0.2f),
                                selectedLabelColor = AmberGold
                            )
                        )
                    }
                }
            }
        }

        // Badges List
        items(filteredBadges) { badge ->
            EnhancedBadgeCard(
                badge = badge,
                onClick = { viewModel.selectBadgeForDetail(badge) }
            )
        }
    }
}
}
}

@Composable
fun StreakEngineCard(
    streak: StreakDetails,
    onToggleShield: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("streak_engine_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            FlameOrange.copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(52.dp)
                                .clip(CircleShape)
                                .background(FlameOrange.copy(alpha = 0.2f))
                                .border(2.dp, FlameOrange, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(streak.flameEmoji, fontSize = 28.sp)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "سلسلة الالتزام: ${streak.streakDays} أيام",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Text(
                                text = "المستوى: ${streak.flameTierArabic}",
                                style = MaterialTheme.typography.bodySmall,
                                color = FlameOrange,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(FlameOrange)
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${streak.multiplier}x XP مضاعف 🔥",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Weekly Calendar Activity Dots (Sun to Sat)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val daysLabels = listOf("أحد", "إثن", "ثلا", "أرب", "خمي", "جمع", "سبت")
                    daysLabels.forEachIndexed { index, dayName ->
                        val isActive = streak.activeDaysThisWeek.getOrElse(index) { false }
                        val isToday = index == 4 // Today indicator
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (isActive) FlameOrange else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = if (isToday) 2.dp else 1.dp,
                                        color = if (isToday) EmeraldNeon else if (isActive) FlameOrange else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isActive) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                                } else {
                                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 16.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isToday) EmeraldNeon else MaterialTheme.colorScheme.onSurfaceVariant,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Rest Day Shield (Streak Freeze)
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = null,
                                tint = if (streak.hasStreakShield) CyanEnergy else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "درع حماية السلسلة (Streak Freeze)",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (streak.hasStreakShield) "درع الحماية نشط ليوم الراحة 🛡️" else "غير مفعل (متاح درع 1)",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (streak.hasStreakShield) CyanEnergy else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Switch(
                            checked = streak.hasStreakShield,
                            onCheckedChange = { onToggleShield() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = CyanEnergy
                            ),
                            modifier = Modifier.testTag("streak_shield_switch")
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EnhancedBadgeCard(
    badge: RewardBadge,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tierColor = when (badge.tier) {
        BadgeTier.BRONZE -> Color(0xFFCD7F32)
        BadgeTier.SILVER -> Color(0xFFC0C0C0)
        BadgeTier.GOLD -> AmberGold
        BadgeTier.PLATINUM -> CyanEnergy
        BadgeTier.DIAMOND -> Color(0xFFB388FF)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .testTag("badge_card_${badge.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (badge.isUnlocked) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (badge.isUnlocked) Modifier.border(1.dp, tierColor.copy(alpha = 0.4f), RoundedCornerShape(16.dp)) else Modifier
                )
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(
                            if (badge.isUnlocked) tierColor.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
                        )
                        .border(
                            1.dp,
                            if (badge.isUnlocked) tierColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badge.iconEmoji,
                        fontSize = 26.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = badge.titleArabic,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (badge.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(tierColor.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(badge.tier.arabicTitle, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = tierColor)
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = badge.descriptionArabic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = badge.progressLabelArabic,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (badge.isUnlocked) EmeraldNeon else tierColor,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            if (badge.isUnlocked) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldNeon.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("مكتسب 🏆", style = MaterialTheme.typography.labelSmall, color = EmeraldNeon, fontWeight = FontWeight.Bold)
                }
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(12.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("+${badge.xpReward} XP", style = MaterialTheme.typography.labelSmall, color = AmberGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyTierBox(
    pointsRange: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
            .padding(vertical = 8.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(pointsRange, fontWeight = FontWeight.Bold, color = color, fontSize = 12.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface, fontSize = 10.sp)
        }
    }
}

