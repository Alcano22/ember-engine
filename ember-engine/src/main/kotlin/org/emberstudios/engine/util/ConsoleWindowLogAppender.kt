package org.emberstudios.engine.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.Layout
import imgui.ImVec4
import org.emberstudios.engine.editor.ConsoleLogCall

class ConsoleWindowLogAppender : AppenderBase<ILoggingEvent>() {

	companion object {
		val LEVEL_TO_COLOR = mapOf(
			Level.TRACE to ImVec4(0.53f, 0.53f, 0.53f, 1.00f),
			Level.DEBUG to ImVec4(0.13f, 0.67f, 1.00f, 1.00f),
			Level.INFO  to ImVec4(0.00f, 0.87f, 0.33f, 1.00f),
			Level.WARN  to ImVec4(1.00f, 0.65f, 0.00f, 1.00f),
			Level.ERROR to ImVec4(1.00f, 0.33f, 0.33f, 1.00f)
		)
	}

	private val callbacks = mutableListOf<(ConsoleLogCall) -> Unit>()

	private var layout: Layout<ILoggingEvent>? = null

	override fun start() {
		if (layout != null)
			layout!!.start()
		super.start()
	}

	override fun append(event: ILoggingEvent) = callbacks.forEach {
		val formattedMsg = layout!!.doLayout(event)
		it(ConsoleLogCall(
			formattedMsg,
			event.callerData.first(),
			LEVEL_TO_COLOR[event.level]!!
		))
	}

	fun setLayout(layout: Layout<ILoggingEvent>) { this.layout = layout }

	fun registerCallback(callback: (ConsoleLogCall) -> Unit) { callbacks += callback }

}
