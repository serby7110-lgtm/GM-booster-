package com.example.data

import com.example.data.database.ProgramRoutineDayEntity
import com.example.data.database.WorkoutProgramEntity

object PreMadeProgramsData {

    fun getInitialPrograms(): List<WorkoutProgramEntity> {
        return listOf(
            WorkoutProgramEntity(
                id = "prog_ppl_hypertrophy_6d",
                titleArabic = "برنامج دفع / سحب / أرجل (PPL)",
                titleEnglish = "Push Pull Legs 6-Day Hypertrophy",
                descriptionArabic = "البرنامج الأقوى والأشهر عالمياً لبناء الكتلة العضلية والضخامة بأعلى كفاءة واستشفاء منظم.",
                goal = ProgramGoal.BULKING.name,
                durationWeeks = 8,
                daysPerWeek = 6,
                difficultyLevel = ProgramDifficulty.INTERMEDIATE.name,
                isCustom = false,
                iconBadge = "💪",
                isCurrentActiveProgram = true
            ),
            WorkoutProgramEntity(
                id = "prog_upper_lower_4d",
                titleArabic = "برنامج الجزء العلوي / السفلي (Upper-Lower)",
                titleEnglish = "Upper / Lower 4-Day Power Split",
                descriptionArabic = "جدول متوازن بـ 4 أيام أسبوعياً؛ يجمع بين زيادة الأوزان والقوة والضخامة مع وقت استشفاء ممتاز.",
                goal = ProgramGoal.STRENGTH.name,
                durationWeeks = 10,
                daysPerWeek = 4,
                difficultyLevel = ProgramDifficulty.INTERMEDIATE.name,
                isCustom = false,
                iconBadge = "⚡",
                isCurrentActiveProgram = false
            ),
            WorkoutProgramEntity(
                id = "prog_full_body_3d",
                titleArabic = "برنامج كامل الجسم للمبتدئين (Full Body 3X)",
                titleEnglish = "Full Body 3-Day Strength Foundation",
                descriptionArabic = "برنامج تأسيسي مثالي لتطوير التكنيك وزيادة القوة الأساسية وتحفيز تخليق البروتين العضلي 3 مرات أسبوعياً.",
                goal = ProgramGoal.FITNESS.name,
                durationWeeks = 6,
                daysPerWeek = 3,
                difficultyLevel = ProgramDifficulty.BEGINNER.name,
                isCustom = false,
                iconBadge = "🛡️",
                isCurrentActiveProgram = false
            ),
            WorkoutProgramEntity(
                id = "prog_arnold_split_6d",
                titleArabic = "جدول أرنولد الكلاسيكي (Arnold Split)",
                titleEnglish = "Arnold Classic Golden Era Split",
                descriptionArabic = "تقسيم العصر الذهبي: صدر وظهر في يوم واحد، أكتاف وذراعين، وأرجل. يمنحك ضخ دماء أسطوري وتناسق عالي.",
                goal = ProgramGoal.BULKING.name,
                durationWeeks = 8,
                daysPerWeek = 6,
                difficultyLevel = ProgramDifficulty.ADVANCED.name,
                isCustom = false,
                iconBadge = "👑",
                isCurrentActiveProgram = false
            ),
            WorkoutProgramEntity(
                id = "prog_weak_legs_focus_5w",
                titleArabic = "برنامج تفجير الأرجل والسمانة الضعيفة",
                titleEnglish = "Legs & Calves Hyper-Growth Specialization",
                descriptionArabic = "برنامج تخصصي لكسر ثبات عضلات الفخذ الأمامي والخلفي والسمانة بتردد مرتين أسبوعياً وتكنيك متقدم.",
                goal = ProgramGoal.WEAK_POINT.name,
                durationWeeks = 5,
                daysPerWeek = 5,
                difficultyLevel = ProgramDifficulty.ADVANCED.name,
                isCustom = false,
                iconBadge = "🦵",
                isCurrentActiveProgram = false
            ),
            WorkoutProgramEntity(
                id = "prog_home_shred_4w",
                titleArabic = "برنامج التنشيف المنزلي بوزن الجسم والدمبلز",
                titleEnglish = "Home Dumbbell & Bodyweight Shred",
                descriptionArabic = "تمارين حارقة للسعرات ونحت العضلات من المنزل دون الحاجة لأجهزة صالة رياضية معقدة.",
                goal = ProgramGoal.CUTTING.name,
                durationWeeks = 4,
                daysPerWeek = 4,
                difficultyLevel = ProgramDifficulty.BEGINNER.name,
                isCustom = false,
                iconBadge = "🔥",
                isCurrentActiveProgram = false
            )
        )
    }

