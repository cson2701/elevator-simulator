package com.example.elevatorsimulator.elevator.view

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
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

class ElevatorActivity : ComponentActivity(), SensorEventListener {
    private val elevatorViewModel by viewModels<ElevatorViewModel>()
    private var tts: TextToSpeech? = null
    private lateinit var sensorManager: SensorManager
    private var proximitySensor: Sensor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        tts = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }

        setContent {
            val currentFloor by elevatorViewModel.currentFloor.collectAsState()
            val elevatorStatus by elevatorViewModel.elevatorStatus.collectAsState()
            val serviceDirection by elevatorViewModel.serviceDirection.collectAsState()
            val openDoor by elevatorViewModel.openDoor.collectAsState()
            val floorsInQueue by elevatorViewModel.floorsInQueue.collectAsState()
            val logs by elevatorViewModel.logs.collectAsState()
            val onDoorStateChangeStable = remember { { state: ElevatorDoorState -> onDoorStateChange(state) } }

            ElevatorSimulatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ElevatorScreen(
                        currentFloor = currentFloor,
                        elevatorStatus = elevatorStatus,
                        serviceDirection = serviceDirection,
                        highestFloor = elevatorViewModel.getHighestFloor(),
                        lowestFloor = elevatorViewModel.getLowestFloor(),
                        openDoor = openDoor,
                        floorsInQueue = floorsInQueue,
                        logs = logs,
                        onDoorStateChange = onDoorStateChangeStable,
                        powerOn = ::powerOn,
                        powerOff = ::powerOff,
                        onOpenDoor = { elevatorViewModel.openDoor() },
                        onCloseDoor = { elevatorViewModel.closeDoor() },
                        onFloorPressed = elevatorViewModel::onFloorPressed,
                        onConfigClick = { startActivity(Intent(this, ConfigActivity::class.java)) },
                        onAlarmClick = { playAlarm() }
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

    private fun playAlarm() {
        val mediaPlayer = MediaPlayer.create(this, R.raw.elevator_ding)
        mediaPlayer.setOnCompletionListener {
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
        proximitySensor?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        if (ElevatorConfig.getInstance().getLowestFloor() == 0 || ElevatorConfig.getInstance().getHighestFloor() == 0) {
            startActivity(Intent(this, ConfigActivity::class.java))
            finish()
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor?.type == Sensor.TYPE_PROXIMITY) {
            val distance = event.values[0]
            val isObstructed = distance < (proximitySensor?.maximumRange ?: 0f)
            elevatorViewModel.setProximityObstructed(isObstructed)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        tts?.stop()
        tts?.shutdown()
        super.onDestroy()
    }
}