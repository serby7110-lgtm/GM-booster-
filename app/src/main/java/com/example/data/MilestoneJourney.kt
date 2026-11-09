package com.example.data

import com.example.data.database.DailyStepLogEntity
import com.example.data.database.GpsTrackEntity
import com.example.data.database.PersonalRecordEntity
import com.example.data.database.WeightProgressEntity
import com.example.data.database.WorkoutLogEntity

enum class MilestoneCategory(
    val titleArabic: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val subtitleArabic: String
) {
    ALL("الكل", "All", "🌟", "جميع محطاتك وإنجازاتك الرياضية"),
    STRENGTH("القوة والأوزان", "Strength", "🏋️", "أرقامك القياسية في البنش، السكوات والديدلفت وحمولات الأوزان"),
    STREAK("الاستمرارية والانضباط", "Streaks", "🔥", "سلاسل الأيام المتتالية وبناء العادات الرياضية الراسخة"),
    TRANSFORMATION("التحول والوزن", "Transformation", "🥗", "تغيرات الوزن، نسب الدهون والالتزام بالتغذية"),
    ENDURANCE("اللياقة والمسافات", "Endurance", "🏃", "مسافات الجري بالـ GPS وخطوات المشي اليومية")
}

enum class MilestoneTier(
    val titleArabic: String,
    val colorHex: Long,
    val strokeColorHex: Long
) {
    BRONZE("برونزي", 0xFFCD7F32, 0xFFE69A4E),
    SILVER("فضي", 0xFFC0C0C0, 0xFFE0E0E0),
    GOLD("ذهبي", 0xFFFFD700, 0xFFFFF176),
    PLATINUM("بلاتيني", 0xFF00E5FF, 0xFF80D8FF),
    DIAMOND("ألماسي", 0xFFB388FF, 0xFFD1C4E9)
}

data class FitnessMilestone(
    val id: String,
    val category: MilestoneCategory,
    val tier: MilestoneTier,
    val titleArabic: String,
    val titleEnglish: String,
    val descriptionArabic: String,
    val targetMetric: Float,
    val currentMetric: Float,
    val unitArabic: String,
    val xpReward: Int,
    val iconEmoji: String,
    val isUnlocked: Boolean = false,
    val isClaimed: Boolean = false,
    val unlockedDate: String? = null,
    val tipsArabic: String = ""
) {
    val progressRatio: Float
        get() = if (targetMetric <= 0f) 0f else (currentMetric / targetMetric).coerceIn(0f, 1f)

    val progressPercent: Int
        get() = (progressRatio * 100).toInt()
}

data class MilestoneCategoryProgress(
    val category: MilestoneCategory,
    val totalCount: Int,
    val unlockedCount: Int,
    val claimedCount: Int,
    val totalXpEarned: Int,
    val totalXpPossible: Int
) {
    val completionPercent: Int
        get() = if (totalCount == 0) 0 else ((unlockedCount.toFloat() / totalCount.toFloat()) * 100).toInt()
}

object MilestoneEngine {

