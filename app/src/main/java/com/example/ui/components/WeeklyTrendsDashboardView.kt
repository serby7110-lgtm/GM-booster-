package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.LocalDrink
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Scale
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingFlat
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.DayTrendStat
import com.example.data.WeeklyProgressSummary
import com.example.ui.GmMainViewModel
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

enum class TrendDashboardTab {
    ALL,
    WEIGHT,
    WATER,
    CALORIES
}

@Composable
fun WeeklyTrendsDashboard(
    summary: WeeklyProgressSummary,
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(TrendDashboardTab.ALL) }
    var selectedDayIndex by remember { mutableStateOf(summary.dayStats.lastIndex) }
    val currentLang = viewModel.currentLanguage.value

    val selectedDay = summary.dayStats.getOrNull(selectedDayIndex) ?: summary.dayStats.lastOrNull()

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Tab Selection Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                Pair(TrendDashboardTab.ALL, viewModel.getString("tab_all_trends")),
                Pair(TrendDashboardTab.WEIGHT, viewModel.getString("tab_weight_trend")),
                Pair(TrendDashboardTab.WATER, viewModel.getString("tab_water_trend")),
                Pair(TrendDashboardTab.CALORIES, viewModel.getString("tab_calories_trend"))
            ).forEach { (tab, title) ->
                val isSelected = selectedTab == tab
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { selectedTab = tab },
                    color = if (isSelected) FlameOrange else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        // Top KPI Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Weight Delta Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Scale, contentDescription = null, tint = AmberGold, modifier = Modifier.size(16.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val isGain = summary.weightDeltaKg > 0
                            val isLoss = summary.weightDeltaKg < 0
                            Icon(
                                imageVector = if (isGain) Icons.Default.TrendingUp else if (isLoss) Icons.Default.TrendingDown else Icons.Default.TrendingFlat,
                                contentDescription = null,
                                tint = if (isGain) CyanEnergy else if (isLoss) EmeraldNeon else AmberGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${if (summary.weightDeltaKg > 0) "+" else ""}${summary.weightDeltaKg} kg",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isGain) CyanEnergy else EmeraldNeon,
                                fontSize = 10.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${summary.endWeightKg} kg",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = viewModel.getString("weekly_avg_weight"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            // Water Average Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalDrink, contentDescription = null, tint = CyanEnergy, modifier = Modifier.size(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(CyanEnergy.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${summary.waterGoalMetDays}/7 d",
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanEnergy,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${summary.averageDailyWaterMl} ml",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = viewModel.getString("weekly_avg_water"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }

            // Calories Burned Card
            Card(
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(FlameOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "${summary.totalWorkouts} WOD",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameOrange,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${summary.totalCaloriesBurned}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = viewModel.getString("weekly_total_calories"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp
                    )
                }
            }
        }

        // Main Chart Section based on selected tab
        when (selectedTab) {
            TrendDashboardTab.ALL -> {
                // 1) Weight Evolution Curve
                WeeklyWeightTrendChart(
                    dayStats = summary.dayStats,
                    targetWeightKg = summary.targetWeightKg,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang
                )

                // 2) Water Intake Bar Chart
                WeeklyWaterIntakeBarChart(
                    dayStats = summary.dayStats,
                    dailyGoalMl = summary.dailyWaterGoalMl,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang
                )

                // 3) Calories Burned Chart
                WeeklyCaloriesBurnedChart(
                    dayStats = summary.dayStats,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang
                )
            }
            TrendDashboardTab.WEIGHT -> {
                WeeklyWeightTrendChart(
                    dayStats = summary.dayStats,
                    targetWeightKg = summary.targetWeightKg,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang,
                    isExpanded = true
                )
            }
            TrendDashboardTab.WATER -> {
                WeeklyWaterIntakeBarChart(
                    dayStats = summary.dayStats,
                    dailyGoalMl = summary.dailyWaterGoalMl,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang,
                    isExpanded = true
                )
            }
            TrendDashboardTab.CALORIES -> {
                WeeklyCaloriesBurnedChart(
                    dayStats = summary.dayStats,
                    selectedDayIndex = selectedDayIndex,
                    onSelectDay = { selectedDayIndex = it },
                    lang = currentLang,
                    isExpanded = true
                )
            }
        }

        // Selected Day Detailed Breakdown Inspector
        if (selectedDay != null) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.8f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (selectedDay.isToday) FlameOrange else CyanEnergy)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "${selectedDay.getDayName(currentLang)} (${selectedDay.dateFormatted})${if (selectedDay.isToday) " • اليوم" else ""}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Text(
                            text = viewModel.getString("tap_day_for_details"),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Day Weight
                        DayMetricPill(
                            icon = Icons.Default.Scale,
                            title = "الوزن",
                            value = "${selectedDay.recordedWeightKg ?: "--"} كجم",
                            accentColor = AmberGold,
                            modifier = Modifier.weight(1f)
                        )

                        // Day Water
                        DayMetricPill(
                            icon = Icons.Default.LocalDrink,
                            title = "الماء",
                            value = "${selectedDay.waterIntakeMl} مل",
                            accentColor = CyanEnergy,
                            isGoalMet = selectedDay.isWaterGoalMet,
                            modifier = Modifier.weight(1f)
                        )

                        // Day Calories
                        DayMetricPill(
                            icon = Icons.Default.LocalFireDepartment,
                            title = "الحرق",
                            value = "${selectedDay.caloriesBurned} سعرة",
                            accentColor = FlameOrange,
                            isGoalMet = selectedDay.isCalorieGoalMet,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }

        // Smart Progress Insights Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AmberGold, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = viewModel.getString("weekly_insights_title"),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "• ${summary.getWeightPaceStatus(currentLang)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• معدل شرب الماء وصل إلى ${summary.averageDailyWaterMl} مل/يوم، مما يسرع الاستشفاء العضلي ويرفع كفاءة حرق الدهون أثناء التمارين.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "• ذروة الحرق الأسبوعي تحققت يوم ${summary.getPeakBurnDayName(currentLang)} بإجمالي ${summary.peakBurnCalories} سعرة حرارية محروقة! 🔥",
                    style = MaterialTheme.typography.bodySmall,
                    color = FlameOrange,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
