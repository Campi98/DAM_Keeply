package pt.ipt.dam.a25269a24639.keeply.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RetrofitClient {
    private const val BASE_URL = "https://keeplybackend-production.up.railway.app/"
    // Para emulador Android acessar localhost do PC, use 10.0.2.2

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }
}