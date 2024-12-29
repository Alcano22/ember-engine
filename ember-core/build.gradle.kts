plugins {
	// Apply the shared build logic from a convention plugin.
	// The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
	id("buildsrc.convention.kotlin-jvm")
	// Apply Kotlin Serialization plugin from `gradle/libs.versions.toml`.
	alias(libs.plugins.kotlinPluginSerialization)
}

val kotlinLoggingVersion = "5.1.0"
val logbackVersion = "1.4.11"
val jansiVersion = "2.4.0"
val jomlVersion = "1.10.7"

dependencies {
	// Apply the kotlinx bundle of dependencies from the version catalog (`gradle/libs.versions.toml`).
	implementation(libs.bundles.kotlinxEcosystem)

	api("io.github.oshai:kotlin-logging:$kotlinLoggingVersion")
	api("ch.qos.logback:logback-classic:$logbackVersion")
	api("org.fusesource.jansi:jansi:$jansiVersion")

	api("org.joml", "joml", jomlVersion)
}