plugins {
	// Apply the shared build logic from a convention plugin.
	// The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
	id("buildsrc.convention.kotlin-jvm")

	// Apply the Application plugin to add support for building an executable JVM application.
	application

	kotlin("plugin.serialization") version "1.9.0"

	id("com.github.johnrengelman.shadow") version "8.1.1"
}

dependencies {
	// Project "engine" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
	implementation(project(":ember-core"))
	implementation(project(":ember-editor"))
	implementation(project(":ember-rendering"))
	implementation(project(":ember-windowing"))
	implementation(project(":ember-networking"))

	implementation("io.github.classgraph:classgraph:4.8.149")

	implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
	implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")

	implementation("org.python:jython-standalone:2.7.2")

	implementation("net.objecthunter:exp4j:0.4.8")

	implementation("org.jbox2d:jbox2d-library:2.2.1.1")
}

application {
	// Define the Fully Qualified Name for the application main class
	// (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
	mainClass = "org.emberstudios.engine.EngineKt"

	applicationDefaultJvmArgs = listOf(
		"-Dproject.root=${project.rootDir.absolutePath}"
	)
}

tasks.register<JavaExec>("runEngine") {
	dependsOn("shadowJar")

	group = "application"
	mainClass.set("org.emberstudios.engine.EngineKt")
	classpath = sourceSets["main"].runtimeClasspath
	systemProperty("ember.editor", "true")
}

tasks.register<JavaExec>("runGame") {
	dependsOn("shadowJar")

	group = "application"
	mainClass.set("org.emberstudios.engine.EngineKt")
	classpath = sourceSets["main"].runtimeClasspath
	systemProperty("ember.editor", "false")
}

tasks.register<Copy>("copyRuntimeDependencies") {
	from(configurations.runtimeClasspath)
	into("${buildDir}/dep")
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "org.emberstudios.engine.EngineKt"
	}
}

tasks.shadowJar {
	archiveClassifier.set("all")
	mergeServiceFiles()

	manifest {
		attributes(
			"Main-Class" to "org.emberstudios.engine.EngineKt"
		)
	}
}
