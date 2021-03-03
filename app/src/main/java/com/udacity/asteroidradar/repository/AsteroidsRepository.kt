package com.udacity.asteroidradar.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import com.udacity.asteroidradar.api.getSeventhDay
import com.udacity.asteroidradar.api.getToday
import com.udacity.asteroidradar.api.parseAsteroidsJsonResult
import com.udacity.asteroidradar.database.AsteroidDatabase
import com.udacity.asteroidradar.database.asDomainModel
import com.udacity.asteroidradar.database.asPictureOfDayDomainModel
import com.udacity.asteroidradar.model.Asteroid
import com.udacity.asteroidradar.model.PictureOfDay
import com.udacity.asteroidradar.network.AsteroidApi
import com.udacity.asteroidradar.network.NetworkAsteroid
import com.udacity.asteroidradar.network.asDatabaseModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import timber.log.Timber
import java.lang.Exception

class AsteroidsRepository(private val database: AsteroidDatabase) {
    private var asteroidService = AsteroidApi.retrofitService

    val asteroids: LiveData<List<Asteroid>> = Transformations.map(
        database.asteroidsDao.getAsteroids()
    ) {
        it.asDomainModel()
    }

    // Picture of day to be shown on screen
    val pictureOfDay: LiveData<PictureOfDay> = Transformations.map(
        database.picOfDayDao.getPictureOfDay()
    ) {
        it?.asPictureOfDayDomainModel()
    }

    suspend fun refreshAsteroids() {
        withContext(Dispatchers.IO) {
            try {
                val asteroidResult = asteroidService.getAsteroids(getToday(), getSeventhDay(), API_KEY)
                val asteroidProperties = parseAsteroidsJsonResult(JSONObject(asteroidResult))
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

    suspend fun refreshPicOfDay() {
        withContext(Dispatchers.IO) {
            try {
                val picOfDayResult = asteroidService.getPicOfDay(API_KEY)
                database.picOfDayDao.insertPicture(picOfDayResult.asDatabaseModel())
            } catch (ex: Exception) {
                Timber.e("Error on inserting pic of day data ${ex.cause}")
            }
        }
    }

    companion object {
        private const val API_KEY = ""
    }
}
