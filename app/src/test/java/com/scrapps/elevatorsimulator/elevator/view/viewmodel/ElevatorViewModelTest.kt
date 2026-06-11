package com.scrapps.elevatorsimulator.elevator.view.viewmodel

import com.scrapps.elevatorsimulator.elevator.ElevatorControl
import com.scrapps.elevatorsimulator.elevator.ElevatorListener
import com.scrapps.elevatorsimulator.elevator.ElevatorProps
import com.scrapps.elevatorsimulator.elevator.config.ElevatorConfig
import com.scrapps.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.slot
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ElevatorViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    
    private lateinit var mockConfig: ElevatorConfig
    private lateinit var mockControl: ElevatorControl
    private val listenerSlot = slot<ElevatorListener>()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        mockConfig = mockk(relaxed = true)
        mockControl = mockk(relaxed = true)

        mockkObject(ElevatorConfig.Companion)
        every { ElevatorConfig.getInstance() } returns mockConfig
        
        mockkObject(ElevatorControl.Companion)
        every { ElevatorControl.getInstance() } returns mockControl
        every { ElevatorControl.build(any(), any(), any(), any(), capture(listenerSlot)) } returns mockControl
        
        // Mock default config values for init
        every { mockConfig.getLowestFloor() } returns 0
        every { mockConfig.getHighestFloor() } returns 10

        // Mock status to IDLE
        every { mockControl.status() } returns ElevatorProps.Status.IDLE
        
        // Mock reportDoorStatus to update status via listener
        every { mockControl.reportDoorStatus(any()) } answers {
            val state = firstArg<ElevatorDoorState>()
            val status = when (state) {
                ElevatorDoorState.OPEN -> ElevatorProps.Status.DOOR_OPEN
                ElevatorDoorState.CLOSED -> ElevatorProps.Status.IDLE
                ElevatorDoorState.OPENING -> ElevatorProps.Status.DOOR_OPENING
                ElevatorDoorState.CLOSING -> ElevatorProps.Status.DOOR_CLOSING
                else -> ElevatorProps.Status.IDLE
            }
            if (listenerSlot.isCaptured) {
                listenerSlot.captured.onStatusChangeListener(status)
            }
        }
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkAll()
    }

    @Test
    fun `initial state should reflect config`() {
        val viewModel = ElevatorViewModel()
        assertEquals(0, viewModel.getLowestFloor())
        assertEquals(10, viewModel.getHighestFloor())
    }

    @Test
    fun `powerOn should call elevatorControl powerOn`() = runTest {
        val viewModel = ElevatorViewModel()
        viewModel.powerOn()
        coVerify { mockControl.powerOn() }
    }

    @Test
    fun `powerOff should call elevatorControl powerOff when IDLE`() {
        every { mockControl.status() } returns ElevatorProps.Status.IDLE
        val viewModel = ElevatorViewModel()
        viewModel.powerOff()
        verify { mockControl.powerOff() }
    }

    @Test
    fun `setProximityObstructed should update log`() {
        val viewModel = ElevatorViewModel()
        viewModel.setProximityObstructed(true)
        
        val logs = viewModel.logs.value
        assertTrue(logs.any { it.contains("Proximity: OBSTRUCTED") })
    }

    @Test
    fun `openDoor should update openDoor state and status`() {
        val viewModel = ElevatorViewModel()
        val result = viewModel.openDoor()
        
        assertTrue(result)
        assertTrue(viewModel.openDoor.value)
        assertEquals(ElevatorProps.Status.DOOR_OPENING, viewModel.elevatorStatus.value)
    }

    @Test
    fun `closeDoor should update openDoor state to false`() {
        // First open it
        every { mockControl.status() } returns ElevatorProps.Status.DOOR_OPEN
        
        val viewModel = ElevatorViewModel()
        val result = viewModel.closeDoor()
        
        assertTrue(result)
        assertTrue(!viewModel.openDoor.value)
        assertEquals(ElevatorProps.Status.DOOR_CLOSING, viewModel.elevatorStatus.value)
    }

    @Test
    fun `onFloorPressed should move elevator to target floor`() = runTest {
        // Force starting floor to 0
        every { mockConfig.getLowestFloor() } returns 0
        every { mockConfig.getHighestFloor() } returns 0 
        
        val viewModel = ElevatorViewModel()
        
        // Restore real config range
        every { mockConfig.getHighestFloor() } returns 10
        
        // Mock move to update floor via listener
        coEvery { mockControl.move(any(), any()) } answers {
            val targetFloor = firstArg<Int>()
            val callback = secondArg<(Boolean) -> Unit>()
            if (listenerSlot.isCaptured) {
                listenerSlot.captured.onFloorChangeListener(targetFloor)
            }
            callback(true)
        }

        viewModel.onFloorPressed(5)
        
        assertEquals(5, viewModel.currentFloor.value)
        assertTrue(viewModel.floorsInQueue.value.isEmpty())
    }

    @Test
    fun `proximity obstruction during closing should reopen door`() {
        val viewModel = ElevatorViewModel()
        
        // Simulate DOOR_CLOSING status in the control
        every { mockControl.status() } returns ElevatorProps.Status.DOOR_CLOSING
        
        viewModel.setProximityObstructed(true)
        
        assertTrue(viewModel.openDoor.value)
        assertEquals(ElevatorProps.Status.DOOR_OPENING, viewModel.elevatorStatus.value)
    }
}
