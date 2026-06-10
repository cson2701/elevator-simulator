package com.example.elevatorsimulator.elevator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ElevatorQueue {
    private val _queue = MutableStateFlow<List<Int>>(emptyList())
    val queue: StateFlow<List<Int>> = _queue.asStateFlow()

    private var upQueue: List<Int> = emptyList()
    private var downQueue: List<Int> = emptyList()

    fun addFloor(floor: Int) {
        if (_queue.value.contains(floor)) return

        val elevatorControl = try {
            ElevatorControl.getInstance()
        } catch (_: Exception) {
            null
        }

        val currentFloor = elevatorControl?.getCurrentFloor() ?: 0
        val status = elevatorControl?.status() ?: ElevatorProps.Status.IDLE

        if (floor > currentFloor) {
            upQueue = (upQueue + floor).distinct().sorted()
        } else if (floor < currentFloor) {
            downQueue = (downQueue + floor).distinct().sortedDescending()
        } else {
            // floor == currentFloor
            when (status) {
                ElevatorProps.Status.MOVING_UP -> {
                    downQueue = (downQueue + floor).distinct().sortedDescending()
                }

                ElevatorProps.Status.MOVING_DOWN -> {
                    upQueue = (upQueue + floor).distinct().sorted()
                }

                ElevatorProps.Status.IDLE, ElevatorProps.Status.TARGET_FLOOR_REACHED -> {
                    // Already at the floor and not yet in a door cycle (or just finished one)
                    _queue.update { (listOf(floor) + it).distinct() }
                }

                else -> {
                    // For other states (DOOR_OPENING, DOOR_OPEN, DOOR_CLOSING), we don't add to queue.
                    // The ViewModel's onFloorPressed will call openDoor() to handle the timer reset.
                    return
                }
            }
        }

        updateQueueState()
    }

    fun peekNextFloor(): Int? {
        return _queue.value.firstOrNull()
    }

    fun removeFloor(floor: Int) {
        upQueue = upQueue.filter { it != floor }
        downQueue = downQueue.filter { it != floor }
        updateQueueState()
    }

    fun hasNextFloor(): Boolean {
        return _queue.value.isNotEmpty()
    }

    private fun updateQueueState() {
        val serviceDirection = try {
            ElevatorControl.getInstance().serviceDirection
        } catch (_: Exception) {
            ElevatorProps.ServiceDirection.IDLE
        }

        _queue.update {
            if (serviceDirection == ElevatorProps.ServiceDirection.DOWN) {
                downQueue + upQueue
            } else {
                upQueue + downQueue
            }
        }
    }
}
