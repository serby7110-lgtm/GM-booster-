package com.example.data

import com.example.data.database.DailyStepLogEntity
import com.example.data.database.ExerciseEntity
import com.example.data.database.GmDatabase
import com.example.data.database.GpsTrackEntity
import com.example.data.database.MealLogEntity
import com.example.data.database.PersonalRecordEntity
import com.example.data.database.ProgramRoutineDayEntity
import com.example.data.database.StrengthLogEntity
import com.example.data.database.UserProfileEntity
import com.example.data.database.WaterLogEntity
import com.example.data.database.WeeklyChallengeEntity
import com.example.data.database.WeightProgressEntity
import com.example.data.database.WorkoutLogEntity
import com.example.data.database.WorkoutProgramEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.util.Calendar

class GmRepository(private val database: GmDatabase) {

    val userProfileFlow: Flow<UserProfileEntity?> = database.userProfileDao().getUserProfile()
    val allWorkoutLogsFlow: Flow<List<WorkoutLogEntity>> = database.workoutLogDao().getAllWorkoutLogs()
    val allWeightEntriesFlow: Flow<List<WeightProgressEntity>> = database.weightProgressDao().getAllWeightEntries()
    val allPersonalRecordsFlow: Flow<List<PersonalRecordEntity>> = database.personalRecordDao().getAllPersonalRecords()
    val allStrengthLogsFlow: Flow<List<StrengthLogEntity>> = database.strengthLogDao().getAllStrengthLogs()
    val allChallengesFlow: Flow<List<WeeklyChallengeEntity>> = database.weeklyChallengeDao().getAllChallenges()
    val allProgramsFlow: Flow<List<WorkoutProgramEntity>> = database.workoutProgramDao().getAllPrograms()
    val activeProgramFlow: Flow<WorkoutProgramEntity?> = database.workoutProgramDao().getActiveProgram()
    val allCatalogExercisesFlow: Flow<List<ExerciseEntity>> = database.exerciseDao().getAllExercises()

    private fun getTodayStartMillis(): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getTodayMealsFlow(): Flow<List<MealLogEntity>> {
        return database.mealLogDao().getMealsToday(getTodayStartMillis())
    }

    fun getCaloriesBurnedTodayFlow(): Flow<Int?> {
        return database.workoutLogDao().getCaloriesBurnedToday(getTodayStartMillis())
    }

    fun getTodayWaterLogsFlow(): Flow<List<WaterLogEntity>> {
        return database.waterLogDao().getWaterLogsToday(getTodayStartMillis())
    }

    fun getAllWaterLogsFlow(): Flow<List<WaterLogEntity>> {
        return database.waterLogDao().getAllWaterLogs()
    }

    fun getTodayWaterTotalMlFlow(): Flow<Int?> {
        return database.waterLogDao().getTotalWaterTodayMl(getTodayStartMillis())
    }

    fun calculateDailyWaterGoalMl(weightKg: Float, activityLevel: String): Int {
        // Standard formula: 35ml per kg of body weight + bonus for fitness activity
        val baseWater = weightKg * 35f
        val activityBonus = when (activityLevel) {
            "SEDENTARY" -> 0
            "MODERATE" -> 400
            "VERY_ACTIVE", "HIGH" -> 800
            else -> 400
        }
        return (baseWater + activityBonus).toInt().coerceIn(1500, 5000)
    }

