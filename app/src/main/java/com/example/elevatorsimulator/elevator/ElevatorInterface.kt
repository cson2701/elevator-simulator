package com.example.elevatorsimulator.elevator

interface ElevatorInterface {
    suspend fun move(
        targetFloor: Int,
        onTargetFloorReached: (isTargetFloorReached: Boolean) -> Unit,
    ): Boolean

    fun status(): ElevatorProps.Status

    suspend fun powerOn()

    fun powerOff()
}