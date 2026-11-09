package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material.icons.filled.MonitorWeight
import androidx.compose.material.icons.filled.PostAdd
import androidx.compose.material.icons.filled.RestaurantMenu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.MealCategory
import com.example.data.Recipe
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun AddMealDialog(
    onDismiss: () -> Unit,
    onConfirm: (name: String, category: String, calories: Int, protein: Int, carbs: Int, fats: Int) -> Unit
) {
    var mealName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(MealCategory.LUNCH) }
    var caloriesStr by remember { mutableStateOf("450") }
    var proteinStr by remember { mutableStateOf("35") }
    var carbsStr by remember { mutableStateOf("40") }
    var fatsStr by remember { mutableStateOf("12") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.RestaurantMenu, contentDescription = null, tint = FlameOrange)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل وجبة صحية جديدة", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = mealName,
                    onValueChange = { mealName = it },
                    label = { Text("اسم الوجبة (مثلاً: أرز بسمتي ودجاج)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("meal_name_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("نوع الوجبة:", style = MaterialTheme.typography.labelMedium)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    MealCategory.values().forEach { cat ->
                        val isSelected = selectedCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) FlameOrange else MaterialTheme.colorScheme.surfaceVariant)
                                .clickable { selectedCategory = cat }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = cat.arabicTitle.split(" ").first(),
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = caloriesStr,
                        onValueChange = { caloriesStr = it },
                        label = { Text("السعرات (kcal)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("meal_cal_input"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = proteinStr,
                        onValueChange = { proteinStr = it },
                        label = { Text("بروتين (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = carbsStr,
                        onValueChange = { carbsStr = it },
                        label = { Text("كارب (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = fatsStr,
                        onValueChange = { fatsStr = it },
                        label = { Text("دهون (g)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (mealName.isNotBlank()) {
                        onConfirm(
                            mealName,
                            selectedCategory.name,
                            caloriesStr.toIntOrNull() ?: 400,
                            proteinStr.toIntOrNull() ?: 30,
                            carbsStr.toIntOrNull() ?: 35,
                            fatsStr.toIntOrNull() ?: 10
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_meal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
            ) {
                Text("إضافة الوجبة")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun AddWeightDialog(
    currentWeight: Float,
    onDismiss: () -> Unit,
    onConfirm: (weight: Float, waist: Float?, chest: Float?, arm: Float?, note: String) -> Unit
) {
    var weightStr by remember { mutableStateOf(currentWeight.toString()) }
    var waistStr by remember { mutableStateOf("") }
    var chestStr by remember { mutableStateOf("") }
    var armStr by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.MonitorWeight, contentDescription = null, tint = CyanEnergy)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل قياس الوزن الجديد", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = weightStr,
                    onValueChange = { weightStr = it },
                    label = { Text("الوزن الحالي بالكيلوجرام (كجم)*") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("weight_input"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))
                Text("قياسات الجسم الإضافية (اختياري بالـ سم):", style = MaterialTheme.typography.labelSmall)
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = waistStr,
                        onValueChange = { waistStr = it },
                        label = { Text("الخصر") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = chestStr,
                        onValueChange = { chestStr = it },
                        label = { Text("الصدر") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = armStr,
                        onValueChange = { armStr = it },
                        label = { Text("الذراع") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("ملاحظة (مثل: بعد الوجبة / صائم)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val w = weightStr.toFloatOrNull()
                    if (w != null && w > 20f) {
                        onConfirm(
                            w,
                            waistStr.toFloatOrNull(),
                            chestStr.toFloatOrNull(),
                            armStr.toFloatOrNull(),
                            note
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("save_weight_button"),
                colors = ButtonDefaults.buttonColors(containerColor = CyanEnergy)
            ) {
                Text("حفظ القياس", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun CreatePostDialog(
    onDismiss: () -> Unit,
    onPublish: (content: String, workoutTag: String?) -> Unit
) {
    var content by remember { mutableStateOf("") }
    var workoutTag by remember { mutableStateOf("تمرين منزلي وصالة Gm 🔥") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.PostAdd, contentDescription = null, tint = EmeraldNeon)
                Spacer(modifier = Modifier.width(8.dp))
                Text("مشاركة إنجازك مع مجتمع Gm", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("اكتب إنجازك أو نصيحة تحفيزية للأبطال...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .testTag("post_content_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = workoutTag,
                    onValueChange = { workoutTag = it },
                    label = { Text("وسم التمرين أو الوجبة (اختياري)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (content.isNotBlank()) {
                        onPublish(content, workoutTag.ifBlank { null })
                        onDismiss()
                    }
                },
                modifier = Modifier.testTag("publish_post_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon)
            ) {
                Text("نشر الإنجاز 🚀", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun RecipeDetailDialog(
    recipe: Recipe,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = recipe.category.arabicTitle,
                        style = MaterialTheme.typography.labelMedium,
                        color = FlameOrange,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "إغلاق")
                    }
                }

                Text(
                    text = recipe.titleArabic,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = recipe.descriptionArabic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nutrition macro pill
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${recipe.calories}", fontWeight = FontWeight.Bold, color = FlameOrange, fontSize = 18.sp)
                        Text("سعرة", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${recipe.proteinGrams}g", fontWeight = FontWeight.Bold, color = CyanEnergy, fontSize = 18.sp)
                        Text("بروتين", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${recipe.carbsGrams}g", fontWeight = FontWeight.Bold, color = AmberGold, fontSize = 18.sp)
                        Text("كارب", style = MaterialTheme.typography.labelSmall)
                    }
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("${recipe.fatGrams}g", fontWeight = FontWeight.Bold, color = EmeraldNeon, fontSize = 18.sp)
                        Text("دهون", style = MaterialTheme.typography.labelSmall)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "المكونات المطلوبة:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                recipe.ingredients.forEach { ingredient ->
                    Text("• $ingredient", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "خطوات التحضير المنزلية:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                recipe.stepsArabic.forEachIndexed { i, step ->
                    Text("${i + 1}. $step", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(EmeraldNeon.copy(alpha = 0.12f))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "✨ الفائدة لجسمك: ${recipe.benefitsArabic}",
                        style = MaterialTheme.typography.bodySmall,
                        color = EmeraldNeon,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                ) {
                    Text("إغلاق الوصفة")
                }
            }
        }
    }
}
