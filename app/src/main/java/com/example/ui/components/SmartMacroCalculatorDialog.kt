package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.CalorieGoalType
import com.example.data.MacroCalculationResult
import com.example.data.MacroCalculatorEngine
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldYellow

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SmartMacroCalculatorDialog(
    initialWeightKg: Float = 75f,
    initialHeightCm: Float = 175f,
    initialAge: Int = 25,
    initialGender: String = "male",
    onDismiss: () -> Unit,
    onApplyMacros: (MacroCalculationResult) -> Unit
) {
    var weightInput by remember { mutableStateOf(initialWeightKg.toString()) }
    var heightInput by remember { mutableStateOf(initialHeightCm.toString()) }
    var ageInput by remember { mutableStateOf(initialAge.toString()) }
    var selectedGender by remember { mutableStateOf(initialGender) }
    var selectedGoal by remember { mutableStateOf(CalorieGoalType.BULKING) }
    var selectedActivity by remember { mutableStateOf("moderate") }

    val currentResult = remember(weightInput, heightInput, ageInput, selectedGender, selectedGoal, selectedActivity) {
        val w = weightInput.toFloatOrNull() ?: 75f
        val h = heightInput.toFloatOrNull() ?: 175f
        val a = ageInput.toIntOrNull() ?: 25
        MacroCalculatorEngine.calculate(
            gender = selectedGender,
            weightKg = w,
            heightCm = h,
            age = a,
            activityLevel = selectedActivity,
            goalType = selectedGoal
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp))
                .testTag("smart_macro_calculator_dialog"),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(EmeraldNeon.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = EmeraldNeon,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "حاسبة السعرات والماكروز الذكية",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Input Parameters (Weight, Height, Age)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = weightInput,
                        onValueChange = { weightInput = it },
                        label = { Text("الوزن (كجم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = heightInput,
                        onValueChange = { heightInput = it },
                        label = { Text("الطول (سم)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    )

                    OutlinedTextField(
                        value = ageInput,
                        onValueChange = { ageInput = it },
                        label = { Text("العمر") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(0.8f),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Goal Selector
                Text(
                    text = "الهدف الرياضي الحالي:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    CalorieGoalType.values().forEach { goal ->
                        val isSelected = goal == selectedGoal
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedGoal = goal },
                            label = { Text(goal.titleArabic, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = FlameOrange,
                                selectedLabelColor = Color.White
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Calculated Results Card
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, EmeraldNeon.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "احتياجك اليومي الموصى به",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${currentResult.targetCalories} سعرة حرارية",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Black,
                                    color = FlameOrange
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text("معدل الأيض BMR: ${currentResult.bmrCalories} ك.س", style = MaterialTheme.typography.labelSmall)
                                Text("الحرق الكلي TDEE: ${currentResult.tdeeCalories} ك.س", style = MaterialTheme.typography.labelSmall)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Macros Breakdown Grid
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.surface)
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("بروتين 🥩", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${currentResult.proteinGrams} غ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = FlameOrange)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("كاربوهيدرات 🍚", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${currentResult.carbsGrams} غ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = GoldYellow)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("دهون صحية 🥑", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${currentResult.fatGrams} غ", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = EmeraldNeon)
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text("ماء 💧", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${"%.1f".format(currentResult.recommendedWaterMl / 1000f)} لتر", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = ElectricBlue)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Apply Button
                Button(
                    onClick = {
                        onApplyMacros(currentResult)
                        onDismiss()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("apply_calculated_macros_button"),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = EmeraldNeon,
                        contentColor = Color.Black
                    )
                ) {
                    Text(
                        text = "تطبيق هذه الأهداف على خطتي اليومية ✅",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
