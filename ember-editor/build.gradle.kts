import buildsrc.convention.org.emberstudios.gradle.getImGuiNatives

plugins {
    // Apply the shared build logic from a convention plugin.
    // The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
    id("buildsrc.convention.kotlin-jvm")
    // Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
    alias(libs.plugins.kotlinPluginSerialization)
}

val imguiVersion = "1.88.0"
val imguiNative = getImGuiNatives()

dependencies {
    implementation(project(":ember-core"))

    api("io.github.spair:imgui-java-binding:$imguiVersion")
    api("io.github.spair:imgui-java-lwjgl3:$imguiVersion")

    runtimeOnly("io.github.spair:imgui-java-natives-windows:$imguiVersion")
}