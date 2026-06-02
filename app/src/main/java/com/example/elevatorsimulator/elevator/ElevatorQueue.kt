package com.example.elevatorsimulator.elevator

class ElevatorQueue {
    private val queue: ArrayDeque<Int> = ArrayDeque()

    fun addFloor(floor: Int) {
        if (!queue.contains(floor)) {
            queue.addLast(floor)
        }
    }

    fun getNextFloorInQueue(): Int? {
        return queue.removeFirstOrNull()
    }

    fun hasNextFloorInQueue(): Boolean {
        return queue.isNotEmpty()
    }
}
