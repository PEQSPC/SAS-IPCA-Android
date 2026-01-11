package com.example.lojasocial.models

import kotlinx.coroutines.flow.Flow

interface LoginRepository {
    fun login(username : String, password : String) : Flow<ResultWrapper<Unit>>
    fun register ( email : String, password: String) :  Flow<ResultWrapper<Unit>>
}