package com.autolearn.ar.ui

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@Composable
fun CameraPreview(
    modifier: Modifier = Modifier,

    onPrediction: (TFLiteClassifier.Prediction) -> Unit = {}
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // Create classifier
    val classifier = remember {
        TFLiteClassifier(context.applicationContext)
    }

    // Background thread for image analysis
    val cameraExecutor: ExecutorService = remember {
        Executors.newSingleThreadExecutor()
    }

    // Close classifier when screen is destroyed
    DisposableEffect(Unit) {

        onDispose {
            classifier.close()
            cameraExecutor.shutdown()
        }
    }

    AndroidView(
        modifier = modifier,

        factory = { ctx: Context ->

            val previewView = PreviewView(ctx)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)

            cameraProviderFuture.addListener({

                val cameraProvider =
                    cameraProviderFuture.get()

                // =========================
                // CAMERA PREVIEW
                // =========================

                val preview =
                    Preview.Builder()
                        .build()
                        .also {
                            it.surfaceProvider =
                                previewView.surfaceProvider
                        }


                // =========================
                // IMAGE ANALYSIS
                // =========================

                val imageAnalysis =
                    ImageAnalysis.Builder()

                        .setBackpressureStrategy(
                            ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST
                        )

                        .setOutputImageFormat(
                            ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888
                        )

                        .build()


                var lastAnalysisTime = 0L


                imageAnalysis.setAnalyzer(
                    cameraExecutor
                ) { imageProxy ->

                    try {

                        // Prevent model from running too frequently
                        val currentTime =
                            System.currentTimeMillis()

                        if (
                            currentTime - lastAnalysisTime < 400
                        ) {
                            imageProxy.close()
                            return@setAnalyzer
                        }

                        lastAnalysisTime = currentTime


                        // Convert camera image to Bitmap
                        val bitmap =
                            imageProxyToBitmap(imageProxy)


                        // Run AI model
                        val prediction =
                            classifier.classify(bitmap)


                        // Send result to Compose UI
                        ContextCompat.getMainExecutor(context).execute {

                            onPrediction(prediction)
                        }


                    } catch (exception: Exception) {

                        exception.printStackTrace()

                    } finally {

                        imageProxy.close()
                    }
                }


                // =========================
                // SELECT BACK CAMERA
                // =========================

                val cameraSelector =
                    CameraSelector.DEFAULT_BACK_CAMERA


                try {

                    cameraProvider.unbindAll()


                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis
                    )


                } catch (exception: Exception) {

                    exception.printStackTrace()
                }


            }, ContextCompat.getMainExecutor(ctx))


            previewView
        }
    )
}


/**
 * Convert CameraX RGBA ImageProxy to Bitmap
 */
private fun imageProxyToBitmap(
    image: ImageProxy
): Bitmap {

    val width =
        image.width

    val height =
        image.height


    val bitmap =
        Bitmap.createBitmap(
            width,
            height,
            Bitmap.Config.ARGB_8888
        )


    val plane =
        image.planes[0]

    val buffer =
        plane.buffer

    val rowStride =
        plane.rowStride

    val pixelStride =
        plane.pixelStride


    for (y in 0 until height) {

        for (x in 0 until width) {

            val offset =
                y * rowStride + x * pixelStride


            val red =
                buffer.get(offset).toInt() and 0xFF

            val green =
                buffer.get(offset + 1).toInt() and 0xFF

            val blue =
                buffer.get(offset + 2).toInt() and 0xFF

            val alpha =
                buffer.get(offset + 3).toInt() and 0xFF


            bitmap.setPixel(
                x,
                y,
                Color.argb(
                    alpha,
                    red,
                    green,
                    blue
                )
            )
        }
    }


    return bitmap
}