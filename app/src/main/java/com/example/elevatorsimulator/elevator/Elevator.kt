package com.example.elevatorsimulator.elevator

class Elevator(
    private val lowestFloor: Int,
    private val highestFloor: Int,
    var currentFloor: Int,
    private val speed: Long
) : ElevatorInterface {
//    val numberOfFloors = 10

    override fun move(targetFloor: Int, floorChangeListener: FloorChangeListener): Boolean {
        if (!isValidTargetFloor(targetFloor)) {
            return false
        }
        while (!isTargetFloorReached(targetFloor, currentFloor)) {
            Thread.sleep(speed)
            if (currentFloor < targetFloor) {
                currentFloor++
            } else {
                currentFloor--
            }
            floorChangeListener.onFloorChangeListener(currentFloor)
        }
        return true
    }

    private fun isTargetFloorReached(targetFloor: Int, currentFloor: Int) = targetFloor == currentFloor

    private fun isValidTargetFloor(targetFloor: Int) =
        !(targetFloor > highestFloor || targetFloor < lowestFloor)
}

interface FloorChangeListener {
    fun onFloorChangeListener(currentFloor: Int)
}

enum class ElevatorSpeed(val value: Long) {
    SPEED_1(1000),
    SPEED_1_5(1500),
    SPEED_2(2000),
    SPEED_2_5(2500),
    SPEED_3(3000),
    SPEED_3_5(3500),
}
