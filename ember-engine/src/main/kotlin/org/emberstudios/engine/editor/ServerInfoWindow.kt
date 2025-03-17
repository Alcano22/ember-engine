package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.ImVec4
import imgui.extension.implot.ImPlot
import imgui.extension.implot.flag.ImPlotAxis
import imgui.extension.implot.flag.ImPlotAxisFlags
import imgui.extension.implot.flag.ImPlotFlags
import imgui.flag.ImGuiCond
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.emberstudios.core.logger.CORE_LOGGER
import org.emberstudios.engine.networking.NetworkingManager
import org.emberstudios.engine.util.Color
import org.emberstudios.engine.util.Time

class ServerInfoWindow : EditorWindow("Server Info") {

	companion object {
		private const val LATENCIES_MAX_SIZE = 30
		private const val WARN_LATENCY_THRESHOLD = 75
		private const val CRITICAL_LATENCY_THRESHOLD = 125
	}

	private var latency = -1L
	private var nextMeasureLatencyTime = 0f
	private val latencyValues = mutableListOf<Long>()

	override fun update(deltaTime: Float) {
		if (!NetworkingManager.isConnected) return

		if (nextMeasureLatencyTime > Time.time) return
		nextMeasureLatencyTime = Time.time + 1f

		CoroutineScope(Dispatchers.IO).launch {
			val measured = NetworkingManager.measureLatency()
			latency = measured

			synchronized(latencyValues) {
				latencyValues += measured
				if (latencyValues.size > LATENCIES_MAX_SIZE + 1)
					latencyValues.removeFirst()
			}
		}
	}

	override fun renderContent() {
		if (!NetworkingManager.isConnected) {
			ImGui.text("Not connected to a server.")
			return
		}

		ImGui.text("Host: ${NetworkingManager.hostAddress}")
		ImGui.text("TCP Port: ${NetworkingManager.tcpPort}")
		ImGui.text("UDP Port: ${NetworkingManager.udpPort}")
		ImGui.text("Connected since: ${NetworkingManager.connectionDuration.toInt()}s")

		ImGui.text("Latency: ")
		ImGui.sameLine(0f, 0f)
		if (latency == -1L) {
			ImGui.text("Measuring...")
			return
		}

		ImGui.textColored(getColorForLatency(latency), "${latency}ms")

		val latencies = synchronized(latencyValues) {
			latencyValues.map { it.toFloat() }.toFloatArray().reversedArray()
		}
		if (latencies.isNotEmpty()) {
			val xValues = FloatArray(latencies.size) { it.toFloat() }
			val yMin = latencies.min().toDouble() * 0.75
			val yMax = latencies.max().toDouble() * 1.25

			ImPlot.setNextAxesLimits(0.0, LATENCIES_MAX_SIZE.toDouble(), yMin, yMax, ImGuiCond.Always)
			if (ImPlot.beginPlot("##LatencyGraph", 0f, 150f,
					ImPlotFlags.NoInputs or
					ImPlotFlags.NoLegend or
					ImPlotFlags.NoFrame
				)
			) {
				ImPlot.setupAxis(ImPlotAxis.X1, ImPlotAxisFlags.Invert)
				ImPlot.plotLine("Latency", xValues, latencies, latencies.size)

				ImPlot.endPlot()
			}
		}
	}

	private fun getColorForLatency(latency: Long): ImVec4 = when {
		latency >= CRITICAL_LATENCY_THRESHOLD -> Color.IM_RED
		latency >= WARN_LATENCY_THRESHOLD -> Color.IM_YELLOW
		else -> Color.IM_GREEN
	}
}