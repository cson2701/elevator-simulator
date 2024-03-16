package com.example.elevatorsimulator.elevator.exceptions

open class ElevatorException(message: String) : Exception(message)

class ElevatorNotPoweredOnException(message: String) : ElevatorException(message)

class ElevatorNotIdleException(message: String) : ElevatorException(message)
