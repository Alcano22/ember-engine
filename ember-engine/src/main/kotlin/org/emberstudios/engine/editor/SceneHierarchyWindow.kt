package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.type.ImString
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.gameobject.GameObject
import org.emberstudios.engine.scene.SceneManager

class SceneHierarchyWindow : EditorWindow("Scene Hierarchy") {

    companion object {
        private val LOGGER = getLogger<SceneHierarchyWindow>()
    }

    private val gameObjects get() = SceneManager.currentScene?.gameObjects ?: emptyList()
    private val searchQuery = ImString(64)

    override fun renderContent() {
        if (ImGui.button("+"))
            SceneManager.currentScene?.loadGameObject(GameObject("New GameObject"))

        ImGui.sameLine()

        ImGui.setNextItemWidth(ImGui.getContentRegionAvailX())
        if (ImGui.inputTextWithHint("##Search", "Search...", searchQuery)) {
            val input = searchQuery.get().trim()

            if (input.matches(Regex("^[0-9+\\-*/().\\s]+$"))) {
                try {

                } catch (e: Exception) {
                    LOGGER.error { e.message }
                }
            }
        }

        ImGui.separator()

        gameObjects.forEach {
            if (ImGui.treeNodeEx(
                    "${it.name}##${it.gid}",
                    ImGuiTreeNodeFlags.OpenOnArrow or
                    if (InspectorWindow.isInspected(it)) ImGuiTreeNodeFlags.Selected else 0)
            ) {
                ImGui.treePop()
            }

            if (ImGui.isItemClicked())
                InspectorWindow.inspect(it)
        }
    }

}