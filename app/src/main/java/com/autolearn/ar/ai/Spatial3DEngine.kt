package com.autolearn.ar.ai

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import kotlin.math.cos
import kotlin.math.sin

data class Point3D(
    val x: Float, // -1.0 to 1.0
    val y: Float, // -1.0 to 1.0
    val z: Float  // 0.5 (near) to 3.0 (far)
)

enum class MotionVectorType {
    NONE,
    UNCLIP_ARC,
    LIFT_UP,
    INSPECT_ROTATE,
    PRESS_DOWN
}

object Spatial3DEngine {

    // Perspective projection parameters
    private const val FOCAL_LENGTH = 1.2f

    fun project3DTo2D(
        point: Point3D,
        screenWidth: Float,
        screenHeight: Float,
        pitchDeg: Float = 12f,
        yawDeg: Float = 0f
    ): Offset {
        val radPitch = Math.toRadians(pitchDeg.toDouble()).toFloat()
        val radYaw = Math.toRadians(yawDeg.toDouble()).toFloat()

        // 3D Rotation around X (Pitch)
        val y1 = point.y * cos(radPitch) - point.z * sin(radPitch)
        val z1 = point.y * sin(radPitch) + point.z * cos(radPitch)

        // 3D Rotation around Y (Yaw)
        val x2 = point.x * cos(radYaw) + z1 * sin(radYaw)
        val z2 = -point.x * sin(radYaw) + z1 * cos(radYaw)

        // Perspective divide
        val safeZ = z2.coerceAtLeast(0.3f)
        val projectedX = (x2 / safeZ) * FOCAL_LENGTH
        val projectedY = (y1 / safeZ) * FOCAL_LENGTH

        val centerX = screenWidth / 2f
        val centerY = screenHeight / 2f

        return Offset(
            x = centerX + projectedX * (screenWidth * 0.35f),
            y = centerY + projectedY * (screenWidth * 0.35f)
        )
    }

    fun calculate3DCuboidVertices(
        centerX: Float = 0f,
        centerY: Float = 0f,
        centerZ: Float = 1.4f,
        width: Float = 0.65f,
        height: Float = 0.5f,
        depth: Float = 0.4f
    ): List<Point3D> {
        val hw = width / 2f
        val hh = height / 2f
        val hd = depth / 2f

        return listOf(
            // Front face vertices (Z - hd)
            Point3D(centerX - hw, centerY - hh, centerZ - hd), // 0: Top-Left-Front
            Point3D(centerX + hw, centerY - hh, centerZ - hd), // 1: Top-Right-Front
            Point3D(centerX + hw, centerY + hh, centerZ - hd), // 2: Bottom-Right-Front
            Point3D(centerX - hw, centerY + hh, centerZ - hd), // 3: Bottom-Left-Front

            // Back face vertices (Z + hd)
            Point3D(centerX - hw, centerY - hh, centerZ + hd), // 4: Top-Left-Back
            Point3D(centerX + hw, centerY - hh, centerZ + hd), // 5: Top-Right-Back
            Point3D(centerX + hw, centerY + hh, centerZ + hd), // 6: Bottom-Right-Back
            Point3D(centerX - hw, centerY + hh, centerZ + hd)  // 7: Bottom-Left-Back
        )
    }

    fun generate3DMotionPath(
        type: MotionVectorType,
        screenWidth: Float,
        screenHeight: Float,
        animProgress: Float,
        pitchDeg: Float = 12f
    ): Path {
        val path = Path()
        when (type) {
            MotionVectorType.UNCLIP_ARC -> {
                val start = project3DTo2D(Point3D(-0.35f, 0.1f, 1.3f), screenWidth, screenHeight, pitchDeg)
                val control = project3DTo2D(Point3D(-0.55f, -0.1f, 1.2f), screenWidth, screenHeight, pitchDeg)
                val end = project3DTo2D(Point3D(-0.45f, -0.3f, 1.1f), screenWidth, screenHeight, pitchDeg)

                path.moveTo(start.x, start.y)
                path.quadraticTo(control.x, control.y, start.x + (end.x - start.x) * animProgress, start.y + (end.y - start.y) * animProgress)
            }
            MotionVectorType.LIFT_UP -> {
                val start = project3DTo2D(Point3D(0f, 0.1f, 1.4f), screenWidth, screenHeight, pitchDeg)
                val end = project3DTo2D(Point3D(0f, -0.5f, 1.1f), screenWidth, screenHeight, pitchDeg)

                path.moveTo(start.x, start.y)
                path.lineTo(start.x + (end.x - start.x) * animProgress, start.y + (end.y - start.y) * animProgress)
            }
            MotionVectorType.INSPECT_ROTATE -> {
                val center = project3DTo2D(Point3D(0f, 0f, 1.3f), screenWidth, screenHeight, pitchDeg)
                val radius = screenWidth * 0.18f
                val sweep = 360f * animProgress
                val rad = Math.toRadians(sweep.toDouble()).toFloat()

                path.moveTo(center.x + radius, center.y)
                path.lineTo(center.x + radius * cos(rad), center.y + radius * sin(rad))
            }
            MotionVectorType.PRESS_DOWN -> {
                val start = project3DTo2D(Point3D(0f, -0.4f, 1.1f), screenWidth, screenHeight, pitchDeg)
                val end = project3DTo2D(Point3D(0f, 0.15f, 1.4f), screenWidth, screenHeight, pitchDeg)

                path.moveTo(start.x, start.y)
                path.lineTo(start.x + (end.x - start.x) * animProgress, start.y + (end.y - start.y) * animProgress)
            }
            MotionVectorType.NONE -> {}
        }
        return path
    }
}
