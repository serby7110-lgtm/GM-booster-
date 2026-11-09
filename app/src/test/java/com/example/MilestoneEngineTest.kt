package com.example

import com.example.data.FitnessMilestone
import com.example.data.MilestoneCategory
import com.example.data.MilestoneEngine
import com.example.data.MilestoneTier
import com.example.data.database.DailyStepLogEntity
import com.example.data.database.GpsTrackEntity
import com.example.data.database.PersonalRecordEntity
import com.example.data.database.WeightProgressEntity
import com.example.data.database.WorkoutLogEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MilestoneEngineTest {

    @Test
    fun testMilestoneTemplateCountsAndCategories() {
        val templates = MilestoneEngine.allMilestoneTemplates
        assertTrue("Should have at least 20 comprehensive fitness milestones", templates.size >= 20)

        val strengthMilestones = templates.filter { it.category == MilestoneCategory.STRENGTH }
        val streakMilestones = templates.filter { it.category == MilestoneCategory.STREAK }
        val transformationMilestones = templates.filter { it.category == MilestoneCategory.TRANSFORMATION }
        val enduranceMilestones = templates.filter { it.category == MilestoneCategory.ENDURANCE }

        assertTrue(strengthMilestones.isNotEmpty())
        assertTrue(streakMilestones.isNotEmpty())
        assertTrue(transformationMilestones.isNotEmpty())
        assertTrue(enduranceMilestones.isNotEmpty())
    }

    @Test
    fun testStrengthMilestoneUnlocking() {
        val records = listOf(
            PersonalRecordEntity(
                exerciseId = "ex_bench_press",
                exerciseNameArabic = "بنش برس مستوي بالبار",
                maxWeightKg = 102.5f,
                maxReps = 3,
                estimatedOneRepMaxKg = 110f
            ),
            PersonalRecordEntity(
                exerciseId = "ex_squat",
                exerciseNameArabic = "سكوات خلفي بالأوزان",
                maxWeightKg = 130f,
                maxReps = 5,
                estimatedOneRepMaxKg = 145f
            )
        )

        val evaluated = MilestoneEngine.evaluateMilestones(
            personalRecords = records,
            streakDays = 5,
            currentWeightKg = 75f,
            targetWeightKg = 78f,
            initialWeightKg = 72f,
            waterGlasses = 6,
            weightLogs = emptyList(),
            workoutLogs = emptyList(),
            gpsTracks = emptyList(),
            dailyStepLogs = emptyList(),
            claimedIds = setOf("ms_bench_60")
        )

        val bench60 = evaluated.find { it.id == "ms_bench_60" }!!
        val bench80 = evaluated.find { it.id == "ms_bench_80" }!!
        val bench100 = evaluated.find { it.id == "ms_bench_100" }!!

        assertTrue("Bench 60 should be unlocked", bench60.isUnlocked)
        assertTrue("Bench 60 should be claimed", bench60.isClaimed)
        assertTrue("Bench 80 should be unlocked", bench80.isUnlocked)
        assertFalse("Bench 80 should not be claimed yet", bench80.isClaimed)
        assertTrue("Bench 100 should be unlocked with 102.5kg PR", bench100.isUnlocked)
        assertEquals(102.5f, bench100.currentMetric, 0.01f)
    }

    @Test
    fun testStreakAndEnduranceMilestoneUnlocking() {
        val gpsTracks = listOf(
            GpsTrackEntity(
                id = 1,
                sessionType = "RUN",
                distanceKm = 10.5f,
                stepCount = 12000,
                durationSeconds = 1800,
                avgSpeedKmh = 10f,
                paceMinPerKm = 5.7f,
                caloriesBurned = 500
            )
        )

        val stepLogs = listOf(
            DailyStepLogEntity(id = 1, steps = 35000, distanceKm = 20f, activeMinutes = 120, caloriesBurned = 1000),
            DailyStepLogEntity(id = 2, steps = 25000, distanceKm = 15f, activeMinutes = 90, caloriesBurned = 700)
        )

        val evaluated = MilestoneEngine.evaluateMilestones(
            personalRecords = emptyList(),
            streakDays = 15,
            currentWeightKg = 74f,
            targetWeightKg = 70f,
            initialWeightKg = 78f,
            waterGlasses = 8,
            weightLogs = emptyList(),
            workoutLogs = emptyList(),
            gpsTracks = gpsTracks,
            dailyStepLogs = stepLogs,
            claimedIds = emptySet()
        )

        val streak7 = evaluated.find { it.id == "ms_streak_7" }!!
        val streak14 = evaluated.find { it.id == "ms_streak_14" }!!
        val streak30 = evaluated.find { it.id == "ms_streak_30" }!!

        assertTrue("Streak 7 should be unlocked", streak7.isUnlocked)
        assertTrue("Streak 14 should be unlocked with 15 days streak", streak14.isUnlocked)
        assertFalse("Streak 30 should be locked", streak30.isUnlocked)

        val end5k = evaluated.find { it.id == "ms_end_5k" }!!
        val end10k = evaluated.find { it.id == "ms_end_10k" }!!
        val end21k = evaluated.find { it.id == "ms_end_21k" }!!
        val steps50k = evaluated.find { it.id == "ms_steps_50k" }!!

        assertTrue("5K Run should be unlocked", end5k.isUnlocked)
        assertTrue("10K Run should be unlocked with 10.5km track", end10k.isUnlocked)
        assertFalse("21K Run should be locked", end21k.isUnlocked)
        assertTrue("50k cumulative steps should be unlocked (35k + 25k = 60k)", steps50k.isUnlocked)

        // Test Category Progress Calculation
        val enduranceProgress = MilestoneEngine.calculateCategoryProgress(evaluated, MilestoneCategory.ENDURANCE)
        assertTrue(enduranceProgress.unlockedCount >= 3)
        assertTrue(enduranceProgress.totalCount >= 5)
    }

    @Test
    fun testTransformationMilestoneCalculation() {
        val evaluated = MilestoneEngine.evaluateMilestones(
            personalRecords = emptyList(),
            streakDays = 2,
            currentWeightKg = 73f,
            targetWeightKg = 68f,
            initialWeightKg = 78f, // 5kg lost towards 68kg goal
            waterGlasses = 8,
            weightLogs = listOf(
                WeightProgressEntity(entryId = 1, dateTimestamp = 1, weightKg = 78f),
                WeightProgressEntity(entryId = 2, dateTimestamp = 2, weightKg = 76f),
                WeightProgressEntity(entryId = 3, dateTimestamp = 3, weightKg = 73f)
            ),
            workoutLogs = emptyList(),
            gpsTracks = emptyList(),
            dailyStepLogs = emptyList(),
            claimedIds = emptySet()
        )

        val trans1kg = evaluated.find { it.id == "ms_trans_1kg" }!!
        val trans3kg = evaluated.find { it.id == "ms_trans_3kg" }!!
        val trans5kg = evaluated.find { it.id == "ms_trans_5kg" }!!
        val trans10kg = evaluated.find { it.id == "ms_trans_10kg" }!!

        assertTrue("1kg should be unlocked", trans1kg.isUnlocked)
        assertTrue("3kg should be unlocked", trans3kg.isUnlocked)
        assertTrue("5kg should be unlocked with 5kg loss delta", trans5kg.isUnlocked)
        assertFalse("10kg should still be locked", trans10kg.isUnlocked)
    }
}
