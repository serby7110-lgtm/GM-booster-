package com.example.data

import org.json.JSONArray
import org.json.JSONObject

enum class ProgramGoal(val arabicTitle: String, val englishTitle: String, val badgeIcon: String) {
    BULKING("تضخيم وبناء عضلات", "Hypertrophy & Bulking", "💪"),
    STRENGTH("زيادة القوة البدنية", "Pure Strength", "⚡"),
    CUTTING("تنشيف وحرق دهون", "Fat Loss & Cutting", "🔥"),
    LEAN_MASS("كتلة عضلية صافية", "Lean Mass", "🎯"),
    WEAK_POINT("تقوية نقاط الضعف", "Weak Points Focus", "🛡️"),
    FITNESS("لياقة عامة ومرونة", "General Fitness", "🏃")
}

enum class ProgramDifficulty(val arabicTitle: String, val englishTitle: String) {
    BEGINNER("مبتدئ (0 - 6 أشهر)", "Beginner"),
    INTERMEDIATE("متوسط (6 أشهر - سنتين)", "Intermediate"),
    ADVANCED("متقدم (+ سنتين)", "Advanced")
}

data class ProgramDayExerciseItem(
    val exerciseId: String,
    val exerciseNameArabic: String,
    val exerciseNameEnglish: String = "",
    val targetSets: Int = 4,
    val repRange: String = "8-12",
    val restSeconds: Int = 90,
    val targetRpe: Float = 8.0f,
    val notes: String = "",
    val equipmentArabic: String = "أوزان حرة"
) {
    fun toJsonObject(): JSONObject {
        val obj = JSONObject()
        obj.put("exerciseId", exerciseId)
        obj.put("exerciseNameArabic", exerciseNameArabic)
        obj.put("exerciseNameEnglish", exerciseNameEnglish)
        obj.put("targetSets", targetSets)
        obj.put("repRange", repRange)
        obj.put("restSeconds", restSeconds)
        obj.put("targetRpe", targetRpe.toDouble())
        obj.put("notes", notes)
        obj.put("equipmentArabic", equipmentArabic)
        return obj
    }

    companion object {
        fun fromJsonObject(obj: JSONObject): ProgramDayExerciseItem {
            return ProgramDayExerciseItem(
                exerciseId = obj.optString("exerciseId", ""),
                exerciseNameArabic = obj.optString("exerciseNameArabic", ""),
                exerciseNameEnglish = obj.optString("exerciseNameEnglish", ""),
                targetSets = obj.optInt("targetSets", 4),
                repRange = obj.optString("repRange", "8-12"),
                restSeconds = obj.optInt("restSeconds", 90),
                targetRpe = obj.optDouble("targetRpe", 8.0).toFloat(),
                notes = obj.optString("notes", ""),
                equipmentArabic = obj.optString("equipmentArabic", "أوزان حرة")
            )
        }

        fun listToJsonString(list: List<ProgramDayExerciseItem>): String {
            val array = JSONArray()
            list.forEach { array.put(it.toJsonObject()) }
            return array.toString()
        }

        fun listFromJsonString(jsonString: String?): List<ProgramDayExerciseItem> {
            if (jsonString.isNullOrBlank()) return emptyList()
            return try {
                val array = JSONArray(jsonString)
                val result = mutableListOf<ProgramDayExerciseItem>()
                for (i in 0 until array.length()) {
                    val obj = array.optJSONObject(i)
                    if (obj != null) {
                        result.add(fromJsonObject(obj))
                    }
                }
                result
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}

data class WorkoutProgramWithDays(
    val programId: String,
    val titleArabic: String,
    val titleEnglish: String,
    val descriptionArabic: String,
    val goal: ProgramGoal,
    val durationWeeks: Int,
    val daysPerWeek: Int,
    val difficultyLevel: ProgramDifficulty,
    val isCustom: Boolean,
    val iconBadge: String,
    val isCurrentActive: Boolean = false,
    val routineDays: List<ProgramRoutineDayItem> = emptyList()
)

data class ProgramRoutineDayItem(
    val id: String,
    val programId: String,
    val dayNumber: Int,
    val dayTitleArabic: String,
    val dayTitleEnglish: String = "",
    val targetMusclesSummary: String = "",
    val exercises: List<ProgramDayExerciseItem> = emptyList(),
    val estimatedDurationMinutes: Int = 50
)

data class ProgressiveOverloadAdvice(
    val exerciseId: String,
    val exerciseNameArabic: String,
    val lastSessionWeightKg: Float,
    val lastSessionReps: Int,
    val lastSessionDate: Long,
    val suggestedWeightKg: Float,
    val suggestedReps: Int,
    val adviceMessageArabic: String,
    val isReadyForWeightIncrease: Boolean
)
