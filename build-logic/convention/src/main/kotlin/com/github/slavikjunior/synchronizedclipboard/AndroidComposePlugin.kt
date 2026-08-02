package com.github.slavikjunior.synchronizedclipboard

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidComposePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.jetbrains.kotlin.plugin.compose")

        with(target.dependencies) {
            add("implementation", platform("androidx.compose:compose-bom:2026.06.01"))
            add("implementation", "androidx.activity:activity-compose:1.13.0")
            add("implementation", "androidx.compose.ui:ui")
            add("implementation", "androidx.compose.ui:ui-graphics")
            add("implementation", "androidx.compose.ui:ui-tooling-preview")
            add("implementation", "androidx.compose.material3:material3")
        }
    }
}
