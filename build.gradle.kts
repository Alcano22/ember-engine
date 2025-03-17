import org.jetbrains.dokka.gradle.DokkaTaskPartial

plugins {
	id("org.jetbrains.dokka") version "2.0.0"
}

subprojects {
	apply(plugin = "org.jetbrains.dokka")
}

tasks.dokkaHtmlMultiModule {
	moduleName.set("WHOLE PROJECT NAME USED IN THE HEADER")
}
