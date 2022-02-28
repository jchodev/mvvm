package com.jerry.myapp.network

import com.jerry.myapp.model.CreateUserResponse
import com.jerry.myapp.model.DeleteUserResponse
import com.jerry.myapp.model.User
import com.jerry.myapp.model.UserListResponse
import retrofit2.http.*


interface ApiService {

    @GET("/public-api/users")
    suspend fun getUserList(@Query("page") pageId: Int): UserListResponse

    @DELETE("/public-api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Long): DeleteUserResponse

    @POST("/public-api/users")
    suspend fun createUser(@Body body: User): CreateUserResponse
}