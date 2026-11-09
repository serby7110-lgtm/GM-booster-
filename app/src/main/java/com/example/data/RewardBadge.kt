package com.example.data

enum class BadgeTier(val arabicTitle: String, val colorHex: Long) {
    BRONZE("برونزي", 0xFFCD7F32),
    SILVER("فضي", 0xFFC0C0C0),
    GOLD("ذهبي", 0xFFFFD700),
    PLATINUM("بلاتيني", 0xFF00E5FF),
    DIAMOND("ألماسي", 0xFFB388FF)
}

enum class BadgeCategory(val arabicTitle: String, val iconEmoji: String) {
    POINTS("مستوى النقاط", "💎"),
    STREAK("الاستمرارية والالتزام", "🔥"),
    WORKOUTS("إنجاز التمارين", "🏋️"),
    PR("الأرقام القياسية", "🏆"),
    TRANSFORMATION("التحول والتغذية", "🥗")
}

data class RewardBadge(
    val id: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val iconEmoji: String,
    val requiredPoints: Float,
    val category: BadgeCategory,
    val isUnlocked: Boolean = false,
    val tier: BadgeTier = BadgeTier.BRONZE,
    val currentProgress: Float = 0f,
    val maxProgress: Float = 1f,
    val progressLabelArabic: String = "",
    val xpReward: Int = 50,
    val unlockedDate: String? = null
)

data class FitnessLevel(
    val levelNumber: Int,
    val nameArabic: String,
    val nameEnglish: String,
    val nameIndonesian: String,
    val minPoints: Float,
    val maxPoints: Float,
    val iconEmoji: String,
    val perksArabic: String
)

data class StreakDetails(
    val streakDays: Int,
    val flameTierArabic: String,
    val flameEmoji: String,
    val multiplier: Float,
    val hasStreakShield: Boolean,
    val nextMilestoneDays: Int,
    val activeDaysThisWeek: List<Boolean>, // 7 days: Sunday to Saturday
    val streakShieldsRemaining: Int = 1
)

object RewardSystem {
    val levels = listOf(
        FitnessLevel(1, "مبتدئ واعد (Novice)", "Promising Novice", "Pemula Berbakat", 0f, 15f, "🌱", "بداية رحلة التحول الصحي وتأسيس اللياقة"),
        FitnessLevel(2, "رياضي طموح (Challenger)", "Ambitious Challenger", "Penantang Ambisius", 15.1f, 40f, "⚡", "بناء العادة اليومية وكسر حواجز الكسل"),
        FitnessLevel(3, "بطل الصالة (Warrior)", "Gym Warrior", "Pendekar Gym", 40.1f, 90f, "🔥", "تطور ملحوظ في الأداء وقوة التحمل العضلي"),
        FitnessLevel(4, "وحش اللياقة (Titan)", "Fitness Titan", "Titan Kebugaran", 90.1f, 180f, "🦍", "أداء استثنائي والتزام حديدي بالتمارين والتغذية"),
        FitnessLevel(5, "أسطورة Gm (Legend)", "Gm Fitness Legend", "Legenda Gm", 180.1f, 9999f, "👑", "القمة في الانضباط والإلهام للمجتمع الرياضي")
    )

