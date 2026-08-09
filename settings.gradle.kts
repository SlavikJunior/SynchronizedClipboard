pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SynchronizedClipboard"
include(":app")
include(":core:navigation")
include(":core:designsystem")
include(":core:network")
include(":core:database")
include(":core:crypto")
include(":core:di")
include(":core:cache")
include(":feature:auth:api")
include(":feature:auth:impl")
include(":feature:clipboard:api")
include(":feature:clipboard:impl")
include(":feature:devices:api")
include(":feature:devices:impl")
include(":feature:settings:api")
include(":feature:settings:impl")
includeBuild("build-logic")
