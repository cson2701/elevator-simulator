package com.example.elevatorsimulator.elevator.view

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.elevatorsimulator.R
import com.example.elevatorsimulator.elevator.config.ElevatorConfig
import com.example.elevatorsimulator.elevator.config.view.ConfigActivity
import com.example.elevatorsimulator.elevator.view.compose.ElevatorDoorState
import com.example.elevatorsimulator.elevator.view.compose.ElevatorScreen
import com.example.elevatorsimulator.elevator.view.viewmodel.ElevatorEvent
import com.example.elevatorsimulator.elevator.view.viewmodel.ElevatorViewModel
import com.example.elevatorsimulator.ui.theme.ElevatorSimulatorTheme
import kotlinx.coroutines.launch
import java.util.Locale

class ElevatorActivity : ComponentActivity() {
    private val elevatorViewModel by viewModels<ElevatorViewModel>()
    private var tts: TextToSpeech? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }

        setContent {
            val currentFloor by elevatorViewModel.currentFloor.collectAsState()
            val elevatorStatus by elevatorViewModel.elevatorStatus.collectAsState()
            val openDoor by elevatorViewModel.openDoor.collectAsState()
            val floorsInQueue by elevatorViewModel.floorsInQueue.collectAsState()
            val onDoorStateChangeStable = remember { { state: ElevatorDoorState -> onDoorStateChange(state) } }

            ElevatorSimulatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElevatorScreen(
                        currentFloor = currentFloor,
                        elevatorStatus = elevatorStatus,
                        highestFloor = elevatorViewModel.getHighestFloor(),
                        lowestFloor = elevatorViewModel.getLowestFloor(),
                        openDoor = openDoor,
                        floorsInQueue = floorsInQueue,
                        onDoorStateChange = onDoorStateChangeStable,
                        powerOn = ::powerOn,
                        powerOff = ::powerOff,
                        onFloorPressed = elevatorViewModel::onFloorPressed,
                        onConfigClick = { startActivity(Intent(this, ConfigActivity::class.java)) }
                    )
                }
            }
        }

        collectEvents()
    }

    private fun collectEvents() {
        lifecycleScope.launch {
            elevatorViewModel.elevatorEvent.collect { event ->
                when (event) {
                    is ElevatorEvent.TargetFloorReached -> {
                        playDing(targetFloor = event.floor)
                    }
                }
            }
        }
    }

    private fun playDing(targetFloor: Int) {
        val mediaPlayer = MediaPlayer.create(this, R.raw.elevator_ding)
        mediaPlayer.setOnCompletionListener {
            tts?.speak(
                "Floor, $targetFloor.",
                TextToSpeech.QUEUE_FLUSH,
                null,
                "greeting"
            )

            it.release()
        }
        mediaPlayer.start()
    }

    private fun onDoorStateChange(doorState: ElevatorDoorState) {
        elevatorViewModel.reportDoorState(doorState)
    }

    private fun powerOn() {
        showToast("Powering on...")
        elevatorViewModel.powerOn()
    }

    private fun powerOff() {
        showToast("Powering off...")
        elevatorViewModel.powerOff()
    }

    private fun showToast(message: String) {
        Toast
            .makeText(this, message, Toast.LENGTH_SHORT)
            .show()
    }

    override fun onResume() {
        super.onResume()
        if (ElevatorConfig.getInstance().getLowestFloor() == 0 || ElevatorConfig.getInstance().getHighestFloor() == 0) {
            startActivity(Intent(this, ConfigActivity::class.java))
            finish()
        }
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}