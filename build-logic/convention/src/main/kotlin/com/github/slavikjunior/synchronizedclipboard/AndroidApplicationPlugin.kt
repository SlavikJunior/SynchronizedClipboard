package com.github.slavikjunior.synchronizedclipboard

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidApplicationPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.application")

        target.configureAndroidModule()

        // Koin Compiler Plugin для generated DI-модулей (:core:network, :core:database).
        target.plugins.apply("com.github.slavikjunior.synchronizedclipboard.koin")
    }
}
