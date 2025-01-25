package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.scene.SceneManager

class HierarchyWindow(
    private val sceneManager: SceneManager
) : EditorWindow("Scene Hierarchy") {

    private val gameObjects get() = sceneManager.currentScene?.gameObjects ?: emptyList()

    override fun renderContent() {
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