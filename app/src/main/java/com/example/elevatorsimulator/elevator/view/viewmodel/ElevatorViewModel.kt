package com.example.elevatorsimulator.elevator.view.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.elevatorsimulator.elevator.ElevatorBuilder
import com.example.elevatorsimulator.elevator.ElevatorControl
import com.example.elevatorsimulator.elevator.ElevatorListener
import com.example.elevatorsimulator.elevator.ElevatorProps
import com.example.elevatorsimulator.elevator.ElevatorQueue
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class ElevatorViewModel : ViewModel() {
    private val elevatorConfig = ElevatorConfig.getInstance()

    private val initCurrentFloor = randomizeInitCurrentFloor()

    private val _currentFloor = MutableStateFlow(initCurrentFloor)
    val currentFloor: StateFlow<Int> = _currentFloor

    private val _openDoor = MutableStateFlow(false)
    val openDoor: StateFlow<Boolean> = _openDoor

    private val _elevatorEvent = Channel<ElevatorEvent>(Channel.BUFFERED)
    val elevatorEvent = _elevatorEvent.receiveAsFlow()

    private val _elevatorStatus = MutableStateFlow(ElevatorProps.Status.POWER_OFF)
    val elevatorStatus: StateFlow<ElevatorProps.Status> = _elevatorStatus

    private val _serviceDirection = MutableStateFlow(ElevatorProps.ServiceDirection.IDLE)
    val serviceDirection: StateFlow<ElevatorProps.ServiceDirection> = _serviceDirection

    private val elevatorQueue = ElevatorQueue()
    val floorsInQueue: StateFlow<List<Int>> = elevatorQueue.queue

    private fun randomizeInitCurrentFloor(): Int {
        val lowestFloor = elevatorConfig.getLowestFloor()
        val highestFloor = elevatorConfig.getHighestFloor()
        return (lowestFloor..highestFloor).random()
    }

    fun getLowestFloor() = elevatorConfig.getLowestFloor()
    fun getHighestFloor() = elevatorConfig.getHighestFloor()

    private val elevatorControl: ElevatorControl
        get() = ElevatorControl.getInstance()

    fun powerOn() {
        viewModelScope.launch {
            try {
                elevatorControl.powerOn()
            } catch (_: UninitializedPropertyAccessException) {
                buildElevator()
                elevatorControl.powerOn()
            }
        }
    }

    private fun buildElevator() {
        ElevatorBuilder(object : ElevatorListener {
            override fun onFloorChangeListener(currentFloor: Int) {
                println("currentFloor = $currentFloor")
                _currentFloor.value = currentFloor
            }

            override fun onStatusChangeListener(status: ElevatorProps.Status) {
                println("status = $status")
                _elevatorStatus.value = status
            }

            override fun onServiceDirectionChangeListener(direction: ElevatorProps.ServiceDirection) {
                println("direction = $direction")
                _serviceDirection.value = direction
            }
        })
            .setLowestFloor(elevatorConfig.getLowestFloor())
            .setHighestFloor(elevatorConfig.getHighestFloor())
            .setCurrentFloor(currentFloor.value)
            .setSpeed(ElevatorProps.Speed.SPEED_1.value)
            .setNumberOfElevators(1)
            .build()
    }

    fun powerOff() {
        if (elevatorControl.status() == ElevatorProps.Status.IDLE) {
            elevatorControl.powerOff()
        }
    }

    private var moveJob: Job? = null

    fun onFloorPressed(targetFloor: Int) {
        elevatorQueue.addFloor(targetFloor)
        if (moveJob?.isActive != true && elevatorControl.status() == ElevatorProps.Status.IDLE) {
            moveJob = move()
        }
    }

    private fun move(): Job = viewModelScope.launch {
        while (elevatorQueue.hasNextFloor()) {
            val destination = elevatorQueue.peekNextFloor() ?: break
            val current = _currentFloor.value

            if (destination == current) {
                _elevatorEvent.trySend(ElevatorEvent.TargetFloorReached(current))
                elevatorQueue.removeFloor(current)

                openDoor()
                coroutineScope {
                    launch { delay(ElevatorConfig.CLOSE_DOOR_DELAY) }
                    launch {
                        elevatorStatus.first { it == ElevatorProps.Status.DOOR_OPEN }
                    }
                }
                closeDoor()
                _elevatorStatus.first { it == ElevatorProps.Status.IDLE }
                continue
            }

            val nextFloor = if (destination > current) current + 1 else current - 1
            var reached = false
            elevatorControl.move(nextFloor) {
                reached = it
            }

            if (reached) {
                if (elevatorQueue.peekNextFloor() == _currentFloor.value) {
                    val arrivedFloor = _currentFloor.value
                    _elevatorEvent.trySend(ElevatorEvent.TargetFloorReached(arrivedFloor))
                    elevatorQueue.removeFloor(arrivedFloor)

                    openDoor()
                    coroutineScope {
                        launch { delay(ElevatorConfig.CLOSE_DOOR_DELAY) }
                        launch {
                            elevatorStatus.first { it == ElevatorProps.Status.DOOR_OPEN }
                        }
                    }
                    closeDoor()
                    _elevatorStatus.first { it == ElevatorProps.Status.IDLE }
                }
            }
        }
    }

    fun reportDoorState(doorState: ElevatorDoorState) {
        viewModelScope.launch {
            elevatorControl.reportDoorStatus(doorState)
        }
    }

    /**
     * Returns true if the door is opening, false otherwise.
     */
    fun openDoor(): Boolean {
        if (elevatorControl.status() == ElevatorProps.Status.IDLE ||
            elevatorControl.status() == ElevatorProps.Status.TARGET_FLOOR_REACHED ||
            elevatorControl.status() == ElevatorProps.Status.DOOR_CLOSING
        ) {
            _openDoor.value = true
            reportDoorState(ElevatorDoorState.OPENING)
            return true
        }
        return false
    }

    /**
     * Returns true if the door is closing, false otherwise.
     */
    fun closeDoor(): Boolean {
        if (elevatorControl.status() == ElevatorProps.Status.DOOR_OPEN) {
            _openDoor.value = false
            reportDoorState(ElevatorDoorState.CLOSING)
            return true
        }
        return false
    }
}

sealed class ElevatorEvent {
    data class TargetFloorReached(val floor: Int) : ElevatorEvent()
}