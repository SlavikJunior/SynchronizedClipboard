plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.syncclip.koin)
}

kotlin {
    // Android target configuration
    android {
        namespace = "com.github.slavikjunior.synchronizedclipboard.core.di"
        compileSdk = 37
        minSdk = 24
        
        // JVM target configuration
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }
    
    // JVM-таргет для desktop-клиента (CLI-демон). Dispatchers.IO на JVM
    // доступен из common coroutines — отдельная реализация не нужна,
    // но actual-декларация обязана существовать.
    jvm {
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // iOS targets
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "di"
            isStatic = true
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
        }
    }
}
