plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.syncclip.android.compose)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.designsystem"
}

dependencies {
    implementation(libs.androidx.compose.material.icons.extended)
}
