package com.example.elevatorsimulator.elevator

interface ElevatorInterface {
    suspend fun move(targetFloor: Int, floorChangeListener: FloorChangeListener): Boolean

}