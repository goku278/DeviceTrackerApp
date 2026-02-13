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
        google()           // ✅ CRITICAL
        mavenCentral()
    }
}

rootProject.name = "DeviceUsageTracker"
include(":app")