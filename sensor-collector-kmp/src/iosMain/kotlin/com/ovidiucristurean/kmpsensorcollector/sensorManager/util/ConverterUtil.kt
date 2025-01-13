package com.ovidiucristurean.kmpsensorcollector.sensorManager.util

import kotlin.math.PI

fun convertLongToDegrees(radians: Double): Double {
    return radians * (180.0 / PI)
}