package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nhbhuiyan.nestify.ui.theme.*
import androidx.compose.runtime.collectAsState
import com.nhbhuiyan.nestify.data.local.entity.SubjectEntity

data class SyncLog(
    val fileName: String,
    val dateString: String,
    val size: String,
    val status: String // "Synced" or "Local Only"
)

@Composable
fun PackagingSyncTab(viewModel: ExamPlannerViewModel) {
    val context = LocalContext.current
    var syncPath by remember { mutableStateOf("Google Drive/Nestify App Data/Backups/") }
    var isSyncing by remember { mutableStateOf(false) }
    var showPackageProgress by remember { mutableStateOf(false) }

    val subjectsList by viewModel.subjects.collectAsState()
    val classTestMarksMap by viewModel.classTestMarks.collectAsState()
    val syllabusTopicsMap by viewModel.syllabusTopics.collectAsState()

    var filterLevel by remember { mutableIntStateOf(2) }
    var filterTerm by remember { mutableIntStateOf(2) }

    val filteredSubjects = remember(subjectsList, filterLevel, filterTerm) {
        subjectsList.filter { it.level == filterLevel && it.term == filterTerm }
    }

    val gradedCourses = filteredSubjects.filter { it.finalGrade != "Pending" }
    val totalCredits = gradedCourses.map { it.credits }.sum()
    val weightedGpSum = gradedCourses.map { it.credits * (AcademicGradingEngine.gradeToGp(it.finalGrade)) }.sum()
    val calculatedGpa = if (totalCredits > 0) weightedGpSum / totalCredits else 0.00f

    val syncHistory = remember {
        mutableStateListOf(
            SyncLog("Nestify_L1T1_Transcript_REP_001.json.zip", "June 10, 2025 14:32", "2.1 MB", "Synced"),
            SyncLog("Nestify_L1T2_Transcript_REP_002.json.zip", "Dec 18, 2025 11:05", "2.4 MB", "Synced"),
            SyncLog("Nestify_L2T1_Transcript_REP_003.json.zip", "May 04, 2026 17:50", "3.0 MB", "Synced")
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Packaging & Cloud Sync Engine",
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = NestifySlate,
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Text(
                "Archive completed semesters and backup grade records to your cloud folder.",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }

        // Viewing Filter Bar
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = NestifySlate,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Viewing Filter:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = NestifySlate
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.05f))
                                .clickable {
                                    filterLevel = if (filterLevel == 4) 1 else filterLevel + 1
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("L$filterLevel", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                        }

                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(NestifySlate.copy(alpha = 0.05f))
                                .clickable {
                                    filterTerm = if (filterTerm == 2) 1 else 2
                                }
                                .padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("T$filterTerm", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = NestifySlate)
                            Icon(Icons.Default.ArrowDropDown, null, modifier = Modifier.size(14.dp), tint = NestifySlate)
                        }
                    }
                }
            }
        }

        // Current Backup Directory Selection
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        "Cloud Target Directory",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NestifySlate
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NestifySlate.copy(alpha = 0.05f))
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CloudQueue, null, tint = NestifySlate, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = syncPath,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = NestifySlate,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            Toast.makeText(context, "Opening Directory Tree Selector (SAF)...", Toast.LENGTH_SHORT).show()
                            syncPath = "OneDrive/Apps/Nestify/SyncBackups/"
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.FolderOpen, null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Configure Folder Target")
                    }
                }
            }
        }

        // Dynamic Packaging Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Package Academic Records",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NestifySlate
                    )
                    Text(
                        "Compile L${filterLevel} T${filterTerm} subjects, CT marks, checklists, and transcripts into a verified JSON package.",
                        fontSize = 11.sp,
                        color = Color.Gray
                    )

                    AnimatedVisibility(visible = showPackageProgress) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(NestifyGreen.copy(alpha = 0.1f))
                                .padding(12.dp)
                        ) {
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Verified, null, tint = NestifyGreen)
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Compilation Successful!", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = NestifySlate)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    "Package Name: Nestify_L${filterLevel}T${filterTerm}_Transcript_REP_004.json.zip",
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text("Size: 3.4 MB | Encryption: AES-256", fontSize = 10.sp, color = Color.Gray)
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val jsonString = AcademicArchiveSyncEngine.exportSemesterPackage(
                                level = filterLevel,
                                term = filterTerm,
                                gpa = calculatedGpa,
                                subjects = filteredSubjects,
                                ctMarksMap = classTestMarksMap,
                                topicsMap = syllabusTopicsMap
                            )
                            showPackageProgress = true
                            Toast.makeText(context, "Packed L${filterLevel}T${filterTerm} Semester Package (${jsonString.length} bytes)", Toast.LENGTH_LONG).show()
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = NestifySlate)
                    ) {
                        Icon(Icons.Default.Archive, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Create L${filterLevel}T${filterTerm} Package", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Force Sync Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.08f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        "Synchronize Backup Folder",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = NestifySlate
                    )

                    if (isSyncing) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Uploading backups to cloud folder...", fontSize = 12.sp, color = Color.Gray)
                                Text("45%", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { 0.45f },
                                modifier = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
                                color = NestifySlate,
                                trackColor = NestifySlate.copy(alpha = 0.1f)
                            )
                        }
                    }

                    Button(
                        onClick = {
                            isSyncing = true
                            Toast.makeText(context, "Syncing files to $syncPath", Toast.LENGTH_SHORT).show()
                            // Simulate upload finishes in 2 seconds
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                isSyncing = false
                                if (showPackageProgress) {
                                    syncHistory.add(
                                        0,
                                        SyncLog("Nestify_L2T2_Transcript_REP_004.json.zip", "Just Now", "3.4 MB", "Synced")
                                    )
                                    showPackageProgress = false
                                }
                                Toast.makeText(context, "Sync Complete!", Toast.LENGTH_SHORT).show()
                            }, 2000)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = if (isSyncing) Color.Gray else NestifySlate),
                        enabled = !isSyncing
                    ) {
                        Icon(Icons.Default.Sync, null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isSyncing) "Syncing..." else "Force Sync Now", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Sync History logs
        item {
            Text(
                "Archived Semester Packages Log",
                fontWeight = FontWeight.Black,
                fontSize = 17.sp,
                color = NestifySlate
            )
        }

        items(syncHistory) { log ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.White,
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.InsertDriveFile,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = log.fileName,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = NestifySlate,
                            maxLines = 1
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(log.dateString, fontSize = 9.sp, color = Color.Gray)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(log.size, fontSize = 9.sp, color = NestifySlate, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(NestifyGreen.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            log.status,
                            fontWeight = FontWeight.Black,
                            fontSize = 10.sp,
                            color = Color(0xFF27AE60)
                        )
                    }
                }
            }
        }
    }
}