    suspend fun initializeDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val currentProfile = database.userProfileDao().getUserProfileSync()
        if (currentProfile == null) {
            val initialProfile = UserProfileEntity(
                id = 1,
                name = "كابتن أحمد",
                gender = "ذكر",
                age = 25,
                heightCm = 178f,
                currentWeightKg = 73.5f,
                targetWeightKg = 78.0f,
                bodyType = "ECTOMORPH",
                activityLevel = "MODERATE",
                fitnessGoal = "BUILD_MUSCLE",
                totalPoints = 8.5f,
                streakDays = 3,
                waterGlasses = 6,
                targetWaterGlasses = 10,
                lastWorkoutDate = System.currentTimeMillis(),
                isCloudSynced = true,
                workoutRemindersEnabled = true,
                reminderHour = 18,
                reminderMinute = 30,
                waterRemindersEnabled = true,
                selectedLanguage = "ar"
            )
            database.userProfileDao().insertOrUpdateProfile(initialProfile)

            // Seed historical weight points for initial progress graph
            val now = System.currentTimeMillis()
            val dayMillis = 24 * 60 * 60 * 1000L
            database.weightProgressDao().insertWeightEntry(
                WeightProgressEntity(
                    weightKg = 71.0f,
                    waistCm = 80f,
                    chestCm = 96f,
                    armCm = 32f,
                    noteArabic = "بداية الاشتراك في برنامج Gm للتمارين",
                    dateTimestamp = now - 21 * dayMillis
                )
            )
            database.weightProgressDao().insertWeightEntry(
                WeightProgressEntity(
                    weightKg = 71.8f,
                    waistCm = 80f,
                    chestCm = 97f,
                    armCm = 32.5f,
                    noteArabic = "أسبوع أول مع جدول الاكتومورف وزيادة السعرات",
                    dateTimestamp = now - 14 * dayMillis
                )
            )
            database.weightProgressDao().insertWeightEntry(
                WeightProgressEntity(
                    weightKg = 72.6f,
                    waistCm = 79.5f,
                    chestCm = 98.5f,
                    armCm = 33f,
                    noteArabic = "تطور ممتاز في القوة وتحسن الكتلة العضلية",
                    dateTimestamp = now - 7 * dayMillis
                )
            )
            database.weightProgressDao().insertWeightEntry(
                WeightProgressEntity(
                    weightKg = 73.5f,
                    waistCm = 79.0f,
                    chestCm = 100f,
                    armCm = 33.8f,
                    noteArabic = "الوزن الحالي مع الالتزام بوصفات التضخيم",
                    dateTimestamp = now
                )
            )

            // Seed today's sample meal
            database.mealLogDao().insertMeal(
                MealLogEntity(
                    mealNameArabic = "سموذي الشوفان وزبدة الفول مع الموز",
                    mealCategory = "BREAKFAST",
                    calories = 650,
                    proteinGrams = 35,
                    carbsGrams = 80,
                    fatGrams = 18,
                    dateTimestamp = now - 4 * 60 * 60 * 1000L
                )
            )

            // Seed sample hydration logs across the past 7 days
            val waterDailyDoses = listOf(
                listOf(500, 250, 500, 250), // today
                listOf(500, 500, 750, 500), // yesterday (2250ml)
                listOf(750, 500, 750, 500), // 2 days ago (2500ml)
                listOf(500, 500, 500, 500, 750), // 3 days ago (2750ml)
                listOf(500, 750, 500, 250), // 4 days ago (2000ml)
                listOf(750, 750, 500, 500), // 5 days ago (2500ml)
                listOf(500, 500, 500, 750)  // 6 days ago (2250ml)
            )

            waterDailyDoses.forEachIndexed { dayOffset, doses ->
                val baseTime = now - (dayOffset * dayMillis)
                doses.forEachIndexed { doseIdx, dose ->
                    database.waterLogDao().insertWaterLog(
                        WaterLogEntity(
                            amountMl = dose,
                            dateTimestamp = baseTime - (doseIdx * 2 * 60 * 60 * 1000L)
                        )
                    )
                }
            }

            // Seed sample completed workouts across the week for calories burned trend
            val sampleWorkouts = listOf(
                Triple("تمرين الصدر وترايسبس مكثف", 340, 1 * dayMillis),
                Triple("تمرين الظهر وبايسبس قوي", 410, 2 * dayMillis),
                Triple("تمرين الأرجل والسمانة الشامل", 480, 3 * dayMillis),
                Triple("كارديو وتمارين بطن HIIT", 290, 4 * dayMillis),
                Triple("تمارين الأكتاف والترابيس", 370, 5 * dayMillis),
                Triple("تمرين القوة والديدلفت", 450, 6 * dayMillis)
            )

            sampleWorkouts.forEach { (name, cal, offset) ->
                database.workoutLogDao().insertWorkoutLog(
                    WorkoutLogEntity(
                        exerciseId = "seeded_${System.currentTimeMillis()}_$offset",
                        exerciseNameArabic = name,
                        category = "CHEST",
                        isHome = false,
                        difficultyPoints = 2.2f,
                        durationMinutes = 45,
                        caloriesBurned = cal,
                        setsCompleted = 4,
                        dateTimestamp = now - offset
                    )
                )
            }

            // Seed initial benchmark PRs
            PersonalRecordsManager.getBenchmarkInitialRecords().forEach { record ->
                database.personalRecordDao().insertOrUpdateRecord(record)
            }

            // Seed default weekly challenges
            val defaultChallenges = ChallengesEngine.generateDefaultChallenges()
            database.weeklyChallengeDao().insertAllChallenges(defaultChallenges)
        }

