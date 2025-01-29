package org.emberstudios.engine.editor

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import ch.qos.logback.classic.spi.CallerData
import imgui.ImGui
import imgui.ImVec4
import imgui.flag.*
import imgui.type.ImBoolean
import imgui.type.ImInt
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.util.Color
import org.emberstudios.engine.util.ConsoleWindowLogAppender
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

data class ConsoleLogCall(
	val msg: String,
	val callerData: StackTraceElement,
	val level: Level,
) : Inspectable {

	companion object {
		val LOGGER = getLogger<ConsoleLogCall>()
	}

	val color: ImVec4 get() = when (level) {
		Level.TRACE -> ImVec4(0.85f, 0.85f, 0.85f, 1.00f)
		Level.DEBUG -> ImVec4(0.00f, 0.87f, 0.33f, 1.00f)
		Level.INFO 	-> ImVec4(0.13f, 0.67f, 1.00f, 1.00f)
		Level.WARN  -> ImVec4(1.00f, 0.65f, 0.00f, 1.00f)
		Level.ERROR -> ImVec4(1.00f, 0.33f, 0.33f, 1.00f)
		else 		-> Color.IM_WHITE
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

		private val LOGGER = getLogger<ConsoleWindow>()
		private val LOG_LEVELS = arrayOf("TRACE", "DEBUG", "INFO", "WARN", "ERROR")
		private val LOG_LEVEL_VALUES = arrayOf(Level.TRACE, Level.DEBUG, Level.INFO, Level.WARN, Level.ERROR)
	}

	private val logAppender: ConsoleWindowLogAppender?
	private val logCalls = mutableListOf<ConsoleLogCall>()
	private var selectedLogLevel = ImInt(2)

	private var autoScroll = ImBoolean(true)
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

	override fun renderContextMenuPopup() {
		if (ImGui.combo("Log Level", selectedLogLevel, LOG_LEVELS))
			LOGGER.trace { "Log Level changed to: ${LOG_LEVELS[selectedLogLevel.get()]}" }

		ImGui.checkbox("Auto-Scroll", autoScroll)

		if (ImGui.button("Clear Console"))
			logCalls.clear()
	}

	override fun renderContent() {
		if (logAppender == null) return

		val goToBottom = ImGui.button("Go to bottom")

		ImGui.separator()

		val isAtBottom = ImGui.getScrollY() >= ImGui.getScrollMaxY() - 1f

		if (ImGui.beginTable("LogTable", 1, ImGuiTableFlags.RowBg or ImGuiTableFlags.ScrollY)) {
			val selectedLevel = LOG_LEVEL_VALUES[selectedLogLevel.get()]

			for ((i, logCall) in logCalls.withIndex()) {
				if (logCall.level.levelInt < selectedLevel.levelInt) continue

				ImGui.tableNextRow()
				ImGui.tableNextColumn()

				val selected = (i == selectedIndex)
				ImGui.pushStyleColor(ImGuiCol.HeaderActive, Color.IM_TRANSPARENT)
				if (selected)
					ImGui.pushStyleColor(ImGuiCol.HeaderHovered, ImGui.getStyleColorVec4(ImGuiCol.Header))
				else
					ImGui.pushStyleColor(ImGuiCol.HeaderHovered, Color.IM_TRANSPARENT)
				ImGui.pushStyleColor(ImGuiCol.Text, logCall.color)
				if (ImGui.selectable(logCall.msg, selected, ImGuiSelectableFlags.SpanAllColumns)) {
					InspectorWindow.inspect(logCall)
					selectedIndex = i
				}
				ImGui.popStyleColor(3)
			}

			ImGui.endTable()
		}

		if ((autoScroll.get() && isAtBottom) || goToBottom)
			ImGui.setScrollY(ImGui.getScrollMaxY())
	}

	override fun loadConfig() {
		selectedLogLevel.set(loadConfigValue("log_level", 0))
	}

	override fun saveConfig() {
		saveConfigValue("log_level", selectedLogLevel.get())
	}

}