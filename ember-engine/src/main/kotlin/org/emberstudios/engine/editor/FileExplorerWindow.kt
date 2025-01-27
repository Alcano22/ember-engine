package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiSelectableFlags
import imgui.flag.ImGuiStyleVar
import imgui.flag.ImGuiWindowFlags
import imgui.type.ImString
import org.emberstudios.core.io.ResourceManager
import org.emberstudios.core.logger.getLogger
import org.emberstudios.input.Input
import org.emberstudios.input.KeyCode
import org.emberstudios.renderer.loadTexture
import java.awt.Desktop
import java.io.File

class FileExplorerWindow(
    private val rootPath: String = "assets",
) : EditorWindow("File Explorer") {

    companion object {
        private const val THUMBNAIL_SIZE_STEP = 32f
        private const val PADDING = 10f

        private val FILE_ICON = ResourceManager.loadTexture("textures\\file_icon.png")
        private val FOLDER_ICON = ResourceManager.loadTexture("textures\\folder_icon.png")

        private val LOGGER = getLogger<FileExplorerWindow>()
    }

    private lateinit var currentPath: String

    private val files = mutableListOf<File>()

    private var thumbnailSize = 128f
    private val searchQuery = ImString(256)

    private var selectedFile: File? = null

    init {
        loadPath(rootPath)
    }

    private fun loadPath(filepath: String) {
        currentPath = filepath

        files.clear()
        File(filepath).listFiles()
            ?.filter { it.path != currentPath }
            ?.forEach { files += it }
    }

    private fun reload() { loadPath(currentPath) }

    override fun renderContent() {
        val buttonSize = 32f
        val backButtonWidth = 64f
        val padding = ImGui.getStyle().windowPaddingX
        val availWidth = ImGui.getContentRegionAvailX()
        val searchBarWidth = availWidth - (buttonSize + padding) * 2f - (backButtonWidth + padding)

        val parentExists = File(currentPath).parentFile != null
        if (!parentExists) ImGui.beginDisabled()
        if (ImGui.button("<-"))
            loadPath(File(currentPath).parent)
        if (!parentExists) ImGui.endDisabled()

        ImGui.sameLine()
        ImGui.setNextItemWidth(searchBarWidth)
        ImGui.inputTextWithHint("##Search", "Search in $currentPath...", searchQuery)

        ImGui.sameLine()
        if (ImGui.button("+", buttonSize, 0f))
            thumbnailSize += THUMBNAIL_SIZE_STEP

        ImGui.sameLine()
        if (ImGui.button("-", buttonSize, 0f))
            thumbnailSize -= THUMBNAIL_SIZE_STEP

        ImGui.separator()

        val filteredFiles = files.filter {
            it.name.contains(searchQuery.get(), ignoreCase = true)
        }.sortedBy { !it.isDirectory }

        val itemSize = 64f
        val contentWidth = ImGui.getContentRegionAvailX()
        val itemsPerRow = (contentWidth / itemSize).toInt().coerceAtLeast(1)

        ImGui.pushStyleVar(ImGuiStyleVar.FramePadding, PADDING, PADDING)

        for ((index, file) in filteredFiles.withIndex()) {
            if (index % itemsPerRow != 0)
                ImGui.sameLine()

            val selected = selectedFile == file

            val groupWidth = thumbnailSize
            val groupHeight = thumbnailSize + ImGui.getTextLineHeightWithSpacing()

            ImGui.beginGroup()

            if (ImGui.selectable("##${file.path}", selected,
                    ImGuiSelectableFlags.None, groupWidth, groupHeight))
                selectedFile = file

            val icon = if (file.isDirectory) FOLDER_ICON else when (file.extension) {
                "png" -> ResourceManager.loadTexture(file.path)
                else -> FILE_ICON
            }

            ImGui.setCursorPosY(ImGui.getCursorPosY() - groupHeight)
            ImGui.image(icon.texID.toLong(), thumbnailSize, thumbnailSize, 0f, 1f, 1f, 0f)

            val textWidth = ImGui.calcTextSizeX(file.name)
            val thumbnailCenter = thumbnailSize / 2f
            val textX = ImGui.getCursorPosX() + thumbnailCenter - textWidth / 2f

            ImGui.setCursorPosX(textX)
            ImGui.textUnformatted(file.name)

            ImGui.endGroup()

            if (ImGui.isItemClicked() && ImGui.getMouseClickedCount(0) == 2) {
                if (file.isDirectory)
                    loadPath(file.path)
                else
                    openFile(file)
            }
        }

        ImGui.popStyleVar()

        if (ImGui.isKeyPressed(KeyCode.DELETE) && selectedFile != null) {
            selectedFile!!.delete()
            reload()
        }
    }

    private fun openFile(file: File) {
        if (!ResourceManager.isTextFile(file)) {
            Desktop.getDesktop().open(file)
            return
        }

        val textEditorWindow = EditorContext.get<TextEditorWindow>()!!
        textEditorWindow.editFile(file)
        EditorContext.show(textEditorWindow)
    }
}