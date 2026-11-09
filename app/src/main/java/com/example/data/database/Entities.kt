package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserProfileEntity(
    @PrimaryKey val id: Int = 1,
    val name: String = "بطل Gm",
    val gender: String = "ذكر",
    val age: Int = 24,
    val heightCm: Float = 178f,
    val currentWeightKg: Float = 74.5f,
    val targetWeightKg: Float = 78.0f,
    val bodyType: String = "ECTOMORPH", // ECTOMORPH, MESOMORPH, ENDOMORPH
    val activityLevel: String = "MODERATE",
    val fitnessGoal: String = "BUILD_MUSCLE",
    val totalPoints: Float = 12.4f,
    val streakDays: Int = 4,
    val waterGlasses: Int = 5,
    val targetWaterGlasses: Int = 8,
    val stepDailyLimit: Int = 10000,
    val distanceDailyLimitKm: Float = 5.0f,
    val lastWorkoutDate: Long = System.currentTimeMillis(),
    val isCloudSynced: Boolean = true,
    val workoutRemindersEnabled: Boolean = true,
    val reminderHour: Int = 18,
    val reminderMinute: Int = 30,
    val waterRemindersEnabled: Boolean = true,
    val selectedLanguage: String = "ar",
    // Social Accounts Linking
    val isGoogleLinked: Boolean = true,
    val googleEmail: String = "serby7110@gmail.com",
    val isFacebookLinked: Boolean = false,
    val facebookName: String = "",
    val isInstagramLinked: Boolean = false,
    val instagramHandle: String = "",
    // Security & Anti-Tamper Armor
    val isAppLockEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val pinCodeHash: String = "1234",
    val isScreenShieldEnabled: Boolean = false,
    val antiTamperLevel: String = "MAXIMUM",
    val securityScore: Int = 98,
    // Location Data
    val lastKnownLocationName: String = "الرياض، المملكة العربية السعودية",
    val lastKnownLatitude: Double = 24.7136,
    val lastKnownLongitude: Double = 46.6753,
    val isAutoLocationEnabled: Boolean = true,
    // Nutrition Macro Targets (calculated flexibly)
    val targetDailyCalories: Int = 2450,
    val targetProteinGrams: Int = 165,
    val targetCarbsGrams: Int = 265,
    val targetFatGrams: Int = 65,
    // Weak Points Focus & Preferences
    val weakPointsJson: String = "[\"LEGS_QUADS\", \"BICEPS\"]",
    // Claimed Milestones (Comma separated milestone IDs)
    val claimedMilestonesString: String = ""
) {
    fun getClaimedMilestonesSet(): Set<String> =
        if (claimedMilestonesString.isBlank()) emptySet()
        else claimedMilestonesString.split(",").filter { it.isNotBlank() }.toSet()
}

@Entity(tableName = "personal_records")
data class PersonalRecordEntity(
    @PrimaryKey val exerciseId: String,
    val exerciseNameArabic: String,
    val maxWeightKg: Float,
    val maxReps: Int,
    val estimatedOneRepMaxKg: Float,
    val previousMaxWeightKg: Float = 0f,
    val achievedTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "strength_logs")
data class StrengthLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val exerciseId: String,
    val exerciseNameArabic: String,
    val setNumber: Int,
    val weightKg: Float,
    val reps: Int,
    val rpe: Float = 8.0f,
    val isPersonalRecord: Boolean = false,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "weekly_challenges")
data class WeeklyChallengeEntity(
    @PrimaryKey val challengeId: String,
    val titleArabic: String,
    val descriptionArabic: String,
    val category: String, // STREAK, WORKOUTS, WATER, CALORIES, PR, STEPS
    val targetGoal: Int,
    val currentProgress: Int,
    val rewardPoints: Float,
    val iconName: String = "emoji_events",
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val expiryTimestamp: Long = 0L
)

