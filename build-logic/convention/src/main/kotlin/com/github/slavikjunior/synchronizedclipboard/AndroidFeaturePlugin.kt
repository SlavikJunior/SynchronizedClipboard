package com.github.slavikjunior.synchronizedclipboard

import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidFeaturePlugin : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("com.android.library")

        target.configureAndroidModule()

        with(target.dependencies) {
            add("implementation", "androidx.core:core-ktx:1.19.0")
            add("implementation", "androidx.lifecycle:lifecycle-runtime-ktx:2.11.0")
            add("implementation", "androidx.activity:activity-compose:1.13.0")
        }
    }
}
