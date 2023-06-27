package com.jerry.myapp.model

import com.jerry.myapp.base.BaseResponse

open class CreateUserResponse(
    code: Int,
    meta: Meta?=null,

) : BaseResponse(code, meta){
    var data: Any? = null
}