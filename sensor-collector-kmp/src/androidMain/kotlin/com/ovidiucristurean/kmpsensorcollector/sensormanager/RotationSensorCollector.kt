package com.ovidiucristurean.kmpsensorcollector.sensormanager

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.ovidiucristurean.kmpsensorcollector.collector.SensorCollector
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

internal class RotationSensorCollector(
    private val sensorManager: SensorManager,
    private val rotationFlow: MutableSharedFlow<RotationData>
) : SensorEventListener, SensorCollector {
    private var rotationVectorSensor: Sensor? =
        sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

    private var scope: CoroutineScope? = null

    override val isAvailable = rotationVectorSensor != null

    override fun register() {
        scope = CoroutineScope(Dispatchers.Main)
        sensorManager.registerListener(
            this,
            rotationVectorSensor,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    override fun unregister() {
        sensorManager.unregisterListener(this, rotationVectorSensor)
        scope?.cancel()
        scope = null
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val rotationMatrix = FloatArray(9)
        SensorManager.getRotationMatrixFromVector(rotationMatrix, event?.values)

        val orientationAngles = FloatArray(3)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)

        val azimuth = Math.toDegrees(orientationAngles[0].toDouble()) // Rotation around Z-axis
        val pitch = Math.toDegrees(orientationAngles[1].toDouble())   // Rotation around X-axis
        val roll = Math.toDegrees(orientationAngles[2].toDouble())    // Rotation around Y-axis

        scope?.launch {
            rotationFlow.emit(
                RotationData(
                    azimuth = azimuth,
                    pitch = pitch,
                    roll = roll
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) = Unit
}