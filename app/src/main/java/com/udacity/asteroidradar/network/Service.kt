package com.udacity.asteroidradar.network

import com.udacity.asteroidradar.model.Asteroid
import kotlinx.coroutines.Deferred
import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface NasaAsteroidsService {
    @GET("neo/rest/v1/feed")
    fun getAsteroids(
        @Query("start_date") startDate: String,
        @Query("end_date") endDate: String,
        @Query("api_key") apiKey: String) : Deferred<List<Asteroid>>
}

/**
 * Main entry point for network access.
 */
object Network {
    // Configure retrofit to parse JSON and use coroutines
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.nasa.gov/")
        .addConverterFactory(ScalarsConverterFactory.create())
        .build()

    val asteroidsService = retrofit.create(NasaAsteroidsService::class.java)
}
