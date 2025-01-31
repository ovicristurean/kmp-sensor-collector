package com.ovidiucristurean.kmpsensorcollector

import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import com.ovidiucristurean.kmpsensorcollector.model.SensorType
import kotlinx.coroutines.flow.SharedFlow

interface PhoneSensorManager {
    val rotationData: SharedFlow<RotationData>
    val accelerometerData: SharedFlow<AccelerometerData>

    fun isSensorAvailable(sensorType: SensorType): Boolean

    fun registerSensor(sensorType: SensorType)
    fun unregisterSensor(sensorType: SensorType)
}
