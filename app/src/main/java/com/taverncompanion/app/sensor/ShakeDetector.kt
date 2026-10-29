package com.taverncompanion.app.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTimestamp: Long = 0L

    companion object {
        private const val SHAKE_THRESHOLD_GRAVITY = 12.5f

        private const val SHAKE_SLUMP_TIME_MS = 600
    }

    fun startListening() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val accelerationMagnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        val netAcceleration = accelerationMagnitude - SensorManager.GRAVITY_EARTH

        if (netAcceleration > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()

            if (lastShakeTimestamp + SHAKE_SLUMP_TIME_MS > now) {
                return
            }

            lastShakeTimestamp = now
            onShake()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}