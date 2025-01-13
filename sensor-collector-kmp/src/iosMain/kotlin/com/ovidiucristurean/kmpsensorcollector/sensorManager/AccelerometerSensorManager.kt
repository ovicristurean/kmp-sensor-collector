package com.ovidiucristurean.kmpsensorcollector.sensorManager

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

class AccelerometerSensorManager(
    private val motionManager: CMMotionManager,
    private val accelerometerFlow: MutableSharedFlow<AccelerometerData>
) {

    private var scope: CoroutineScope? = null

    @OptIn(ExperimentalForeignApi::class)
    fun register() {
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

    fun unregister() {
        motionManager.stopAccelerometerUpdates()
        scope?.cancel()
        scope = null
    }
}
