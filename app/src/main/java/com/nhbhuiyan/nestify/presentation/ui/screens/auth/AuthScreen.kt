package com.nhbhuiyan.nestify.presentation.ui.screens.auth

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.nhbhuiyan.nestify.presentation.navigation.Components.Route
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BrandMark
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.TabPill
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun AuthScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val c = NestifyTheme.colors
    val authState by viewModel.authState.collectAsState()

    var isLoginTab by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Google Sign-In setup
    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.signInWithGoogle(idToken)
                } else {
                    Toast.makeText(context, "Google Sign-In Failed: No ID Token found. Make sure SHA-1 fingerprint is registered.", Toast.LENGTH_LONG).show()
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google Sign-In API Exception: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Authenticated -> {
                navController.navigate(Route.InAppNav1.route) {
                    popUpTo(Route.Auth.route) { inclusive = true }
                }
            }
            is AuthState.Error -> {
                Toast.makeText(context, (authState as AuthState.Error).message, Toast.LENGTH_LONG).show()
                viewModel.clearError()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(c.canvas),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = Space.xl, vertical = Space.xxxl),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Space.l)
        ) {
            // ── App Branding Header ──────────────────────────────────────────
            BrandMark(size = 56.dp, showLabel = false)
            Spacer(Modifier.height(Space.xs))
            Text(
                text = "Nestify",
                style = NestifyTheme.type.displaySerif,
                color = c.ink
            )
            Kicker("Your personal productivity nest", color = c.ink50)

            Spacer(Modifier.height(Space.s))

            // ── Auth Card ────────────────────────────────────────────────────
            NestifyCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(),
                padding = Space.xl,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Space.l)
                ) {
                    // Tab Selector
                    TabPill(
                        tabs = listOf("Log In", "Sign Up"),
                        active = if (isLoginTab) 0 else 1,
                        onChange = { isLoginTab = it == 0 },
                    )

                    Text(
                        text = if (isLoginTab) "Welcome back" else "Create your account",
                        style = NestifyTheme.type.h2Serif,
                        color = c.ink,
                        textAlign = TextAlign.Center,
                    )

                    // Form Fields
                    NestifyInput(
                        value = email,
                        onValueChange = { email = it },
                        label = "Email Address",
                        placeholder = "you@student.cuet.ac.bd",
                        leadingIcon = Icons.Default.Email,
                        modifier = Modifier.fillMaxWidth(),
                    )

                    NestifyInput(
                        value = password,
                        onValueChange = { password = it },
                        label = "Password",
                        placeholder = "••••••••",
                        leadingIcon = Icons.Default.Lock,
                        modifier = Modifier.fillMaxWidth(),
                        trailing = {
                            val icon = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility
                            Icon(
                                icon,
                                contentDescription = "Toggle password visibility",
                                tint = c.ink50,
                                modifier = Modifier
                                    .size(18.dp)
                                    .clickableWithoutRipple { passwordVisible = !passwordVisible }
                            )
                        },
                    )

                    // Confirm Password Field (Sign Up Only)
                    AnimatedVisibility(visible = !isLoginTab) {
                        NestifyInput(
                            value = confirmPassword,
                            onValueChange = { confirmPassword = it },
                            label = "Confirm Password",
                            placeholder = "••••••••",
                            leadingIcon = Icons.Default.Lock,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }

                    if (authState is AuthState.Loading) {
                        CircularProgressIndicator(color = c.brand, modifier = Modifier.size(24.dp))
                    } else {
                        // Submit Button
                        NButton(
                            label = if (isLoginTab) "Sign In" else "Create Account",
                            onClick = {
                                if (email.isEmpty() || password.isEmpty()) {
                                    Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                                    return@NButton
                                }
                                if (!isLoginTab && password != confirmPassword) {
                                    Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
                                    return@NButton
                                }
                                if (isLoginTab) {
                                    viewModel.signInWithEmail(email, password)
                                } else {
                                    viewModel.signUpWithEmail(email, password)
                                }
                            },
                            variant = BtnVariant.Primary,
                            size = BtnSize.Lg,
                            full = true,
                        )
                    }

                    // Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(c.hair)
                        )
                        Kicker(
                            "Or continue with",
                            color = c.ink50,
                            modifier = Modifier.padding(horizontal = Space.s)
                        )
                        Box(
                            Modifier
                                .weight(1f)
                                .height(1.dp)
                                .background(c.hair)
                        )
                    }

                    // Google Sign-In Button
                    NButton(
                        label = "Sign In with Google",
                        onClick = {
                            val webClientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                            val webClientId = if (webClientIdResId != 0) context.getString(webClientIdResId) else ""
                            if (webClientId.isEmpty()) {
                                Toast.makeText(
                                    context,
                                    "Developer Config Error: Firebase is not configured yet. Please add google-services.json to app/ directory.",
                                    Toast.LENGTH_LONG
                                ).show()
                                return@NButton
                            }

                            val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestIdToken(webClientId)
                                .requestEmail()
                                .build()
                            val client = GoogleSignIn.getClient(context, gso)
                            // Force sign out to ensure account selector shows up
                            client.signOut().addOnCompleteListener {
                                googleSignInLauncher.launch(client.signInIntent)
                            }
                        },
                        variant = BtnVariant.Secondary,
                        size = BtnSize.Lg,
                        full = true,
                    )
                }
            }

            // Dev-only quick login (visible only in debug builds)
            if (com.nhbhuiyan.nestify.BuildConfig.DEBUG) {
                DevQuickLoginPanel(
                    onPick = { account ->
                        email = account.email
                        password = "123456"
                        confirmPassword = "123456"
                        isLoginTab = true
                    }
                )
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dev-only quick login helper. Strategically samples ~30 seeded test accounts
// across every department, batch, and role so you can sign in with one tap.
// Compiled in for all builds but only rendered when BuildConfig.DEBUG is true.
// ---------------------------------------------------------------------------

private data class DevAccount(val email: String, val dept: String, val batch: String, val role: String)

private val devAccounts: List<DevAccount> = run {
    val deptNumeric = mapOf("CSE" to "04", "EEE" to "08", "CE" to "01", "ME" to "02")
    // (dept, batch, roll) — roll 1 = Admin·CR, roll 2-4 = CR, roll 5-10 = Student.
    val picks = listOf(
        Triple("CSE", "21", 1), Triple("CSE", "21", 3), Triple("CSE", "21", 7),
        Triple("CSE", "22", 5), Triple("CSE", "23", 1), Triple("CSE", "23", 2),
        Triple("CSE", "24", 4), Triple("CSE", "24", 9),
        Triple("EEE", "21", 8), Triple("EEE", "22", 1), Triple("EEE", "22", 6),
        Triple("EEE", "23", 3), Triple("EEE", "23", 10), Triple("EEE", "24", 2),
        Triple("EEE", "24", 7),
        Triple("CE", "21", 4), Triple("CE", "21", 9), Triple("CE", "22", 1),
        Triple("CE", "22", 7), Triple("CE", "23", 2), Triple("CE", "24", 1),
        Triple("CE", "24", 5),
        Triple("ME", "21", 2), Triple("ME", "21", 6), Triple("ME", "22", 3),
        Triple("ME", "22", 10), Triple("ME", "23", 1), Triple("ME", "23", 8),
        Triple("ME", "24", 4), Triple("ME", "24", 5)
    )
    picks.map { (dept, batch, roll) ->
        val numeric = deptNumeric.getValue(dept)
        val roll3 = roll.toString().padStart(3, '0')
        val role = when {
            roll == 1 -> "Admin · CR"
            roll <= 4 -> "CR"
            else -> "Student"
        }
        DevAccount(
            email = "u$batch$numeric$roll3@student.cuet.ac.bd",
            dept = dept,
            batch = "'$batch",
            role = role
        )
    }
}

@Composable
private fun DevQuickLoginPanel(onPick: (DevAccount) -> Unit) {
    val c = NestifyTheme.colors
    var expanded by remember { mutableStateOf(false) }

    NestifyCard(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        padding = Space.l,
        background = c.surface2,
    ) {
        Column {
            // Dropdown header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickableWithoutRipple { expanded = !expanded },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Science, contentDescription = null, tint = c.brand)
                Spacer(Modifier.width(Space.s))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Dev quick login",
                        color = c.ink,
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Kicker(
                        "${devAccounts.size} test accounts · password 123456",
                        color = c.ink50,
                    )
                }
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Collapse" else "Expand",
                    tint = c.ink50
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(max = 300.dp)
                        .verticalScroll(rememberScrollState())
                        .padding(top = Space.s),
                    verticalArrangement = Arrangement.spacedBy(Space.xs)
                ) {
                    devAccounts.forEach { account ->
                        DevAccountRow(account = account, onClick = { onPick(account) })
                    }
                }
            }
        }
    }
}

@Composable
private fun DevAccountRow(account: DevAccount, onClick: () -> Unit) {
    val c = NestifyTheme.colors
    val roleColor = when {
        account.role.startsWith("Admin") -> c.warn
        account.role == "CR" -> c.brand
        else -> c.ok
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(Radii.s)
            .background(c.surface)
            .clickable(onClick = onClick)
            .padding(horizontal = Space.m, vertical = Space.s),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                account.email,
                color = c.ink,
                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                maxLines = 1
            )
            Kicker(
                "${account.dept} · Batch ${account.batch}",
                color = c.ink50,
            )
        }
        Box(
            modifier = Modifier
                .clip(Radii.xs)
                .background(roleColor.copy(alpha = 0.18f))
                .padding(horizontal = Space.s, vertical = 3.dp)
        ) {
            Text(
                account.role,
                color = roleColor,
                style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
            )
        }
    }
}

// Ripple-free clickable helper for custom-shaped containers
@Composable
fun Modifier.clickableWithoutRipple(onClick: () -> Unit): Modifier {
    val interactionSource = remember { MutableInteractionSource() }
    return this
        .clip(RoundedCornerShape(24.dp))
        .clickable(interactionSource = interactionSource, indication = null, onClick = onClick)
}
