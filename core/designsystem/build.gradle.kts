plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.syncclip.android.compose)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.designsystem"
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.compose.material.icons.extended)
}
