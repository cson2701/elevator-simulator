package com.example.elevatorsimulator.elevator

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ElevatorViewModel : ViewModel() {
    private val elevatorConfig = ElevatorConfig()
    private val initCurrentFloor = randomizeInitCurrentFloor()

    private val _currentFloor = MutableLiveData(initCurrentFloor)
    val currentFloor: LiveData<Int> = _currentFloor

    private val _isTargetReached = MutableLiveData(false)
    val isTargetFloorReached: LiveData<Boolean> = _isTargetReached

    private val _elevatorStatus = MutableLiveData(ElevatorProps.Status.POWER_OFF)
    val elevatorStatus: LiveData<ElevatorProps.Status> = _elevatorStatus

    private fun randomizeInitCurrentFloor(): Int {
        val lowestFloor = elevatorConfig.getLowestFloor()
        val highestFloor = elevatorConfig.getHighestFloor()
        return (lowestFloor..highestFloor).random()
    }

    // TODO: Build Elevator when power on

    private val elevator: Elevator = ElevatorBuilder(object : ElevatorListener {
        override fun onFloorChangeListener(currentFloor: Int) {
            println("currentFloor = $currentFloor")
            _currentFloor.postValue(currentFloor)
        }

        override fun onStatusChangeListener(status: ElevatorProps.Status) {
            println("status = $status")
            _elevatorStatus.postValue(status)
        }
    })
        .setLowestFloor(elevatorConfig.getLowestFloor())
        .setHighestFloor(elevatorConfig.getHighestFloor())
        .setCurrentFloor(initCurrentFloor)
        .setSpeed(SPEED)
        .setNumberOfElevators(1)
        .build()

    fun powerOn() {
        viewModelScope.launch(Dispatchers.IO) {
            elevator.powerOn()
        }
    }

    fun powerOff() {
        if (elevator.status() == ElevatorProps.Status.IDLE) {
            elevator.powerOff()
        }
    }

    fun move(targetFloor: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isTargetReached.postValue(false)
            delay(100) // added a delay to let the view have time to react to previous false
            try {
                _isTargetReached.postValue(elevator.move(targetFloor))
            } catch (e: ElevatorNotPoweredOnException) {
                e.printStackTrace()
                System.err.println("Disable UI before powering on the elevator")
            }
        }
    }

    companion object {
        private val SPEED = ElevatorProps.Speed.SPEED_1.value
    }
}