package com.orlandus.uploadfilesserver

data class UploadResponse(
    val error:Boolean,
    val message: String,
    val image:String?
)
