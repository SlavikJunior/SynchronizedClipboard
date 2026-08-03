plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.google.ksp)        // Room compiler — KSP
    // Koin Compiler Plugin применяется автоматически через AndroidLibraryPlugin.
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.database"

    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)

    // Room KMP — runtime
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)

    // Room KSP processor — для генерации skeleton AppDatabase кода
    ksp(libs.androidx.room.compiler)
}
