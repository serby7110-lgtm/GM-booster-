package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.database.WeeklyChallengeEntity
import com.example.ui.GmMainViewModel
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.GoldYellow

@Composable
fun WeeklyChallengesCard(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val challenges by viewModel.weeklyChallenges.collectAsState()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weekly_challenges_card"),
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
                        .background(GoldYellow.copy(alpha = 0.18f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CardGiftcard,
                        contentDescription = null,
                        tint = GoldYellow,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "تحديات الأسبوع والجوائز 🎁",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "أكمل التحديات الرياضية لحصد نقاط المكافآت",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (challenges.isEmpty()) {
                Text(
                    text = "جاري تحميل التحديات الأسبوعية...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                challenges.forEach { ch ->
                    ChallengeItemRow(
                        challenge = ch,
                        onClaim = { viewModel.claimChallengeReward(ch) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun ChallengeItemRow(
    challenge: WeeklyChallengeEntity,
    onClaim: () -> Unit,
    modifier: Modifier = Modifier
) {
    val progressFraction = (challenge.currentProgress.toFloat() / challenge.targetGoal.toFloat()).coerceIn(0f, 1f)
    val unitArabic = when(challenge.category) {
        "STREAK" -> "أيام"
        "WORKOUTS" -> "تمارين"
        "WATER" -> "أيام"
        "CALORIES" -> "سعرة"
        "STEPS" -> "خطوة"
        "PR" -> "أرقام"
        else -> "إنجاز"
    }
    val iconEmoji = when(challenge.category) {
        "STREAK" -> "🔥"
        "WORKOUTS" -> "💪"
        "WATER" -> "💧"
        "CALORIES" -> "⚡"
        "STEPS" -> "🏃"
        "PR" -> "🏆"
        else -> "🎯"
    }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = if (challenge.isCompleted && !challenge.isClaimed) {
            androidx.compose.foundation.BorderStroke(1.5.dp, GoldYellow)
        } else null
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = iconEmoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = challenge.titleArabic,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = challenge.descriptionArabic,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = GoldYellow.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "+${challenge.rewardPoints} نقطة",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldYellow
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (challenge.isCompleted) EmeraldNeon else ElectricBlue,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${challenge.currentProgress} / ${challenge.targetGoal} $unitArabic",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (challenge.isClaimed) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "تم الاستلام ✅",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = EmeraldNeon
                        )
                    }
                } else if (challenge.isCompleted) {
                    Button(
                        onClick = onClaim,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldYellow,
                            contentColor = Color.Black
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Icon(Icons.Default.EmojiEvents, contentDescription = null, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("استلام الجائزة 🎉", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Text(
                        text = "جاري التقدم",
                        style = MaterialTheme.typography.labelSmall,
                        color = ElectricBlue
                    )
                }
            }
        }
    }
}
