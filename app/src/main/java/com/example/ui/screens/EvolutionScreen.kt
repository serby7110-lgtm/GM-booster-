package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Compare
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.GmMainViewModel
import com.example.ui.components.AddWeightDialog
import com.example.ui.components.InteractiveWeightChart
import com.example.ui.components.PersonalRecordsTrophyCard
import com.example.ui.components.WeeklyTrendsDashboard
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun EvolutionScreen(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val profile by viewModel.userProfile.collectAsState()
    val weightEntries by viewModel.weightEntries.collectAsState()
    val allWaterLogs by viewModel.allWaterLogs.collectAsState()
    val workoutLogs by viewModel.workoutLogs.collectAsState()
    val liveSteps by viewModel.liveSteps.collectAsState()
    val bodyType = viewModel.getBodyTypeEnum()

    var showAddWeightDialog by remember { mutableStateOf(false) }

    val currentWeight = profile?.currentWeightKg ?: 73.5f
    val targetWeight = profile?.targetWeightKg ?: 78.0f
    val heightM = (profile?.heightCm ?: 178f) / 100f
    val bmi = currentWeight / (heightM * heightM)

    val weeklySummary = remember(profile, weightEntries, allWaterLogs, workoutLogs, liveSteps) {
        viewModel.getWeeklyTrendsSummary()
    }

    val (bmiStatus, bmiColor) = when {
        bmi < 18.5f -> Pair("نحافة (يحتاج تضخيم)", CyanEnergy)
        bmi in 18.5f..24.9f -> Pair("وزن مثالي متوازن", EmeraldNeon)
        bmi in 25.0f..29.9f -> Pair("وزن زائد خفيف", AmberGold)
        else -> Pair("مستوى دهون مرتفع", FlameOrange)
    }

    if (showAddWeightDialog) {
        AddWeightDialog(
            currentWeight = currentWeight,
            onDismiss = { showAddWeightDialog = false },
            onConfirm = { weight, waist, chest, arm, note ->
                viewModel.addWeightEntry(weight, waist, chest, arm, note, null)
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title & Add Record Button
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = viewModel.getString("weekly_dashboard_title"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = viewModel.getString("weekly_trends_subtitle"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddWeightDialog = true },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanEnergy),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("open_add_weight_button")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تسجيل قياس", color = Color.Black, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // Weekly Trends Dashboard (Visualizes Trends for Weight, Water Intake, and Calories Burned using Charts)
        item {
            WeeklyTrendsDashboard(
                summary = weeklySummary,
                viewModel = viewModel
            )
        }

        // BMI & Metrics Analysis Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "مؤشر كتلة الجسم (BMI)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = bmiStatus,
                            style = MaterialTheme.typography.labelMedium,
                            color = bmiColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bmiColor.copy(alpha = 0.15f))
                            .border(1.dp, bmiColor.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "%.1f".format(bmi),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = bmiColor
                        )
                    }
                }
            }
        }

        // Body Evolution Photos Comparison Card (قبل وبعد / تطور الجسم بالصور)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Compare, contentDescription = null, tint = FlameOrange)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "معرض تطور الجسم (قبل وبعد)",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(FlameOrange.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "مقارنة صور",
                                style = MaterialTheme.typography.labelSmall,
                                color = FlameOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Before and After side-by-side Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Before Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.body_types_art),
                                        contentDescription = "صورة قبل",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(Color.Black.copy(alpha = 0.7f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("البداية (قبل)", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "${weightEntries.firstOrNull()?.weightKg ?: 71.0f} كجم",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                                Text("الخصر: 80 سم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        // After / Current Card
                        Card(
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Column(
                                modifier = Modifier.padding(10.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.healthy_meals_art),
                                        contentDescription = "صورة الآن",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomStart)
                                            .padding(6.dp)
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(EmeraldNeon.copy(alpha = 0.85f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text("الآن (تطور)", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$currentWeight كجم",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldNeon
                                )
                                Text("الخصر: 79 سم", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }

        // Personal Records (PR) & One-Rep-Max (1RM) Trophy Room
        item {
            PersonalRecordsTrophyCard(viewModel = viewModel)
        }

        // Measurement History List
        item {
            Text(
                text = "سجل القياسات الزمنية (${weightEntries.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        val sdf = SimpleDateFormat("dd MMMM yyyy", Locale("ar"))
        items(weightEntries.reversed()) { entry ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${entry.weightKg} كجم",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = FlameOrange
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sdf.format(Date(entry.dateTimestamp)),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        if (entry.noteArabic.isNotBlank()) {
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = entry.noteArabic,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        if (entry.waistCm != null || entry.chestCm != null || entry.armCm != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                entry.waistCm?.let { Text("خصر: $it سم", style = MaterialTheme.typography.labelSmall, color = CyanEnergy) }
                                entry.chestCm?.let { Text("صدر: $it سم", style = MaterialTheme.typography.labelSmall, color = AmberGold) }
                                entry.armCm?.let { Text("ذراع: $it سم", style = MaterialTheme.typography.labelSmall, color = EmeraldNeon) }
                            }
                        }
                    }

                    IconButton(onClick = { viewModel.deleteWeightEntry(entry.entryId) }) {
                        Icon(Icons.Default.DeleteOutline, contentDescription = "حذف القياس", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
