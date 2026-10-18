package com.autolearn.ar.ai

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color

data class ComponentMask(
    val componentId: String,
    val displayName: String,
    val maskColor: Color,
    val cuboidWidth: Float,
    val cuboidHeight: Float,
    val cuboidDepth: Float
)

data class IdentifiedPart(
    val partName: String,
    val description: String,
    val targetPositionOffset: Offset,
    val spatialPoint3D: Point3D,
    val motionType: MotionVectorType,
    val actionDirective: String
)

object PartGuidanceEngine {

    fun getMaskForComponent(componentId: String): ComponentMask {
        return when (componentId.lowercase()) {
            "battery", "battery_terminal", "car_battery" -> ComponentMask(
                componentId = "battery_terminal",
                displayName = "BATTERY TERMINALS & CASING",
                maskColor = Color(0x3538BDF8), // Translucent Cyan
                cuboidWidth = 0.7f,
                cuboidHeight = 0.55f,
                cuboidDepth = 0.45f
            )
            "engine_oil_dipstick", "oil_filter", "dipstick" -> ComponentMask(
                componentId = "engine_oil_dipstick",
                displayName = "ENGINE OIL DIPSTICK ASSEMBLY",
                maskColor = Color(0x35F59E0B), // Translucent Amber
                cuboidWidth = 0.5f,
                cuboidHeight = 0.6f,
                cuboidDepth = 0.35f
            )
            "spark_plug" -> ComponentMask(
                componentId = "spark_plug",
                displayName = "SPARK PLUG ASSEMBLY",
                maskColor = Color(0x35A855F7), // Translucent Purple
                cuboidWidth = 0.45f,
                cuboidHeight = 0.7f,
                cuboidDepth = 0.3f
            )
            "coolant_reservoir" -> ComponentMask(
                componentId = "coolant_reservoir",
                displayName = "COOLANT RESERVOIR TANK",
                maskColor = Color(0x353B82F6), // Translucent Blue
                cuboidWidth = 0.6f,
                cuboidHeight = 0.55f,
                cuboidDepth = 0.4f
            )
            else -> ComponentMask( // Air Filter default
                componentId = "air_filter",
                displayName = "AIR FILTER HOUSING",
                maskColor = Color(0x3510B981), // Translucent Emerald
                cuboidWidth = 0.65f,
                cuboidHeight = 0.5f,
                cuboidDepth = 0.4f
            )
        }
    }

