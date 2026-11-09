package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.ProgramRoutineDayEntity
import com.example.data.database.WorkoutProgramEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkoutProgramsView(
    programs: List<WorkoutProgramEntity>,
    activeProgram: WorkoutProgramEntity?,
    onSelectProgram: (programId: String) -> Unit,
    onCreateCustomProgramClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(0) } // 0: Pre-made, 1: Custom

    val filteredPrograms = remember(programs, selectedTab) {
        if (selectedTab == 0) {
            programs.filter { !it.isCustom }
        } else {
            programs.filter { it.isCustom }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Active Program Hero Card
        if (activeProgram != null) {
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
                    .clickable { onSelectProgram(activeProgram.id) }
                    .testTag("active_program_hero_card")
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                            )
                        )
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                color = Color(0xFF00E676).copy(alpha = 0.2f),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .background(Color(0xFF00E676), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        "برنامجك النشط الحالي",
                                        color = Color(0xFF00E676),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Text(activeProgram.iconBadge, fontSize = 24.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            activeProgram.titleArabic,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp,
                            color = Color.White
                        )
                        Text(
                            activeProgram.descriptionArabic,
                            fontSize = 12.sp,
                            color = Color.LightGray,
                            maxLines = 2
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                "🗓️ ${activeProgram.durationWeeks} أسابيع | ⚡ ${activeProgram.daysPerWeek} أيام أسبوعياً",
                                fontSize = 12.sp,
                                color = Color(0xFF00E5FF)
                            )

                            FilledTonalButton(
                                onClick = { onSelectProgram(activeProgram.id) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color(0xFF00E676),
                                    contentColor = Color.Black
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text("فتح الجدول", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tabs & Create Button Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TabRow(
                selectedTabIndex = selectedTab,
                modifier = Modifier.weight(1f).padding(end = 8.dp),
                containerColor = Color.Transparent,
                divider = {}
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("برامج جاهزة 🌟", fontSize = 13.sp, fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("برامجي المخصصة 🛠️", fontSize = 13.sp, fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal) }
                )
            }

            Button(
                onClick = onCreateCustomProgramClick,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                modifier = Modifier.testTag("open_custom_builder_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("إنشاء جدول", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Programs List
        if (filteredPrograms.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("🛠️", fontSize = 42.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("لم تقم بإنشاء برامج تدريبية مخصصة بعد", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    Text("اضغط على زر 'إنشاء جدول' لتصميم برنامجك من الصفر!", fontSize = 12.sp, color = Color.Gray)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(onClick = onCreateCustomProgramClick) {
                        Text("بدء تصميم برنامجك الآن")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredPrograms, key = { it.id }) { program ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectProgram(program.id) }
                            .testTag("program_card_${program.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (program.isCurrentActiveProgram)
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                            else
                                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(program.iconBadge, fontSize = 26.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            program.titleArabic,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 15.sp
                                        )
                                        if (program.titleEnglish.isNotBlank()) {
                                            Text(
                                                program.titleEnglish,
                                                fontSize = 11.sp,
                                                color = Color.Gray
                                            )
                                        }
                                    }
                                }

                                if (program.isCurrentActiveProgram) {
                                    Badge(containerColor = Color(0xFF00E676)) {
                                        Text("مفعّل", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                program.descriptionArabic,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("${program.durationWeeks} أسابيع", fontSize = 10.sp) }
                                    )
                                    SuggestionChip(
                                        onClick = {},
                                        label = { Text("${program.daysPerWeek} أيام", fontSize = 10.sp) }
                                    )
                                }

                                TextButton(
                                    onClick = { onSelectProgram(program.id) },
                                    contentPadding = PaddingValues(horizontal = 8.dp)
                                ) {
                                    Text("عرض التمارين", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    Icon(Icons.Default.ChevronLeft, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
