package com.ovidiucristurean.kmpsensorcollector.sensorManager

import com.ovidiucristurean.kmpsensorcollector.collector.SensorCollector
import com.ovidiucristurean.kmpsensorcollector.model.RotationData
import com.ovidiucristurean.kmpsensorcollector.sensorManager.util.convertLongToDegrees
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

internal class RotationSensorCollector(
    private val motionManager: CMMotionManager,
    private val rotationFlow: MutableSharedFlow<RotationData>
) : SensorCollector {

    private var scope: CoroutineScope? = null

    override val isAvailable = motionManager.isDeviceMotionAvailable()

    override fun register() {
        scope = CoroutineScope(Dispatchers.Main)
        if (motionManager.isDeviceMotionAvailable()) {
            motionManager.deviceMotionUpdateInterval = 1.0 / 60.0 // 60 Hz
            motionManager.startDeviceMotionUpdatesToQueue(NSOperationQueue.mainQueue) { motion, error ->
                motion?.let {
                    val attitude = it.attitude
                    val azimuth = convertLongToDegrees(attitude.yaw)   // Rotation around Z-axis
                    val pitch = convertLongToDegrees(attitude.pitch)  // Rotation around X-axis
                    val roll = convertLongToDegrees(attitude.roll)    // Rotation around Y-axis

                    scope?.launch {
                        rotationFlow.emit(
                            RotationData(
                                azimuth = azimuth,
                                pitch = pitch,
                                roll = roll
                            )
                        )
                    }

                    println("iOS: Azimuth: $azimuth, Pitch: $pitch, Roll: $roll")
                }

                error?.let {
                    println("Error: ${it.localizedDescription}")
                }
            }
        } else {
            println("Device motion is not available")
        }
    }

    override fun unregister() {
        motionManager.stopDeviceMotionUpdates()
        scope?.cancel()
        scope = null
    }
}
