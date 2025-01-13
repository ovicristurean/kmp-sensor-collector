package com.ovidiucristurean.kmpsensorcollector

import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import com.ovidiucristurean.kmpsensorcollector.model.SensorType
import com.ovidiucristurean.kmpsensorcollector.sensorManager.AccelerometerSensorManager
import com.ovidiucristurean.kmpsensorcollector.sensorManager.RotationSensorManager
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import platform.CoreMotion.CMMotionManager

class IosPhoneSensorManager : PhoneSensorManager {

    private val motionManager = CMMotionManager()


    private val _rotationData = MutableSharedFlow<RotationData>()
    override val rotationData: SharedFlow<RotationData>
        get() = _rotationData

    private val rotationSensorManager = RotationSensorManager(motionManager, _rotationData)

    private val _accelerometerData = MutableSharedFlow<AccelerometerData>()
    override val accelerometerData: SharedFlow<AccelerometerData>
        get() = _accelerometerData

    private val accelerometerSensorManager =
        AccelerometerSensorManager(motionManager, _accelerometerData)

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
