package com.nhbhuiyan.nestify.presentation.ui.screens.ProfileScreen

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nhbhuiyan.nestify.domain.model.ExperienceData
import com.nhbhuiyan.nestify.domain.model.ProjectData
import com.nhbhuiyan.nestify.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showImagePicker by remember { mutableStateOf(false) }
    var imagePickerTarget by remember { mutableStateOf(ImagePickerTarget.NONE) }

    var showExperienceDialog by remember { mutableStateOf(false) }
    var editingExperienceIndex by remember { mutableStateOf(-1) }
    var tempExperience by remember { mutableStateOf(ExperienceData()) }

    var showProjectDialog by remember { mutableStateOf(false) }
    var editingProjectIndex by remember { mutableIntStateOf(-1) }
    var tempProject by remember { mutableStateOf(ProjectData()) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null && imagePickerTarget != ImagePickerTarget.NONE) {
            viewModel.handleImageResult(context, uri, imagePickerTarget)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(if (state.isEditing) "Edit Profile" else "Profile") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                actions = {
                    if (state.isEditing) {
                        TextButton(onClick = { viewModel.cancelEditing() }) {
                            Text("Cancel", color = MaterialTheme.colorScheme.error)
                        }
                    }
                    IconButton(onClick = { viewModel.toggleEditing() }) {
                        Icon(
                            if (state.isEditing) Icons.Default.Check else Icons.Default.Edit,
                            if (state.isEditing) "Save" else "Edit"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = NestifySurface
    ) { paddingValues ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    ProfileHeader(
                        state = state,
                        isEditing = state.isEditing,
                        onAvatarClick = {
                            imagePickerTarget = ImagePickerTarget.AVATAR
                            imagePickerLauncher.launch("image/*")
                        },
                        onBackgroundClick = {
                            imagePickerTarget = ImagePickerTarget.BACKGROUND
                            imagePickerLauncher.launch("image/*")
                        },
                        onNameChange = viewModel::updateName,
                        onJobTitleChange = viewModel::updateJobTitle,
                        onLocationChange = viewModel::updateLocation
                    )
                }

                item {
                    AboutMeSection(
                        bio = state.bio,
                        isEditing = state.isEditing,
                        onBioChange = viewModel::updateBio
                    )
                }

                item {
                    SkillsSection(
                        skills = state.skills,
                        newSkill = state.newSkill,
                        isEditing = state.isEditing,
                        onNewSkillChange = viewModel::updateNewSkill,
                        onAddSkill = viewModel::addSkill,
                        onRemoveSkill = viewModel::removeSkill
                    )
                }

                item {
                    ExperienceSection(
                        experiences = state.experiences,
                        isEditing = state.isEditing,
                        onAdd = {
                            tempExperience = ExperienceData()
                            editingExperienceIndex = -1
                            showExperienceDialog = true
                        },
                        onEdit = { index ->
                            tempExperience = state.experiences[index]
                            editingExperienceIndex = index
                            showExperienceDialog = true
                        },
                        onRemove = viewModel::removeExperience
                    )
                }

                item {
                    ProjectsSection(
                        projects = state.projects,
                        isEditing = state.isEditing,
                        onAdd = {
                            tempProject = ProjectData()
                            editingProjectIndex = -1
                            showProjectDialog = true
                        },
                        onEdit = { index ->
                            tempProject = state.projects[index]
                            editingProjectIndex = index
                            showProjectDialog = true
                        },
                        onRemove = viewModel::removeProject
                    )
                }

                item {
                    ConnectSection(
                        email = state.email,
                        linkedin = state.linkedin,
                        github = state.github,
                        website = state.website,
                        twitter = state.twitter,
                        youtube = state.youtube,
                        instagram = state.instagram,
                        isEditing = state.isEditing,
                        onEmailChange = viewModel::updateEmail,
                        onLinkedinChange = viewModel::updateLinkedin,
                        onGithubChange = viewModel::updateGithub,
                        onWebsiteChange = viewModel::updateWebsite,
                        onTwitterChange = viewModel::updateTwitter,
                        onYoutubeChange = viewModel::updateYoutube,
                        onInstagramChange = viewModel::updateInstagram
                    )
                }

                item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }

    if (showExperienceDialog) {
        ExperienceDialog(
            experience = tempExperience,
            onExperienceChange = { tempExperience = it },
            onSave = {
                if (editingExperienceIndex >= 0) {
                    viewModel.updateExperience(editingExperienceIndex, tempExperience)
                } else {
                    viewModel.addExperience(tempExperience)
                }
                showExperienceDialog = false
            },
            onDismiss = { showExperienceDialog = false }
        )
    }

    if (showProjectDialog) {
        ProjectDialog(
            project = tempProject,
            onProjectChange = { tempProject = it },
            onSave = {
                if (editingProjectIndex >= 0) {
                    viewModel.updateProject(editingProjectIndex, tempProject)
                } else {
                    viewModel.addProject(tempProject)
                }
                showProjectDialog = false
            },
            onDismiss = { showProjectDialog = false }
        )
    }
}

@Composable
fun ProfileHeader(
    state: ProfileUiState,
    isEditing: Boolean,
    onAvatarClick: () -> Unit,
    onBackgroundClick: () -> Unit,
    onNameChange: (String) -> Unit,
    onJobTitleChange: (String) -> Unit,
    onLocationChange: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
            ) {
                if (state.backgroundUrl.isNotEmpty()) {
                    AsyncImage(
                        model = state.backgroundUrl,
                        contentDescription = "Background",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NestifyGradients.meshGradient())
                    )
                }

                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.3f))
                            .clickable { onBackgroundClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.CameraAlt, "Change Background", tint = Color.White, modifier = Modifier.size(32.dp))
                            Text("Change Background", color = Color.White, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .offset(y = (-40).dp)
                            .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                            .clip(CircleShape)
                            .background(NestifySurface)
                    ) {
                        if (state.avatarUrl.isNotEmpty()) {
                            AsyncImage(
                                model = state.avatarUrl,
                                contentDescription = "Profile",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    Icons.Default.Person,
                                    "Profile",
                                    modifier = Modifier.size(48.dp),
                                    tint = NestifySlate.copy(alpha = 0.4f)
                                )
                            }
                        }
                    }

                    if (isEditing) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .offset(y = (-40).dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.3f))
                                .clickable { onAvatarClick() },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.CameraAlt, "Change Photo", tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }

                Spacer(Modifier.width(12.dp))


                Column(modifier = Modifier.weight(1f).offset(y = (-40).dp)) {


                    if (isEditing) {
                        OutlinedTextField(
                            value = state.name,
                            onValueChange = onNameChange,
                            label = { Text("Name") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold)
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = state.jobTitle,
                            onValueChange = onJobTitleChange,
                            label = { Text("Profession") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            textStyle = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(4.dp))
                        OutlinedTextField(
                            value = state.location,
                            onValueChange = onLocationChange,
                            label = { Text("Location") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            leadingIcon = { Icon(Icons.Default.LocationOn, null, modifier = Modifier.size(16.dp)) },
                            textStyle = MaterialTheme.typography.bodySmall
                        )
                    } else {

                        Text(
                            text = state.name.ifEmpty { "Your Name" },
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = NestifySlate
                        )

                        Text(
                            text = state.jobTitle.ifEmpty { "Profession" },
                            style = MaterialTheme.typography.titleMedium,
                            color = NestifySlate.copy(alpha = 0.8f)
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = NestifySlate.copy(alpha = 0.6f)
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = state.location.ifEmpty { "Location" },
                                style = MaterialTheme.typography.bodySmall,
                                color = NestifySlate.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AboutMeSection(bio: String, isEditing: Boolean, onBioChange: (String) -> Unit) {
    SectionCard(title = "About Me") {
        if (isEditing) {
            OutlinedTextField(
                value = bio,
                onValueChange = onBioChange,
                label = { Text("About Me") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 100.dp),
                textStyle = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = bio.ifEmpty { "No bio added yet." },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp
            )
        }
    }
}

@Composable
fun SkillsSection(
    skills: List<String>,
    newSkill: String,
    isEditing: Boolean,
    onNewSkillChange: (String) -> Unit,
    onAddSkill: () -> Unit,
    onRemoveSkill: (String) -> Unit
) {
    SectionCard(title = "Technical Expertise") {
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = newSkill,
                    onValueChange = onNewSkillChange,
                    label = { Text("Add skill") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onAddSkill() })
                )
                Spacer(Modifier.width(8.dp))
                FilledTonalIconButton(onClick = onAddSkill) {
                    Icon(Icons.Default.Add, "Add")
                }
            }
            Spacer(Modifier.height(8.dp))
        }

        if (skills.isEmpty() && !isEditing) {
            Text("No skills added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(skills) { skill ->
                    if (isEditing) {
                        InputChip(
                            selected = false,
                            onClick = { onRemoveSkill(skill) },
                            label = { Text(skill) },
                            trailingIcon = {
                                Icon(Icons.Default.Close, "Remove", modifier = Modifier.size(16.dp))
                            },
                            colors = InputChipDefaults.inputChipColors(
                                containerColor = NestifySkyBlue.copy(alpha = 0.2f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NestifySkyBlue.copy(alpha = 0.5f))
                        )
                    } else {
                        AssistChip(
                            onClick = {},
                            label = { Text(skill) },
                            colors = AssistChipDefaults.assistChipColors(
                                labelColor = NestifySlate,
                                containerColor = NestifySkyBlue.copy(alpha = 0.2f)
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, NestifySkyBlue.copy(alpha = 0.5f))
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ExperienceSection(
    experiences: List<ExperienceData>,
    isEditing: Boolean,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    SectionCard(title = "Experience") {
        if (isEditing) {
            FilledTonalButton(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Experience")
            }
            Spacer(Modifier.height(8.dp))
        }

        if (experiences.isEmpty() && !isEditing) {
            Text("No experience added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        experiences.forEachIndexed { index, exp ->
            if (isEditing) {
                ExperienceEditItem(exp = exp, onEdit = { onEdit(index) }, onRemove = { onRemove(index) })
            } else {
                ExperienceItem(exp = exp)
            }
            if (index < experiences.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 12.dp),
                    color = MaterialTheme.colorScheme.outlineVariant
                )
            }
        }
    }
}

@Composable
fun ExperienceItem(exp: ExperienceData) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(NestifySkyBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.Work, contentDescription = null, tint = NestifySlate, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(16.dp))
        Column {
            Text(exp.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Text(exp.company, style = MaterialTheme.typography.bodyMedium, color = NestifySlate.copy(alpha = 0.7f))
            Text(exp.duration, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Spacer(Modifier.height(4.dp))
            Text(exp.description, style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun ExperienceEditItem(exp: ExperienceData, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = NestifySurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(NestifySkyBlue.copy(alpha = 0.2f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Work, null, tint = NestifySlate, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(exp.title.ifEmpty { "Title" }, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(exp.company.ifEmpty { "Company" }, style = MaterialTheme.typography.bodySmall, color = NestifySlate.copy(alpha = 0.7f))
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ProjectsSection(
    projects: List<ProjectData>,
    isEditing: Boolean,
    onAdd: () -> Unit,
    onEdit: (Int) -> Unit,
    onRemove: (Int) -> Unit
) {
    SectionCard(title = "Featured Products") {
        if (isEditing) {
            FilledTonalButton(
                onClick = onAdd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(4.dp))
                Text("Add Project")
            }
            Spacer(Modifier.height(8.dp))
        }

        if (projects.isEmpty() && !isEditing) {
            Text("No projects added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        projects.forEachIndexed { index, project ->
            if (isEditing) {
                ProjectEditItem(project = project, onEdit = { onEdit(index) }, onRemove = { onRemove(index) })
            } else {
                ProjectItem(project = project)
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@Composable
fun ProjectItem(project: ProjectData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = NestifySurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(NestifyPeach.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Launch, contentDescription = null, tint = NestifySlate)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, fontWeight = FontWeight.Bold)
                Text(project.description, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
        }
    }
}

@Composable
fun ProjectEditItem(project: ProjectData, onEdit: () -> Unit, onRemove: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = NestifySurface),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(NestifyPeach.copy(alpha = 0.3f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Launch, null, tint = NestifySlate)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name.ifEmpty { "Project Name" }, fontWeight = FontWeight.Bold)
                Text(project.description.ifEmpty { "Description" }, style = MaterialTheme.typography.bodySmall, maxLines = 1)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, "Edit", modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, "Remove", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ConnectSection(
    email: String, linkedin: String, github: String,
    website: String, twitter: String, youtube: String, instagram: String,
    isEditing: Boolean,
    onEmailChange: (String) -> Unit, onLinkedinChange: (String) -> Unit,
    onGithubChange: (String) -> Unit, onWebsiteChange: (String) -> Unit,
    onTwitterChange: (String) -> Unit, onYoutubeChange: (String) -> Unit,
    onInstagramChange: (String) -> Unit
) {
    SectionCard(title = "Connect") {
        if (isEditing) {
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Email, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = linkedin,
                onValueChange = onLinkedinChange,
                label = { Text("LinkedIn URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Link, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = github,
                onValueChange = onGithubChange,
                label = { Text("GitHub URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Code, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = website,
                onValueChange = onWebsiteChange,
                label = { Text("Website URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Public, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = twitter,
                onValueChange = onTwitterChange,
                label = { Text("Twitter / X URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.AlternateEmail, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = youtube,
                onValueChange = onYoutubeChange,
                label = { Text("YouTube URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.PlayArrow, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = instagram,
                onValueChange = onInstagramChange,
                label = { Text("Instagram URL") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.CameraAlt, null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
            )
        } else {
            val links = listOfNotNull(
                email.takeIf { it.isNotEmpty() }?.let { "Email" to it to Icons.Default.Email },
                linkedin.takeIf { it.isNotEmpty() }?.let { "LinkedIn" to it to Icons.Default.Link },
                github.takeIf { it.isNotEmpty() }?.let { "GitHub" to it to Icons.Default.Code },
                website.takeIf { it.isNotEmpty() }?.let { "Website" to it to Icons.Default.Public },
                twitter.takeIf { it.isNotEmpty() }?.let { "Twitter" to it to Icons.Default.AlternateEmail },
                youtube.takeIf { it.isNotEmpty() }?.let { "YouTube" to it to Icons.Default.PlayArrow },
                instagram.takeIf { it.isNotEmpty() }?.let { "Instagram" to it to Icons.Default.CameraAlt }
            )

            if (links.isEmpty()) {
                Text("No links added yet.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(links) { (pair, icon) ->
                        val (label, url) = pair
                        SocialIconButton(icon = icon, label = label, url = url)
                    }
                }
            }
        }
    }
}

@Composable
fun SocialIconButton(icon: ImageVector, label: String, url: String) {
    val context = LocalContext.current
    val intentUrl = if (label == "Email") "mailto:$url" else url

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(1.dp, NestifySlate.copy(alpha = 0.1f), CircleShape)
                .clip(CircleShape)
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = NestifySlate, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        border = borderStroke()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = NestifySlate,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            content()
        }
    }
}

@Composable
fun ExperienceDialog(
    experience: ExperienceData,
    onExperienceChange: (ExperienceData) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Experience") },
        text = {
            Column {
                OutlinedTextField(
                    value = experience.title,
                    onValueChange = { onExperienceChange(experience.copy(title = it)) },
                    label = { Text("Title") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = experience.company,
                    onValueChange = { onExperienceChange(experience.copy(company = it)) },
                    label = { Text("Company") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = experience.duration,
                    onValueChange = { onExperienceChange(experience.copy(duration = it)) },
                    label = { Text("Duration (e.g. 2022 - Present)") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = experience.description,
                    onValueChange = { onExperienceChange(experience.copy(description = it)) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 80.dp)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun ProjectDialog(
    project: ProjectData,
    onProjectChange: (ProjectData) -> Unit,
    onSave: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Project") },
        text = {
            Column {
                OutlinedTextField(
                    value = project.name,
                    onValueChange = { onProjectChange(project.copy(name = it)) },
                    label = { Text("Project Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = project.description,
                    onValueChange = { onProjectChange(project.copy(description = it)) },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth().heightIn(min = 60.dp)
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = project.link,
                    onValueChange = { onProjectChange(project.copy(link = it)) },
                    label = { Text("Project URL") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onSave) { Text("Save") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, NestifySlate.copy(alpha = 0.05f))
