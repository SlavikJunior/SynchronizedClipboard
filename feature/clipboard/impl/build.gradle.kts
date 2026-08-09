plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.syncclip.android.compose)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.feature.clipboard.impl"
}

dependencies {
    implementation(project(":feature:clipboard:api"))
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    implementation(project(":feature:devices:api"))
    implementation(project(":feature:settings:api"))

    implementation(project(":core:crypto"))
    implementation(project(":core:database"))
    implementation(project(":core:cache"))
    implementation(project(":core:di"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.android)
    implementation(libs.koin.androidx.compose)

    implementation(libs.androidx.navigation3.runtime)

    implementation(libs.androidx.compose.material.icons.extended)
}
