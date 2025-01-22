package org.emberstudios.engine.editor

import ch.qos.logback.classic.LoggerContext
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiTableFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.ConsoleWindowLogAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory

data class ConsoleLogCall(val msg: String, val color: ImVec4) : Inspectable {
	override fun inspect() {
		ImGui.pushStyleColor(ImGuiCol.Text, color)
		ImGui.textUnformatted(msg)
		ImGui.popStyleColor()
	}
}

class ConsoleWindow(showing: Boolean = false) : EditorWindow("Console", showing) {

	companion object {
		const val MAX_LOG_HISTORY = 5000

		val LOGGER = getLogger<ConsoleWindow>()
	}

	private val logAppender: ConsoleWindowLogAppender?
	private val logCalls = mutableListOf<ConsoleLogCall>()

	private var selectedIndex = -1

	init {
		val context = LoggerFactory.getILoggerFactory() as LoggerContext
		val rootLogger = context.getLogger(Logger.ROOT_LOGGER_NAME)

		val appender = rootLogger.getAppender("EDITOR_CONSOLE")
		if (appender is ConsoleWindowLogAppender) {
			appender.registerCallback { onLogCall(it) }
			logAppender = appender
		} else {
			LOGGER.exit { "Could not retrieve ConsoleWindow log appender" }
			logAppender = null
		}
	}

	private fun onLogCall(logCall: ConsoleLogCall) {
		if (logCalls.size >= MAX_LOG_HISTORY)
			logCalls.removeFirst()

		logCalls += logCall
	}

	override fun renderContent() {
		if (logAppender == null)
			return

		if (ImGui.beginTable("LogTable", 1, ImGuiTableFlags.RowBg or ImGuiTableFlags.ScrollY)) {
			for ((i, logCall) in logCalls.withIndex()) {
				ImGui.tableNextRow()
				ImGui.tableNextColumn()

				val selected = (i == selectedIndex)
				ImGui.pushStyleColor(ImGuiCol.Text, logCall.color)
				if (ImGui.selectable(logCall.msg, selected, ImGuiSelectableFlags.SpanAllColumns)) {
					InspectorWindow.inspect(logCall)
					selectedIndex = i
				}
				ImGui.popStyleColor()
			}

			ImGui.endTable()
		}
	}

}