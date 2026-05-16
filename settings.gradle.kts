pluginManagement {
    includeBuild("build-logic")
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

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "UmnaFizika"

include(":app")

// Core modules
include(":core:core-ui")
include(":core:core-network")
include(":core:core-database")
include(":core:core-common")

// Feature modules
include(":feature:feature-formulas")
include(":feature:feature-games")
include(":feature:feature-tests")
include(":feature:feature-solver")
include(":feature:feature-ai-chat")
