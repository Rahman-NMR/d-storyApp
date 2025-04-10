package com.rahman.storyapp.data.remote.api

import com.rahman.storyapp.data.remote.response.StoriesResponse
import com.rahman.storyapp.data.remote.response.LoginResponse
import com.rahman.storyapp.data.remote.response.ErrorResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): ErrorResponse

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): LoginResponse

    @GET("stories")
    suspend fun getStories(
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20
    ): StoriesResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location: Int = 1,
    ): StoriesResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStories(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): ErrorResponse

    @Multipart
    @POST("stories")
    suspend fun addNewStoriesLocation(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody
    ): ErrorResponse
}