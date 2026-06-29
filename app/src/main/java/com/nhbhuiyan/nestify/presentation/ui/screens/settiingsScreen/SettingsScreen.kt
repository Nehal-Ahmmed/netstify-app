package com.nhbhuiyan.nestify.presentation.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.Backup
import androidx.compose.material.icons.outlined.CloudUpload
import androidx.compose.material.icons.outlined.DeleteSweep
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.FileDownload
import androidx.compose.material.icons.outlined.Logout
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyScaffold
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.TabPill
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Space
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Link
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
fun SettingsScreen(
    navController: NavController,
    onSignOut: () -> Unit = {}
) {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState by viewModel.uiState.collectAsState()
    val session by viewModel.sessionFlow.collectAsState()
    val context = LocalContext.current
    val c = NestifyTheme.colors

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken != null) {
                    viewModel.linkGoogleAccount(
                        idToken = idToken,
                        onSuccess = { Toast.makeText(context, "Google account linked successfully!", Toast.LENGTH_SHORT).show() },
                        onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                    )
                }
            } catch (e: ApiException) {
                Toast.makeText(context, "Google link failed: ${e.statusCode}", Toast.LENGTH_LONG).show()
            }
        }
    }

    val themeTabs = listOf("System", "Light", "Dark")
    val themeIndex = when (uiState.themeMode) {
        "light" -> 1
        "dark" -> 2
        else -> 0
    }

    NestifyScaffold(
        appBar = { NestifyAppBar(title = "Settings", onBack = { navController.popBackStack() }) },
    ) {
        Spacer(Modifier.height(Space.l))

        // ── Appearance ────────────────────────────────────────────────────────
        SectionHead(title = "Appearance", kicker = "Theme")
        Spacer(Modifier.height(Space.m))
        NestifyCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                IconTile(Icons.Outlined.Palette)
                Column(Modifier.weight(1f)) {
                    Text("App theme", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                    Kicker("Follow system, or force light / dark")
                }
            }
        }
        Spacer(Modifier.height(Space.s))
        TabPill(
            tabs = themeTabs,
            active = themeIndex,
            onChange = { idx ->
                viewModel.onThemeModeChanged(
                    when (idx) {
                        1 -> "light"
                        2 -> "dark"
                        else -> "system"
                    }
                )
            },
        )

        Spacer(Modifier.height(Space.xl))

        // ── Sync & Backup ─────────────────────────────────────────────────────
        SectionHead(title = "Sync & Backup", kicker = "Cloud")
        Spacer(Modifier.height(Space.m))
        ToggleRow(
            icon = Icons.Outlined.CloudUpload,
            title = "Cloud Sync",
            subtitle = "Automatically sync your data to the cloud",
            checked = uiState.isSyncEnabled,
            onCheckedChange = viewModel::onSyncEnabledChanged,
        )
        Spacer(Modifier.height(Space.s))
        ActionRow(
            icon = Icons.Outlined.Backup,
            title = "Backup & Export",
            subtitle = "Create a backup or export your data",
            onClick = viewModel::onBackupRequested,
        )

        Spacer(Modifier.height(Space.xl))

        // ── Data ──────────────────────────────────────────────────────────────
        SectionHead(title = "Data", kicker = "Storage")
        Spacer(Modifier.height(Space.m))
        ActionRow(
            icon = Icons.Outlined.DeleteSweep,
            title = "Clear Cache",
            subtitle = "Free up storage space",
            onClick = viewModel::onClearCacheRequested,
        )
        Spacer(Modifier.height(Space.s))
        ActionRow(
            icon = Icons.Outlined.FileDownload,
            title = "Export All Data",
            subtitle = "Export everything as files",
            onClick = viewModel::onExportDataRequested,
        )

        Spacer(Modifier.height(Space.xl))

        // ── About ─────────────────────────────────────────────────────────────
        SectionHead(title = "About", kicker = "Nestify")
        Spacer(Modifier.height(Space.m))
        ActionRow(
            icon = Icons.Outlined.Apps,
            title = "App Version",
            subtitle = "v1.0.0",
            onClick = viewModel::onCheckForUpdates,
        )
        Spacer(Modifier.height(Space.s))
        ActionRow(
            icon = Icons.Outlined.PrivacyTip,
            title = "Privacy Policy",
            subtitle = "How we handle your data",
            onClick = viewModel::onPrivacyPolicyClicked,
        )
        Spacer(Modifier.height(Space.s))
        ActionRow(
            icon = Icons.Outlined.Description,
            title = "Terms of Service",
            subtitle = "App usage terms and conditions",
            onClick = viewModel::onTermsOfServiceClicked,
        )
        Spacer(Modifier.height(Space.s))
        ActionRow(
            icon = Icons.Outlined.Star,
            title = "Rate App",
            subtitle = "Share your feedback",
            onClick = viewModel::onRateAppClicked,
        )

        Spacer(Modifier.height(Space.xl))

        // ── Connected Accounts ────────────────────────────────────────────────
        SectionHead(title = "Connected Accounts", kicker = "Security")
        Spacer(Modifier.height(Space.m))

        // Primary Email
        NestifyCard(modifier = Modifier.fillMaxWidth()) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                IconTile(Icons.Outlined.Email, background = c.brandSoft, tint = c.brand)
                Column(Modifier.weight(1f)) {
                    Text("Primary Email", style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                    Kicker(session?.email ?: "Not available")
                }
                Text("Primary", style = NestifyTheme.type.kicker, color = c.brand)
            }
        }

        Spacer(Modifier.height(Space.s))

        // Google Account
        val isGoogleLinked = session?.providers?.contains("google.com") == true
        val linkedEmail = session?.linkedEmails?.firstOrNull() ?: "Google Account"

        NestifyCard(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                if (isGoogleLinked) {
                    viewModel.unlinkGoogleAccount(
                        onSuccess = { Toast.makeText(context, "Google account unlinked successfully!", Toast.LENGTH_SHORT).show() },
                        onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                    )
                } else {
                    val webClientIdResId = context.resources.getIdentifier("default_web_client_id", "string", context.packageName)
                    val webClientId = if (webClientIdResId != 0) context.getString(webClientIdResId) else ""
                    if (webClientId.isEmpty()) {
                        Toast.makeText(context, "Firebase not configured.", Toast.LENGTH_LONG).show()
                        return@NestifyCard
                    }
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestIdToken(webClientId)
                        .requestEmail()
                        .build()
                    val client = GoogleSignIn.getClient(context, gso)
                    client.signOut().addOnCompleteListener {
                        googleSignInLauncher.launch(client.signInIntent)
                    }
                }
            }
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                IconTile(
                    icon = Icons.Outlined.Link,
                    background = if (isGoogleLinked) c.brandSoft else c.coralSoft,
                    tint = if (isGoogleLinked) c.brand else c.coral
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = if (isGoogleLinked) "Linked Google Account" else "Link Google Account",
                        style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold),
                        color = c.ink
                    )
                    Kicker(if (isGoogleLinked) linkedEmail else "Allow signing in with a secondary Google account")
                }
                Text(
                    text = if (isGoogleLinked) "Disconnect" else "Connect",
                    style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Bold),
                    color = if (isGoogleLinked) c.coral else c.brand
                )
            }
        }

        Spacer(Modifier.height(Space.xl))

        // ── Account ───────────────────────────────────────────────────────────
        SectionHead(title = "Account", kicker = "Session")
        Spacer(Modifier.height(Space.m))
        ActionRow(
            icon = Icons.Outlined.Logout,
            title = "Sign Out",
            subtitle = "Log out from your Nestify account",
            tint = c.coral,
            onClick = { viewModel.signOut { onSignOut() } },
        )
    }

    // ── Dialogs ───────────────────────────────────────────────────────────────
    if (viewModel.showBackupDialog) {
        BackupDialog(
            onExport = { viewModel.onExportBackup() },
            onImport = { viewModel.onImportBackup() },
            onDismiss = { viewModel.showBackupDialog = false }
        )
    }
    if (viewModel.showClearCacheDialog) {
        ConfirmationDialog(
            title = "Clear Cache",
            message = "This will free up storage space. This action cannot be undone.",
            onConfirm = { viewModel.onClearCacheConfirmed() },
            onDismiss = { viewModel.showClearCacheDialog = false }
        )
    }
}

