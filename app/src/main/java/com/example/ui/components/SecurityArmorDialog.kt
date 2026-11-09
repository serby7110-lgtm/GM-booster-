package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Pin
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.security.SecurityAuditReport
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun SecurityArmorDialog(
    auditReport: SecurityAuditReport,
    isPinEnabled: Boolean,
    isBiometricEnabled: Boolean,
    isScreenShieldEnabled: Boolean,
    currentPin: String,
    onTogglePin: (Boolean) -> Unit,
    onToggleBiometrics: (Boolean) -> Unit,
    onToggleScreenShield: (Boolean) -> Unit,
    onChangePin: (String) -> Unit,
    onRunAuditScan: () -> Unit,
    onDismiss: () -> Unit
) {
    var isChangingPin by remember { mutableStateOf(false) }
    var newPinInput by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("security_armor_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(FlameOrange.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Shield,
                                    contentDescription = null,
                                    tint = FlameOrange,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "درع الحماية والأمان العالي",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Gm Armor & Anti-Tamper Shield",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }

                // Security Score Banner
                item {
                    Surface(
                        shape = RoundedCornerShape(18.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.fillMaxWidth()
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
                                    text = "تقييم أمان التطبيق",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${auditReport.overallScore}/100 🛡️",
                                    style = MaterialTheme.typography.headlineSmall,
                                    fontWeight = FontWeight.Black,
                                    color = if (auditReport.overallScore >= 80) EmeraldNeon else FlameOrange
                                )
                                Text(
                                    text = auditReport.securityLevel,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = EmeraldNeon,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Button(
                                onClick = onRunAuditScan,
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = FlameOrange)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إعادة الفحص", fontSize = 12.sp)
                            }
                        }
                    }
                }

                // Toggles Section
                item {
                    Text(
                        text = "خيارات الحماية النشطة",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                // 1. PIN Lock Toggle
                item {
                    SecurityToggleRow(
                        title = "قفل الـ PIN برقم سري (4 أرقام)",
                        subtitle = "منع فتح التطبيق إلا برمز PIN خاص",
                        isChecked = isPinEnabled,
                        icon = Icons.Default.Pin,
                        onCheckedChange = { onTogglePin(it) }
                    )
                }

                // 2. Biometric Toggle
                item {
                    SecurityToggleRow(
                        title = "فتح القفل بالبصمة / Face Unlock",
                        subtitle = "تسجيل دخول سريع وآمن عبر المستشعر البيومتري",
                        isChecked = isBiometricEnabled,
                        icon = Icons.Default.Fingerprint,
                        onCheckedChange = { onToggleBiometrics(it) }
                    )
                }

                // 3. Screen Shield Toggle
                item {
                    SecurityToggleRow(
                        title = "درع خصوصية الشاشة (FLAG_SECURE)",
                        subtitle = "منع لقطات الشاشة وتسجيل الفيديو للبيانات",
                        isChecked = isScreenShieldEnabled,
                        icon = Icons.Default.Security,
                        onCheckedChange = { onToggleScreenShield(it) }
                    )
                }

                // Change PIN Button & Input
                if (isPinEnabled) {
                    item {
                        if (!isChangingPin) {
                            Button(
                                onClick = { isChangingPin = true },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Icon(Icons.Default.Lock, contentDescription = null, tint = FlameOrange, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("تغيير رمز الـ PIN الحالي", color = MaterialTheme.colorScheme.onSurface)
                            }
                        } else {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(14.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                                    .padding(12.dp)
                            ) {
                                Text("تعيين رمز PIN جديد (4 أرقام)", style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = newPinInput,
                                    onValueChange = { if (it.length <= 4) newPinInput = it },
                                    label = { Text("رمز الـ PIN الجديد") },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(10.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            if (newPinInput.length == 4) {
                                                onChangePin(newPinInput)
                                                isChangingPin = false
                                                newPinInput = ""
                                            }
                                        },
                                        enabled = newPinInput.length == 4,
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldNeon)
                                    ) {
                                        Text("حفظ الرمز", color = Color.Black, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = { isChangingPin = false },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Text("إلغاء", color = MaterialTheme.colorScheme.onSurface)
                                    }
                                }
                            }
                        }
                    }
                }

                // Security Checks List
                item {
                    Text(
                        text = "سجل التحقق الأمني وفحص الثغرات",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                items(auditReport.checkSummary) { check ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (check.passed) Icons.Default.CheckCircle else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (check.passed) EmeraldNeon else Color(0xFFEF4444),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = check.titleArabic,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = check.descriptionArabic,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(10.dp))
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                    ) {
                        Text("تم والعودة للتطبيق", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun SecurityToggleRow(
    title: String,
    subtitle: String,
    isChecked: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onCheckedChange: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (isChecked) EmeraldNeon.copy(alpha = 0.15f) else Color(0xFF64748B).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isChecked) EmeraldNeon else Color(0xFF64748B),
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = EmeraldNeon,
                    checkedTrackColor = EmeraldNeon.copy(alpha = 0.3f)
                )
            )
        }
    }
}
