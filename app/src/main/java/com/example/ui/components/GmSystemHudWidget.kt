package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Opacity
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.auth.SocialAccountState
import com.example.data.location.UserLocationInfo
import com.example.data.security.SecurityAuditReport
import com.example.data.tracking.GpsSessionState
import com.example.data.tracking.GpsTrackingSession
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import java.util.Locale

/**
 * Enhanced High-Interactivity System HUD Widget
 * Featuring animated ProgressBars for Daily Steps and Consumed Water in Liters,
 * Real-time Location support, Social account badges, and Security Shield status.
 */
@Composable
fun GmSystemHudWidget(
    liveSteps: Int,
    stepDailyLimit: Int,
    consumedWaterMl: Int,
    targetWaterMl: Int,
    gpsSession: GpsTrackingSession,
    locationInfo: UserLocationInfo,
    socialAccountState: SocialAccountState,
    securityReport: SecurityAuditReport,
    onQuickAddWater: (Int) -> Unit,
    onQuickRemoveWater: (Int) -> Unit,
    onOpenGpsTracker: () -> Unit,
    onOpenLimitSettings: () -> Unit,
    onOpenSocialLinking: () -> Unit,
    onOpenSecurityArmor: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stepProgress = (liveSteps.toFloat() / stepDailyLimit.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isStepGoalReached = liveSteps >= stepDailyLimit && stepDailyLimit > 0

    val consumedLiters = consumedWaterMl / 1000f
    val targetLiters = targetWaterMl / 1000f
    val waterProgress = (consumedWaterMl.toFloat() / targetWaterMl.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    val isWaterGoalReached = consumedWaterMl >= targetWaterMl && targetWaterMl > 0

    val infiniteTransition = rememberInfiniteTransition(label = "hud_pulse")
    val goalGlowScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_scale"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("system_hud_widget"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.95f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Top Status Bar: Location Chip, Social Account Badge, Security Shield & Cloud Sync
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Location & Geocoding Chip
                Surface(
                    onClick = onOpenGpsTracker,
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.testTag("hud_location_chip")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Location",
                            tint = FlameOrange,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = locationInfo.locationName.take(22) + if (locationInfo.locationName.length > 22) "..." else "",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.5.sp
                        )
                    }
                }

                // Security Shield & Social Badges
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Security Shield Chip
                    Surface(
                        onClick = onOpenSecurityArmor,
                        shape = RoundedCornerShape(12.dp),
                        color = EmeraldNeon.copy(alpha = 0.15f),
                        modifier = Modifier.testTag("hud_security_shield_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Security",
                                tint = EmeraldNeon,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${securityReport.overallScore}% آمن",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = EmeraldNeon,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Social Account Linked Chip
                    Surface(
                        onClick = onOpenSocialLinking,
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFF4285F4).copy(alpha = 0.15f),
                        modifier = Modifier.testTag("hud_social_chip")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (socialAccountState.isGoogleLinked) "🌐 Google" else if (socialAccountState.isInstagramLinked) "📸 Insta" else "🔗 ربط الحساب",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4285F4),
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Interactive Metric Cards with Animated ProgressBars
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // 1. Interactive Steps Progress Card (with Animated Linear & Visual Progress)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .scale(if (isStepGoalReached) goalGlowScale else 1f)
                        .clip(RoundedCornerShape(18.dp))
                        .clickable { onOpenLimitSettings() }
                        .testTag("hud_steps_card"),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = if (isStepGoalReached) androidx.compose.foundation.BorderStroke(1.5.dp, FlameOrange) else null,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(FlameOrange.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsWalk,
                                        contentDescription = "Steps",
                                        modifier = Modifier.size(16.dp),
                                        tint = FlameOrange
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الخطوات اليومية",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Percentage Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isStepGoalReached) FlameOrange else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isStepGoalReached) "100% 🏆" else "${(stepProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isStepGoalReached) Color.White else FlameOrange,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = String.format(Locale.US, "%,d", liveSteps),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Black,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "من هدف ${String.format(Locale.US, "%,d", stepDailyLimit)} خطوة",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Animated ProgressBar for Steps
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(FlameOrange.copy(alpha = 0.15f))
                        ) {
                            LinearProgressIndicator(
                                progress = { stepProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = FlameOrange,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }

                // 2. Interactive Water Progress Card (In Liters with Animated ProgressBar)
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .scale(if (isWaterGoalReached) goalGlowScale else 1f)
                        .clip(RoundedCornerShape(18.dp))
                        .testTag("hud_water_card"),
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(18.dp),
                    border = if (isWaterGoalReached) androidx.compose.foundation.BorderStroke(1.5.dp, CyanEnergy) else null,
                    tonalElevation = 2.dp
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(CyanEnergy.copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Opacity,
                                        contentDescription = "Water",
                                        modifier = Modifier.size(16.dp),
                                        tint = CyanEnergy
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الماء المستهلك",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            // Percentage Badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isWaterGoalReached) CyanEnergy else MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = if (isWaterGoalReached) "100% 💧" else "${(waterProgress * 100).toInt()}%",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWaterGoalReached) Color.Black else CyanEnergy,
                                    fontSize = 10.5.sp
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = String.format(Locale.US, "%.2f", consumedLiters),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                color = CyanEnergy
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "لتر",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = CyanEnergy
                            )
                        }

                        Text(
                            text = "من هدف ${String.format(Locale.US, "%.1f", targetLiters)} لتر",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Visual Animated ProgressBar for Water in Liters
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(CyanEnergy.copy(alpha = 0.15f))
                        ) {
                            LinearProgressIndicator(
                                progress = { waterProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp),
                                color = CyanEnergy,
                                trackColor = Color.Transparent
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Bottom Interactive Toolbar: Quick Water Loggers + GPS Session Launcher
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick +250ml (0.25 L) Button
                Surface(
                    onClick = { onQuickAddWater(250) },
                    shape = RoundedCornerShape(12.dp),
                    color = CyanEnergy.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f).testTag("quick_water_250_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Water",
                            tint = CyanEnergy,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "0.25 لتر",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanEnergy,
                            fontSize = 11.sp
                        )
                    }
                }

                // Quick +500ml (0.50 L) Button
                Surface(
                    onClick = { onQuickAddWater(500) },
                    shape = RoundedCornerShape(12.dp),
                    color = CyanEnergy.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1f).testTag("quick_water_500_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 6.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Water",
                            tint = CyanEnergy,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "0.50 لتر",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = CyanEnergy,
                            fontSize = 11.sp
                        )
                    }
                }

                // Undo Water Button (-250ml)
                Surface(
                    onClick = { onQuickRemoveWater(250) },
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.weight(0.7f).testTag("undo_water_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 4.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Undo Water",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "تراجع",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                // GPS Live Run/Walk Launcher
                val isGpsActive = gpsSession.state == GpsSessionState.TRACKING
                Surface(
                    onClick = onOpenGpsTracker,
                    shape = RoundedCornerShape(12.dp),
                    color = if (isGpsActive) FlameOrange else FlameOrange.copy(alpha = 0.15f),
                    modifier = Modifier.weight(1.3f).testTag("quick_gps_btn")
                ) {
                    Row(
                        modifier = Modifier.padding(vertical = 7.dp, horizontal = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isGpsActive) Icons.Default.DirectionsRun else Icons.Default.LocationOn,
                            contentDescription = "GPS Tracker",
                            tint = if (isGpsActive) Color.White else FlameOrange,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isGpsActive) {
                                "${String.format(Locale.US, "%.2f", gpsSession.distanceKm)} كم ⚡"
                            } else {
                                "تتبع GPS 📍"
                            },
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isGpsActive) Color.White else FlameOrange,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
