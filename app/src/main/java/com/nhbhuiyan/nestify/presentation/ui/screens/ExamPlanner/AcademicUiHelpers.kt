package com.nhbhuiyan.nestify.presentation.ui.screens.ExamPlanner

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.Chip
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.ChipTone
import com.nhbhuiyan.nestify.presentation.ui.components.brainston.NestifyCard
import com.nhbhuiyan.nestify.ui.theme.NestifyTheme

/** Bottom clearance so scrolling tab content can scroll clear of the floating glass nav. */
val AcademicNavClearance = 108.dp

/** Map a letter grade to a BrainSton chip tone for color-coded status pills. */
fun gradeTone(grade: String): ChipTone = when (grade.uppercase()) {
    "A+", "A", "A-" -> ChipTone.Ok
    "B+", "B", "B-" -> ChipTone.Brand
    "C+", "C", "D" -> ChipTone.Warn
    "F" -> ChipTone.Coral
    else -> ChipTone.Default
}

/**
 * Shared level / term viewing filter as a BrainSton card row. The two chips cycle
 * level (1→4) and term (1↔2) on tap, reporting via [onLevel] / [onTerm].
 */
@Composable
fun LevelTermFilter(
    level: Int,
    term: Int,
    onLevel: (Int) -> Unit,
    onTerm: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val c = NestifyTheme.colors
    NestifyCard(modifier = modifier.fillMaxWidth(), padding = 12.dp) {
        Row(
            Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Tune, contentDescription = null, tint = c.ink70, modifier = Modifier.size(18.dp))
                Text("Viewing", style = NestifyTheme.type.label, color = c.ink70)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Chip("L$level", tone = ChipTone.Default, onClick = { onLevel(if (level == 4) 1 else level + 1) })
                Chip("T$term", tone = ChipTone.Default, onClick = { onTerm(if (term == 2) 1 else 2) })
            }
        }
    }
}
