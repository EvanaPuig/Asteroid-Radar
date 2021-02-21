package com.udacity.asteroidradar.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.udacity.asteroidradar.model.Asteroid

class MainViewModel : ViewModel() {
    private val _asteroidList = MutableLiveData<List<Asteroid>>()
    val asteroidList: LiveData<List<Asteroid>>
        get() = _asteroidList

    init {
        _asteroidList.value = createTestAsteroids()
    }

    private fun createTestAsteroids(): List<Asteroid> {
        val asteroidList = mutableListOf<Asteroid>()

        for ( i in 0..10) {
            val asteroid = Asteroid(
                5678,
                "Test Name",
                "05-08-21",
                5.5,
                5.0,
                5.0,
                4.3,
                false
            )

            val asteroid2 = Asteroid(
                4231,
                "Test Name",
                "05-07-21",
                5.5,
                5.0,
                5.0,
                4.3,
                true
            )

            asteroidList.add(asteroid)
            asteroidList.add(asteroid2)
        }

        return asteroidList
    }
}
