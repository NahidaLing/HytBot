rootProject.name = "HytBot"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()

        gradlePluginPortal()
    }

    val kotlinVersion: String by settings

    plugins {
        id("org.jetbrains.kotlin.jvm") version kotlinVersion
    }
}