plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.syncclip.koin)
}

kotlin {
    // Android target configuration
    android {
        namespace = "com.github.slavikjunior.synchronizedclipboard.core.crypto"
        compileSdk = 37
        minSdk = 24

        // Java 21 toolchain — синхронно с остальными модулями проекта
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // iOS targets.
    // iosX64 отсутствует осознанно: androidx.sqlite 2.7.x исключил iosX64 из
    // публикаций, и целевой набор приведён к единому виду с :core:database.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "crypto"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
        }

        // Android-реализация крипты остаётся на JCA (`javax.crypto`) с ключом
        // в AndroidKeyStore — ключ неэкспортируем, поэтому whyoleg там не
        // используется. Зависимость объявлена только для iOS: там
        // provider-optimal разворачивается в CryptoKit (+ CommonCrypto fallback).
        iosMain.dependencies {
            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider.optimal)
            // runBlocking-мост для suspend-API whyoleg → sync-контракт CryptoManager
            implementation(libs.kotlinx.coroutines.core)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
