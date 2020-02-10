package com.example.notify.structure.response

class ScopeList(status: String, error: BaseResponse.Error, content: Success) : BaseResponse<ScopeList.Success>(
    status,
    error,
    content
){
    data class Success(
        val items: ArrayList<Scope>
    )

    data class Scope(
        val id_scope: Int,
        val name: String
    )
}