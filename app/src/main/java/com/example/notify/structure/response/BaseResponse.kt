package com.example.notify.structure.response

open class BaseResponse<T> (
    val status: String,
    val error: Error?,
    val content: T?
) {
    data class Error(
        val code: Int,
        val message: String
    )

    fun content(): T? {
        return content
    }

    fun error(): Error? {
        return error
    }
}