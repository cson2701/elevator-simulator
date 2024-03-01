package com.example.elevatorsimulator.elevator

interface ElevatorInterface {
    suspend fun move(targetFloor: Int, elevatorListener: ElevatorListener): Boolean

    fun status(): ElevatorProps.Status
}