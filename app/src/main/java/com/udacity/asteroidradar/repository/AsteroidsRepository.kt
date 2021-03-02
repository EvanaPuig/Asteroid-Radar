package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

class AsteroidsRepository (private val database: AsteroidDatabase){
    private var service = AsteroidApi.retrofitService

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidsDao.getAsteroids()
    ) {
        it.asDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidResult = service.getAsteroids(getToday(), getSeventhDay(), API_KEY)
                val asteroidProperties = parseAsteroidsJsonResult(JSONObject(asteroidResult))
                for (asteroid in asteroidProperties) {
                    Timber.d("asteroid $asteroid")
                }
                val networkAsteroidList = asteroidProperties.map {
                    NetworkAsteroid(
                        it.id,
                        it.codename,
                        it.closeApproachDate,
                        it.absoluteMagnitude,
                        it.estimatedDiameter,
                        it.relativeVelocity,
                        it.distanceFromEarth,
                        it.isPotentiallyHazardous
                    )
                }
                database.asteroidsDao.insertAll(*networkAsteroidList.asDatabaseModel())
                Timber.i("Success insertion of Asteroid Data")
            } catch (ex: Exception) {
                Timber.e("Error on inserting asteroid data ${ex.cause}")
            }
        }
    }

    companion object {
        private const val API_KEY = ""
    }
}
