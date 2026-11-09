package com.example.data

enum class GeneratorEquipment(val id: String, val titleArabic: String, val iconEmoji: String) {
    FULL_GYM("FULL_GYM", "نادي رياضي متكامل (Gym)", "🏋️"),
    DUMBBELLS_ONLY("DUMBBELLS_ONLY", "دمبلز منزلية فقط", "💪"),
    BODYWEIGHT_ONLY("BODYWEIGHT_ONLY", "وزن الجسم فقط (بدون أدوات)", "🤸"),
    RESISTANCE_BANDS("RESISTANCE_BANDS", "حبال مقاومة وأدوات خفيفة", "🎗️")
}

enum class GeneratorDuration(val minutes: Int, val titleArabic: String, val exerciseCount: Int) {
    QUICK_15(15, "15 دقيقة (سريعة ومكثفة)", 3),
    MEDIUM_30(30, "30 دقيقة (متوسطة وفعالة)", 4),
    STANDARD_45(45, "45 دقيقة (شاملة ومثالية)", 6),
    INTENSE_60(60, "60 دقيقة (جلسة أبطال متكاملة)", 7)
}

enum class GeneratorSplit(val id: String, val titleArabic: String, val matchingCategories: List<WorkoutCategory>) {
    FULL_BODY("FULL_BODY", "كامل الجسم (Full Body)", listOf(WorkoutCategory.FULL_BODY, WorkoutCategory.LEGS, WorkoutCategory.CHEST, WorkoutCategory.BACK, WorkoutCategory.CORE_ABS)),
    UPPER_BODY("UPPER_BODY", "الجزء العلوي (Upper Body)", listOf(WorkoutCategory.CHEST, WorkoutCategory.BACK, WorkoutCategory.SHOULDERS_ARMS)),
    LOWER_BODY("LOWER_BODY", "الجزء السفلي (Lower Body & Legs)", listOf(WorkoutCategory.LEGS, WorkoutCategory.CORE_ABS)),
    PUSH("PUSH", "عضلات الدفع (Push: صدر/كتف/تراي)", listOf(WorkoutCategory.CHEST, WorkoutCategory.SHOULDERS_ARMS)),
    PULL("PULL", "عضلات السحب (Pull: ظهر/باي)", listOf(WorkoutCategory.BACK, WorkoutCategory.SHOULDERS_ARMS)),
    CORE_AND_HIIT("CORE_AND_HIIT", "حرق دهون وكور (HIIT & Abs)", listOf(WorkoutCategory.CORE_ABS, WorkoutCategory.HIIT_CARDIO))
}

enum class GeneratorGoal(val id: String, val titleArabic: String, val repRange: String, val restSeconds: Int, val setsPerExercise: Int) {
    HYPERTROPHY("HYPERTROPHY", "تضخيم وبناء عضلات", "8-12 عدة مع أقصى عصر", 60, 4),
    FAT_LOSS("FAT_LOSS", "تنشيف وحرق دهون مكثف", "15-20 عدة سريعة", 35, 3),
    STRENGTH("STRENGTH", "قوة وانفجار عضلي", "5-6 عدات ثقيلة ومركزة", 90, 4)
}

data class GeneratedWorkoutSession(
    val titleArabic: String,
    val summaryArabic: String,
    val totalEstimatedMinutes: Int,
    val totalEstimatedCalories: Int,
    val exercises: List<GeneratedExerciseItem>,
    val equipmentUsed: GeneratorEquipment,
    val fitnessGoal: GeneratorGoal
)

data class GeneratedExerciseItem(
    val exercise: Exercise,
    val prescribedSets: Int,
    val prescribedReps: String,
    val prescribedRestSeconds: Int,
    val orderNumber: Int,
    val coachingCueArabic: String
)

object SmartWorkoutGeneratorEngine {

    fun generateWorkout(
        equipment: GeneratorEquipment,
        duration: GeneratorDuration,
        split: GeneratorSplit,
        goal: GeneratorGoal,
        userBodyType: BodyType = BodyType.MESOMORPH
    ): GeneratedWorkoutSession {
        val allExercises = WorkoutDatabase.allExercises

        // Filter based on equipment
        val equipmentFiltered = when (equipment) {
            GeneratorEquipment.FULL_GYM -> allExercises
            GeneratorEquipment.DUMBBELLS_ONLY -> allExercises.filter {
                it.equipmentArabic.contains("دمبل") || it.equipmentArabic.contains("وزن الجسم") || it.environment == WorkoutEnvironment.HOME
            }
            GeneratorEquipment.BODYWEIGHT_ONLY -> allExercises.filter {
                it.equipmentArabic.contains("وزن الجسم") || it.environment == WorkoutEnvironment.HOME
            }
            GeneratorEquipment.RESISTANCE_BANDS -> allExercises.filter {
                it.equipmentArabic.contains("حبال") || it.equipmentArabic.contains("وزن الجسم") || it.environment == WorkoutEnvironment.HOME
            }
        }

        // Filter matching categories for the split
        val splitFiltered = equipmentFiltered.filter { exercise ->
            split.matchingCategories.contains(exercise.category)
        }

        // Ensure we have enough exercises, fallback to equipmentFiltered if needed
        val pool = if (splitFiltered.size >= duration.exerciseCount) splitFiltered else equipmentFiltered

        // Pick the top matching exercises, prioritizing user's body type
        val prioritized = pool.sortedByDescending { exercise ->
            var score = 0
            if (exercise.targetBodyTypes.contains(userBodyType)) score += 5
            if (split.matchingCategories.contains(exercise.category)) score += 10
            score
        }

        val selectedExercises = prioritized.take(duration.exerciseCount)

        val totalCalories = selectedExercises.sumOf { it.estimatedCaloriesBurned } * duration.exerciseCount / 3

        val items = selectedExercises.mapIndexed { index, exercise ->
            val coachingCue = when (goal) {
                GeneratorGoal.HYPERTROPHY -> "ركز على ثانيتين نزول بالوزن وانقباض كامل في القمة."
                GeneratorGoal.FAT_LOSS -> "حافظ على وتيرة سريعة دون توقف أثناء العدات لحرق أعلى سعرات."
                GeneratorGoal.STRENGTH -> "تحكم بالوزن بقوة وانفجر بالدفع مع الحفاظ على استقامة المفاصل."
            }
            GeneratedExerciseItem(
                exercise = exercise,
                prescribedSets = goal.setsPerExercise,
                prescribedReps = goal.repRange,
                prescribedRestSeconds = goal.restSeconds,
                orderNumber = index + 1,
                coachingCueArabic = coachingCue
            )
        }

        val title = "جلسة ${split.titleArabic} الذكية (${duration.minutes} دقيقة)"
        val summary = "تم توليد هذه الجلسة بناءً على أدواتك (${equipment.titleArabic}) وهدفك (${goal.titleArabic}) لتحقيق أقصى استجابة عضلية بدون إهدار وقت."

        return GeneratedWorkoutSession(
            titleArabic = title,
            summaryArabic = summary,
            totalEstimatedMinutes = duration.minutes,
            totalEstimatedCalories = totalCalories.coerceAtLeast(120),
            exercises = items,
            equipmentUsed = equipment,
            fitnessGoal = goal
        )
    }
}
