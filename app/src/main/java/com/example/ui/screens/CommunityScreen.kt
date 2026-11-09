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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CommunityChallenge
import com.example.data.CommunityLeaderboardEngine
import com.example.data.CommunityPost
import com.example.data.LeaderboardMember
import com.example.ui.GmMainViewModel
import com.example.ui.components.CelebrationModal
import com.example.ui.components.CreatePostDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

enum class CommunityTab(val titleArabic: String, val iconEmoji: String) {
    CHALLENGES("تحديات المجتمع", "🏆"),
    LEADERBOARD("لوحة المتصدرين", "👑"),
    FEED("حائط الأبطال", "💬")
}

@Composable
fun CommunityScreen(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val celebrationMessage by viewModel.celebrationRewardMessage.collectAsState()
    var showCreatePostDialog by remember { mutableStateOf(false) }

    if (celebrationMessage != null) {
        CelebrationModal(
            message = celebrationMessage ?: "",
            onDismiss = { viewModel.dismissCelebrationModal() }
        )
    }

    if (showCreatePostDialog) {
        CreatePostDialog(
            onDismiss = { showCreatePostDialog = false },
            onPublish = { content, tag ->
                viewModel.publishCommunityPost(content, tag)
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("community_screen")
    ) {
        // Top Modern Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = EmeraldNeon,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = EmeraldNeon,
                    height = 3.dp
                )
            }
        ) {
            CommunityTab.entries.forEachIndexed { index, tab ->
                Tab(
                    selected = selectedTabIndex == index,
                    onClick = { selectedTabIndex = index },
                    modifier = Modifier.testTag("community_tab_$index"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(tab.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = tab.titleArabic,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedTabIndex == index) EmeraldNeon else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTabIndex) {
                0 -> ChallengesTabContent(viewModel = viewModel)
                1 -> LeaderboardTabContent(viewModel = viewModel)
                2 -> FeedTabContent(
                    viewModel = viewModel,
                    onCreatePost = { showCreatePostDialog = true }
                )
            }
        }
    }
}

@Composable
fun ChallengesTabContent(
    viewModel: GmMainViewModel
) {
    val challenges = viewModel.getCommunityChallenges()
    val joinedCount = challenges.count { it.isJoined }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }

    val filteredChallenges = remember(challenges, selectedCategoryFilter) {
        if (selectedCategoryFilter == "ALL") challenges
        else challenges.filter { it.category == selectedCategoryFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Challenges Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    EmeraldNeon.copy(alpha = 0.15f),
                                    CyanEnergy.copy(alpha = 0.08f)
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
                            Column {
                                Text(
                                    text = "تحديات اللياقة الأسبوعية 🏆",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "شارك مع آلاف الأبطال وحقق نقاط صعوبة إضافية وشارات حصرية",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            StatPill(
                                label = "التحديات النشطة",
                                value = "${challenges.size}",
                                color = EmeraldNeon,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                label = "أنت منضم إلى",
                                value = "$joinedCount تحديات",
                                color = AmberGold,
                                modifier = Modifier.weight(1f)
                            )
                            StatPill(
                                label = "المشاركون الإجمالي",
                                value = "+12,000",
                                color = CyanEnergy,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }
            }
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                val filters = listOf(
                    "ALL" to "الكل 🌟",
                    "STREAK" to "الاستمرارية 🔥",
                    "WORKOUTS" to "التمارين 🏋️",
                    "VOLUME" to "الحجم التدريبي 🦍",
                    "STEPS" to "الخطوات 👟",
                    "PR" to "الأرقام القياسية 🥇"
                )
                items(filters) { (key, label) ->
                    FilterChip(
                        selected = selectedCategoryFilter == key,
                        onClick = { selectedCategoryFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = EmeraldNeon.copy(alpha = 0.2f),
                            selectedLabelColor = EmeraldNeon
                        )
                    )
                }
            }
        }

        // Challenge Cards
        items(filteredChallenges) { challenge ->
            CommunityChallengeCard(
                challenge = challenge,
                onToggleJoin = { viewModel.toggleJoinChallenge(challenge.id) },
                onClaimReward = { viewModel.claimCommunityChallenge(challenge) }
            )
        }
    }
}

