package com.jerry.myapp.base

import com.jerry.myapp.model.Meta


open class BaseResponse(
    val code: Int,
    val meta: Meta?
)