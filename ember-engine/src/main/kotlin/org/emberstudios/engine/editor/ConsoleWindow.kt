package org.emberstudios.engine.editor

import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.CallerData
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiMouseButton
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiTableFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.ConsoleWindowLogAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

data class ConsoleLogCall(
	val msg: String,
	val callerData: StackTraceElement,
	val color: ImVec4,
) : Inspectable {

	companion object {
		val LOGGER = getLogger<ConsoleLogCall>()
	}

	override fun inspect() {
		ImGui.text("Occured at ")

		ImGui.sameLine()
		ImGui.pushStyleColor(ImGuiCol.Text, 0f, .5f, 1f, 1f)
		ImGui.text("${callerData.fileName}:${callerData.lineNumber}")
		ImGui.popStyleColor()

		ImGui.sameLine()
		ImGui.text(" in ")

		ImGui.sameLine()
		ImGui.pushStyleColor(ImGuiCol.Text, .25f, .75f, 1f, 1f)
		ImGui.text("${callerData.methodName}()")
		ImGui.popStyleColor()
	}
}

class ConsoleWindow : EditorWindow("Console") {

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