package com.ovidiucristurean.kmpsensorcollector.collector

internal interface SensorCollector {
    val isAvailable: Boolean
    fun register()
    fun unregister()
}
