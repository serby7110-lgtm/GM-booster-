package com.example.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [
        UserProfileEntity::class,
        WorkoutLogEntity::class,
        MealLogEntity::class,
        WeightProgressEntity::class,
        WaterLogEntity::class,
        GpsTrackEntity::class,
        DailyStepLogEntity::class,
        PersonalRecordEntity::class,
        StrengthLogEntity::class,
        WeeklyChallengeEntity::class,
        ExerciseEntity::class,
        WorkoutProgramEntity::class,
        ProgramRoutineDayEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class GmDatabase : RoomDatabase() {
    abstract fun userProfileDao(): UserProfileDao
    abstract fun workoutLogDao(): WorkoutLogDao
    abstract fun mealLogDao(): MealLogDao
    abstract fun weightProgressDao(): WeightProgressDao
    abstract fun waterLogDao(): WaterLogDao
    abstract fun gpsTrackDao(): GpsTrackDao
    abstract fun dailyStepLogDao(): DailyStepLogDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun strengthLogDao(): StrengthLogDao
    abstract fun weeklyChallengeDao(): WeeklyChallengeDao
    abstract fun exerciseDao(): ExerciseDao
    abstract fun workoutProgramDao(): WorkoutProgramDao
    abstract fun programRoutineDayDao(): ProgramRoutineDayDao

    companion object {
        @Volatile
        private var INSTANCE: GmDatabase? = null

        val MIGRATION_6_7 = object : Migration(6, 7) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE user_profile ADD COLUMN claimedMilestonesString TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): GmDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GmDatabase::class.java,
                    "gm_fitness_db"
                )
                    .addMigrations(MIGRATION_6_7)
                    .fallbackToDestructiveMigration(true)
                    .fallbackToDestructiveMigrationOnDowngrade(true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
