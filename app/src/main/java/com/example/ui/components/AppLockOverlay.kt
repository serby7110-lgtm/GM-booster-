package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanEnergy
import com.example.ui.theme.EmeraldNeon
import com.example.ui.theme.FlameOrange
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun AppLockOverlay(
    isLocked: Boolean,
    onVerifyPin: (String) -> Boolean,
    onBiometricUnlock: () -> Unit,
    failedAttempts: Int,
    lockoutUntilTimestamp: Long,
    modifier: Modifier = Modifier
) {
    if (!isLocked) return

    var enteredPin by remember { mutableStateOf("") }
    var isError by remember { mutableStateOf(false) }
    val shakeOffset = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val now = System.currentTimeMillis()
    val isLockout = now < lockoutUntilTimestamp
    val remainingSeconds = if (isLockout) ((lockoutUntilTimestamp - now) / 1000).toInt() + 1 else 0

    val infiniteTransition = rememberInfiniteTransition(label = "shield_pulse")
    val shieldGlow by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow"
    )

    fun handleKeyPress(digit: String) {
        if (isLockout || enteredPin.length >= 4) return
        val newPin = enteredPin + digit
        enteredPin = newPin

        if (newPin.length == 4) {
            val success = onVerifyPin(newPin)
            if (!success) {
                isError = true
                scope.launch {
                    // Shake animation on error
                    repeat(3) {
                        shakeOffset.animateTo(20f, tween(50))
                        shakeOffset.animateTo(-20f, tween(50))
                    }
                    shakeOffset.animateTo(0f, tween(50))
                    delay(300)
                    enteredPin = ""
                    isError = false
                }
            } else {
                enteredPin = ""
            }
        }
    }

    fun handleBackspace() {
        if (enteredPin.isNotEmpty()) {
            enteredPin = enteredPin.dropLast(1)
            isError = false
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF020617)
                    )
                )
            )
            .testTag("app_lock_overlay"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .offset { IntOffset(shakeOffset.value.roundToInt(), 0) },
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Security Emblem
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .scale(shieldGlow)
                    .clip(CircleShape)
                    .background(FlameOrange.copy(alpha = 0.15f))
                    .border(2.dp, FlameOrange, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "Security Shield",
                    tint = FlameOrange,
                    modifier = Modifier.size(44.dp)
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "نظام الحماية والأمان المشفر",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = if (isLockout) {
                    "تم تعليق المحاولات لحماية بياناتك. انتظر $remainingSeconds ثانية"
                } else {
                    "أدخل رمز الـ PIN المكون من 4 أرقام لفتح تطبيق Gm"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (isLockout) Color(0xFFEF4444) else Color(0xFF94A3B8),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(28.dp))

            // 4 PIN Dots Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 4) {
                    val isFilled = i < enteredPin.length
                    val dotColor = when {
                        isError -> Color(0xFFEF4444)
                        isFilled -> CyanEnergy
                        else -> Color(0xFF334155)
                    }

                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(dotColor)
                            .border(
                                width = 1.5.dp,
                                color = if (isFilled) CyanEnergy else Color(0xFF475569),
                                shape = CircleShape
                            )
                    )
                }
            }

            if (isError) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "رمز PIN غير صحيح! المحاولة ($failedAttempts/5)",
                    style = MaterialTheme.typography.labelMedium,
                    color = Color(0xFFEF4444),
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(36.dp))

            // Keypad Layout (1-9, Biometric, 0, Backspace)
            val keypad = listOf(
                listOf("1", "2", "3"),
                listOf("4", "5", "6"),
                listOf("7", "8", "9"),
                listOf("BIOMETRIC", "0", "BACKSPACE")
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                keypad.forEach { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        row.forEach { key ->
                            when (key) {
                                "BIOMETRIC" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(EmeraldNeon.copy(alpha = 0.15f))
                                            .clickable(enabled = !isLockout) { onBiometricUnlock() }
                                            .testTag("biometric_unlock_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Fingerprint,
                                            contentDescription = "Biometric Unlock",
                                            tint = EmeraldNeon,
                                            modifier = Modifier.size(32.dp)
                                        )
                                    }
                                }
                                "BACKSPACE" -> {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B))
                                            .clickable(enabled = !isLockout) { handleBackspace() }
                                            .testTag("pin_backspace_btn"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Backspace,
                                            contentDescription = "Backspace",
                                            tint = Color.White,
                                            modifier = Modifier.size(24.dp)
                                        )
                                    }
                                }
                                else -> {
                                    Box(
                                        modifier = Modifier
                                            .size(72.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF1E293B))
                                            .border(1.dp, Color(0xFF334155), CircleShape)
                                            .clickable(enabled = !isLockout) { handleKeyPress(key) }
                                            .testTag("pin_key_$key"),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = key,
                                            style = MaterialTheme.typography.headlineMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color(0xFF64748B),
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "مشفر ومحمي بمعايير AES-256 ودرع الحماية التلقائي",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }
    }
}
