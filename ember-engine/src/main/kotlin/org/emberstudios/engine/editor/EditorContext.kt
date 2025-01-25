package org.emberstudios.engine.editor

class EditorContext {

    companion object {
        lateinit var instance: EditorContext
            private set

        inline fun <reified T : EditorWindow> get() = instance.get<T>()
        inline fun <reified T : EditorWindow> setShown(shown: Boolean) = instance.setShown<T>(shown)
        inline fun <reified T : EditorWindow> show() = instance.show<T>()
        inline fun <reified T : EditorWindow> hide() = instance.hide<T>()
        inline fun <reified T : EditorWindow> isShown() = instance.isShown<T>()

        fun setShown(window: EditorWindow, shown: Boolean) = instance.setShown(window, shown)
        fun show(window: EditorWindow) = instance.show(window)
        fun hide(window: EditorWindow) = instance.hide(window)
        fun isShown(window: EditorWindow) = instance.isShown(window)
    }

    init {
        instance = this
    }

    val windows = mutableMapOf<EditorWindow, Boolean>()

    fun registerWindow(window: EditorWindow, showing: Boolean = false): EditorWindow {
        windows[window] = showing
        return window
    }

    inline fun <reified T : EditorWindow> get() = windows.keys
        .filterIsInstance<T>()
        .firstOrNull()

    inline fun <reified T : EditorWindow> setShown(showing: Boolean) = get<T>()?.let {
        windows[it] = showing
        if (showing)
            it.requestFocus()
    }

    inline fun <reified T : EditorWindow> show() = setShown<T>(true)
    inline fun <reified T : EditorWindow> hide() = setShown<T>(false)
    inline fun <reified T : EditorWindow> isShown() = get<T>()?.let { windows[it] } ?: false

    fun setShown(window: EditorWindow, shown: Boolean) {
        windows[window] = shown
    }

    fun show(window: EditorWindow) {
        windows[window] = true
        window.requestFocus()
    }

    fun hide(window: EditorWindow) {
        windows[window] = false
    }

    fun isShown(window: EditorWindow) = windows[window] ?: false

    fun init() = windows.keys.forEach { it.init() }
    fun update(deltaTime: Float) = windows.keys.forEach { it.update(deltaTime) }
    fun render() = windows.keys.filter { windows[it]!! }.forEach { it.render() }

}