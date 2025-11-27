package com.example.comicstoreapp.data.remote


import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RemotePedidos {

    private const val BASE_URL = "http://10.0.2.2:8083/" // puerto del microservicio de pedidos

    fun <T> create(service: Class<T>): T {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(service)
    }
}
