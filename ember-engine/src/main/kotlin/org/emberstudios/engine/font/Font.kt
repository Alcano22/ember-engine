package org.emberstudios.engine.font

import org.w3c.dom.Element
import javax.xml.parsers.DocumentBuilderFactory

data class FontConfig(
	val name: String,
	val size: Int,
	val texturePath: String,
	val chars: List<FontChar>
) {
	operator fun get(char: Char) = chars.find { it.char == char }
}

data class FontChar(
	val char: Char,
	val x: Int,
	val y: Int,
	val width: Int,
	val height: Int
)

fun parseFontConfig(filepath: String): FontConfig {
	val builder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
	val doc = builder.parse(filepath)
	doc.documentElement.normalize()

	val root = doc.getElementsByTagName("fontcfg").item(0) as Element

	val name = root.getAttribute("name")
	val size = root.getAttribute("size").toInt()
	val texturePath = root.getAttribute("texture")
	val defaultWidth = root.getAttribute("defaultWidth").toInt()
	val defaultHeight = root.getAttribute("defaultHeight").toInt()

	val chars = mutableListOf<FontChar>()
	val charNodes = root.getElementsByTagName("char")

	for (i in 0..<charNodes.length) {
		val charElement = charNodes.item(i) as Element
		val char = charElement.getAttribute("char")[0]
		val x = charElement.getAttribute("x").toInt()
		val y = charElement.getAttribute("y").toInt()

		val width =
			if (charElement.hasAttribute("width")) charElement.getAttribute("width").toInt() else defaultWidth
		val height =
			if (charElement.hasAttribute("height")) charElement.getAttribute("height").toInt() else defaultHeight

		chars += FontChar(char, x, y, width, height)
	}

	return FontConfig(name, size, texturePath, chars)
}