    val allMilestoneTemplates: List<FitnessMilestone> = listOf(
        // ==================== STRENGTH MILESTONES ====================
        FitnessMilestone(
            id = "ms_bench_60",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.BRONZE,
            titleArabic = "نادي الستين بنش برس (60 كجم)",
            titleEnglish = "60kg Bench Club",
            descriptionArabic = "تحقيق رقم قياسي 60 كجم في تمرين ضغط الصدر بالبار المستوي",
            targetMetric = 60f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 80,
            iconEmoji = "🏋️",
            tipsArabic = "ثبّت قوس أسفل الظهر وأحكم قبضة يديك لدفع الوزن بثبات وانفجارية."
        ),
        FitnessMilestone(
            id = "ms_bench_80",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.SILVER,
            titleArabic = "حاجز الثمانين كجم (80 كجم)",
            titleEnglish = "80kg Bench Milestone",
            descriptionArabic = "دفع 80 كجم في البنش برس مع ثبات المسار الحركي الكامل",
            targetMetric = 80f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 150,
            iconEmoji = "💪",
            tipsArabic = "زد الحمل التدريجي بنصف كجم أسبوعياً واهتم بتمارين الترايسبس والأكتاف المساعدة."
        ),
        FitnessMilestone(
            id = "ms_bench_100",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.GOLD,
            titleArabic = "نادي المئة الأسطوري (100 كجم)",
            titleEnglish = "Century Bench Club (100kg)",
            descriptionArabic = "الانضمام رسمياً لنخبة رافعي المئة كجم بنش برس (2 ألواح خضراء كاملة)",
            targetMetric = 100f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 350,
            iconEmoji = "👑",
            tipsArabic = "إنجاز جبار يُميّز أقوى أبطال الصالة! حافظ على الإحماء الممتاز واستعن بمساعد أثناء الرفعة."
        ),
        FitnessMilestone(
            id = "ms_squat_80",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.BRONZE,
            titleArabic = "قواعد السكوات الصلبة (80 كجم)",
            titleEnglish = "80kg Solid Squat",
            descriptionArabic = "نزول كامل متوازي ورفع 80 كجم في السكوات الخلفي",
            targetMetric = 80f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 100,
            iconEmoji = "🦵",
            tipsArabic = "حافظ على الصدر مرتفعاً والركبتين باتجاه أطراف الأصابع مع التنفس البطني العميق."
        ),
        FitnessMilestone(
            id = "ms_squat_120",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.SILVER,
            titleArabic = "سكوات الأرجل الفولاذية (120 كجم)",
            titleEnglish = "120kg Steel Legs Squat",
            descriptionArabic = "كسر حاجز 120 كجم سكوات بنزول مثالي وقوة ارتداد عضلي",
            targetMetric = 120f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 220,
            iconEmoji = "⚡",
            tipsArabic = "عزز قوة عضلات الجذع والسمانة لضمان استقرار العمود الفقري تحت الأوزان الثقيلة."
        ),
        FitnessMilestone(
            id = "ms_squat_160",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.DIAMOND,
            titleArabic = "سكوات الجبابرة (160 كجم)",
            titleEnglish = "160kg Titan Squat",
            descriptionArabic = "قوة استثنائية تفوق التوقعات بحمل 160 كجم في السكوات الكامل",
            targetMetric = 160f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 500,
            iconEmoji = "🦍",
            tipsArabic = "هنا تُصنع أساطير القوة البدنية! استخدم حزام رفع الأثقال وتأكد من ثبات القدمين بالأرض."
        ),
        FitnessMilestone(
            id = "ms_deadlift_100",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.BRONZE,
            titleArabic = "ديدلفت البدايات القوية (100 كجم)",
            titleEnglish = "100kg Deadlift Foundation",
            descriptionArabic = "رفع 100 كجم من الأرض باستقامة ظهر كاملة في الرفعة الميتة",
            targetMetric = 100f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 120,
            iconEmoji = "🎯",
            tipsArabic = "ابدأ بسحب البار ملاصقاً لقصبة الساق وفكّر في دفع الأرض بقدميك بدلاً من مجرد سحب الوزن بظهرك."
        ),
        FitnessMilestone(
            id = "ms_deadlift_150",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.GOLD,
            titleArabic = "ديدلفت الحديد المسلح (150 كجم)",
            titleEnglish = "150kg Heavy Deadlift",
            descriptionArabic = "إقفال رفعة 150 كجم ديدلفت مع تفعيل عضلات المؤخرة واللاتس بالكامل",
            targetMetric = 150f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 300,
            iconEmoji = "🛡️",
            tipsArabic = "القبضة العكسية أو استخدام الباودر يساعد كثيراً على تجاوز تعب الأصابع والقبضة."
        ),
        FitnessMilestone(
            id = "ms_deadlift_200",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.DIAMOND,
            titleArabic = "أسطورة الـ 200 كجم ديدلفت",
            titleEnglish = "200kg Deadlift Beast",
            descriptionArabic = "الوصول إلى قمة القوة القصوى ورفع 200 كجم من على الأرض",
            targetMetric = 200f,
            currentMetric = 0f,
            unitArabic = "كجم",
            xpReward = 750,
            iconEmoji = "🏆",
            tipsArabic = "إنجاز لا يُحققه سوى 1% من ممارسي رياضة كمال الأجسام والقوة! تحية لقوتك الأسطورية."
        ),
        FitnessMilestone(
            id = "ms_volume_10k",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.SILVER,
            titleArabic = "طاحونة الأوزان (10,000 كجم حجم)",
            titleEnglish = "10-Ton Volume Crusher",
            descriptionArabic = "رفع حجم تدريبي تراكمي يتجاوز 10,000 كجم (الوزن × التكرارات) في التمارين",
            targetMetric = 10000f,
            currentMetric = 0f,
            unitArabic = "كجم حجم",
            xpReward = 180,
            iconEmoji = "⚙️",
            tipsArabic = "الحجم التدريبي (Volume) هو المحرك الأساسي للتضخيم العضلي وزيادة السعة التدريبية."
        ),
        FitnessMilestone(
            id = "ms_volume_50k",
            category = MilestoneCategory.STRENGTH,
            tier = MilestoneTier.PLATINUM,
            titleArabic = "عملاق الخمسين طناً (50,000 كجم)",
            titleEnglish = "50-Ton Mega Volume Titan",
            descriptionArabic = "تخطي حاجز 50 طناً من الأوزان المرفوعة التراكمية في جلساتك",
            targetMetric = 50000f,
            currentMetric = 0f,
            unitArabic = "كجم حجم",
            xpReward = 450,
            iconEmoji = "🌋",
            tipsArabic = "كتلة تدريبية ضخمة تظهر كفاءتك العالية في الاستشفاء وبناء القوة والتحمل."
        ),

        // ==================== STREAK & CONSISTENCY ====================
        FitnessMilestone(
            id = "ms_streak_3",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.BRONZE,
            titleArabic = "شعلة الحماس الأولى (3 أيام)",
            titleEnglish = "3-Day Flame Starter",
            descriptionArabic = "الالتزام بالتمارين والأنشطة لمدة 3 أيام متتالية دون انقطاع",
            targetMetric = 3f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 50,
            iconEmoji = "🔥",
            tipsArabic = "أول 3 أيام هي الأصعب في كسر الكسل وبناء العادة، استمر في الحفاظ على توهج الشعلة!"
        ),
        FitnessMilestone(
            id = "ms_streak_7",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.BRONZE,
            titleArabic = "الأسبوع الحديدي (7 أيام متتالية)",
            titleEnglish = "Iron Week (7-Day Streak)",
            descriptionArabic = "إتمام أسبوع كامل من الانضباط وتفعيل مضاعف نقاط الصعوبة 1.5x",
            targetMetric = 7f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 120,
            iconEmoji = "⚔️",
            tipsArabic = "فعل درع الشعلة (Streak Shield) في أيام الراحة لتضمن عدم فقدان السلسلة أبداً."
        ),
        FitnessMilestone(
            id = "ms_streak_14",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.SILVER,
            titleArabic = "محارب الأسبوعين (14 يوماً)",
            titleEnglish = "Fortnight Champion (14 Days)",
            descriptionArabic = "الاستمرار لنصف شهر وبناء مسار عصبي قوي للعادة الرياضية",
            targetMetric = 14f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 200,
            iconEmoji = "🛡️",
            tipsArabic = "الآن أصبحت التمارين جزءاً أصيلاً من روتينك اليومي، لم تعد بحاجة للحماس لأن الانضباط تولى المهمة."
        ),
        FitnessMilestone(
            id = "ms_streak_30",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.GOLD,
            titleArabic = "مهندس العادات (30 يوماً متواصلاً)",
            titleEnglish = "Habit Architect (30 Days)",
            descriptionArabic = "إكمال شهر كامل من الالتزام الرياضي وتحقيق الشعلة البلاتينية",
            targetMetric = 30f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 450,
            iconEmoji = "💎",
            tipsArabic = "30 يوماً كافية لإعادة تشكيل نمط حياتك بالكامل وصنع نسخة أقوى وأكثر صحة منك!"
        ),
        FitnessMilestone(
            id = "ms_streak_60",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.PLATINUM,
            titleArabic = "الانضباط الماسي (60 يوماً)",
            titleEnglish = "Diamond Discipline (60 Days)",
            descriptionArabic = "شهرين متتاليين من الإصرار الحديدي والتفوق على كل المشتتات والظروف",
            targetMetric = 60f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 700,
            iconEmoji = "✨",
            tipsArabic = "أنت قدوة ملهمة في مجتمع Gm! الجميع ينظر إلى استمراريتك بإعجاب وتقدير كبير."
        ),
        FitnessMilestone(
            id = "ms_streak_100",
            category = MilestoneCategory.STREAK,
            tier = MilestoneTier.DIAMOND,
            titleArabic = "المئوية الذهبية (100 يوم streak)",
            titleEnglish = "Centurion Streak (100 Days)",
            descriptionArabic = "بلوغ نادي المئة يوم متتالي في اللياقة والانضباط بلا هوادة",
            targetMetric = 100f,
            currentMetric = 0f,
            unitArabic = "أيام",
            xpReward = 1500,
            iconEmoji = "👑",
            tipsArabic = "إنجاز أسطوري يجعلك في طليعة قائمة المتصدرين مع لقب سفير الانضباط الرياضي."
        ),

        // ==================== TRANSFORMATION & NUTRITION ====================
        FitnessMilestone(
            id = "ms_trans_1kg",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.BRONZE,
            titleArabic = "أول علامة تحول (1 كجم نحو الهدف)",
            titleEnglish = "First Spark of Change (1kg)",
            descriptionArabic = "التقدم بمقدار 1 كجم نحو وزنك المستهدف المسجل في ملفك الشخصي",
            targetMetric = 1.0f,
            currentMetric = 0f,
            unitArabic = "كجم تحول",
            xpReward = 75,
            iconEmoji = "🌱",
            tipsArabic = "رحلة الألف ميل تبدأ بخطوة، والتحول الكبير يبدأ بأول كيلوجرام من التغيير الحقيقي."
        ),
        FitnessMilestone(
            id = "ms_trans_3kg",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.SILVER,
            titleArabic = "التغيير الملموس (3 كجم تحول)",
            titleEnglish = "3kg Noticeable Shift",
            descriptionArabic = "تحقيق 3 كجم فرق نحو الهدف مع ظهور النتائج بوضوح في المرآة",
            targetMetric = 3.0f,
            currentMetric = 0f,
            unitArabic = "كجم تحول",
            xpReward = 160,
            iconEmoji = "✨",
            tipsArabic = "هنا تبدأ بملاحظة توسع الملابس أو امتلاء القمصان بالعضلات، استمر بنفس الوتيرة!"
        ),
        FitnessMilestone(
            id = "ms_trans_5kg",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.GOLD,
            titleArabic = "التحول الأكبر (5 كجم فارق إيجابي)",
            titleEnglish = "5kg Major Metamorphosis",
            descriptionArabic = "إنجاز 5 كيلوجرامات نحو الوزن المثالي وبناء كتلة خالية من الدهون",
            targetMetric = 5.0f,
            currentMetric = 0f,
            unitArabic = "كجم تحول",
            xpReward = 320,
            iconEmoji = "🎯",
            tipsArabic = "فارق كبير جداً في شكل الجسم ومستوى الطاقة والنشاط الحيوي طوال اليوم."
        ),
        FitnessMilestone(
            id = "ms_trans_10kg",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.DIAMOND,
            titleArabic = "التحول الأسطوري الكامل (10 كجم)",
            titleEnglish = "10kg Supreme Transformation",
            descriptionArabic = "إعادة تشكيل جسدية كاملة بـ 10 كجم تغيير مقصود ومدروس",
            targetMetric = 10.0f,
            currentMetric = 0f,
            unitArabic = "كجم تحول",
            xpReward = 800,
            iconEmoji = "🌟",
            tipsArabic = "تحول جذري يُعد مصدر فخر وإلهام لا يُنسى! شارك إنجازك في مجتمع Gm الرياضي."
        ),
        FitnessMilestone(
            id = "ms_trans_water_7d",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.BRONZE,
            titleArabic = "شريان الترطيب (إكمال الماء المستهدف)",
            titleEnglish = "Hydration Habit Master",
            descriptionArabic = "الوصول للهدف اليومي من شرب الماء المسجل بملفك الشخصي (8 أكواب)",
            targetMetric = 8f,
            currentMetric = 0f,
            unitArabic = "أكواب ماء",
            xpReward = 60,
            iconEmoji = "💧",
            tipsArabic = "الترطيب الكافي يرفع معدل حرق الدهون ويساعد في طرد السموم وسرعة الاستشفاء العضلي."
        ),
        FitnessMilestone(
            id = "ms_trans_logs_10",
            category = MilestoneCategory.TRANSFORMATION,
            tier = MilestoneTier.SILVER,
            titleArabic = "أرشيف التطور (10 قياسات مسجلة)",
            titleEnglish = "Progress Tracker (10 Logs)",
            descriptionArabic = "تسجيل 10 قراءات وزن وصور في سجل تطورك الجسدي",
            targetMetric = 10f,
            currentMetric = 0f,
            unitArabic = "تسجيلات",
            xpReward = 140,
            iconEmoji = "📸",
            tipsArabic = "ما يمكن قياسه يمكن تحسينه دائماً! تتبع الوزن أسبوعياً يمنحك البوصلة الدقيقة لتعديل السعرات."
        ),

        // ==================== ENDURANCE & CARDIO ====================
        FitnessMilestone(
            id = "ms_end_5k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.BRONZE,
            titleArabic = "عدّاء الـ 5 كيلومترات (5km Run)",
            titleEnglish = "5K Run Milestone",
            descriptionArabic = "إكمال جولة مشي أو جري بمسافة 5 كيلومترات مسجلة بالـ GPS",
            targetMetric = 5.0f,
            currentMetric = 0f,
            unitArabic = "كم بجلسة واحدة",
            xpReward = 90,
            iconEmoji = "🏃",
            tipsArabic = "قسّم المسافة ذهنياً وتدرج في السرعة لتكمل الجري بنفس منتظم ودقات قلب مستقرة."
        ),
        FitnessMilestone(
            id = "ms_end_10k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.SILVER,
            titleArabic = "وحش العشرة كيلومترات (10km Beast)",
            titleEnglish = "10K Endurance Milestone",
            descriptionArabic = "كسر حاجز الـ 10 كم في جلسة تتبع GPS مفردة بقوة لياقة قلبية وتنفسية",
            targetMetric = 10.0f,
            currentMetric = 0f,
            unitArabic = "كم بجلسة واحدة",
            xpReward = 220,
            iconEmoji = "⚡",
            tipsArabic = "تأكد من شرب السوائل الكافية وارتداء حذاء جري مبطن لتقليل الصدمات على الركبتين."
        ),
        FitnessMilestone(
            id = "ms_end_21k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.GOLD,
            titleArabic = "بطل نصف الماراثون (21.1 كم)",
            titleEnglish = "Half-Marathon Champion (21.1km)",
            descriptionArabic = "إتمام مسافة نصف الماراثون الرسمية (21.1 كم) مسجلة بدقة عبر مسار GPS",
            targetMetric = 21.1f,
            currentMetric = 0f,
            unitArabic = "كم بجلسة واحدة",
            xpReward = 500,
            iconEmoji = "🏅",
            tipsArabic = "إنجاز رياضي خارق يضعك في مصاف عدائي المسافات الطويلة المحترفين!"
        ),
        FitnessMilestone(
            id = "ms_steps_50k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.BRONZE,
            titleArabic = "مستكشف الخطوات (50,000 خطوة)",
            titleEnglish = "50,000 Steps Explorer",
            descriptionArabic = "تسجيل 50 ألف خطوة تراكمية في سجلات النشاط اليومي لمستشعر الهاتف",
            targetMetric = 50000f,
            currentMetric = 0f,
            unitArabic = "خطوة تراكمية",
            xpReward = 80,
            iconEmoji = "👟",
            tipsArabic = "الحركة اليومية غير التمرينية (NEAT) تحرق مئات السعرات وتبقي مفاصلك مرنة وحيوية."
        ),
        FitnessMilestone(
            id = "ms_steps_100k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.SILVER,
            titleArabic = "فارس المئة ألف خطوة (100k Steps)",
            titleEnglish = "100,000 Steps Centurion",
            descriptionArabic = "تخطي حاجز 100,000 خطوة تراكمية في مسارك اليومي بالكامل",
            targetMetric = 100000f,
            currentMetric = 0f,
            unitArabic = "خطوة تراكمية",
            xpReward = 180,
            iconEmoji = "🎖️",
            tipsArabic = "عشرة أيام بمعدل 10 آلاف خطوة يومياً تصنع فارقاً هائلاً في صحة قلبك وشرايينك."
        ),
        FitnessMilestone(
            id = "ms_steps_250k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.PLATINUM,
            titleArabic = "بطل الربع مليون خطوة (250k Steps)",
            titleEnglish = "Quarter-Million Steps Legend",
            descriptionArabic = "تسجيل 250 ألف خطوة في سجل نشاطك الرياضي المتكامل",
            targetMetric = 250000f,
            currentMetric = 0f,
            unitArabic = "خطوة تراكمية",
            xpReward = 400,
            iconEmoji = "💫",
            tipsArabic = "مسافة هائلة تعادل قطع مئات الكيلومترات سيراً على الأقدام، استمر في المشي!"
        ),
        FitnessMilestone(
            id = "ms_steps_500k",
            category = MilestoneCategory.ENDURANCE,
            tier = MilestoneTier.DIAMOND,
            titleArabic = "عملاق النصف مليون خطوة (500k Steps)",
            titleEnglish = "Half-Million Steps Titan",
            descriptionArabic = "بلوغ قمة المشي واللياقة التراكمية بنصف مليون خطوة مسجلة",
            targetMetric = 500000f,
            currentMetric = 0f,
            unitArabic = "خطوة تراكمية",
            xpReward = 900,
            iconEmoji = "🌍",
            tipsArabic = "لقب عملاق اللياقة البدنية والنشاط اليومي! بصمة استثنائية في تطبيق Gm."
        )
    )

