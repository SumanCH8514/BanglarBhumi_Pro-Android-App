package com.banglarbhumi.pro.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import android.app.Activity
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.banglarbhumi.pro.R
import com.banglarbhumi.pro.ui.theme.*
import com.banglarbhumi.pro.ui.viewmodel.MainViewModel
import com.banglarbhumi.pro.ui.viewmodel.PortalLoginStep
import com.banglarbhumi.pro.util.SmsOtpHelper

@Composable
fun PortalLoginDialog(
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    val isBengali by viewModel.isBengali.collectAsState()
    val step by viewModel.portalLoginStep.collectAsState()
    val captchaBitmap by viewModel.captchaBitmap.collectAsState()
    val isCaptchaLoading by viewModel.isCaptchaLoading.collectAsState()
    val isSubmitting by viewModel.isLoginSubmitting.collectAsState()
    val errorMessage by viewModel.portalLoginError.collectAsState()
    val statusMessage by viewModel.portalLoginStatusMessage.collectAsState()
    val cachedUsername by viewModel.cachedUsername.collectAsState()
    val cachedPassword by viewModel.cachedPassword.collectAsState()

    var username by remember(cachedUsername) { mutableStateOf(cachedUsername) }
    var password by remember(cachedPassword) { mutableStateOf(cachedPassword) }
    var passwordVisible by remember { mutableStateOf(false) }
    var captcha by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }

    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    var isOtpAutoFilled by remember { mutableStateOf(false) }
    var clipboardOtp by remember { mutableStateOf<String?>(null) }

    val consentLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            val extracted = SmsOtpHelper.extractOtpFromMessage(message)
            if (!extracted.isNullOrBlank()) {
                otp = extracted
                isOtpAutoFilled = true
            }
        }
    }

    DisposableEffect(step) {
        if (step == PortalLoginStep.OTP_VERIFICATION) {
            isOtpAutoFilled = false
            SmsOtpHelper.startSmsUserConsent(context)
            val receiver = SmsOtpHelper.createSmsConsentReceiver { consentIntent ->
                try {
                    consentLauncher.launch(consentIntent)
                } catch (_: Exception) {}
            }
            SmsOtpHelper.registerConsentReceiver(context, receiver)

            // Check clipboard on entering OTP step
            val clipText = clipboardManager.getText()?.text
            val extractedClip = SmsOtpHelper.extractOtpFromMessage(clipText)
            if (!extractedClip.isNullOrBlank()) {
                clipboardOtp = extractedClip
            }

            onDispose {
                SmsOtpHelper.unregisterConsentReceiver(context, receiver)
            }
        } else {
            onDispose { }
        }
    }

    LaunchedEffect(isOtpAutoFilled, otp) {
        if (isOtpAutoFilled && otp.length == 6 && !isSubmitting) {
            viewModel.verifyLoginOtp(otp)
        }
    }

    LaunchedEffect(cachedUsername, cachedPassword) {
        if (username.isBlank() && cachedUsername.isNotBlank()) {
            username = cachedUsername
        }
        if (password.isBlank() && cachedPassword.isNotBlank()) {
            password = cachedPassword
        }
    }

    DisposableEffect(username, password) {
        onDispose {
            if (username.isNotBlank()) {
                viewModel.saveCachedCredentials(username, password)
            }
        }
    }

    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .wrapContentHeight()
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(24.dp),
            color = Color.White,
            tonalElevation = 6.dp,
            shadowElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .verticalScroll(rememberScrollState())
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White,
                            shadowElevation = 2.dp,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Image(
                                    painter = painterResource(id = R.drawable.app_logo),
                                    contentDescription = "Logo",
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                )
                            }
                        }

                        Column {
                            Text(
                                text = if (isBengali) "বাংলারভূমি সিটিজেন লগইন" else "BanglarBhumi Portal Login",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate900
                            )
                            Text(
                                text = if (step == PortalLoginStep.CREDENTIALS_AND_CAPTCHA)
                                    (if (isBengali) "ধাপ ১: ইউজার আইডি ও ক্যাপচা" else "Step 1: Credentials & Captcha")
                                else
                                    (if (isBengali) "ধাপ ২: ওটিপি যাচাইকরণ" else "Step 2: OTP Verification"),
                                fontSize = 11.sp,
                                color = Emerald700,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(onClick = onDismissRequest) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate500)
                    }
                }

                Surface(
                    color = Emerald50.copy(alpha = 0.6f),
                    shape = RoundedCornerShape(10.dp),
                    border = ButtonDefaults.outlinedButtonBorder
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = Emerald700,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = if (isBengali)
                                "লাইভ জমির রেকর্ডের জন্য সিটিজেন লগইন আবশ্যক।"
                            else
                                "Citizen login is required to fetch live land records.",
                            fontSize = 11.5.sp,
                            color = Slate700,
                            lineHeight = 15.sp
                        )
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = Color(0xFFFEE2E2),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color(0xFFB91C1C),
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = errorMessage.orEmpty(),
                                color = Color(0xFF991B1B),
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                AnimatedVisibility(
                    visible = statusMessage != null,
                    enter = fadeIn(animationSpec = tween(350)) + expandVertically(animationSpec = tween(350)),
                    exit = fadeOut(animationSpec = tween(250)) + shrinkVertically(animationSpec = tween(250))
                ) {
                    Surface(
                        color = Color(0xFFDCFCE7),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, Color(0xFFBBF7D0))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = Emerald700,
                                modifier = Modifier.size(17.dp)
                            )
                            Text(
                                text = if (isBengali) "ওটিপি সফলভাবে পাঠানো হয়েছে।" else "OTP sent successfully.",
                                color = Emerald900,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }

                if (step == PortalLoginStep.CREDENTIALS_AND_CAPTCHA) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            viewModel.saveCachedCredentials(it, password)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isBengali) "সিটিজেন ইউজার আইডি / মোবাইল" else "User ID / Mobile / Email") },
                        placeholder = { Text("e.g. 9876543210") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900,
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = Slate300,
                            focusedLabelColor = Emerald800,
                            unfocusedLabelColor = Slate600,
                            cursorColor = Emerald800,
                            focusedPlaceholderColor = Slate400,
                            unfocusedPlaceholderColor = Slate400,
                            focusedLeadingIconColor = Emerald700,
                            unfocusedLeadingIconColor = Slate500
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            viewModel.saveCachedCredentials(username, it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(if (isBengali) "পাসওয়ার্ড" else "Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = null
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Slate900,
                            unfocusedTextColor = Slate900,
                            focusedBorderColor = Emerald700,
                            unfocusedBorderColor = Slate300,
                            focusedLabelColor = Emerald800,
                            unfocusedLabelColor = Slate600,
                            cursorColor = Emerald800,
                            focusedLeadingIconColor = Emerald700,
                            unfocusedLeadingIconColor = Slate500,
                            focusedTrailingIconColor = Emerald700,
                            unfocusedTrailingIconColor = Slate500
                        )
                    )

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = if (isBengali) "ক্যাপচা যাচাইকরণ" else "Captcha Verification",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Slate700
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(52.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                if (isCaptchaLoading) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(22.dp),
                                            color = Emerald700,
                                            strokeWidth = 2.dp
                                        )
                                        Text(
                                            text = if (isBengali) "ক্যাপচা লোড হচ্ছে..." else "Loading captcha...",
                                            fontSize = 12.sp,
                                            color = Slate500
                                        )
                                    }
                                } else if (captchaBitmap != null) {
                                    Image(
                                        bitmap = captchaBitmap!!.asImageBitmap(),
                                        contentDescription = "Captcha Image",
                                        modifier = Modifier
                                            .height(50.dp)
                                            .clip(RoundedCornerShape(4.dp)),
                                        contentScale = ContentScale.FillHeight
                                    )
                                } else {
                                    Text(
                                        text = if (isBengali) "ক্যাপচা পাওয়া যায়নি" else "Captcha unavailable",
                                        fontSize = 12.sp,
                                        color = Slate500
                                    )
                                }
                            }

                            FilledTonalIconButton(
                                onClick = { viewModel.refreshCaptcha() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.size(44.dp),
                                colors = IconButtonDefaults.filledTonalIconButtonColors(
                                    containerColor = Emerald50,
                                    contentColor = Emerald800
                                )
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = "Refresh Captcha")
                            }
                        }

                        OutlinedTextField(
                            value = captcha,
                            onValueChange = { captcha = it.uppercase() },
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text(if (isBengali) "ক্যাপচা কোড লিখুন" else "Enter Captcha Code") },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate900,
                                unfocusedTextColor = Slate900,
                                focusedBorderColor = Emerald700,
                                unfocusedBorderColor = Slate300,
                                focusedLabelColor = Emerald800,
                                unfocusedLabelColor = Slate600,
                                cursorColor = Emerald800,
                                focusedPlaceholderColor = Slate400,
                                unfocusedPlaceholderColor = Slate400
                            )
                        )
                    }

                    Button(
                        onClick = {
                            if (username.isBlank()) {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "ইউজার আইডি বা মোবাইল নম্বর লিখুন" else "Please enter User ID or Mobile",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            if (password.isBlank()) {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "পাসওয়ার্ড লিখুন" else "Please enter Password",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            if (captcha.isBlank()) {
                                Toast.makeText(
                                    context,
                                    if (isBengali) "অনুগ্রহ করে ক্যাপচা কোডটি লিখুন" else "Please enter the Captcha code",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@Button
                            }
                            viewModel.requestLoginOtp(username, password, captcha)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Emerald700,
                            contentColor = Color.White,
                            disabledContainerColor = Emerald700.copy(alpha = 0.5f),
                            disabledContentColor = Color.White.copy(alpha = 0.8f)
                        ),
                        elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
                        enabled = !isSubmitting
                    ) {
                        if (isSubmitting) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "ওটিপি পাঠানো হচ্ছে..." else "Sending OTP...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isBengali) "ওটিপি পাঠান (Send OTP)" else "Generate & Send OTP",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.5.sp
                            )
                        }
                    }
                }

                if (step == PortalLoginStep.OTP_VERIFICATION) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = if (isBengali)
                                "আপনার নিবন্ধিত ইমেলে প্রেরিত ৬ সংখ্যার ওটিপি কোডটি লিখুন:"
                            else
                                "Enter the 6-digit OTP sent to your Mobile Number.",
                            fontSize = 11.5.sp,
                            color = Slate700,
                            textAlign = TextAlign.Center,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )

                        Surface(
                            color = Emerald50,
                            shape = RoundedCornerShape(20.dp),
                            border = BorderStroke(1.dp, Emerald100)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (otp.isBlank()) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(13.dp),
                                        color = Emerald700,
                                        strokeWidth = 2.dp
                                    )
                                    Text(
                                        text = if (isBengali) "স্বয়ংক্রিয়ভাবে ওটিপি অনুসন্ধান করা হচ্ছে..." else "Fetching OTP automatically...",
                                        fontSize = 12.sp,
                                        color = Emerald900,
                                        fontWeight = FontWeight.Medium
                                    )
                                } else {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = Emerald700,
                                        modifier = Modifier.size(15.dp)
                                    )
                                    Text(
                                        text = if (isBengali) "ওটিপি স্বয়ংক্রিয়ভাবে পূরণ হয়েছে" else "OTP fetched & filled automatically",
                                        fontSize = 12.sp,
                                        color = Emerald900,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                        OutlinedTextField(
                            value = otp,
                            onValueChange = { if (it.length <= 6) otp = it },
                            modifier = Modifier.fillMaxWidth(0.85f),
                            label = { Text(if (isBengali) "ওটিপি কোড (OTP)" else "Enter OTP") },
                            placeholder = { Text("123456") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = Slate900,
                                unfocusedTextColor = Slate900,
                                focusedBorderColor = Emerald700,
                                unfocusedBorderColor = Slate300,
                                focusedLabelColor = Emerald800,
                                unfocusedLabelColor = Slate600,
                                cursorColor = Emerald800,
                                focusedPlaceholderColor = Slate400,
                                unfocusedPlaceholderColor = Slate400
                            )
                        )

                        Button(
                            onClick = {
                                if (otp.isBlank()) {
                                    Toast.makeText(
                                        context,
                                        if (isBengali) "ওটিপি কোডটি লিখুন" else "Please enter the OTP code",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                    return@Button
                                }
                                viewModel.verifyLoginOtp(otp)
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(52.dp),
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Emerald700,
                                contentColor = Color.White,
                                disabledContainerColor = Emerald700.copy(alpha = 0.5f),
                                disabledContentColor = Color.White.copy(alpha = 0.8f)
                            ),
                            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp, pressedElevation = 4.dp),
                            enabled = !isSubmitting
                        ) {
                            if (isSubmitting) {
                                CircularProgressIndicator(
                                    color = Color.White,
                                    modifier = Modifier.size(20.dp),
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "যাচাই করা হচ্ছে..." else "Verifying...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isBengali) "লগইন সম্পন্ন করুন ও সন্ধান করুন" else "Verify & Complete Login",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.5.sp
                                )
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TextButton(onClick = { viewModel.backToCredentialsStep() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isBengali) "পুনরায় ক্যাপচা ও তথ্য" else "Back to Credentials",
                                    fontSize = 12.sp,
                                    color = Slate600
                                )
                            }

                            TextButton(
                                onClick = {
                                    SmsOtpHelper.startSmsUserConsent(context)
                                    viewModel.requestLoginOtp(username, password, captcha)
                                },
                                enabled = !isSubmitting
                            ) {
                                Text(
                                    text = if (isBengali) "পুনরায় ওটিপি পাঠান" else "Resend OTP",
                                    fontSize = 12.sp,
                                    color = Emerald700,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}