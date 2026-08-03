package com.github.slavikjunior.synchronizedclipboard

import com.android.build.api.dsl.ApplicationExtension
import com.android.build.api.dsl.LibraryExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension

fun Project.configureAndroidModule() {
    val isLibrary = plugins.hasPlugin("com.android.library")

    if (isLibrary) {
        configure<LibraryExtension> {
            compileSdk = 37

            defaultConfig {
                minSdk = 24
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }

            buildFeatures {
                compose = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    } else {
        configure<ApplicationExtension> {
            compileSdk = 37

            defaultConfig {
                minSdk = 24
                targetSdk = 36
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
            }

            compileOptions {
                sourceCompatibility = JavaVersion.VERSION_21
                targetCompatibility = JavaVersion.VERSION_21
            }

            buildTypes {
                release {
                    isMinifyEnabled = false
                }
            }

            buildFeatures {
                compose = true
            }

            packaging {
                resources {
                    excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
            }
        }
    }

    configureKotlin()

    // KSP включен для всех Android-модулей — используется Room (room-compiler)
    // и Koin Annotations (koin-ksp-compiler) для генерации кода DI.
    // Плагин id = "com.google.devtools.ksp" применяется здесь, а не в модуле,
    // чтобы не дублировать в каждом build.gradle.kts.
    // Настройки KSP (args, gen-dirs) задаются в модулях, которые фактически используют KSP,
    // чтобы не навязывать их другим модулям (например, :core:navigation без KSP-аннотаций).
}

fun Project.configureKotlin() {
    configure<KotlinAndroidProjectExtension> {
        jvmToolchain(21)
    }
}
