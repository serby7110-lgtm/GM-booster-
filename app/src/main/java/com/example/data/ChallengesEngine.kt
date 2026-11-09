package com.example.data

import com.example.data.database.DailyStepLogEntity
import com.example.data.database.UserProfileEntity
import com.example.data.database.WaterLogEntity
import com.example.data.database.WeeklyChallengeEntity
import com.example.data.database.WorkoutLogEntity

object ChallengesEngine {

    fun generateDefaultChallenges(): List<WeeklyChallengeEntity> {
        val now = System.currentTimeMillis()
        val weekMillis = 7 * 24 * 60 * 60 * 1000L
        val expiry = now + weekMillis

        return listOf(
            WeeklyChallengeEntity(
                challengeId = "ch_streak_7",
                titleArabic = "تحدي الالتزام الأسبوعي الناري",
                descriptionArabic = "تمرّن أو سجّل نشاطك لـ 5 أيام خلال هذا الأسبوع للمحافظة على الشعلة 🔥",
                category = "STREAK",
                targetGoal = 5,
                currentProgress = 3,
                rewardPoints = 5.0f,
                iconName = "local_fire_department",
                isCompleted = false,
                isClaimed = false,
                expiryTimestamp = expiry
            ),
            WeeklyChallengeEntity(
                challengeId = "ch_workouts_volume",
                titleArabic = "تحدي إنجاز 4 جلسات تدريبية",
                descriptionArabic = "أكمل 4 تمارين مختلفة بنجاح في النادي أو المنزل هذا الأسبوع 💪",
                category = "WORKOUTS",
                targetGoal = 4,
                currentProgress = 2,
                rewardPoints = 4.0f,
                iconName = "fitness_center",
                isCompleted = false,
                isClaimed = false,
                expiryTimestamp = expiry
            ),
            WeeklyChallengeEntity(
                challengeId = "ch_water_mastery",
                titleArabic = "تحدي الترطيب الفائق (Hydration Hero)",
                descriptionArabic = "سجّل شرب ما لا يقل عن 8 أكواب ماء (2 لتر) لـ 4 أيام هذا الأسبوع 💧",
                category = "WATER",
                targetGoal = 4,
                currentProgress = 3,
                rewardPoints = 3.5f,
                iconName = "local_drink",
                isCompleted = false,
                isClaimed = false,
                expiryTimestamp = expiry
            ),
            WeeklyChallengeEntity(
                challengeId = "ch_calories_burned",
                titleArabic = "تحدي حرق 1500 سعرة حرارية",
                descriptionArabic = "احرق ما مجموعه 1500 سعرة من خلال التمارين والجري هذا الأسبوع ⚡",
                category = "CALORIES",
                targetGoal = 1500,
                currentProgress = 850,
                rewardPoints = 6.0f,
                iconName = "bolt",
                isCompleted = false,
                isClaimed = false,
                expiryTimestamp = expiry
            ),
            WeeklyChallengeEntity(
                challengeId = "ch_steps_weekly",
                titleArabic = "تحدي الـ 40,000 خطوة أسبوعياً",
                descriptionArabic = "تحرك وابق نشيطاً للوصول إلى 40 ألف خطوة في الأسبوع 🏃",
                category = "STEPS",
                targetGoal = 40000,
                currentProgress = 24500,
                rewardPoints = 4.5f,
                iconName = "directions_run",
                isCompleted = false,
                isClaimed = false,
                expiryTimestamp = expiry
            ),
            WeeklyChallengeEntity(
                challengeId = "ch_pr_crusher",
                titleArabic = "تحدي تحطيم رقم قياسي شخصي (Break a PR)",
                descriptionArabic = "حطّم رقمك السابق في أي تمرين (زيادة وزن أو عدات) 🏆",
                category = "PR",
                targetGoal = 1,
                currentProgress = 1,
                rewardPoints = 5.5f,
                iconName = "emoji_events",
                isCompleted = true,
                isClaimed = false,
                expiryTimestamp = expiry
            )
        )
    }

    fun syncChallengesWithActivity(
        currentChallenges: List<WeeklyChallengeEntity>,
        profile: UserProfileEntity?,
        workoutLogs: List<WorkoutLogEntity>,
        totalCaloriesBurnedToday: Int,
        liveSteps: Int
    ): List<WeeklyChallengeEntity> {
        val streak = profile?.streakDays ?: 0
        val workoutsCount = workoutLogs.size.coerceAtLeast(1)

        return currentChallenges.map { ch ->
            val updatedProgress = when (ch.category) {
                "STREAK" -> streak.coerceAtMost(ch.targetGoal)
                "WORKOUTS" -> workoutsCount.coerceAtMost(ch.targetGoal)
                "CALORIES" -> (ch.currentProgress + (totalCaloriesBurnedToday / 10)).coerceAtMost(ch.targetGoal)
                "STEPS" -> (ch.currentProgress + liveSteps).coerceAtMost(ch.targetGoal)
                else -> ch.currentProgress
            }
            val completed = updatedProgress >= ch.targetGoal
            ch.copy(
                currentProgress = updatedProgress,
                isCompleted = completed
            )
        }
    }
}
