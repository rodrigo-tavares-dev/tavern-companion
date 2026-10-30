package com.taverncompanion.app.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

/**
 * Detector de movimentos físicos para detectar o gesto de shake.
 * Dispara uma ação quando o gesto de shake é detectado.
 *
 * @param context Contexto da aplicação.
 * @param onShake Função a ser executada quando o gesto de shake é detectado.
 */

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    //Obtém o gerenciador de sensores do sistema.
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    //Obtém o sensor de aceleração do dispositivo.
    private val accelerometer: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    //Armazena o tempo do último gesto de shake.
    private var lastShakeTimestamp: Long = 0L

    companion object {
        //Limite de aceleração para detectar o gesto de shake.
        private const val SHAKE_THRESHOLD_GRAVITY = 12.5f

        //Intervalo de tempo entre dois gestos de shake.
        private const val SHAKE_SLUMP_TIME_MS = 600
    }

    /**
     * Registra o detector de sensores para ouvir os eventos de aceleração.
     */
    fun startListening() {
        accelerometer?.let { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_UI
            )
        }
    }

    /**
     * Cancela a escuta dos eventos de aceleração para evitar vazamento de recursos e consumo de bateria.
     */
    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    /**
     * Disparado pelo gerenciador de sensores quando os dados de aceleração são atualizados.
     */
    override fun onSensorChanged(event: SensorEvent) {
        //Ignora eventos que não são de aceleração.
        if (event.sensor.type != Sensor.TYPE_ACCELEROMETER) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        //Calcula a magnitude da aceleração
        val accelerationMagnitude = sqrt((x * x + y * y + z * z).toDouble()).toFloat()

        //Desconta o g da aceleração
        val netAcceleration = accelerationMagnitude - SensorManager.GRAVITY_EARTH

        if (netAcceleration > SHAKE_THRESHOLD_GRAVITY) {
            val now = System.currentTimeMillis()

            //Descarta se o gesto de shake for muito curto.
            if (lastShakeTimestamp + SHAKE_SLUMP_TIME_MS > now) {
                return
            }

            lastShakeTimestamp = now
            onShake()
        }
    }

    /**
     * Disparado pelo gerenciador de sensores quando a precisão do sensor é alterada.
     */
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}