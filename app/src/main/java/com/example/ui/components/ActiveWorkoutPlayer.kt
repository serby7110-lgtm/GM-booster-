package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Exercise
import com.example.ui.ActiveWorkoutSession

@Composable
fun ActiveWorkoutPlayerScreen(
    session: ActiveWorkoutSession,
    onPauseResume: () -> Unit,
    onCompleteSet: (weightKg: Float, reps: Int) -> Unit,
    onSkipRest: () -> Unit,
    onAdjustRest: (Int) -> Unit = {},
    onFinishWorkout: () -> Unit,
    onClose: () -> Unit,
    onNextRoutineExercise: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val exercise = session.exercise
    var setWeightKg by remember(session.currentSet) { mutableFloatStateOf(session.currentWeightKg) }
    var setReps by remember(session.currentSet) { mutableIntStateOf(session.currentReps) }
    var showMistakesWarning by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (session.isRunning && !session.isResting) 1.04f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF090D14) // Pure Gym Dark Background
    ) {
        if (session.isCompleted) {
            WorkoutCelebrationCard(
                exercise = exercise,
                durationSeconds = session.elapsedSeconds,
                totalVolumeKg = session.totalVolumeLiftedKg,
                completedSets = session.completedSetsHistory,
                recentPr = session.recentPrResult,
                activeProgramTitle = session.activeProgramTitle,
                activeDayTitle = session.activeDayTitle,
                routineExerciseIndex = session.routineExerciseIndex,
                totalRoutineExercises = session.totalRoutineExercises,
                onNextExercise = onNextRoutineExercise,
                onDone = onClose
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top Header: Program Context & Close Action
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color(0xFF1E293B), CircleShape)
                            .testTag("close_player_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "إغلاق",
                            tint = Color.White
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        if (!session.activeProgramTitle.isNullOrBlank()) {
                            Text(
                                text = "${session.activeProgramTitle} • ${session.activeDayTitle ?: ""}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E5FF)
                            )
                            Text(
                                text = "تمرين ${session.routineExerciseIndex + 1} من ${session.totalRoutineExercises}",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.Gray
                            )
                        } else {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFFF5722).copy(alpha = 0.2f),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF5722).copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "🔥 الوضع الرياضي النشط",
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFF5722)
                                )
                            }
                        }
                    }

                    DifficultyBadge(difficultyRating = exercise.difficultyRating)
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Exercise Title & Target Muscle
                Text(
                    text = exercise.nameArabic,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    color = Color.White
                )

                if (exercise.nameEnglish.isNotBlank()) {
                    Text(
                        text = exercise.nameEnglish,
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Progressive Overload Coach Card
                if (session.overloadAdvice != null) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131E2B)),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF00E5FF).copy(alpha = 0.3f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        Icons.Default.TrendingUp,
                                        contentDescription = null,
                                        tint = Color(0xFF00E5FF),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "مدرب الزيادة التدريجية (Progressive Overload)",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = Color(0xFF00E5FF)
                                    )
                                }

                                if (session.overloadAdvice.isReadyForWeightIncrease) {
                                    Surface(
                                        color = Color(0xFF00E676).copy(alpha = 0.2f),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Text(
                                            "جاهز لرفع الوزن! 🚀",
                                            color = Color(0xFF00E676),
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = session.overloadAdvice.adviceMessageArabic,
                                fontSize = 12.sp,
                                color = Color.White,
                                lineHeight = 16.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // REST TIMER DISPLAY or SET CONTROLLER
                if (session.isResting) {
                    // Resting Screen Mode
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF131D24)),
                        border = androidx.compose.foundation.BorderStroke(2.dp, Color(0xFF00E676))
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "⏱️ فترة الراحة والاستشفاء",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF00E676)
                            )
                            Spacer(modifier = Modifier.height(14.dp))

                            Box(
                                modifier = Modifier
                                    .size(150.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF0B141A)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    progress = {
                                        (session.restSecondsRemaining.toFloat() / exercise.restDurationSeconds.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
                                    },
                                    modifier = Modifier.fillMaxSize(),
                                    color = Color(0xFF00E676),
                                    strokeWidth = 10.dp,
                                    trackColor = Color(0xFF1E293B)
                                )
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "${session.restSecondsRemaining}",
                                        fontSize = 46.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color.White
                                    )
                                    Text("ثانية", fontSize = 13.sp, color = Color.Gray)
                                }
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                FilledTonalButton(
                                    onClick = { onAdjustRest(15) },
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E293B))
                                ) {
                                    Text("+15 ثانية", color = Color.White, fontSize = 12.sp)
                                }

                                FilledTonalButton(
                                    onClick = { onAdjustRest(30) },
                                    colors = ButtonDefaults.filledTonalButtonColors(containerColor = Color(0xFF1E293B))
                                ) {
                                    Text("+30 ثانية", color = Color.White, fontSize = 12.sp)
                                }

                                Button(
                                    onClick = onSkipRest,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                    modifier = Modifier.testTag("skip_rest_button")
                                ) {
                                    Text("تخطي الراحة ⚡", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                } else {
                    // Set Logging HUD Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp)),
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFF141A23))
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // Set indicator pill
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1E293B),
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Text(
                                    text = "المجموعة ${session.currentSet} من إجمالي ${session.totalSets}",
                                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = Color(0xFF00E5FF)
                                )
                            }

                            // Huge Glanceable Inputs: Weight & Reps
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Weight Card
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(end = 6.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0F1722))
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("الوزن (كجم)", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        text = "${setWeightKg}",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFFFF5722)
                                    )
                                    Text("كيلوغرام", fontSize = 11.sp, color = Color.Gray)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { setWeightKg = (setWeightKg - 2.5f).coerceAtLeast(0f) },
                                            modifier = Modifier.size(36.dp).background(Color(0xFF1E293B), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "-2.5kg", tint = Color.White)
                                        }

                                        IconButton(
                                            onClick = { setWeightKg += 2.5f },
                                            modifier = Modifier.size(36.dp).background(Color(0xFF1E293B), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "+2.5kg", tint = Color.White)
                                        }
                                    }
                                }

                                // Reps Card
                                Column(
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 6.dp)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(Color(0xFF0F1722))
                                        .padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text("العدات المنجزة", fontSize = 12.sp, color = Color.Gray)
                                    Text(
                                        text = "$setReps",
                                        fontSize = 36.sp,
                                        fontWeight = FontWeight.Black,
                                        color = Color(0xFF00E5FF)
                                    )
                                    Text("تكرار", fontSize = 11.sp, color = Color.Gray)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        IconButton(
                                            onClick = { setReps = (setReps - 1).coerceAtLeast(1) },
                                            modifier = Modifier.size(36.dp).background(Color(0xFF1E293B), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "-1 rep", tint = Color.White)
                                        }

                                        IconButton(
                                            onClick = { setReps += 1 },
                                            modifier = Modifier.size(36.dp).background(Color(0xFF1E293B), CircleShape)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "+1 rep", tint = Color.White)
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // ONE-TAP LOG SET BUTTON
                    Button(
                        onClick = { onCompleteSet(setWeightKg, setReps) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .testTag("complete_set_button"),
                        shape = RoundedCornerShape(18.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(26.dp))
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (session.currentSet >= session.totalSets) "إنهاء التمرين وتسجيل الإنجاز 🔥" else "تم أداء المجموعة (${session.currentSet}) والبدء بالراحة ⏱️",
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Pause & Timer Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏱️ وقت التمرين: ${formatDuration(session.elapsedSeconds)}",
                        fontSize = 13.sp,
                        color = Color.LightGray
                    )

                    OutlinedButton(
                        onClick = onPauseResume,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                    ) {
                        Icon(
                            imageVector = if (session.isRunning) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (session.isRunning) "إيقاف مؤقت" else "استئناف", fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Mistakes & Safety Guide
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { showMistakesWarning = !showMistakesWarning },
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B).copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("الأخطاء الشائعة وسلامة المفاصل", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color.White)
                            }
                            Text(if (showMistakesWarning) "▲" else "▼", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold)
                        }

                        AnimatedVisibility(visible = showMistakesWarning) {
                            Column(modifier = Modifier.padding(top = 8.dp)) {
                                exercise.getResolvedCommonMistakes().forEach { mistake ->
                                    Text("❌ $mistake", fontSize = 12.sp, color = Color.LightGray, modifier = Modifier.padding(vertical = 2.dp))
                                }
                                if (exercise.tipArabic.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text("💡 ${exercise.tipArabic}", fontSize = 12.sp, color = Color(0xFFFFD600), fontWeight = FontWeight.SemiBold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WorkoutCelebrationCard(
    exercise: Exercise,
    durationSeconds: Int,
    totalVolumeKg: Float,
    completedSets: List<Pair<Float, Int>>,
    recentPr: com.example.data.ExercisePrCheckResult?,
    activeProgramTitle: String?,
    activeDayTitle: String?,
    routineExerciseIndex: Int,
    totalRoutineExercises: Int,
    onNextExercise: (() -> Unit)?,
    onDone: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF131E2A))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🏆🔥💪", fontSize = 38.sp)
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = "كفو يا بطل! أداء تدريبي استثنائي",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "تم تسجيل أحجام الأوزان وتحديث سجل القوة ومكافأة النقاط",
                fontSize = 12.sp,
                color = Color.LightGray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            // New PR Card if achieved
            if (recentPr != null && recentPr.isNewPr) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFD600).copy(alpha = 0.15f)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFD600))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🥇", fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                "رقم قياسي شخصي جديد (PR)!",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFFFD600),
                                fontSize = 14.sp
                            )
                            Text(
                                "1RM المقدر الجديد: ${recentPr.estimated1RmKg} كجم (+${recentPr.improvementPercent}%)",
                                fontSize = 12.sp,
                                color = Color.White
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(14.dp))
            }

            // Stats Grid (Total Volume, Duration, Calories)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = if (totalVolumeKg > 0) "${totalVolumeKg.toInt()} كجم" else "وزن جسم",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E5FF)
                    )
                    Text("الحجم المرفوع 🏋️", fontSize = 11.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${(durationSeconds / 60).coerceAtLeast(1)} د",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF00E676)
                    )
                    Text("الوقت المستغرق ⏱️", fontSize = 11.sp, color = Color.Gray)
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${exercise.estimatedCaloriesBurned}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFFF5722)
                    )
                    Text("سعرة محروقة 🔥", fontSize = 11.sp, color = Color.Gray)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // If routine has more exercises
            if (!activeProgramTitle.isNullOrBlank() && (routineExerciseIndex + 1) < totalRoutineExercises && onNextExercise != null) {
                Button(
                    onClick = onNextExercise,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("next_routine_exercise_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676))
                ) {
                    Text(
                        text = "الانتقال للتمرين التالي في الجدول (${routineExerciseIndex + 2}/$totalRoutineExercises) 🚀",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            Button(
                onClick = onDone,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("celebration_done_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (activeProgramTitle.isNullOrBlank()) Color(0xFF00E676) else Color(0xFF1E293B)
                )
            ) {
                Text(
                    text = if (activeProgramTitle.isNullOrBlank()) "العودة للتمارين" else "إنهاء اليوم التدريبي والعودة",
                    fontWeight = FontWeight.Bold,
                    color = if (activeProgramTitle.isNullOrBlank()) Color.Black else Color.White
                )
            }
        }
    }
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%02d:%02d", m, s)
}
