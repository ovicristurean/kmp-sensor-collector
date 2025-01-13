package com.ovidiucristurean.kmpsensorcollector

import android.app.Activity.SENSOR_SERVICE
import android.content.Context
import android.hardware.SensorManager
import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import com.ovidiucristurean.kmpsensorcollector.model.SensorType
import com.ovidiucristurean.kmpsensorcollector.sensormanager.AccelerometerSensorManager
import com.ovidiucristurean.kmpsensorcollector.sensormanager.RotationSensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

class AndroidPhoneSensorManager(
    context: Context
) : PhoneSensorManager {

    private val sensorManager = context.getSystemService(SENSOR_SERVICE) as SensorManager

    private val _rotationData = MutableSharedFlow<RotationData>()
    override val rotationData: SharedFlow<RotationData>
        get() = _rotationData

    private val rotationSensorManager = RotationSensorManager(sensorManager, _rotationData)

    private val _accelerometerData = MutableSharedFlow<AccelerometerData>()
    override val accelerometerData: SharedFlow<AccelerometerData>
        get() = _accelerometerData

    private val accelerometerSensorManager =
        AccelerometerSensorManager(sensorManager, _accelerometerData)

    override fun registerSensor(sensorType: SensorType) {
        when (sensorType) {
            SensorType.ROTATION_VECTOR -> {
                rotationSensorManager.register()
            }

            SensorType.ACCELEROMETER -> {
                accelerometerSensorManager.register()
            }
        }
    }

    override fun unregisterSensor(sensorType: SensorType) {
        when (sensorType) {
            SensorType.ROTATION_VECTOR -> {
                rotationSensorManager.unregister()
            }

            SensorType.ACCELEROMETER -> {
                accelerometerSensorManager.unregister()
            }
        }

    }

}