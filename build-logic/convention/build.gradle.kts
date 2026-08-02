plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

group = "com.github.slavikjunior.synchronizedclipboard.buildlogic"

kotlin {
    jvmToolchain(21)
}

dependencies {
    compileOnly(libs.android.gradle.plugin)
    compileOnly(libs.kotlin.gradle.plugin)
    compileOnly(gradleApi())
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = libs.plugins.syncclip.android.application.get().pluginId
            implementationClass = "com.github.slavikjunior.synchronizedclipboard.AndroidApplicationPlugin"
        }
        register("androidLibrary") {
            id = libs.plugins.syncclip.android.library.get().pluginId
            implementationClass = "com.github.slavikjunior.synchronizedclipboard.AndroidLibraryPlugin"
        }
        register("androidCompose") {
            id = libs.plugins.syncclip.android.compose.get().pluginId
            implementationClass = "com.github.slavikjunior.synchronizedclipboard.AndroidComposePlugin"
        }
        register("androidFeature") {
            id = libs.plugins.syncclip.android.feature.get().pluginId
            implementationClass = "com.github.slavikjunior.synchronizedclipboard.AndroidFeaturePlugin"
        }
    }
}
