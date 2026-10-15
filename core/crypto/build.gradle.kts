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

    // JVM-таргет для desktop-клиента (CLI-демон на macOS/Linux, Фаза 5 MVP).
    // Ключ PBKDF2 деривируется из парольной фразы (JCA), поэтому Android и JVM
    // используют один и тот же код — он вынесен в промежуточный source set.
    jvm {
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

        // Android и JVM (desktop) используют один и тот же JCA-стек. Общий
        // промежуточный source set (jvmCommon) здесь сознательно НЕ создаётся:
        // в Kotlin 2.4 + AGP 9 ручная wiring'-иерархия ломает Default Hierarchy
        // Template (iosMain выпадает из компиляций). Дублирование ~100 строк
        // JCA-кода между androidMain и jvmMain — осознанный компромисс в пользу
        // работающей сборки; реализации и так разойдутся (AndroidKeyStore).

        // whyoleg остаётся iOS-only зависимостью: provider-optimal
        // разворачивается в CryptoKit (+ CommonCrypto fallback).
        // runBlocking-мост больше не нужен — используются blocking-варианты API.
        iosMain.dependencies {
            implementation(libs.cryptography.core)
            implementation(libs.cryptography.provider.optimal)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
        }
    }
}
