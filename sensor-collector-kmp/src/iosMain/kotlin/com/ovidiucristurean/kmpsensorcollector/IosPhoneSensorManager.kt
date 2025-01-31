package com.ovidiucristurean.kmpsensorcollector

import com.ovidiucristurean.kmpsensorcollector.collector.SensorCollector
import com.ovidiucristurean.kmpsensorcollector.exception.SensorTypeNotFoundException
import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import com.ovidiucristurean.kmpsensorcollector.model.SensorType
import com.ovidiucristurean.kmpsensorcollector.sensorManager.AccelerometerSensorCollector
import com.ovidiucristurean.kmpsensorcollector.sensorManager.RotationSensorCollector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import platform.CoreMotion.CMMotionManager

class IosPhoneSensorManager : PhoneSensorManager {

    private val motionManager = CMMotionManager()

    private val _rotationData = MutableSharedFlow<RotationData>()
    override val rotationData: SharedFlow<RotationData>
        get() = _rotationData

    private val rotationSensorCollector = RotationSensorCollector(motionManager, _rotationData)

    private val _accelerometerData = MutableSharedFlow<AccelerometerData>()
    override val accelerometerData: SharedFlow<AccelerometerData>
        get() = _accelerometerData

    private val accelerometerSensorCollector =
        AccelerometerSensorCollector(motionManager, _accelerometerData)

    private val sensorCollectors: Map<SensorType, SensorCollector> = mapOf(
        SensorType.ROTATION_VECTOR to rotationSensorCollector,
        SensorType.ACCELEROMETER to accelerometerSensorCollector
    )

    override fun isSensorAvailable(sensorType: SensorType): Boolean {
        return sensorCollectors[sensorType]?.isAvailable ?: false
    }

    override fun registerSensor(sensorType: SensorType) {
        sensorCollectors[sensorType]?.register() ?: throw SensorTypeNotFoundException(sensorType)
    }

    override fun unregisterSensor(sensorType: SensorType) {
        sensorCollectors[sensorType]?.unregister() ?: throw SensorTypeNotFoundException(sensorType)
    }

}
