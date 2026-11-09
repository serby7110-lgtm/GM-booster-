package com.example.data

enum class MealCategory(val arabicTitle: String, val englishTitle: String, val iconEmoji: String) {
    BREAKFAST("فطور الطاقة", "Energy Breakfast", "🍳"),
    LUNCH("غداء بناء العضلات", "Muscle Building Lunch", "🍗"),
    DINNER("عشاء خفيف وصحي", "Light & Healthy Dinner", "🥗"),
    SNACK("سناك بروتين وطاقة", "Protein Snack", "🥜"),
    SMOOTHIE("سموذي ومشروبات بروتين", "Protein Smoothies", "🥤"),
    BUDGET("وجبات اقتصادية منزلية", "Budget Home Meals", "🪙")
}

data class Recipe(
    val id: String,
    val titleArabic: String,
    val titleEnglish: String = "",
    val descriptionArabic: String,
    val category: MealCategory,
    val targetBodyType: BodyType,
    val calories: Int,
    val proteinGrams: Int,
    val carbsGrams: Int,
    val fatGrams: Int,
    val prepTimeMinutes: Int,
    val difficultyArabic: String = "سهل وسريع",
    val ingredients: List<String>,
    val stepsArabic: List<String>,
    val benefitsArabic: String,
    val isBudgetFriendly: Boolean = true
)

