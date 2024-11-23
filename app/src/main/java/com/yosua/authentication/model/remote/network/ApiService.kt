package com.yosua.authentication.model.remote.network

import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.RegisterResponse
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email : String,
        @Field("password") password : String
    ) : LoginResponse

    @GET("stories")
    suspend fun getAllStories(): AllStoryResponse
}