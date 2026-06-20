package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class BookmarkLinkUseCase @Inject constructor(
    private val repository: ContentRepository
) {
    suspend operator fun invoke(id: Long, isBookmarked : Boolean){
        repository.bookmarkLink(id, isBookmarked)
    }
}