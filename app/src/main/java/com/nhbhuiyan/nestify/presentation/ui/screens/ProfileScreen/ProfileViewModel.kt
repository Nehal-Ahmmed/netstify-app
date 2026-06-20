package com.nhbhuiyan.nestify.presentation.ui.screens.ProfileScreen

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nhbhuiyan.nestify.domain.model.ExperienceData
import com.nhbhuiyan.nestify.domain.model.ProjectData
import com.nhbhuiyan.nestify.domain.model.UserProfile
import com.nhbhuiyan.nestify.domain.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

data class ProfileUiState(
    val isLoading: Boolean = true,
    val isEditing: Boolean = false,
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = "",
    val skills: List<String> = emptyList(),
    val experiences: List<ExperienceData> = emptyList(),
    val projects: List<ProjectData> = emptyList(),
    val email: String = "",
    val linkedin: String = "",
    val github: String = "",
    val website: String = "",
    val twitter: String = "",
    val youtube: String = "",
    val instagram: String = "",
    val newSkill: String = ""
)

enum class ImagePickerTarget { NONE, AVATAR, BACKGROUND }

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileUiState())
    val state: StateFlow<ProfileUiState> = _state.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            val profile = profileRepository.getProfile()
            if (profile != null) {
                _state.value = ProfileUiState(
                    isLoading = false,
                    name = profile.name,
                    jobTitle = profile.jobTitle,
                    location = profile.location,
                    bio = profile.bio,
                    avatarUrl = profile.avatarUrl,
                    backgroundUrl = profile.backgroundUrl,
                    skills = profile.skills,
                    experiences = profile.experiences,
                    projects = profile.projects,
                    email = profile.email,
                    linkedin = profile.linkedin,
                    github = profile.github,
                    website = profile.website,
                    twitter = profile.twitter,
                    youtube = profile.youtube,
                    instagram = profile.instagram
                )
            } else {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    fun toggleEditing() {
        val current = _state.value
        if (current.isEditing) {
            saveProfile()
        }
        _state.value = current.copy(isEditing = !current.isEditing)
    }

    fun updateName(value: String) { _state.value = _state.value.copy(name = value) }
    fun updateJobTitle(value: String) { _state.value = _state.value.copy(jobTitle = value) }
    fun updateLocation(value: String) { _state.value = _state.value.copy(location = value) }
    fun updateBio(value: String) { _state.value = _state.value.copy(bio = value) }
    fun updateEmail(value: String) { _state.value = _state.value.copy(email = value) }
    fun updateLinkedin(value: String) { _state.value = _state.value.copy(linkedin = value) }
    fun updateGithub(value: String) { _state.value = _state.value.copy(github = value) }
    fun updateWebsite(value: String) { _state.value = _state.value.copy(website = value) }
    fun updateTwitter(value: String) { _state.value = _state.value.copy(twitter = value) }
    fun updateYoutube(value: String) { _state.value = _state.value.copy(youtube = value) }
    fun updateInstagram(value: String) { _state.value = _state.value.copy(instagram = value) }
    fun updateNewSkill(value: String) { _state.value = _state.value.copy(newSkill = value) }

    fun addSkill() {
        val current = _state.value
        val trimmed = current.newSkill.trim()
        if (trimmed.isNotEmpty() && trimmed !in current.skills) {
            _state.value = current.copy(
                skills = current.skills + trimmed,
                newSkill = ""
            )
        }
    }

    fun removeSkill(skill: String) {
        val current = _state.value
        _state.value = current.copy(skills = current.skills - skill)
    }

    fun addExperience(exp: ExperienceData) {
        val current = _state.value
        _state.value = current.copy(experiences = current.experiences + exp)
    }

    fun updateExperience(index: Int, exp: ExperienceData) {
        val current = _state.value
        val list = current.experiences.toMutableList()
        if (index in list.indices) {
            list[index] = exp
            _state.value = current.copy(experiences = list)
        }
    }

    fun removeExperience(index: Int) {
        val current = _state.value
        val list = current.experiences.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _state.value = current.copy(experiences = list)
        }
    }

    fun addProject(project: ProjectData) {
        val current = _state.value
        _state.value = current.copy(projects = current.projects + project)
    }

    fun updateProject(index: Int, project: ProjectData) {
        val current = _state.value
        val list = current.projects.toMutableList()
        if (index in list.indices) {
            list[index] = project
            _state.value = current.copy(projects = list)
        }
    }

    fun removeProject(index: Int) {
        val current = _state.value
        val list = current.projects.toMutableList()
        if (index in list.indices) {
            list.removeAt(index)
            _state.value = current.copy(projects = list)
        }
    }

    fun handleImageResult(context: Context, uri: Uri, target: ImagePickerTarget) {
        viewModelScope.launch {
            val savedPath = copyUriToInternalStorage(context, uri)
            if (savedPath != null) {
                when (target) {
                    ImagePickerTarget.AVATAR -> _state.value = _state.value.copy(avatarUrl = savedPath)
                    ImagePickerTarget.BACKGROUND -> _state.value = _state.value.copy(backgroundUrl = savedPath)
                    else -> {}
                }
            }
        }
    }

    private fun copyUriToInternalStorage(context: Context, uri: Uri): String? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val fileName = "profile_${System.currentTimeMillis()}.jpg"
            val file = File(context.filesDir, fileName)
            file.outputStream().use { output -> inputStream.copyTo(output) }
            inputStream.close()
            file.absolutePath
        } catch (_: Exception) { null }
    }

    private fun saveProfile() {
        viewModelScope.launch {
            val s = _state.value
            val profile = UserProfile(
                name = s.name,
                jobTitle = s.jobTitle,
                location = s.location,
                bio = s.bio,
                avatarUrl = s.avatarUrl,
                backgroundUrl = s.backgroundUrl,
                skills = s.skills,
                experiences = s.experiences,
                projects = s.projects,
                email = s.email,
                linkedin = s.linkedin,
                github = s.github,
                website = s.website,
                twitter = s.twitter,
                youtube = s.youtube,
                instagram = s.instagram
            )
            profileRepository.saveProfile(profile)
        }
    }

    fun cancelEditing() {
        loadProfile()
        _state.value = _state.value.copy(isEditing = false)
    }
}
