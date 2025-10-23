package com.nhbhuiyan.nestify.domain.usecases.Linkusecases

import com.nhbhuiyan.nestify.domain.model.Link
import com.nhbhuiyan.nestify.domain.repository.ContentRepository
import kotlinx.datetime.Clock
import javax.inject.Inject

class CreateLinkUseCase @Inject constructor(
    private val repository: ContentRepository
) {


    suspend operator fun invoke(
        title: String,
        description: String,
        url: String
    ) : Long {
        val now = Clock.System.now()
        val domain = extractDomain(url)
        val link = Link(
            title = title,
            description = description,
            url = url,
            createdAt = now,
            updatedAt = now,
            domain = domain
        )
        return repository.createLink(link)
    }

    private fun extractDomain(url: String): String {
        return try {
            java.net.URI(url).host ?: url
        }catch (e: Exception){
            url
        }
    }
}