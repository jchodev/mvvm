package com.jerry.myapp.model

import com.jerry.myapp.base.BaseResponse


open class DeleteUserResponse(
    code: Int,
    meta: Meta?=null
) : BaseResponse(code, meta) {
    var data: DeleteUserData? = null
}