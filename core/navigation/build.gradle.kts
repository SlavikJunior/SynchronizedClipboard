plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.core.navigation"

    buildFeatures {
        compose = false
    }
}

dependencies {
    // Nav3Router (arttttt/Nav3Router) — высокоуровневый Router поверх Navigation 3
    implementation(libs.nav3.router)
    // Официальная navigation3: runtime (уже транзитивно через nav3-router,
    // но явно — для стабильности contracts) + ui (NavDisplay, entryProvider DSL)
    implementation(libs.androidx.navigation3.runtime)
    implementation(libs.androidx.navigation3.ui)
    // kotlinx-serialization — нужен для @Serializable Route-ключей навигации
    implementation(libs.kotlinx.serialization.json)
}
