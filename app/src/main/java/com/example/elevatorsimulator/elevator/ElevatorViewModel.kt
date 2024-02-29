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

    companion object {
        //@formatter:off
        const val targetFloor = 4
        const val LOWEST_FLOOR = 1
        const val HIGHEST_FLOOR = 10
        const val CURRENT_FLOOR = 7
        val SPEED = ElevatorSpeed.SPEED_1.value
        //@formatter:on
    }

    init {
        val elevator = ElevatorBuilder()
            .setLowestFloor(LOWEST_FLOOR)
            .setHighestFloor(HIGHEST_FLOOR)
            .setCurrentFloor(CURRENT_FLOOR)
            .setSpeed(SPEED)
            .setNumberOfElevators(1)
            .build()

        println("Elevator is on floor ${elevator.currentFloor}")
        println("Target floor: ${targetFloor}")
        println("===")

        viewModelScope.launch(Dispatchers.IO) {
            val isElevatorArrived = elevator.move(targetFloor, object : FloorChangeListener {
                override fun onFloorChangeListener(currentFloor: Int) {
                    println("currentFloor = $currentFloor")
                    _currentFloor.postValue(currentFloor)
                }
            })
            println(if (isElevatorArrived) "Ding!" else "Ah oh")
        }
    }
}