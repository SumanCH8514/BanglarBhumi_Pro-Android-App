package com.banglarbhumi.pro.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import androidx.core.content.ContextCompat
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.util.regex.Pattern

object SmsOtpHelper {

    fun startSmsUserConsent(context: Context) {
        try {
            SmsRetriever.getClient(context).startSmsUserConsent(null)
        } catch (_: Exception) {}
    }

    fun createSmsConsentReceiver(
        onConsentIntentReceived: (Intent) -> Unit
    ): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == SmsRetriever.SMS_RETRIEVED_ACTION) {
                    val extras = intent.extras ?: return
                    val smsRetrieverStatus = extras.get(SmsRetriever.EXTRA_STATUS) as? Status ?: return
                    if (smsRetrieverStatus.statusCode == CommonStatusCodes.SUCCESS) {
                        val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                        } else {
                            @Suppress("DEPRECATION")
                            extras.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT)
                        }
                        consentIntent?.let { onConsentIntentReceived(it) }
                    }
                }
            }
        }
    }

    fun registerConsentReceiver(context: Context, receiver: BroadcastReceiver) {
        val filter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_EXPORTED
            )
        } else {
            ContextCompat.registerReceiver(
                context,
                receiver,
                filter,
                ContextCompat.RECEIVER_NOT_EXPORTED
            )
        }
    }

    fun unregisterConsentReceiver(context: Context, receiver: BroadcastReceiver) {
        try {
            context.unregisterReceiver(receiver)
        } catch (_: Exception) {}
    }

    fun extractOtpFromMessage(message: String?): String? {
        if (message.isNullOrBlank()) return null

        val pattern6 = Pattern.compile("(?<!\\d)(\\d{6})(?!\\d)")
        val matcher6 = pattern6.matcher(message)
        if (matcher6.find()) {
            return matcher6.group(1)
        }

        val pattern4 = Pattern.compile("(?<!\\d)(\\d{4})(?!\\d)")
        val matcher4 = pattern4.matcher(message)
        if (matcher4.find()) {
            return matcher4.group(1)
        }

        return null
    }
}
