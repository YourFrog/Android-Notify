package com.example.notify.structure.response

class SignIn(status: String, error: BaseResponse.Error, content: Success) : BaseResponse<SignIn.Success>(
    status,
    error,
    content
){
    data class Success(
        val hash: String
    )
}