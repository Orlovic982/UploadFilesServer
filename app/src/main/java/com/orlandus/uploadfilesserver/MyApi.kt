package com.orlandus.uploadfilesserver


import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface MyApi {


    @Multipart//Requst to upload file to server, every parametar must be @Part
    @POST("Api.php?apicall=upload")
    fun uploadImage(
        @Part
        image: MultipartBody.Part,
        @Part ("desc")
        desc: RequestBody
    ) : Call<UploadResponse>




    companion object{
        operator fun invoke():MyApi{
            return Retrofit.Builder()
                .baseUrl("http://192.168.0.17/android/imageuploader/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MyApi::class.java)
        }
    }

}