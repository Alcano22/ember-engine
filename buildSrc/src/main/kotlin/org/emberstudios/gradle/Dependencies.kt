package buildsrc.convention.org.emberstudios.gradle

object Version {
	const val LWJGL = "3.3.5"
}

fun getLWJGLNatives() = Pair(
	System.getProperty("os.name")!!,
	System.getProperty("os.arch")!!
).let { (name, arch) ->
	when {
		arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } ->
			if (arrayOf("arm", "aarch64").any { arch.startsWith(it) })
				"natives-linux${if (arch.contains("64") || arch.startsWith("armv8")) "-arm64" else "-arm32"}"
			else if (arch.startsWith("ppc"))
				"natives-linux-ppc64le"
			else if (arch.startsWith("riscv"))
				"natives-linux-riscv64"
			else
				"natives-linux"
		arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } -> "natives-macos"
		arrayOf("Windows").any { name.startsWith(it) } -> "natives-windows"
		else -> throw Error("Unrecognized or unsupported platform. Please set 'lwjglNatives' manually")
	}
}

fun getImGuiNatives() = System.getProperty("os.name").let { name ->
	when {
		arrayOf("Linux", "SunOS", "Unit").any { name.startsWith(it) } -> "natives-linux"
		arrayOf("Mac OS X", "Darwin").any { name.startsWith(it) } -> "natives-macos"
		arrayOf("Windows").any { name.startsWith(it) } -> "natives-windows"
		else -> throw Error("Unrecognized or unsupported platform. Please set 'imguiNatives' manually")
	}
}
