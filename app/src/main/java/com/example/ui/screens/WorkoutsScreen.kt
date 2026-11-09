package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.Exercise
import com.example.data.WorkoutCategory
import com.example.data.WorkoutDatabase
import com.example.data.WorkoutEnvironment
import com.example.ui.GmMainViewModel
import com.example.ui.components.DifficultyBadge
import com.example.ui.components.ExerciseDetailDialog
import com.example.ui.components.SmartWorkoutGeneratorCard
import com.example.ui.components.WeakPointFocusCard
import com.example.ui.components.WorkoutProgramsView
import com.example.ui.components.ProgramDetailModal
import com.example.ui.components.CustomProgramBuilderDialog
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldYellow

@Composable
fun WorkoutsScreen(
    viewModel: GmMainViewModel,
    onStartWorkout: (Exercise) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val selectedEnv by viewModel.selectedEnvironment.collectAsState()
    val selectedCat by viewModel.selectedCategory.collectAsState()
    val filterByBodyTypeOnly by viewModel.filterByBodyTypeOnly.collectAsState()
    val bodyType = viewModel.getBodyTypeEnum()
    val filteredExercises = viewModel.getFilteredExercises()
    val selectedDetailExercise by viewModel.selectedExerciseForDetail.collectAsState()

    // Program States
    val allPrograms by viewModel.allPrograms.collectAsState()
    val activeProgram by viewModel.activeProgram.collectAsState()
    val catalogExercises by viewModel.catalogExercises.collectAsState()
    val selectedProgramIdForDetail by viewModel.selectedProgramIdForDetail.collectAsState()

    var activeWorkoutsTab by remember { mutableIntStateOf(0) } // 0: Programs, 1: Library, 2: Weak Points, 3: Smart Generator
    var showCustomProgramBuilder by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("workouts_screen")
    ) {
        Spacer(modifier = Modifier.height(12.dp))

        // Title and Section Navigation
        Text(
            text = "${viewModel.getString("workouts_title")} (${WorkoutDatabase.allExercises.size} حركات مدروسة)",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Main Mode Navigation Tabs (Programs, Library, Weak Points, Smart Generator)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val tabs = listOf(
                Pair(0, "📋 البرامج"),
                Pair(1, "📚 المكتبة"),
                Pair(2, "🎯 الضعف"),
                Pair(3, "✨ المولد")
            )

            tabs.forEach { (index, title) ->
                val isSelected = activeWorkoutsTab == index
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) FlameOrange else Color.Transparent)
                        .clickable { activeWorkoutsTab = index }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        when (activeWorkoutsTab) {
            0 -> {
                // Workout Programs & Periodized Routines View
                WorkoutProgramsView(
                    programs = allPrograms,
                    activeProgram = activeProgram,
                    onSelectProgram = { viewModel.openProgramDetails(it) },
                    onCreateCustomProgramClick = { showCustomProgramBuilder = true },
                    modifier = Modifier.weight(1f)
                )
            }
            2 -> {
                // Weak Point Focus View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        WeakPointFocusCard(
                            viewModel = viewModel,
                            onStartExercise = { onStartWorkout(it) }
                        )
                    }
                }
            }
            3 -> {
                // Smart Workout Generator View
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 32.dp)
                ) {
                    item {
                        SmartWorkoutGeneratorCard(
                            viewModel = viewModel,
                            onStartExercise = { onStartWorkout(it) }
                        )
                    }
                }
            }
            else -> {
                // Exercise Library (With Common Mistakes & Target Muscle Inspection)
                // Environment Segmented Switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val envOptions = listOf(
                        Pair(WorkoutEnvironment.BOTH, viewModel.getString("all_workouts")),
                        Pair(WorkoutEnvironment.HOME, "${viewModel.getString("home_workouts")} 🏠"),
                        Pair(WorkoutEnvironment.GYM, "${viewModel.getString("gym_workouts")} 🏋️")
                    )

                    envOptions.forEach { (env, label) ->
                        val isSelected = selectedEnv == env
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) ElectricBlue else Color.Transparent)
                                .clickable { viewModel.setEnvironmentFilter(env) }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Body type filter toggle banner
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (filterByBodyTypeOnly) FlameOrange.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.toggleBodyTypeFilter() }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = FlameOrange,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (filterByBodyTypeOnly) "${viewModel.getString("custom_for_body_type")} ${bodyType.getLocalizedName(currentLang)}" else viewModel.getString("show_all_body_types"),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                color = if (filterByBodyTypeOnly) FlameOrange else MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(22.dp)
                                .clip(CircleShape)
                                .background(if (filterByBodyTypeOnly) FlameOrange else MaterialTheme.colorScheme.outline),
                            contentAlignment = Alignment.Center
                        ) {
                            if (filterByBodyTypeOnly) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Muscle Group Category System Grid / Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        FilterChip(
                            selected = selectedCat == null,
                            onClick = { viewModel.setCategoryFilter(null) },
                            label = { Text("جميع المجموعات العضلية ✨") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlameOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }

                    items(WorkoutCategory.entries.toTypedArray()) { category ->
                        val count = WorkoutDatabase.allExercises.count { it.category == category }
                        FilterChip(
                            selected = selectedCat == category,
                            onClick = { viewModel.setCategoryFilter(category) },
                            label = { Text("${category.getLocalizedTitle(currentLang)} ($count)") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlameOrange,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Systematic Exercise Categorization List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    if (filteredExercises.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 40.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = viewModel.getString("no_workouts_match"),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    } else if (selectedCat != null) {
                        item {
                            CategoryHeaderBanner(category = selectedCat!!, currentLang = currentLang, count = filteredExercises.size)
                        }

                        items(filteredExercises) { exercise ->
                            WorkoutCardItem(
                                exercise = exercise,
                                currentLang = currentLang,
                                startLabel = viewModel.getString("start_workout_btn"),
                                onOpenDetails = { viewModel.openExerciseDetails(exercise) },
                                onStart = { onStartWorkout(exercise) }
                            )
                        }
                    } else {
                        val grouped = filteredExercises.groupBy { it.category }

                        WorkoutCategory.entries.forEach { category ->
                            val exercisesInCategory = grouped[category] ?: emptyList()
                            if (exercisesInCategory.isNotEmpty()) {
                                item {
                                    CategoryHeaderBanner(category = category, currentLang = currentLang, count = exercisesInCategory.size)
                                }

                                items(exercisesInCategory) { exercise ->
                                    WorkoutCardItem(
                                        exercise = exercise,
                                        currentLang = currentLang,
                                        startLabel = viewModel.getString("start_workout_btn"),
                                        onOpenDetails = { viewModel.openExerciseDetails(exercise) },
                                        onStart = { onStartWorkout(exercise) }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Modal dialog for detailed exercise analysis, common mistakes, and safety
    selectedDetailExercise?.let { exercise ->
        ExerciseDetailDialog(
            exercise = exercise,
            onDismiss = { viewModel.closeExerciseDetails() },
            onStartWorkout = { ex ->
                viewModel.closeExerciseDetails()
                onStartWorkout(ex)
            }
        )
    }

    // Program Details Modal
    if (selectedProgramIdForDetail != null) {
        val selectedProgram = allPrograms.find { it.id == selectedProgramIdForDetail }
        if (selectedProgram != null) {
            val routineDays by viewModel.getProgramDaysFlow(selectedProgram.id).collectAsState(initial = emptyList())
            ProgramDetailModal(
                program = selectedProgram,
                routineDays = routineDays,
                onDismiss = { viewModel.closeProgramDetails() },
                onSetActive = { viewModel.setActiveProgram(selectedProgram.id) },
                onStartDayRoutine = { day ->
                    viewModel.startProgramDayRoutine(day, selectedProgram.titleArabic)
                },
                onDeleteProgram = if (selectedProgram.isCustom) {
                    {
                        viewModel.deleteProgram(selectedProgram.id)
                        viewModel.closeProgramDetails()
                    }
                } else null
            )
        }
    }

    // Custom Program Builder Dialog
    if (showCustomProgramBuilder) {
        CustomProgramBuilderDialog(
            catalogExercises = catalogExercises,
            onDismiss = { showCustomProgramBuilder = false },
            onSaveProgram = { titleAr, titleEn, desc, goal, duration, daysCount, diff, days ->
                viewModel.createCustomProgram(
                    titleArabic = titleAr,
                    titleEnglish = titleEn,
                    descriptionArabic = desc,
                    goal = goal,
                    durationWeeks = duration,
                    daysPerWeek = daysCount,
                    difficulty = diff,
                    routineDays = days
                )
            }
        )
    }
}

@Composable
fun CategoryHeaderBanner(
    category: WorkoutCategory,
    currentLang: AppLanguage,
    count: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 6.dp, bottom = 2.dp),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(FlameOrange.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = FlameOrange,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = category.getLocalizedTitle(currentLang),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surface
            ) {
                Text(
                    text = "$count تمارين",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FlameOrange
                )
            }
        }
    }
}

@Composable
fun WorkoutCardItem(
    exercise: Exercise,
    currentLang: AppLanguage,
    startLabel: String,
    onOpenDetails: () -> Unit,
    onStart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOpenDetails() }
            .testTag("workout_card_${exercise.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = exercise.getName(currentLang),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (currentLang == AppLanguage.ARABIC) exercise.nameEnglish else exercise.nameArabic,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DifficultyBadge(difficultyRating = exercise.difficultyRating)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = exercise.getInstructions(currentLang),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2
            )

            // Common Mistakes preview pill
            val firstMistake = exercise.getResolvedCommonMistakes().firstOrNull()
            if (firstMistake != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = FlameOrange.copy(alpha = 0.1f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = FlameOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "تجنب الخطأ: $firstMistake",
                            style = MaterialTheme.typography.labelSmall,
                            color = FlameOrange,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${exercise.defaultSets} مجموعات",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = FlameOrange
                    )
                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        text = exercise.defaultReps,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = onOpenDetails,
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("التفاصيل", fontSize = 12.sp)
                    }

                    Button(
                        onClick = onStart,
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                        modifier = Modifier.height(34.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(startLabel, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}
