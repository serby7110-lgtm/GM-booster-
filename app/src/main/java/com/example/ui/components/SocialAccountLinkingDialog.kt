package com.example.ui.components

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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.example.data.auth.SocialAccountState
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange

@Composable
fun SocialAccountLinkingDialog(
    socialState: SocialAccountState,
    onLinkGoogle: (email: String) -> Unit,
    onUnlinkGoogle: () -> Unit,
    onLinkFacebook: (name: String) -> Unit,
    onUnlinkFacebook: () -> Unit,
    onLinkInstagram: (handle: String) -> Unit,
    onUnlinkInstagram: () -> Unit,
    onDismiss: () -> Unit
) {
    var googleEmailInput by remember { mutableStateOf(socialState.googleEmail) }
    var fbNameInput by remember { mutableStateOf(socialState.facebookName) }
    var igHandleInput by remember { mutableStateOf(socialState.instagramHandle) }

    var isEditingGoogle by remember { mutableStateOf(false) }
    var isEditingFb by remember { mutableStateOf(false) }
    var isEditingIg by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("social_linking_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
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
                                .background(CyanEnergy.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = CyanEnergy,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ربط الحسابات الشخصية",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Google • Facebook • Instagram",
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

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(16.dp))

                // 1. Google Account Card
                SocialAccountItemRow(
                    title = "حساب Google",
                    subtitle = if (socialState.isGoogleLinked) socialState.googleEmail else "غير مرتبط",
                    isLinked = socialState.isGoogleLinked,
                    iconEmoji = "🌐",
                    brandColor = Color(0xFF4285F4),
                    onToggleLink = {
                        if (socialState.isGoogleLinked) {
                            onUnlinkGoogle()
                        } else {
                            isEditingGoogle = !isEditingGoogle
                        }
                    }
                )

                if (isEditingGoogle && !socialState.isGoogleLinked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = googleEmailInput,
                        onValueChange = { googleEmailInput = it },
                        label = { Text("بريد Google الإلكتروني") },
                        placeholder = { Text("example@gmail.com") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4285F4)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            onLinkGoogle(googleEmailInput)
                            isEditingGoogle = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
                    ) {
                        Text("تأكيد ربط حساب Google", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 2. Facebook Account Card
                SocialAccountItemRow(
                    title = "حساب Facebook",
                    subtitle = if (socialState.isFacebookLinked) socialState.facebookName else "غير مرتبط",
                    isLinked = socialState.isFacebookLinked,
                    iconEmoji = "📘",
                    brandColor = Color(0xFF1877F2),
                    onToggleLink = {
                        if (socialState.isFacebookLinked) {
                            onUnlinkFacebook()
                        } else {
                            isEditingFb = !isEditingFb
                        }
                    }
                )

                if (isEditingFb && !socialState.isFacebookLinked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = fbNameInput,
                        onValueChange = { fbNameInput = it },
                        label = { Text("اسم المستخدم أو الرابط في Facebook") },
                        placeholder = { Text("مثال: facebook.com/athlete") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1877F2)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            onLinkFacebook(fbNameInput.ifEmpty { "بطل Gm الرياضي" })
                            isEditingFb = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1877F2))
                    ) {
                        Text("تأكيد ربط حساب Facebook", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. Instagram Account Card
                SocialAccountItemRow(
                    title = "حساب Instagram",
                    subtitle = if (socialState.isInstagramLinked) socialState.instagramHandle else "غير مرتبط",
                    isLinked = socialState.isInstagramLinked,
                    iconEmoji = "📸",
                    brandColor = Color(0xFFE1306C),
                    onToggleLink = {
                        if (socialState.isInstagramLinked) {
                            onUnlinkInstagram()
                        } else {
                            isEditingIg = !isEditingIg
                        }
                    }
                )

                if (isEditingIg && !socialState.isInstagramLinked) {
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = igHandleInput,
                        onValueChange = { igHandleInput = it },
                        label = { Text("حساب إنستغرام (Instagram Handle)") },
                        placeholder = { Text("@your_handle") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFE1306C)
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Button(
                        onClick = {
                            onLinkInstagram(igHandleInput.ifEmpty { "@gm_athlete" })
                            isEditingIg = false
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C))
                    ) {
                        Text("تأكيد ربط حساب Instagram", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                ) {
                    Text("تم وحفظ الإعدادات", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun SocialAccountItemRow(
    title: String,
    subtitle: String,
    isLinked: Boolean,
    iconEmoji: String,
    brandColor: Color,
    onToggleLink: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
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
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(brandColor.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = iconEmoji, fontSize = 18.sp)
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        if (isLinked) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Linked",
                                tint = EmeraldNeon,
                                modifier = Modifier.size(14.dp)
                            )
                        }
                    }
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isLinked) brandColor else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }
            }

            Surface(
                onClick = onToggleLink,
                shape = RoundedCornerShape(10.dp),
                color = if (isLinked) Color(0xFFEF4444).copy(alpha = 0.15f) else brandColor.copy(alpha = 0.15f)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (isLinked) Icons.Default.LinkOff else Icons.Default.Link,
                        contentDescription = null,
                        tint = if (isLinked) Color(0xFFEF4444) else brandColor,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLinked) "إلغاء الربط" else "ربط الآن",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (isLinked) Color(0xFFEF4444) else brandColor,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}
