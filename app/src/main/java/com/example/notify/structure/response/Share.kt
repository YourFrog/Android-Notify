package com.example.notify.structure.response

class Share(status: String, error: BaseResponse.Error, content: Success) : BaseResponse<Share.Success>(
    status,
    error,
    content
){
    data class Success(
        val code: String
    )
}