pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("jitpack.io") }
    }
}


rootProject.name = "composable-realtime-animations"
include(":sample-app")
include(":composable-realtime-animations")
