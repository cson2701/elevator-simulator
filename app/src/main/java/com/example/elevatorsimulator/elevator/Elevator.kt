package com.example.elevatorsimulator.elevator

import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotIdleException
import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import kotlinx.coroutines.delay

class Elevator(
    private val lowestFloor: Int,
    private val highestFloor: Int,
    private var currentFloor: Int,
    private val speed: Long,
    private val elevatorListener: ElevatorListener,
) : ElevatorInterface {

    private var status: ElevatorProps.Status = ElevatorProps.Status.POWER_OFF

    @Throws(ElevatorNotPoweredOnException::class)
    override suspend fun move(
        targetFloor: Int,
        onTargetFloorReached: (isTargetFloorReached: Boolean) -> Unit,
    ): Boolean {
        if (status == ElevatorProps.Status.POWER_OFF) {
            throw ElevatorNotPoweredOnException("Elevator status is POWER_OFF. Call powerOn() before starting any operations.")
        }
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
            delay(speed)
            currentFloor += if (currentFloor < targetFloor) 1 else -1
            elevatorListener.onFloorChangeListener(currentFloor)
        }
        setStatus(ElevatorProps.Status.IDLE)
        onTargetFloorReached(true)
        return true
    }

    override suspend fun powerOn() {
        if (status == ElevatorProps.Status.POWER_OFF) {
            delay(2500)
            setStatus(ElevatorProps.Status.POWER_ON)
            delay(500)
            setStatus(ElevatorProps.Status.IDLE)

        }
    }

    override fun powerOff() {
        if (status != ElevatorProps.Status.IDLE) {
            throw ElevatorNotIdleException("Elevator status is not IDLE. Elevator can only be powered off when IDLE.")
        }
        setStatus(ElevatorProps.Status.POWER_OFF)
    }

    override fun status(): ElevatorProps.Status {
        return status
    }

    private fun setStatus(status: ElevatorProps.Status) {
        this.status = status
        elevatorListener.onStatusChangeListener(status = status)
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
