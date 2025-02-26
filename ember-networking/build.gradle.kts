plugins {
    id("buildsrc.convention.kotlin-jvm")

    application

    kotlin("plugin.serialization") version "1.9.0"
}

dependencies {
    implementation(project(":ember-core"))

    listOf(
        "ktor-server-core",
        "ktor-server-netty",
        "ktor-server-websockets",
        "ktor-client-core",
        "ktor-client-cio",
        "ktor-client-cio-jvm",
        "ktor-client-websockets",
        "ktor-serialization-kotlinx-json"
    ).forEach { implementation("io.ktor:$it:2.3.5") }

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
}

application {
    mainClass.set("org.emberstudios.networking.GameServerKt")
}
