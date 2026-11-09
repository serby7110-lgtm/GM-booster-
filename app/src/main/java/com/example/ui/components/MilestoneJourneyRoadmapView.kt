package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FitnessMilestone
import com.example.data.MilestoneCategory
import com.example.data.MilestoneEngine
import com.example.data.MilestoneTier
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MilestoneJourneyRoadmapView(
    milestones: List<FitnessMilestone>,
    onClaimMilestone: (FitnessMilestone) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedCategory by remember { mutableStateOf(MilestoneCategory.ALL) }
    var selectedMilestoneForSheet by remember { mutableStateOf<FitnessMilestone?>(null) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val filteredMilestones = remember(milestones, selectedCategory) {
        if (selectedCategory == MilestoneCategory.ALL) milestones
        else milestones.filter { it.category == selectedCategory }
    }

    val categoryProgress = remember(milestones, selectedCategory) {
        MilestoneEngine.calculateCategoryProgress(milestones, selectedCategory)
    }

    // Modal Bottom Sheet for detailed milestone inspection
    if (selectedMilestoneForSheet != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedMilestoneForSheet = null },
            sheetState = sheetState,
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            MilestoneDetailContent(
                milestone = selectedMilestoneForSheet!!,
                onClaim = {
                    onClaimMilestone(selectedMilestoneForSheet!!)
                    selectedMilestoneForSheet = null
                },
                onDismiss = { selectedMilestoneForSheet = null }
            )
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("milestone_journey_roadmap_view"),
        contentPadding = PaddingValues(top = 8.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Category Selector Chips
        item {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("milestone_category_row"),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(MilestoneCategory.values()) { category ->
                    val isSelected = selectedCategory == category
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = "${category.iconEmoji} ${category.titleArabic}",
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = FlameOrange,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.testTag("category_chip_${category.name.lowercase()}")
                    )
                }
            }
        }

        // Hero Roadmap Progress Summary Card
        item {
            MilestoneRoadmapHeroCard(
                category = selectedCategory,
                progress = categoryProgress
            )
        }

        // Roadmap Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "محطات مسار الإنجاز (${filteredMilestones.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "اضغط على أي محطة للاطلاع على نصائح المدرب وتفاصيل التقدم",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Timeline Nodes
        items(filteredMilestones) { milestone ->
            MilestoneTimelineNode(
                milestone = milestone,
                onClick = { selectedMilestoneForSheet = milestone },
                onClaim = { onClaimMilestone(milestone) }
            )
        }
    }
}

@Composable
fun MilestoneRoadmapHeroCard(
    category: MilestoneCategory,
    progress: com.example.data.MilestoneCategoryProgress
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_hero_progress_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            FlameOrange.copy(alpha = 0.18f),
                            AmberGold.copy(alpha = 0.12f),
                            CyanEnergy.copy(alpha = 0.08f)
                        )
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(category.iconEmoji, fontSize = 34.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = category.titleArabic,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = category.subtitleArabic,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "${progress.completionPercent}%",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Black,
                            color = AmberGold
                        )
                        Text(
                            text = "نسبة الإنجاز",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                LinearProgressIndicator(
                    progress = { (progress.completionPercent / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp)),
                    color = FlameOrange,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Stats Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    MilestoneMetricPill(
                        label = "المحطات",
                        value = "${progress.unlockedCount} / ${progress.totalCount}",
                        icon = "🎯",
                        accentColor = CyanEnergy
                    )
                    MilestoneMetricPill(
                        label = "المستلمة",
                        value = "${progress.claimedCount}",
                        icon = "✅",
                        accentColor = EmeraldNeon
                    )
                    MilestoneMetricPill(
                        label = "نقاط XP",
                        value = "+${progress.totalXpEarned}",
                        icon = "💎",
                        accentColor = AmberGold
                    )
                }
            }
        }
    }
}

