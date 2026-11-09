package com.example.data

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flagEmoji: String,
    val isRtl: Boolean
) {
    ARABIC("ar", "العربية", "العربية", "🇸🇦", true),
    ENGLISH("en", "English", "English", "🇺🇸", false),
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "🇮🇩", false);

    companion object {
        fun fromCode(code: String): AppLanguage {
            return values().firstOrNull { it.code.equals(code, ignoreCase = true) } ?: ARABIC
        }
    }
}

object AppStrings {
    private val translations: Map<String, Map<AppLanguage, String>> = mapOf(
        // App title and branding
        "app_title" to mapOf(
            AppLanguage.ARABIC to "Gm للياقة والتمارين",
            AppLanguage.ENGLISH to "Gm Fitness & Workouts",
            AppLanguage.INDONESIAN to "Gm Kebugaran & Latihan"
        ),
        "tagline" to mapOf(
            AppLanguage.ARABIC to "تمارين المنزل والصالة • تتبع السعرات والماء • مجتمع ومكافآت",
            AppLanguage.ENGLISH to "Home & Gym Workouts • Calories & Water • Community & Rewards",
            AppLanguage.INDONESIAN to "Latihan Rumah & Gym • Kalori & Air • Komunitas & Hadiah"
        ),

        // Navigation
        "nav_home" to mapOf(
            AppLanguage.ARABIC to "الرئيسية",
            AppLanguage.ENGLISH to "Home",
            AppLanguage.INDONESIAN to "Beranda"
        ),
        "nav_workouts" to mapOf(
            AppLanguage.ARABIC to "التمارين",
            AppLanguage.ENGLISH to "Workouts",
            AppLanguage.INDONESIAN to "Latihan"
        ),
        "nav_nutrition" to mapOf(
            AppLanguage.ARABIC to "التغذية والماء",
            AppLanguage.ENGLISH to "Nutrition & Water",
            AppLanguage.INDONESIAN to "Nutrisi & Air"
        ),
        "nav_evolution" to mapOf(
            AppLanguage.ARABIC to "تطور الجسم",
            AppLanguage.ENGLISH to "Body Evolution",
            AppLanguage.INDONESIAN to "Evolusi Tubuh"
        ),
        "nav_community" to mapOf(
            AppLanguage.ARABIC to "المجتمع",
            AppLanguage.ENGLISH to "Community",
            AppLanguage.INDONESIAN to "Komunitas"
        ),
        "nav_rewards" to mapOf(
            AppLanguage.ARABIC to "المكافآت",
            AppLanguage.ENGLISH to "Rewards",
            AppLanguage.INDONESIAN to "Hadiah"
        ),
        "nav_settings" to mapOf(
            AppLanguage.ARABIC to "الإعدادات",
            AppLanguage.ENGLISH to "Settings",
            AppLanguage.INDONESIAN to "Pengaturan"
        ),

        // Water Tracker
        "water_tracker_title" to mapOf(
            AppLanguage.ARABIC to "تعقب شرب الماء اليومي",
            AppLanguage.ENGLISH to "Daily Water Intake Tracker",
            AppLanguage.INDONESIAN to "Pelacak Asupan Air Harian"
        ),
        "water_goal_calculated" to mapOf(
            AppLanguage.ARABIC to "الهدف المحسوب بناءً على وزنك ومستوى نشاطك",
            AppLanguage.ENGLISH to "Target calculated based on weight & activity level",
            AppLanguage.INDONESIAN to "Target dihitung berdasarkan berat badan & aktivitas"
        ),
        "water_consumed" to mapOf(
            AppLanguage.ARABIC to "المستهلك اليوم",
            AppLanguage.ENGLISH to "Consumed Today",
            AppLanguage.INDONESIAN to "Dikonsumsi Hari Ini"
        ),
        "water_remaining" to mapOf(
            AppLanguage.ARABIC to "المتبقي للوصول للهدف",
            AppLanguage.ENGLISH to "Remaining to Goal",
            AppLanguage.INDONESIAN to "Tersisa untuk Mencapai Target"
        ),
        "water_history_title" to mapOf(
            AppLanguage.ARABIC to "سجل شرب الماء والتاريخ",
            AppLanguage.ENGLISH to "Water Intake History & Logs",
            AppLanguage.INDONESIAN to "Riwayat & Catatan Minum Air"
        ),
        "water_quick_add" to mapOf(
            AppLanguage.ARABIC to "إضافة سريعة",
            AppLanguage.ENGLISH to "Quick Add",
            AppLanguage.INDONESIAN to "Tambah Cepat"
        ),
        "water_glass" to mapOf(
            AppLanguage.ARABIC to "كوب (250 مل)",
            AppLanguage.ENGLISH to "Glass (250 ml)",
            AppLanguage.INDONESIAN to "Gelas (250 ml)"
        ),
        "water_bottle" to mapOf(
            AppLanguage.ARABIC to "قارورة (500 مل)",
            AppLanguage.ENGLISH to "Bottle (500 ml)",
            AppLanguage.INDONESIAN to "Botol (500 ml)"
        ),
        "water_flask" to mapOf(
            AppLanguage.ARABIC to "مطارة (750 مل)",
            AppLanguage.ENGLISH to "Flask (750 ml)",
            AppLanguage.INDONESIAN to "Termos (750 ml)"
        ),
        "water_litre" to mapOf(
            AppLanguage.ARABIC to "لتر كامل (1000 مل)",
            AppLanguage.ENGLISH to "Full Liter (1000 ml)",
            AppLanguage.INDONESIAN to "Satu Liter (1000 ml)"
        ),
        "water_goal_reached" to mapOf(
            AppLanguage.ARABIC to "أحسنت! حققت هدف الترطيب اليومي بنجاح 💧",
            AppLanguage.ENGLISH to "Awesome! You reached your daily hydration goal 💧",
            AppLanguage.INDONESIAN to "Luar biasa! Anda mencapai target hidrasi harian 💧"
        ),
        "water_reminder_prompt" to mapOf(
            AppLanguage.ARABIC to "تذكير بشرب الماء للحفاظ على النشاط والأداء الرياضي",
            AppLanguage.ENGLISH to "Hydration reminder to boost metabolism & recovery",
            AppLanguage.INDONESIAN to "Pengingat minum air untuk menjaga stamina dan metabolisme"
        ),

        // Workouts
        "all_workouts" to mapOf(
            AppLanguage.ARABIC to "مكتبة التمارين الشاملة (+50 تمرين)",
            AppLanguage.ENGLISH to "Exercise Library (50+ Exercises)",
            AppLanguage.INDONESIAN to "Perpustakaan Latihan (50+ Latihan)"
        ),
        "filter_home" to mapOf(
            AppLanguage.ARABIC to "منزل",
            AppLanguage.ENGLISH to "Home",
            AppLanguage.INDONESIAN to "Rumah"
        ),
        "filter_gym" to mapOf(
            AppLanguage.ARABIC to "صالة (Gym)",
            AppLanguage.ENGLISH to "Gym",
            AppLanguage.INDONESIAN to "Gym"
        ),
        "filter_all" to mapOf(
            AppLanguage.ARABIC to "الكل",
            AppLanguage.ENGLISH to "All",
            AppLanguage.INDONESIAN to "Semua"
        ),
        "start_workout" to mapOf(
            AppLanguage.ARABIC to "ابدأ التمرين الآن",
            AppLanguage.ENGLISH to "Start Workout",
            AppLanguage.INDONESIAN to "Mulai Latihan"
        ),
        "points_reward" to mapOf(
            AppLanguage.ARABIC to "نقاط الصعوبة",
            AppLanguage.ENGLISH to "Difficulty Points",
            AppLanguage.INDONESIAN to "Poin Kesulitan"
        ),
        "sets_and_reps" to mapOf(
            AppLanguage.ARABIC to "المجموعات والتكرارات",
            AppLanguage.ENGLISH to "Sets & Reps",
            AppLanguage.INDONESIAN to "Set & Repetisi"
        ),
        "rest_duration" to mapOf(
            AppLanguage.ARABIC to "فترة الراحة",
            AppLanguage.ENGLISH to "Rest Time",
            AppLanguage.INDONESIAN to "Waktu Istirahat"
        ),

        // Weekly Trends Dashboard
        "weekly_dashboard_title" to mapOf(
            AppLanguage.ARABIC to "لوحة اتجاهات وتطور الأسبوع",
            AppLanguage.ENGLISH to "Weekly Trends & Progress Dashboard",
            AppLanguage.INDONESIAN to "Dasbor Tren & Progres Mingguan"
        ),
        "weekly_trends_subtitle" to mapOf(
            AppLanguage.ARABIC to "تحليل بياني شامل لتغير الوزن، استهلاك الماء، وحرق السعرات",
            AppLanguage.ENGLISH to "Comprehensive charts for weight change, water intake, and calories burned",
            AppLanguage.INDONESIAN to "Grafik komprehensif untuk perubahan berat, asupan air, dan kalori terbakar"
        ),
        "tab_all_trends" to mapOf(
            AppLanguage.ARABIC to "📊 نظرة شاملة",
            AppLanguage.ENGLISH to "📊 Overview",
            AppLanguage.INDONESIAN to "📊 Ringkasan"
        ),
        "tab_weight_trend" to mapOf(
            AppLanguage.ARABIC to "⚖️ اتجاه الوزن",
            AppLanguage.ENGLISH to "⚖️ Weight Trend",
            AppLanguage.INDONESIAN to "⚖️ Tren Berat"
        ),
        "tab_water_trend" to mapOf(
            AppLanguage.ARABIC to "💧 شرب الماء",
            AppLanguage.ENGLISH to "💧 Hydration",
            AppLanguage.INDONESIAN to "💧 Hidrasi"
        ),
        "tab_calories_trend" to mapOf(
            AppLanguage.ARABIC to "🔥 حرق السعرات",
            AppLanguage.ENGLISH to "🔥 Calories Burned",
            AppLanguage.INDONESIAN to "🔥 Kalori Terbakar"
        ),
        "weekly_avg_weight" to mapOf(
            AppLanguage.ARABIC to "متوسط وزن الأسبوع",
            AppLanguage.ENGLISH to "Weekly Avg Weight",
            AppLanguage.INDONESIAN to "Rata-rata Berat Mingguan"
        ),
        "weekly_weight_delta" to mapOf(
            AppLanguage.ARABIC to "التغير الصافي في الوزن",
            AppLanguage.ENGLISH to "Net Weight Delta",
            AppLanguage.INDONESIAN to "Perubahan Bersih Berat"
        ),
        "weekly_avg_water" to mapOf(
            AppLanguage.ARABIC to "معدل شرب الماء اليومي",
            AppLanguage.ENGLISH to "Daily Water Average",
            AppLanguage.INDONESIAN to "Rata-rata Air Harian"
        ),
        "weekly_water_goal_met" to mapOf(
            AppLanguage.ARABIC to "أيام تحقيق الهدف",
            AppLanguage.ENGLISH to "Goal Met Days",
            AppLanguage.INDONESIAN to "Hari Target Tercapai"
        ),
        "weekly_total_calories" to mapOf(
            AppLanguage.ARABIC to "إجمالي حرق الأسبوع",
            AppLanguage.ENGLISH to "Total Weekly Burn",
            AppLanguage.INDONESIAN to "Total Pembakaran Mingguan"
        ),
        "weekly_avg_calories" to mapOf(
            AppLanguage.ARABIC to "متوسط الحرق اليومي",
            AppLanguage.ENGLISH to "Daily Average Burn",
            AppLanguage.INDONESIAN to "Rata-rata Bakar Harian"
        ),
        "weekly_peak_day" to mapOf(
            AppLanguage.ARABIC to "اليوم الأعلى حرقاً",
            AppLanguage.ENGLISH to "Peak Burn Day",
            AppLanguage.INDONESIAN to "Hari Puncak Bakar"
        ),
        "weekly_consistency" to mapOf(
            AppLanguage.ARABIC to "مؤشر الالتزام الرياضي",
            AppLanguage.ENGLISH to "Weekly Consistency Index",
            AppLanguage.INDONESIAN to "Indeks Konsistensi Mingguan"
        ),
        "weekly_insights_title" to mapOf(
            AppLanguage.ARABIC to "تحليلات ونصائح التطور الذكي",
            AppLanguage.ENGLISH to "Smart Progress Insights",
            AppLanguage.INDONESIAN to "Wawasan Progres Cerdas"
        ),
        "tap_day_for_details" to mapOf(
            AppLanguage.ARABIC to "اضغط على أي يوم لعرض تفاصيله الدقيقة",
            AppLanguage.ENGLISH to "Tap on any day to inspect detailed metrics",
            AppLanguage.INDONESIAN to "Ketuk hari apa saja untuk melihat metrik detail"
        ),
        "day_breakdown_title" to mapOf(
            AppLanguage.ARABIC to "سجل الأيام التفصيلي (7 أيام)",
            AppLanguage.ENGLISH to "7-Day Daily Breakdown",
            AppLanguage.INDONESIAN to "Rincian Harian 7 Hari"
        ),
        "view_weekly_trends" to mapOf(
            AppLanguage.ARABIC to "لوحة الرسوم البيانية الأسبوعية",
            AppLanguage.ENGLISH to "Weekly Trends Dashboard",
            AppLanguage.INDONESIAN to "Dasbor Tren Mingguan"
        ),

        // Language Selector
        "select_language" to mapOf(
            AppLanguage.ARABIC to "لغة التطبيق (Language)",
            AppLanguage.ENGLISH to "App Language",
            AppLanguage.INDONESIAN to "Bahasa Aplikasi"
        ),
        "language_changed_msg" to mapOf(
            AppLanguage.ARABIC to "تم تغيير لغة التطبيق إلى العربية",
            AppLanguage.ENGLISH to "App language switched to English",
            AppLanguage.INDONESIAN to "Bahasa aplikasi diubah ke Bahasa Indonesia"
        )
    )

    fun get(key: String, language: AppLanguage): String {
        return translations[key]?.get(language) ?: translations[key]?.get(AppLanguage.ARABIC) ?: key
    }
}
