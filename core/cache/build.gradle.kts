plugins {
    alias(libs.plugins.syncclip.android.library)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.cache"
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.kotlinx.coroutines.core)
}
