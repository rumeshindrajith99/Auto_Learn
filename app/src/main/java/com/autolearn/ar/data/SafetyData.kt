package com.autolearn.ar.data

val safetyRules = mapOf(
    "air_filter" to listOf(
        "Ensure the engine is switched off.",
        "Keep dust away from the intake system.",
        "Handle the dirty filter carefully to avoid inhaling accumulated dust."
    ),
    "spark_plug" to listOf(
        "Ensure the engine is switched off.",
        "Allow the engine to cool before inspection.",
        "Use the correct tool for removal.",
        "Never force a plug that doesn't turn freely by hand — cross-threading damages the cylinder head."
    ),
    "battery_terminal" to listOf(
        "Always disconnect the negative terminal first and reconnect it last.",
        "Never let a tool bridge both terminals at once.",
        "No open flame or sparks near the battery — batteries vent flammable hydrogen gas.",
        "Wear eye protection when cleaning corrosion.",
        "Use appropriate protective equipment."
    ),
    "engine_oil_dipstick" to listOf(
        "Let the engine cool enough that nearby components won't burn your hand or arm.",
        "Dispose of the wiping rag or paper towel appropriately — do not leave it in the engine bay.",
        "Avoid prolonged skin contact with used engine oil."
    ),
    "coolant_reservoir" to listOf(
        "NEVER open a radiator cap or the cooling system while the engine is hot — it is pressurized and can cause severe burns.",
        "Coolant is toxic — handle and store it away from skin contact.",
        "Only top up with the coolant type specified for the vehicle.",
        "Clean up any coolant spills immediately."
    )
)
