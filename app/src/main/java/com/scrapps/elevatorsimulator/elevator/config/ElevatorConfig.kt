package com.scrapps.elevatorsimulator.elevator.config

import com.scrapps.elevatorsimulator.elevator.ElevatorProps
import com.scrapps.elevatorsimulator.elevator.exceptions.IllegalFloorException
import com.scrapps.elevatorsimulator.utils.TinyDbSingleton

class ElevatorConfig private constructor() {

    private val tinyDB get() = TinyDbSingleton.getInstance()

    fun getLowestFloor(): Int {
        return tinyDB?.getInt("lowestFloor", 0) ?: 0
    }

    fun getHighestFloor(): Int {
        return tinyDB?.getInt("highestFloor", 0) ?: 0
    }

    fun getSpeed(): Long {
        val speed = tinyDB?.getLong("speed") ?: 0L
        return if (speed == 0L) ElevatorProps.Speed.SPEED_1.value else speed
    }

    fun getDoorOpenDuration(): Long {
        val duration = tinyDB?.getLong("doorOpenDuration") ?: 0L
        if (duration != 0L) return duration
        
        // Fallback to legacy doorSpeed or default
        val legacySpeed = tinyDB?.getLong("doorSpeed") ?: 0L
        return if (legacySpeed == 0L) 1200L else legacySpeed
    }

    fun getDoorCloseDuration(): Long {
        val duration = tinyDB?.getLong("doorCloseDuration") ?: 0L
        if (duration != 0L) return duration

        // Fallback to legacy doorSpeed or default
        val legacySpeed = tinyDB?.getLong("doorSpeed") ?: 0L
        return if (legacySpeed == 0L) 1200L else legacySpeed
    }

    fun getPlaySound(): Boolean {
        return if (tinyDB?.objectExists("playSound") == true) {
            tinyDB?.getBoolean("playSound") ?: true
        } else {
            true
        }
    }

    fun getAnnounceFloor(): Boolean {
        return if (tinyDB?.objectExists("announceFloor") == true) {
            tinyDB?.getBoolean("announceFloor") ?: true
        } else {
            true
        }
    }

    @Throws(IllegalFloorException::class)
    fun save(
        lowestFloor: Int,
        highestFloor: Int,
        speed: Long = getSpeed(),
        doorOpenDuration: Long = getDoorOpenDuration(),
        doorCloseDuration: Long = getDoorCloseDuration(),
        playSound: Boolean = getPlaySound(),
        announceFloor: Boolean = getAnnounceFloor()
    ): Boolean {
        if (lowestFloor < highestFloor) {
            tinyDB?.putInt("lowestFloor", lowestFloor)
            tinyDB?.putInt("highestFloor", highestFloor)
            tinyDB?.putLong("speed", speed)
            tinyDB?.putLong("doorOpenDuration", doorOpenDuration)
            tinyDB?.putLong("doorCloseDuration", doorCloseDuration)
            tinyDB?.putBoolean("playSound", playSound)
            tinyDB?.putBoolean("announceFloor", announceFloor)
            
            // Clean up legacy key
            tinyDB?.remove("doorSpeed")
            return true
        } else {
            throw IllegalFloorException("Lowest floor must lower than highest floor.")
        }
    }

    companion object {
        const val CLOSE_DOOR_DELAY = 3000L
        private var instance: ElevatorConfig? = null

        fun getInstance(): ElevatorConfig {
            if (instance == null) {
                instance = ElevatorConfig()
            }
            return instance!!
        }
    }
}
