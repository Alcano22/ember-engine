import buildsrc.convention.org.emberstudios.gradle.Version
import buildsrc.convention.org.emberstudios.gradle.getLWJGLNatives

plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

val lwjglNatives = getLWJGLNatives()

dependencies {
    implementation(project(":ember-core"))
    implementation(project(":ember-rendering"))

    implementation(platform("org.lwjgl:lwjgl-bom:${Version.LWJGL}"))
    implementation("org.lwjgl", "lwjgl-glfw")

    implementation("com.github.StrikerX3:JXInput:1.0.0")

    runtimeOnly("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
}
