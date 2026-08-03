plugins {
    alias(libs.plugins.syncclip.android.application)
    alias(libs.plugins.syncclip.android.compose)
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
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    // Внутренние модули
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
}
