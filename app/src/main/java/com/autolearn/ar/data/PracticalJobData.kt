package com.autolearn.ar.data

import com.autolearn.ar.ai.Point3D

data class DetailedPracticalStep(
    val stepNumber: Int,
    val shortTitle: String,
    val description: String,
    val locationDetail: String,
    val directiveInstruction: String,
    val safetyWarning: String? = null,
    val target3D: Point3D = Point3D(0f, -0.1f, 1.3f)
)

enum class JobIconType {
    AIR_FILTER,
    OIL_FILTER,
    SPARK_PLUG,
    BATTERY
}

enum class CompetencyStatus {
    COMPETENT,
    NEEDS_PRACTICE,
    NOT_ASSESSED
}

data class PracticalJob(
    val id: String,
    val title: String,
    val subtitle: String,
    val componentLocationText: String,
    val iconType: JobIconType,
    val status: CompetencyStatus,
    val score: Int? = null,
    val steps: List<DetailedPracticalStep>
)

val practicalJobsList = listOf(

    // 1. AIR FILTER SERVICE
    PracticalJob(
        id = "air_filter_service",
        title = "Air filter service",
        subtitle = "Air filter housing · 4 steps",
        componentLocationText = "Air filter housing",
        iconType = JobIconType.AIR_FILTER,
        status = CompetencyStatus.COMPETENT,
        score = 100,
        steps = listOf(
            DetailedPracticalStep(
                stepNumber = 1,
                shortTitle = "Find the air box",
                description = "Locate rectangular plastic air box on top of the engine intake.",
                locationDetail = "Top intake side of engine bay.",
                directiveInstruction = "Point camera at closed air filter box.",
                safetyWarning = null,
                target3D = Point3D(-0.15f, -0.25f, 1.35f)
            ),
            DetailedPracticalStep(
                stepNumber = 2,
                shortTitle = "Open housing cover",
                description = "Unlatch housing clips and lift air box cover open.",
                locationDetail = "Air box cover lid clips.",
                directiveInstruction = "Unclip and open the filter housing cover.",
                safetyWarning = "Keep debris out of intake duct.",
                target3D = Point3D(0f, -0.1f, 1.25f)
            ),
            DetailedPracticalStep(
                stepNumber = 3,
                shortTitle = "Inspect filter element",
                description = "Remove air filter panel and visually inspect pleats for dirt and tears.",
                locationDetail = "Filter element inside box.",
                directiveInstruction = "Inspect filter panel thoroughly for dirt/clogging.",
                safetyWarning = "Do not tap filter against clean surface.",
                target3D = Point3D(0.05f, -0.05f, 1.28f)
            ),
            DetailedPracticalStep(
                stepNumber = 4,
                shortTitle = "Reinstall & latch cover",
                description = "Re-seat filter panel, close housing lid, and fasten clips.",
                locationDetail = "Air box housing assembly.",
                directiveInstruction = "Place element flat and latch all housing clips securely.",
                safetyWarning = null,
                target3D = Point3D(0.1f, 0.05f, 1.3f)
            )
        )
    ),

    // 2. ENGINE OIL DIPSTICK INSPECTION (dipstick_action_model.tflite)
    PracticalJob(
        id = "oil_filter_inspection",
        title = "Engine Oil Dipstick Inspection",
        subtitle = "Engine dipstick · 4 steps",
        componentLocationText = "Engine oil dipstick",
        iconType = JobIconType.OIL_FILTER,
        status = CompetencyStatus.NEEDS_PRACTICE,
        score = 0,
        steps = listOf(
            DetailedPracticalStep(
                stepNumber = 1,
                shortTitle = "Dipstick Inserted",
                description = "Dipstick fully seated in guide tube (Initial state).",
                locationDetail = "Engine block dipstick guide tube.",
                directiveInstruction = "Ensure dipstick is fully seated in tube.",
                safetyWarning = "Engine components may be hot — exercise care.",
                target3D = Point3D(0.2f, 0.15f, 1.35f)
            ),
            DetailedPracticalStep(
                stepNumber = 2,
                shortTitle = "Dipstick Pulled",
                description = "Pull dipstick straight up out of guide tube.",
                locationDetail = "Dipstick handle loop and blade.",
                directiveInstruction = "Pull dipstick straight out of guide tube.",
                safetyWarning = null,
                target3D = Point3D(0.25f, 0.05f, 1.25f)
            ),
            DetailedPracticalStep(
                stepNumber = 3,
                shortTitle = "Wiped Clean",
                description = "Wipe oil off dipstick tip cleanly using rag.",
                locationDetail = "Dipstick metal blade tip.",
                directiveInstruction = "Wipe oil level clean with a clean rag.",
                safetyWarning = null,
                target3D = Point3D(0.2f, 0.15f, 1.35f)
            ),
            DetailedPracticalStep(
                stepNumber = 4,
                shortTitle = "Level Inspected",
                description = "Hold dipstick horizontally to inspect oil film between markings.",
                locationDetail = "Dipstick MIN and MAX indicators.",
                directiveInstruction = "Hold dipstick horizontally and inspect oil level.",
                safetyWarning = null,
                target3D = Point3D(0.25f, 0.25f, 1.3f)
            )
        )
    ),

    // 3. SPARK PLUG REPLACEMENT (spark_plug_action_model.tflite)
    PracticalJob(
        id = "spark_plug_replacement",
        title = "Spark Plug Replacement",
        subtitle = "Spark plug · 4 steps",
        componentLocationText = "Spark plug",
        iconType = JobIconType.SPARK_PLUG,
        status = CompetencyStatus.NOT_ASSESSED,
        score = null,
        steps = listOf(
            DetailedPracticalStep(
                stepNumber = 1,
                shortTitle = "Ignition Boot On",
                description = "Ignition boot / coil attached to cylinder head (Initial state).",
                locationDetail = "Top of cylinder head spark plug well.",
                directiveInstruction = "Point camera at ignition boot attached to plug well.",
                safetyWarning = "Ensure ignition system is powered off.",
                target3D = Point3D(-0.1f, -0.3f, 1.25f)
            ),
            DetailedPracticalStep(
                stepNumber = 2,
                shortTitle = "Boot Removed",
                description = "Disconnect ignition boot, exposing spark plug well.",
                locationDetail = "Spark plug well opening.",
                directiveInstruction = "Disconnect boot to expose spark plug well.",
                safetyWarning = null,
                target3D = Point3D(-0.1f, -0.2f, 1.2f)
            ),
            DetailedPracticalStep(
                stepNumber = 3,
                shortTitle = "Plug Loosened",
                description = "Socket wrench attached and plug loosened counter-clockwise.",
                locationDetail = "Inside spark plug well.",
                directiveInstruction = "Attach socket wrench and loosen spark plug.",
                safetyWarning = "Do not tilt socket to avoid cracking porcelain.",
                target3D = Point3D(-0.05f, -0.1f, 1.25f)
            ),
            DetailedPracticalStep(
                stepNumber = 4,
                shortTitle = "New Plug Seated",
                description = "New spark plug installed and hand threaded securely.",
                locationDetail = "Spark plug cylinder head threads.",
                directiveInstruction = "Install new plug and hand thread into place.",
                safetyWarning = null,
                target3D = Point3D(0f, 0f, 1.3f)
            )
        )
    ),

    // 4. BATTERY TERMINAL CHANGE (battery_action_model.tflite)
    PracticalJob(
        id = "battery_inspection",
        title = "Battery Terminal Change",
        subtitle = "12 V battery · 4 steps",
        componentLocationText = "12 V battery",
        iconType = JobIconType.BATTERY,
        status = CompetencyStatus.NEEDS_PRACTICE,
        score = 0,
        steps = listOf(
            DetailedPracticalStep(
                stepNumber = 1,
                shortTitle = "Battery Connected",
                description = "Both battery terminals attached and clamped (Initial state).",
                locationDetail = "Top surface of 12V battery casing.",
                directiveInstruction = "Point camera at connected battery terminals.",
                safetyWarning = "Always disconnect NEGATIVE (-) terminal first.",
                target3D = Point3D(-0.35f, 0.1f, 1.3f)
            ),
            DetailedPracticalStep(
                stepNumber = 2,
                shortTitle = "Negative Unclamped",
                description = "Negative (-) terminal unbolted and removed.",
                locationDetail = "Negative (-) battery post.",
                directiveInstruction = "Unbolt and remove negative (-) terminal clamp.",
                safetyWarning = "Keep negative cable isolated from metallic body.",
                target3D = Point3D(0.35f, 0.1f, 1.3f)
            ),
            DetailedPracticalStep(
                stepNumber = 3,
                shortTitle = "Positive Unclamped",
                description = "Positive (+) terminal unbolted and detached.",
                locationDetail = "Positive (+) battery post.",
                directiveInstruction = "Unbolt positive (+) terminal clamp.",
                safetyWarning = "Do not touch positive clamp to chassis ground.",
                target3D = Point3D(-0.25f, 0.25f, 1.35f)
            ),
            DetailedPracticalStep(
                stepNumber = 4,
                shortTitle = "Battery Removed",
                description = "Battery lifted out of mounting tray.",
                locationDetail = "Battery mounting tray.",
                directiveInstruction = "Lift battery straight up out of tray.",
                safetyWarning = "Wear gloves — battery residue can irritate skin.",
                target3D = Point3D(0f, 0f, 1.3f)
            )
        )
    )
)
