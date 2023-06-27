package com.jerry.myapp.model

import com.jerry.myapp.base.BaseResponse

open class UserListResponse(
    code: Int,
    meta: Meta,

) : BaseResponse(code, meta){
    var data: List<User> = emptyList()
}