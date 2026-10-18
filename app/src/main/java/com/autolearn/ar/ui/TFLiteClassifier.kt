package com.autolearn.ar.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

class TFLiteClassifier(
    private val context: Context
) {

    companion object {
        private const val TAG = "ACTION_MODEL"

        private const val MODEL_FILE =
            "air_filter_action_model.tflite"

        private const val LABELS_FILE =
            "action_labels.txt"

        private const val IMAGE_SIZE = 224
        private const val NUM_CHANNELS = 3
    }

    private val interpreter: Interpreter
    private val labels: List<String>

    init {

        interpreter =
            Interpreter(loadModelFile())

        labels =
            loadLabels()

        Log.d(
            TAG,
            "Model loaded successfully"
        )

        Log.d(
            TAG,
            "Labels: $labels"
        )

        Log.d(
            TAG,
            "Number of classes: ${labels.size}"
        )
    }


    // ==========================================
    // LOAD TFLITE MODEL
    // ==========================================

    private fun loadModelFile(): ByteBuffer {

        val assetFileDescriptor =
            context.assets.openFd(
                MODEL_FILE
            )

        FileInputStream(
            assetFileDescriptor.fileDescriptor
        ).channel.use { fileChannel ->

            return fileChannel.map(
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

        val labelsList =
            mutableListOf<String>()

        context.assets
            .open(LABELS_FILE)
            .bufferedReader()
            .use { reader ->

                reader.forEachLine { line ->

                    val label =
                        line.trim()

                    if (
                        label.isNotEmpty()
                    ) {

                        labelsList.add(
                            label
                        )
                    }
                }
            }

        require(
            labelsList.isNotEmpty()
        ) {
            "No labels found in $LABELS_FILE"
        }

        return labelsList
    }


    // ==========================================
    // CLASSIFY IMAGE
    // ==========================================

    fun classify(
        bitmap: Bitmap
    ): Prediction {

        // Resize camera image to 224 x 224

        val resizedBitmap =
            Bitmap.createScaledBitmap(
                bitmap,
                IMAGE_SIZE,
                IMAGE_SIZE,
                true
            )


        // Create Float32 input buffer

        val inputBuffer =
            ByteBuffer.allocateDirect(
                4 *
                        IMAGE_SIZE *
                        IMAGE_SIZE *
                        NUM_CHANNELS
            ).apply {

                order(
                    ByteOrder.nativeOrder()
                )
            }


        // Get pixels

        val pixels =
            IntArray(
                IMAGE_SIZE *
                        IMAGE_SIZE
            )

        resizedBitmap.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )


        // ==========================================
        // NORMALIZE PIXELS
        //
        // Training used:
        // rescale = 1.0 / 255.0
        //
        // Therefore Android must also use:
        // RGB / 255.0f
        // ==========================================

        // Model graph contains Rescaling(1.0/127.5, offset=-1) — expects float RGB in [0.0, 255.0]
        for (pixelValue in pixels) {
            val red   = ((pixelValue shr 16) and 0xFF).toFloat()
            val green = ((pixelValue shr 8)  and 0xFF).toFloat()
            val blue  = (pixelValue          and 0xFF).toFloat()

            inputBuffer.putFloat(red)
            inputBuffer.putFloat(green)
            inputBuffer.putFloat(blue)
        }


        inputBuffer.rewind()


        // ==========================================
        // MODEL OUTPUT
        // ==========================================

        val output =
            Array(1) {

                FloatArray(
                    labels.size
                )
            }


        // Run TensorFlow Lite model

        interpreter.run(
            inputBuffer,
            output
        )


        val probabilities =
            output[0]


        // ==========================================
        // FIND BEST PREDICTION
        // ==========================================

        var maxIndex = 0

        var maxConfidence =
            probabilities[0]


        for (
        i in 1 until probabilities.size
        ) {

            if (
                probabilities[i] >
                maxConfidence
            ) {

                maxConfidence =
                    probabilities[i]

                maxIndex =
                    i
            }
        }


        // ==========================================
        // LOG ALL PREDICTIONS
        // ==========================================

        for (
        i in probabilities.indices
        ) {

            val label =
                labels.getOrElse(i) {
                    "unknown"
                }

            Log.d(
                TAG,
                "$label = ${probabilities[i]}"
            )
        }


        return Prediction(
            label =
                labels[maxIndex],

            confidence =
                maxConfidence
        )
    }


    // ==========================================
    // CLOSE MODEL
    // ==========================================

    fun close() {

        interpreter.close()
    }


    // ==========================================
    // PREDICTION RESULT
    // ==========================================

    data class Prediction(

        val label: String,

        val confidence: Float
    )
}

