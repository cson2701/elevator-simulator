package com.example.elevatorsimulator.elevator

import kotlinx.coroutines.delay

class Elevator(
    private val lowestFloor: Int,
    private val highestFloor: Int,
    var currentFloor: Int,
    private val speed: Long,
) : ElevatorInterface {
//    val numberOfFloors = 10

    override suspend fun move(targetFloor: Int, floorChangeListener: FloorChangeListener): Boolean {
        if (!isValidTargetFloor(targetFloor)) {
            return false
        }
        while (!isTargetFloorReached(targetFloor, currentFloor)) {
            delay(speed)
            if (currentFloor < targetFloor) {
                currentFloor++
            } else {
                currentFloor--
            }
            floorChangeListener.onFloorChangeListener(currentFloor)
        }
        return true
    }

    private fun isTargetFloorReached(targetFloor: Int, currentFloor: Int) =
        targetFloor == currentFloor

    private fun isValidTargetFloor(targetFloor: Int) =
        !(targetFloor > highestFloor || targetFloor < lowestFloor)
}

interface FloorChangeListener {
    fun onFloorChangeListener(currentFloor: Int)
}
