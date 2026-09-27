plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kmp.library)
    alias(libs.plugins.syncclip.koin)
    alias(libs.plugins.google.ksp)          // Room compiler — KSP (единственное применение KSP в проекте)
    alias(libs.plugins.androidx.room)        // Room Gradle Plugin: схемы + KMP-конфигурации Room
}

kotlin {
    // Android target configuration
    android {
        namespace = "com.github.slavikjunior.synchronizedclipboard.core.database"
        compileSdk = 37
        minSdk = 24

        // JVM target configuration
        compilerOptions {
            jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
        }
    }

    // iOS targets.
    // iosX64 намеренно отсутствует: androidx.sqlite 2.7.x (единственная версия,
    // совместимая с Room 2.8.x) больше не публикует iosX64-варианты.
    listOf(
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "database"
            isStatic = true
        }
    }

    sourceSets {
        commonMain.dependencies {
            // Диспатчеры берём из :core:di — хардкод Dispatchers.IO в core-модулях запрещён
            implementation(project(":core:di"))

            implementation(libs.koin.core)
            implementation(libs.koin.annotations)
            implementation(libs.kotlinx.coroutines.core)

            // Room KMP runtime + DAO-аннотации
            implementation(libs.androidx.room.runtime)
        }

        androidMain.dependencies {
            // room-ktx тянет kotlinx-coroutines-android (JVM-only) → только на Android
            implementation(libs.androidx.room.ktx)
        }

        iosMain.dependencies {
            // Нативный SQLite для iOS. На Android Room работает через
            // SupportSQLiteOpenHelper, поэтому драйвер нужен только здесь.
            implementation(libs.androidx.sqlite.bundled)
        }
    }
}

dependencies {
    // Room KSP-процессор. Конфигурации Room Gradle Plugin создаёт сам, поэтому
    // после его применения блок можно было бы опустить — оставляем явными,
    // т.к. плагин лишь добавляет обработчик, не настраивая target-конфигурации.
    // См. https://developer.android.com/kotlin/multiplatform/room#set_up_dependencies
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    // Плагин требует каталог схем; exportSchema = false в @Database означает,
    // что Room не будет туда ничего писать, но путь должен быть объявлен.
    schemaDirectory("$projectDir/schemas")
}