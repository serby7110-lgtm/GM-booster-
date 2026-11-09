package com.example.data

data class DayTrendStat(
    val dayIndex: Int, // 0 to 6 (e.g. 6 days ago .. today)
    val dayNameShortAr: String,
    val dayNameShortEn: String,
    val dayNameShortId: String,
    val dateFormatted: String,
    val timestamp: Long,
    val recordedWeightKg: Float?,
    val waterIntakeMl: Int,
    val dailyWaterGoalMl: Int,
    val caloriesBurned: Int,
    val dailyCalorieGoal: Int,
    val workoutCount: Int,
    val isToday: Boolean = false
) {
    fun getDayName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> dayNameShortAr
        AppLanguage.ENGLISH -> dayNameShortEn
        AppLanguage.INDONESIAN -> dayNameShortId
    }

    val waterGoalPercentage: Float
        get() = if (dailyWaterGoalMl > 0) (waterIntakeMl.toFloat() / dailyWaterGoalMl).coerceIn(0f, 2f) else 0f

    val isWaterGoalMet: Boolean
        get() = waterIntakeMl >= dailyWaterGoalMl

    val isCalorieGoalMet: Boolean
        get() = caloriesBurned >= dailyCalorieGoal
}

data class WeeklyProgressSummary(
    val dayStats: List<DayTrendStat>,
    val averageWeightKg: Float,
    val startWeightKg: Float,
    val endWeightKg: Float,
    val weightDeltaKg: Float,
    val targetWeightKg: Float,
    val totalWaterMl: Int,
    val averageDailyWaterMl: Int,
    val waterGoalMetDays: Int,
    val dailyWaterGoalMl: Int,
    val totalCaloriesBurned: Int,
    val averageDailyCaloriesBurned: Int,
    val peakBurnCalories: Int,
    val peakBurnDayNameAr: String,
    val peakBurnDayNameEn: String,
    val peakBurnDayNameId: String,
    val totalWorkouts: Int,
    val adherenceScorePercent: Int,
    val weightPaceStatusAr: String,
    val weightPaceStatusEn: String,
    val weightPaceStatusId: String
) {
    fun getPeakBurnDayName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> peakBurnDayNameAr
        AppLanguage.ENGLISH -> peakBurnDayNameEn
        AppLanguage.INDONESIAN -> peakBurnDayNameId
    }

    fun getWeightPaceStatus(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> weightPaceStatusAr
        AppLanguage.ENGLISH -> weightPaceStatusEn
        AppLanguage.INDONESIAN -> weightPaceStatusId
    }
}