fun DayMetricPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    isGoalMet: Boolean? = null
) {
    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = title, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                if (isGoalMet == true) {
                    Spacer(modifier = Modifier.width(2.dp))
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(10.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
        }
    }
}

// -------------------------------------------------------------
// 1. Weekly Weight Trend Interactive Chart (Smooth Bezier Line)
// -------------------------------------------------------------
@Composable
fun WeeklyWeightTrendChart(
    dayStats: List<DayTrendStat>,
    targetWeightKg: Float,
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val weights = dayStats.mapNotNull { it.recordedWeightKg }
    val minWeight = (weights.minOrNull() ?: 60f) - 1.0f
    val maxWeight = (weights.maxOrNull() ?: 85f) + 1.0f
    val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Scale, contentDescription = null, tint = AmberGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "منحنى تغير الوزن الأسبوعي (كجم)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مقارنة الوزن اليومي مع الوزن المستهدف",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanEnergy)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "الهدف: $targetWeightKg",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanEnergy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Canvas
            val chartHeight = if (isExpanded) 200.dp else 140.dp
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                val width = size.width
                val height = size.height

                // Draw Target Weight horizontal dashed line
                val targetY = height - (((targetWeightKg - minWeight) / weightRange) * height).coerceIn(0f, height)
                drawLine(
                    color = CyanEnergy.copy(alpha = 0.5f),
                    start = Offset(0f, targetY),
                    end = Offset(width, targetY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                )

                val stepX = if (dayStats.size > 1) width / (dayStats.size - 1) else width / 2
                val points = dayStats.mapIndexed { index, stat ->
                    val x = if (dayStats.size == 1) width / 2 else index * stepX
                    val w = stat.recordedWeightKg ?: minWeight
                    val y = height - (((w - minWeight) / weightRange) * height).coerceIn(0f, height)
                    Offset(x, y)
                }

                // Fill Gradient below line
                val fillPath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, height)
                        points.forEach { point -> lineTo(point.x, point.y) }
                        lineTo(points.last().x, height)
                        close()
                    }
                }

                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(
                        colors = listOf(AmberGold.copy(alpha = 0.35f), Color.Transparent),
                        startY = 0f,
                        endY = height
                    )
                )

                // Stroke Curve Line
                val strokePath = Path().apply {
                    if (points.isNotEmpty()) {
                        moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val midX = (prev.x + curr.x) / 2
                            val midY = (prev.y + curr.y) / 2
                            quadraticTo(prev.x, prev.y, midX, midY)
                        }
                        if (points.size > 1) {
                            lineTo(points.last().x, points.last().y)
                        }
                    }
                }

                drawPath(
                    path = strokePath,
                    color = AmberGold,
                    style = Stroke(width = 3.dp.toPx())
                )

                // Draw Point markers
                points.forEachIndexed { idx, point ->
                    val isSelected = idx == selectedDayIndex
                    drawCircle(
                        color = if (isSelected) FlameOrange else AmberGold,
                        radius = if (isSelected) 7.dp.toPx() else 4.5.dp.toPx(),
                        center = point
                    )
                    drawCircle(
                        color = Color.White,
                        radius = if (isSelected) 3.5.dp.toPx() else 2.dp.toPx(),
                        center = point
                    )
                }
            }

            // Days Labels Row with clickable triggers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayStats.forEachIndexed { index, day ->
                    val isSelected = index == selectedDayIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectDay(index) }
                            .background(if (isSelected) AmberGold.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = day.getDayName(lang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) AmberGold else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${day.recordedWeightKg ?: "--"}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) AmberGold else MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 2. Weekly Water Intake Interactive Bar / Grouped Chart
