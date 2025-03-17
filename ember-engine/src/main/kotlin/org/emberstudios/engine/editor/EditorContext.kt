package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import kotlin.reflect.full.hasAnnotation

class EditorContext {

    companion object {
        lateinit var instance: EditorContext
            private set

        inline fun <reified T : EditorWindow> get() = instance.get<T>()
        inline fun <reified T : EditorWindow> show(position: ImVec2? = null, size: ImVec2? = null) =
            instance.show<T>(position, size)
        inline fun <reified T : EditorWindow> showCentered(size: ImVec2) =
            instance.showCentered<T>(size)
        inline fun <reified T : EditorWindow> hide() = instance.hide<T>()
        inline fun <reified T : EditorWindow> isShown() = instance.isShown<T>()

        fun show(window: EditorWindow, position: ImVec2? = null, size: ImVec2? = null) =
            instance.show(window, position, size)
        fun hide(window: EditorWindow) = instance.hide(window)
        fun isShown(window: EditorWindow) = instance.isShown(window)
    }

    val windows = mutableMapOf<EditorWindow, Boolean>()

    init {
        instance = this
    }

    fun registerWindow(window: EditorWindow, showing: Boolean = false): EditorWindow {
        windows[window] = showing
        return window
    }

    inline fun <reified T : EditorWindow> get() = windows.keys
        .filterIsInstance<T>()
        .firstOrNull()

    inline fun <reified T : EditorWindow> show(position: ImVec2? = null, size: ImVec2? = null) =
        get<T>()?.let { show(it, position, size) }
    inline fun <reified T : EditorWindow> showCentered(size: ImVec2) =
        get<T>()?.let { showCentered(it, size) }
    inline fun <reified T : EditorWindow> hide() =
        get<T>()?.let { hide(it) }
    inline fun <reified T : EditorWindow> isShown() = get<T>()?.let { windows[it] } ?: false

    fun show(window: EditorWindow, position: ImVec2? = null, size: ImVec2? = null) {
        if (isShown(window)) return

        position?.let { window.position = it }
        size?.let { window.size = it }

        window.preShow()
        windows[window] = true
        window.requestFocus()
    }

    fun showCentered(window: EditorWindow, size: ImVec2) {
        val viewport = ImGui.getMainViewport()
        val position = ImVec2(
            viewport.workPosX + (viewport.workSizeX - size.x) * .5f,
            viewport.workPosY + (viewport.workSizeY - size.y) * .5f
        )

        show(window, position, size)
    }

    fun hide(window: EditorWindow) {
        windows[window] = false
    }

    fun isShown(window: EditorWindow) = windows[window] ?: false

    fun init() {
        windows.keys.forEach { it.init() }
        loadConfig()
    }
    fun update(deltaTime: Float) = windows.keys.forEach { it.update(deltaTime) }
    fun render() = windows.keys.filter { windows[it]!! }.forEach { it.render() }

    private fun loadConfig() {
        EditorConfig.loadConfig()

        windows.keys.forEach {
            if (it.saveShowingInConfig) {
                if (EditorConfig.get(it.getConfigKey("showing"), isShown(it)))
                    show(it)
                else
                    hide(it)
            }

            it.loadConfig()
        }
    }

    fun saveConfig() {
        windows.keys.forEach {
            if (it.saveShowingInConfig)
                EditorConfig.set(it.getConfigKey("showing"), isShown(it))
            it.saveConfig()
        }

        EditorConfig.saveConfig()
    }

}