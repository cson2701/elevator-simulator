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

    private val _elevatorStatus = MutableLiveData(ElevatorProps.Status.IDLE)
    val elevatorStatus: LiveData<ElevatorProps.Status> = _elevatorStatus

    companion object {
        //@formatter:off
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

    fun move(targetFloor: Int) {
        _isTargetReached.postValue(false)
        viewModelScope.launch(Dispatchers.IO) {
            val isElevatorArrived = elevator.move(targetFloor, object : ElevatorListener {
                override fun onFloorChangeListener(currentFloor: Int) {
                    _currentFloor.postValue(currentFloor)
                }

                override fun onStatusChangeListener(status: ElevatorProps.Status) {
                    _elevatorStatus.postValue(elevator.status())
                }
            })
            if (isElevatorArrived) {
                _isTargetReached.postValue(true)
            } else {
                _isTargetReached.postValue(false)
            }
        }
    }
}