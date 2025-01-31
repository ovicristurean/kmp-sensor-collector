package com.ovidiucristurean.kmpsensorcollector.exception

import com.ovidiucristurean.kmpsensorcollector.model.SensorType

class SensorTypeNotFoundException(sensorType: SensorType) :
    Exception("Sensor of type $sensorType does not have a sensor collector registered")
