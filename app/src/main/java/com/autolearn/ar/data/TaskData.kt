package com.autolearn.ar.data

import com.autolearn.ar.model.Task

val tasks = listOf(
    Task(
        "battery_inspection",
        "battery",
        "Battery Inspection",
        listOf(
            "Identify the battery",
            "Check battery terminals",
            "Inspect for visible damage",
            "Check battery connection",
            "Complete inspection"
        )
    ),
    Task(
        "air_filter_inspection",
        "air_filter",
        "Air Filter Inspection",
        listOf(
            "Identify the air filter",
            "Locate the filter housing",
            "Open the filter housing",
            "Inspect the filter",
            "Complete inspection"
        )
    ),
    Task(
        "spark_plug_removal",
        "spark_plug",
        "Spark Plug Removal",
        listOf(
            "Identify the spark plug",
            "Disconnect the ignition connection",
            "Position the tool correctly",
            "Remove the spark plug",
            "Inspect the spark plug"
        )
    )
)