    val defaultBadges = listOf(
        RewardBadge(
            id = "badge_first_workout",
            titleArabic = "الخطوة الأولى",
            descriptionArabic = "إكمال أول تمرين بنجاح وحصد أولى النقاط",
            iconEmoji = "🎯",
            requiredPoints = 1.0f,
            category = BadgeCategory.WORKOUTS,
            tier = BadgeTier.BRONZE,
            currentProgress = 1f,
            maxProgress = 1f,
            progressLabelArabic = "1 / 1 تمرين",
            xpReward = 50,
            unlockedDate = "أمس"
        ),
        RewardBadge(
            id = "badge_streak_3",
            titleArabic = "شعلة الحماس (3 أيام)",
            descriptionArabic = "التمرين المتواصل لـ 3 أيام متتالية وتأسيس عادة الانضباط",
            iconEmoji = "🔥",
            requiredPoints = 5.0f,
            category = BadgeCategory.STREAK,
            tier = BadgeTier.BRONZE,
            currentProgress = 3f,
            maxProgress = 3f,
            progressLabelArabic = "3 / 3 أيام متتالية",
            xpReward = 80,
            unlockedDate = "منذ يومين"
        ),
        RewardBadge(
            id = "badge_streak_7",
            titleArabic = "الانضباط الحديدي (7 أيام)",
            descriptionArabic = "الاستمرار لمدة أسبوع كامل دون انقطاع وتفعيل مضاعف 1.5x",
            iconEmoji = "⚔️",
            requiredPoints = 12.0f,
            category = BadgeCategory.STREAK,
            tier = BadgeTier.SILVER,
            currentProgress = 4f,
            maxProgress = 7f,
            progressLabelArabic = "4 / 7 أيام",
            xpReward = 150
        ),
        RewardBadge(
            id = "badge_streak_30",
            titleArabic = "أسطورة الاستمرارية (30 يوماً)",
            descriptionArabic = "الحفاظ على شعلة النشاط لمدة شهر كامل لتصبح قدوة للمجتمع",
            iconEmoji = "👑",
            requiredPoints = 50.0f,
            category = BadgeCategory.STREAK,
            tier = BadgeTier.DIAMOND,
            currentProgress = 4f,
            maxProgress = 30f,
            progressLabelArabic = "4 / 30 يوماً",
            xpReward = 500
        ),
        RewardBadge(
            id = "badge_century_club",
            titleArabic = "نادي المئة جولة (Century Club)",
            descriptionArabic = "إكمال 100 جولة تمرين مسجلة في سجل التدريب",
            iconEmoji = "💯",
            requiredPoints = 25.0f,
            category = BadgeCategory.WORKOUTS,
            tier = BadgeTier.SILVER,
            currentProgress = 38f,
            maxProgress = 100f,
            progressLabelArabic = "38 / 100 جولة",
            xpReward = 200
        ),
        RewardBadge(
            id = "badge_point_collector_10",
            titleArabic = "جامع النقاط الفضي",
            descriptionArabic = "حصد 10 نقاط من صعوبة التمارين (0.1 - 3.0)",
            iconEmoji = "🥈",
            requiredPoints = 10.0f,
            category = BadgeCategory.POINTS,
            tier = BadgeTier.SILVER,
            currentProgress = 10f,
            maxProgress = 10f,
            progressLabelArabic = "10 / 10 نقاط",
            xpReward = 100
        ),
        RewardBadge(
            id = "badge_point_collector_50",
            titleArabic = "جامع النقاط الذهبي",
            descriptionArabic = "حصد 50 نقطة تدريبية بإصرار وعزيمة عالية",
            iconEmoji = "🥇",
            requiredPoints = 50.0f,
            category = BadgeCategory.POINTS,
            tier = BadgeTier.GOLD,
            currentProgress = 12.4f,
            maxProgress = 50f,
            progressLabelArabic = "12.4 / 50 نقطة",
            xpReward = 250
        ),
        RewardBadge(
            id = "badge_volume_titan",
            titleArabic = "سيد الحجم التدريبي (Volume Titan)",
            descriptionArabic = "رفع ما يتجاوز 10,000 كجم حجم إجمالي في أسبوع واحد",
            iconEmoji = "🦍",
            requiredPoints = 20.0f,
            category = BadgeCategory.WORKOUTS,
            tier = BadgeTier.GOLD,
            currentProgress = 6200f,
            maxProgress = 10000f,
            progressLabelArabic = "6,200 / 10,000 كجم",
            xpReward = 300
        ),
        RewardBadge(
            id = "badge_pr_crusher",
            titleArabic = "صائد الأرقام القياسية (PR Crusher)",
            descriptionArabic = "تحطيم 3 أرقام قياسية شخصية بزيادة وزن أو عدات",
            iconEmoji = "⚡",
            requiredPoints = 15.0f,
            category = BadgeCategory.PR,
            tier = BadgeTier.GOLD,
            currentProgress = 2f,
            maxProgress = 3f,
            progressLabelArabic = "2 / 3 أرقام قياسية",
            xpReward = 200
        ),
        RewardBadge(
            id = "badge_max_difficulty",
            titleArabic = "قاهر الصعوبة (3.0)",
            descriptionArabic = "إتمام تمرين بدرجة صعوبة قصوى (3.0 نقاط)",
            iconEmoji = "🔥",
            requiredPoints = 3.0f,
            category = BadgeCategory.WORKOUTS,
            tier = BadgeTier.PLATINUM,
            currentProgress = 3f,
            maxProgress = 3f,
            progressLabelArabic = "مكتمل بنجاح",
            xpReward = 150,
            unlockedDate = "أمس"
        ),
        RewardBadge(
            id = "badge_nutrition_master",
            titleArabic = "شيف اللياقة والترطيب",
            descriptionArabic = "الالتزام بوجبات نوع الجسم وتسجيل 8 أكواب ماء يومياً",
            iconEmoji = "🥗",
            requiredPoints = 15.0f,
            category = BadgeCategory.TRANSFORMATION,
            tier = BadgeTier.SILVER,
            currentProgress = 8f,
            maxProgress = 15f,
            progressLabelArabic = "8 / 15 يوماً",
            xpReward = 120
        ),
        RewardBadge(
            id = "badge_early_bird",
            titleArabic = "طائر الصباح الباكر (Early Bird)",
            descriptionArabic = "أداء تمرين رياضي مكثف قبل الساعة 7 صباحاً",
            iconEmoji = "🌅",
            requiredPoints = 8.0f,
            category = BadgeCategory.WORKOUTS,
            tier = BadgeTier.BRONZE,
            currentProgress = 1f,
            maxProgress = 1f,
            progressLabelArabic = "مكتمل بنجاح",
            xpReward = 80,
            unlockedDate = "هذا الأسبوع"
        )
    )

    fun getCurrentLevel(points: Float): FitnessLevel {
        return levels.firstOrNull { points in it.minPoints..it.maxPoints } ?: levels.last()
    }

    fun calculateStreakDetails(streakDays: Int, hasShieldActive: Boolean): StreakDetails {
        val (tierName, emoji, mult, nextGoal) = when {
            streakDays >= 30 -> Quadruple("أسطوري (Legendary)", "👑", 2.0f, 60)
            streakDays >= 14 -> Quadruple("مستعر (Blazing)", "🌋", 1.8f, 30)
            streakDays >= 7 -> Quadruple("ناري (Inferno)", "🔥", 1.5f, 14)
            streakDays >= 3 -> Quadruple("متوهج (Glowing)", "⚡", 1.2f, 7)
            else -> Quadruple("دافئ (Warm)", "🌱", 1.0f, 3)
        }

        // Active days simulation for Sun to Sat based on current streak
        val daysActive = (0..6).map { dayIndex ->
            // Assume today is index 4 (Thursday/Friday), streak covers preceding days
            dayIndex in 1..4 || (streakDays > 5)
        }

        return StreakDetails(
            streakDays = streakDays,
            flameTierArabic = tierName,
            flameEmoji = emoji,
            multiplier = mult,
            hasStreakShield = hasShieldActive,
            nextMilestoneDays = nextGoal,
            activeDaysThisWeek = daysActive,
            streakShieldsRemaining = if (hasShieldActive) 0 else 1
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
