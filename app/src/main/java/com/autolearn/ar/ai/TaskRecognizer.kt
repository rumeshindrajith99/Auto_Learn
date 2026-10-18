package com.autolearn.ar.ai

import com.autolearn.ar.data.altoTasks
import com.autolearn.ar.data.tasks
import com.autolearn.ar.model.Task

class TaskRecognizer {
    fun recognizeTask(componentId: String, actions: List<String> = emptyList()): Task? {
        val cleanId = componentId.trim().lowercase()
        return altoTasks.firstOrNull { 
            it.componentId.equals(cleanId, ignoreCase = true) ||
            it.id.contains(cleanId, ignoreCase = true)
        } ?: tasks.firstOrNull { 
            it.componentId.equals(cleanId, ignoreCase = true) 
        }
    }
}
