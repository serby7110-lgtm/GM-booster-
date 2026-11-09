package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.*
import com.example.data.database.ExerciseEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomProgramBuilderDialog(
    catalogExercises: List<ExerciseEntity>,
    onDismiss: () -> Unit,
    onSaveProgram: (
        titleArabic: String,
        titleEnglish: String,
        descriptionArabic: String,
        goal: ProgramGoal,
        durationWeeks: Int,
        daysPerWeek: Int,
        difficulty: ProgramDifficulty,
        routineDays: List<ProgramRoutineDayItem>
    ) -> Unit
) {
    var programTitle by remember { mutableStateOf("") }
    var programDescription by remember { mutableStateOf("") }
    var selectedGoal by remember { mutableStateOf(ProgramGoal.BULKING) }
    var selectedDifficulty by remember { mutableStateOf(ProgramDifficulty.INTERMEDIATE) }
    var durationWeeks by remember { mutableStateOf(8) }
    var daysPerWeek by remember { mutableStateOf(4) }

    // Routine Days State
    var routineDays by remember {
        mutableStateOf(
            listOf(
                ProgramRoutineDayItem(
                    id = "temp_1",
                    programId = "",
                    dayNumber = 1,
                    dayTitleArabic = "اليوم الأول: دفع (Push)",
                    targetMusclesSummary = "صدر، أكتاف، ترايسبس",
                    exercises = listOf(
                        ProgramDayExerciseItem("chest_bench_press", "بنش برس بالبار مستوي", "Flat Bench Press", 4, "8-10", 90, 8.0f, "تركيز على التكنيك النظيف", "بار")
                    )
                ),
                ProgramRoutineDayItem(
                    id = "temp_2",
                    programId = "",
                    dayNumber = 2,
                    dayTitleArabic = "اليوم الثاني: سحب (Pull)",
                    targetMusclesSummary = "ظهر، بايسبس",
                    exercises = listOf(
                        ProgramDayExerciseItem("back_pullups", "عقلة بالوزن", "Pull-ups", 4, "8-10", 90, 8.0f, "سحب باللاتس", "بار عقلة")
                    )
                )
            )
        )
    }

    var showExercisePickerForDayIndex by remember { mutableStateOf<Int?>(null) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("custom_builder_close_button")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                    Text(
                        text = "🛠️ تصميم برنامج تدريبي مخصص",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Button(
                        onClick = {
                            if (programTitle.isBlank()) {
                                errorMessage = "يرجى كتابة اسم للبرنامج التدريبي"
                                return@Button
                            }
                            if (routineDays.isEmpty() || routineDays.all { it.exercises.isEmpty() }) {
                                errorMessage = "يرجى إضافة تمرين واحد على الأقل في أيام البرنامج"
                                return@Button
                            }
                            onSaveProgram(
                                programTitle,
                                programTitle,
                                programDescription.ifBlank { "برنامج تدريبي مخصص مصمم لتحقيق أفضل نتائج الضخامة والقوة." },
                                selectedGoal,
                                durationWeeks,
                                daysPerWeek,
                                selectedDifficulty,
                                routineDays
                            )
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("save_custom_program_button")
                    ) {
                        Text("حفظ البرنامج", fontWeight = FontWeight.Bold)
                    }
                }

                if (errorMessage != null) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            modifier = Modifier.padding(12.dp),
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Program Meta Section
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                Text("1. بيانات البرنامج الأساسية", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                                OutlinedTextField(
                                    value = programTitle,
                                    onValueChange = { programTitle = it; errorMessage = null },
                                    label = { Text("اسم البرنامج (مثال: بروجرام التضخيم الخماسي)") },
                                    modifier = Modifier.fillMaxWidth().testTag("custom_program_title_input"),
                                    singleLine = true
                                )

                                OutlinedTextField(
                                    value = programDescription,
                                    onValueChange = { programDescription = it },
                                    label = { Text("وصف البرنامج والهدف التدريبي (اختياري)") },
                                    modifier = Modifier.fillMaxWidth(),
                                    maxLines = 2
                                )

                                // Goal Selection
                                Text("الهدف الأساسي:", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    ProgramGoal.values().take(3).forEach { goal ->
                                        FilterChip(
                                            selected = selectedGoal == goal,
                                            onClick = { selectedGoal = goal },
                                            label = { Text("${goal.badgeIcon} ${goal.arabicTitle}", fontSize = 11.sp) }
                                        )
                                    }
                                }

                                // Weeks & Days
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("المدة بالأسابيع ($durationWeeks أسابيع)", fontSize = 12.sp)
                                        Slider(
                                            value = durationWeeks.toFloat(),
                                            onValueChange = { durationWeeks = it.toInt() },
                                            valueRange = 4f..16f,
                                            steps = 11
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("أيام التمرين ($daysPerWeek أيام/أسبوع)", fontSize = 12.sp)
                                        Slider(
                                            value = daysPerWeek.toFloat(),
                                            onValueChange = { daysPerWeek = it.toInt() },
                                            valueRange = 2f..7f,
                                            steps = 4
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Routine Days Section
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "2. أيام وجلسات التمارين (${routineDays.size} أيام)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            FilledTonalButton(
                                onClick = {
                                    val newDayNum = routineDays.size + 1
                                    routineDays = routineDays + ProgramRoutineDayItem(
                                        id = "temp_$newDayNum",
                                        programId = "",
                                        dayNumber = newDayNum,
                                        dayTitleArabic = "اليوم $newDayNum: تمارين متنوعة",
                                        targetMusclesSummary = "كامل الجسم",
                                        exercises = emptyList()
                                    )
                                },
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إضافة يوم تدريبي", fontSize = 12.sp)
                            }
                        }
                    }

                    // Days List
                    itemsIndexed(routineDays) { dayIdx, dayItem ->
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(28.dp)
                                                .background(MaterialTheme.colorScheme.primary, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${dayIdx + 1}", color = MaterialTheme.colorScheme.onPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(dayItem.dayTitleArabic, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                    }

                                    if (routineDays.size > 1) {
                                        IconButton(
                                            onClick = {
                                                routineDays = routineDays.toMutableList().also { it.removeAt(dayIdx) }
                                            },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Delete, contentDescription = "حذف اليوم", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                // Exercises in this Day
                                if (dayItem.exercises.isEmpty()) {
                                    Text("لا توجد تمارين بعد في هذا اليوم.", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(vertical = 4.dp))
                                } else {
                                    dayItem.exercises.forEachIndexed { exIdx, ex ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(vertical = 4.dp)
                                                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp))
                                                .padding(8.dp),
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(ex.exerciseNameArabic, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                                Text("${ex.targetSets} مجموعات × ${ex.repRange} عدة | راحة ${ex.restSeconds}ث", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                                            }
                                            IconButton(
                                                onClick = {
                                                    val updatedExercises = dayItem.exercises.toMutableList().also { it.removeAt(exIdx) }
                                                    routineDays = routineDays.toMutableList().also {
                                                        it[dayIdx] = dayItem.copy(exercises = updatedExercises)
                                                    }
                                                },
                                                modifier = Modifier.size(24.dp)
                                            ) {
                                                Icon(Icons.Default.Close, contentDescription = "حذف التمرين", modifier = Modifier.size(16.dp), tint = Color.Gray)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                OutlinedButton(
                                    onClick = { showExercisePickerForDayIndex = dayIdx },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.FitnessCenter, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("+ إضافة تمرين لهذا اليوم", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Exercise Picker Sheet / Dialog
    if (showExercisePickerForDayIndex != null) {
        val targetDayIdx = showExercisePickerForDayIndex!!
        var searchQuery by remember { mutableStateOf("") }
        var selectedCategoryFilter by remember { mutableStateOf<String?>(null) }

        val availableExercises = remember(searchQuery, selectedCategoryFilter, catalogExercises) {
            val baseList = if (catalogExercises.isNotEmpty()) {
                catalogExercises.map {
                    ProgramDayExerciseItem(
                        exerciseId = it.id,
                        exerciseNameArabic = it.nameArabic,
                        exerciseNameEnglish = it.nameEnglish,
                        targetSets = it.defaultSets,
                        repRange = it.defaultReps,
                        restSeconds = it.restDurationSeconds,
                        equipmentArabic = it.equipmentRequired
                    )
                }
            } else {
                WorkoutDatabase.allExercises.map {
                    ProgramDayExerciseItem(
                        exerciseId = it.id,
                        exerciseNameArabic = it.nameArabic,
                        exerciseNameEnglish = it.nameEnglish,
                        targetSets = it.defaultSets,
                        repRange = it.defaultReps,
                        restSeconds = it.restDurationSeconds,
                        equipmentArabic = it.equipmentArabic
                    )
                }
            }

            baseList.filter {
                (searchQuery.isBlank() || it.exerciseNameArabic.contains(searchQuery, ignoreCase = true) || it.exerciseNameEnglish.contains(searchQuery, ignoreCase = true))
            }
        }

        Dialog(onDismissRequest = { showExercisePickerForDayIndex = null }) {
            Surface(
                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f),
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("اختر تمرين لإضافته", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        IconButton(onClick = { showExercisePickerForDayIndex = null }) {
                            Icon(Icons.Default.Close, contentDescription = "إلغاء")
                        }
                    }

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("ابحث عن اسم التمرين...") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(availableExercises.size) { idx ->
                            val ex = availableExercises[idx]
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        val day = routineDays[targetDayIdx]
                                        val updated = day.copy(exercises = day.exercises + ex)
                                        routineDays = routineDays.toMutableList().also { it[targetDayIdx] = updated }
                                        showExercisePickerForDayIndex = null
                                    },
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(12.dp).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(ex.exerciseNameArabic, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        Text(ex.equipmentArabic, fontSize = 11.sp, color = Color.Gray)
                                    }
                                    Icon(Icons.Default.AddCircleOutline, contentDescription = "إضافة", tint = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
