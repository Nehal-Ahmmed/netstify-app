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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.nhbhuiyan.nestify.domain.model.ExperienceData
import com.nhbhuiyan.nestify.domain.model.ProjectData
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnSize
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.BtnVariant
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconButtonChrome
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.IconTile
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NButton
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyAppBar
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyInput
import com.nhbhuiyan.nestify.ui.theme.NestifyGradients
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme
import com.nhbhuiyan.nestify.ui.theme.Radii
import com.nhbhuiyan.nestify.ui.theme.Space

@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val c = NestifyTheme.colors
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(c.canvas)
    ) {
        NestifyAppBar(
            title = if (state.isEditing) "Edit Profile" else "Profile",
            onBack = { navController.popBackStack() },
            trailing = {
                if (state.isEditing) {
                    NButton(
                        label = "Cancel",
                        onClick = { viewModel.cancelEditing() },
                        variant = BtnVariant.Ghost,
                        size = BtnSize.Sm
                    )
                }
                IconButtonChrome(
                    icon = if (state.isEditing) Icons.Default.Check else Icons.Default.Edit,
                    onClick = { viewModel.toggleEditing() },
                    tint = if (state.isEditing) c.brand else c.ink,
                    contentDescription = if (state.isEditing) "Save" else "Edit"
                )
            }
        )

        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = c.brand)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = Space.xxl),
                verticalArrangement = Arrangement.spacedBy(Space.l)
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
    val c = NestifyTheme.colors
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
                            .background(NestifyGradients.brandWash())
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
                            Text("Change Background", color = Color.White, style = NestifyTheme.type.meta)
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = Space.l, end = Space.l, top = Space.s),
                verticalAlignment = Alignment.Bottom
            ) {
                Box {
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .offset(y = (-40).dp)
                            .border(4.dp, c.surface, CircleShape)
                            .clip(CircleShape)
                            .background(c.surface2)
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
                                    tint = c.ink30
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
                        NestifyInput(
                            value = state.name,
                            onValueChange = onNameChange,
                            label = "Name",
                            placeholder = "Your Name"
                        )
                        Spacer(Modifier.height(Space.s))
                        NestifyInput(
                            value = state.jobTitle,
                            onValueChange = onJobTitleChange,
                            label = "Profession",
                            placeholder = "Profession"
                        )
                        Spacer(Modifier.height(Space.s))
                        NestifyInput(
                            value = state.location,
                            onValueChange = onLocationChange,
                            label = "Location",
                            placeholder = "Location",
                            leadingIcon = Icons.Default.LocationOn
                        )
                    } else {
                        Text(
                            text = state.name.ifEmpty { "Your Name" },
                            style = NestifyTheme.type.h2Serif,
                            color = c.ink
                        )
                        Text(
                            text = state.jobTitle.ifEmpty { "Profession" },
                            style = NestifyTheme.type.label.copy(fontWeight = FontWeight.Medium),
                            color = c.ink70
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.LocationOn,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = c.ink50
                            )
                            Spacer(Modifier.width(4.dp))
                            Text(
                                text = state.location.ifEmpty { "Location" },
                                style = NestifyTheme.type.meta,
                                color = c.ink50
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
    val c = NestifyTheme.colors
    SectionCard(title = "About Me") {
        if (isEditing) {
            NestifyInput(
                value = bio,
                onValueChange = onBioChange,
                placeholder = "Tell people about yourself…"
            )
        } else {
            Text(
                text = bio.ifEmpty { "No bio added yet." },
                style = NestifyTheme.type.body,
                color = c.ink70,
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
    val c = NestifyTheme.colors
    SectionCard(title = "Technical Expertise") {
        if (isEditing) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                NestifyInput(
                    value = newSkill,
                    onValueChange = onNewSkillChange,
                    placeholder = "Add skill",
                    modifier = Modifier.weight(1f)
                )
                Spacer(Modifier.width(Space.s))
                IconTile(
                    icon = Icons.Default.Add,
                    modifier = Modifier.clip(Radii.s).clickable { onAddSkill() }
                )
            }
            Spacer(Modifier.height(Space.m))
        }

        if (skills.isEmpty() && !isEditing) {
            Text("No skills added yet.", style = NestifyTheme.type.body, color = c.ink50)
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(Space.s),
                contentPadding = PaddingValues(vertical = 4.dp)
            ) {
                items(skills) { skill ->
                    if (isEditing) {
                        Chip(
                            label = skill,
                            tone = ChipTone.Soft,
                            leadingIcon = Icons.Default.Close,
                            onClick = { onRemoveSkill(skill) }
                        )
                    } else {
                        Chip(label = skill, tone = ChipTone.Soft)
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
    val c = NestifyTheme.colors
    SectionCard(title = "Experience") {
        if (isEditing) {
            NButton(
                label = "Add Experience",
                onClick = onAdd,
                variant = BtnVariant.Soft,
                full = true,
                leadingIcon = Icons.Default.Add
            )
            Spacer(Modifier.height(Space.m))
        }

        if (experiences.isEmpty() && !isEditing) {
            Text("No experience added yet.", style = NestifyTheme.type.body, color = c.ink50)
        }

        experiences.forEachIndexed { index, exp ->
            if (isEditing) {
                ExperienceEditItem(exp = exp, onEdit = { onEdit(index) }, onRemove = { onRemove(index) })
            } else {
                ExperienceItem(exp = exp)
            }
            if (index < experiences.size - 1) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp)
                        .height(1.dp)
                        .background(c.hair)
                )
            }
        }
    }
}

@Composable
fun ExperienceItem(exp: ExperienceData) {
    val c = NestifyTheme.colors
    Row(modifier = Modifier.fillMaxWidth()) {
        IconTile(icon = Icons.Default.Work, background = c.brandSoft, tint = c.brand)
        Spacer(Modifier.width(Space.l))
        Column {
            Text(exp.title, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
            Text(exp.company, style = NestifyTheme.type.body, color = c.ink70)
            Text(exp.duration, style = NestifyTheme.type.meta, color = c.ink50)
            Spacer(Modifier.height(4.dp))
            Text(exp.description, style = NestifyTheme.type.body, color = c.ink70)
        }
    }
}

@Composable
fun ExperienceEditItem(exp: ExperienceData, onEdit: () -> Unit, onRemove: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        padding = Space.m,
        background = c.surface2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = Icons.Default.Work, background = c.brandSoft, tint = c.brand)
            Spacer(Modifier.width(Space.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(exp.title.ifEmpty { "Title" }, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Text(exp.company.ifEmpty { "Company" }, style = NestifyTheme.type.meta, color = c.ink70)
            }
            IconButtonChrome(Icons.Default.Edit, onEdit, tint = c.ink, contentDescription = "Edit")
            IconButtonChrome(Icons.Default.Delete, onRemove, tint = c.coral, contentDescription = "Remove")
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
    val c = NestifyTheme.colors
    SectionCard(title = "Featured Products") {
        if (isEditing) {
            NButton(
                label = "Add Project",
                onClick = onAdd,
                variant = BtnVariant.Soft,
                full = true,
                leadingIcon = Icons.Default.Add
            )
            Spacer(Modifier.height(Space.m))
        }

        if (projects.isEmpty() && !isEditing) {
            Text("No projects added yet.", style = NestifyTheme.type.body, color = c.ink50)
        }

        projects.forEachIndexed { index, project ->
            if (isEditing) {
                ProjectEditItem(project = project, onEdit = { onEdit(index) }, onRemove = { onRemove(index) })
            } else {
                ProjectItem(project = project)
            }
            Spacer(modifier = Modifier.height(Space.s))
        }
    }
}

@Composable
fun ProjectItem(project: ProjectData) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier.fillMaxWidth(),
        padding = Space.m,
        background = c.surface2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = Icons.Default.Launch, size = 48.dp, background = c.coralSoft, tint = c.coral)
            Spacer(Modifier.width(Space.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Text(project.description, style = NestifyTheme.type.meta, color = c.ink70, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

@Composable
fun ProjectEditItem(project: ProjectData, onEdit: () -> Unit, onRemove: () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        padding = Space.m,
        background = c.surface2
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon = Icons.Default.Launch, size = 48.dp, background = c.coralSoft, tint = c.coral)
            Spacer(Modifier.width(Space.m))
            Column(modifier = Modifier.weight(1f)) {
                Text(project.name.ifEmpty { "Project Name" }, style = NestifyTheme.type.label.copy(fontWeight = FontWeight.SemiBold), color = c.ink)
                Text(project.description.ifEmpty { "Description" }, style = NestifyTheme.type.meta, color = c.ink70, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            IconButtonChrome(Icons.Default.Edit, onEdit, tint = c.ink, contentDescription = "Edit")
            IconButtonChrome(Icons.Default.Delete, onRemove, tint = c.coral, contentDescription = "Remove")
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
    val c = NestifyTheme.colors
    SectionCard(title = "Connect") {
        if (isEditing) {
            Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                NestifyInput(email, onEmailChange, label = "Email", placeholder = "you@example.com", leadingIcon = Icons.Default.Email)
                NestifyInput(linkedin, onLinkedinChange, label = "LinkedIn URL", placeholder = "https://…", leadingIcon = Icons.Default.Link)
                NestifyInput(github, onGithubChange, label = "GitHub URL", placeholder = "https://…", leadingIcon = Icons.Default.Code)
                NestifyInput(website, onWebsiteChange, label = "Website URL", placeholder = "https://…", leadingIcon = Icons.Default.Public)
                NestifyInput(twitter, onTwitterChange, label = "Twitter / X URL", placeholder = "https://…", leadingIcon = Icons.Default.AlternateEmail)
                NestifyInput(youtube, onYoutubeChange, label = "YouTube URL", placeholder = "https://…", leadingIcon = Icons.Default.PlayArrow)
                NestifyInput(instagram, onInstagramChange, label = "Instagram URL", placeholder = "https://…", leadingIcon = Icons.Default.CameraAlt)
            }
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
                Text("No links added yet.", style = NestifyTheme.type.body, color = c.ink50)
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(Space.s)
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
    val c = NestifyTheme.colors
    val context = LocalContext.current
    val intentUrl = if (label == "Email") "mailto:$url" else url

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .border(1.dp, c.hair, CircleShape)
                .clip(CircleShape)
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl))
                        context.startActivity(intent)
                    } catch (_: Exception) {}
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = c.ink, modifier = Modifier.size(22.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = NestifyTheme.type.meta, color = c.ink70, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}

@Composable
fun SectionCard(title: String, content: @Composable () -> Unit) {
    val c = NestifyTheme.colors
    NestifyCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = Space.l),
        padding = Space.l
    ) {
        Column {
            Text(
                text = title,
                style = NestifyTheme.type.h3Serif,
                color = c.ink,
                modifier = Modifier.padding(bottom = Space.m)
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
    val c = NestifyTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        titleContentColor = c.ink,
        title = { Text("Experience", style = NestifyTheme.type.h3Serif, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                NestifyInput(experience.title, { onExperienceChange(experience.copy(title = it)) }, label = "Title")
                NestifyInput(experience.company, { onExperienceChange(experience.copy(company = it)) }, label = "Company")
                NestifyInput(experience.duration, { onExperienceChange(experience.copy(duration = it)) }, label = "Duration (e.g. 2022 - Present)")
                NestifyInput(experience.description, { onExperienceChange(experience.copy(description = it)) }, label = "Description")
            }
        },
        confirmButton = {
            NButton(label = "Save", onClick = onSave, size = BtnSize.Sm)
        },
        dismissButton = {
            NButton(label = "Cancel", onClick = onDismiss, variant = BtnVariant.Ghost, size = BtnSize.Sm)
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
    val c = NestifyTheme.colors
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = c.surface,
        titleContentColor = c.ink,
        title = { Text("Project", style = NestifyTheme.type.h3Serif, color = c.ink) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(Space.s)) {
                NestifyInput(project.name, { onProjectChange(project.copy(name = it)) }, label = "Project Name")
                NestifyInput(project.description, { onProjectChange(project.copy(description = it)) }, label = "Description")
                NestifyInput(project.link, { onProjectChange(project.copy(link = it)) }, label = "Project URL")
            }
        },
        confirmButton = {
            NButton(label = "Save", onClick = onSave, size = BtnSize.Sm)
        },
        dismissButton = {
            NButton(label = "Cancel", onClick = onDismiss, variant = BtnVariant.Ghost, size = BtnSize.Sm)
        }
    )
}
