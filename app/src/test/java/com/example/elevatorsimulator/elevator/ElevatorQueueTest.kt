package com.example.elevatorsimulator.elevator

import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ElevatorQueueTest {

    private lateinit var elevatorQueue: ElevatorQueue
    private lateinit var elevatorListener: ElevatorListener

    @Before
    fun setUp() {
        elevatorListener = mockk(relaxed = true)
        // Initialize ElevatorControl singleton
        ElevatorControl.build(
            lowestFloor = 0,
            highestFloor = 10,
            currentFloor = 0,
            speed = 0,
            elevatorListener = elevatorListener
        )
        elevatorQueue = ElevatorQueue()
    }

    @Test
    fun `addFloor should add floor to queue when idle`() = runTest {
        elevatorQueue.addFloor(5)
        assertEquals(listOf(5), elevatorQueue.queue.value)
    }

    @Test
    fun `addFloor should sort upQueue correctly`() = runTest {
        elevatorQueue.addFloor(5)
        elevatorQueue.addFloor(3)
        // Since currentFloor is 0, both go to upQueue: [3, 5]
        assertEquals(listOf(3, 5), elevatorQueue.queue.value)
    }

    @Test
    fun `addFloor should handle downQueue correctly`() = runTest {
        // Rebuild at floor 10 to simulate being at the top
        ElevatorControl.build(
            lowestFloor = 0,
            highestFloor = 10,
            currentFloor = 10,
            speed = 0,
            elevatorListener = elevatorListener
        )
        
        elevatorQueue.addFloor(5)
        elevatorQueue.addFloor(7)
        // Current floor is 10, both go to downQueue: [7, 5] (descending)
        assertEquals(listOf(7, 5), elevatorQueue.queue.value)
    }

    @Test
    fun `removeFloor should remove floor from queue`() = runTest {
        elevatorQueue.addFloor(5)
        elevatorQueue.addFloor(3)
        elevatorQueue.removeFloor(5)
        assertEquals(listOf(3), elevatorQueue.queue.value)
    }

    @Test
    fun `peekNextFloor should return first element`() = runTest {
        elevatorQueue.addFloor(5)
        elevatorQueue.addFloor(3)
        assertEquals(3, elevatorQueue.peekNextFloor())
    }
}
