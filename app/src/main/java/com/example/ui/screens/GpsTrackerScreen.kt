package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.DirectionsWalk
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.GpsTrackEntity
import com.example.data.tracking.GpsCoordinate
import com.example.data.tracking.GpsSessionState
import com.example.data.tracking.GpsSessionType
import com.example.data.tracking.GpsTrackingSession
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GpsTrackerScreen(
    gpsSession: GpsTrackingSession,
    savedTracks: List<GpsTrackEntity>,
    userDailyStepLimit: Int,
    userDailyDistanceLimitKm: Float,
    isOnline: Boolean = true,
    onStartTracking: (GpsSessionType) -> Unit,
    onPauseTracking: () -> Unit,
    onResumeTracking: () -> Unit,
    onFinishAndSave: () -> Unit,
    onResetTracking: () -> Unit,
    onUpdateDailyLimits: (Int, Float) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Live Tracking & Map, 1: Activity History
    var selectedType by remember { mutableStateOf(GpsSessionType.OUTDOOR_RUN) }
    var showLimitSettingsDialog by remember { mutableStateOf(false) }

    val isTracking = gpsSession.state == GpsSessionState.TRACKING
    val isPaused = gpsSession.state == GpsSessionState.PAUSED
    val isIdle = gpsSession.state == GpsSessionState.IDLE

    val currentDistance = gpsSession.distanceKm
    val distanceTarget = gpsSession.targetDistanceKm.coerceAtLeast(0.5f)
    val distanceProgress = (currentDistance / distanceTarget).coerceIn(0f, 1f)

    val currentSteps = gpsSession.stepCount
    val stepTarget = gpsSession.targetSteps.coerceAtLeast(1000)
    val stepProgress = (currentSteps.toFloat() / stepTarget.toFloat()).coerceIn(0f, 1f)

    val isLimitAchieved = (currentDistance >= distanceTarget || currentSteps >= stepTarget) && (isTracking || isPaused)

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
            .testTag("gps_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "GPS Tracker",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "نظام تتبع المشي والجري GPS",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "تتبع مسارك بدقة مع مراقبة الأهداف اليومية (Limit) وتخزين محلي مع مزامنة سحابية",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }

                    IconButton(
                        onClick = { showLimitSettingsDialog = true },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface)
                            .testTag("open_limits_dialog_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Adjust Daily Limit",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }

        // Tab Selector (Live Tracking vs History)
        item {
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                edgePadding = 0.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Map, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("الجلسة المباشرة والخريطة", fontWeight = FontWeight.Bold)
                        }
                    }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("سجل المسارات المحفوظة (${savedTracks.size})", fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        if (selectedTab == 0) {
            // Daily Limit Goals Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Flag,
                                    contentDescription = "Daily Limit",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "الأهداف والحد اليومي المطلوب (Daily Limit)",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(onClick = { showLimitSettingsDialog = true }) {
                                Text("تعديل الهدف ⚙️", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Distance Limit Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "المسافة: ${String.format(Locale.US, "%.2f", currentDistance)} / ${String.format(Locale.US, "%.1f", distanceTarget)} كم",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(distanceProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { distanceProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Step Limit Progress
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "الخطوات: ${String.format(Locale.US, "%,d", currentSteps)} / ${String.format(Locale.US, "%,d", stepTarget)} خطوة",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${(stepProgress * 100).toInt()}%",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF10B981)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { stepProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = Color(0xFF10B981),
                            trackColor = Color(0xFF10B981).copy(alpha = 0.15f)
                        )

                        // Goal Achieved Badge Banner
                        AnimatedVisibility(visible = isLimitAchieved) {
                            Column {
                                Spacer(modifier = Modifier.height(12.dp))
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF10B981).copy(alpha = 0.15f),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        modifier = Modifier.padding(10.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.EmojiEvents,
                                            contentDescription = "Achieved",
                                            tint = Color(0xFF10B981),
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = "رائع! لقد حققت الحد اليومي المطلوب بنجاح 🏆",
                                                style = MaterialTheme.typography.labelLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF047857)
                                            )
                                            Text(
                                                text = "استمر في تعزيز لياقتك وحرق المزيد من السعرات!",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = Color(0xFF047857),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Mode Selector (When Idle)
            if (isIdle) {
                item {
                    Text(
                        text = "اختر نوع النشاط الرياضي:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        GpsSessionType.entries.forEach { type ->
                            val isSelected = selectedType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(14.dp))
                                    .clickable { selectedType = type },
                                shape = RoundedCornerShape(14.dp),
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = when (type) {
                                            GpsSessionType.OUTDOOR_WALK -> Icons.Default.DirectionsWalk
                                            GpsSessionType.OUTDOOR_RUN -> Icons.Default.DirectionsRun
                                            GpsSessionType.CYCLING -> Icons.Default.DirectionsBike
                                            GpsSessionType.INDOOR_WALK -> Icons.Default.DirectionsWalk
                                        },
                                        contentDescription = type.titleArabic,
                                        tint = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(22.dp)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = type.titleArabic,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Interactive Live GPS Route Canvas Map
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        GpsRouteMapCanvas(
                            routeCoordinates = gpsSession.routeCoordinates,
                            isTracking = isTracking,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Top Overlays: GPS Signal & Speed Badge
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = Color.Black.copy(alpha = 0.6f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(if (isTracking) Color(0xFF10B981) else Color(0xFFF59E0B))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isTracking) "GPS نشط ومباشر" else if (isPaused) "GPS متوقف مؤقتاً" else "GPS في وضع الاستعداد",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(20.dp),
                                color = MaterialTheme.colorScheme.primary
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Speed,
                                        contentDescription = "Speed",
                                        tint = MaterialTheme.colorScheme.onPrimary,
                                        modifier = Modifier.size(14.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${String.format(Locale.US, "%.1f", gpsSession.currentSpeedKmh)} كم/س",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                        }

                        // Bottom Overlay: Live Coordinates & Waypoint Count
                        Surface(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(8.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = Color.Black.copy(alpha = 0.65f)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                val lastCoord = gpsSession.routeCoordinates.lastOrNull()
                                Text(
                                    text = if (lastCoord != null) "الإحداثيات: ${String.format(Locale.US, "%.4f, %.4f", lastCoord.latitude, lastCoord.longitude)}" else "في انتظار إشارة البداية...",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White.copy(alpha = 0.85f),
                                    fontSize = 10.sp
                                )
                                Text(
                                    text = "${gpsSession.routeCoordinates.size} نقطة مسار",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }

            // Real-Time Telemetry Metrics Grid
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Duration Card
                        TelemetryMetricCard(
                            title = "الوقت المنقضي",
                            value = formatDuration(gpsSession.durationSeconds),
                            unit = "دقيقة : ثانية",
                            icon = Icons.Default.Timer,
                            iconColor = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f)
                        )

                        // Distance Card
                        TelemetryMetricCard(
                            title = "المسافة المقطوعة",
                            value = String.format(Locale.US, "%.2f", gpsSession.distanceKm),
                            unit = "كيلومتر",
                            icon = Icons.Default.Navigation,
                            iconColor = Color(0xFF0284C7),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Steps Card
                        TelemetryMetricCard(
                            title = "عدد الخطوات",
                            value = String.format(Locale.US, "%,d", gpsSession.stepCount),
                            unit = "خطوة",
                            icon = Icons.Default.DirectionsWalk,
                            iconColor = Color(0xFF10B981),
                            modifier = Modifier.weight(1f)
                        )

                        // Pace Card
                        TelemetryMetricCard(
                            title = "متوسط الوتيرة",
                            value = String.format(Locale.US, "%.1f", gpsSession.avgPaceMinPerKm),
                            unit = "دقيقة / كم",
                            icon = Icons.Default.Speed,
                            iconColor = Color(0xFF8B5CF6),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Calories Burned Strip Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFFEA580C).copy(alpha = 0.15f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("🔥", fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "السعرات المحروقة المقدرة",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = "${gpsSession.caloriesBurned} سعرة حرارية",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFEA580C)
                                    )
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.primaryContainer
                            ) {
                                Text(
                                    text = gpsSession.sessionType.titleArabic,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                        }
                    }
                }
            }

            // Session Action Buttons
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (isIdle) {
                        Button(
                            onClick = { onStartTracking(selectedType) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(54.dp)
                                .testTag("start_gps_tracking_btn"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Start")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "بدء تتبع ${selectedType.titleArabic} 🚀",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    } else {
                        // Pause / Resume Button
                        Button(
                            onClick = { if (isTracking) onPauseTracking() else onResumeTracking() },
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("pause_resume_gps_btn"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isTracking) Color(0xFFF59E0B) else Color(0xFF10B981)
                            )
                        ) {
                            Icon(
                                imageVector = if (isTracking) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isTracking) "Pause" else "Resume"
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isTracking) "إيقاف مؤقت" else "استئناف",
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Finish and Save Button
                        Button(
                            onClick = onFinishAndSave,
                            modifier = Modifier
                                .weight(1f)
                                .height(54.dp)
                                .testTag("finish_save_gps_btn"),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Finish & Save")
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("إنهاء وحفظ 🏆", fontWeight = FontWeight.Bold)
                        }

                        // Reset / Cancel Button
                        OutlinedButton(
                            onClick = onResetTracking,
                            modifier = Modifier
                                .height(54.dp)
                                .testTag("cancel_gps_btn"),
                            shape = RoundedCornerShape(16.dp)
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset")
                        }
                    }
                }
                Spacer(modifier = Modifier.height(20.dp))
            }
        } else {
            // Activity History List
            if (savedTracks.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("🏃", fontSize = 48.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "لا توجد جلسات تتبع محفوظة بعد",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "ابدأ جلستك الأولى للمشي أو الجري وسيتم حفظ كل تفاصيل مسارك وسرعتك والخطوات محلياً وسحابياً!",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
                items(savedTracks) { track ->
                    GpsTrackHistoryCard(track = track)
                }
            }
            item { Spacer(modifier = Modifier.height(20.dp)) }
        }
    }

    // Daily Limit Settings Dialog
    if (showLimitSettingsDialog) {
        DailyLimitSettingsDialog(
            currentStepLimit = userDailyStepLimit,
            currentDistanceLimitKm = userDailyDistanceLimitKm,
            onDismiss = { showLimitSettingsDialog = false },
            onSave = { steps, distance ->
                onUpdateDailyLimits(steps, distance)
                showLimitSettingsDialog = false
            }
        )
    }
}

/**
 * Custom Canvas Drawing the interactive GPS route with grid, waypoints, start/end markers, and pulsing radar ring.
 */
@Composable
fun GpsRouteMapCanvas(
    routeCoordinates: List<GpsCoordinate>,
    isTracking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar_pulse")
    val radarRadius by infiniteTransition.animateFloat(
        initialValue = 10f,
        targetValue = 28f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )
    val radarAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )

    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // 1. Draw Map Grid Background
        val gridSpacing = 40.dp.toPx()
        val gridColor = Color(0xFF334155).copy(alpha = 0.4f)
        var x = 0f
        while (x < width) {
            drawLine(
                color = gridColor,
                start = Offset(x, 0f),
                end = Offset(x, height),
                strokeWidth = 1f
            )
            x += gridSpacing
        }
        var y = 0f
        while (y < height) {
            drawLine(
                color = gridColor,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
            y += gridSpacing
        }

        if (routeCoordinates.isEmpty()) {
            // Draw standby marker in center
            drawCircle(
                color = Color(0xFF3B82F6).copy(alpha = 0.3f),
                radius = 24.dp.toPx(),
                center = Offset(width / 2f, height / 2f)
            )
            drawCircle(
                color = Color(0xFF3B82F6),
                radius = 8.dp.toPx(),
                center = Offset(width / 2f, height / 2f)
            )
            return@Canvas
        }

        // 2. Compute normalized coordinates to fit canvas
        val lats = routeCoordinates.map { it.latitude }
        val lngs = routeCoordinates.map { it.longitude }
        val minLat = lats.minOrNull() ?: 0.0
        val maxLat = lats.maxOrNull() ?: 0.0
        val minLng = lngs.minOrNull() ?: 0.0
        val maxLng = lngs.maxOrNull() ?: 0.0

        val latSpan = (maxLat - minLat).coerceAtLeast(0.0005)
        val lngSpan = (maxLng - minLng).coerceAtLeast(0.0005)

        val padding = 40.dp.toPx()
        val drawWidth = width - (padding * 2)
        val drawHeight = height - (padding * 2)

        val points = routeCoordinates.map { coord ->
            val normX = padding + (((coord.longitude - minLng) / lngSpan) * drawWidth).toFloat()
            val normY = height - padding - (((coord.latitude - minLat) / latSpan) * drawHeight).toFloat()
            Offset(normX, normY)
        }

        // 3. Draw Route Polyline
        val routePath = Path().apply {
            moveTo(points.first().x, points.first().y)
            for (i in 1 until points.size) {
                lineTo(points[i].x, points[i].y)
            }
        }

        // Route Glow Outer
        drawPath(
            path = routePath,
            color = Color(0xFF38BDF8).copy(alpha = 0.4f),
            style = Stroke(
                width = 10.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Route Core Line
        drawPath(
            path = routePath,
            color = Color(0xFF0284C7),
            style = Stroke(
                width = 4.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 4. Draw Start Point Pin (Green)
        val startPoint = points.first()
        drawCircle(
            color = Color(0xFF10B981).copy(alpha = 0.4f),
            radius = 12.dp.toPx(),
            center = startPoint
        )
        drawCircle(
            color = Color(0xFF10B981),
            radius = 6.dp.toPx(),
            center = startPoint
        )

        // 5. Draw Current Runner Pin (Pulsing Radar Pin)
        val currentPoint = points.last()
        if (isTracking) {
            // Radar pulse ring
            drawCircle(
                color = Color(0xFF38BDF8).copy(alpha = radarAlpha),
                radius = radarRadius.dp.toPx(),
                center = currentPoint
            )
        }
        drawCircle(
            color = Color.White,
            radius = 8.dp.toPx(),
            center = currentPoint
        )
        drawCircle(
            color = Color(0xFF0284C7),
            radius = 5.dp.toPx(),
            center = currentPoint
        )
    }
}

@Composable
fun TelemetryMetricCard(
    title: String,
    value: String,
    unit: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                Box(
                    modifier = Modifier
                        .size(26.dp)
                        .clip(CircleShape)
                        .background(iconColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconColor,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = unit,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
        }
    }
}

val GpsTrackEntity.titleArabic: String
    get() = when (sessionType) {
        "OUTDOOR_RUN", "RUN" -> "تتبع الجري في الهواء الطلق"
        "OUTDOOR_WALK", "WALK" -> "تتبع المشي والخطوات"
        "CYCLING" -> "تتبع ركوب الدراجة"
        "INDOOR_WALK" -> "تتبع المشي الداخلي"
        else -> "جلسة حركة وتتبع"
    }

@Composable
fun GpsTrackHistoryCard(
    track: GpsTrackEntity,
    modifier: Modifier = Modifier
) {
    val sdf = SimpleDateFormat("dd MMM yyyy - hh:mm a", Locale.US)
    val dateStr = sdf.format(Date(track.dateTimestamp))

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = when (track.sessionType) {
                                "OUTDOOR_WALK" -> Icons.Default.DirectionsWalk
                                "OUTDOOR_RUN" -> Icons.Default.DirectionsRun
                                "CYCLING" -> Icons.Default.DirectionsBike
                                else -> Icons.Default.DirectionsWalk
                            },
                            contentDescription = track.titleArabic,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = track.titleArabic,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = dateStr,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp
                        )
                    }
                }

                // Cloud Sync Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (track.isSynced) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFF59E0B).copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (track.isSynced) Icons.Default.CloudDone else Icons.Default.Refresh,
                            contentDescription = "Sync",
                            tint = if (track.isSynced) Color(0xFF10B981) else Color(0xFFF59E0B),
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (track.isSynced) "مُزامن سحابياً" else "محفوظ محلياً",
                            style = MaterialTheme.typography.labelSmall,
                            color = if (track.isSynced) Color(0xFF047857) else Color(0xFFB45309),
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Metrics Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("المسافة", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${track.distanceKm} كم", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("الوقت", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(formatDuration(track.durationSeconds), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("الخطوات", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${track.stepCount}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("السعرات", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${track.caloriesBurned} ك.سعرة", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = Color(0xFFEA580C))
                }
            }
        }
    }
}

@Composable
fun DailyLimitSettingsDialog(
    currentStepLimit: Int,
    currentDistanceLimitKm: Float,
    onDismiss: () -> Unit,
    onSave: (Int, Float) -> Unit
) {
    var stepLimit by remember { mutableFloatStateOf(currentStepLimit.toFloat()) }
    var distanceLimit by remember { mutableFloatStateOf(currentDistanceLimitKm) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.width(8.dp))
                Text("تحديد الهدف اليومي (Limit)", fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text(
                    text = "حدد الحد اليومي المستهدف للخطوات والمسافة بالكيلومتر لمتابعة إنجازك الرياضي:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Steps Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الهدف اليومي للخطوات:", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${String.format(Locale.US, "%,d", stepLimit.toInt())} خطوة",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Slider(
                        value = stepLimit,
                        onValueChange = { stepLimit = it },
                        valueRange = 2000f..30000f,
                        steps = 27
                    )
                }

                // Distance Slider
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("الهدف اليومي للمسافة:", style = MaterialTheme.typography.labelMedium)
                        Text(
                            text = "${String.format(Locale.US, "%.1f", distanceLimit)} كم",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0284C7)
                        )
                    }
                    Slider(
                        value = distanceLimit,
                        onValueChange = { distanceLimit = it },
                        valueRange = 1.0f..25.0f,
                        steps = 23
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { onSave(stepLimit.toInt(), (distanceLimit * 10).toInt() / 10f) }) {
                Text("حفظ الهدف 🎯")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

private fun formatDuration(seconds: Int): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format(Locale.US, "%02d:%02d", m, s)
}
