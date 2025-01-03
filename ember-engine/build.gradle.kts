plugins {
	// Apply the shared build logic from a convention plugin.
	// The shared code is located in `buildSrc/src/main/kotlin/kotlin-jvm.gradle.kts`.
	id("buildsrc.convention.kotlin-jvm")

	// Apply the Application plugin to add support for building an executable JVM application.
	application
}

dependencies {
	// Project "engine" depends on project "utils". (Project paths are separated with ":", so ":utils" refers to the top-level "utils" project.)
	implementation(project(":ember-core"))
	implementation(project(":ember-input"))
	implementation(project(":ember-editor"))
	implementation(project(":ember-rendering"))
	implementation(project(":ember-windowing"))
}

application {
	// Define the Fully Qualified Name for the application main class
	// (Note that Kotlin compiles `App.kt` to a class with FQN `com.example.app.AppKt`.)
	mainClass = "org.emberstudios.engine.EngineKt"
}
