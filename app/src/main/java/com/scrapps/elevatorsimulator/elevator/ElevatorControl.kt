package com.scrapps.elevatorsimulator.elevator

import com.scrapps.elevatorsimulator.elevator.exceptions.ElevatorNotIdleException
import com.scrapps.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import com.scrapps.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import kotlinx.coroutines.delay

class ElevatorControl private constructor(
    private val lowestFloor: Int,
    private val highestFloor: Int,
    private var currentFloor: Int,
    private val speed: Long,
    private val elevatorListener: ElevatorListener,
) : ElevatorInterface {
    private var status: ElevatorProps.Status = ElevatorProps.Status.POWER_OFF
    var serviceDirection: ElevatorProps.ServiceDirection = ElevatorProps.ServiceDirection.IDLE
        private set

    @Throws(ElevatorNotPoweredOnException::class)
    override suspend fun move(
        targetFloor: Int,
        onTargetFloorReached: (isTargetFloorReached: Boolean) -> Unit,
    ) {
        if (status == ElevatorProps.Status.POWER_OFF) {
            throw ElevatorNotPoweredOnException("Elevator status is POWER_OFF. Call powerOn() before starting any operations.")
        }
        if (!isValidTargetFloor(targetFloor)) {
            return
        }
        while (!isTargetFloorReached(targetFloor, currentFloor)) {
            val direction = if (currentFloor < targetFloor) {
                serviceDirection = ElevatorProps.ServiceDirection.UP
                ElevatorProps.Status.MOVING_UP
            } else {
                serviceDirection = ElevatorProps.ServiceDirection.DOWN
                ElevatorProps.Status.MOVING_DOWN
            }
            setStatus(direction)
            delay(speed)
            currentFloor += if (currentFloor < targetFloor) 1 else -1
            elevatorListener.onFloorChangeListener(currentFloor)
        }
        onTargetFloorReached(true)
    }

    override fun reportDoorStatus(doorState: ElevatorDoorState) {
        when (doorState) {
            ElevatorDoorState.OPEN -> setStatus(ElevatorProps.Status.DOOR_OPEN)
            ElevatorDoorState.CLOSED -> setStatus(ElevatorProps.Status.IDLE)
            ElevatorDoorState.OPENING -> setStatus(ElevatorProps.Status.DOOR_OPENING)
            ElevatorDoorState.OPENING_STUCK -> {}
            ElevatorDoorState.CLOSING -> setStatus(ElevatorProps.Status.DOOR_CLOSING)
            ElevatorDoorState.CLOSING_STUCK -> {}
        }
    }

    override fun reportTargetReached() {
        setStatus(ElevatorProps.Status.TARGET_FLOOR_REACHED)
    }

    override suspend fun powerOn() {
        if (status == ElevatorProps.Status.POWER_OFF) {
            setStatus(ElevatorProps.Status.POWER_ON)
            delay(1500)
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

    fun getCurrentFloor(): Int {
        return currentFloor
    }

    private fun setStatus(status: ElevatorProps.Status) {
        this.status = status
        if (status == ElevatorProps.Status.IDLE) {
            serviceDirection = ElevatorProps.ServiceDirection.IDLE
        }
        elevatorListener.onStatusChangeListener(status = status)
        elevatorListener.onServiceDirectionChangeListener(serviceDirection)
    }

    private fun isTargetFloorReached(targetFloor: Int, currentFloor: Int) =
        targetFloor == currentFloor

    private fun isValidTargetFloor(targetFloor: Int) =
        targetFloor in lowestFloor..highestFloor

    companion object {
        private var instance: ElevatorControl? = null

        fun getInstance(): ElevatorControl {
            return instance ?: throw UninitializedPropertyAccessException("ElevatorControl is not initialized. Call build() first.")
        }

        fun build(
            lowestFloor: Int,
            highestFloor: Int,
            currentFloor: Int,
            speed: Long,
            elevatorListener: ElevatorListener,
        ): ElevatorControl {
            instance = ElevatorControl(lowestFloor, highestFloor, currentFloor, speed, elevatorListener)
            return instance!!
        }
    }
}

interface ElevatorListener {
    fun onFloorChangeListener(currentFloor: Int)

    fun onStatusChangeListener(status: ElevatorProps.Status)

    fun onServiceDirectionChangeListener(direction: ElevatorProps.ServiceDirection)
}
