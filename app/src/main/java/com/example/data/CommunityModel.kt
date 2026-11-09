package com.example.data

data class CommunityPost(
    val id: String,
    val authorName: String,
    val authorBodyType: BodyType,
    val authorLevelName: String,
    val contentArabic: String,
    val workoutTagArabic: String?,
    val pointsGained: Float,
    val likesCount: Int,
    val isLikedByMe: Boolean,
    val timeAgoArabic: String,
    val comments: List<CommunityComment> = emptyList()
)

data class CommunityComment(
    val id: String,
    val authorName: String,
    val textArabic: String,
    val timeAgoArabic: String
)

object CommunitySeed {
    val initialPosts = listOf(
        CommunityPost(
            id = "post_1",
            authorName = "كابتن طارق السعيد",
            authorBodyType = BodyType.MESOMORPH,
            authorLevelName = "وحش اللياقة (Titan)",
            contentArabic = "أنهيت اليوم جولة ديدلفت 4 مجموعات وحققت 3.0 نقاط كاملة! استجابة الجسم لنظام الميزومورف مذهلة، لا تستسلموا يا أبطال اليوم هو بداية التغيير 💪🔥",
            workoutTagArabic = "ديدلفت بالبار - 3.0 نقاط",
            pointsGained = 3.0f,
            likesCount = 28,
            isLikedByMe = true,
            timeAgoArabic = "منذ ساعة",
            comments = listOf(
                CommunityComment("c1", "سارة محمد", "كفو يا بطل! أداء ملهم حقاً 👏", "منذ 40 دقيقة"),
                CommunityComment("c2", "فيصل العتيبي", "وحش ما شاء الله، استمر!", "منذ 15 دقيقة")
            )
        ),
        CommunityPost(
            id = "post_2",
            authorName = "عمر الخالدي",
            authorBodyType = BodyType.ECTOMORPH,
            authorLevelName = "رياضي طموح (Challenger)",
            contentArabic = "كنت أعاني دائماً من ثبات وزني، بعد الالتزام بنظام سموذي التضخيم وتمارين الصدر المنزلية زاد وزني 2.5 كجم عضل صافي خلال شهر واحد! النقاط اليوم: 2.1 🎯",
            workoutTagArabic = "ضغط الماس + سموذي التضخيم",
            pointsGained = 2.1f,
            likesCount = 42,
            isLikedByMe = false,
            timeAgoArabic = "منذ 3 ساعات",
            comments = listOf(
                CommunityComment("c3", "يوسف علي", "ممتاز جداً، أنا اكتومورف أيضاً وشجعتني أبدأ اليوم!", "منذ ساعتين")
            )
        ),
        CommunityPost(
            id = "post_3",
            authorName = "نورة القحطاني",
            authorBodyType = BodyType.ENDOMORPH,
            authorLevelName = "بطل الصالة (Warrior)",
            contentArabic = "أكملت اليوم تمرين البيربيز والكارديو المائل وخسرت 450 سعرة حرارية! شعور الخفة والطاقة لا يوصف مع وصفات الإندومورف قليلة الكارب 🥗⚡",
            workoutTagArabic = "بيربيز HIIT + مشي مائل",
            pointsGained = 2.8f,
            likesCount = 35,
            isLikedByMe = true,
            timeAgoArabic = "منذ 5 ساعات",
            comments = listOf(
                CommunityComment("c4", "أميرة فهد", "عاش يا بطلة، استمري بإشعال الحماس!", "منذ 3 ساعات")
            )
        )
    )
}
