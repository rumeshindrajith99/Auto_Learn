package com.autolearn.ar.ai

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

data class ClassificationResult(
    val label: String,
    val confidence: Float,
    val allScores: Map<String, Float> = emptyMap()
)

class MLClassifier(
    private val context: Context
) {

    companion object {
        private const val TAG = "COMPONENT_MODEL"

        // Must exactly match the filename in app/src/main/assets/
        private const val MODEL_FILE  = "component_model.tflite"
        private const val LABELS_FILE = "labels.txt"

        private const val IMAGE_SIZE = 224   // MobileNetV2 input resolution
        private const val PIXEL_SIZE = 3     // RGB channels

        // Minimum confidence to accept a detection as valid
        const val CONFIDENCE_THRESHOLD = 0.55f
    }

    private val interpreter: Interpreter
    private val labels: List<String>

    init {
        interpreter = Interpreter(loadModelFile())
        labels      = loadLabels()

        Log.d(TAG, "Component model loaded: ${labels.size} classes")
        Log.d(TAG, "Classes: $labels")

        val inputShape  = interpreter.getInputTensor(0).shape()
        val outputShape = interpreter.getOutputTensor(0).shape()
        Log.d(TAG, "Model input shape:  ${inputShape.toList()}")
        Log.d(TAG, "Model output shape: ${outputShape.toList()}")

        require(outputShape[1] == labels.size) {
            "Label count (${labels.size}) does not match model output (${outputShape[1]}). " +
            "Check that labels.txt has exactly ${outputShape[1]} lines."
        }
    }

    // ==========================================
    // LOAD TFLITE MODEL (memory mapped — faster)
    // ==========================================

    private fun loadModelFile(): ByteBuffer {
        val assetFileDescriptor = context.assets.openFd(MODEL_FILE)
        FileInputStream(assetFileDescriptor.fileDescriptor).channel.use { channel ->
            return channel.map(
                FileChannel.MapMode.READ_ONLY,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.declaredLength
            )
        }
    }

    // ==========================================
    // LOAD LABELS
    // ==========================================

    private fun loadLabels(): List<String> {
        val list = mutableListOf<String>()
        context.assets.open(LABELS_FILE).bufferedReader().useLines { lines ->
            lines.map { it.trim() }.filter { it.isNotEmpty() }.forEach { list.add(it) }
        }
        require(list.isNotEmpty()) { "No labels found in $LABELS_FILE" }
        return list
    }

    // ==========================================
    // CLASSIFY IMAGE
    // ==========================================

    fun classify(bitmap: Bitmap): ClassificationResult {

        // Resize to model input size
        val resized = Bitmap.createScaledBitmap(bitmap, IMAGE_SIZE, IMAGE_SIZE, true)

        // Allocate Float32 input buffer [1, 224, 224, 3]
        val inputBuffer = ByteBuffer.allocateDirect(
            4 * IMAGE_SIZE * IMAGE_SIZE * PIXEL_SIZE
        ).apply { order(ByteOrder.nativeOrder()) }

        // Extract pixels
        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)
        resized.getPixels(pixels, 0, IMAGE_SIZE, 0, 0, IMAGE_SIZE, IMAGE_SIZE)

        // MobileNetV2 preprocess_input is inside model graph — expects float RGB in [0.0, 255.0]
        for (pixel in pixels) {
            inputBuffer.putFloat(((pixel shr 16) and 0xFF).toFloat())  // R
            inputBuffer.putFloat(((pixel shr 8)  and 0xFF).toFloat())  // G
            inputBuffer.putFloat((pixel           and 0xFF).toFloat())  // B
        }
        inputBuffer.rewind()

        // Run inference
        val output = Array(1) { FloatArray(labels.size) }
        interpreter.run(inputBuffer, output)

        val probabilities = output[0]

        // Find best class
        var bestIndex = 0
        for (i in 1 until probabilities.size) {
            if (probabilities[i] > probabilities[bestIndex]) bestIndex = i
        }

        // Build score map for debugging
        val scoreMap = labels.indices.associate { i -> labels[i] to probabilities[i] }

        // Log all scores
        Log.d(TAG, "─── Classification result ───")
        scoreMap.entries.sortedByDescending { it.value }.forEach { (lbl, score) ->
            Log.d(TAG, "  %-28s %.1f%%".format(lbl, score * 100))
        }

        val best      = labels.getOrElse(bestIndex) { "unknown" }
        val bestScore = probabilities[bestIndex]
        Log.d(TAG, "Top prediction: $best  (${String.format("%.1f", bestScore * 100)}%)")

        return ClassificationResult(
            label      = best,
            confidence = bestScore,
            allScores  = scoreMap
        )
    }

    // ==========================================
    // RELEASE
    // ==========================================

    fun close() {
        try {
            interpreter.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error closing interpreter", e)
        }
    }
}