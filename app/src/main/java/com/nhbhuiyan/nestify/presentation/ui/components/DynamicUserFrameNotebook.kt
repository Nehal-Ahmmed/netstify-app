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
                
                if (area > maxArea && area < imgArea * 0.95) {
                    maxArea = area
                    bestRect = Imgproc.boundingRect(contour)
                }
            }
        }
        
        if (bestRect == null) {
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
    // Get from cache or compute once
    val detectedArea = remember(frameBitmap) {
        WritingSpaceCache.getArea(frameBitmap)
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
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.TopStart
                        ) {
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
