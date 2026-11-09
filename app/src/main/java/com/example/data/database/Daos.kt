package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserProfileDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUserProfile(): Flow<UserProfileEntity?>

    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserProfileSync(): UserProfileEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateProfile(profile: UserProfileEntity)

    @Update
    suspend fun updateProfile(profile: UserProfileEntity)
}

@Dao
interface WorkoutLogDao {
    @Query("SELECT * FROM workout_logs ORDER BY dateTimestamp DESC")
    fun getAllWorkoutLogs(): Flow<List<WorkoutLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkoutLog(log: WorkoutLogEntity): Long

    @Query("SELECT SUM(difficultyPoints) FROM workout_logs")
    suspend fun getTotalPoints(): Float?

    @Query("SELECT SUM(caloriesBurned) FROM workout_logs WHERE dateTimestamp >= :startOfDayTimestamp")
    fun getCaloriesBurnedToday(startOfDayTimestamp: Long): Flow<Int?>

    @Query("SELECT * FROM workout_logs WHERE isSynced = 0")
    suspend fun getUnsyncedWorkoutLogs(): List<WorkoutLogEntity>

    @Query("UPDATE workout_logs SET isSynced = 1 WHERE logId IN (:ids)")
    suspend fun markWorkoutLogsAsSynced(ids: List<Long>)
}

@Dao
interface MealLogDao {
    @Query("SELECT * FROM meal_logs WHERE dateTimestamp >= :startOfDayTimestamp ORDER BY dateTimestamp DESC")
    fun getMealsToday(startOfDayTimestamp: Long): Flow<List<MealLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLogEntity): Long

    @Query("DELETE FROM meal_logs WHERE mealId = :id")
    suspend fun deleteMeal(id: Long)

    @Query("SELECT * FROM meal_logs WHERE isSynced = 0")
    suspend fun getUnsyncedMealLogs(): List<MealLogEntity>

    @Query("UPDATE meal_logs SET isSynced = 1 WHERE mealId IN (:ids)")
    suspend fun markMealLogsAsSynced(ids: List<Long>)
}

@Dao
interface WeightProgressDao {
    @Query("SELECT * FROM weight_progress ORDER BY dateTimestamp ASC")
    fun getAllWeightEntries(): Flow<List<WeightProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeightEntry(entry: WeightProgressEntity): Long

    @Query("DELETE FROM weight_progress WHERE entryId = :id")
    suspend fun deleteWeightEntry(id: Long)

    @Query("SELECT * FROM weight_progress WHERE isSynced = 0")
    suspend fun getUnsyncedWeightEntries(): List<WeightProgressEntity>

    @Query("UPDATE weight_progress SET isSynced = 1 WHERE entryId IN (:ids)")
    suspend fun markWeightEntriesAsSynced(ids: List<Long>)
}

@Dao
interface WaterLogDao {
    @Query("SELECT * FROM water_logs WHERE dateTimestamp >= :startOfDayTimestamp ORDER BY dateTimestamp DESC")
    fun getWaterLogsToday(startOfDayTimestamp: Long): Flow<List<WaterLogEntity>>

    @Query("SELECT * FROM water_logs ORDER BY dateTimestamp DESC LIMIT 100")
    fun getAllWaterLogs(): Flow<List<WaterLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWaterLog(log: WaterLogEntity): Long

    @Query("DELETE FROM water_logs WHERE id = :id")
    suspend fun deleteWaterLog(id: Long)

    @Query("SELECT SUM(amountMl) FROM water_logs WHERE dateTimestamp >= :startOfDayTimestamp")
    fun getTotalWaterTodayMl(startOfDayTimestamp: Long): Flow<Int?>

    @Query("SELECT * FROM water_logs WHERE isSynced = 0")
    suspend fun getUnsyncedWaterLogs(): List<WaterLogEntity>

    @Query("UPDATE water_logs SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markWaterLogsAsSynced(ids: List<Long>)
}

@Dao
interface GpsTrackDao {
    @Query("SELECT * FROM gps_tracks ORDER BY dateTimestamp DESC")
    fun getAllGpsTracks(): Flow<List<GpsTrackEntity>>

    @Query("SELECT * FROM gps_tracks WHERE dateTimestamp >= :startOfDayTimestamp ORDER BY dateTimestamp DESC")
    fun getGpsTracksToday(startOfDayTimestamp: Long): Flow<List<GpsTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGpsTrack(track: GpsTrackEntity): Long

    @Query("DELETE FROM gps_tracks WHERE id = :id")
    suspend fun deleteGpsTrack(id: Long)

    @Query("SELECT * FROM gps_tracks WHERE isSynced = 0")
    suspend fun getUnsyncedGpsTracks(): List<GpsTrackEntity>

    @Query("UPDATE gps_tracks SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markGpsTracksAsSynced(ids: List<Long>)
}

@Dao
interface DailyStepLogDao {
    @Query("SELECT * FROM step_logs ORDER BY dateTimestamp DESC")
    fun getAllStepLogs(): Flow<List<DailyStepLogEntity>>