    fun getInitialRoutineDays(): List<ProgramRoutineDayEntity> {
        val days = mutableListOf<ProgramRoutineDayEntity>()

        // 1. PPL Program Days
        days.add(
            ProgramRoutineDayEntity(
                id = "ppl_d1",
                programId = "prog_ppl_hypertrophy_6d",
                dayNumber = 1,
                dayTitleArabic = "اليوم الأول: دفع (Push - صدر وأكتاف أمامية وتراي)",
                dayTitleEnglish = "Day 1: Push (Chest, Front Delts & Triceps)",
                targetMusclesSummary = "صدر، أكتاف، ترايسبس",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("chest_bench_press", "بنش برس بالبار مستوي", "Flat Barbell Bench Press", 4, "6-8", 120, 8.5f, "التمرين الأساسي لقوة الصدر", "بار وأوزان"),
                        ProgramDayExerciseItem("chest_incline_dumbbell_press", "ضغط دمبلز مائل للأعلى", "Incline Dumbbell Press", 4, "8-10", 90, 8.0f, "استهداف الصدر العلوي بزاوية 30 درجة", "دمبلز ومقعد مائل"),
                        ProgramDayExerciseItem("shoulders_overhead_press", "ضغط كتف بالبار واقف (OHP)", "Overhead Barbell Press", 3, "8-10", 90, 8.0f, "تثبيت الكور وعصر الكتف", "بار"),
                        ProgramDayExerciseItem("shoulders_lateral_raise", "رفرفة جانبية بالدمبلز", "Dumbbell Lateral Raise", 4, "12-15", 60, 9.0f, "تركيز على الكتف الجانبي والتحكم بالنزول", "دمبلز"),
                        ProgramDayExerciseItem("arms_triceps_pushdown", "ترايسبس كابل مستقيم بالحبل", "Rope Triceps Pushdown", 3, "10-12", 60, 8.5f, "فتح الحبل في نهاية النزول لعصر الرأس الجانبي", "جهاز كابل")
                    )
                ),
                estimatedDurationMinutes = 55
            )
        )

        days.add(
            ProgramRoutineDayEntity(
                id = "ppl_d2",
                programId = "prog_ppl_hypertrophy_6d",
                dayNumber = 2,
                dayTitleArabic = "اليوم الثاني: سحب (Pull - ظهر وأكتاف خلفية وبايسبس)",
                dayTitleEnglish = "Day 2: Pull (Back, Rear Delts & Biceps)",
                targetMusclesSummary = "ظهر، ترابيس، بايسبس",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("back_deadlift", "الرفعة الميتة (ديدلفت)", "Conventional Deadlift", 4, "5-6", 150, 8.5f, "الحفاظ على استقامة الظهر وعصر الهامسترينج والظهر", "بار وأوزان"),
                        ProgramDayExerciseItem("back_pullups", "عقلة بوزن الجسم / مساعدة", "Pull-ups / Chin-ups", 4, "8-10", 90, 8.5f, "سحب باللاتس والنزول لمدى حركي كامل", "بار عقلة"),
                        ProgramDayExerciseItem("back_barbell_row", "تجديف بالبار منحني (باربل رو)", "Bent-Over Barbell Row", 4, "8-10", 90, 8.0f, "سحب البار نحو السرة وعصر منتصف الظهر", "بار"),
                        ProgramDayExerciseItem("arms_barbell_curl", "تبادل بايسبس بالبار إيزي كيرل", "EZ-Bar Biceps Curl", 3, "10-12", 60, 8.5f, "تثبيت الكوع بجانب الجذع", "بار إيزي"),
                        ProgramDayExerciseItem("arms_hammer_curl", "تبادل بايسبس مطرقة بالدمبلز", "Dumbbell Hammer Curls", 3, "10-12", 60, 8.0f, "استهداف عضلة البراكيلس وسماكة الذراع", "دمبلز")
                    )
                ),
                estimatedDurationMinutes = 60
            )
        )

        days.add(
            ProgramRoutineDayEntity(
                id = "ppl_d3",
                programId = "prog_ppl_hypertrophy_6d",
                dayNumber = 3,
                dayTitleArabic = "اليوم الثالث: أرجل وبطن (Legs & Abs)",
                dayTitleEnglish = "Day 3: Legs (Quads, Hamstrings & Core)",
                targetMusclesSummary = "أرجل، سمانة، بطن",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("legs_barbell_squat", "سكوات بالبار حر خلفي", "Barbell Back Squat", 4, "6-8", 120, 8.5f, "النزول لعمق موازي والضغط بالكعبين", "بار وأوزان وراك"),
                        ProgramDayExerciseItem("legs_romanian_deadlift", "ديدلفت روماني للأرجل الخلفية (RDL)", "Romanian Deadlift", 4, "8-10", 90, 8.0f, "دفع الحوض للخلف وإطالة الهامسترينج", "بار أو دمبلز"),
                        ProgramDayExerciseItem("legs_leg_press", "ضغط أرجل بالجهاز (Leg Press)", "Leg Press Machine", 3, "10-12", 90, 8.5f, "وضع القدمين بمنتصف اللوح وعدم قفل الركبة", "جهاز الليج برس"),
                        ProgramDayExerciseItem("legs_standing_calf_raise", "رفع سمانة واقف", "Standing Calf Raises", 4, "12-15", 60, 9.0f, "التوقف ثانية في القمة وثانية في القاع", "جهاز سمانة"),
                        ProgramDayExerciseItem("core_hanging_leg_raise", "رفع أرجل معلق للبطن السفلي", "Hanging Leg Raises", 3, "12-15", 60, 8.5f, "رفع الحوض مع الساقين لاستهداف البطن", "عقلة")
                    )
                ),
                estimatedDurationMinutes = 55
            )
        )

        // 2. Upper Lower Program Days
        days.add(
            ProgramRoutineDayEntity(
                id = "ul_d1",
                programId = "prog_upper_lower_4d",
                dayNumber = 1,
                dayTitleArabic = "اليوم الأول: الجزء العلوي قوة (Upper Power)",
                dayTitleEnglish = "Day 1: Upper Power & Mass",
                targetMusclesSummary = "صدر، ظهر، أكتاف، ذراعين",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("chest_bench_press", "بنش برس بالبار مستوي", "Flat Barbell Bench Press", 4, "5-6", 120, 8.5f, "تركيز على زيادة الوزن التدريجي", "بار"),
                        ProgramDayExerciseItem("back_barbell_row", "تجديف بالبار (Barbell Row)", "Bent-Over Barbell Row", 4, "6-8", 90, 8.5f, "بناء سمك الظهر والقوة", "بار"),
                        ProgramDayExerciseItem("shoulders_overhead_press", "ضغط كتف بالبار واقف (OHP)", "Overhead Barbell Press", 3, "6-8", 90, 8.0f, "قوة ودفع علوي", "بار"),
                        ProgramDayExerciseItem("arms_barbell_curl", "تبادل بايسبس بالبار", "Barbell Biceps Curl", 3, "8-10", 60, 8.0f, "عصر البايسبس بقوة", "بار")
                    )
                ),
                estimatedDurationMinutes = 50
            )
        )

        days.add(
            ProgramRoutineDayEntity(
                id = "ul_d2",
                programId = "prog_upper_lower_4d",
                dayNumber = 2,
                dayTitleArabic = "اليوم الثاني: الجزء السفلي قوة (Lower Power)",
                dayTitleEnglish = "Day 2: Lower Power & Legs",
                targetMusclesSummary = "أرجل أمامية، خلفية، سمانة، كور",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("legs_barbell_squat", "سكوات بالبار خلفي", "Barbell Back Squat", 4, "5-6", 120, 8.5f, "تمرين القوة الأساسي للجزء السفلي", "بار"),
                        ProgramDayExerciseItem("legs_romanian_deadlift", "ديدلفت روماني (RDL)", "Romanian Deadlift", 4, "6-8", 90, 8.0f, "قوة السلسلة الخلفية والأرجل", "بار"),
                        ProgramDayExerciseItem("legs_standing_calf_raise", "رفع سمانة واقف", "Standing Calf Raises", 4, "10-12", 60, 8.5f, "تركيز على التكرار البطيء", "جهاز سمانة")
                    )
                ),
                estimatedDurationMinutes = 45
            )
        )

        // 3. Full Body Program Days
        days.add(
            ProgramRoutineDayEntity(
                id = "fb_d1",
                programId = "prog_full_body_3d",
                dayNumber = 1,
                dayTitleArabic = "اليوم الأول: كامل الجسم A (Full Body A)",
                dayTitleEnglish = "Day 1: Full Body Workout A",
                targetMusclesSummary = "صدر، ظهر، أرجل، كتف، كور",
                exercisesJson = ProgramDayExerciseItem.listToJsonString(
                    listOf(
                        ProgramDayExerciseItem("legs_barbell_squat", "سكوات بالبار", "Barbell Squat", 3, "8-10", 90, 8.0f, "تحفيز العضلات الكبيرة", "بار"),
                        ProgramDayExerciseItem("chest_bench_press", "بنش برس مستوي", "Flat Bench Press", 3, "8-10", 90, 8.0f, "قوة وبناء الصدر", "بار"),
                        ProgramDayExerciseItem("back_lat_pulldown", "سحب عريض لأسفل للظهر (Lat Pulldown)", "Lat Pulldown", 3, "10-12", 60, 8.0f, "توسيع الظهر", "جهاز السحب"),
                        ProgramDayExerciseItem("shoulders_lateral_raise", "رفرفة جانبية بالدمبلز", "Lateral Raises", 3, "12-15", 60, 8.5f, "تدوير الكتف", "دمبلز"),
                        ProgramDayExerciseItem("core_plank", "بلانك ثبات للكور", "Core Plank", 3, "45-60 ثانية", 60, 8.0f, "تثبيت العمود الفقري", "وزن الجسم")
                    )
                ),
                estimatedDurationMinutes = 45
            )
        )

        return days
    }
}