    fun getPartGuidanceForStep(stepIndex: Int, componentId: String = "air_filter"): List<IdentifiedPart> {
        return when (componentId.lowercase()) {
            "battery", "battery_terminal", "car_battery" -> when (stepIndex) {
                0 -> listOf(
                    IdentifiedPart(
                        partName = "NEGATIVE TERMINAL (-) CAP",
                        description = "Black protective lead post clamp",
                        targetPositionOffset = Offset(0.35f, 0.4f),
                        spatialPoint3D = Point3D(-0.35f, 0.1f, 1.3f),
                        motionType = MotionVectorType.UNCLIP_ARC,
                        actionDirective = "Point camera at negative clamp & check tight fit"
                    )
                )
                1 -> listOf(
                    IdentifiedPart(
                        partName = "POSITIVE TERMINAL (+) CAP",
                        description = "Red insulated cover",
                        targetPositionOffset = Offset(0.65f, 0.4f),
                        spatialPoint3D = Point3D(0.35f, 0.1f, 1.3f),
                        motionType = MotionVectorType.UNCLIP_ARC,
                        actionDirective = "Inspect positive cable connection and shell"
                    )
                )
                2 -> listOf(
                    IdentifiedPart(
                        partName = "HOLD-DOWN CLAMP BRACKET",
                        description = "Base securing bracket",
                        targetPositionOffset = Offset(0.5f, 0.65f),
                        spatialPoint3D = Point3D(-0.25f, 0.25f, 1.35f),
                        motionType = MotionVectorType.LIFT_UP,
                        actionDirective = "Inspect casing polymer for swelling and clamp tightness"
                    )
                )
                3 -> listOf(
                    IdentifiedPart(
                        partName = "TERMINAL BOLTS",
                        description = "10mm hex clamping nut",
                        targetPositionOffset = Offset(0.5f, 0.5f),
                        spatialPoint3D = Point3D(0f, 0f, 1.3f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Confirm both terminals are tight and securely seated"
                    )
                )
                else -> emptyList()
            }

            "engine_oil_dipstick", "oil_filter", "dipstick" -> when (stepIndex) {
                0 -> listOf(
                    IdentifiedPart(
                        partName = "DIPSTICK LOOP HANDLE",
                        description = "Yellow/Orange pull ring",
                        targetPositionOffset = Offset(0.5f, 0.4f),
                        spatialPoint3D = Point3D(0.2f, 0.15f, 1.35f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Point camera at engine oil dipstick handle"
                    )
                )
                1 -> listOf(
                    IdentifiedPart(
                        partName = "DIPSTICK BLADE & TIP",
                        description = "Flat steel indicator blade",
                        targetPositionOffset = Offset(0.5f, 0.5f),
                        spatialPoint3D = Point3D(0.25f, 0.05f, 1.25f),
                        motionType = MotionVectorType.LIFT_UP,
                        actionDirective = "Pull dipstick straight out and wipe tip clean"
                    )
                )
                2 -> listOf(
                    IdentifiedPart(
                        partName = "DIPSTICK GUIDE TUBE",
                        description = "Metal oil tube sleeve",
                        targetPositionOffset = Offset(0.5f, 0.4f),
                        spatialPoint3D = Point3D(0.2f, 0.15f, 1.35f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Insert dipstick back into guide tube fully"
                    )
                )
                3 -> listOf(
                    IdentifiedPart(
                        partName = "MIN/MAX INDICATOR MARKS",
                        description = "Cross-hatched oil range",
                        targetPositionOffset = Offset(0.5f, 0.6f),
                        spatialPoint3D = Point3D(0.25f, 0.25f, 1.3f),
                        motionType = MotionVectorType.INSPECT_ROTATE,
                        actionDirective = "Withdraw dipstick and verify oil is between MIN & MAX"
                    )
                )
                else -> emptyList()
            }

            "spark_plug" -> when (stepIndex) {
                0 -> listOf(
                    IdentifiedPart(
                        partName = "IGNITION COIL BOOT",
                        description = "Black rubber spark plug boot",
                        targetPositionOffset = Offset(0.5f, 0.4f),
                        spatialPoint3D = Point3D(-0.1f, -0.3f, 1.25f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Point camera at cylinder head spark plug wells"
                    )
                )
                1 -> listOf(
                    IdentifiedPart(
                        partName = "PLUG BOOT TERMINAL",
                        description = "High-voltage lead wire",
                        targetPositionOffset = Offset(0.5f, 0.45f),
                        spatialPoint3D = Point3D(-0.1f, -0.2f, 1.2f),
                        motionType = MotionVectorType.UNCLIP_ARC,
                        actionDirective = "Twist rubber boot slightly and pull straight off"
                    )
                )
                2 -> listOf(
                    IdentifiedPart(
                        partName = "HEX NUT SHELL",
                        description = "5/8 inch spark plug hex nut",
                        targetPositionOffset = Offset(0.5f, 0.5f),
                        spatialPoint3D = Point3D(-0.05f, -0.1f, 1.25f),
                        motionType = MotionVectorType.LIFT_UP,
                        actionDirective = "Loosen spark plug using spark plug socket"
                    )
                )
                3 -> listOf(
                    IdentifiedPart(
                        partName = "ELECTRODE GAP & THREAD",
                        description = "0.8 - 1.1 mm spark gap tip",
                        targetPositionOffset = Offset(0.5f, 0.55f),
                        spatialPoint3D = Point3D(0f, 0f, 1.3f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Inspect gap with gauge, thread by hand & torque to spec"
                    )
                )
                else -> emptyList()
            }

            "coolant_reservoir" -> when (stepIndex) {
                0 -> listOf(
                    IdentifiedPart(
                        partName = "EXPANSION TANK CASING",
                        description = "Translucent polymer body",
                        targetPositionOffset = Offset(0.5f, 0.4f),
                        spatialPoint3D = Point3D(0.3f, -0.2f, 1.3f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Point camera at coolant reservoir expansion tank"
                    )
                )
                1 -> listOf(
                    IdentifiedPart(
                        partName = "MIN/MAX LEVEL MARKS",
                        description = "Fill capacity lines",
                        targetPositionOffset = Offset(0.5f, 0.45f),
                        spatialPoint3D = Point3D(0.3f, -0.1f, 1.25f),
                        motionType = MotionVectorType.INSPECT_ROTATE,
                        actionDirective = "Check fluid level relative to MIN / MAX side marks"
                    )
                )
                2 -> listOf(
                    IdentifiedPart(
                        partName = "PRESSURE CAP & HOSES",
                        description = "Sealed pressure cap & radiator hoses",
                        targetPositionOffset = Offset(0.5f, 0.5f),
                        spatialPoint3D = Point3D(0.25f, -0.05f, 1.35f),
                        motionType = MotionVectorType.UNCLIP_ARC,
                        actionDirective = "Inspect cap rubber seal and check hoses for leaks"
                    )
                )
                3 -> listOf(
                    IdentifiedPart(
                        partName = "FILLER NECK",
                        description = "Coolant top-off opening",
                        targetPositionOffset = Offset(0.5f, 0.55f),
                        spatialPoint3D = Point3D(0.3f, -0.15f, 1.3f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Top off with coolant if low and tighten cap securely"
                    )
                )
                else -> emptyList()
            }

            else -> when (stepIndex) { // Air filter default
                0 -> listOf(
                    IdentifiedPart(
                        partName = "HOUSING COVER GASKET",
                        description = "Perimeter protective seal",
                        targetPositionOffset = Offset(0.5f, 0.42f),
                        spatialPoint3D = Point3D(-0.15f, -0.25f, 1.35f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Point camera at closed air filter box"
                    )
                )
                1 -> listOf(
                    IdentifiedPart(
                        partName = "HOUSING LATCH CLIPS",
                        description = "Spring steel clips",
                        targetPositionOffset = Offset(0.3f, 0.52f),
                        spatialPoint3D = Point3D(0f, -0.1f, 1.25f),
                        motionType = MotionVectorType.UNCLIP_ARC,
                        actionDirective = "Unclip and open the filter housing cover"
                    )
                )
                2 -> listOf(
                    IdentifiedPart(
                        partName = "FILTER ELEMENT PLEATS",
                        description = "Cellulose paper media",
                        targetPositionOffset = Offset(0.5f, 0.5f),
                        spatialPoint3D = Point3D(0.05f, -0.05f, 1.28f),
                        motionType = MotionVectorType.INSPECT_ROTATE,
                        actionDirective = "Inspect pleats for debris accumulation & tears"
                    )
                )
                3 -> listOf(
                    IdentifiedPart(
                        partName = "ALIGNMENT GUIDE TABS",
                        description = "Positioning slots",
                        targetPositionOffset = Offset(0.48f, 0.62f),
                        spatialPoint3D = Point3D(0.1f, 0.05f, 1.3f),
                        motionType = MotionVectorType.PRESS_DOWN,
                        actionDirective = "Place element flat and latch all housing clips securely"
                    )
                )
                else -> emptyList()
            }
        }
    }
}
