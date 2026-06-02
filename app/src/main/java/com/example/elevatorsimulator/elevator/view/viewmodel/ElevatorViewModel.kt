package com.example.elevatorsimulator.elevator.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elevatorsimulator.elevator.ElevatorBuilder
import com.example.elevatorsimulator.elevator.ElevatorControl
import com.example.elevatorsimulator.elevator.ElevatorListener
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import com.example.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ElevatorViewModel : ViewModel() {
    private val elevatorConfig = ElevatorConfig

    private val initCurrentFloor = randomizeInitCurrentFloor()

    private val _currentFloor = MutableStateFlow(initCurrentFloor)
    val currentFloor: StateFlow<Int> = _currentFloor

    private val _isTargetReached = MutableStateFlow(false)
    val isTargetFloorReached: StateFlow<Boolean> = _isTargetReached

    private val _openDoor = MutableStateFlow(false)
    val openDoor: StateFlow<Boolean> = _openDoor

    private val _elevatorEvent = Channel<ElevatorEvent>(Channel.BUFFERED)
    val elevatorEvent = _elevatorEvent.receiveAsFlow()

    private val _elevatorStatus = MutableStateFlow(ElevatorProps.Status.POWER_OFF)
    val elevatorStatus: StateFlow<ElevatorProps.Status> = _elevatorStatus

    private fun randomizeInitCurrentFloor(): Int {
        val lowestFloor = elevatorConfig.getLowestFloor()
        val highestFloor = elevatorConfig.getHighestFloor()
        return (lowestFloor..highestFloor).random()
    }

    fun getLowestFloor() = elevatorConfig.getLowestFloor()
    fun getHighestFloor() = elevatorConfig.getHighestFloor()

    private var elevatorControl: ElevatorControl? = null

    fun powerOn() {
        viewModelScope.launch(Dispatchers.IO) {
            buildElevator()
            elevatorControl?.powerOn()
        }
    }

    private fun buildElevator() {
        elevatorControl = ElevatorBuilder(object : ElevatorListener {
            override fun onFloorChangeListener(currentFloor: Int) {
                println("currentFloor = $currentFloor")
                _currentFloor.value = currentFloor
            }

            override fun onStatusChangeListener(status: ElevatorProps.Status) {
                println("status = $status")
                _elevatorStatus.value = status
            }
        })
            .setLowestFloor(elevatorConfig.getLowestFloor())
            .setHighestFloor(elevatorConfig.getHighestFloor())
            .setCurrentFloor(currentFloor.value)
            .setSpeed(SPEED)
            .setNumberOfElevators(1)
            .build()
    }

    fun powerOff() {
        if (elevatorControl?.status() == ElevatorProps.Status.IDLE) {
            elevatorControl?.powerOff()
        }
    }

    fun move(targetFloor: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            _isTargetReached.value = false
            delay(100) // added a delay to let the view have time to react to previous false
            try {
                elevatorControl?.move(targetFloor) {
                    if (it) {
                        println("target floor $targetFloor reached")
                        _elevatorEvent.trySend(ElevatorEvent.TargetFloorReached(targetFloor))
                    }
                }
            } catch (e: ElevatorNotPoweredOnException) {
                e.printStackTrace()
                System.err.println("Disable UI before powering on the elevator")
            }
        }
    }

    fun reportDoorState(doorState: ElevatorDoorState) {
        viewModelScope.launch {
            elevatorControl?.reportDoorStatus(doorState)
        }
    }

    /**
     * Returns true if the door is opening, false otherwise.
     */
    fun openDoor(): Boolean {
        if (elevatorControl?.status() == ElevatorProps.Status.IDLE || elevatorControl?.status() == ElevatorProps.Status.DOOR_CLOSING) {
            _openDoor.value = true
            return true
        }
        return false
    }

    /**
     * Returns true if the door is closing, false otherwise.
     */
    fun closeDoor(): Boolean {
        if (elevatorControl?.status() == ElevatorProps.Status.DOOR_OPEN) {
            _openDoor.value = false
            return true
        }
        return false
    }

    companion object {
        private val SPEED = ElevatorProps.Speed.SPEED_1.value
    }
}

sealed class ElevatorEvent {
    data class TargetFloorReached(val floor: Int) : ElevatorEvent()
}