    fun evaluateMilestones(
        personalRecords: List<PersonalRecordEntity>,
        streakDays: Int,
        currentWeightKg: Float,
        targetWeightKg: Float,
        initialWeightKg: Float,
        waterGlasses: Int,
        weightLogs: List<WeightProgressEntity>,
        workoutLogs: List<WorkoutLogEntity>,
        gpsTracks: List<GpsTrackEntity>,
        dailyStepLogs: List<DailyStepLogEntity>,
        claimedIds: Set<String>
    ): List<FitnessMilestone> {

        // 1. Calculate Strength Metrics
        val benchPr = personalRecords.find { it.exerciseId.contains("bench", ignoreCase = true) || it.exerciseNameArabic.contains("صدر") || it.exerciseNameArabic.contains("بنش") }?.maxWeightKg ?: 0f
        val squatPr = personalRecords.find { it.exerciseId.contains("squat", ignoreCase = true) || it.exerciseNameArabic.contains("سكوات") || it.exerciseNameArabic.contains("أرجل") }?.maxWeightKg ?: 0f
        val deadliftPr = personalRecords.find { it.exerciseId.contains("deadlift", ignoreCase = true) || it.exerciseNameArabic.contains("ديدلفت") || it.exerciseNameArabic.contains("ظهر") }?.maxWeightKg ?: 0f

        // Total volume calculation across workout logs
        val totalVolumeKg = workoutLogs.sumOf { log ->
            (log.setsCompleted * 10 * 45).toDouble()
        }.toFloat()

        // 2. Transformation Delta Metric
        val targetDelta = kotlin.math.abs(targetWeightKg - initialWeightKg)
        val currentDelta = if (targetDelta > 0.1f) {
            val progressTowardsTarget = if (targetWeightKg < initialWeightKg) {
                // Weight loss goal
                (initialWeightKg - currentWeightKg).coerceAtLeast(0f)
            } else {
                // Bulking goal
                (currentWeightKg - initialWeightKg).coerceAtLeast(0f)
            }
            progressTowardsTarget
        } else {
            kotlin.math.abs(currentWeightKg - initialWeightKg)
        }

        val totalWeightLogsCount = weightLogs.size.toFloat()

        // 3. Endurance Metrics
        val maxSingleGpsDistanceKm = (gpsTracks.maxOfOrNull { it.distanceKm } ?: 0f)
        val totalCumulativeSteps = dailyStepLogs.sumOf { it.steps }.toFloat()

        return allMilestoneTemplates.map { template ->
            val currentVal = when (template.id) {
                // Strength
                "ms_bench_60", "ms_bench_80", "ms_bench_100" -> benchPr
                "ms_squat_80", "ms_squat_120", "ms_squat_160" -> squatPr
                "ms_deadlift_100", "ms_deadlift_150", "ms_deadlift_200" -> deadliftPr
                "ms_volume_10k", "ms_volume_50k" -> totalVolumeKg

                // Streaks
                "ms_streak_3", "ms_streak_7", "ms_streak_14",
                "ms_streak_30", "ms_streak_60", "ms_streak_100" -> streakDays.toFloat()

                // Transformation
                "ms_trans_1kg", "ms_trans_3kg", "ms_trans_5kg", "ms_trans_10kg" -> currentDelta
                "ms_trans_water_7d" -> waterGlasses.toFloat()
                "ms_trans_logs_10" -> totalWeightLogsCount

                // Endurance
                "ms_end_5k", "ms_end_10k", "ms_end_21k" -> maxSingleGpsDistanceKm
                "ms_steps_50k", "ms_steps_100k", "ms_steps_250k", "ms_steps_500k" -> totalCumulativeSteps

                else -> 0f
            }

            val unlocked = currentVal >= template.targetMetric
            val claimed = claimedIds.contains(template.id)

            template.copy(
                currentMetric = currentVal,
                isUnlocked = unlocked,
                isClaimed = claimed,
                unlockedDate = if (unlocked) "مكتملة ومتاحة للأبطال" else null
            )
        }
    }

    fun calculateCategoryProgress(
        milestones: List<FitnessMilestone>,
        category: MilestoneCategory
    ): MilestoneCategoryProgress {
        val filtered = if (category == MilestoneCategory.ALL) milestones else milestones.filter { it.category == category }
        val totalCount = filtered.size
        val unlockedCount = filtered.count { it.isUnlocked }
        val claimedCount = filtered.count { it.isClaimed }
        val totalXpEarned = filtered.filter { it.isClaimed }.sumOf { it.xpReward }
        val totalXpPossible = filtered.sumOf { it.xpReward }

        return MilestoneCategoryProgress(
            category = category,
            totalCount = totalCount,
            unlockedCount = unlockedCount,
            claimedCount = claimedCount,
            totalXpEarned = totalXpEarned,
            totalXpPossible = totalXpPossible
        )
    }
}
