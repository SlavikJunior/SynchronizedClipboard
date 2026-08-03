// Top-level build file where you can add configuration options common to all sub-projects/modules.
//
// Koin Compiler Plugin (Kotlin Compiler Plugin, Koin 4.x) требует jar в classpath.
// koin-compiler-gradle-plugin опубликован в Maven Central без Gradle plugin marker,
// поэтому apply через id("...") НЕ работает. Решение из Context7 CASE_STUDY:
// корневой buildscript добавляет его в classpath, затем модули вызывают
// plugins.apply("io.insert-koin.compiler.plugin") — Gradle разрешает плагин
// из META-INF/gradle-plugins внутри jar.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        // Koin Compiler Plugin — замена KSP для Koin Annotations 4.x.
        // Версия синхронна с gradle.properties: koinCompilerVersion=1.1.0
        classpath("io.insert-koin:koin-compiler-gradle-plugin:${providers.gradleProperty("koinCompilerVersion").get()}")
    }
}