// ── Rows ───────────────────────────────────────────────────────────────────────

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
            IconTile(icon)
            Column(Modifier.weight(1f)) {
                Text(title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Kicker(subtitle)
            }
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = c.brand,
                ),
            )
        }
    }
}

@Composable
private fun ActionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    tint: Color? = null,
    onClick: () -> Unit,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = Modifier.fillMaxWidth(), onClick = onClick) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
            IconTile(
                icon,
                background = if (tint != null) c.coralSoft else c.brandSoft,
                tint = tint ?: c.brand,
            )
            Column(Modifier.weight(1f)) {
                Text(title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = tint ?: c.ink)
                Kicker(subtitle)
            }
        }
    }
}

// ── Dialogs (kept functional; light Material) ────────────────────────────────────

@Composable
fun BackupDialog(
    onExport: () -> Unit,
    onImport: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Backup & Export") },
        text = { Text("Choose whether to export your data as backup or import an existing backup.") },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = {
            Column {
                Button(onClick = { onExport(); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Export Backup")
                }
                Spacer(Modifier.height(8.dp))
                OutlinedButton(onClick = { onImport(); onDismiss() }, modifier = Modifier.fillMaxWidth()) {
                    Text("Import Backup")
                }
            }
        }
    )
}

@Composable
fun ConfirmationDialog(
    title: String,
    message: String,
    confirmText: String = "Confirm",
    isDestructive: Boolean = false,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val c = NestifyTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = { Text(message) },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(); onDismiss() },
                colors = ButtonDefaults.textButtonColors(contentColor = if (isDestructive) c.coral else c.brand)
            ) { Text(confirmText) }
        }
    )
}
