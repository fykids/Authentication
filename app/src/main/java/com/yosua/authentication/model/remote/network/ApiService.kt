package com.yosua.authentication.model.remote.network

import com.yosua.authentication.model.remote.response.AddStoryResponse
import com.yosua.authentication.model.remote.response.AllStoryResponse
import com.yosua.authentication.model.remote.response.DetailResponse
import com.yosua.authentication.model.remote.response.LoginResponse
import com.yosua.authentication.model.remote.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun registerUser(
        @Field("name") name : String,
        @Field("email") email : String,
        @Field("password") password : String,
    ) : RegisterResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun loginUser(
        @Field("email") email : String,
        @Field("password") password : String,
    ) : LoginResponse

    @GET("stories")
    suspend fun getAllStories() : Response<AllStoryResponse>

    @GET("stories/{storyId}")
    suspend fun getStories(
        @Path("storyId") storyId : String,
    ) : Response<DetailResponse>

    @Multipart
    @POST("stories")
    suspend fun uploadImage(
        @Part file : MultipartBody.Part,
        @Part("description") description : RequestBody,
    ) : Response<AddStoryResponse>

    @GET("stories")
    suspend fun getStoriesPaging(
        @Query("page") page : Int = 1,
        @Query("size") size : Int = 20,
    ) : AllStoryResponse
}