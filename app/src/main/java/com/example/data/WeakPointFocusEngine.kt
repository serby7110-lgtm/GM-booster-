package com.example.data

data class WeakPointFocusPlan(
    val titleArabic: String,
    val targetMuscle: MuscleGroup,
    val descriptionArabic: String,
    val weeklyFrequency: String,
    val specializationStrategyArabic: String,
    val routineDays: List<WeakPointDaySchedule>
)

data class WeakPointDaySchedule(
    val dayNameArabic: String,
    val focusTitleArabic: String,
    val isPrimaryWeakPointDay: Boolean,
    val exercises: List<Exercise>,
    val proTipArabic: String
)

object WeakPointFocusEngine {

    fun generatePlanForMuscle(muscle: MuscleGroup, environment: WorkoutEnvironment = WorkoutEnvironment.BOTH): WeakPointFocusPlan {
        val allExercises = WorkoutDatabase.allExercises.filter {
            if (environment == WorkoutEnvironment.BOTH) true else it.environment == environment || it.environment == WorkoutEnvironment.BOTH
        }

        return when (muscle) {
            MuscleGroup.LEGS_QUADS, MuscleGroup.LEGS_HAMSTRINGS -> {
                val legExercises = allExercises.filter { it.category == WorkoutCategory.LEGS }
                val quadFocus = legExercises.filter { it.nameArabic.contains("سكوات") || it.nameArabic.contains("دفع") || it.nameArabic.contains("فخذ") || it.nameArabic.contains("طعن") }
                val posteriorFocus = legExercises.filter { it.nameArabic.contains("ديدليفت") || it.nameArabic.contains("جلوتس") || it.nameArabic.contains("سمانة") }
                val upperSupporting = allExercises.filter { it.category == WorkoutCategory.CHEST || it.category == WorkoutCategory.BACK }.take(3)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول تفجير عضلات الأرجل وسحق نقطة الضعف",
                    targetMuscle = muscle,
                    descriptionArabic = "خطة مكثفة تعتمد على تمرين الأرجل مرتين أسبوعياً (يوم تركيز أمامي ويوم تركيز خلفي) مع زيادة المدى الحركي والتركيز السلبي (Eccentric tempo).",
                    weeklyFrequency = "2-3 مرات أسبوعياً",
                    specializationStrategyArabic = "أولوية البداية بالأرجل في أول الجلسة عند قمة طاقة الجليكوجين، مع رفع الحمل التدريبي بنسبة 35% وتأخير باقي العضلات للمحافظة.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (التركيز الأساسي)",
                            focusTitleArabic = "تفجير الفخذ الأمامي (Quads Focus) والسمانة",
                            isPrimaryWeakPointDay = true,
                            exercises = (quadFocus.take(4) + legExercises.take(1)).distinct(),
                            proTipArabic = "انزل في السكوات ببطء (3 ثوانٍ نزول وثانية توقف في القاع) لتفعيل أقصى عدد من الألياف العضلية البطيئة والسريعة."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (مساند)",
                            focusTitleArabic = "الجزء العلوي متوازن (دفع وسحب)",
                            isPrimaryWeakPointDay = false,
                            exercises = upperSupporting,
                            proTipArabic = "تدريب للمحافظة على الجزء العلوي دون استنزاف الجهاز العصبي المركزي."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (التركيز الثاني)",
                            focusTitleArabic = "سحق الفخذ الخلفي (Hamstrings) والمؤخرة والبطات",
                            isPrimaryWeakPointDay = true,
                            exercises = (posteriorFocus.take(4) + quadFocus.take(1)).distinct(),
                            proTipArabic = "التركيز على إطالة العضلة الخلفية بالديدليفت الروماني مع المحافظة على استقامة العمود الفقري تماماً."
                        )
                    )
                )
            }

            MuscleGroup.CHEST -> {
                val chestExercises = allExercises.filter { it.category == WorkoutCategory.CHEST }
                val tricepAndShoulder = allExercises.filter { it.category == WorkoutCategory.SHOULDERS_ARMS }.take(3)
                val backAndLegs = allExercises.filter { it.category == WorkoutCategory.BACK || it.category == WorkoutCategory.LEGS }.take(4)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول بناء درع الصدر ثلاثي الأبعاد",
                    targetMuscle = muscle,
                    descriptionArabic = "برنامج مخصص لملء زوايا الصدر الضعيفة (الصدر العلوي والداخلي) بزيادة التردد إلى مرتين أسبوعياً مع تكنيك عصر العضلة (Peak Contraction).",
                    weeklyFrequency = "مرتان أسبوعياً بفاصل 72 ساعة",
                    specializationStrategyArabic = "البدء بزوايا الصدر العلوي المائل 30 درجة لملء الجزء العلوي الذي يعاني منه 80% من المتدربين.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (التركيز الأساسي)",
                            focusTitleArabic = "الصدر العلوي والقوة الانفجارية + ترايسبس",
                            isPrimaryWeakPointDay = true,
                            exercises = (chestExercises.take(4) + tricepAndShoulder.take(2)).distinct(),
                            proTipArabic = "اضغط على عضلات الصدر بقوة في قمة كل عدة لمدة ثانية كاملة لتعزيز الارتباط العصبي العضلي."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (مساند)",
                            focusTitleArabic = "الظهر والأرجل الشاملة",
                            isPrimaryWeakPointDay = false,
                            exercises = backAndLegs,
                            proTipArabic = "حافظ على توازن القوة بين عضلات السحب والدفع لمنع انحناء الكتفين للأمام."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (التركيز الثاني)",
                            focusTitleArabic = "تمديد الصدر والمضخة العضلية (Pumping & Stretch)",
                            isPrimaryWeakPointDay = true,
                            exercises = (chestExercises.reversed().take(4) + tricepAndShoulder.take(1)).distinct(),
                            proTipArabic = "ركز على تمارين التفتيح (Flyes) بأقصى إطالة آمنة لتحفيز غشاء اللفافة العضلية (Fascia)."
                        )
                    )
                )
            }

            MuscleGroup.BICEPS, MuscleGroup.TRICEPS -> {
                val armExercises = allExercises.filter { it.category == WorkoutCategory.SHOULDERS_ARMS }
                val chestAndBack = allExercises.filter { it.category == WorkoutCategory.CHEST || it.category == WorkoutCategory.BACK }.take(3)
                val legs = allExercises.filter { it.category == WorkoutCategory.LEGS }.take(3)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول تضخيم الذراعين الخارق (Arm Specialization)",
                    targetMuscle = muscle,
                    descriptionArabic = "برنامج عالي الشدة والتركيز مخصص لكسر ثبات حجم الذراعين باستهداف الرأس الطويل والقصير وعضلة البراكياليس.",
                    weeklyFrequency = "3 مرات أسبوعياً بحجم مدروس",
                    specializationStrategyArabic = "تطبيق مجموعات الدروب سيت (Drop sets) والسوبر سيت المعاكس بين الباي والتراي لتحقيق أقصى ضخ دموي.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (جلسة الذراعين الملحمية)",
                            focusTitleArabic = "سوبر سيت بايسبس وترايسبس مكثف",
                            isPrimaryWeakPointDay = true,
                            exercises = armExercises.take(5),
                            proTipArabic = "ثبت كوعيك بجانب جذعك ولا تستخدم عزم التأرجح من الظهر أبداً لضمان عزل العضلة 100%."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (جسم كامل مساند)",
                            focusTitleArabic = "أرجل وصدر مساند",
                            isPrimaryWeakPointDay = false,
                            exercises = (legs.take(2) + chestAndBack.take(2)),
                            proTipArabic = "حافظ على المجموعات دون الفشل العضلي التام لتوفير طاقة الاستشفاء للذراعين."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (التركيز النهائي)",
                            focusTitleArabic = "قمة البايسبس والحزام الوحشي للترايسبس",
                            isPrimaryWeakPointDay = true,
                            exercises = armExercises.reversed().take(4),
                            proTipArabic = "أداء العدات الأخيرة بتكنيك التوقف المؤقت (Rest-Pause) لتحفيز النمو العضلي السريع."
                        )
                    )
                )
            }

            MuscleGroup.BACK -> {
                val backExercises = allExercises.filter { it.category == WorkoutCategory.BACK }
                val chestAndShoulders = allExercises.filter { it.category == WorkoutCategory.CHEST || it.category == WorkoutCategory.SHOULDERS_ARMS }.take(3)
                val legs = allExercises.filter { it.category == WorkoutCategory.LEGS }.take(3)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول الظهر العريض ثلاثي الأبعاد (V-Taper)",
                    targetMuscle = muscle,
                    descriptionArabic = "برنامج متقدم لبناء كثافة وعرض عضلات الظهر والمجنص، مع تصحيح انحناء الأكتاف وتقوية العمود الفقري.",
                    weeklyFrequency = "مرتان أسبوعياً بتركيزين مختلفين (عرض وكثافة)",
                    specializationStrategyArabic = "يوم مخصص لعرض الظهر (Lat Width) بسحب عمودي، ويوم مخصص لسماكة الظهر (Thickness) بسحب أفقي وديدليفت.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (عرض المجنص V-Taper)",
                            focusTitleArabic = "سحب عمودي عريض وعقلة + بايسبس",
                            isPrimaryWeakPointDay = true,
                            exercises = backExercises.filter { it.nameArabic.contains("عقلة") || it.nameArabic.contains("سحب") || it.nameArabic.contains("مجنص") }.take(4),
                            proTipArabic = "اسحب بكوعيك للأسفل وليس بيديك لتشغيل المجنص بدلاً من استهلاك طاقة البايسبس."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (مساند)",
                            focusTitleArabic = "الصدر والأكتاف",
                            isPrimaryWeakPointDay = false,
                            exercises = chestAndShoulders,
                            proTipArabic = "احرص على إطالة عضلات الصدر جيداً بعد التمرين للحفاظ على الوضعية المستقيمة للكتفين."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (كثافة وسمك الظهر)",
                            focusTitleArabic = "ديدليفت وتجديف بالبار والدمبلز + الترابيس",
                            isPrimaryWeakPointDay = true,
                            exercises = backExercises.filter { it.nameArabic.contains("تجديف") || it.nameArabic.contains("ديدليفت") || it.nameArabic.contains("بار") }.take(4),
                            proTipArabic = "اقفل لوحي الكتف (Scapula Retraction) في قمة كل سحبة مع إبقاء أسفل الظهر مشدوداً ومستقيماً."
                        )
                    )
                )
            }

            MuscleGroup.SHOULDERS -> {
                val shoulderExercises = allExercises.filter { it.category == WorkoutCategory.SHOULDERS_ARMS }
                val chestAndArms = allExercises.filter { it.category == WorkoutCategory.CHEST || it.category == WorkoutCategory.SHOULDERS_ARMS }.take(3)
                val backAndLegs = allExercises.filter { it.category == WorkoutCategory.BACK || it.category == WorkoutCategory.LEGS }.take(3)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول الأكتاف الدائرية ثلاثية الأبعاد (3D Shoulders)",
                    targetMuscle = muscle,
                    descriptionArabic = "استهداف خاص للكتف الجانبي والخلفي لإعطاء المظهر العريض والمتناسق، مع حماية أوتار الكتف من الاختناق.",
                    weeklyFrequency = "مرتان أسبوعياً بحجم ذكي",
                    specializationStrategyArabic = "إعطاء الأولوية للرفرفة الجانبية والكتف الخلفي مع تجنب الأوزان المفرطة في الدفع لحماية مفصل الروتاتور كف.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (الكتف الجانبي والدفع)",
                            focusTitleArabic = "دفع عسكري ورفرفة جانبية مكثفة",
                            isPrimaryWeakPointDay = true,
                            exercises = shoulderExercises.take(4),
                            proTipArabic = "في الرفرفة الجانبية، ارفع كوعك بمستوى كتفك واثنِ مفصل الكوع بدرجة بسيطة جداً."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (مساند)",
                            focusTitleArabic = "أرجل وظهر مساند",
                            isPrimaryWeakPointDay = false,
                            exercises = backAndLegs,
                            proTipArabic = "استمر في تدريب باقي الجسم بتكرارات معتدلة لضمان التعافي الأسرع للكتف."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (الكتف الخلفي والتناسق)",
                            focusTitleArabic = "رفرفة خلفية ثلاثية وسحب للوجه (Face Pulls)",
                            isPrimaryWeakPointDay = true,
                            exercises = shoulderExercises.reversed().take(4),
                            proTipArabic = "الكتف الخلفي هو السر الحقيقي لمظهر الكتف الدائري الكامل وحماية مفصل الكتف من آلام الصدر."
                        )
                    )
                )
            }

            MuscleGroup.CORE, MuscleGroup.CALVES -> {
                val coreExercises = allExercises.filter { it.category == WorkoutCategory.CORE_ABS || it.category == WorkoutCategory.HIIT_CARDIO }
                val fullBody = allExercises.filter { it.category == WorkoutCategory.CHEST || it.category == WorkoutCategory.BACK || it.category == WorkoutCategory.LEGS }.take(4)

                WeakPointFocusPlan(
                    titleArabic = "بروتوكول تفجير عضلات الكور والبطن الصلبة والسمانة",
                    targetMuscle = muscle,
                    descriptionArabic = "خطة مكثفة لبناء عضلات البطن العميقة (Transverse Abdominis) والبطات ببروتوكولات التوتر المستمر.",
                    weeklyFrequency = "3-4 مرات أسبوعياً بعد التمارين الرئيسية",
                    specializationStrategyArabic = "تمارين ثبات (Isometric) مع تمارين ثني العمود الفقري المتحكم بها بدون إجهاد أسفل الظهر.",
                    routineDays = listOf(
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 1 (نحت عضلات البطن العلوية والسفلية)",
                            focusTitleArabic = "بلانك ورفع الأرجل الدائري + سمانة",
                            isPrimaryWeakPointDay = true,
                            exercises = coreExercises.take(4),
                            proTipArabic = "ازفر الهواء بالكامل في كل انقباض لعضلات البطن لتشغيل الألياف العميقة."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 2 (قوة الجسم كاملة)",
                            focusTitleArabic = "تمارين القوة المركبة للجسم",
                            isPrimaryWeakPointDay = false,
                            exercises = fullBody,
                            proTipArabic = "اشدد عضلات البطن أثناء تمارين السكوات والديدليفت لحماية العمود الفقري."
                        ),
                        WeakPointDaySchedule(
                            dayNameArabic = "اليوم 3 (عضلات الخصر والثبات الديناميكي)",
                            focusTitleArabic = "بلانك جانبي وتويرست روسي ومقاومة دوران",
                            isPrimaryWeakPointDay = true,
                            exercises = coreExercises.reversed().take(4),
                            proTipArabic = "حافظ على حوضك ثابتاً وتجنب الالتواء السريع لحماية فقرات أسفل الظهر."
                        )
                    )
                )
            }
        }
    }
}
