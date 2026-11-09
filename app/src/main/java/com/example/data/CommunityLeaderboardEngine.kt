package com.example.data

import java.util.concurrent.TimeUnit

data class LeaderboardMember(
    val rank: Int,
    val id: String,
    val name: String,
    val avatarEmoji: String,
    val bodyType: BodyType,
    val levelName: String,
    val points: Float,
    val streakDays: Int,
    val rankChange: Int, // positive = climbed up, negative = dropped, 0 = stable
    val isCurrentUser: Boolean = false,
    val badgeEmoji: String = "🥇",
    val workoutsCompleted: Int = 12
)

data class LeagueInfo(
    val nameArabic: String,
    val nameEnglish: String,
    val tierIcon: String,
    val minPoints: Float,
    val maxPoints: Float,
    val promotionRankCutoff: Int,
    val demotionRankCutoff: Int,
    val timeRemainingHours: Int = 58
)

data class CommunityChallenge(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val category: String, // STREAK, WORKOUTS, VOLUME, STEPS, WATER, PR
    val iconEmoji: String,
    val targetGoal: Int,
    val currentProgress: Int,
    val unitLabelArabic: String,
    val rewardPoints: Float,
    val participantsCount: Int,
    val daysRemaining: Int,
    val isJoined: Boolean = false,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false
)

object CommunityLeaderboardEngine {

    val leagues = listOf(
        LeagueInfo("دوري المحاربين البرونزي", "Bronze League", "🥉", 0f, 25f, 5, 20),
        LeagueInfo("دوري النخبة الفضي", "Silver League", "🥈", 25.1f, 60f, 4, 18),
        LeagueInfo("دوري الأبطال الذهبي", "Gold League", "🥇", 60.1f, 120f, 3, 15),
        LeagueInfo("دوري الجبابرة الألماسي", "Diamond League", "💎", 120.1f, 250f, 3, 12),
        LeagueInfo("دوري أساطير Gm التيتانيوم", "Legend League", "👑", 250.1f, 9999f, 2, 10)
    )

    fun getCurrentLeague(points: Float): LeagueInfo {
        return leagues.firstOrNull { points in it.minPoints..it.maxPoints } ?: leagues.first()
    }

    private val simulatedCompetitors = listOf(
        Triple("كابتن طارق السعيد", 142.5f, 18),
        Triple("سارة محمد المهندس", 135.0f, 14),
        Triple("فيصل العتيبي", 118.5f, 11),
        Triple("عمر الخالدي", 96.0f, 9),
        Triple("نورة القحطاني", 88.0f, 8),
        Triple("عبدالله الدوسري", 79.5f, 7),
        Triple("ريم الشمري", 67.0f, 6),
        Triple("خالد الزهراني", 54.0f, 5),
        Triple("ياسر المطيري", 46.5f, 4),
        Triple("منى السبيعي", 38.0f, 3),
        Triple("بدر الحازمي", 29.5f, 3),
        Triple("هند الغامدي", 22.0f, 2)
    )

    fun getLeaderboard(
        currentUserPoints: Float,
        currentUserName: String,
        currentUserStreak: Int,
        currentUserBodyType: BodyType = BodyType.MESOMORPH,
        isWeekly: Boolean = true
    ): List<LeaderboardMember> {
        val multiplier = if (isWeekly) 0.35f else 1.0f
        val userScaledPoints = (currentUserPoints * multiplier).coerceAtLeast(4.0f)

        val competitors = simulatedCompetitors.mapIndexed { index, (name, pts, streak) ->
            val scaledPts = pts * multiplier
            val rankChange = when (index % 4) {
                0 -> 1
                1 -> -1
                2 -> 2
                else -> 0
            }
            val avatar = when (index % 6) {
                0 -> "🦁"
                1 -> "⚡"
                2 -> "🐺"
                3 -> "🦅"
                4 -> "🔥"
                else -> "🐯"
            }
            val bType = when (index % 3) {
                0 -> BodyType.MESOMORPH
                1 -> BodyType.ECTOMORPH
                else -> BodyType.ENDOMORPH
            }
            LeaderboardMember(
                rank = 0,
                id = "sim_user_$index",
                name = name,
                avatarEmoji = avatar,
                bodyType = bType,
                levelName = RewardSystem.getCurrentLevel(scaledPts).nameArabic,
                points = scaledPts,
                streakDays = (streak * (if (isWeekly) 1 else 2)).coerceAtLeast(1),
                rankChange = rankChange,
                isCurrentUser = false,
                badgeEmoji = if (index < 3) listOf("👑", "🥈", "🥉")[index] else "⭐",
                workoutsCompleted = (streak * 1.5).toInt().coerceAtLeast(3)
            )
        }.toMutableList()

        // Insert current user
        val userMember = LeaderboardMember(
            rank = 0,
            id = "current_user",
            name = currentUserName.ifBlank { "أنت (بطل Gm)" },
            avatarEmoji = "👑",
            bodyType = currentUserBodyType,
            levelName = RewardSystem.getCurrentLevel(userScaledPoints).nameArabic,
            points = userScaledPoints,
            streakDays = currentUserStreak.coerceAtLeast(1),
            rankChange = 2,
            isCurrentUser = true,
            badgeEmoji = "🔥",
            workoutsCompleted = (currentUserStreak * 2).coerceAtLeast(2)
        )
        competitors.add(userMember)

        // Sort descending by points
        val sorted = competitors.sortedByDescending { it.points }

        return sorted.mapIndexed { index, member ->
            val rank = index + 1
            member.copy(
                rank = rank,
                badgeEmoji = when (rank) {
                    1 -> "👑"
                    2 -> "🥈"
                    3 -> "🥉"
                    else -> member.badgeEmoji
                }
            )
        }
    }

