package com.ovidiucristurean.kmpsensorcollector.sensorManager

import com.ovidiucristurean.kmpsensorcollector.collector.SensorCollector
import com.ovidiucristurean.kmpsensorcollector.model.AccelerometerData
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.useContents
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import platform.CoreMotion.CMMotionManager
import platform.Foundation.NSOperationQueue

internal class AccelerometerSensorCollector(
    private val motionManager: CMMotionManager,
    private val accelerometerFlow: MutableSharedFlow<AccelerometerData>
) : SensorCollector {

    private var scope: CoroutineScope? = null

    override val isAvailable = motionManager.isAccelerometerAvailable()

    @OptIn(ExperimentalForeignApi::class)
    override fun register() {
        scope = CoroutineScope(Dispatchers.Main)
        if (motionManager.isAccelerometerAvailable()) {
            motionManager.accelerometerUpdateInterval = 0.1 // Update every 100ms
            motionManager.startAccelerometerUpdatesToQueue(NSOperationQueue.mainQueue) { data, _ ->
                data?.let {
                    val acceleration = it.acceleration.useContents {
                        AccelerometerData(
                            accelerationX = x.toFloat(),
                            accelerationY = y.toFloat(),
                            accelerationZ = z.toFloat()
                        )
                    }
                    scope?.launch { accelerometerFlow.emit(acceleration) }
                }
            }
        }
    }

    override fun unregister() {
        motionManager.stopAccelerometerUpdates()
        scope?.cancel()
        scope = null
    }
}
