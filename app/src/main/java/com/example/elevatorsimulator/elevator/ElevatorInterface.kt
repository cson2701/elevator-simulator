package com.example.elevatorsimulator.elevator

import com.example.elevatorsimulator.elevator.view.compose.ElevatorDoorState

interface ElevatorInterface {
    suspend fun move(
        targetFloor: Int,
        onTargetFloorReached: (isTargetFloorReached: Boolean) -> Unit,
    )

    fun reportDoorStatus(doorState: ElevatorDoorState)

    fun reportTargetReached()

    fun status(): ElevatorProps.Status

    suspend fun powerOn()

    fun powerOff()
}