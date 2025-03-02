import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("buildsrc.convention.kotlin-jvm")
    id("com.github.johnrengelman.shadow") version "8.1.1"

    application

    kotlin("plugin.serialization") version "1.9.0"
}

val mainClassName = "org.emberstudios.networking.GameServerKt"

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

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.15.0")
}

application {
    mainClass.set(mainClassName)
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = mainClassName
    }
}

tasks.named<ShadowJar>("shadowJar") {
    archiveBaseName.set("ember-networking")
    archiveClassifier.set("")
    archiveVersion.set("1.0")
}
