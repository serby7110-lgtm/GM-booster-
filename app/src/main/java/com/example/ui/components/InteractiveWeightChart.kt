package com.example.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.WeightProgressEntity
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun InteractiveWeightChart(
    entries: List<WeightProgressEntity>,
    targetWeightKg: Float,
    currentWeightKg: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "منحنى تقدم الوزن",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "الوزن الحالي: $currentWeightKg كجم",
                        style = MaterialTheme.typography.bodySmall,
                        color = FlameOrange,
                        fontWeight = FontWeight.SemiBold
                    )
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
                        text = "الهدف: $targetWeightKg كجم",
                        style = MaterialTheme.typography.labelSmall,
                        color = CyanEnergy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "لا توجد قياسات مسجلة بعد. أضف قياسك الأول!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                // Canvas Graph
                val weights = entries.map { it.weightKg }
                val minWeight = (weights.minOrNull() ?: 60f) - 1.5f
                val maxWeight = (weights.maxOrNull() ?: 85f) + 1.5f
                val weightRange = (maxWeight - minWeight).coerceAtLeast(1f)

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .padding(horizontal = 8.dp, vertical = 12.dp)
                ) {
                    val width = size.width
                    val height = size.height

                    // Draw Target Weight horizontal dashed line
                    val targetY = height - (((targetWeightKg - minWeight) / weightRange) * height).coerceIn(0f, height)
                    drawLine(
                        color = CyanEnergy.copy(alpha = 0.6f),
                        start = Offset(0f, targetY),
                        end = Offset(width, targetY),
                        strokeWidth = 2.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    )

                    val stepX = if (entries.size > 1) width / (entries.size - 1) else width / 2
                    val points = entries.mapIndexed { index, entry ->
                        val x = if (entries.size == 1) width / 2 else index * stepX
                        val y = height - (((entry.weightKg - minWeight) / weightRange) * height).coerceIn(0f, height)
                        Offset(x, y)
                    }

                    // Draw Gradient Fill Path below curve
                    val fillPath = Path().apply {
                        if (points.isNotEmpty()) {
                            moveTo(points.first().x, height)
                            points.forEach { point ->
                                lineTo(point.x, point.y)
                            }
                            lineTo(points.last().x, height)
                            close()
                        }
                    }

                    drawPath(
                        path = fillPath,
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                FlameOrange.copy(alpha = 0.35f),
                                Color.Transparent
                            ),
                            startY = 0f,
                            endY = height
                        )
                    )

                    // Draw Connected Stroke Line
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
                        color = FlameOrange,
                        style = Stroke(width = 3.5.dp.toPx())
                    )

                    // Draw Dot markers
                    points.forEach { point ->
                        drawCircle(
                            color = FlameOrange,
                            radius = 5.dp.toPx(),
                            center = point
                        )
                        drawCircle(
                            color = Color.White,
                            radius = 2.5.dp.toPx(),
                            center = point
                        )
                    }
                }

                // Date range row
                val sdf = SimpleDateFormat("d MMM", Locale("ar"))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = sdf.format(Date(entries.first().dateTimestamp)),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "آخر قياس: ${sdf.format(Date(entries.last().dateTimestamp))}",
                        style = MaterialTheme.typography.labelSmall,
                        color = FlameOrange,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