        // Always check & seed programs/exercises if empty
        seedProgramsAndExercisesCatalogIfNeeded()
    }

    suspend fun logStrengthSet(
        exerciseId: String,
        exerciseNameArabic: String,
        setNumber: Int,
        weightKg: Float,
        reps: Int,
        rpe: Float = 8.0f
    ): ExercisePrCheckResult = withContext(Dispatchers.IO) {
        val existingPr = database.personalRecordDao().getRecordForExercise(exerciseId)
        val prResult = PersonalRecordsManager.evaluatePr(
            currentRecord = existingPr,
            exerciseId = exerciseId,
            exerciseNameArabic = exerciseNameArabic,
            newWeightKg = weightKg,
            newReps = reps
        )

        val strengthLog = StrengthLogEntity(
            exerciseId = exerciseId,
            exerciseNameArabic = exerciseNameArabic,
            setNumber = setNumber,
            weightKg = weightKg,
            reps = reps,
            rpe = rpe,
            isPersonalRecord = prResult.isNewPr,
            dateTimestamp = System.currentTimeMillis()
        )
        database.strengthLogDao().insertStrengthLog(strengthLog)

        if (prResult.isNewPr) {
            val updatedPr = PersonalRecordEntity(
                exerciseId = exerciseId,
                exerciseNameArabic = exerciseNameArabic,
                maxWeightKg = weightKg,
                maxReps = reps,
                estimatedOneRepMaxKg = prResult.estimated1RmKg,
                previousMaxWeightKg = existingPr?.maxWeightKg ?: 0f,
                achievedTimestamp = System.currentTimeMillis()
            )
            database.personalRecordDao().insertOrUpdateRecord(updatedPr)
        }

        prResult
    }

    suspend fun savePersonalRecord(record: PersonalRecordEntity) = withContext(Dispatchers.IO) {
        database.personalRecordDao().insertOrUpdateRecord(record)
    }

    suspend fun getPrForExercise(exerciseId: String): PersonalRecordEntity? = withContext(Dispatchers.IO) {
        database.personalRecordDao().getRecordForExercise(exerciseId)
    }

    suspend fun claimChallengeReward(challengeId: String, points: Float) = withContext(Dispatchers.IO) {
        database.weeklyChallengeDao().claimChallenge(challengeId)
        val profile = database.userProfileDao().getUserProfileSync()
        if (profile != null) {
            val updatedPoints = profile.totalPoints + points
            database.userProfileDao().updateProfile(
                profile.copy(totalPoints = (updatedPoints * 10).toInt() / 10f)
            )
        }
    }

    suspend fun updateMacroGoals(calories: Int, protein: Int, carbs: Int, fats: Int) = withContext(Dispatchers.IO) {
        val profile = database.userProfileDao().getUserProfileSync()
        if (profile != null) {
            database.userProfileDao().updateProfile(
                profile.copy(
                    targetDailyCalories = calories,
                    targetProteinGrams = protein,
                    targetCarbsGrams = carbs,
                    targetFatGrams = fats
                )
            )
        }
    }

    suspend fun updateWeakPoints(weakPointsJson: String) = withContext(Dispatchers.IO) {
        val profile = database.userProfileDao().getUserProfileSync()
        if (profile != null) {
            database.userProfileDao().updateProfile(
                profile.copy(weakPointsJson = weakPointsJson)
            )
        }
    }

    suspend fun updateProfile(profile: UserProfileEntity) = withContext(Dispatchers.IO) {
        database.userProfileDao().insertOrUpdateProfile(profile)
    }

    suspend fun logWorkoutCompletion(
        exercise: Exercise,
        durationMinutes: Int,
        setsDone: Int
    ) = withContext(Dispatchers.IO) {
        val log = WorkoutLogEntity(
            exerciseId = exercise.id,
            exerciseNameArabic = exercise.nameArabic,
            category = exercise.category.name,
            isHome = exercise.environment == WorkoutEnvironment.HOME,
            difficultyPoints = exercise.difficultyRating,
            durationMinutes = durationMinutes,
            caloriesBurned = exercise.estimatedCaloriesBurned,
            setsCompleted = setsDone,
            dateTimestamp = System.currentTimeMillis()
        )
        database.workoutLogDao().insertWorkoutLog(log)

        val currentProfile = database.userProfileDao().getUserProfileSync()
        if (currentProfile != null) {
            val newTotalPoints = currentProfile.totalPoints + exercise.difficultyRating
            val updated = currentProfile.copy(
                totalPoints = (newTotalPoints * 10).toInt() / 10f,
                lastWorkoutDate = System.currentTimeMillis()
            )
            database.userProfileDao().updateProfile(updated)
        }
    }

    suspend fun addMeal(meal: MealLogEntity) = withContext(Dispatchers.IO) {
        database.mealLogDao().insertMeal(meal)
    }

    suspend fun deleteMeal(id: Long) = withContext(Dispatchers.IO) {
        database.mealLogDao().deleteMeal(id)
    }

    suspend fun addWeightProgress(entry: WeightProgressEntity) = withContext(Dispatchers.IO) {
        database.weightProgressDao().insertWeightEntry(entry)
        val profile = database.userProfileDao().getUserProfileSync()
        if (profile != null) {
            database.userProfileDao().updateProfile(profile.copy(currentWeightKg = entry.weightKg))
        }
    }

    suspend fun deleteWeightProgress(id: Long) = withContext(Dispatchers.IO) {
        database.weightProgressDao().deleteWeightEntry(id)
    }

    suspend fun logWaterIntake(amountMl: Int) = withContext(Dispatchers.IO) {
        database.waterLogDao().insertWaterLog(
            WaterLogEntity(
                amountMl = amountMl,
                dateTimestamp = System.currentTimeMillis()
            )
        )
        // Also sync glasses in profile
        val currentProfile = database.userProfileDao().getUserProfileSync()
        if (currentProfile != null) {
            val glassesDelta = (amountMl / 250).coerceAtLeast(1)
            val updatedGlasses = (currentProfile.waterGlasses + glassesDelta).coerceIn(0, 30)
            database.userProfileDao().updateProfile(currentProfile.copy(waterGlasses = updatedGlasses))
        }
    }

    suspend fun deleteWaterLog(id: Long, amountMl: Int) = withContext(Dispatchers.IO) {
        database.waterLogDao().deleteWaterLog(id)
        val currentProfile = database.userProfileDao().getUserProfileSync()
        if (currentProfile != null) {
            val glassesDelta = (amountMl / 250).coerceAtLeast(1)
            val updatedGlasses = (currentProfile.waterGlasses - glassesDelta).coerceAtLeast(0)
            database.userProfileDao().updateProfile(currentProfile.copy(waterGlasses = updatedGlasses))
        }
    }

    suspend fun updateWaterIntake(glasses: Int) = withContext(Dispatchers.IO) {
        val currentProfile = database.userProfileDao().getUserProfileSync()
        if (currentProfile != null) {
            database.userProfileDao().updateProfile(
                currentProfile.copy(waterGlasses = glasses.coerceIn(0, 30))
            )
        }
    }

    // Program & Exercise Catalog Management
    fun getRoutineDaysForProgram(programId: String): Flow<List<ProgramRoutineDayEntity>> {
        return database.programRoutineDayDao().getDaysForProgram(programId)
    }

    suspend fun getRoutineDaysForProgramSync(programId: String): List<ProgramRoutineDayEntity> = withContext(Dispatchers.IO) {
        database.programRoutineDayDao().getDaysForProgramSync(programId)
    }

    suspend fun setActiveProgram(programId: String) = withContext(Dispatchers.IO) {
        database.workoutProgramDao().setActiveProgram(programId)
    }

    suspend fun saveCustomProgram(
        program: WorkoutProgramEntity,
        days: List<ProgramRoutineDayEntity>
    ) = withContext(Dispatchers.IO) {
        database.workoutProgramDao().insertProgram(program)
        database.programRoutineDayDao().deleteDaysForProgram(program.id)
        database.programRoutineDayDao().insertRoutineDays(days)
    }

    suspend fun deleteProgram(programId: String) = withContext(Dispatchers.IO) {
        database.workoutProgramDao().deleteProgram(programId)
        database.programRoutineDayDao().deleteDaysForProgram(programId)
    }

    suspend fun addCustomExercise(exercise: ExerciseEntity) = withContext(Dispatchers.IO) {
        database.exerciseDao().insertExercise(exercise)
    }

    suspend fun deleteCustomExercise(exerciseId: String) = withContext(Dispatchers.IO) {
        database.exerciseDao().deleteExercise(exerciseId)
    }

    suspend fun getOverloadAdvice(
        exerciseId: String,
        exerciseNameArabic: String,
        targetRepRange: String = "8-12"
    ): ProgressiveOverloadAdvice = withContext(Dispatchers.IO) {
        val recentLogs = database.strengthLogDao().getAllStrengthLogs().firstOrNull() ?: emptyList()
        ProgressiveOverloadEngine.calculateOverloadAdvice(
            exerciseId = exerciseId,
            exerciseNameArabic = exerciseNameArabic,
            recentLogs = recentLogs,
            targetRepRange = targetRepRange
        )
    }

    suspend fun seedProgramsAndExercisesCatalogIfNeeded() = withContext(Dispatchers.IO) {
        // Seed Exercises Catalog
        if (database.exerciseDao().getCount() == 0) {
            val exerciseEntities = WorkoutDatabase.allExercises.map { ex ->
                val secMusclesArray = JSONArray(ex.targetMusclesArabic)
                val mistakesArray = JSONArray(ex.getResolvedCommonMistakes())
                ExerciseEntity(
                    id = ex.id,
                    nameArabic = ex.nameArabic,
                    nameEnglish = ex.nameEnglish,
                    targetMuscle = ex.category.name,
                    secondaryMusclesJson = secMusclesArray.toString(),
                    equipmentRequired = ex.equipmentArabic,
                    difficultyLevel = if (ex.difficultyRating >= 2.2f) "ADVANCED" else if (ex.difficultyRating >= 1.5f) "INTERMEDIATE" else "BEGINNER",
                    instructionsArabic = ex.instructionsArabic,
                    instructionsEnglish = ex.instructionsEnglish,
                    commonMistakesArabicJson = mistakesArray.toString(),
                    category = ex.category.name,
                    environment = ex.environment.name,
                    defaultSets = ex.defaultSets,
                    defaultReps = ex.defaultReps,
                    restDurationSeconds = ex.restDurationSeconds,
                    estimatedCaloriesBurned = ex.estimatedCaloriesBurned,
                    tipArabic = ex.tipArabic,
                    mindMuscleCueArabic = ex.mindMuscleCueArabic,
                    jointSafetyTipArabic = ex.jointSafetyTipArabic,
                    isCustom = false
                )
            }
            database.exerciseDao().insertExercises(exerciseEntities)
        }

        // Seed Pre-made Workout Programs
        if (database.workoutProgramDao().getProgramsCount() == 0) {
            val programs = PreMadeProgramsData.getInitialPrograms()
            val days = PreMadeProgramsData.getInitialRoutineDays()
            database.workoutProgramDao().insertPrograms(programs)
            database.programRoutineDayDao().insertRoutineDays(days)
        }
    }
}

