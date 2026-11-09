package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ProgramDayExerciseItem
import com.example.data.database.ProgramRoutineDayEntity
import com.example.data.database.WorkoutProgramEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailModal(
    program: WorkoutProgramEntity,
    routineDays: List<ProgramRoutineDayEntity>,
    onDismiss: () -> Unit,
    onSetActive: () -> Unit,
    onStartDayRoutine: (day: ProgramRoutineDayEntity) -> Unit,
    onDeleteProgram: (() -> Unit)? = null
) {
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_program_modal_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }

                    Text(
                        text = "تفاصيل البرنامج التدريبي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (onDeleteProgram != null) {
                        IconButton(onClick = onDeleteProgram) {
                            Icon(Icons.Default.Delete, contentDescription = "حذف البرنامج", tint = Color.Red)
                        }
                    } else {
                        Spacer(modifier = Modifier.size(48.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Program Meta Hero
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(18.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Text(program.iconBadge, fontSize = 32.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = program.titleArabic,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        if (program.titleEnglish.isNotBlank()) {
                                            Text(
                                                text = program.titleEnglish,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = program.descriptionArabic,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Surface(
                                        color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "⏱️ ${program.durationWeeks} أسابيع",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "📅 ${program.daysPerWeek} أيام/أسبوع",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                    Surface(
                                        color = MaterialTheme.colorScheme.tertiaryContainer.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    ) {
                                        Text(
                                            text = "🎯 ${program.difficultyLevel}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Routine Days Section
                    item {
                        Text(
                            text = "📋 جدول الأيام والتمارين (${routineDays.size} أيام)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    itemsIndexed(routineDays) { index, day ->
                        val exercises = remember(day.exercisesJson) {
                            ProgramDayExerciseItem.listFromJsonString(day.exercisesJson)
                        }

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(day.dayTitleArabic, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                        if (day.targetMusclesSummary.isNotBlank()) {
                                            Text("العضلات: ${day.targetMusclesSummary}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                        }
                                    }

                                    Button(
                                        onClick = {
                                            onStartDayRoutine(day)
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E676)),
                                        shape = RoundedCornerShape(10.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                        modifier = Modifier.testTag("start_day_routine_btn_${day.dayNumber}")
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("بدء اليوم", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))
                                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f))
                                Spacer(modifier = Modifier.height(8.dp))

                                // Exercise items list
                                exercises.forEachIndexed { exIdx, ex ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("${exIdx + 1}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(ex.exerciseNameArabic, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                            Text("${ex.targetSets} مجموعات × ${ex.repRange} تكرار (راحة: ${ex.restSeconds}ث)", fontSize = 11.sp, color = Color.Gray)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Bottom Action: Set as Active Program
                if (!program.isCurrentActiveProgram) {
                    Button(
                        onClick = {
                            onSetActive()
                            onDismiss()
                        },
                        modifier = Modifier.fillMaxWidth().testTag("set_active_program_action"),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("تعيين كبرنامجي الحالي المعتمد ⭐", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }
    }
}
