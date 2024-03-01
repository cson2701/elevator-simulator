package com.example.elevatorsimulator.elevator

import kotlinx.coroutines.delay

class Elevator(
    private val lowestFloor: Int,
    private val highestFloor: Int,
    var currentFloor: Int,
    private val speed: Long,
) : ElevatorInterface {

    private lateinit var status: ElevatorProps.Status

    override suspend fun move(targetFloor: Int, elevatorListener: ElevatorListener): Boolean {
        if (!isValidTargetFloor(targetFloor)) {
            return false
        }
        while (!isTargetFloorReached(targetFloor, currentFloor)) {
            val direction = if (currentFloor < targetFloor) {
                ElevatorProps.Status.MOVING_UP
            } else {
                ElevatorProps.Status.MOVING_DOWN
            }
            setStatus(direction)
            elevatorListener.onStatusChangeListener(direction)
            delay(speed)
            currentFloor += if (currentFloor < targetFloor) 1 else -1
            elevatorListener.onFloorChangeListener(currentFloor)
        }
        setStatus(ElevatorProps.Status.IDLE)
        elevatorListener.onStatusChangeListener(ElevatorProps.Status.IDLE)
        return true
    }

    override fun status(): ElevatorProps.Status {
        return status
    }

    private fun setStatus(status: ElevatorProps.Status) {
        this.status = status
    }

    private fun isTargetFloorReached(targetFloor: Int, currentFloor: Int) =
        targetFloor == currentFloor

    private fun isValidTargetFloor(targetFloor: Int) =
        !(targetFloor > highestFloor || targetFloor < lowestFloor)
}

interface ElevatorListener {
    fun onFloorChangeListener(currentFloor: Int)

    fun onStatusChangeListener(status: ElevatorProps.Status)
}