object RecipeDatabase {
    val allRecipes = listOf(
        // ==========================================
        // ECTOMORPH RECIPES (Clean Mass & Calorie Surplus)
        // ==========================================
        Recipe(
            id = "rec_ecto_smoothie_mass",
            titleArabic = "سموذي التضخيم الخارق بالشوفان وزبدة الفول والموز",
            titleEnglish = "Super Mass Gainer Oatmeal & PB Smoothie",
            descriptionArabic = "مشروب عالي السعرات النظيفة وسهل الهضم لزيادة الوزن العضلي دون دهون ضارة.",
            category = MealCategory.SMOOTHIE,
            targetBodyType = BodyType.ECTOMORPH,
            calories = 820,
            proteinGrams = 44,
            carbsGrams = 98,
            fatGrams = 24,
            prepTimeMinutes = 5,
            difficultyArabic = "فائق السهولة (5 دقائق)",
            ingredients = listOf(
                "كوب ونصف حليب كامل الدسم أو حليب الشوفان (350 مل)",
                "كوب كامل شوفان مطحون ناعم (90 جم)",
                "ملعقتان كبيرتان زبدة فول سوداني طبيعية (45 جم)",
                "موزتان ناضجتان",
                "3 حبات تمر مجدول منزوع النوى",
                "ملعقة كبيرة عسل سدر طبيعي",
                "ملعقة صغيرة بذور الشيا والقرفة"
            ),
            stepsArabic = listOf(
                "اطحن الشوفان أولاً في الخلاط الكهربائي حتى يصبح دقيقاً ناعماً.",
                "أضف الحليب والموز والتمر وزبدة الفول السوداني والعسل والقرفة.",
                "اخلط على سرعة عالية لمدة 60 ثانية حتى يتكون قوام كريمي كثيف وشهي.",
                "اشربه صباحاً كفطور أو مباشرة بعد التمرين لضخ السعرات والبروتين."
            ),
            benefitsArabic = "فائض طاقة مركز (820 سعرة)، يعزز تجديد مخازن الجليكوجين ويسهل استهلاك سعرات مرتفعة للنحفاء."
        ),
        Recipe(
            id = "rec_ecto_salmon_rice",
            titleArabic = "طاجن السلمون والأرز البسمتي مع الأفوكادو وزيت الزيتون",
            titleEnglish = "Salmon & Basmati Bowl with Avocado",
            descriptionArabic = "وجبة غداء متكاملة بالأحماض الدهنية الأساسية أوميغا 3 والبروتين الكامل للبناء العضلي.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.ECTOMORPH,
            calories = 860,
            proteinGrams = 50,
            carbsGrams = 90,
            fatGrams = 28,
            prepTimeMinutes = 20,
            difficultyArabic = "متوسط",
            ingredients = listOf(
                "شريحة سلمون طازج أو سمك فيليه (200 جم)",
                "كوب ونصف أرز بسمتي مطبوخ بزيت الزيتون (250 جم)",
                "نصف حبة أفوكادو مقطعة شرائح",
                "ملعقة كبيرة زيت زيتون بكر ممتاز",
                "عصير ليمونة، رشة بابريكا مدخنة، ملح الهملايا وفلفل أسود"
            ),
            stepsArabic = listOf(
                "تبّل شريحة السلمون بالليمون والملح والفلفل والبابريكا وزيت الزيتون.",
                "اطبخ السلمون على مقلاة غير لاصقة أو في القلاية الهوائية لمدة 10-12 دقيقة.",
                "قدّم السلمون فوق الأرز البسمتي الساخن مع شرائح الأفوكادو الطازجة."
            ),
            benefitsArabic = "أوميغا 3 لتقليل الالتهابات العضلية وسعرات صحية تدعم تضخيم النحفاء."
        ),
        Recipe(
            id = "rec_ecto_potato_beef_hash",
            titleArabic = "هاش البطاطا المشوية باللحم المفروم والبيض",
            titleEnglish = "Sweet Potato & Lean Beef Recovery Hash",
            descriptionArabic = "وجبة طاقة دسمة وصحية غنية بالزنك والحديد والكربوهيدرات المعقدة لدعم النمو العضلي السريع.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.ECTOMORPH,
            calories = 780,
            proteinGrams = 52,
            carbsGrams = 75,
            fatGrams = 22,
            prepTimeMinutes = 25,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "لحم عجل مفروم قليل الدهن (180 جم)",
                "حبتان بطاطا حلوة أو بطاطس مسلوقة ومقطعة مكعبات (250 جم)",
                "بيضتان بلدي مقليتان بقليل من زيت الزيتون",
                "بصلة مفرومة وفلفل رومي ملون",
                "ملعقة صغيرة كمون، كزبرة، بابريكا مدخنة وملح بحري"
            ),
            stepsArabic = listOf(
                "شوح البصل والفلفل في مقلاة مع مسحة زيت زيتون، ثم أضف اللحم المفروم والبهارات حتى ينضج.",
                "أضف مكعبات البطاطا المسلوقة وحمصها مع اللحم 5 دقائق حتى تكتسب لوناً ذهبياً مقرمشاً.",
                "ضع البيضتين على الوجه وقدم الوجبة ساخنة مع شريحة أفوكادو."
            ),
            benefitsArabic = "حديد وزنك لرفع هرمون التستوستيرون الطبيعي وطاقة بطيئة الامتصاص لمنع الهدم العضلي."
        ),
        Recipe(
            id = "rec_ecto_pb_oat_bars",
            titleArabic = "ألواح طاقة الشوفان وزبدة الفول السوداني المنزلية",
            titleEnglish = "Homemade High-Calorie PB Oat Energy Bars",
            descriptionArabic = "سناك سريع التحضير ومثالي لحمله في العمل أو الجامعة للحفاظ على فائض السعرات اليومي.",
            category = MealCategory.SNACK,
            targetBodyType = BodyType.ECTOMORPH,
            calories = 490,
            proteinGrams = 22,
            carbsGrams = 56,
            fatGrams = 18,
            prepTimeMinutes = 10,
            difficultyArabic = "بدون فرن (10 دقائق)",
            ingredients = listOf(
                "كوب ونصف شوفان حبة كاملة (150 جم)",
                "نصف كوب زبدة فول سوداني نقية (100 جم)",
                "ثلث كوب عسل نحل طبيعي (80 جم)",
                "ملعقتان كبيرتان بذور الكتان أو الشيا",
                "حفنة مكسرات لوز أو جوز مجروش"
            ),
            stepsArabic = listOf(
                "في وعاء، سخن زبدة الفول السوداني مع العسل لمدة 30 ثانية حتى تسيل.",
                "أضف الشوفان وبذور الكتان والمكسرات واخلط جيداً حتى تتكون عجينة متماسكة.",
                "افرد الخليط في قالب مبطن بورق زبدة واضغط عليه جيداً.",
                "ضعه في الثلاجة لمدة ساعة ثم قطعه إلى 6 ألواح متساوية."
            ),
            benefitsArabic = "وجبة خفيفة عالية الكثافة الغذائية تمنع فترات الجوع الطويلة وتدعم التضخيم المستمر."
        ),
        Recipe(
            id = "rec_ecto_chicken_pasta",
            titleArabic = "مكرونة الدجاج بصلصة الكريمة الصحية والجبن",
            titleEnglish = "Creamy High-Protein Chicken Pasta",
            descriptionArabic = "وجبة غداء أو عشاء إيطالية صحية غنية بالبروتين والكارب سهلة الاستهلاك والبلع.",
            category = MealCategory.DINNER,
            targetBodyType = BodyType.ECTOMORPH,
            calories = 750,
            proteinGrams = 48,
            carbsGrams = 85,
            fatGrams = 16,
            prepTimeMinutes = 20,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "صدور دجاج مقطعة مكعبات (180 جم)",
                "مكرونة حبة كاملة أو مكرونة عادية مطبوخة (200 جم)",
                "نصف كوب حليب قليل الدسم ممزوج بملعقة جبن كريمي لايت وثوم مهروس",
                "فطر مشروم طازج مقطع",
                "ملعقة صغيرة أوريغانو، زعتر بري وفلفل أسود"
            ),
            stepsArabic = listOf(
                "شوح صدور الدجاج والمشروم في مقلاة مع رشة ملح وفلفل وأوريغانو حتى النضج.",
                "أضف مزيج الحليب والجبن الخفيف واتركه يغلي دقيقتين حتى يسمك القوام.",
                "أضف المكرونة المسلوقة وقلبها مع الصلصة والدجاج لمدة دقيقة وقدمها ساخنة."
            ),
            benefitsArabic = "مذاق رائع يفتح الشهية للنحفاء ويوفر جرعة سريعة من البروتين والكربوهيدرات لإعادة البناء."
        ),

        // ==========================================
        // MESOMORPH RECIPES (Balanced Athletic Performance)
        // ==========================================
        Recipe(
            id = "rec_meso_chicken_quinoa",
            titleArabic = "صدر دجاج متبل مع كينوا وخضار مشوية بالأعشاب",
            titleEnglish = "Grilled Herb Chicken with Quinoa & Veggies",
            descriptionArabic = "توازن مثالي بنسبة 40% كربوهيدرات و35% بروتين لدعم الألياف العضلية الصافية.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.MESOMORPH,
            calories = 540,
            proteinGrams = 52,
            carbsGrams = 48,
            fatGrams = 12,
            prepTimeMinutes = 20,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "صدر دجاج خالي من الجلد والعظم (200 جم)",
                "كوب كينوا مطبوخة (150 جم)",
                "كوب بروكلي وفلفل ملون وكوسة مقطعة",
                "ملعقة صغيرة زيت زيتون",
                "بهارات إيطالية، بودرة ثوم، رشة خردل وعصير ليمون"
            ),
            stepsArabic = listOf(
                "تبّل الدجاج بالليمون والثوم والخردل والأعشاب الإيطالية واشوِه على الجريل 6-7 دقائق لكل جانب.",
                "سوتيه الخضار (البروكلي والكوسة والفلفل) مع قطرات زيت الزيتون حتى تنضج وتبقى مقرمشة.",
                "ضع الكينوا في الطبق ورتب الدجاج المشوي والخضار واستمتع بوجبة أبطال متكاملة."
            ),
            benefitsArabic = "أعلى نسبة امتصاص بروتيني مع كينوا غنية بالأحماض الأمينية التسعة الأساسية."
        ),
        Recipe(
            id = "rec_meso_egg_toast",
            titleArabic = "توست الحبوب الكاملة بالبيض والسبانخ وجبن الحلوم المشوي",
            titleEnglish = "Whole Grain Toast with Eggs & Grilled Halloumi",
            descriptionArabic = "فطور رياضي مفعم بالطاقة ومضادات الأكسدة لتعزيز القوة والتركيز اليومي.",
            category = MealCategory.BREAKFAST,
            targetBodyType = BodyType.MESOMORPH,
            calories = 460,
            proteinGrams = 32,
            carbsGrams = 38,
            fatGrams = 16,
            prepTimeMinutes = 10,
            difficultyArabic = "سريع جداً (10 دقائق)",
            ingredients = listOf(
                "شريحتان خبز حبوب كاملة 100%",
                "بيضتان بلدي مسلوقتان أو عيون",
                "شريحتان جبن حلوم لايت قليل الدسم (40 جم)",
                "حفنة سبانخ طازجة",
                "طماطم كرزية ورشة بذور السمسم وبابريكا"
            ),
            stepsArabic = listOf(
                "حمص شرائح التوست في المحمصة حتى تكتسب لوناً ذهبياً.",
                "اشوِ جبن الحلوم في مقلاة ساخنة دقيقة واحدة لكل جانب.",
                "رتب السبانخ على التوست وضع البيض والحلوم والطماطم الكرزية ورش البابريكا."
            ),
            benefitsArabic = "مزيج غني بالكالسيوم، الحديد، والبروتين لبدء اليوم بنشاط حيوي متقد."
        ),
        Recipe(
            id = "rec_meso_tuna_baked_potato",
            titleArabic = "بطاطا مشوية محشوة بالتونة والذرة والزبادي",
            titleEnglish = "Stuffed Baked Potato with Tuna & Greek Yogurt",
            descriptionArabic = "وجبة استشفاء بعد التمرين غنية بالبوتاسيوم لمنع التقلصات والبروتين لإصلاح الألياف.",
            category = MealCategory.DINNER,
            targetBodyType = BodyType.MESOMORPH,
            calories = 480,
            proteinGrams = 42,
            carbsGrams = 52,
            fatGrams = 8,
            prepTimeMinutes = 15,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "حبة بطاطس متوسطة مشوية في القلاية الهوائية أو الفرن (200 جم)",
                "علبة تونة مصفاة بالماء (160 جم)",
                "ملعقتان كبيرتان زبادي يوناني خالي الدسم كبديل صحي للمايونيز",
                "ملعقتان كبيرتان ذرة حلوة وبقدونس مفروم",
                "عصير ليمون، رشة كمون وفلفل أسود"
            ),
            stepsArabic = listOf(
                "اشق البطاطا المشوية من المنتصف واهرس اللب الداخلي بالشوكة.",
                "في وعاء، اخلط التونة مع الزبادي اليوناني والليمون والذرة والبهارات والبقدونس.",
                "احشُ البطاطا بمزيج التونة وزين برشة بابريكا وتناولها كوجبة تعافي مثالية."
            ),
            benefitsArabic = "بوتاسيوم عالي لتعويض الأملاح المفقودة في العرق وبروتين صافي بدون دهون مشبعة."
        ),
        Recipe(
            id = "rec_meso_beef_burrito_bowl",
            titleArabic = "بوريتو بول اللحم المفروم والأرز البني والفاصوليا الحمراء",
            titleEnglish = "Mexican Beef, Brown Rice & Black Bean Bowl",
            descriptionArabic = "طبق مكسيكي صحي متوازن وغني بالألياف النباتية والبروتين الحيواني عالي القيمة.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.MESOMORPH,
            calories = 580,
            proteinGrams = 46,
            carbsGrams = 58,
            fatGrams = 14,
            prepTimeMinutes = 20,
            difficultyArabic = "متوسط",
            ingredients = listOf(
                "لحم بقري مفروم خالي من الشحم (160 جم)",
                "كوب أرز بني أو برغل مطبوخ (150 جم)",
                "نصف كوب فاصوليا حمراء مسلوقة",
                "سلطة بيكو دي جالو (طماطم، بصل، كزبرة، ليمون)",
                "ملعقة صغيرة زيت زيتون وبهارات تاكو (كمون، بابريكا، ثوم)"
            ),
            stepsArabic = listOf(
                "شوح اللحم مع بهارات التاكو في المقلاة حتى ينضج تماماً.",
                "رتب الأرز البني في وعاء التقديم، ثم أضف اللحم والفاصوليا الحمراء وسلطة الطماطم.",
                "اعصر نصف ليمونة خضراء على الوجه وتمتع بوجبة رياضية مشبعة وقوية."
            ),
            benefitsArabic = "ألياف عالية تحافظ على مستويات طاقة مستقرة وتعزز عملية التمثيل الغذائي الرياضي."
        ),
        Recipe(
            id = "rec_meso_berry_protein_shake",
            titleArabic = "سموذي التوت المشكل والزبادي اليوناني للتعافي السريع",
            titleEnglish = "Wild Berry & Greek Yogurt Recovery Shake",
            descriptionArabic = "مشروب مضادات أكسدة جبار يقلل من آلام العضلات المتأخرة (DOMS) بعد التمارين الشاقة.",
            category = MealCategory.SMOOTHIE,
            targetBodyType = BodyType.MESOMORPH,
            calories = 340,
            proteinGrams = 32,
            carbsGrams = 38,
            fatGrams = 5,
            prepTimeMinutes = 4,
            difficultyArabic = "فائق السرعة (4 دقائق)",
            ingredients = listOf(
                "كوب توت مشكل مثلج (فراولة، توت أزرق، توت أسود)",
                "نصف كوب زبادي يوناني (120 جم)",
                "كوب حليب لوز أو حليب خالي الدسم",
                "ملعقة عسل نحل طبيعي",
                "رشة بذور الشيا"
            ),
            stepsArabic = listOf(
                "ضع جميع المكونات في الخلاط الكهربائي.",
                "اخلط لمدة 45 ثانية حتى يصبح القوام ناعماً ومثلجاً.",
                "اشربه في غضون 30 دقيقة بعد الانتهاء من التمارين لتعزيز تخليق البروتين العضلي."
            ),
            benefitsArabic = "مضادات أكسدة بوليفينولية تسرع ترميم الألياف العضلية وتخفف الإجهاد التأكسدي."
        ),

        // ==========================================
        // ENDOMORPH RECIPES (Low-Carb, High-Satiety, Fat Burn)
        // ==========================================
        Recipe(
            id = "rec_endo_tuna_avocado",
            titleArabic = "سلطة التونة بالبيض المسلوق والأفوكادو وزيت الزيتون",
            titleEnglish = "Keto Tuna & Boiled Egg Avocado Salad",
            descriptionArabic = "وجبة مشبعة جداً منخفضة الكارب وغنية بالألياف والبروتين لحرق الدهون السريع.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 390,
            proteinGrams = 46,
            carbsGrams = 7,
            fatGrams = 18,
            prepTimeMinutes = 8,
            difficultyArabic = "سريع جداً (8 دقائق)",
            ingredients = listOf(
                "علبة تونة مصفاة بالماء (160 جم)",
                "بيضتان مسلوقتان مقطعتان",
                "نصف حبة أفوكادو مهروسة مع رشة ليمون",
                "خس روماني مقطع، خيار، وجرجير طازج",
                "ملعقة صغيرة زيت زيتون بكر وخل التفاح العضوي"
            ),
            stepsArabic = listOf(
                "في وعاء، اخلط التونة المصفاة مع الأفوكادو المهروس والليمون وخل التفاح كبديل صحي للمايونيز.",
                "أضف الخس والجرجير والخيار المقطع.",
                "زين بالبيض المسلوق ورشة من الفلفل الأسود وبذور الكتان."
            ),
            benefitsArabic = "شبع يدوم لساعات طويلة مع استقرار مستويات الإنسولين في الدم مما يحفز حرق الدهون."
        ),
        Recipe(
            id = "rec_endo_greek_yogurt_chia",
            titleArabic = "زبادي يوناني عالي البروتين مع التوت وبذور الشيا والقرفة",
            titleEnglish = "High-Protein Greek Yogurt with Chia & Berries",
            descriptionArabic = "عشاء خفيف أو سناك يغذي العضلات أثناء النوم بدون سكريات ترفع الإنسولين.",
            category = MealCategory.DINNER,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 240,
            proteinGrams = 28,
            carbsGrams = 11,
            fatGrams = 6,
            prepTimeMinutes = 3,
            difficultyArabic = "فائق السرعة (3 دقائق)",
            ingredients = listOf(
                "كوب زبادي يوناني سادة خالي من السكر (200 جم)",
                "حفنة توت بري أزرق أو فراولة مقطعة (40 جم)",
                "ملعقة كبيرة بذور الشيا (12 جم)",
                "رشة قرفة سيلانية وقطرات ستيفيا"
            ),
            stepsArabic = listOf(
                "ضع الزبادي اليوناني في وعاء وأضف قطرات الستيفيا والقرفة.",
                "انثر حبات التوت وبذور الشيا الغنية بالألياف على الوجه.",
                "تناوله مساءً لتثبيت سكر الدم وبناء العضلات أثناء النوم."
            ),
            benefitsArabic = "بروتين الكازين بطيء الامتصاص يدعم تعافي الأنسجة طوال الليل دون تخزين دهون."
        ),
        Recipe(
            id = "rec_endo_spinach_omelet",
            titleArabic = "أومليت البيض بالسبانخ والمشروم وجبن القريش",
            titleEnglish = "Fluffy Spinach & Cottage Cheese Omelet",
            descriptionArabic = "فطور كيتوني مشبع خالي من الكربوهيدرات البسيطة يدعم حرق الدهون طوال الصباح.",
            category = MealCategory.BREAKFAST,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 310,
            proteinGrams = 34,
            carbsGrams = 5,
            fatGrams = 15,
            prepTimeMinutes = 10,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "3 بيضات كاملة",
                "نصف كوب جبن قريش طبيعي قليل الملح (80 جم)",
                "كوب سبانخ طازجة ومشروم مقطع",
                "نصف ملعقة صغيرة زيت زيتون أو زيت جوز هند",
                "رشة ملح بحري، فلفل أسود، وبودرة بصل"
            ),
            stepsArabic = listOf(
                "اخفق البيض مع جبن القريش والبهارات حتى يصبح الخليط هشاً ومتجانساً.",
                "شوح السبانخ والمشروم في مقلاة مدهونة بمسحة زيت لمدة دقيقة حتى تذبل.",
                "اسكب خليط البيض فوق الخضار واطبخه على نار هادئة حتى ينضج ثم اقلبه برفق."
            ),
            benefitsArabic = "مؤشر جلايسيمي منخفض جداً يحافظ على إفراز هرمون حرق الدهون (الجلوكاجون)."
        ),
        Recipe(
            id = "rec_endo_grilled_chicken_zucchini",
            titleArabic = "شاورما الدجاج المنزلية مع نودلز الكوسة وصلصة الطحينة",
            titleEnglish = "Homemade Chicken Shawarma & Zucchini Noodles",
            descriptionArabic = "بديل صحي منخفض السعرات للشاورما التقليدية بدون خبز مع طعم شهي لا يقاوم.",
            category = MealCategory.LUNCH,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 380,
            proteinGrams = 48,
            carbsGrams = 9,
            fatGrams = 14,
            prepTimeMinutes = 15,
            difficultyArabic = "سهل",
            ingredients = listOf(
                "صدور دجاج مقطعة شرائح رفيعة (200 جم)",
                "حبتان كوسة مبشورة بشكل شرائط نودلز (Zoodles)",
                "ملعقة كبيرة طحينة سمسم نقي مخلوطة بليمون وثوم وقليل من الماء",
                "بهارات شاورما (حبهان، قرفة، ثوم، كمون، بابريكا)",
                "مخلل خيار منزلي وشرائح طماطم"
            ),
            stepsArabic = listOf(
                "تبل الدجاج ببهارات الشاورما والليمون واشوِه في مقلاة حامية حتى يتحمر.",
                "شوح شرائط الكوسة في مقلاة دقيقتين فقط حتى تصبح طرية دون أن تفقد قرمشتها.",
                "ضع نودلز الكوسة في الطبق وضع فوقها الدجاج واسكب صلصة الطحينة والليمون."
            ),
            benefitsArabic = "بروتين مرتفع مع ألياف مائية تملأ المعدة وتعزز الشبع بدون أي سكريات."
        ),
        Recipe(
            id = "rec_endo_green_detox_smoothie",
            titleArabic = "سموذي الديتوكس الأخضر بالسبانخ والزنجبيل والليمون",
            titleEnglish = "Fat-Burning Green Ginger Detox Smoothie",
            descriptionArabic = "مشروب حارق للدهون ومدر للمياه الزائدة ينشط الهضم وينعش الجسم صباحاً.",
            category = MealCategory.SMOOTHIE,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 120,
            proteinGrams = 8,
            carbsGrams = 14,
            fatGrams = 2,
            prepTimeMinutes = 5,
            difficultyArabic = "سريع (5 دقائق)",
            ingredients = listOf(
                "كوبان أوراق سبانخ وجرجير طازجة",
                "نصف خيارة مقطعة",
                "قطعة صغيرة زنجبيل طازج مبشور",
                "عصير نصف ليمونة مع رشة نعناع طازج",
                "كوب ماء بارد ومكعبات ثلج",
                "ملعقة صغيرة بذور الكتان المطحونة"
            ),
            stepsArabic = listOf(
                "ضع السبانخ، الخيار، الزنجبيل، النعناع، وعصير الليمون في الخلاط.",
                "أضف الماء والثلج وبذور الكتان واخلط جيداً حتى ينعم تماماً.",
                "اشربه على الريق لتنشيط الكبد وحرق الدهون العنيدة."
            ),
            benefitsArabic = "غني بمضادات الأكسدة والكلوروفيل ويساعد في طرد السوائل المحتبسة تحت الجلد."
        ),

        // ==========================================
        // BUDGET-FRIENDLY & HOMEMADE PROTEIN SNACKS
        // ==========================================
        Recipe(
            id = "rec_budget_lentil_soup",
            titleArabic = "شوربة العدس الأصفر التركية بالليمون وزيت الزيتون",
            titleEnglish = "High-Protein Turkish Red Lentil Soup",
            descriptionArabic = "أفضل وجبة اقتصادية غنية بالبروتين النباتي والحديد تدفئ الجسم وتدعم المناعة.",
            category = MealCategory.BUDGET,
            targetBodyType = BodyType.MESOMORPH,
            calories = 360,
            proteinGrams = 24,
            carbsGrams = 52,
            fatGrams = 6,
            prepTimeMinutes = 20,
            difficultyArabic = "اقتصادي وسهل",
            ingredients = listOf(
                "كوب عدس أصفر مغسول (150 جم)",
                "بصلة، جزرة، وحبة طماطم مقطعة",
                "3 فصوص ثوم مهروسة",
                "ملعقة صغيرة كمون، كركم، وملح بحري",
                "ملعقة صغيرة زيت زيتون وعصير ليمون طازج"
            ),
            stepsArabic = listOf(
                "في قدر، ضع العدس مع الخضار والماء واتركه يغلي حتى تنضج المكونات (15 دقيقة).",
                "اطحن المزيج بالخلاط اليدوي حتى يصبح ناعماً وكريمياً.",
                "أضف الكمون والكركم والملح وزيت الزيتون وقدمه مع عصير الليمون."
            ),
            benefitsArabic = "بروتين نباتي عالي الكفاءة واقتصادي جداً متوفر في كل منزل مع كركم مضاد للالتهاب."
        ),
        Recipe(
            id = "rec_budget_egg_shakshouka",
            titleArabic = "شكشوكة البيض البلدية بالطماطم والفلفل والكمون",
            titleEnglish = "Traditional High-Protein Shakshouka",
            descriptionArabic = "فطور شعبي غني بالبروتين الكامل وفيتامين B12 بمكونات متوفرة في كل بيت.",
            category = MealCategory.BREAKFAST,
            targetBodyType = BodyType.MESOMORPH,
            calories = 380,
            proteinGrams = 28,
            carbsGrams = 22,
            fatGrams = 18,
            prepTimeMinutes = 12,
            difficultyArabic = "سهل جداً",
            ingredients = listOf(
                "3 أو 4 بيضات طازجة",
                "حبتان طماطم مقطعة مكعبات",
                "بصلة صغيرة وفلفل أخضر مقطع",
                "ملعقة صغيرة زيت زيتون",
                "رشة كمون، فلفل أسود، بابريكا وملح"
            ),
            stepsArabic = listOf(
                "شوح البصل والفلفل في المقلاة حتى يذبلا، ثم أضف الطماطم والبهارات واتركها تتسبك 5 دقائق.",
                "اصنع فجوات في الصلصة واكسر حبات البيض فيها.",
                "غطِ المقلاة واترك البيض ينضج حسب الرغبة وتناولها ساخنة مع رغيف خبز بلدي أسمر."
            ),
            benefitsArabic = "كولين لصحة الدماغ والجهاز العصبي، بروتين كامل سهل الامتصاص وغير مكلف."
        ),
        Recipe(
            id = "rec_budget_chickpea_salad",
            titleArabic = "سلطة الحمص المسلوق بالخضار وزيت الزيتون والليمون",
            titleEnglish = "Mediterranean Chickpea Protein Salad",
            descriptionArabic = "وجبة سريعة ومغذية لا تحتاج طبخ، غنية بالألياف والمعادن ومناسبة لأيام العمل.",
            category = MealCategory.SNACK,
            targetBodyType = BodyType.ENDOMORPH,
            calories = 320,
            proteinGrams = 18,
            carbsGrams = 42,
            fatGrams = 8,
            prepTimeMinutes = 5,
            difficultyArabic = "جاهز في 5 دقائق",
            ingredients = listOf(
                "كوب حمص مسلوق جاهز (180 جم)",
                "خيار، طماطم، بصل أحمر، وبقدونس مفروم",
                "ملعقة صغيرة زيت زيتون بكر ممتاز",
                "عصير ليمونة كاملة ورشة كمون وملح"
            ),
            stepsArabic = listOf(
                "اشطف الحمص المسلوق وصفه جيداً من الماء.",
                "اخلط الحمص مع الخضار المقطعة في وعاء كبير.",
                "تبّل بالليمون وزيت الزيتون والكمون والملح وقدمه مباشرة."
            ),
            benefitsArabic = "ألياف ذائبة تخفض الكوليسترول الضار وتمنح طاقة نظيفة بدون كسل أو هبوط في السكر."
        ),
        Recipe(
            id = "rec_snack_energy_date_balls",
            titleArabic = "كرات طاقة التمر والمكسرات والكاكاو الخام (Pre-Workout)",
            titleEnglish = "Raw Cacao & Date Pre-Workout Energy Bites",
            descriptionArabic = "أفضل سناك طبيعي قبل التمرين بـ 30 دقيقة يمدك بطاقة متفجرة وتركيز ذهني عالي.",
            category = MealCategory.SNACK,
            targetBodyType = BodyType.MESOMORPH,
            calories = 280,
            proteinGrams = 10,
            carbsGrams = 45,
            fatGrams = 9,
            prepTimeMinutes = 8,
            difficultyArabic = "بدون طبخ (8 دقائق)",
            ingredients = listOf(
                "كوب عجينة تمر طري أو تمر منزوع النوى (150 جم)",
                "ملعقتان كبيرتان كاكاو خام طبيعي خالي من السكر",
                "ملعقة كبيرة زبدة فول سوداني أو لوز",
                "ملعقتان كبيرتان لوز أو شوفان مطحون",
                "رشة قرفة سيلانية"
            ),
            stepsArabic = listOf(
                "في محضر الطعام، اخلط التمر مع الكاكاو وزبدة الفول السوداني والشوفان والقرفة.",
                "شكل الخليط إلى كرات صغيرة بحجم حبة الجوز (حوالي 6-8 كرات).",
                "دحرج الكرات في مسحوق الكاكاو أو السمسم واحفظها في علبة بالثلاجة.",
                "تناول كرتين قبل التمرين بـ 30 دقيقة لطاقة فورية."
            ),
            benefitsArabic = "سكريات طبيعية سريعة الامتصاص مع مغنيسيوم طبيعي من الكاكاو لتحسين الانقباض العضلي."
        )
    )
}
