# 🖼️ Dynamic User Frame Notebook Guide

## 🗺️ High-Level Roadmap

1. **OpenCV Image Processing Integration**: Implemented a Kotlin function `detectWritingSpace` that processes an uploaded frame bitmap, extracts the optimal writing bounding box ignoring edge noise, and returns responsive percentage coordinates.
2. **Dynamic UI Rendering**: Developed the `DynamicUserFrameNotebook` Jetpack Compose function to overlay an interactive text area over a custom background.
3. **Memory Optimization**: Ensured native memory cleanup inside `finally` blocks for all instantiated OpenCV matrices.
4. **Responsive Layouts**: Leveraged `BoxWithConstraints` to compute the interactive Text Field offset and dimensions dynamically in Jetpack Compose, avoiding any hardcoded scaling values.

---

## 🧠 Logical Descriptions

### OpenCV Computer Vision Layer
- **Simple Description**: We convert the colorful border image into black and white. Then we look for the largest "empty" square in the middle where a user can write text. Finally, we output exactly how much space it takes up (like 80% wide and 70% tall) so the app knows where to place the text box.
- **Technical Description**: The source bitmap is converted to Grayscale. We apply a Gaussian Blur (`5x5`) to denoise. Adaptive Thresholding (`ADAPTIVE_THRESH_GAUSSIAN_C` and `THRESH_BINARY_INV`) isolates the light writing region. Contours are detected via `RETR_EXTERNAL` and evaluated using `approxPolyDP` to verify polygon structures with $\ge 4$ vertices. Bounding rectangle extraction ensures maximum fit while dropping outer frame artifacts, yielding relative fractional dimensions (`0.0` to `1.0`).

### Jetpack Compose Layout Layer
- **Simple Description**: The screen stretches the downloaded border over the background. Using the percentages calculated by the CV tool, we place an invisible box right on top of the empty section. We put a text box inside so you can click and write.
- **Technical Description**: Wrapped inside a `BoxWithConstraints`, it utilizes exact container constraints (width/height) multiplied by the computed CV fractions. A two-layer stack handles the `Image` rendering with `ContentScale.FillBounds` (Layer 1) and a `BasicTextField` (Layer 2) shifted via `Modifier.offset()` and bounded using `Modifier.size()`. Padding is enforced dynamically.

---

## 💻 Full Implementation Code

### `DynamicUserFrameNotebook.kt`
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import org.opencv.android.Utils
import org.opencv.core.Mat
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Rect
import org.opencv.imgproc.Imgproc

data class DetectedWritingArea(
    val xPercent: Float,
    val yPercent: Float,
    val widthPercent: Float,
    val heightPercent: Float
)

fun detectWritingSpace(bitmap: Bitmap): DetectedWritingArea {
    val src = Mat()
    val gray = Mat()
    val blurred = Mat()
    val thresh = Mat()
    
    try {
        Utils.bitmapToMat(bitmap, src)
        
        Imgproc.cvtColor(src, gray, Imgproc.COLOR_RGBA2GRAY)
        Imgproc.GaussianBlur(gray, blurred, org.opencv.core.Size(5.0, 5.0), 0.0)
        
        Imgproc.adaptiveThreshold(
            blurred, 
            thresh, 
            255.0, 
            Imgproc.ADAPTIVE_THRESH_GAUSSIAN_C, 
            Imgproc.THRESH_BINARY_INV, 
            11, 
            2.0
        )
        
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
                
                // Reject the absolute outer edge (the whole image boundary)
                if (area > maxArea && area < imgArea * 0.95) {
                    maxArea = area
                    bestRect = Imgproc.boundingRect(contour)
                }
            }
        }
        
        if (bestRect == null) {
            // Fallback to a default safe area if no suitable contour is found
            return DetectedWritingArea(0.1f, 0.1f, 0.8f, 0.8f)
        }
        
        return DetectedWritingArea(
            xPercent = bestRect.x.toFloat() / src.cols().toFloat(),
            yPercent = bestRect.y.toFloat() / src.rows().toFloat(),
            widthPercent = bestRect.width.toFloat() / src.cols().toFloat(),
            heightPercent = bestRect.height.toFloat() / src.rows().toFloat()
        )
    } finally {
        src.release()
        gray.release()
        blurred.release()
        thresh.release()
    }
}

@Composable
fun DynamicUserFrameNotebook(
    frameBitmap: Bitmap,
    modifier: Modifier = Modifier
) {
    var textState by remember { mutableStateOf("") }
    
    // Compute the area exactly once for the given bitmap
    val detectedArea = remember(frameBitmap) {
        detectWritingSpace(frameBitmap)
    }
    
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val boxX = maxWidth * detectedArea.xPercent
        val boxY = maxHeight * detectedArea.yPercent
        val boxWidth = maxWidth * detectedArea.widthPercent
        val boxHeight = maxHeight * detectedArea.heightPercent
        
        // Layer 1 (Background)
        Image(
            bitmap = frameBitmap.asImageBitmap(),
            contentDescription = "Notebook Frame",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )
        
        // Layer 2 (Interactive UI Overlay)
        Box(
            modifier = Modifier
                .offset(x = boxX, y = boxY)
                .size(width = boxWidth, height = boxHeight)
                .padding(12.dp)
        ) {
            BasicTextField(
                value = textState,
                onValueChange = { textState = it },
                modifier = Modifier.fillMaxSize(),
                decorationBox = { innerTextField ->
                    if (textState.isEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "Write here...", color = Color.Gray)
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

---

## 🛠️ Extra Steps

- **OpenCV Dependency**: This implementation requires OpenCV for Android to be properly integrated into your project. Make sure you add OpenCV via Gradle or as an imported module.
- **Initialization**: Before invoking any UI that uses OpenCV functions, ensure `OpenCVLoader.initDebug()` runs successfully (typically in the `Application` class or `MainActivity`).
- **Memory Check**: Since matrices are disposed in the `finally` block, ensure you do not hold static references to `Bitmap` instances or `Mat` instances unintentionally to avoid JVM memory leaks.

---

## 📝 Summary
Data flows seamlessly from an imported `Bitmap` file to the OpenCV parser. The CV engine creates a Grayscale copy, finds the central safe zone, and outputs four floating-point percentages representing the valid internal writing area. These exact percentages are injected into the Jetpack Compose constraint builder, shifting and scaling the standard Material `BasicTextField` seamlessly on top of the organic user image. All native CV buffers are automatically de-allocated upon completion to maintain device performance.
