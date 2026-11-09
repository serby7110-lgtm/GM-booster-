package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.Recipe
import com.example.ui.GmMainViewModel
import com.example.ui.components.AddMealDialog
import com.example.ui.components.MuscleRecoveryRadarCard
import com.example.ui.components.RecipeDetailDialog
import com.example.ui.components.SmartMacroCalculatorDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun NutritionScreen(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val todayWaterLogs by viewModel.todayWaterLogs.collectAsState()
    val todayWaterTotalMl by viewModel.todayWaterTotalMl.collectAsState()

    val bodyType = viewModel.getBodyTypeEnum()
    val macros = viewModel.calculateMacros()
    val recipes = viewModel.getFilteredRecipes()

    var showAddMealDialog by remember { mutableStateOf(false) }
    var showMacroCalculatorDialog by remember { mutableStateOf(false) }
    var selectedRecipeForModal by remember { mutableStateOf<Recipe?>(null) }
    var showWaterHistory by remember { mutableStateOf(false) }

    val totalCalories = todayMeals.sumOf { it.calories }
    val totalProtein = todayMeals.sumOf { it.proteinGrams }
    val totalCarbs = todayMeals.sumOf { it.carbsGrams }
    val totalFats = todayMeals.sumOf { it.fatGrams }

    // Dynamic water calculation based on user weight and activity
    val targetWaterMl = viewModel.calculateDailyWaterGoalMl()
    val currentWaterMl = todayWaterTotalMl ?: (profile?.waterGlasses?.times(250) ?: 0)
    val waterProgress = (currentWaterMl.toFloat() / targetWaterMl.coerceAtLeast(1000)).coerceIn(0f, 1f)
    val currentWaterGlasses = (currentWaterMl / 250)
    val targetWaterGlasses = (targetWaterMl / 250)

    if (showAddMealDialog) {
        AddMealDialog(
            onDismiss = { showAddMealDialog = false },
            onConfirm = { name, cat, cal, prot, carb, fat ->
                viewModel.addMeal(name, cat, cal, prot, carb, fat)
            }
        )
    }

    if (showMacroCalculatorDialog) {
        SmartMacroCalculatorDialog(
            initialWeightKg = profile?.currentWeightKg ?: 75f,
            initialHeightCm = profile?.heightCm ?: 175f,
            initialAge = profile?.age ?: 25,
            initialGender = profile?.gender ?: "male",
            onDismiss = { showMacroCalculatorDialog = false },
            onApplyMacros = { res ->
                viewModel.applyCustomMacros(res)
            }
        )
    }

    selectedRecipeForModal?.let { recipe ->
        RecipeDetailDialog(
            recipe = recipe,
            onDismiss = { selectedRecipeForModal = null }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Daily Calorie & Macro Target Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = viewModel.getString("calorie_tracker_title") + " (${bodyType.getLocalizedName(currentLang)})",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${viewModel.getString("target_label")}: ${macros.dailyCalories} kcal",
                                style = MaterialTheme.typography.bodySmall,
                                color = FlameOrange
                            )
                        }

                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Button(
                                onClick = { showMacroCalculatorDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("open_macro_calc_button")
                            ) {
                                Icon(Icons.Default.Calculate, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الحاسبة", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = { showAddMealDialog = true },
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("open_add_meal_button")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(viewModel.getString("log_meal"), style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    val calorieProgress = (totalCalories.toFloat() / macros.dailyCalories).coerceIn(0f, 1f)
                    LinearProgressIndicator(
                        progress = { calorieProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = if (calorieProgress >= 1f) EmeraldNeon else FlameOrange,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "$totalCalories ${viewModel.getString("consumed_label")}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = FlameOrange
                        )
                        Text(
                            text = "${viewModel.getString("remaining_label")}: ${(macros.dailyCalories - totalCalories).coerceAtLeast(0)} kcal",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // 3 Macro Progress Bars (Protein, Carbs, Fats)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        MacroColumn(
                            title = viewModel.getString("protein"),
                            current = totalProtein,
                            target = macros.proteinGrams,
                            color = CyanEnergy,
                            modifier = Modifier.weight(1f)
                        )
                        MacroColumn(
                            title = viewModel.getString("carbs"),
                            current = totalCarbs,
                            target = macros.carbsGrams,
                            color = AmberGold,
                            modifier = Modifier.weight(1f)
                        )
                        MacroColumn(
                            title = viewModel.getString("healthy_fats"),
                            current = totalFats,
                            target = macros.fatGrams,
                            color = EmeraldNeon,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Dedicated Comprehensive Water Hydration Tracker Card with Dynamic Calculation & History
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("water_tracker_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header with Icon and Dynamic Goal Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(CyanEnergy.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalDrink,
                                    contentDescription = null,
                                    tint = CyanEnergy,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = viewModel.getString("water_tracker_title"),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "$currentWaterMl / $targetWaterMl ml ($currentWaterGlasses/$targetWaterGlasses ${viewModel.getString("glasses")})",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = CyanEnergy,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }

                        // Percentage Pill
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(CyanEnergy.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${(waterProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanEnergy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Progress Indicator Bar
                    LinearProgressIndicator(
                        progress = { waterProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(10.dp)
                            .clip(RoundedCornerShape(5.dp)),
                        color = CyanEnergy,
                        trackColor = MaterialTheme.colorScheme.surface
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Dynamic calculation badge formula explanation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${viewModel.getString("water_calculated_formula")} (${profile?.currentWeightKg ?: 73.5f} kg)",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        if (profile?.waterRemindersEnabled == true) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NotificationsActive,
                                    contentDescription = null,
                                    tint = EmeraldNeon,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = viewModel.getString("reminders_active"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldNeon,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick-Add Buttons: +150ml, +250ml, +500ml, +750ml
                    Text(
                        text = viewModel.getString("quick_add_water"),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        WaterQuickAddChip(
                            label = "+150 ml",
                            subtext = viewModel.getString("cup_small"),
                            onClick = { viewModel.logWater(150) },
                            modifier = Modifier.weight(1f)
                        )
                        WaterQuickAddChip(
                            label = "+250 ml",
                            subtext = viewModel.getString("glass_standard"),
                            onClick = { viewModel.logWater(250) },
                            modifier = Modifier.weight(1f),
                            isHighlighted = true
                        )
                        WaterQuickAddChip(
                            label = "+500 ml",
                            subtext = viewModel.getString("bottle_half_liter"),
                            onClick = { viewModel.logWater(500) },
                            modifier = Modifier.weight(1f)
                        )
                        WaterQuickAddChip(
                            label = "+750 ml",
                            subtext = viewModel.getString("bottle_sports"),
                            onClick = { viewModel.logWater(750) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Toggle History Section Button
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showWaterHistory = !showWaterHistory }
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = null,
                                tint = CyanEnergy,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${viewModel.getString("water_history_title")} (${todayWaterLogs.size})",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold,
                                color = CyanEnergy
                            )
                        }

                        Icon(
                            imageVector = if (showWaterHistory) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null,
                            tint = CyanEnergy
                        )
                    }

                    // Collapsible History Log List
                    AnimatedVisibility(visible = showWaterHistory) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            if (todayWaterLogs.isEmpty()) {
                                Text(
                                    text = viewModel.getString("no_water_logs_today"),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(vertical = 8.dp)
                                )
                            } else {
                                val timeFormat = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }
                                todayWaterLogs.forEach { log ->
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(10.dp))
                                            .background(MaterialTheme.colorScheme.surface)
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Icon(
                                                imageVector = Icons.Default.LocalDrink,
                                                contentDescription = null,
                                                tint = CyanEnergy,
                                                modifier = Modifier.size(16.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "+${log.amountMl} ml",
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = timeFormat.format(Date(log.dateTimestamp)),
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontSize = 11.sp
                                            )
                                        }

                                        IconButton(
                                            onClick = { viewModel.deleteWaterLog(log.id, log.amountMl) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.DeleteOutline,
                                                contentDescription = "Delete water log",
                                                tint = MaterialTheme.colorScheme.error,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Healthy Home Recipes Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = viewModel.getString("recipes_section_title") + " (${bodyType.getLocalizedName(currentLang)})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${recipes.size} ${viewModel.getString("approved_recipes")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = EmeraldNeon
                )
            }
        }

        // Healthy Recipes Cards
        items(recipes) { recipe ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedRecipeForModal = recipe }
                    .testTag("recipe_card_${recipe.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = recipe.category.arabicTitle,
                            style = MaterialTheme.typography.labelSmall,
                            color = FlameOrange,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "⏱️ ${recipe.prepTimeMinutes} min",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = recipe.titleArabic,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = recipe.descriptionArabic,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("${recipe.calories} kcal", fontWeight = FontWeight.Bold, color = FlameOrange, fontSize = 12.sp)
                            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${recipe.proteinGrams}g ${viewModel.getString("protein")}", fontWeight = FontWeight.SemiBold, color = CyanEnergy, fontSize = 12.sp)
                            Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${recipe.carbsGrams}g ${viewModel.getString("carbs")}", fontWeight = FontWeight.SemiBold, color = AmberGold, fontSize = 12.sp)
                        }

                        Button(
                            onClick = { selectedRecipeForModal = recipe },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.MenuBook, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(viewModel.getString("view_recipe"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }
        }

        // Today's Logged Meals Header
        item {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${viewModel.getString("today_meals")} (${todayMeals.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        if (todayMeals.isEmpty()) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = viewModel.getString("no_meals_today"),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            items(todayMeals) { meal ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = meal.mealNameArabic,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${meal.calories} kcal • ${meal.proteinGrams}g ${viewModel.getString("protein")} • ${meal.carbsGrams}g ${viewModel.getString("carbs")} • ${meal.fatGrams}g ${viewModel.getString("healthy_fats")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        IconButton(onClick = { viewModel.deleteMeal(meal.mealId) }) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Delete meal",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WaterQuickAddChip(
    label: String,
    subtext: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    isHighlighted: Boolean = false
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() },
        color = if (isHighlighted) CyanEnergy.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = if (isHighlighted) CyanEnergy else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtext,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
fun MacroColumn(
    title: String,
    current: Int,
    target: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = "${current}g / ${target}g",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = color
        )
        Spacer(modifier = Modifier.height(4.dp))
        val prog = (current.toFloat() / target.coerceAtLeast(1)).coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { prog },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
