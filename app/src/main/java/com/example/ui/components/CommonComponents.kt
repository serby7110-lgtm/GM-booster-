package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.BodyType
import com.example.data.RewardSystem
import com.example.ui.theme.AmberGold
import com.example.ui.theme.DarkCardBg
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import com.example.ui.theme.FlameOrangeLight

@Composable
fun GmAppTopHeader(
    userName: String,
    totalPoints: Float,
    streakDays: Int,
    bodyType: BodyType,
    isCloudSynced: Boolean,
    onNavigateToRewards: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val level = RewardSystem.getCurrentLevel(totalPoints)

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // User & Level Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.then(
                        if (onNavigateToRewards != null) Modifier.clickable { onNavigateToRewards() }
                        else Modifier
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(FlameOrange, AmberGold)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = level.iconEmoji,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = userName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = level.nameArabic,
                                style = MaterialTheme.typography.bodySmall,
                                color = FlameOrange,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Quick Stats
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.then(
                        if (onNavigateToRewards != null) Modifier.clickable { onNavigateToRewards() }
                        else Modifier
                    )
                ) {
                    // Points Pill
                    PointsPill(points = totalPoints)

                    // Streak Pill
                    StreakPill(streak = streakDays)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Body type tag indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(FlameOrange.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "نمط الجسم: ${bodyType.arabicName}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameOrange,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = "نظام نقاط الصعوبة 0.1 - 3.0",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun PointsPill(points: Float, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(
                Brush.horizontalGradient(
                    colors = listOf(AmberGold.copy(alpha = 0.2f), FlameOrange.copy(alpha = 0.25f))
                )
            )
            .border(1.dp, AmberGold.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = AmberGold,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "%.1f".format(points),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
        }
    }
}

@Composable
fun StreakPill(streak: Int, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .background(FlameOrange.copy(alpha = 0.2f))
            .border(1.dp, FlameOrange.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.LocalFireDepartment,
                contentDescription = null,
                tint = FlameOrange,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = "$streak أيام",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = FlameOrange
            )
        }
    }
}

@Composable
fun DifficultyBadge(
    difficultyRating: Float, // 0.1 to 3.0
    modifier: Modifier = Modifier
) {
    val (color, label) = when {
        difficultyRating <= 1.0f -> Pair(EmeraldNeon, "خفيف ($difficultyRating)")
        difficultyRating <= 2.0f -> Pair(AmberGold, "متوسط ($difficultyRating)")
        else -> Pair(FlameOrange, "شديد ($difficultyRating)")
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(3.dp))
            Text(
                text = "$label نقطة",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuickStatCard(
    title: String,
    value: String,
    unit: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    subtext: String? = null
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
        tonalElevation = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.Bottom) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = unit,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 2.dp)
                )
            }

            if (subtext != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtext,
                    style = MaterialTheme.typography.labelSmall,
                    color = accentColor,
                    fontSize = 11.sp
                )
            }
        }
    }
}
