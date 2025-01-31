import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "com.ovidiucristurean"
version = "0.1.4"

mavenPublishing {
    // Define coordinates for the published artifact
    coordinates(
        groupId = "io.github.ovicristurean",
        artifactId = "sensor-collector-kmp",
        version = "0.1.4"
    )

    // Configure POM metadata for the published artifact
    pom {
        name.set("KMP Library for collecting sensor data")
        description.set("This library can be used by Android and iOS targets for the shared functionality of collecting sensor data")
        inceptionYear.set("2025")
        url.set("https://github.com/ovicristurean/kmp-sensor-collector")

        licenses {
            license {
                name.set("MIT")
                url.set("https://opensource.org/licenses/MIT")
            }
        }

        // Specify developer information
        developers {
            developer {
                id.set("ovicristurean")
                name.set("Ovidiu Cristurean")
                email.set("cristurean.marius.ovidiu@gmail.com")
            }
        }

        // Specify SCM information
        scm {
            url.set("https://github.com/ovicristurean/kmp-sensor-collector")
        }
    }

    // Configure publishing to Maven Central
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    // Enable GPG signing for all publications
    signAllPublications()
}

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
        publishLibraryVariants("release", "debug")
    }
    
    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "sensor-collector-kmp"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.ovidiucristurean.kmpsensorcollector"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}