@Composable
fun MilestoneMetricPill(
    label: String,
    value: String,
    icon: String,
    accentColor: Color
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.7f),
        border = androidx.compose.foundation.BorderStroke(1.dp, accentColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(icon, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = value,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = accentColor
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun MilestoneTimelineNode(
    milestone: FitnessMilestone,
    onClick: () -> Unit,
    onClaim: () -> Unit
) {
    val tierColor = Color(milestone.tier.colorHex)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("milestone_node_${milestone.id}"),
        verticalAlignment = Alignment.Top
    ) {
        // Vertical Timeline Indicator Column
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(42.dp)
        ) {
            // Milestone Badge Circle
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(
                        when {
                            milestone.isClaimed -> EmeraldNeon.copy(alpha = 0.2f)
                            milestone.isUnlocked -> tierColor.copy(alpha = 0.25f)
                            else -> MaterialTheme.colorScheme.surfaceVariant
                        }
                    )
                    .border(
                        width = if (milestone.isUnlocked) 2.dp else 1.dp,
                        color = when {
                            milestone.isClaimed -> EmeraldNeon
                            milestone.isUnlocked -> tierColor
                            else -> MaterialTheme.colorScheme.outlineVariant
                        },
                        shape = CircleShape
                    )
                    .clickable { onClick() },
                contentAlignment = Alignment.Center
            ) {
                when {
                    milestone.isClaimed -> Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Claimed",
                        tint = EmeraldNeon,
                        modifier = Modifier.size(20.dp)
                    )
                    milestone.isUnlocked -> Text(milestone.iconEmoji, fontSize = 18.sp)
                    else -> Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "Locked",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Connecting vertical dashed-like bar
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(80.dp)
                    .background(
                        if (milestone.isUnlocked) tierColor.copy(alpha = 0.5f)
                        else MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        // Milestone Content Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = when {
                    milestone.isClaimed -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    milestone.isUnlocked -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.85f)
                    else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                }
            ),
            border = if (milestone.isUnlocked && !milestone.isClaimed) {
                androidx.compose.foundation.BorderStroke(1.5.dp, tierColor.copy(alpha = 0.8f))
            } else null
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                // Tier & XP Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tierColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "${milestone.tier.titleArabic} • ${milestone.category.titleArabic}",
                            style = MaterialTheme.typography.labelSmall,
                            color = tierColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = AmberGold.copy(alpha = 0.15f)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("💎", fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "+${milestone.xpReward} XP",
                                style = MaterialTheme.typography.labelSmall,
                                color = AmberGold,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Title & Description
                Text(
                    text = milestone.titleArabic,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = milestone.descriptionArabic,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Progress Bar & Ratio
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "%.1f / %.1f %s".format(
                            milestone.currentMetric,
                            milestone.targetMetric,
                            milestone.unitArabic
                        ),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = if (milestone.isUnlocked) EmeraldNeon else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${milestone.progressPercent}%",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (milestone.isUnlocked) EmeraldNeon else FlameOrange
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                LinearProgressIndicator(
                    progress = { milestone.progressRatio },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = if (milestone.isUnlocked) EmeraldNeon else FlameOrange,
                    trackColor = MaterialTheme.colorScheme.surface
                )

                // Action Bar (Claim or Status)
                if (milestone.isUnlocked && !milestone.isClaimed) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onClaim,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .testTag("claim_button_${milestone.id}"),
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "Claim Reward",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "استلام الجائزة (+${milestone.xpReward} XP)",
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            fontSize = 12.sp
                        )
                    }
                } else if (milestone.isClaimed) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Claimed",
                            tint = EmeraldNeon,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "تم استلام الجائزة بنجاح",
                            style = MaterialTheme.typography.labelSmall,
                            color = EmeraldNeon,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MilestoneDetailContent(
    milestone: FitnessMilestone,
    onClaim: () -> Unit,
    onDismiss: () -> Unit
) {
    val tierColor = Color(milestone.tier.colorHex)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
            .testTag("milestone_detail_modal")
    ) {
        // Header Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = tierColor.copy(alpha = 0.15f)
            ) {
                Text(
                    text = "${milestone.tier.titleArabic} • ${milestone.category.titleArabic}",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tierColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }

            IconButton(onClick = onDismiss) {
                Icon(Icons.Default.Close, contentDescription = "Close")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center Icon & Title
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(tierColor.copy(alpha = 0.2f))
                    .border(2.dp, tierColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(milestone.iconEmoji, fontSize = 36.sp)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = milestone.titleArabic,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Text(
                text = milestone.titleEnglish,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description Card
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "وصف المحطة:",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = FlameOrange
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = milestone.descriptionArabic,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Coach Tips Card
        if (milestone.tipsArabic.isNotBlank()) {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = AmberGold.copy(alpha = 0.1f),
                border = androidx.compose.foundation.BorderStroke(1.dp, AmberGold.copy(alpha = 0.3f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Tips",
                        tint = AmberGold,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "نصيحة مدرب Gm لتحقيق المحطة:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = AmberGold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = milestone.tipsArabic,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Progress section
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "التقدم الحالي: %.1f %s".format(milestone.currentMetric, milestone.unitArabic),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "المطلوب: %.1f %s".format(milestone.targetMetric, milestone.unitArabic),
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = tierColor
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { milestone.progressRatio },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                color = if (milestone.isUnlocked) EmeraldNeon else FlameOrange,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Claim button or Close
        if (milestone.isUnlocked && !milestone.isClaimed) {
            Button(
                onClick = onClaim,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("modal_claim_button"),
                colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "استلام المكافأة الآن (+${milestone.xpReward} XP)",
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    fontSize = 14.sp
                )
            }
        } else {
            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = if (milestone.isClaimed) "تم الاستلام مسبقاً (إغلاق)" else "مواصلة التمرين لتحقيق المحطة",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
