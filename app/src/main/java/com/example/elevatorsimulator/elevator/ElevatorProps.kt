package com.example.elevatorsimulator.elevator

class ElevatorProps {
    enum class Speed(val value: Long) {
        SPEED_1(1000),
        SPEED_1_5(1500),
        SPEED_2(2000),
        SPEED_2_5(2500),
        SPEED_3(3000),
        SPEED_3_5(3500),
    }

    enum class Status{
        MOVING_UP,
        MOVING_DOWN,
        IDLE,
        DOOR_OPENING,
        DOOR_OPEN,
        DOOR_CLOSING,
        DOOR_CLOSED,
        POWER_OFF,
        POWER_ON,
    }
}