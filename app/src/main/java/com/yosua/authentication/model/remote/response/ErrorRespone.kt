package com.yosua.authentication.model.remote.response

import com.google.gson.annotations.SerializedName

data class ErrorRespone (
    @field:SerializedName("error")
    val error : Boolean? = null,

    @field:SerializedName("message")
    val message : String? = null
)