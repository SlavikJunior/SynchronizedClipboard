package com.github.slavikjunior.synchronizedclipboard

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.buildscript
import org.gradle.kotlin.dsl.dependencies

class KoinConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        // Применяем Koin Compiler Plugin по id (добавлен в classpath через buildscript корневого build.gradle.kts)
        target.plugins.apply("io.insert-koin.compiler.plugin")
    }
}
