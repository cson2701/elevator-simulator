package com.example.elevatorsimulator.elevator

interface ElevatorInterface {
    suspend fun move(
        targetFloor: Int,
        onTargetFloorReached: (isTargetFloorReached: Boolean) -> Unit,
    ): Boolean

    suspend fun openDoor()

    suspend fun closeDoor()

    fun status(): ElevatorProps.Status

    suspend fun powerOn()

    fun powerOff()
}