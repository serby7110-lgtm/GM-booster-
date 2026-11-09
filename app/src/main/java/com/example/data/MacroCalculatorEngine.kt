package com.example.data

enum class CalorieGoalType(val id: String, val titleArabic: String, val calorieAdjustment: Int, val descriptionArabic: String) {
    BULKING("BULKING", "تضخيم وبناء عضلات (Hypertrophy)", 400, "فائض طاقة معتدل لتغذية البناء العضلي وتخليق البروتين مع أقل نسبة دهون."),
    CUTTING("CUTTING", "تنشيف وحرق دهون (Fat Loss)", -500, "عجز سعرات ذكي لحرق الدهون الحشوية والعنيدة مع الحفاظ التام على الكتلة العضلية."),
    MAINTENANCE("MAINTENANCE", "زيادة قوة وثبات الوزن (Strength)", 0, "موازنة دقيقة لطاقة الجسم لدعم رفع الأوزان الثقيلة والاستشفاء السريع.")
}

data class MacroCalculationResult(
    val bmrCalories: Int,
    val tdeeCalories: Int,
    val targetCalories: Int,
    val proteinGrams: Int,
    val proteinCalories: Int,
    val carbsGrams: Int,
    val carbsCalories: Int,
    val fatGrams: Int,
    val fatCalories: Int,
    val recommendedWaterMl: Int,
    val mealSplitSuggestion: String,
    val proDietTipArabic: String
)

object MacroCalculatorEngine {

    fun calculate(
        gender: String, // ذكر / أنثى
        weightKg: Float,
        heightCm: Float,
        age: Int,
        activityLevel: String,
        goalType: CalorieGoalType
    ): MacroCalculationResult {
        // Mifflin-St Jeor Equation
        val baseBmr = (10f * weightKg) + (6.25f * heightCm) - (5f * age)
        val bmr = if (gender.contains("أنثى") || gender.equals("female", ignoreCase = true)) {
            (baseBmr - 161f).toInt()
        } else {
            (baseBmr + 5f).toInt()
        }

        // TDEE multiplier
        val multiplier = when (activityLevel.uppercase()) {
            "SEDENTARY" -> 1.2f
            "LIGHT" -> 1.375f
            "MODERATE" -> 1.55f
            "VERY_ACTIVE", "HIGH" -> 1.725f
            "ATHLETE" -> 1.9f
            else -> 1.55f
        }

        val tdee = (bmr * multiplier).toInt()
        val targetCalories = (tdee + goalType.calorieAdjustment).coerceAtLeast(1400)

        // Protein calculation (grams per kg)
        val proteinPerKg = when (goalType) {
            CalorieGoalType.BULKING -> 2.1f
            CalorieGoalType.CUTTING -> 2.4f
            CalorieGoalType.MAINTENANCE -> 2.0f
        }
        val proteinGrams = (weightKg * proteinPerKg).toInt().coerceAtLeast(100)
        val proteinCalories = proteinGrams * 4

        // Fats calculation (25% of target calories)
        val fatCalories = (targetCalories * 0.25f).toInt()
        val fatGrams = (fatCalories / 9).coerceAtLeast(40)

        // Carbs calculation (remaining calories)
        val remainingCalories = (targetCalories - proteinCalories - (fatGrams * 9)).coerceAtLeast(400)
        val carbsGrams = (remainingCalories / 4)
        val carbsCalories = carbsGrams * 4

        // Daily water
        val waterMl = ((weightKg * 38f) + if (goalType == CalorieGoalType.BULKING) 500f else 300f).toInt()

        val mealSplit = "وزع هذا المجموع على 4-5 وجبات بمعدل ${(proteinGrams / 4.5f).toInt()}غ بروتين في كل وجبة."

        val proTip = when (goalType) {
            CalorieGoalType.BULKING -> "💡 ركز على الكاربوهيدرات المعقدة (أرز بسمتي، شوفان، بطاطا حلوة) قبل وبعد التمرين لشحن مخازن الجليكوجين."
            CalorieGoalType.CUTTING -> "💡 احرص على تناول كمية وفيرة من الخضار الورقية وشرب الماء لزيادة الشبع واستقرار مستويات هرمون الأنسولين."
            CalorieGoalType.MAINTENANCE -> "💡 ثبات السعرات يمنحك بيئة مثالية لزيادة أوزانك في التمارين المركبة أسبوعياً (Progressive Overload)."
        }

        return MacroCalculationResult(
            bmrCalories = bmr,
            tdeeCalories = tdee,
            targetCalories = targetCalories,
            proteinGrams = proteinGrams,
            proteinCalories = proteinCalories,
            carbsGrams = carbsGrams,
            carbsCalories = carbsCalories,
            fatGrams = fatGrams,
            fatCalories = fatGrams * 9,
            recommendedWaterMl = waterMl,
            mealSplitSuggestion = mealSplit,
            proDietTipArabic = proTip
        )
    }
}
