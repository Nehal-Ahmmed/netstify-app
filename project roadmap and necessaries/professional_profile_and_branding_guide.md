# 👤 Professional Profile & Branding Guide

This guide documents the implementation of the comprehensive, responsive, and fully editable Profile section in Nestify.

## 🗺️ High-Level Roadmap

1.  **Database Layer**: Verified/Enhanced `ProfileEntity` in Room to store all branding fields (images, bio, social links, experience, projects).
2.  **ViewModel Architecture**: Implemented `ProfileViewModel` to manage UI state, handle image picking/saving, and perform CRUD operations via `ProfileRepository`.
3.  **Responsive UI Design**: 
    - Re-designed `ProfileHeader` with dynamic background and avatar image support.
    - Implemented `SectionCard` pattern for modular profile components.
    - Created interactive `ConnectSection` with browser redirection for social links.
4.  **Editability**: Implemented a "Toggle Edit" mode that transforms read-only text into interactive `OutlinedTextFields` and dialogs.
5.  **Image Persistence**: Implemented internal storage copying for picked images to ensure URLs remain valid across sessions.

## 🧠 Logical Descriptions

### Backend (Data Persistence)
- **Room Persistence**: The `ProfileEntity` acts as a single-row configuration table (ID=1) storing basic info and JSON-serialized lists for Experience and Projects.
- **Repository Pattern**: `ProfileRepository` abstracts the data source, providing clean `getProfile()` and `saveProfile()` methods.
- **Image Management**: Picked images are copied to `context.filesDir` to prevent "permission denied" errors that occur when referencing external gallery URIs later.

### Frontend (User Interface)
- **LazyColumn Layout**: Ensures the profile remains scrollable and responsive across different screen sizes.
- **Adaptive Components**:
    - `ProfileHeader`: Uses `AsyncImage` for high-performance image loading and mesh gradients as fallbacks.
    - `ExperienceSection` & `ProjectsSection`: Feature dynamic adding/editing via custom dialogs.
    - `ConnectSection`: Maps social platform labels to specific `Intent` actions (View URL or Send Email).

## 💻 Full Implementation Code

### ProfileEntity.kt
```kotlin
@Entity(tableName = "profile_table")
data class ProfileEntity(
    @PrimaryKey val id: Long = 1L,
    val name: String = "",
    val jobTitle: String = "",
    val location: String = "",
    val bio: String = "",
    val avatarUrl: String = "",
    val backgroundUrl: String = "",
    val skills: String = "", // Comma-separated or JSON
    val experiencesJson: String = "[]",
    val projectsJson: String = "[]",
    val email: String = "",
    val linkedin: String = "",
    val github: String = "",
    val website: String = "",
    val twitter: String = "",
    val youtube: String = "",
    val instagram: String = ""
)
```

### ProfileScreen.kt (Social Redirection)
```kotlin
@Composable
fun SocialIconButton(icon: ImageVector, label: String, url: String) {
    val context = LocalContext.current
    val intentUrl = if (label == "Email") "mailto:$url" else url

    Box(
        modifier = Modifier.clickable {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(intentUrl))
                context.startActivity(intent)
            } catch (_: Exception) {}
        }
    ) {
        // ... Icon UI ...
    }
}
```

## 🛠️ Extra Steps

1.  **File Permissions**: Ensure `READ_EXTERNAL_STORAGE` is requested if needed for older Android versions, though `ActivityResultContracts.GetContent()` handles most cases.
2.  **Serialization**: The `Converters.kt` file must handle the conversion between `List<ExperienceData>` and the `experiencesJson` String in the database.
3.  **Optimization**: Consider using a blurred version of the background image for faster initial loading.

## 📝 Summary

1.  **User** opens the Profile screen.
2.  **ViewModel** fetches the single `ProfileEntity` and maps it to `ProfileUiState`.
3.  **UI** renders the data using custom branding components.
4.  **User** clicks "Edit", modifies fields (including picking new images), and clicks "Save".
5.  **ViewModel** copies images to local storage, updates the `ProfileEntity`, and refreshes the state.
6.  **User** clicks on a social link, triggering a system `Intent` to open the URL in a browser.

Style: Detailed documentation with copy-pasteable snippets and clear data flow descriptions. ✨
