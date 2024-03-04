package com.example.elevatorsimulator.elevator

class ElevatorBuilder(private val elevatorListener: ElevatorListener) {
    private var numberOfElevators = 1
    private var lowestFloor = 1
    private var highestFloor = 1
    private var currentFloor = 1
    private var speed = 2000L

    fun setNumberOfElevators(numberOfElevators: Int): ElevatorBuilder {
        this.numberOfElevators = numberOfElevators
        return this
    }

    fun setLowestFloor(lowestFloor: Int): ElevatorBuilder {
        this.lowestFloor = lowestFloor
        return this
    }

    fun setHighestFloor(highestFloor: Int): ElevatorBuilder {
        this.highestFloor = highestFloor
        return this
    }

    fun setCurrentFloor(currentFloor: Int): ElevatorBuilder {
        this.currentFloor = currentFloor
        return this
    }

    fun setSpeed(speed: Long): ElevatorBuilder {
        this.speed = speed
        return this
    }

    fun build(): Elevator {
        return Elevator(lowestFloor, highestFloor, currentFloor, speed, elevatorListener)
    }
}