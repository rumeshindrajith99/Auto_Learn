package com.autolearn.ar.data

data class AchievementBadge(
    val id: String,
    val title: String,
    val description: String,
    val unlocked: Boolean
)

object AchievementCatalog {

    fun evaluate(records: List<ScoreRecord>): List<AchievementBadge> {
        val attempts = records.size
        val components = records.map { it.componentId }.filter { it.isNotBlank() }.toSet()
        val best = records.maxOfOrNull { it.score } ?: 0
        val lastThree = records.take(3)

        return listOf(
            AchievementBadge(
                id = "first_sheet",
                title = "First sheet",
                description = "Complete your first practical.",
                unlocked = attempts >= 1
            ),
            AchievementBadge(
                id = "three_attempts",
                title = "On the tools",
                description = "Log three practical attempts.",
                unlocked = attempts >= 3
            ),
            AchievementBadge(
                id = "pass_mark",
                title = "Pass mark",
                description = "Score 70 or higher on a sheet.",
                unlocked = best >= 70
            ),
            AchievementBadge(
                id = "distinction",
                title = "Distinction",
                description = "Score 90 or higher on a sheet.",
                unlocked = best >= 90
            ),
            AchievementBadge(
                id = "multi_component",
                title = "Range of work",
                description = "Attempt three different components.",
                unlocked = components.size >= 3
            ),
            AchievementBadge(
                id = "consistent",
                title = "Consistent",
                description = "Last three attempts all score 70+.",
                unlocked = lastThree.size >= 3 && lastThree.all { it.score >= 70 }
            )
        )
    }
}