@Entity(tableName = "water_logs")
data class WaterLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amountMl: Int,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "workout_logs")
data class WorkoutLogEntity(
    @PrimaryKey(autoGenerate = true) val logId: Long = 0,
    val exerciseId: String,
    val exerciseNameArabic: String,
    val category: String,
    val isHome: Boolean,
    val difficultyPoints: Float, // 0.1 to 3.0
    val durationMinutes: Int,
    val caloriesBurned: Int,
    val setsCompleted: Int,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "meal_logs")
data class MealLogEntity(
    @PrimaryKey(autoGenerate = true) val mealId: Long = 0,
    val mealNameArabic: String,
    val mealCategory: String, // BREAKFAST, LUNCH, DINNER, SNACK
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "weight_progress")
data class WeightProgressEntity(
    @PrimaryKey(autoGenerate = true) val entryId: Long = 0,
    val weightKg: Float,
    val waistCm: Float? = null,
    val chestCm: Float? = null,
    val armCm: Float? = null,
    val photoUri: String? = null,
    val noteArabic: String = "",
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "gps_tracks")
data class GpsTrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionType: String = "RUN", // RUN, WALK
    val distanceKm: Float,
    val stepCount: Int,
    val durationSeconds: Int,
    val avgSpeedKmh: Float,
    val paceMinPerKm: Float,
    val caloriesBurned: Int,
    val routeCoordinatesJson: String = "[]",
    val targetLimitKm: Float = 5.0f,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "step_logs")
data class DailyStepLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val steps: Int,
    val distanceKm: Float,
    val activeMinutes: Int,
    val caloriesBurned: Int,
    val targetLimit: Int = 10000,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)

@Entity(tableName = "exercises_catalog")
data class ExerciseEntity(
    @PrimaryKey val id: String, // UUID or unique slug
    val nameArabic: String,
    val nameEnglish: String,
    val targetMuscle: String, // e.g. CHEST_MID, LATS, QUADS, SHOULDERS_FRONT, etc.
    val secondaryMusclesJson: String = "[]", // Array of secondary assisting muscles
    val equipmentRequired: String = "BARBELL", // BARBELL, DUMBBELL, CABLE, MACHINE, BODYWEIGHT
    val difficultyLevel: String = "INTERMEDIATE", // BEGINNER, INTERMEDIATE, ADVANCED
    val instructionsArabic: String = "",
    val instructionsEnglish: String = "",
    val commonMistakesArabicJson: String = "[]", // JSON array of safety cautions
    val mediaUrl: String = "",
    val category: String = "CHEST", // CHEST, BACK, LEGS, SHOULDERS_ARMS, CORE_ABS, FULL_BODY, HIIT_CARDIO
    val environment: String = "GYM", // HOME, GYM, BOTH
    val defaultSets: Int = 4,
    val defaultReps: String = "8-12",
    val restDurationSeconds: Int = 90,
    val estimatedCaloriesBurned: Int = 120,
    val tipArabic: String = "",
    val mindMuscleCueArabic: String = "",
    val jointSafetyTipArabic: String = "",
    val isCustom: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "workout_programs")
data class WorkoutProgramEntity(
    @PrimaryKey val id: String, // UUID or slug
    val titleArabic: String,
    val titleEnglish: String,
    val descriptionArabic: String,
    val goal: String, // BULKING, STRENGTH, CUTTING, LEAN_MASS, WEAK_POINT, FITNESS
    val durationWeeks: Int = 8,
    val daysPerWeek: Int = 4,
    val difficultyLevel: String = "INTERMEDIATE", // BEGINNER, INTERMEDIATE, ADVANCED
    val isCustom: Boolean = false,
    val iconBadge: String = "🔥",
    val isCurrentActiveProgram: Boolean = false,
    val createdTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "program_routine_days")
data class ProgramRoutineDayEntity(
    @PrimaryKey val id: String, // UUID
    val programId: String,
    val dayNumber: Int,
    val dayTitleArabic: String,
    val dayTitleEnglish: String = "",
    val targetMusclesSummary: String = "",
    val exercisesJson: String = "[]", // JSON list of ProgramDayExerciseItem
    val estimatedDurationMinutes: Int = 50
)

