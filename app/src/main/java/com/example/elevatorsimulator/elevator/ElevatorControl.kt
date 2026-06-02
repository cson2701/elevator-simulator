package com.example.elevatorsimulator.elevator

import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotIdleException
import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import com.example.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import kotlinx.coroutines.delay

class ElevatorControl(
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
    ) {
        if (status == ElevatorProps.Status.POWER_OFF) {
            throw ElevatorNotPoweredOnException("Elevator status is POWER_OFF. Call powerOn() before starting any operations.")
        }
        if (!isValidTargetFloor(targetFloor)) {
            return
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
//        openDoor()
    }
//
//    override suspend fun openDoor() {
//        if (status == ElevatorProps.Status.IDLE || status == ElevatorProps.Status.DOOR_CLOSING) {
//            setStatus(ElevatorProps.Status.DOOR_OPENING)
////            val doorOpeningJob = CoroutineScope(Dispatchers.Main).launch {
////                delay(1000)
////                setStatus(ElevatorProps.Status.DOOR_OPEN)
////                delay(1500)
////                closeDoor()
////            }
//            // TODO: Make door close interrupt-able
//            // Check if the coroutine has been cancelled before proceeding further.
////            if (isActive) {
////                // Wait for the door opening process to complete.
////                doorOpeningJob.join()
////            }
//        }
//    }
//
//    override suspend fun closeDoor() {
//        if (status == ElevatorProps.Status.DOOR_OPEN) {
//            setStatus(ElevatorProps.Status.DOOR_CLOSING)
////            delay(1000)
////            setStatus(ElevatorProps.Status.IDLE)
//        }
//    }

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

    override suspend fun powerOn() {
        if (status == ElevatorProps.Status.POWER_OFF) {
            delay(2500)
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
        targetFloor in lowestFloor..highestFloor
}

interface ElevatorListener {
    fun onFloorChangeListener(currentFloor: Int)

    fun onStatusChangeListener(status: ElevatorProps.Status)
}
