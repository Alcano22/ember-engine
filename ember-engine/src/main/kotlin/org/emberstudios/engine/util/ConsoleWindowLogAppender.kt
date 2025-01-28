package org.emberstudios.engine.util

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.AppenderBase
import ch.qos.logback.core.Layout
import imgui.ImVec4
import org.emberstudios.engine.editor.ConsoleLogCall

class ConsoleWindowLogAppender : AppenderBase<ILoggingEvent>() {

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
			event.level
		))
	}

	fun setLayout(layout: Layout<ILoggingEvent>) { this.layout = layout }

	fun registerCallback(callback: (ConsoleLogCall) -> Unit) { callbacks += callback }

}
