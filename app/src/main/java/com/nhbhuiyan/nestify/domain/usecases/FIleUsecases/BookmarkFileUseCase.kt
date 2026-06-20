package com.nhbhuiyan.nestify.domain.usecases.FIleUsecases

import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import javax.inject.Inject

class BookmarkFileUseCase @Inject constructor(
    private val repository: ContentRepository
) {
}