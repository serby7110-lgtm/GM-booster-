package com.example.data

import com.example.data.database.StrengthLogEntity

object ProgressiveOverloadEngine {

    /**
     * Calculates the next recommended weight and reps target based on user's recent performance.
     * Uses Double Progression & Linear Periodization principles:
     * - If user hit target top reps (e.g. >= 10 reps on working set), recommend +2.5 kg.
     * - If user used same weight with lower reps, recommend +1 to 2 reps.
     * - Provide actionable Arabic advice for the athlete.
     */
    fun calculateOverloadAdvice(
        exerciseId: String,
        exerciseNameArabic: String,
        recentLogs: List<StrengthLogEntity>,
        targetRepRange: String = "8-12"
    ): ProgressiveOverloadAdvice {
        val exerciseLogs = recentLogs.filter { it.exerciseId == exerciseId }.sortedByDescending { it.dateTimestamp }

        if (exerciseLogs.isEmpty()) {
            return ProgressiveOverloadAdvice(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                lastSessionWeightKg = 0f,
                lastSessionReps = 0,
                lastSessionDate = 0L,
                suggestedWeightKg = 30f,
                suggestedReps = 10,
                adviceMessageArabic = "جلسة أولى لهذا التمرين! اختر وزناً تشعر بالتحكم الكامل به لـ 10 تكرارات بتكنيك نظيف لتأسيس معيار قوتك.",
                isReadyForWeightIncrease = false
            )
        }

        // Find the heaviest / best set from the most recent session
        val latestTimestamp = exerciseLogs.first().dateTimestamp
        val sessionWindowMillis = 2 * 60 * 60 * 1000L // within 2 hours of same session
        val lastSessionSets = exerciseLogs.filter { (latestTimestamp - it.dateTimestamp) < sessionWindowMillis }
        val bestSet = lastSessionSets.maxByOrNull { it.weightKg * (1f + it.reps / 30f) } ?: exerciseLogs.first()

        val lastWeight = bestSet.weightKg
        val lastReps = bestSet.reps

        // Parse rep range (e.g. "8-12", "6-8", "10-12")
        val maxTargetReps = try {
            val parts = targetRepRange.split("-")
            if (parts.size == 2) parts[1].trim().toInt() else 10
        } catch (e: Exception) {
            10
        }

        val minTargetReps = try {
            val parts = targetRepRange.split("-")
            if (parts.size == 2) parts[0].trim().toInt() else 8
        } catch (e: Exception) {
            8
        }

        return if (lastReps >= maxTargetReps && lastWeight > 0f) {
            // Reached ceiling of rep range! Ready for weight bump (+2.5 kg for upper, +5 kg for lower)
            val weightIncrease = if (lastWeight >= 60f) 5.0f else 2.5f
            val newWeight = lastWeight + weightIncrease
            ProgressiveOverloadAdvice(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                lastSessionWeightKg = lastWeight,
                lastSessionReps = lastReps,
                lastSessionDate = latestTimestamp,
                suggestedWeightKg = newWeight,
                suggestedReps = minTargetReps,
                adviceMessageArabic = "وحش! في جلستك السابقة حققت أقصى مدى للتكرارات ($lastReps عدة بـ $lastWeight كجم). حان وقت زيادة الوزن بمقدار +$weightIncrease كجم والبدء بـ $minTargetReps تكرارات! 🚀",
                isReadyForWeightIncrease = true
            )
        } else if (lastWeight > 0f) {
            // In the middle of rep range, push for +1 or +2 reps with the same weight
            val targetNextReps = (lastReps + 1).coerceAtMost(maxTargetReps)
            ProgressiveOverloadAdvice(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                lastSessionWeightKg = lastWeight,
                lastSessionReps = lastReps,
                lastSessionDate = latestTimestamp,
                suggestedWeightKg = lastWeight,
                suggestedReps = targetNextReps,
                adviceMessageArabic = "جلستك السابقة: $lastWeight كجم × $lastReps عدة. هدفك اليوم هو تحقيق $targetNextReps عدة بنفس الوزن مع الحفاظ على زمن تحت الضغط (TUT) 🎯",
                isReadyForWeightIncrease = false
            )
        } else {
            ProgressiveOverloadAdvice(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                lastSessionWeightKg = 0f,
                lastSessionReps = lastReps,
                lastSessionDate = latestTimestamp,
                suggestedWeightKg = 0f,
                suggestedReps = lastReps + 2,
                adviceMessageArabic = "تمرين بوزن الجسم: هدفك زيادة تكرارين مقارنة بالمرة السابقة للوصول لـ ${lastReps + 2} عدة! 🔥",
                isReadyForWeightIncrease = false
            )
        }
    }
}
