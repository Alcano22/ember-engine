package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.emberstudios.engine.networking.NetworkingManager
import org.emberstudios.engine.util.Color

class ServerConnectWindow(
	private val networkingManager: NetworkingManager
) : EditorWindow(
	"Connect to Server",
	true,
	ImGuiWindowFlags.NoResize or
	ImGuiWindowFlags.NoCollapse or
	ImGuiWindowFlags.NoMove or
	ImGuiWindowFlags.NoDocking,
	saveShowingInConfig = false
) {

	private enum class ConnectionState {
		CONNECTING,
		CONNECTED,
		FAILED
	}

	class SuccessWindow : EditorWindow(
		"Connection state",
		flags = ImGuiWindowFlags.NoResize or
				ImGuiWindowFlags.NoCollapse or
				ImGuiWindowFlags.NoMove or
				ImGuiWindowFlags.NoDocking,
		saveShowingInConfig = false
	) {
		var success = true

		override fun renderContent() {
			if (success)
				ImGui.text("Connected successfully!")
			else
				ImGui.textColored(Color.IM_RED, "Connection failed.")

			if (ImGui.button("OK"))
				hide()
		}
	}

	private var imHost = ImString(32)
	private var imTCPPort = ImString(5)
	private var imUDPPort = ImString(5)

	private var connectionState: ConnectionState? = null

	override fun preShow() {
		connectionState = null
	}

	override fun renderContent() {
		ImGui.inputText("Host", imHost)
		ImGui.inputText("TCP Port", imTCPPort, ImGuiInputTextFlags.CharsDecimal)
		ImGui.inputText("UDP Port", imUDPPort, ImGuiInputTextFlags.CharsDecimal)

		if (ImGui.button("Connect")) {
			val tcpPort = imTCPPort.get().toInt()
			val udpPort = imUDPPort.get().toInt()
			connectionState = ConnectionState.CONNECTING
			networkingManager.connect(imHost.get(), tcpPort, udpPort) {
				connectionState = if (it) ConnectionState.CONNECTED else ConnectionState.FAILED
			}
		}

		connectionState?.let { state ->
			if (state == ConnectionState.CONNECTING) {
				ImGui.text("Connecting...")
				return@let
			}

			EditorContext.get<SuccessWindow>()?.let { successWindow ->
				if (state == ConnectionState.CONNECTED)
					successWindow.success = true
				else if (state == ConnectionState.FAILED)
					successWindow.success = false
			}

			EditorContext.showCentered<SuccessWindow>(ImVec2(200f, 100f))
			hide()
		}
	}

	override fun loadConfig() {
		imHost.set(loadConfigValue("host", "localhost"))
		imTCPPort.set(loadConfigValue("tcp_port", ""))
		imUDPPort.set(loadConfigValue("udp_port", ""))
	}

	override fun saveConfig() {
		saveConfigValue("host", imHost.get())
		saveConfigValue("tcp_port", imTCPPort.get())
		saveConfigValue("udp_port", imUDPPort.get())
	}

}