    @Query("SELECT * FROM step_logs WHERE dateTimestamp >= :startOfDayTimestamp LIMIT 1")
    suspend fun getTodayStepLog(startOfDayTimestamp: Long): DailyStepLogEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateStepLog(log: DailyStepLogEntity): Long

    @Query("SELECT * FROM step_logs WHERE isSynced = 0")
    suspend fun getUnsyncedStepLogs(): List<DailyStepLogEntity>

    @Query("UPDATE step_logs SET isSynced = 1 WHERE id IN (:ids)")
    suspend fun markStepLogsAsSynced(ids: List<Long>)
}

@Dao
interface PersonalRecordDao {
    @Query("SELECT * FROM personal_records ORDER BY maxWeightKg DESC")
    fun getAllPersonalRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId LIMIT 1")
    suspend fun getRecordForExercise(exerciseId: String): PersonalRecordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateRecord(record: PersonalRecordEntity)

    @Query("DELETE FROM personal_records WHERE exerciseId = :exerciseId")
    suspend fun deleteRecord(exerciseId: String)
}

@Dao
interface StrengthLogDao {
    @Query("SELECT * FROM strength_logs WHERE exerciseId = :exerciseId ORDER BY dateTimestamp ASC")
    fun getLogsForExercise(exerciseId: String): Flow<List<StrengthLogEntity>>

    @Query("SELECT * FROM strength_logs ORDER BY dateTimestamp DESC")
    fun getAllStrengthLogs(): Flow<List<StrengthLogEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStrengthLog(log: StrengthLogEntity): Long
}

@Dao
interface WeeklyChallengeDao {
    @Query("SELECT * FROM weekly_challenges ORDER BY isCompleted ASC, rewardPoints DESC")
    fun getAllChallenges(): Flow<List<WeeklyChallengeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllChallenges(challenges: List<WeeklyChallengeEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateChallenge(challenge: WeeklyChallengeEntity)

    @Query("UPDATE weekly_challenges SET currentProgress = :progress, isCompleted = :completed WHERE challengeId = :challengeId")
    suspend fun updateChallengeProgress(challengeId: String, progress: Int, completed: Boolean)

    @Query("UPDATE weekly_challenges SET isClaimed = 1 WHERE challengeId = :challengeId")
    suspend fun claimChallenge(challengeId: String)
}

@Dao
interface ExerciseDao {
    @Query("SELECT * FROM exercises_catalog ORDER BY isCustom DESC, nameArabic ASC")
    fun getAllExercises(): Flow<List<ExerciseEntity>>

    @Query("SELECT * FROM exercises_catalog WHERE id = :id LIMIT 1")
    suspend fun getExerciseById(id: String): ExerciseEntity?

    @Query("SELECT * FROM exercises_catalog WHERE category = :category")
    fun getExercisesByCategory(category: String): Flow<List<ExerciseEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercises(exercises: List<ExerciseEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExercise(exercise: ExerciseEntity)

    @Query("DELETE FROM exercises_catalog WHERE id = :id")
    suspend fun deleteExercise(id: String)

    @Query("SELECT COUNT(*) FROM exercises_catalog")
    suspend fun getCount(): Int
}

@Dao
interface WorkoutProgramDao {
    @Query("SELECT * FROM workout_programs ORDER BY isCurrentActiveProgram DESC, isCustom ASC, createdTimestamp DESC")
    fun getAllPrograms(): Flow<List<WorkoutProgramEntity>>

    @Query("SELECT * FROM workout_programs WHERE id = :programId LIMIT 1")
    suspend fun getProgramById(programId: String): WorkoutProgramEntity?

    @Query("SELECT * FROM workout_programs WHERE isCurrentActiveProgram = 1 LIMIT 1")
    fun getActiveProgram(): Flow<WorkoutProgramEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProgram(program: WorkoutProgramEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPrograms(programs: List<WorkoutProgramEntity>)

    @Query("UPDATE workout_programs SET isCurrentActiveProgram = CASE WHEN id = :programId THEN 1 ELSE 0 END")
    suspend fun setActiveProgram(programId: String)

    @Query("DELETE FROM workout_programs WHERE id = :programId")
    suspend fun deleteProgram(programId: String)

    @Query("SELECT COUNT(*) FROM workout_programs")
    suspend fun getProgramsCount(): Int
}

@Dao
interface ProgramRoutineDayDao {
    @Query("SELECT * FROM program_routine_days WHERE programId = :programId ORDER BY dayNumber ASC")
    fun getDaysForProgram(programId: String): Flow<List<ProgramRoutineDayEntity>>

    @Query("SELECT * FROM program_routine_days WHERE programId = :programId ORDER BY dayNumber ASC")
    suspend fun getDaysForProgramSync(programId: String): List<ProgramRoutineDayEntity>

    @Query("SELECT * FROM program_routine_days WHERE id = :dayId LIMIT 1")
    suspend fun getDayById(dayId: String): ProgramRoutineDayEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDays(days: List<ProgramRoutineDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineDay(day: ProgramRoutineDayEntity)

    @Query("DELETE FROM program_routine_days WHERE programId = :programId")
    suspend fun deleteDaysForProgram(programId: String)

    @Query("DELETE FROM program_routine_days WHERE id = :dayId")
    suspend fun deleteDay(dayId: String)
}


