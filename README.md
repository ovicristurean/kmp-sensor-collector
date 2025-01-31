This is a Kotlin Multiplatform library which collects data from the phone's rotation vector and accelerometer, with implementations for Android and iOS. A sample app for this library: https://github.com/ovicristurean/SensorCollectorPlayground

Steps to integrate the library:
1. Make sure that the `mavenCentral` is added in `settings.gradle`:

```
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
}
```

2. Declare the library in the `libs.versions.toml`:

```
[versions]
sensor-collector = "0.1.4"

[libraries]
sensor-collector = { module = "io.github.ovicristurean:sensor-collector-kmp", version.ref = "sensor-collector" }
```

3. In the `commonMain` of the shared module's `build.gradle.kts` file of you app, add the dependency:
```
commonMain.dependencies {
    implementation(libs.sensor.collector)
}
```

4. Provide an instance of the `PhoneSensorManager` for android and iOS, manually or using the prefferred DI framework: `AndroidPhoneSensorManager(activityContext)` for Android, or `IosPhoneSensorManager()` for iOS.

5. Inject the `PhoneSensorManager` in the prefferred app component, for example in a `ViewModel`:
```
class SensorListViewModel(
    private val phoneSensorManager: PhoneSensorManager
) : ViewModel() {

}
```

6. You can check if the sensor you want to collect is available using the `isAvailable(sensorType)` method.

7. Register for the `SharedFlow` that emits sensor events, and then call the `registerSensor` method:

```
viewModelScope.launch {
    phoneSensorManager.rotationData.collectLatest { rotationData ->
        _uiState.update { currentState ->
            //update the UI
        }
    }
}
phoneSensorManager.registerSensor(SensorType.ROTATION_VECTOR)
```
