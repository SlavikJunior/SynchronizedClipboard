package com.github.slavikjunior.synchronizedclipboard

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")
        target.configureAndroidModule()
        // Koin Compiler Plugin (Kotlin Compiler Plugin) для @Module/@Single аннотаций.
        // Применяем везде, где есть Koin annotations (core:network, core:database, feature-модули).
        target.plugins.apply("com.github.slavikjunior.synchronizedclipboard.koin")
    }
}
