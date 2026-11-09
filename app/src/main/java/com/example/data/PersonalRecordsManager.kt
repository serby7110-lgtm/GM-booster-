package com.example.data

import com.example.data.database.PersonalRecordEntity
import com.example.data.database.StrengthLogEntity
import kotlin.math.roundToInt

data class ExercisePrCheckResult(
    val isNewPr: Boolean,
    val previousMaxKg: Float,
    val newMaxKg: Float,
    val estimated1RmKg: Float,
    val improvementPercent: Float
)

object PersonalRecordsManager {

    /**
     * Calculates estimated One Rep Max (1RM) using the validated Epley formula
     */
    fun calculateOneRepMax(weightKg: Float, reps: Int): Float {
        if (reps <= 1) return weightKg
        val calculated = weightKg * (1f + (reps.toFloat() / 30f))
        return (calculated * 10f).roundToInt() / 10f
    }

    /**
     * Evaluates whether a set is a new PR
     */
    fun evaluatePr(
        currentRecord: PersonalRecordEntity?,
        exerciseId: String,
        exerciseNameArabic: String,
        newWeightKg: Float,
        newReps: Int
    ): ExercisePrCheckResult {
        val newEstimated1Rm = calculateOneRepMax(newWeightKg, newReps)
        val previous1Rm = currentRecord?.estimatedOneRepMaxKg ?: 0f
        val previousWeight = currentRecord?.maxWeightKg ?: 0f

        val isPr = newEstimated1Rm > previous1Rm || (newWeightKg > previousWeight && newReps >= (currentRecord?.maxReps ?: 1))

        val improvement = if (previous1Rm > 0f && isPr) {
            (((newEstimated1Rm - previous1Rm) / previous1Rm) * 100f).roundToInt().toFloat()
        } else {
            0f
        }

        return ExercisePrCheckResult(
            isNewPr = isPr,
            previousMaxKg = previousWeight,
            newMaxKg = newWeightKg,
            estimated1RmKg = newEstimated1Rm,
            improvementPercent = improvement
        )
    }

    /**
     * Pre-seeded benchmark records for the trophy room if user is new
     */
    fun getBenchmarkInitialRecords(): List<PersonalRecordEntity> {
        val now = System.currentTimeMillis()
        val dayMillis = 24 * 60 * 60 * 1000L
        return listOf(
            PersonalRecordEntity(
                exerciseId = "ex_barbell_bench_press",
                exerciseNameArabic = "بنش برس بالبار (Barbell Bench Press)",
                maxWeightKg = 85.0f,
                maxReps = 6,
                estimatedOneRepMaxKg = 102.0f,
                previousMaxWeightKg = 80.0f,
                achievedTimestamp = now - 3 * dayMillis
            ),
            PersonalRecordEntity(
                exerciseId = "ex_barbell_squat",
                exerciseNameArabic = "سكوات خلفي بالبار (Barbell Back Squat)",
                maxWeightKg = 110.0f,
                maxReps = 5,
                estimatedOneRepMaxKg = 128.3f,
                previousMaxWeightKg = 100.0f,
                achievedTimestamp = now - 5 * dayMillis
            ),
            PersonalRecordEntity(
                exerciseId = "ex_conventional_deadlift",
                exerciseNameArabic = "الديدليفت التقليدي (Deadlift)",
                maxWeightKg = 135.0f,
                maxReps = 4,
                estimatedOneRepMaxKg = 153.0f,
                previousMaxWeightKg = 125.0f,
                achievedTimestamp = now - 7 * dayMillis
            ),
            PersonalRecordEntity(
                exerciseId = "ex_seated_dumbbell_shoulder_press",
                exerciseNameArabic = "ضغط أكتاف بالدمبلز (Dumbbell Shoulder Press)",
                maxWeightKg = 28.0f,
                maxReps = 8,
                estimatedOneRepMaxKg = 35.5f,
                previousMaxWeightKg = 24.0f,
                achievedTimestamp = now - 2 * dayMillis
            )
        )
    }
}
