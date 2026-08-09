plugins {
    alias(libs.plugins.syncclip.android.library)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.crypto"

    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
}
