package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.ImVec4
import imgui.extension.texteditor.TextEditor
import imgui.extension.texteditor.TextEditorLanguageDefinition
import imgui.flag.ImGuiCol
import imgui.flag.ImGuiInputTextFlags
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.emberstudios.core.logger.getLogger
import org.emberstudios.core.thread.createThread
import org.emberstudios.engine.util.Time
import org.emberstudios.renderer.ShaderLibrary
import org.python.util.PythonInterpreter
import java.io.File
import java.io.StringWriter

class TextEditorWindow : EditorWindow(
    "Text Editor",
    true,
    ImGuiWindowFlags.NoScrollWithMouse or
    ImGuiWindowFlags.NoScrollbar
) {

    companion object {
        val LOGGER = getLogger<TextEditorWindow>()

        val ERROR_COL = ImVec4(1f, .3f, .3f, 1f)
    }

    private var file: File? = null
    private var editor = TextEditor()
    private var changedContentSinceSave = false

    private val outputMSG = ImString(1024 * 10)
    private var isError = false
    private var python: PythonInterpreter? = null

    init {
        editor.setShowWhitespaces(false)
        editor.isReadOnly = false

        preloadPythonInterpreter()
    }

    private fun preloadPythonInterpreter() {
        createThread(
            "PythonInterpreterLauncher",
            start = true,
            action = {
                PythonInterpreter.initialize(System.getProperties(), System.getProperties(), arrayOf())
                python = PythonInterpreter()
            }
        )
    }

    override fun renderContent() {
        val windowSize = ImGui.getContentRegionAvail()
        val editorHeight = windowSize.y * .7f

        ImGui.beginChild("TextEditor", windowSize.x, editorHeight, true)
        editor.render("##Editor")
        ImGui.endChild()

        if (!changedContentSinceSave)
            changedContentSinceSave = editor.isTextChanged

        val outputHeight = windowSize.y - editorHeight
        ImGui.beginChild("OutputWindow", windowSize.x, outputHeight, true)
        ImGui.textWrapped("Output:")
        ImGui.separator()

        if (isError)
            ImGui.pushStyleColor(ImGuiCol.Text, ERROR_COL)
        ImGui.inputTextMultiline("##Output", outputMSG, -1f, -1f,
            ImGuiInputTextFlags.ReadOnly)
        if (isError)
            ImGui.popStyleColor()

        ImGui.endChild()
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

        if (file?.extension == "py") {
            val disableRunButton = python == null
            if (disableRunButton) ImGui.beginDisabled()
            if (ImGui.menuItem("Run"))
                runPythonScript()
            if (disableRunButton) ImGui.endDisabled()
        }
    }

    fun editFile(file: File) {
        this.file = file

        editor.languageDefinition = when (file.extension) {
            "py" -> pythonLanguageDefinition()
            "glsl" -> TextEditorLanguageDefinition.GLSL()
            else -> TextEditorLanguageDefinition()
        }
        editor.text = file.readText()
    }

    private fun runPythonScript() {
        val outputStream = StringWriter()

        val py = python!!

        py.setOut(outputStream)
        py.setErr(outputStream)

        py.set("time", Time.time)

        outputMSG.clear()
        isError = false

        try {
            py.exec(editor.text)
        } catch (e: Exception) {
            outputStream.write("Error: ${e.message}\n")
            isError = true
        } finally {
            outputMSG.set(outputStream.toString())
            py.close()
        }
    }

    private fun pythonLanguageDefinition(): TextEditorLanguageDefinition {
        val def = TextEditorLanguageDefinition()
        def.name = "Python"

        def.setKeywords(arrayOf(
            "def", "class", "import", "from", "if", "else", "elif", "while", "for", "return",
            "break", "continue", "lambda", "with", "as", "try", "except", "finally", "raise",
            "in", "not", "or", "and", "is", "pass"
        ))

        def.setIdentifiers(mapOf(
            "print" to "Built-in function",
            "len" to "Built-in function",
            "range" to "Built-in function",
            "input" to "Built-in function",
            "open" to "Built-in function",
            "int" to "Built-in function",
            "float" to "Built-in function",
            "str" to "Built-in function",
            "list" to "Built-in function",
            "dict" to "Built-in function",
            "tuple" to "Built-in function"
        ))

        def.commentStart = "#"
        def.commentEnd = ""

        return def
    }

}