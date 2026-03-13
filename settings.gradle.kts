rootProject.name = "news"

pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

include(
    "api",
    "core",
    "infra",
    "app"
)
