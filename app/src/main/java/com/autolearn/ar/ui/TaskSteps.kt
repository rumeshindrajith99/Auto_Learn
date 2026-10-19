package com.autolearn.ar.ui

object TaskSteps {

    const val AIR_FILTER_CHANGE = "AIR_FILTER_CHANGE"

    fun getSteps(
        taskId: String
    ): List<String> {

        return when (taskId) {

            AIR_FILTER_CHANGE -> listOf(
                "air_filter_closed",
                "cover_open",
                "inspection",
                "reinstall"
            )

            else -> emptyList()
        }
    }

    fun getDisplayName(
        step: String
    ): String {

        return when (step) {

            "air_filter_closed" ->
                "Air Filter Housing Closed"

            "cover_open" ->
                "Open Air Filter Cover"

            "inspection" ->
                "Inspect Air Filter"

            "reinstall" ->
                "Reinstall Air Filter Cover"

            else -> step
        }
    }
}