plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.kotlin.serialization)
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.feature.auth.api"

    // :feature:auth:api — чисто контракты (Route, UseCase, Repository). Без UI.
    buildFeatures {
        compose = false
    }
}

dependencies {
    implementation(project(":core:navigation"))
    implementation(libs.kotlinx.serialization.json)

    // androidx.navigation3.runtime (NavKey) объявлен как `implementation` в :core:navigation
    // и не транзитивно экспортируется. AuthRoute : Route : NavKey требует NavKey на classpath.
    implementation(libs.androidx.navigation3.runtime)
}
