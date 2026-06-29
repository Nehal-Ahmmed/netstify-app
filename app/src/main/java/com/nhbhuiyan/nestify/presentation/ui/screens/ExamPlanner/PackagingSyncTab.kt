package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudQueue
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Kicker
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.OneLine
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.SectionHead
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class SyncLog(
    val fileName: String,
    val dateString: String,
    val size: String,
    val status: String,
)

@Composable
fun PackagingSyncTab(
    viewModel: ExamPlannerViewModel,
    defaultLevel: Int,
    defaultTerm: Int,
) {
    val c = NestifyTheme.colors
    val context = LocalContext.current
    var syncPath by remember { mutableStateOf("Google Drive/Nestify App Data/Backups/") }
    var isSyncing by remember { mutableStateOf(false) }
    var showPackageProgress by remember { mutableStateOf(false) }
    var syncProgressMessage by remember { mutableStateOf("") }
    var currentActionType by remember { mutableStateOf("sync") }

    val subjectsList by viewModel.subjects.collectAsState()
    val classTestMarksMap by viewModel.classTestMarks.collectAsState()

    var filterLevel by remember(defaultLevel) { mutableIntStateOf(defaultLevel) }
    var filterTerm by remember(defaultTerm) { mutableIntStateOf(defaultTerm) }

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    val syncHistory = remember {
        mutableStateListOf(
            SyncLog("Nestify_L1T1_Transcript_REP_001.json.zip", "June 10, 2025 14:32", "2.1 MB", "Synced"),
            SyncLog("Nestify_L1T2_Transcript_REP_002.json.zip", "Dec 18, 2025 11:05", "2.4 MB", "Synced"),
            SyncLog("Nestify_L2T1_Transcript_REP_003.json.zip", "May 04, 2026 17:50", "3.0 MB", "Synced"),
        )
    }

    val coroutineScope = rememberCoroutineScope()
    var isProcessingArchive by remember { mutableStateOf(false) }
    val termReports by viewModel.termReports.collectAsState()

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/zip"),
    ) { uri ->
        if (uri != null) {
            isProcessingArchive = true
            coroutineScope.launch {
                try {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        val termReport = termReports.firstOrNull { it.level == filterLevel && it.term == filterTerm }
                        val success = withContext(Dispatchers.IO) {
                            AcademicArchiveSyncEngine.exportToZip(
                                context = context, outputStream = outputStream,
                                level = filterLevel, term = filterTerm,
                                subjects = filteredSubjects, ctMarksMap = classTestMarksMap, termReport = termReport,
                            )
                        }
                        isProcessingArchive = false
                        if (success) {
                            Toast.makeText(context, "L${filterLevel}T${filterTerm} Backup Exported Successfully!", Toast.LENGTH_SHORT).show()
                            showPackageProgress = true
                            syncHistory.add(0, SyncLog("Nestify_Personal_Backup_L${filterLevel}T${filterTerm}.zip", "Just Now", "ZIP Backup", "Local Only"))
                        } else {
                            Toast.makeText(context, "Failed to compile ZIP archive.", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    isProcessingArchive = false
                    Toast.makeText(context, "Error exporting backup: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri != null) {
            isProcessingArchive = true
            coroutineScope.launch {
                try {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val archive = withContext(Dispatchers.IO) {
                            AcademicArchiveSyncEngine.importFromZip(context, inputStream)
                        }
                        if (archive != null) {
                            viewModel.restoreSemesterArchive(
                                archive = archive,
                                onSuccess = {
                                    isProcessingArchive = false
                                    Toast.makeText(context, "L${archive.level}T${archive.term} Data Restored Successfully!", Toast.LENGTH_LONG).show()
                                },
                                onFailure = { error ->
                                    isProcessingArchive = false
                                    Toast.makeText(context, "Restore failed: $error", Toast.LENGTH_LONG).show()
                                },
                            )
                        } else {
                            isProcessingArchive = false
                            Toast.makeText(context, "Invalid or corrupt backup ZIP file.", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    isProcessingArchive = false
                    Toast.makeText(context, "Restore error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    val googleSignInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val task = com.google.android.gms.auth.api.signin.GoogleSignIn.getSignedInAccountFromIntent(result.data)
            try {
                val account = task.getResult(com.google.android.gms.common.api.ApiException::class.java)
                coroutineScope.launch {
                    isSyncing = true
                    syncPath = "Google Drive/Nestify/"
                    try {
                        if (currentActionType == "restore") {
                            syncProgressMessage = "Connecting to Google Drive..."
                            val success = GoogleDriveSyncManager.restoreAllFromDrive(
                                context = context, account = account!!, database = viewModel.appDataBase,
                            ) { progress -> syncProgressMessage = progress }
                            if (success) Toast.makeText(context, "Full restore completed successfully!", Toast.LENGTH_LONG).show()
                            else Toast.makeText(context, "Restore failed or no backup found.", Toast.LENGTH_LONG).show()
                        } else {
                            syncProgressMessage = "Connecting to Google Drive..."
                            val success = GoogleDriveSyncManager.syncAllDataToDrive(
                                context = context, account = account!!, database = viewModel.appDataBase,
                            ) { progress -> syncProgressMessage = progress }
                            if (success) {
                                Toast.makeText(context, "Full sync to Google Drive completed!", Toast.LENGTH_SHORT).show()
                                syncHistory.add(0, SyncLog("Full Sync (Google Drive/Nestify)", "Just Now", "All Data", "Synced to Drive"))
                            } else {
                                Toast.makeText(context, "Google Drive sync failed.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(context, "Sync/Restore error: ${e.message}", Toast.LENGTH_SHORT).show()
                    } finally {
                        isSyncing = false
                    }
                }
            } catch (e: com.google.android.gms.common.api.ApiException) {
                Toast.makeText(context, "Google Sign-In Failed: ${e.statusCode}", Toast.LENGTH_SHORT).show()
                isSyncing = false
            }
        } else {
            isSyncing = false
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(start = Space.screen, end = Space.screen, top = Space.s, bottom = AcademicNavClearance),
        verticalArrangement = Arrangement.spacedBy(Space.m),
    ) {
        item {
            SectionHead(title = "Packaging & sync", kicker = "Cloud engine")
            Spacer(Modifier.height(4.dp))
            Text("Archive completed semesters and back grade records up to your cloud folder.", style = NestifyTheme.type.body, color = c.ink50)
        }

        item {
            LevelTermFilter(
                level = filterLevel, term = filterTerm,
                onLevel = { filterLevel = it }, onTerm = { filterTerm = it },
            )
        }

        // Cloud target directory
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                    Text("Cloud target directory", style = NestifyTheme.type.h3Serif, color = c.ink)
                    Row(
                        Modifier.fillMaxWidth().clip(Radii.m).background(c.surface2).padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconTile(Icons.Outlined.CloudQueue, size = 32.dp, corner = 8.dp)
                        Spacer(Modifier.size(10.dp))
                        OneLine(syncPath, style = NestifyTheme.type.label, color = c.ink70, modifier = Modifier.weight(1f))
                    }
                    NButton("Configure folder target", {
                        Toast.makeText(context, "Opening Directory Tree Selector (SAF)...", Toast.LENGTH_SHORT).show()
                        syncPath = "OneDrive/Apps/Nestify/SyncBackups/"
                    }, full = true, variant = BtnVariant.Secondary)
                }
            }
        }

        // Package records
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Package academic records", style = NestifyTheme.type.h3Serif, color = c.ink)
                    Text("Compile L$filterLevel T$filterTerm subjects, CT marks, checklists and transcripts into a verified ZIP package.", style = NestifyTheme.type.meta, color = c.ink50)

                    AnimatedVisibility(visible = showPackageProgress) {
                        Box(Modifier.fillMaxWidth().clip(Radii.m).background(c.okSoft).padding(12.dp)) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Chip("Done", tone = ChipTone.Ok)
                                    Spacer(Modifier.size(8.dp))
                                    Text("Compilation successful!", style = NestifyTheme.type.label, color = c.ink)
                                }
                                Spacer(Modifier.height(6.dp))
                                OneLine("Nestify_Personal_Backup_L${filterLevel}T${filterTerm}.zip", style = NestifyTheme.type.meta, color = c.ink70)
                            }
                        }
                    }

                    if (isProcessingArchive) {
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = c.brand, strokeWidth = 2.dp)
                            Spacer(Modifier.size(8.dp))
                            Text("Processing package...", style = NestifyTheme.type.meta, color = c.ink50)
                        }
                    }

                    NButton("Export L${filterLevel}T${filterTerm} backup", {
                        exportLauncher.launch("Nestify_Personal_Backup_L${filterLevel}T${filterTerm}.zip")
                    }, full = true)
                }
            }
        }

        // Restore records
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Restore academic records", style = NestifyTheme.type.h3Serif, color = c.ink)
                    Text("Import a previously backed-up Nestify ZIP to restore subjects, CT marks and dashboard records.", style = NestifyTheme.type.meta, color = c.ink50)
                    NButton("Restore previous data", { importLauncher.launch(arrayOf("application/zip")) }, full = true, variant = BtnVariant.Secondary)
                }
            }
        }

        // Cloud sync / restore
        item {
            NestifyCard(Modifier.fillMaxWidth()) {
                Column(verticalArrangement = Arrangement.spacedBy(Space.m)) {
                    Text("Nestify cloud sync & restore", style = NestifyTheme.type.h3Serif, color = c.ink)
                    Text("Securely sync your entire database into structured folders under '/Nestify' in your Google Drive.", style = NestifyTheme.type.meta, color = c.ink50)

                    if (isSyncing) {
                        Column(Modifier.fillMaxWidth().clip(Radii.m).background(c.surface2).padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                                Text(syncProgressMessage, style = NestifyTheme.type.label, color = c.ink)
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp, color = c.brand)
                            }
                            LinearProgressIndicator(
                                modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                                color = c.brand, trackColor = c.ink10,
                            )
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                        NButton("Backup now", {
                            currentActionType = "sync"; isSyncing = true
                            Toast.makeText(context, "Connecting to Google Drive for Sync...", Toast.LENGTH_SHORT).show()
                            googleSignInLauncher.launch(GoogleDriveSyncManager.getSignInClient(context).signInIntent)
                        }, modifier = Modifier.weight(1f))
                        NButton("Restore now", {
                            currentActionType = "restore"; isSyncing = true
                            Toast.makeText(context, "Connecting to Google Drive for Restore...", Toast.LENGTH_SHORT).show()
                            googleSignInLauncher.launch(GoogleDriveSyncManager.getSignInClient(context).signInIntent)
                        }, modifier = Modifier.weight(1f), variant = BtnVariant.Secondary)
                    }
                }
            }
        }

        item { SectionHead(title = "Archived packages", kicker = "Log") }

        items(syncHistory) { log ->
            NestifyCard(Modifier.fillMaxWidth()) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Space.m)) {
                    IconTile(Icons.Outlined.Description, size = 36.dp)
                    Column(Modifier.weight(1f)) {
                        OneLine(log.fileName, style = NestifyTheme.type.label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold), color = c.ink)
                        Kicker("${log.dateString} · ${log.size}")
                    }
                    Chip(log.status, tone = ChipTone.Ok)
                }
            }
        }
    }
}
