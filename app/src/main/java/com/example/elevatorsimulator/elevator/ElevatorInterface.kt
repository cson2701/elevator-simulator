package com.example.elevatorsimulator.elevator

interface ElevatorInterface {
    fun move(targetFloor: Int, floorChangeListener: FloorChangeListener): Boolean

}