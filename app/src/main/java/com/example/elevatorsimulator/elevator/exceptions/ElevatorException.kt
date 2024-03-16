package com.example.elevatorsimulator.elevator.exceptions

abstract class ElevatorException(message: String) : Exception(message)

class ElevatorNotPoweredOnException(message: String) : ElevatorException(message)

class ElevatorNotIdleException(message: String) : ElevatorException(message)

class IllegalFloorException(message: String) : ElevatorException(message)