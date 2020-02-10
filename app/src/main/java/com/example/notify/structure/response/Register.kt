package com.example.notify.structure.response

class Register(status: String, error: BaseResponse.Error, content: Register.Success) : BaseResponse<Register.Success>(
    status,
    error,
    content
){
    data class Success(
        val hash: String
    )
}