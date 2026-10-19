package com.autolearn.ar.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

@Composable
fun CameraScreen(
    isAnalyzing: Boolean,
    analysisMessage: String?,
    onImageCaptured: (Bitmap) -> Unit
) {

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var previewView by remember {
        mutableStateOf<PreviewView?>(null)
    }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->

            hasCameraPermission = granted
        }


    LaunchedEffect(Unit) {

        if (!hasCameraPermission) {

            permissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }


    if (hasCameraPermission) {

        Box(
            modifier = Modifier.fillMaxSize()
        ) {

            // ==========================================
            // CAMERA PREVIEW
            // ==========================================

            CameraPreview(
                context = context,
                lifecycleOwner = lifecycleOwner,
                modifier = Modifier.fillMaxSize(),

                onPreviewReady = { view ->
                    previewView = view
                }
            )


            // ==========================================
            // DARK OVERLAY
            // ==========================================

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Color.Black.copy(alpha = 0.12f)
                    )
            )


            // ==========================================
            // TOP FROSTED PILL
            // ==========================================
            Row(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 44.dp)
                    .background(
                        MaterialTheme.colorScheme.surface.copy(alpha = 0.92f),
                        MaterialTheme.shapes.medium
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline,
                        MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(7.dp)
                        .background(MaterialTheme.colorScheme.secondary, CircleShape)
                )

                Text(
                    text = "Component scanner",
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.labelLarge
                )
            }


            // ==========================================
            // CENTER AR VIEWFINDER RETICLE (Canvas)
            // ==========================================
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(240.dp)
            ) {
                androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
                    val strokeW = 3.dp.toPx()
                    val cornerLen = 32.dp.toPx()
                    val reticleColor = if (isAnalyzing) Color(0xFF00D4C6) else Color(0xFF3C93FA)
                    val reticleRadius = 6.dp.toPx()

                    // AR Component Opacity Mask
                    drawRoundRect(
                        color = reticleColor.copy(alpha = 0.18f),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(reticleRadius)
                    )

                    drawRoundRect(
                        color = reticleColor.copy(alpha = 0.4f),
                        topLeft = androidx.compose.ui.geometry.Offset(0f, 0f),
                        size = size,
                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(reticleRadius),
                        style = Stroke(width = 1.dp.toPx())
                    )

                    // Top-Left
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(cornerLen, 0f), strokeW)
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(0f, 0f), androidx.compose.ui.geometry.Offset(0f, cornerLen), strokeW)

                    // Top-Right
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width - cornerLen, 0f), strokeW)
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(size.width, 0f), androidx.compose.ui.geometry.Offset(size.width, cornerLen), strokeW)

                    // Bottom-Left
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(cornerLen, size.height), strokeW)
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(0f, size.height), androidx.compose.ui.geometry.Offset(0f, size.height - cornerLen), strokeW)

                    // Bottom-Right
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width - cornerLen, size.height), strokeW)
                    drawLine(reticleColor, androidx.compose.ui.geometry.Offset(size.width, size.height), androidx.compose.ui.geometry.Offset(size.width, size.height - cornerLen), strokeW)
                }

                Text(
                    text = if (isAnalyzing) "Analyzing…" else "Align the component",
                    color = Color.White,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 12.dp)
                        .background(Color.Black.copy(alpha = 0.55f), MaterialTheme.shapes.small)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }


            // ==========================================
            // BOTTOM FROSTED CONTROL PANEL
            // ==========================================
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(20.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
                        MaterialTheme.shapes.medium
                    ),
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (analysisMessage != null) {
                        Text(
                            text = analysisMessage,
                            color = if (
                                analysisMessage.contains("failed", ignoreCase = true) ||
                                analysisMessage.contains("not recognized", ignoreCase = true)
                            ) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    Button(
                        enabled = !isAnalyzing,
                        onClick = {
                            val bitmap = previewView?.bitmap
                            if (bitmap != null) {
                                onImageCaptured(bitmap)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(
                            text = if (isAnalyzing) "Processing…" else "Scan component",
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }
        }

    } else {

        // ==========================================
        // CAMERA PERMISSION SCREEN
        // ==========================================

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Camera access required",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Camera access is required to identify automobile engine components.",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyLarge
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Allow camera access")
            }
        }
    }
}


@Composable
private fun CameraPreview(
    context: Context,
    lifecycleOwner: androidx.lifecycle.LifecycleOwner,
    modifier: Modifier = Modifier,
    onPreviewReady: (PreviewView) -> Unit
) {

    AndroidView(
        modifier = modifier,

        factory = { ctx ->

            val previewView =
                PreviewView(ctx)

            onPreviewReady(previewView)

            val cameraProviderFuture =
                ProcessCameraProvider.getInstance(ctx)


            cameraProviderFuture.addListener({

                try {

                    val cameraProvider =
                        cameraProviderFuture.get()


                    val preview =
                        Preview.Builder()
                            .build()
                            .also {

                                it.surfaceProvider =
                                    previewView.surfaceProvider
                            }


                    cameraProvider.unbindAll()


                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview
                    )

                } catch (exception: Exception) {

                    exception.printStackTrace()
                }

            }, ContextCompat.getMainExecutor(ctx))


            previewView
        }
    )
}