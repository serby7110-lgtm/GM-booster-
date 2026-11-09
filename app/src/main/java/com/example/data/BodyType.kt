package com.example.data

enum class BodyType(
    val arabicName: String,
    val englishName: String,
    val indonesianName: String,
    val shortDesc: String,
    val shortDescEn: String,
    val shortDescId: String,
    val detailedAdvice: String,
    val recommendedCalorieAdjustment: Int, // e.g. +400 surplus or -350 deficit
    val carbRatio: Float,
    val proteinRatio: Float,
    val fatRatio: Float,
    val workoutFocus: String
) {
    ECTOMORPH(
        arabicName = "اكتومورف (Ectomorph)",
        englishName = "Ectomorph",
        indonesianName = "Ektomorf",
        shortDesc = "بنية نحيفة، أيض سريع وصعوبة في زيادة الوزن والكتلة العضلية.",
        shortDescEn = "Lean build, fast metabolism, difficulty gaining muscle mass.",
        shortDescId = "Postur kurus, metabolisme cepat, sulit menambah massa otot.",
        detailedAdvice = "يحتاج جسمك إلى فائض حراري غني بالكربوهيدرات المعقدة والبروتين، مع تقليل الكارديو الطويل والتركيز على التمارين المركبة الثقيلة وفترات راحة كافية بين المجموعات.",
        recommendedCalorieAdjustment = 450,
        carbRatio = 0.50f,
        proteinRatio = 0.30f,
        fatRatio = 0.20f,
        workoutFocus = "تمارين مركبة ثقيلة مع فترات راحة 2-3 دقيقة ومجموعات من 6-10 تكرارات"
    ),
    MESOMORPH(
        arabicName = "ميزومورف (Mesomorph)",
        englishName = "Mesomorph",
        indonesianName = "Mesomorf",
        shortDesc = "بنية رياضية متوازنة، سهولة بناء العضلات واستجابة سريعة للتمارين.",
        shortDescEn = "Athletic build, easily builds muscle and responds fast to training.",
        shortDescId = "Postur atletis, mudah membentuk otot dan responsif terhadap latihan.",
        detailedAdvice = "جسمك يستجيب بامتياز للتنويع بين رفع الأوزان والكارديو عالي الشدة (HIIT). حافظ على توازن متساوٍ بين البروتين والكارب مع زيادة تدريجية في الأوزان.",
        recommendedCalorieAdjustment = 100,
        carbRatio = 0.40f,
        proteinRatio = 0.35f,
        fatRatio = 0.25f,
        workoutFocus = "تمارين القوة والبناء العضلي المتوازن مع مجموعات 8-12 تكرار وكارديو متوسط"
    ),
    ENDOMORPH(
        arabicName = "إندومورف (Endomorph)",
        englishName = "Endomorph",
        indonesianName = "Endomorf",
        shortDesc = "بنية ممتلئة، أيض أبطأ وميل لتخزين الدهون مع قابلية جيدة لبناء القوة.",
        shortDescEn = "Stocky build, slower metabolism, stores fat easily with high strength potential.",
        shortDescId = "Postur berisi, metabolisme lambat, mudah menyimpan lemak dengan potensi kekuatan tinggi.",
        detailedAdvice = "يحتاج جسمك إلى نظام غذائي غني بالبروتين والدهون الصحية مع كربوهيدرات منخفضة ومدروسة التوقيت، مع دمج تمارين المقاومة الدائرية والكارديو لحرق أقصى قدر من الدهون.",
        recommendedCalorieAdjustment = -400,
        carbRatio = 0.25f,
        proteinRatio = 0.45f,
        fatRatio = 0.30f,
        workoutFocus = "تمارين دائرية وسوبر سيت مع فترات راحة قصيرة (30-60 ثانية) وHIIT مكثف"
    );

    fun getLocalizedName(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> arabicName
        AppLanguage.ENGLISH -> englishName
        AppLanguage.INDONESIAN -> indonesianName
    }

    fun getLocalizedDesc(lang: AppLanguage): String = when (lang) {
        AppLanguage.ARABIC -> shortDesc
        AppLanguage.ENGLISH -> shortDescEn
        AppLanguage.INDONESIAN -> shortDescId
    }
}

enum class FitnessGoal(val arabicTitle: String) {
    BUILD_MUSCLE("تضخيم وبناء عضلات صافية"),
    LOSE_FAT("تنشيف وخسارة دهون الجسم"),
    LEAN_FITNESS("تحسين اللياقة والمرونة العامة"),
    MAX_STRENGTH("زيادة القوة البدنية والتحمل")
}

enum class ActivityLevel(val arabicTitle: String, val multiplier: Float) {
    SEDENTARY("قليل الحركة (مكتبي)", 1.2f),
    LIGHT("نشاط خفيف (1-3 أيام أسبوعياً)", 1.375f),
    MODERATE("نشاط متوسط (3-5 أيام أسبوعياً)", 1.55f),
    VERY_ACTIVE("نشاط عالي (6-7 أيام أسبوعياً)", 1.725f)
}
