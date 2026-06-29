# 📝 Daily Notes Frontend Revamp Guide

## 🗺️ High-Level Roadmap

1. **OpenCV Memory Cache (`DynamicUserFrameNotebook.kt`)**: Added an in-memory `ConcurrentHashMap` caching layer to `DynamicUserFrameNotebook`. This prevents the OpenCV engine from recalculating the text bounding boxes repeatedly when scrolling through a long list of notes.
2. **Read-Only Mode (`DynamicUserFrameNotebook.kt`)**: Expanded the Jetpack Compose component to accept a `readOnly` parameter so it can be used seamlessly in both the editing screen and the list preview screens.
3. **Beautiful Grid View (`NotesListScreen.kt`)**: Completely purged the old Material Design cards and replaced them with a responsive grid displaying the beautiful organic canvas image (`a2y6_21s0_220127.jpg`) dynamically loaded with the user's note contents perfectly aligned via OpenCV.
4. **Immersive Creation Experience (`CreateNoteScreen.kt`)**: Replaced the traditional title and content forms with an immersive, full-screen interactive sticky note canvas.

---

## 🧠 Logical Descriptions

### Frontend Layer (Jetpack Compose & OpenCV)
- **Simple Description**: We turned the boring list of notes into a beautiful wall of sticky notes! The app now uses your awesome green canvas picture as the background everywhere in the Daily Notes section. OpenCV finds exactly where the text should go so the notes always look perfect.
- **Technical Description**: The `DynamicUserFrameNotebook` was refactored to support `readOnly` states, removing placeholder text and disabling input when displayed in `NotesListScreen`. A `WritingSpaceCache` caches the `DetectedWritingArea` using the `Bitmap`'s hashCode, reducing CPU overhead during `LazyColumn` recompositions. `CreateNoteScreen` automatically generates a Title string under the hood to satisfy the data models without breaking the clean UI immersion.

### Backend Layer (Database & Logic)
- **Simple vs Technical**: *No backend changes were made as per explicit user instructions.* The data flow relies entirely on the pre-existing `NotesViewModel` and `CreateNoteViewModel`.

---

## 💻 Full Implementation Code

### 1. `DynamicUserFrameNotebook.kt` (Updated Component)
```kotlin
package com.nhbhuiyan.nestify.presentation.ui.components

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc
import java.util.concurrent.ConcurrentHashMap

data class DetectedWritingArea(
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float
)

object WritingSpaceCache {
    private val cache = ConcurrentHashMap<Int, DetectedWritingArea>()
    
    fun getArea(bitmap: Bitmap): DetectedWritingArea {
        val hash = bitmap.hashCode()
        return cache.getOrPut(hash) {
            detectWritingSpace(bitmap)
        }
    }
}

fun detectWritingSpace(bitmap: Bitmap): DetectedWritingArea {
    // OpenCV logic (omitted for brevity, see source file for full code)
    // Same as previously implemented but now cached!
    val src = Mat()
    val gray = Mat()
    val blurred = Mat()
    val thresh = Mat()
    
    try {
        Utils.bitmapToMat(bitmap, src)
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.GaussianBlur(gray, blurred, org.opencv.core.Size(5.0, 5.0), 0.0)
        Imgproc.adaptiveThreshold(blurred, thresh, 255.0, Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, Imgproc.THRESH_BINARY_INV, 11, 2.0)
        
        val contours = ArrayList<MatOfPoint>()
        val hierarchy = Mat()
        Imgproc.findContours(thresh, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        hierarchy.release()
        
        var maxArea = 0.0
        var bestRect: Rect? = null
        val imgArea = src.cols() * src.rows()
        
        for (contour in contours) {
            val contour2f = MatOfPoint2f(*contour.toArray())
            val approx = MatOfPoint2f()
            val peri = Imgproc.arcLength(contour2f, true)
            Imgproc.approxPolyDP(contour2f, approx, 0.02 * peri, true)
            
            if (approx.rows() >= 4) {
                val area = Imgproc.contourArea(contour)
                if (area > maxArea && area < imgArea * 0.95) {
                    maxArea = area
                    bestRect = Imgproc.boundingRect(contour)
                }
            }
        }
        
        if (bestRect == null) return DetectedWritingArea(0.1f, 0.1f, 0.8f, 0.8f)
        
        return DetectedWritingArea(
            xPercent = bestRect.x.toFloat() / src.cols().toFloat(),
            yPercent = bestRect.y.toFloat() / src.rows().toFloat(),
            widthPercent = bestRect.width.toFloat() / src.cols().toFloat(),
            heightPercent = bestRect.height.toFloat() / src.rows().toFloat()
        )
    } finally {
        src.release(); gray.release(); blurred.release(); thresh.release()
    }
}

@Composable
fun DynamicUserFrameNotebook(
    frameBitmap: Bitmap,
    text: String,
    onTextChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    textStyle: TextStyle = TextStyle(
        color = Color(0xFF2C3E50),
        fontSize = 18.sp,
        fontFamily = FontFamily.Cursive
    )
) {
    val detectedArea = remember(frameBitmap) { WritingSpaceCache.getArea(frameBitmap) }
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxX = maxWidth * detectedArea.xPercent
        val boxY = maxHeight * detectedArea.yPercent
        val boxWidth = maxWidth * detectedArea.widthPercent
        val boxHeight = maxHeight * detectedArea.heightPercent
        
        Image(
            bitmap = frameBitmap.asImageBitmap(),
            contentDescription = "Notebook Frame",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        Box(
            modifier = Modifier
                .offset(x = boxX, y = boxY)
                .size(width = boxWidth, height = boxHeight)
                .padding(8.dp)
        ) {
            BasicTextField(
                value = text,
                onValueChange = onTextChange,
                modifier = Modifier.fillMaxSize(),
                readOnly = readOnly,
                textStyle = textStyle,
                decorationBox = { innerTextField ->
                    if (text.isEmpty() && !readOnly) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                            Text(text = "Write here...", color = Color.Gray.copy(alpha = 0.5f), style = textStyle)
                            innerTextField()
                        }
                    } else {
                        innerTextField()
                    }
                }
            )
        }
    }
}
```

*(Note: `NotesListScreen.kt` and `CreateNoteScreen.kt` were completely updated to consume this new component. Full code is omitted from this summary for brevity, but they successfully strip the old Material cards and replace them with `DynamicUserFrameNotebook` grids.)*

---

## 🛠️ Extra Steps

- **No backend tasks run.** You may proceed to customize the database logic for notes safely as planned.
- **Image Requirements**: Ensure `a2y6_21s0_220127.jpg` remains in `res/drawable/`.

---

## 📝 Summary
The workflow completely transformed the aesthetic of the Daily Notes section. The list screen fetches notes from the ViewModel and renders each `content` string tightly within a cached, OpenCV-calculated bounding box over the organic canvas image. The creation screen provides a massive version of this exact same canvas. When saved, the string is passed into the pre-existing ViewModel pipeline.
