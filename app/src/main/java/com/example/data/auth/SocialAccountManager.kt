package com.example.data.auth

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class SocialAuthProvider(
    val titleArabic: String,
    val titleEnglish: String,
    val iconEmoji: String,
    val brandColorHex: Long
) {
    GOOGLE("حساب Google", "Google Account", "🇬", 0xFF4285F4),
    FACEBOOK("حساب Facebook", "Facebook Account", "🇫", 0xFF1877F2),
    INSTAGRAM("حساب Instagram", "Instagram Account", "📸", 0xFFE1306C)
}

data class SocialAccountState(
    val isGoogleLinked: Boolean = false,
    val googleEmail: String = "serby7110@gmail.com",
    val googleDisplayName: String = "بطل Gm الرياضي",
    val googlePhotoUrl: String = "",

    val isFacebookLinked: Boolean = false,
    val facebookName: String = "",
    val facebookId: String = "",

    val isInstagramLinked: Boolean = false,
    val instagramHandle: String = "",

    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

class SocialAccountManager(private val context: Context) {

    private val _accountState = MutableStateFlow(
        SocialAccountState(
            isGoogleLinked = true,
            googleEmail = "serby7110@gmail.com",
            googleDisplayName = "سعود الحربي (Gm Champion)",
            isFacebookLinked = false,
            isInstagramLinked = false
        )
    )
    val accountState: StateFlow<SocialAccountState> = _accountState.asStateFlow()

    fun linkGoogle(email: String = "serby7110@gmail.com", name: String = "بطل Gm الرياضي") {
        _accountState.value = _accountState.value.copy(
            isGoogleLinked = true,
            googleEmail = email.ifEmpty { "serby7110@gmail.com" },
            googleDisplayName = name,
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    fun unlinkGoogle() {
        _accountState.value = _accountState.value.copy(
            isGoogleLinked = false
        )
    }

    fun linkFacebook(facebookName: String) {
        _accountState.value = _accountState.value.copy(
            isFacebookLinked = true,
            facebookName = facebookName.ifEmpty { "Gm Athlete" },
            facebookId = "fb_${System.currentTimeMillis() % 1000000}",
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    fun unlinkFacebook() {
        _accountState.value = _accountState.value.copy(
            isFacebookLinked = false,
            facebookName = "",
            facebookId = ""
        )
    }

    fun linkInstagram(handle: String) {
        val cleanHandle = if (handle.startsWith("@")) handle else "@$handle"
        _accountState.value = _accountState.value.copy(
            isInstagramLinked = true,
            instagramHandle = cleanHandle.ifEmpty { "@gm_champion" },
            lastSyncTimestamp = System.currentTimeMillis()
        )
    }

    fun unlinkInstagram() {
        _accountState.value = _accountState.value.copy(
            isInstagramLinked = false,
            instagramHandle = ""
        )
    }
}
