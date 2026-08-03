plugins {
    alias(libs.plugins.syncclip.android.library)
    // Koin Compiler Plugin применяется автоматически через AndroidLibraryPlugin.
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.network"

    // :core:network — чисто backend-модуль (Ktor), без UI.
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)

    // Ktor client (Android engine = OkHttp)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.okhttp)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)
}
