package com.indexed.server.comics.domain.model

sealed interface ComicError {
    data class Validation(val message: String) : ComicError
    data class NotFound(val message: String) : ComicError
    data class UpstreamUnavailable(val message: String) : ComicError
}
