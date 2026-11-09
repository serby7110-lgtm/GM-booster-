package com.example.data

import com.example.data.database.WorkoutLogEntity

enum class MuscleGroup(
    val id: String,
    val arabicName: String,
    val englishName: String,
    val baseRecoveryHours: Int,
    val category: String // UPPER, LOWER, CORE, ARMS
) {
    CHEST("CHEST", "الصدر", "Chest", 48, "UPPER"),
    BACK("BACK", "الظهر والمجنص", "Back & Lats", 48, "UPPER"),
    LEGS_QUADS("LEGS_QUADS", "الفخذ الأمامي (الكوادس)", "Quads", 72, "LOWER"),
    LEGS_HAMSTRINGS("LEGS_HAMSTRINGS", "الفخذ الخلفي والمؤخرة", "Hamstrings & Glutes", 72, "LOWER"),
    CALVES("CALVES", "عضلات السمانة (البطات)", "Calves", 36, "LOWER"),
    SHOULDERS("SHOULDERS", "الأكتاف (أمامي/جانبي/خلفي)", "Shoulders", 48, "UPPER"),
    BICEPS("BICEPS", "البايسبس (عضلات الذراع)", "Biceps", 36, "ARMS"),
    TRICEPS("TRICEPS", "الترايسبس (عضلات الذراع)", "Triceps", 36, "ARMS"),
    CORE("CORE", "عضلات البطن والكور", "Core & Abs", 24, "CORE")
}

enum class RecoveryStatus(val labelArabic: String, val colorCode: Long) {
    READY("جاهز 100% للتمرين ⚡", 0xFF00E676),
    RECOVERING("في طور الاستشفاء 🟡", 0xFFFFD700),
    FATIGUED("مجهد وبحاجة لراحة 🛑", 0xFFFF5722)
}

data class MuscleRecoveryState(
    val muscleGroup: MuscleGroup,
    val recoveryPercentage: Int, // 0 to 100
    val hoursSinceLastWorkout: Float,
    val status: RecoveryStatus,
    val lastTrainedExercise: String?,
    val adviceArabic: String
)

object MuscleRecoveryAdvisor {

    fun mapCategoryToMuscles(categoryName: String): List<MuscleGroup> {
        return when (categoryName.uppercase()) {
            "CHEST" -> listOf(MuscleGroup.CHEST, MuscleGroup.TRICEPS, MuscleGroup.SHOULDERS)
            "BACK" -> listOf(MuscleGroup.BACK, MuscleGroup.BICEPS)
            "LEGS" -> listOf(MuscleGroup.LEGS_QUADS, MuscleGroup.LEGS_HAMSTRINGS, MuscleGroup.CALVES)
            "SHOULDERS" -> listOf(MuscleGroup.SHOULDERS, MuscleGroup.TRICEPS)
            "ARMS" -> listOf(MuscleGroup.BICEPS, MuscleGroup.TRICEPS)
            "ABS_CORE" -> listOf(MuscleGroup.CORE)
            "HIIT_CARDIO", "FULL_BODY" -> listOf(MuscleGroup.LEGS_QUADS, MuscleGroup.CORE, MuscleGroup.CHEST)
            else -> listOf(MuscleGroup.CHEST, MuscleGroup.CORE)
        }
    }

    fun calculateRecoveryStates(workoutLogs: List<WorkoutLogEntity>): List<MuscleRecoveryState> {
        val now = System.currentTimeMillis()
        val allMuscles = MuscleGroup.values()

        return allMuscles.map { muscle ->
            // Find most recent workout that impacted this muscle
            val relevantLog = workoutLogs.firstOrNull { log ->
                val impacted = mapCategoryToMuscles(log.category)
                impacted.contains(muscle)
            }

            if (relevantLog == null) {
                // Not trained recently -> fully ready
                MuscleRecoveryState(
                    muscleGroup = muscle,
                    recoveryPercentage = 100,
                    hoursSinceLastWorkout = 999f,
                    status = RecoveryStatus.READY,
                    lastTrainedExercise = null,
                    adviceArabic = "العضلة متعافية تماماً وفي أعلى درجات الجاهزية للتمرين القوي."
                )
            } else {
                val diffHours = ((now - relevantLog.dateTimestamp) / (1000f * 60f * 60f)).coerceAtLeast(0f)
                val totalHoursNeeded = muscle.baseRecoveryHours.toFloat()
                val progress = (diffHours / totalHoursNeeded).coerceIn(0f, 1f)
                val percentage = (progress * 100).toInt()

                val status = when {
                    percentage >= 90 -> RecoveryStatus.READY
                    percentage >= 45 -> RecoveryStatus.RECOVERING
                    else -> RecoveryStatus.FATIGUED
                }

                val advice = when (status) {
                    RecoveryStatus.READY -> "جاهزية كاملة! العضلة استعادت مخازن الجليكوجين وأليافها جاهزة للتحطيم."
                    RecoveryStatus.RECOVERING -> "العضلة في مرحلة بناء البروتين، يمكن تدريبها بجهد خفيف أو التركيز على مجموعة عضلية أخرى."
                    RecoveryStatus.FATIGUED -> "تحذير: لا ينصح بتدريب هذه العضلة اليوم لحمايتها من الهدم العضلي والإصابات."
                }

                MuscleRecoveryState(
                    muscleGroup = muscle,
                    recoveryPercentage = percentage,
                    hoursSinceLastWorkout = diffHours,
                    status = status,
                    lastTrainedExercise = relevantLog.exerciseNameArabic,
                    adviceArabic = advice
                )
            }
        }
    }
}