    fun getTopThreePodium(leaderboard: List<LeaderboardMember>): Triple<LeaderboardMember?, LeaderboardMember?, LeaderboardMember?> {
        val first = leaderboard.getOrNull(0)
        val second = leaderboard.getOrNull(1)
        val third = leaderboard.getOrNull(2)
        return Triple(second, first, third) // formatted for (left=2nd, center=1st, right=3rd)
    }

    fun getDefaultChallenges(
        joinedIds: Set<String>,
        claimedIds: Set<String>,
        currentStreak: Int,
        workoutsCount: Int,
        totalPoints: Float
    ): List<CommunityChallenge> {
        val rawChallenges = listOf(
            CommunityChallenge(
                id = "comm_ch_iron_streak_7",
                titleArabic = "تحدي الانضباط الحديدي (7 أيام متتالية)",
                descriptionArabic = "تمرن أو سجل نشاطك لمدة 7 أيام متتالية دون انقطاع لحصد شعلة التميز والمضاعف 1.5x 🔥",
                category = "STREAK",
                iconEmoji = "🔥",
                targetGoal = 7,
                currentProgress = currentStreak.coerceAtMost(7),
                unitLabelArabic = "أيام",
                rewardPoints = 6.0f,
                participantsCount = 3840,
                daysRemaining = 4
            ),
            CommunityChallenge(
                id = "comm_ch_century_sets_50",
                titleArabic = "تحدي الـ 50 مجموعة تدريبية أسبوعياً",
                descriptionArabic = "أكمل 50 مجموعة تمارين في الصالة أو المنزل لدفع عضلاتك نحو النمو الأقصى 💪",
                category = "WORKOUTS",
                iconEmoji = "🏋️",
                targetGoal = 50,
                currentProgress = (workoutsCount * 4).coerceAtMost(50),
                unitLabelArabic = "مجموعة",
                rewardPoints = 7.5f,
                participantsCount = 2910,
                daysRemaining = 5
            ),
            CommunityChallenge(
                id = "comm_ch_volume_titan_10k",
                titleArabic = "تحدي حجم التيتانيوم (10,000 كجم)",
                descriptionArabic = "ارفع حجماً تدريبياً إجمالياً يتجاوز 10 أطنان هذا الأسبوع من خلال التمارين المركبة 🦍",
                category = "VOLUME",
                iconEmoji = "⚡",
                targetGoal = 10000,
                currentProgress = ((workoutsCount * 1800) + (totalPoints.toInt() * 150)).coerceIn(1200, 10000),
                unitLabelArabic = "كجم",
                rewardPoints = 8.0f,
                participantsCount = 1750,
                daysRemaining = 3
            ),
            CommunityChallenge(
                id = "comm_ch_steps_70k",
                titleArabic = "تحدي الـ 70,000 خطوة الأسبوعي",
                descriptionArabic = "حافظ على معدل 10,000 خطوة يومياً لتعزيز صحة القلب وحرق الدهون العنيدة 🏃",
                category = "STEPS",
                iconEmoji = "👟",
                targetGoal = 70000,
                currentProgress = 42500,
                unitLabelArabic = "خطوة",
                rewardPoints = 5.0f,
                participantsCount = 4200,
                daysRemaining = 2
            ),
            CommunityChallenge(
                id = "comm_ch_pr_breaker",
                titleArabic = "تحدي تحطيم رقم قياسي شخصي (New PR)",
                descriptionArabic = "زد وزن تمرين مركب أو عدد العدات وسجل رقماً قياسياً جديداً في سجل أرقامك 🏆",
                category = "PR",
                iconEmoji = "🥇",
                targetGoal = 2,
                currentProgress = 1,
                unitLabelArabic = "أرقام",
                rewardPoints = 6.5f,
                participantsCount = 2100,
                daysRemaining = 6
            )
        )

        return rawChallenges.map { ch ->
            val isJoined = joinedIds.contains(ch.id) || ch.id == "comm_ch_iron_streak_7" // default joined
            val isCompleted = ch.currentProgress >= ch.targetGoal
            val isClaimed = claimedIds.contains(ch.id)
            ch.copy(
                isJoined = isJoined,
                isCompleted = isCompleted,
                isClaimed = isClaimed
            )
        }
    }
}
