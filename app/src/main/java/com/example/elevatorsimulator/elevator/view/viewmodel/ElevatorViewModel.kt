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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

    private val _logs = MutableStateFlow<List<String>>(emptyList())
    val logs: StateFlow<List<String>> = _logs

    private val elevatorQueue = ElevatorQueue()
    val floorsInQueue: StateFlow<List<Int>> = elevatorQueue.queue

    private val _isProximityObstructed = MutableStateFlow(false)

    private var doorClosingJob: Job? = null

    init {
        addLog("Elevator Config: Low=${elevatorConfig.getLowestFloor()}, High=${elevatorConfig.getHighestFloor()}")
    }

    private fun addLog(message: String) {
        val timestamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        _logs.value = listOf("[$timestamp] $message") + _logs.value.take(99)
    }

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
        addLog("Powering on...")
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
        addLog("Building elevator...")
        ElevatorBuilder(object : ElevatorListener {
            override fun onFloorChangeListener(currentFloor: Int) {
                println("currentFloor = $currentFloor")
                _currentFloor.value = currentFloor
            }

            override fun onStatusChangeListener(status: ElevatorProps.Status) {
                println("status = $status")
                _elevatorStatus.value = status
                addLog("Status: $status")
            }

            override fun onServiceDirectionChangeListener(direction: ElevatorProps.ServiceDirection) {
                println("direction = $direction")
                _serviceDirection.value = direction
                addLog("Direction: $direction")
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
            addLog("Powering off...")
            elevatorControl.powerOff()
        }
    }

    fun setProximityObstructed(obstructed: Boolean) {
        if (_isProximityObstructed.value != obstructed) {
            addLog("Proximity: ${if (obstructed) "OBSTRUCTED" else "CLEAR"}")
        }
        _isProximityObstructed.value = obstructed
        if (obstructed && elevatorControl.status() == ElevatorProps.Status.DOOR_CLOSING) {
            openDoor()
        }
    }

    private var moveJob: Job? = null

    fun onFloorPressed(targetFloor: Int) {
        addLog("Button pressed: $targetFloor")
        elevatorQueue.addFloor(targetFloor)
        addLog("Queue: ${elevatorQueue.queue.value}")
        val status = elevatorControl.status()
        val canStartMove = status != ElevatorProps.Status.POWER_OFF &&
                status != ElevatorProps.Status.MOVING_UP &&
                status != ElevatorProps.Status.MOVING_DOWN
        if (moveJob?.isActive != true && canStartMove) {
            moveJob = move()
        }
    }

    private fun move(): Job = viewModelScope.launch {
        while (elevatorQueue.hasNextFloor()) {
            val destination = elevatorQueue.peekNextFloor() ?: break

            // Ensure the door is closed before moving
            val status = elevatorControl.status()
            if (status == ElevatorProps.Status.DOOR_OPEN ||
                status == ElevatorProps.Status.DOOR_OPENING ||
                status == ElevatorProps.Status.DOOR_CLOSING
            ) {
                // Wait for the door to be fully closed (status becomes IDLE)
                _elevatorStatus.first { it == ElevatorProps.Status.IDLE }
            }

            val current = _currentFloor.value

            if (destination == current) {
                addLog("Reached target floor $current")
                _elevatorEvent.trySend(ElevatorEvent.TargetFloorReached(current))
                elevatorQueue.removeFloor(current)
                addLog("Queue: ${elevatorQueue.queue.value}")

                openDoor()
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
                    addLog("Reached target floor $arrivedFloor")
                    _elevatorEvent.trySend(ElevatorEvent.TargetFloorReached(arrivedFloor))
                    elevatorQueue.removeFloor(arrivedFloor)
                    addLog("Queue: ${elevatorQueue.queue.value}")

                    openDoor()
                    _elevatorStatus.first { it == ElevatorProps.Status.IDLE }
                }
            }
        }
    }

    fun reportDoorState(doorState: ElevatorDoorState) {
        addLog("Door State: $doorState")
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
            addLog("Request: Open Door")
            _openDoor.value = true
            reportDoorState(ElevatorDoorState.OPENING)
            startAutoCloseTimer()
            return true
        }
        return false
    }

    private fun startAutoCloseTimer() {
        doorClosingJob?.cancel()
        doorClosingJob = viewModelScope.launch {
            // Wait for the door to be fully open
            elevatorStatus.first { it == ElevatorProps.Status.DOOR_OPEN }

            addLog("Auto-close timer started")
            // 1. Wait for the close door delay
            delay(ElevatorConfig.CLOSE_DOOR_DELAY)

            // 2. Wait for the proximity sensor to be clear
            _isProximityObstructed.first { !it }

            // 3. Close the door
            closeDoor()
        }
    }

    /**
     * Returns true if the door is closing, false otherwise.
     */
    fun closeDoor(): Boolean {
        if (_isProximityObstructed.value) {
            addLog("Request: Close Door (DENIED: Obstructed)")
            return false
        }
        if (elevatorControl.status() == ElevatorProps.Status.DOOR_OPEN) {
            addLog("Request: Close Door")
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