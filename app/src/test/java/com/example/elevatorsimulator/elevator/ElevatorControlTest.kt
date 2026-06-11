package com.example.elevatorsimulator.elevator

import com.example.elevatorsimulator.elevator.exceptions.ElevatorNotPoweredOnException
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ElevatorControlTest {

    private lateinit var elevatorListener: ElevatorListener
    private lateinit var elevatorControl: ElevatorControl

    @Before
    fun setUp() {
        elevatorListener = mockk(relaxed = true)
        elevatorControl = ElevatorControl.build(
            lowestFloor = 0,
            highestFloor = 10,
            currentFloor = 0,
            speed = 0, // Set to 0 for faster tests
            elevatorListener = elevatorListener
        )
    }

    @Test
    fun `powerOn should change status to POWER_ON then IDLE`() = runTest {
        elevatorControl.powerOn()
        verify { elevatorListener.onStatusChangeListener(ElevatorProps.Status.POWER_ON) }
        verify { elevatorListener.onStatusChangeListener(ElevatorProps.Status.IDLE) }
        assertEquals(ElevatorProps.Status.IDLE, elevatorControl.status())
    }

    @Test(expected = ElevatorNotPoweredOnException::class)
    fun `move should throw exception if not powered on`() = runTest {
        elevatorControl.move(5) {}
    }

    @Test
    fun `move should change floors and reach target`() = runTest {
        elevatorControl.powerOn()
        
        var reached = false
        elevatorControl.move(2) { reached = it }
        
        assertEquals(2, elevatorControl.getCurrentFloor())
        assertEquals(true, reached)
        
        verify { elevatorListener.onFloorChangeListener(1) }
        verify { elevatorListener.onFloorChangeListener(2) }
        verify { elevatorListener.onStatusChangeListener(ElevatorProps.Status.MOVING_UP) }
    }

    @Test
    fun `move to invalid floor should do nothing`() = runTest {
        elevatorControl.powerOn()
        elevatorControl.move(11) {}
        assertEquals(0, elevatorControl.getCurrentFloor())
    }

    @Test
    fun `move down should update status to MOVING_DOWN`() = runTest {
        // Build at floor 5
        elevatorControl = ElevatorControl.build(
            lowestFloor = 0,
            highestFloor = 10,
            currentFloor = 5,
            speed = 0,
            elevatorListener = elevatorListener
        )
        elevatorControl.powerOn()
        
        elevatorControl.move(3) {}
        
        assertEquals(3, elevatorControl.getCurrentFloor())
        verify { elevatorListener.onStatusChangeListener(ElevatorProps.Status.MOVING_DOWN) }
    }
}
