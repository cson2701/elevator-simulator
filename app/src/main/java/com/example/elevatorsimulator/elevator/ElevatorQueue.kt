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

        val currentFloor = try {
            ElevatorControl.getInstance().getCurrentFloor()
        } catch (_: Exception) {
            0 // Default to 0 if not initialized
        }

        if (floor > currentFloor) {
            upQueue = (upQueue + floor).distinct().sorted()
        } else if (floor < currentFloor) {
            downQueue = (downQueue + floor).distinct().sortedDescending()
        } else {
            // Already at the floor - add it to the front to trigger the door cycle
            _queue.update { (listOf(floor) + it).distinct() }
            return
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
