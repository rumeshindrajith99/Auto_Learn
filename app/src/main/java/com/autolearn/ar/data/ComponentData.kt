package com.autolearn.ar.data

import com.autolearn.ar.model.EngineComponent

val engineComponents = listOf(
    EngineComponent(
        "battery",
        "Battery",
        "Provides electrical power to the vehicle.",
        "12V automotive battery"
    ),
    EngineComponent(
        "air_filter",
        "Air Filter",
        "Filters dust and particles from incoming air.",
        "Engine intake air filtration"
    ),
    EngineComponent(
        "spark_plug",
        "Spark Plug",
        "Produces the spark required for combustion.",
        "Petrol engine ignition component"
    ),
    EngineComponent(
        "oil_filter",
        "Oil Filter",
        "Removes contaminants from engine oil.",
        "Engine lubrication system"
    ),
    EngineComponent(
        "radiator",
        "Radiator",
        "Helps cool the engine coolant.",
        "Engine cooling system"
    )
)
