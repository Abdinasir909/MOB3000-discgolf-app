package no.usn.mob3000_gruppe15.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object NetworkModule {

    private const val BASE_URL = "https://disk-applikasjon-39f504b7af19.herokuapp.com"
    private const val YR_API_URL = "https://api.met.no/weatherapi/locationforecast/2.0/"

    private val metApiHttpClient = OkHttpClient.Builder().apply {
        addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "DiskGolfApp github.com/BjarneBeruldsen")
                .build()
            chain.proceed(request)
        }
    }.build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    val værApiService: VærApiService by lazy {
        Retrofit.Builder()
            .baseUrl(YR_API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(metApiHttpClient)
            .build()
            .create(VærApiService::class.java)
    }

}