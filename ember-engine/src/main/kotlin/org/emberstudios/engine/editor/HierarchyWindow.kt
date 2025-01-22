package org.emberstudios.engine.editor

import imgui.ImGui
import imgui.flag.ImGuiTreeNodeFlags
import imgui.flag.ImGuiWindowFlags
import org.emberstudios.core.logger.getLogger
import org.emberstudios.engine.scene.SceneManager

class HierarchyWindow(
    private val inspectorWindow: InspectorWindow,
    private val sceneManager: SceneManager,
    showing: Boolean = false
) : EditorWindow("Scene Hierarchy", showing) {

    private val gameObjects get() = sceneManager.currentScene?.gameObjects ?: emptyList()

    override fun renderContent() {
        gameObjects.forEach {
            if (ImGui.treeNodeEx(
                    "${it.name}##${it.gid}",
                    ImGuiTreeNodeFlags.OpenOnArrow or
                    if (inspectorWindow.isInspected(it)) ImGuiTreeNodeFlags.Selected else 0)
            ) {
                ImGui.treePop()
            }

            if (ImGui.isItemClicked())
                inspectorWindow.inspect(it)
        }
    }

}