// -------------------------------------------------------------
@Composable
fun WeeklyWaterIntakeBarChart(
    dayStats: List<DayTrendStat>,
    dailyGoalMl: Int,
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val maxWater = (dayStats.maxOfOrNull { it.waterIntakeMl } ?: dailyGoalMl).coerceAtLeast(dailyGoalMl + 500)

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalDrink, contentDescription = null, tint = CyanEnergy)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "اتجاه شرب الماء اليومي (مل)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "مقارنة الاستهلاك اليومي مع هدف الترطيب",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(CyanEnergy)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "هدف اليوم: $dailyGoalMl ml",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanEnergy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Bars Container
            val barHeight = if (isExpanded) 190.dp else 130.dp
            val trackBgColor = MaterialTheme.colorScheme.surfaceVariant
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(barHeight)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val width = size.width
                val height = size.height
                val numDays = dayStats.size
                val barWidth = (width / numDays) * 0.55f
                val spacing = width / numDays

                // Draw Goal Line
                val goalY = height - ((dailyGoalMl.toFloat() / maxWater.toFloat()) * height).coerceIn(0f, height)
                drawLine(
                    color = CyanEnergy.copy(alpha = 0.6f),
                    start = Offset(0f, goalY),
                    end = Offset(width, goalY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                )

                dayStats.forEachIndexed { index, day ->
                    val centerX = (index * spacing) + (spacing / 2)
                    val left = centerX - (barWidth / 2)
                    val barFillHeight = ((day.waterIntakeMl.toFloat() / maxWater.toFloat()) * height).coerceIn(0f, height)
                    val top = height - barFillHeight

                    val isSelected = index == selectedDayIndex
                    val isGoalMet = day.isWaterGoalMet

                    // Background Track Bar
                    drawRoundRect(
                        color = trackBgColor,
                        topLeft = Offset(left, 0f),
                        size = Size(barWidth, height),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Fill Bar
                    val barBrush = Brush.verticalGradient(
                        colors = if (isGoalMet) listOf(
                            EmeraldNeon,
                            CyanEnergy
                        ) else listOf(
                            CyanEnergy.copy(alpha = 0.9f),
                            CyanEnergy.copy(alpha = 0.4f)
                        ),
                        startY = top,
                        endY = height
                    )

                    drawRoundRect(
                        brush = barBrush,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barFillHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Selected ring
                    if (isSelected) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left - 2.dp.toPx(), top - 2.dp.toPx()),
                            size = Size(barWidth + 4.dp.toPx(), barFillHeight + 4.dp.toPx()),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            // Bottom Day and Amount Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayStats.forEachIndexed { index, day ->
                    val isSelected = index == selectedDayIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectDay(index) }
                            .background(if (isSelected) CyanEnergy.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = day.getDayName(lang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) CyanEnergy else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${day.waterIntakeMl}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (day.isWaterGoalMet) EmeraldNeon else MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// 3. Weekly Calories Burned Interactive Chart
// -------------------------------------------------------------
@Composable
fun WeeklyCaloriesBurnedChart(
    dayStats: List<DayTrendStat>,
    selectedDayIndex: Int,
    onSelectDay: (Int) -> Unit,
    lang: AppLanguage,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false
) {
    val maxCalories = (dayStats.maxOfOrNull { it.caloriesBurned } ?: 500).coerceAtLeast(600)
    val avgCalories = if (dayStats.isNotEmpty()) dayStats.map { it.caloriesBurned }.average().toInt() else 350

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = FlameOrange)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "اتجاه حرق السعرات اليومي (kcal)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "التمارين الرياضية + الخطوات والنشاط الحركي",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(FlameOrange)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "المعدل: $avgCalories kcal",
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Chart
            val chartHeight = if (isExpanded) 190.dp else 130.dp
            val trackBgColor = MaterialTheme.colorScheme.surfaceVariant
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(chartHeight)
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                val width = size.width
                val height = size.height
                val numDays = dayStats.size
                val barWidth = (width / numDays) * 0.55f
                val spacing = width / numDays

                // Average line
                val avgY = height - ((avgCalories.toFloat() / maxCalories.toFloat()) * height).coerceIn(0f, height)
                drawLine(
                    color = AmberGold.copy(alpha = 0.5f),
                    start = Offset(0f, avgY),
                    end = Offset(width, avgY),
                    strokeWidth = 2.dp.toPx(),
                    pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f), 0f)
                )

                dayStats.forEachIndexed { index, day ->
                    val centerX = (index * spacing) + (spacing / 2)
                    val left = centerX - (barWidth / 2)
                    val barFillHeight = ((day.caloriesBurned.toFloat() / maxCalories.toFloat()) * height).coerceIn(0f, height)
                    val top = height - barFillHeight

                    val isSelected = index == selectedDayIndex

                    // Background Track
                    drawRoundRect(
                        color = trackBgColor,
                        topLeft = Offset(left, 0f),
                        size = Size(barWidth, height),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Fill Flame Gradient
                    val flameBrush = Brush.verticalGradient(
                        colors = listOf(
                            FlameOrange,
                            AmberGold
                        ),
                        startY = top,
                        endY = height
                    )

                    drawRoundRect(
                        brush = flameBrush,
                        topLeft = Offset(left, top),
                        size = Size(barWidth, barFillHeight),
                        cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                    )

                    // Indicator for workouts count on that day
                    if (day.workoutCount > 0) {
                        drawCircle(
                            color = Color.White,
                            radius = 3.dp.toPx(),
                            center = Offset(centerX, top - 6.dp.toPx())
                        )
                    }

                    // Selection stroke
                    if (isSelected) {
                        drawRoundRect(
                            color = Color.White,
                            topLeft = Offset(left - 2.dp.toPx(), top - 2.dp.toPx()),
                            size = Size(barWidth + 4.dp.toPx(), barFillHeight + 4.dp.toPx()),
                            cornerRadius = CornerRadius(10.dp.toPx(), 10.dp.toPx()),
                            style = Stroke(width = 2.dp.toPx())
                        )
                    }
                }
            }

            // Bottom Day & Calorie Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dayStats.forEachIndexed { index, day ->
                    val isSelected = index == selectedDayIndex
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onSelectDay(index) }
                            .background(if (isSelected) FlameOrange.copy(alpha = 0.2f) else Color.Transparent)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = day.getDayName(lang),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) FlameOrange else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${day.caloriesBurned}",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isSelected) FlameOrange else MaterialTheme.colorScheme.onSurface,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
