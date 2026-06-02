package com.example.elevatorsimulator.elevator

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class ElevatorQueue {
    private val _queue = MutableStateFlow<List<Int>>(emptyList())
    val queue: StateFlow<List<Int>> = _queue.asStateFlow()

    fun addFloor(floor: Int) {
        _queue.update { current ->
            if (current.contains(floor)) current else current + floor
        }
    }

    fun peekNextFloor(): Int? {
        return _queue.value.firstOrNull()
    }

    fun removeFloor(floor: Int) {
        _queue.update { current ->
            current.filter { it != floor }
        }
    }

    fun hasNextFloor(): Boolean {
        return _queue.value.isNotEmpty()
    }
}