@Composable
fun CommunityChallengeCard(
    challenge: CommunityChallenge,
    onToggleJoin: () -> Unit,
    onClaimReward: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("challenge_card_${challenge.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Icon, Title, Days remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldNeon.copy(alpha = 0.15f))
                            .border(1.dp, EmeraldNeon.copy(alpha = 0.3f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(challenge.iconEmoji, fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = challenge.titleArabic,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Group,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${challenge.participantsCount} مشارك",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FlameOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = FlameOrange
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "باقي ${challenge.daysRemaining} أيام",
                            style = MaterialTheme.typography.labelSmall,
                            color = FlameOrange,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = challenge.descriptionArabic,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Progress bar
            val progressFraction = (challenge.currentProgress.toFloat() / challenge.targetGoal.toFloat())
                .coerceIn(0f, 1f)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "التقدم: ${challenge.currentProgress} / ${challenge.targetGoal} ${challenge.unitLabelArabic}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${(progressFraction * 100).toInt()}%",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (challenge.isCompleted) EmeraldNeon else AmberGold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (challenge.isCompleted) EmeraldNeon else AmberGold,
                trackColor = MaterialTheme.colorScheme.surface
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Footer: Reward points & Action button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(AmberGold.copy(alpha = 0.15f))
                        .border(1.dp, AmberGold.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "+${challenge.rewardPoints} نقطة صعوبة 💎",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = AmberGold
                    )
                }

                when {
                    challenge.isClaimed -> {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("تم استلام المكافأة", style = MaterialTheme.typography.labelMedium, color = EmeraldNeon)
                            }
                        }
                    }
                    challenge.isCompleted -> {
                        Button(
                            onClick = onClaimReward,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                            modifier = Modifier.testTag("claim_challenge_${challenge.id}")
                        ) {
                            Text("استلم المكافأة 🎁", fontWeight = FontWeight.Bold, color = Color.Black)
                        }
                    }
                    challenge.isJoined -> {
                        OutlinedButton(
                            onClick = onToggleJoin,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("leave_challenge_${challenge.id}")
                        ) {
                            Text("أنت مشارك ✓", fontWeight = FontWeight.Bold, color = EmeraldNeon)
                        }
                    }
                    else -> {
                        Button(
                            onClick = onToggleJoin,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier.testTag("join_challenge_${challenge.id}")
                        ) {
                            Text("انضم بالتحدي 🚀", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardTabContent(
    viewModel: GmMainViewModel
) {
    val isWeekly by viewModel.isLeaderboardWeekly.collectAsState()
    val leaderboard = viewModel.getLeaderboard()
    val topThree = CommunityLeaderboardEngine.getTopThreePodium(leaderboard)
    val userProfile by viewModel.userProfile.collectAsState()
    val currentPoints = userProfile?.totalPoints ?: 12.4f
    val currentLeague = CommunityLeaderboardEngine.getCurrentLeague(currentPoints)

    val currentUserMember = leaderboard.firstOrNull { it.isCurrentUser }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // League Header Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        AmberGold.copy(alpha = 0.2f),
                                        FlameOrange.copy(alpha = 0.1f)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(currentLeague.tierIcon, fontSize = 36.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = currentLeague.nameArabic,
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = "تصفية الترتيب الأسبوعي خلال 2 يوم 14 ساعة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.surface)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "ترقية Top ${currentLeague.promotionRankCutoff} ⚡",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon
                                )
                            }
                        }
                    }
                }
            }

            // Weekly vs All-Time Toggle
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Row(modifier = Modifier.padding(4.dp)) {
                            FilterChip(
                                selected = isWeekly,
                                onClick = { viewModel.setLeaderboardFilter(true) },
                                label = { Text("المتصدرون هذا الأسبوع", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldNeon,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("leaderboard_weekly_toggle")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            FilterChip(
                                selected = !isWeekly,
                                onClick = { viewModel.setLeaderboardFilter(false) },
                                label = { Text("الترتيب التاريخي الشامل", fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = EmeraldNeon,
                                    selectedLabelColor = Color.Black
                                ),
                                modifier = Modifier.testTag("leaderboard_alltime_toggle")
                            )
                        }
                    }
                }
            }

            // Visual Podium (Top 3)
            item {
                LeaderboardPodium(
                    first = topThree.second,
                    second = topThree.first,
                    third = topThree.third
                )
            }

            // Remaining Contenders Header
            item {
                Text(
                    text = "باقي أبطال الدوري",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // List of other contenders
            val remainingMembers = leaderboard.drop(3)
            items(remainingMembers) { member ->
                LeaderboardMemberRow(member = member)
            }
        }

        // Sticky User Rank Bar at bottom
        if (currentUserMember != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .testTag("sticky_user_rank_bar"),
                color = MaterialTheme.colorScheme.surfaceVariant,
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    EmeraldNeon.copy(alpha = 0.2f),
                                    CyanEnergy.copy(alpha = 0.1f)
                                )
                            )
                        )
                        .border(1.dp, EmeraldNeon.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldNeon),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("#${currentUserMember.rank}", fontWeight = FontWeight.Black, color = Color.Black, fontSize = 16.sp)
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = currentUserMember.name,
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldNeon.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("أنت", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                                    }
                                }
                                Text(
                                    text = "شعلة ${currentUserMember.streakDays} أيام 🔥 • ${currentUserMember.workoutsCompleted} تمرين",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "%.1f ن".format(currentUserMember.points),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = AmberGold
                            )
                            Text(
                                text = "الترتيب الحالي",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardPodium(
    first: LeaderboardMember?,
    second: LeaderboardMember?,
    third: LeaderboardMember?
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "منصة الأبطال المتصدرين 👑",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                // 2nd Place (Left)
                PodiumColumn(
                    member = second,
                    rank = 2,
                    pedestalHeight = 110.dp,
                    crownColor = Color(0xFFC0C0C0),
                    crownEmoji = "🥈",
                    modifier = Modifier.weight(1f)
                )

                // 1st Place (Center - Tallest)
                PodiumColumn(
                    member = first,
                    rank = 1,
                    pedestalHeight = 145.dp,
                    crownColor = AmberGold,
                    crownEmoji = "👑",
                    modifier = Modifier.weight(1.1f)
                )

                // 3rd Place (Right)
                PodiumColumn(
                    member = third,
                    rank = 3,
                    pedestalHeight = 90.dp,
                    crownColor = Color(0xFFCD7F32),
                    crownEmoji = "🥉",
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
fun PodiumColumn(
    member: LeaderboardMember?,
    rank: Int,
    pedestalHeight: androidx.compose.ui.unit.Dp,
    crownColor: Color,
    crownEmoji: String,
    modifier: Modifier = Modifier
) {
    if (member == null) return

    Column(
        modifier = modifier.padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(crownEmoji, fontSize = if (rank == 1) 28.sp else 22.sp)

        Box(
            modifier = Modifier
                .size(if (rank == 1) 56.dp else 48.dp)
                .clip(CircleShape)
                .background(crownColor.copy(alpha = 0.2f))
                .border(2.dp, crownColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(member.avatarEmoji, fontSize = if (rank == 1) 28.sp else 22.sp)
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = member.name.split(" ").take(2).joinToString(" "),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            textAlign = TextAlign.Center
        )

        Text(
            text = "%.1f ن".format(member.points),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Black,
            color = AmberGold
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Pedestal block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(pedestalHeight)
                .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            crownColor.copy(alpha = 0.35f),
                            MaterialTheme.colorScheme.surface
                        )
                    )
                )
                .border(1.dp, crownColor.copy(alpha = 0.4f), RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "#$rank",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    color = crownColor
                )
                Text(
                    text = "${member.streakDays}d 🔥",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun LeaderboardMemberRow(
    member: LeaderboardMember,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("leaderboard_row_${member.rank}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (member.isCurrentUser) MaterialTheme.colorScheme.surfaceVariant else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (member.isCurrentUser) Modifier.border(1.dp, EmeraldNeon, RoundedCornerShape(14.dp)) else Modifier
                )
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                // Rank number
                Text(
                    text = "#${member.rank}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.width(36.dp)
                )

                // Avatar
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.surface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(member.avatarEmoji, fontSize = 20.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = member.name,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        if (member.isCurrentUser) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(EmeraldNeon.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("أنت", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                            }
                        }
                    }
                    Text(
                        text = "${member.levelName} • شعلة ${member.streakDays} أيام 🔥",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Rank change & Points
            Row(verticalAlignment = Alignment.CenterVertically) {
                when {
                    member.rankChange > 0 -> {
                        Text("▲ +${member.rankChange}", style = MaterialTheme.typography.labelSmall, color = EmeraldNeon, fontWeight = FontWeight.Bold)
                    }
                    member.rankChange < 0 -> {
                        Text("▼ ${member.rankChange}", style = MaterialTheme.typography.labelSmall, color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                    }
                    else -> {
                        Text("—", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "%.1f".format(member.points),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = AmberGold
                    )
                    Text("نقطة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun FeedTabContent(
    viewModel: GmMainViewModel,
    onCreatePost: () -> Unit
) {
    val posts by viewModel.communityPosts.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Create Post Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(EmeraldNeon.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PostAdd, contentDescription = null, tint = EmeraldNeon)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "شارك إنجازك الرياضي اليوم 🚀",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ألهم أبطال المجتمع بصورة أو تمرين",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Button(
                        onClick = onCreatePost,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                        modifier = Modifier.testTag("open_create_post_btn")
                    ) {
                        Text("نشر", fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }
        }

        items(posts) { post ->
            CommunityPostCard(
                post = post,
                onLike = { viewModel.togglePostLike(post.id) },
                onAddComment = { text -> viewModel.addCommentToPost(post.id, text) }
            )
        }
    }
}

@Composable
fun CommunityPostCard(
    post: CommunityPost,
    onLike: () -> Unit,
    onAddComment: (String) -> Unit
) {
    var isExpandedComments by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Author info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("💪", fontSize = 22.sp)
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = post.authorName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "${post.authorLevelName} • ${post.timeAgoArabic}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(EmeraldNeon.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("+%.1f ن".format(post.pointsGained), style = MaterialTheme.typography.labelSmall, color = EmeraldNeon, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Content
            Text(
                text = post.contentArabic,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                lineHeight = 22.sp
            )

            // Workout tag
            if (!post.workoutTagArabic.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(CyanEnergy.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = CyanEnergy, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(post.workoutTagArabic, style = MaterialTheme.typography.labelSmall, color = CyanEnergy, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action row: Likes & Comments toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onLike() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (post.isLikedByMe) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "إعجاب",
                        tint = if (post.isLikedByMe) Color(0xFFFF5252) else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} إعجاب",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (post.isLikedByMe) Color(0xFFFF5252) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { isExpandedComments = !isExpandedComments }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Default.ChatBubbleOutline, contentDescription = "تعليقات", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("${post.comments.size} تعليق", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            // Expanded comments section
            AnimatedVisibility(visible = isExpandedComments) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(10.dp))

                    post.comments.forEach { comment ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(comment.authorName, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                                Text(comment.textArabic, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                            }
                            Text(comment.timeAgoArabic, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = { Text("اكتب تشجيعاً للبطل...", fontSize = 12.sp) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    onAddComment(commentInput)
                                    commentInput = ""
                                }
                            }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "إرسال", tint = EmeraldNeon)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatPill(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, fontWeight = FontWeight.Black, color = color, fontSize = 14.sp)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}
