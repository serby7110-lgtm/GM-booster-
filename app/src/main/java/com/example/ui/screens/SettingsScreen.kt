package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Watch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AppLanguage
import com.example.data.BodyType
import com.example.data.FitnessGoal
import com.example.ui.GmMainViewModel
import com.example.ui.components.SecurityArmorDialog
import com.example.ui.components.SocialAccountLinkingDialog
import com.example.ui.theme.AmberGold
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun SettingsScreen(
    viewModel: GmMainViewModel,
    modifier: Modifier = Modifier
) {
    val currentLang by viewModel.currentLanguage.collectAsState()
    val profile by viewModel.userProfile.collectAsState()
    val isTrackerConnected by viewModel.isTrackerConnected.collectAsState()
    val socialState by viewModel.socialAccountState.collectAsState()
    val securityReport by viewModel.securityAuditReport.collectAsState()
    val locationInfo by viewModel.userLocationInfo.collectAsState()

    var showSocialModal by remember { mutableStateOf(false) }
    var showSecurityModal by remember { mutableStateOf(false) }

    var name by remember(profile) { mutableStateOf(profile?.name ?: "بطل Gm") }
    var gender by remember(profile) { mutableStateOf(profile?.gender ?: "ذكر") }
    var ageStr by remember(profile) { mutableStateOf((profile?.age ?: 25).toString()) }
    var heightStr by remember(profile) { mutableStateOf((profile?.heightCm ?: 178f).toString()) }
    var targetWeightStr by remember(profile) { mutableStateOf((profile?.targetWeightKg ?: 78f).toString()) }
    var selectedBodyType by remember(profile) {
        mutableStateOf(viewModel.getBodyTypeEnum())
    }
    var selectedGoal by remember(profile) {
        val goalStr = profile?.fitnessGoal ?: "BUILD_MUSCLE"
        mutableStateOf(
            try { FitnessGoal.valueOf(goalStr) } catch (e: Exception) { FitnessGoal.BUILD_MUSCLE }
        )
    }

    var workoutReminders by remember(profile) { mutableStateOf(profile?.workoutRemindersEnabled ?: true) }
    var waterReminders by remember(profile) { mutableStateOf(profile?.waterRemindersEnabled ?: true) }

    if (showSocialModal) {
        SocialAccountLinkingDialog(
            socialState = socialState,
            onLinkGoogle = { email -> viewModel.linkGoogleAccount(email) },
            onUnlinkGoogle = { viewModel.unlinkGoogleAccount() },
            onLinkFacebook = { name -> viewModel.linkFacebookAccount(name) },
            onUnlinkFacebook = { viewModel.unlinkFacebookAccount() },
            onLinkInstagram = { handle -> viewModel.linkInstagramAccount(handle) },
            onUnlinkInstagram = { viewModel.unlinkInstagramAccount() },
            onDismiss = { showSocialModal = false }
        )
    }

    if (showSecurityModal) {
        SecurityArmorDialog(
            auditReport = securityReport,
            isPinEnabled = profile?.isAppLockEnabled ?: false,
            isBiometricEnabled = profile?.isBiometricEnabled ?: false,
            isScreenShieldEnabled = profile?.isScreenShieldEnabled ?: false,
            currentPin = profile?.pinCodeHash ?: "1234",
            onTogglePin = { viewModel.togglePinLock(it) },
            onToggleBiometrics = { viewModel.toggleBiometricLock(it) },
            onToggleScreenShield = { viewModel.toggleScreenShield(it) },
            onChangePin = { viewModel.changePinCode(it) },
            onRunAuditScan = { viewModel.runSecurityScan() },
            onDismiss = { showSecurityModal = false }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title Header
        item {
            Column {
                Text(
                    text = viewModel.getString("settings_title"),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = viewModel.getString("settings_subtitle"),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Language Selector Card (Arabic, English, Indonesian)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_selector_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.75f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                tint = CyanEnergy,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = viewModel.getString("language_selection"),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(CyanEnergy.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = currentLang.displayName,
                                style = MaterialTheme.typography.labelSmall,
                                color = CyanEnergy,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AppLanguage.values().forEach { lang ->
                            val isSelected = currentLang == lang
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { viewModel.setLanguage(lang) }
                                    .testTag("lang_button_${lang.code}"),
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) FlameOrange else MaterialTheme.colorScheme.surface,
                                tonalElevation = if (isSelected) 4.dp else 1.dp
                            ) {
                                Column(
                                    modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = lang.flagEmoji,
                                        fontSize = 20.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = lang.displayName,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Body Type Selection Section (اكتومورف، ميزومورف، إندومورف)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.getString("body_type_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = viewModel.getString("body_type_desc"),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BodyType.values().forEach { type ->
                        val isSelected = selectedBodyType == type
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                                .clickable { selectedBodyType = type },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) FlameOrange.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ),
                            border = if (isSelected) androidx.compose.foundation.BorderStroke(1.5.dp, FlameOrange) else null
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = type.getLocalizedName(currentLang),
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) FlameOrange else MaterialTheme.colorScheme.onSurface
                                    )

                                    if (isSelected) {
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(6.dp))
                                                .background(FlameOrange)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(viewModel.getString("selected"), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = type.getLocalizedDesc(currentLang),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Personal Metrics Form
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.getString("personal_metrics_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(viewModel.getString("name_label")) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = heightStr,
                            onValueChange = { heightStr = it },
                            label = { Text(viewModel.getString("height_cm")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = targetWeightStr,
                            onValueChange = { targetWeightStr = it },
                            label = { Text(viewModel.getString("target_weight_kg")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = ageStr,
                            onValueChange = { ageStr = it },
                            label = { Text(viewModel.getString("age_years")) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = gender,
                            onValueChange = { gender = it },
                            label = { Text(viewModel.getString("gender")) },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(viewModel.getString("primary_goal"), style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(6.dp))
                    FitnessGoal.values().forEach { goal ->
                        val isSel = selectedGoal == goal
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) CyanEnergy.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface)
                                .clickable { selectedGoal = goal }
                                .padding(10.dp)
                        ) {
                            Text(
                                text = goal.arabicTitle,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSel) CyanEnergy else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }

        // Notifications & Reminders (Workout & Water)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.getString("notifications_title"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(viewModel.getString("workout_reminder"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(viewModel.getString("workout_reminder_sub"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = workoutReminders,
                            onCheckedChange = { workoutReminders = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = FlameOrange, checkedTrackColor = FlameOrange.copy(alpha = 0.4f))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(viewModel.getString("water_reminder"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                            Text(viewModel.getString("water_reminder_sub"), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(
                            checked = waterReminders,
                            onCheckedChange = { waterReminders = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = CyanEnergy, checkedTrackColor = CyanEnergy.copy(alpha = 0.4f))
                        )
                    }
                }
            }
        }

        // Security & Anti-Tamper Shield Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldNeon)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("درع الأمان ومكافحة الاختراق (Armor Shield)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("فحص النزاهة، قفل PIN، البصمة، ومنع التصوير", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("درجة تقييم الحماية", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${securityReport.overallScore}/100 - ${securityReport.securityLevel}", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = if (securityReport.overallScore >= 70) EmeraldNeon else AmberGold)
                        }

                        Button(
                            onClick = { showSecurityModal = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon.copy(alpha = 0.15f))
                        ) {
                            Icon(Icons.Default.Shield, contentDescription = null, tint = EmeraldNeon, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("إدارة الأمان", color = EmeraldNeon, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Social Account Linking & Location Diagnostics Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                            Icon(Icons.Default.Share, contentDescription = null, tint = CyanEnergy)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text("الحسابات المتصلة ودعم المواقع", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                Text("Google, Facebook, Instagram و GPS", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Location snippet
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = AmberGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (locationInfo.hasPermission) locationInfo.locationName else "إذن الموقع غير مفعّل",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Button(
                            onClick = { showSocialModal = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanEnergy.copy(alpha = 0.15f))
                        ) {
                            Text("ربط الحسابات", color = CyanEnergy, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Fitness Tracker Sync & Cloud Backup
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = viewModel.getString("trackers_and_sync"),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Watch, contentDescription = null, tint = if (isTrackerConnected) EmeraldNeon else MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(viewModel.getString("fitness_tracker_compat"), style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
                                Text(if (isTrackerConnected) viewModel.getString("tracker_connected") else viewModel.getString("tracker_disconnected"), style = MaterialTheme.typography.labelSmall, color = if (isTrackerConnected) EmeraldNeon else MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }

                        Button(
                            onClick = { viewModel.toggleTrackerConnection() },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                        ) {
                            Text(if (isTrackerConnected) viewModel.getString("disconnect") else viewModel.getString("connect"), color = MaterialTheme.colorScheme.onSurface, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }
        }

        // Save Button
        item {
            Button(
                onClick = {
                    viewModel.updateProfileInfo(
                        name = name,
                        gender = gender,
                        age = ageStr.toIntOrNull() ?: 25,
                        height = heightStr.toFloatOrNull() ?: 178f,
                        targetWeight = targetWeightStr.toFloatOrNull() ?: 78f,
                        bodyType = selectedBodyType,
                        fitnessGoal = selectedGoal,
                        workoutReminders = workoutReminders,
                        waterReminders = waterReminders,
                        reminderHour = 18,
                        reminderMinute = 30
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("save_profile_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
            ) {
                Icon(Icons.Default.Save, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(viewModel.getString("save_changes"), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
