package com.nhbhuiyan.nestify.presentation.ui.screens.LinkScreen.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nhbhuiyan.nestify.domain.model.Link

@Composable
fun linkItem(link: Link, onClick: () -> Unit) {
    Card (
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ){
        Column (
            modifier = Modifier.padding(16.dp)
        ){
            link.title?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleMedium
                )
            }
            link.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            link.url?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}