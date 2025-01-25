package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec2
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.extension.texteditor.flag.TextEditorPaletteIndex
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.emberstudios.renderer.ShaderLibrary
import java.io.File

class TextEditorWindow : EditorWindow("Text Editor", true) {

    private var file: File? = null
    private var editor = TextEditor()
    private var changedContentSinceSave = false

    init {
        editor.setShowWhitespaces(false)
        editor.isReadOnly = false
    }

    override fun renderContent() {
        val windowSize = ImGui.getContentRegionAvail()

        ImGui.beginChild("TextEditor", windowSize.x, windowSize.y, true,
            ImGuiWindowFlags.AlwaysVerticalScrollbar)
        editor.render("##Editor")
        ImGui.endChild()

        if (!changedContentSinceSave)
            changedContentSinceSave = editor.isTextChanged
    }

    override fun renderMenuBar() {
        val disableSaveButton = file == null || !changedContentSinceSave
        if (disableSaveButton) ImGui.beginDisabled()
        if (ImGui.menuItem("Save")) {
            file?.let {
                file!!.writeText(editor.text)
                changedContentSinceSave = false

                if (it.extension == "glsl")
                    ShaderLibrary.reloadShader(it.path)
            }
        }
        if (disableSaveButton) ImGui.endDisabled()
    }

    fun editFile(file: File) {
        this.file = file

        editor.languageDefinition = when (file.extension) {
            "lua" -> TextEditorLanguageDefinition.Lua()
            "glsl" -> TextEditorLanguageDefinition.GLSL()
            else -> TextEditorLanguageDefinition()
        }
        editor.text = file.readText()
    }
}