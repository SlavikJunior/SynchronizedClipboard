plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.feature.clipboard.api"

    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.navigation3.runtime)
}
