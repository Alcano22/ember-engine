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

    implementation(platform("org.lwjgl:lwjgl-bom:${Version.LWJGL}"))

    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-vulkan")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation("org.lwjgl", "lwjgl-openal")

    runtimeOnly("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)
    runtimeOnly("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)

    if (lwjglNatives == "natives-macos")
        runtimeOnly("org.lwjgl", "lwjgl-vulkan", classifier = lwjglNatives)
}
