package com.jerry.myapp.responsitory

import com.jerry.myapp.model.CreateUserResponse
import com.jerry.myapp.model.DeleteUserResponse
import com.jerry.myapp.model.User
import com.jerry.myapp.model.UserListResponse
import com.jerry.myapp.network.ApiService
import javax.inject.Inject

class NetworkRepository @Inject constructor(
    private val apiService : ApiService
) {
    suspend fun getUserList(page : Int): UserListResponse {
        return  apiService.getUserList(page)
    }

    suspend fun deleteUser(id: Long): DeleteUserResponse {
        return  apiService.deleteUser(id)
    }

    suspend fun createUser(body: User): CreateUserResponse {
        return  apiService.createUser(body)
    }
}