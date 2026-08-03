plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.syncclip.android.compose)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.designsystem"
}

dependencies {
    // Material 3 icons-extended — для иконок в SyncClipEmptyView(..., icon: Painter)
    implementation(libs.androidx.compose.material.icons.extended)
}
