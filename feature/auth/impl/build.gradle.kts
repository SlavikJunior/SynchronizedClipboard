plugins {
    alias(libs.plugins.syncclip.android.library)
    alias(libs.plugins.syncclip.android.compose)
    // Koin Compiler Plugin (Kotlin Compiler Plugin) auto-applied через
    // AndroidLibraryPlugin → KoinConventionPlugin.
    // НЕ используется KSP для Koin annotations — это compiler plugin 4.x.
}

android {
    namespace = "com.github.slavikjunior.synchronizedclipboard.feature.auth.impl"
}

dependencies {
    implementation(project(":feature:auth:api"))
    implementation(project(":core:navigation"))
    implementation(project(":core:designsystem"))
    // Провайдеры сетевого слоя (AuthTokenProvider/DeviceIdentityProvider)
    // реализуются здесь — интерфейсы живут в :core:network.
    implementation(project(":core:network"))
    // Диспатчеры (io_dispatcher) для main-safe чтения EncryptedSharedPreferences.
    implementation(project(":core:di"))

    // core-ktx + lifecycle (выделены из AndroidFeaturePlugin для явности)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // Koin 4.x — compiler plugin auto-applied, runtime + annotations нужны вручную
    implementation(platform(libs.koin.bom))
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.android)              // ViewModel factory bridge
    implementation(libs.koin.androidx.compose)    // koinViewModel()

    // Navigation 3 (EntryProviderScope, NavKey) — через :core:navigation transitively,
    // но добавляем явно для стабильности API.
    implementation(libs.androidx.navigation3.runtime)

    implementation(libs.androidx.security.crypto)

    // Хранилище deviceId (DataStore Preferences).
    implementation(libs.androidx.datastore.preferences)
}
