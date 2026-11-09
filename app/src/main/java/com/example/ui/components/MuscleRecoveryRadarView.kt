package com.example.ui.components

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Healing
import androidx.compose.material.icons.filled.HourglassBottom
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MuscleRecoveryState
import com.example.data.RecoveryStatus
import com.example.ui.GmMainViewModel
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldYellow

@Composable
fun MuscleRecoveryRadarCard(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val recoveryStates by viewModel.muscleRecoveryStates.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("muscle_recovery_radar_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.55f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(EmeraldNeon.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Healing,
                        contentDescription = null,
                        tint = EmeraldNeon,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "مؤشر الاستشفاء العضلي الذكي",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "متابعة جاهزية العضلات للتدريب وتجنب الإجهاد الزائد",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            recoveryStates.forEach { item ->
                MuscleRecoveryRow(state = item)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun MuscleRecoveryRow(
    state: MuscleRecoveryState,
    modifier: Modifier = Modifier
) {
    val statusColor = Color(state.status.colorCode)
    val remainingHours = ((100 - state.recoveryPercentage) * state.muscleGroup.baseRecoveryHours / 100).coerceAtLeast(0)

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, statusColor.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = state.muscleGroup.arabicName,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = statusColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = state.status.labelArabic,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall,
                            color = statusColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                }

                Text(
                    text = "${state.recoveryPercentage}%",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { state.recoveryPercentage / 100f },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = statusColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (state.hoursSinceLastWorkout >= 900) "لم يتم التدريب مؤخراً" else "منذ ${state.hoursSinceLastWorkout.toInt()} ساعة",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (remainingHours > 0 && state.recoveryPercentage < 90) {
                    Text(
                        text = "متبقي ~$remainingHours ساعة للاستشفاء",
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldYellow,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Text(
                        text = "جاهزة للتمرين الآن ⚡",
                        style = MaterialTheme.typography.labelSmall,
                        color = EmeraldNeon,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
