package com.nhbhuiyan.nestify.presentation.ui.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun <T> GenericList(
    items: List<T>,
    modifier: Modifier= Modifier,
    itemContent: @Composable (T) -> Unit
){
    LazyColumn (
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ){
        items(count = items.size){index ->
            itemContent(items[index])
            if (index < items.size-1)
                Spacer(modifier = Modifier.height(8.dp))
        }
    }
}