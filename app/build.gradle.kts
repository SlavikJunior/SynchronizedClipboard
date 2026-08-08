plugins {
    alias(libs.plugins.syncclip.android.application)
    alias(libs.plugins.syncclip.android.compose)
    // Koin Compiler Plugin применяется автоматически через convention
    // (AndroidApplicationPlugin → KoinConventionPlugin via buildscript classpath).
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard"

    defaultConfig {
        applicationId = "com.github.slavikjunior.synchronizedclipboard"
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    sourceSets {
        getByName("debug") {
            kotlin.setSrcDirs(listOf("src/main/java", "build/generated/koin/debug/kotlin"))
        }
        getByName("release") {
            kotlin.setSrcDirs(listOf("src/main/java", "build/generated/koin/release/kotlin"))
        }
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.activity.compose)

    // Koin runtime — для startKoin() и androidContext() в Application классе
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    // Navigation 3 — runtime + UI (NavDisplay, NavBackStack, entryProvider)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    implementation(libs.nav3.router)

    // kotlinx-serialization-json для NavBackStackSerializer
    implementation(libs.kotlinx.serialization.json)

    // Внутренние модули
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:network"))
    implementation(project(":core:database"))
    implementation(project(":feature:auth:api"))
    implementation(project(":feature:auth:impl"))
    implementation(project(":feature:clipboard:api"))
    implementation(project(":feature:clipboard:impl"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
