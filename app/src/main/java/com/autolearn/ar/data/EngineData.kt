package com.autolearn.ar.data

import com.autolearn.ar.model.EngineComponent
import com.autolearn.ar.model.Task

val altoComponents = listOf(

    EngineComponent(
        id = "air_filter",
        name = "Air Filter",
        function = "Filters dust and dirt from the air before it enters the engine.",
        specifications = "Suzuki Alto L800 air intake system component."
    ),

    EngineComponent(
        id = "battery_terminal",
        name = "Battery Terminal",
        function = "Connects the vehicle battery to the electrical system.",
        specifications = "Positive and negative battery connection terminals."
    ),

    EngineComponent(
        id = "engine_oil_dipstick",
        name = "Engine Oil Dipstick",
        function = "Used to check the engine oil level and condition.",
        specifications = "Engine oil level inspection component."
    ),

    EngineComponent(
        id = "spark_plug",
        name = "Spark Plug",
        function = "Creates the spark required to ignite the air-fuel mixture.",
        specifications = "Engine ignition system component."
    ),

    EngineComponent(
        id = "coolant_reservoir",
        name = "Coolant Reservoir",
        function = "Holds engine coolant and allows visual level checking.",
        specifications = "Engine cooling system overflow tank."
    )
)


val altoTasks  = listOf(

    Task(
        id = "air_filter_inspection",
        componentId = "air_filter",
        name = "Air Filter Inspection",
        steps = listOf(
            "Switch off the engine and allow it to cool.",
            "Open the engine compartment.",
            "Locate the air filter housing.",
            "Open the air filter housing carefully.",
            "Remove and inspect the air filter.",
            "Check for excessive dirt or damage.",
            "Reinstall the air filter and close the housing."
        )
    ),

    Task(
        id = "battery_terminal_change",
        componentId = "battery_terminal",
        name = "Battery Terminal Change",
        steps = listOf(
            "Switch off the engine and remove the ignition key.",
            "Wear appropriate safety protection.",
            "Locate the vehicle battery.",
            "Disconnect the negative battery terminal first.",
            "Disconnect the positive battery terminal.",
            "Remove or replace the required terminal.",
            "Reconnect the positive terminal.",
            "Reconnect the negative terminal.",
            "Check that both terminals are secure."
        )
    ),

    Task(
        id = "engine_oil_dipstick_inspection",
        componentId = "engine_oil_dipstick",
        name = "Engine Oil Dipstick Inspection",
        steps = listOf(
            "Park the vehicle on level ground.",
            "Switch off the engine.",
            "Allow the engine oil to settle.",
            "Locate the engine oil dipstick.",
            "Remove the dipstick carefully.",
            "Wipe the dipstick clean.",
            "Insert the dipstick fully.",
            "Remove it again and check the oil level.",
            "Confirm that the oil level is between the minimum and maximum marks."
        )
    ),

    Task(
        id = "spark_plug_change",
        componentId = "spark_plug",
        name = "Spark Plug Change",
        steps = listOf(
            "Switch off the engine and allow it to cool completely.",
            "Locate the spark plug area.",
            "Disconnect the spark plug lead or ignition coil carefully.",
            "Remove the old spark plug using the correct tool.",
            "Inspect the new spark plug.",
            "Install the new spark plug carefully.",
            "Tighten according to the vehicle service specification.",
            "Reconnect the spark plug lead or ignition coil.",
            "Start the engine and check for normal operation."
        )
    ),

    Task(
        id = "coolant_reservoir_inspection",
        componentId = "coolant_reservoir",
        name = "Coolant Reservoir Level Inspection",
        steps = listOf(
            "Ensure the engine is completely cool before beginning.",
            "Open the engine compartment and locate the coolant reservoir.",
            "Check the coolant level against the MIN and MAX markings.",
            "If the level is below MIN, identify the correct coolant type.",
            "Slowly add coolant to the reservoir until it reaches the MAX line.",
            "Replace the reservoir cap securely.",
            "Check for signs of leaks around the reservoir and hoses."
        )
    )
)