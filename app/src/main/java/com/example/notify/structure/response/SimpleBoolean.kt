package com.example.notify.structure.response

class SimpleBoolean(status: String, error: BaseResponse.Error, content: Success) : BaseResponse<SimpleBoolean.Success>(
    status,
    error,
    content
){
    data class Success(
        val result: Boolean
    )
}