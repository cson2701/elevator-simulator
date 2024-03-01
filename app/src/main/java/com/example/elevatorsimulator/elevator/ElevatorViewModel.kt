package com.example.elevatorsimulator.elevator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ElevatorViewModel : ViewModel() {
    private val _currentFloor = MutableLiveData(-99)
    val currentFloor: LiveData<Int> = _currentFloor

    private val _isTargetReached = MutableLiveData(false)
    val isTargetFloorReached: LiveData<Boolean> = _isTargetReached

    companion object {
        //@formatter:off
        const val targetFloor = 4
        const val LOWEST_FLOOR = 1
        const val HIGHEST_FLOOR = 10
        const val CURRENT_FLOOR = 7
        val SPEED = ElevatorProps.Speed.SPEED_1.value
        //@formatter:on
    }

    private val elevator: Elevator = ElevatorBuilder()
        .setLowestFloor(LOWEST_FLOOR)
        .setHighestFloor(HIGHEST_FLOOR)
        .setCurrentFloor(CURRENT_FLOOR)
        .setSpeed(SPEED)
        .setNumberOfElevators(1)
        .build()

    init {
        println("Elevator is on floor ${elevator.currentFloor}")
        println("Target floor: $targetFloor")
        println("===")
    }

    fun move(targetFloor: Int) {
        _isTargetReached.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {
            val isElevatorArrived = elevator.move(targetFloor, object : FloorChangeListener {
                override fun onFloorChangeListener(currentFloor: Int) {
                    println("currentFloor = $currentFloor")
                    _currentFloor.postValue(currentFloor)
                }
            })
            if (isElevatorArrived) {
                _isTargetReached.postValue(true)
                println("Ding!")
            } else {
                _isTargetReached.postValue(false)
                println("Ah oh")
            }
        }
    }
}