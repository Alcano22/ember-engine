package org.emberstudios.engine.editor

import imgui.ImGui

object ImGuiEx {

	fun textEllipsis(text: String, maxWidth: Float) {
		val textWidth = ImGui.calcTextSizeX(text)

		if (textWidth <= maxWidth) {
			ImGui.textUnformatted(text)
			return
		}

		var truncatedText = text
		while (truncatedText.isNotEmpty() && ImGui.calcTextSizeX("$truncatedText...") > maxWidth)
			truncatedText = truncatedText.dropLast(1)

		ImGui.textUnformatted("$truncatedText...")
	}
}