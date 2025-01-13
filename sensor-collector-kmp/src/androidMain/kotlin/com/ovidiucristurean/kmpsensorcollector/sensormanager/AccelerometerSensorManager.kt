package com.ovidiucristurean.kmpsensorcollector.sensormanager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

class AccelerometerSensorManager(
    private val sensorManager: SensorManager,
    private val accelerometerFlow: MutableSharedFlow<AccelerometerData>
) : SensorEventListener {

    private var scope: CoroutineScope? = null

    private var accelerometerSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    fun register() {
        scope = CoroutineScope(Dispatchers.Main)
        sensorManager.registerListener(
            this,
            accelerometerSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    fun unregister() {
        sensorManager.unregisterListener(this, accelerometerSensor)
        scope?.cancel()
        scope = null
    }

    override fun onSensorChanged(event: SensorEvent) {
        val data = AccelerometerData(
            accelerationX = event.values[0],
            accelerationY = event.values[1],
            accelerationZ = event.values[2]
        )
        scope?.launch { accelerometerFlow.emit(data) }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}