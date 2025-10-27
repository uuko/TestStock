package com.example.teststock.domain.model

/**
 * Domain Layer 的錯誤類型
 */
sealed class DomainError : Exception() {
    object NetworkError : DomainError()
    object DataNotFound : DomainError()
    object AuthenticationError : DomainError()
    data class UnknownError(override val message: String) : DomainError()
}
