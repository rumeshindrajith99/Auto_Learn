package com.autolearn.ar.navigation

import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.autolearn.ar.BuildConfig
import com.autolearn.ar.ai.ChatbotService
import com.autolearn.ar.ai.MLClassifier
import com.autolearn.ar.data.altoComponents
import com.autolearn.ar.data.altoTasks
import com.autolearn.ar.data.DailyGoalStore
import com.autolearn.ar.data.InProgressSession
import com.autolearn.ar.data.InProgressStore
import com.autolearn.ar.data.ResumeNotifier
import com.autolearn.ar.data.safetyRules
import com.autolearn.ar.data.theoryContentMap
import com.autolearn.ar.ui.AccountScreen
import com.autolearn.ar.ui.CameraScreen
import com.autolearn.ar.ui.ChatbotScreen
import com.autolearn.ar.ui.ComponentScreen
import com.autolearn.ar.ui.EvaluationScreen
import com.autolearn.ar.ui.FloatingAppBar
import com.autolearn.ar.ui.HistoryScreen
import com.autolearn.ar.ui.HomeScreen
import com.autolearn.ar.ui.LibraryScreen
import com.autolearn.ar.ui.SplashScreen
import com.autolearn.ar.ui.TaskRecognitionScreen
import com.autolearn.ar.ui.TheoryDetailScreen
import com.autolearn.ar.ui.VerificationScreen
import androidx.compose.runtime.mutableFloatStateOf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun AppNavigation(
    launchIntent: Intent? = null,
    resumeGeneration: Int = 0
) {

    val navController = rememberNavController()
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current


    // ==========================================
    // COMPONENT IDENTIFICATION TFLITE MODEL
    // ==========================================

    val mlClassifier = remember {
        MLClassifier(context.applicationContext)
    }


    // ==========================================
    // CHATBOT SERVICE (Gemini API)
    // ==========================================

    val chatbotService = remember {
        ChatbotService(BuildConfig.GEMINI_API_KEY)
    }


    // ==========================================
    // CLOSE TFLITE MODEL
    // ==========================================

    DisposableEffect(Unit) {

        onDispose {

            try {

                mlClassifier.close()

            } catch (e: Exception) {

                android.util.Log.e(
                    "TFLITE_COMPONENT",
                    "Error closing classifier",
                    e
                )
            }
        }
    }


    // ==========================================
    // APP SESSION STATE
    // ==========================================

    val coldResumeSession = remember {
        if (launchIntent?.getBooleanExtra(ResumeNotifier.EXTRA_RESUME_PRACTICAL, false) == true) {
            InProgressStore.load(context)
        } else {
            null
        }
    }

    var selectedComponentId by remember {
        mutableStateOf(coldResumeSession?.componentId.orEmpty())
    }

    var selectedTaskId by remember {
        mutableStateOf(coldResumeSession?.taskId.orEmpty())
    }

    var resumeStepIndex by remember {
        mutableIntStateOf(coldResumeSession?.currentStepIndex ?: 0)
    }
    var resumeVerifiedSteps by remember {
        mutableStateOf(coldResumeSession?.verifiedSteps.orEmpty())
    }
    var pausedSession by remember { mutableStateOf(InProgressStore.load(context)) }

    fun applyPausedSession(session: InProgressSession) {
        selectedComponentId = session.componentId
        selectedTaskId = session.taskId
        resumeStepIndex = session.currentStepIndex
        resumeVerifiedSteps = session.verifiedSteps
        pausedSession = session
    }

    fun clearResumeCheckpoint() {
        resumeStepIndex = 0
        resumeVerifiedSteps = emptyList()
        pausedSession = null
        InProgressStore.finish(context)
    }

    LaunchedEffect(resumeGeneration) {
        if (resumeGeneration == 0) return@LaunchedEffect
        val session = InProgressStore.load(context) ?: return@LaunchedEffect
        applyPausedSession(session)
        ResumeNotifier.cancel(context)
        navController.navigate("verification") {
            launchSingleTop = true
        }
    }

    LaunchedEffect(coldResumeSession) {
        if (coldResumeSession != null) {
            ResumeNotifier.cancel(context)
        }
    }


    // ==========================================
    // VERIFICATION RESULTS
    // ==========================================

    var completedSteps by remember {
        mutableStateOf<List<Boolean>>(emptyList())
    }

    var wrongAttempts by remember {
        mutableIntStateOf(0)
    }

    var verificationTimeMillis by remember {
        mutableLongStateOf(0L)
    }

    var finalScore by remember {
        mutableIntStateOf(0)
    }

    var manuallySkippedSteps by remember {
        mutableStateOf<Set<Int>>(emptySet())
    }


    // ==========================================
    // COMPONENT IDENTIFICATION STATE
    // ==========================================

    var isAnalyzing by remember {
        mutableStateOf(false)
    }

    var analysisMessage by remember {
        mutableStateOf<String?>(null)
    }

    var detectedConfidence by remember {
        mutableFloatStateOf(0.92f)
    }


    // ==========================================
    // CURRENT NAVIGATION DESTINATION
    // ==========================================

    val navBackStackEntry by
    navController.currentBackStackEntryAsState()

    val currentDestination =
        navBackStackEntry?.destination


    // ==========================================
    // MAIN BOTTOM NAVIGATION ROUTES
    // ==========================================

    val bottomNavigationRoutes =
        listOf(
            "home",
            "library",
            "history",
            "account"
        )

    val showBottomNavigation =
        currentDestination?.route in bottomNavigationRoutes


    // ==========================================
    // MAIN APP LAYOUT
    // ==========================================

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            NavHost(
                navController = navController,
                startDestination = if (coldResumeSession != null) "verification" else "splash",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
            ) {

                composable("splash") {
                    SplashScreen(
                        onFinished = {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                // ==========================================
                // HOME
                // ==========================================

                composable("home") {
                    LaunchedEffect(Unit) {
                        pausedSession = InProgressStore.load(context)
                    }

                    HomeScreen(
                        pausedSession = pausedSession,
                        onResumePaused = {
                            val session = InProgressStore.load(context) ?: return@HomeScreen
                            applyPausedSession(session)
                            ResumeNotifier.cancel(context)
                            navController.navigate("verification") {
                                launchSingleTop = true
                            }
                        },
                        onDismissPaused = {
                            clearResumeCheckpoint()
                        },
                        onScanComponent = {
                            navController.navigate("camera") {
                                launchSingleTop = true
                            }
                        },
                        onOpenLibrary = {
                            navController.navigate("library") {
                                popUpTo("home") { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        onStartJob = { job ->
                            clearResumeCheckpoint()
                            selectedComponentId = when (job.id) {
                                "air_filter_service"     -> "air_filter"
                                "oil_filter_inspection"  -> "engine_oil_dipstick"
                                "spark_plug_replacement" -> "spark_plug"
                                "battery_inspection"     -> "battery_terminal"
                                else                     -> job.id
                            }
                            selectedTaskId = when (job.id) {
                                "air_filter_service" -> "air_filter_inspection"
                                "oil_filter_inspection" -> "engine_oil_dipstick_inspection"
                                "spark_plug_replacement" -> "spark_plug_change"
                                "battery_inspection" -> "battery_terminal_change"
                                else -> "air_filter_inspection"
                            }

                            completedSteps = emptyList()
                            wrongAttempts = 0
                            verificationTimeMillis = 0L
                            finalScore = 0
                            manuallySkippedSteps = emptySet()
                            isAnalyzing = false
                            analysisMessage = null

                            navController.navigate("verification") {
                                launchSingleTop = true
                            }
                        }
                    )
                }


                // ==========================================
                // LIBRARY (Enhanced — clickable cards)
                // ==========================================

                composable("library") {

                    LibraryScreen(
                        onComponentClick = { componentId ->
                            navController.navigate("theory_detail/$componentId") {
                                launchSingleTop = true
                            }
                        }
                    )
                }


                // ==========================================
                // THEORY DETAIL
                // ==========================================

                composable("theory_detail/{componentId}") { backStackEntry ->

                    val componentId = backStackEntry.arguments
                        ?.getString("componentId") ?: ""

                    val theory = theoryContentMap[componentId]

                    if (theory != null) {
                        LaunchedEffect(componentId) {
                            DailyGoalStore.markTheoryCompleted(context)
                            withContext(Dispatchers.IO) {
                                DailyGoalStore.refreshFromHistory(context)
                            }
                        }

                        TheoryDetailScreen(
                            theory = theory,

                            onBack = {
                                navController.popBackStack()
                            },

                            onStartPractical = {
                                // Reset session and go to camera
                                clearResumeCheckpoint()
                                selectedComponentId = ""
                                selectedTaskId = ""
                                completedSteps = emptyList()
                                wrongAttempts = 0
                                verificationTimeMillis = 0L
                                finalScore = 0
                                manuallySkippedSteps = emptySet()
                                isAnalyzing = false
                                analysisMessage = null

                                navController.navigate("camera") {
                                    launchSingleTop = true
                                }
                            },

                            onOpenChatbot = {
                                navController.navigate("chatbot/$componentId") {
                                    launchSingleTop = true
                                }
                            }
                        )

                    } else {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Theory content not found for: $componentId")
                        }
                    }
                }


                // ==========================================
                // AI CHATBOT
                // ==========================================

                composable("chatbot/{componentId}") { backStackEntry ->

                    val componentId = backStackEntry.arguments
                        ?.getString("componentId") ?: ""

                    val theory = theoryContentMap[componentId]

                    if (theory != null) {

                        ChatbotScreen(
                            theory = theory,
                            chatbotService = chatbotService,
                            onBack = {
                                navController.popBackStack()
                            }
                        )

                    } else {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Chatbot content not found for: $componentId")
                        }
                    }
                }


                // ==========================================
                // HISTORY
                // ==========================================

                composable("history") {

                    HistoryScreen()
                }


                // ==========================================
                // ACCOUNT
                // ==========================================

                composable("account") {

                    AccountScreen()
                }


                // ==========================================
                // COMPONENT IDENTIFICATION CAMERA
                // ==========================================

                composable("camera") {

                    CameraScreen(

                        isAnalyzing =
                            isAnalyzing,

                        analysisMessage =
                            analysisMessage,

                        onImageCaptured = { bitmap ->


                            if (isAnalyzing) {

                                return@CameraScreen
                            }


                            isAnalyzing = true

                            analysisMessage =
                                "Identifying component..."


                            coroutineScope.launch {

                                try {


                                    // ==================================
                                    // RUN COMPONENT IDENTIFICATION MODEL
                                    // ==================================

                                    val result =
                                        withContext(
                                            Dispatchers.Default
                                        ) {

                                            mlClassifier.classify(bitmap)
                                        }


                                    val detectedId =
                                        normalizeTFLiteComponentId(
                                            result.label
                                        )


                                    android.util.Log.d(
                                        "TFLITE_COMPONENT",
                                        "Raw label: ${result.label}"
                                    )


                                    android.util.Log.d(
                                        "TFLITE_COMPONENT",
                                        "Confidence: ${result.confidence}"
                                    )


                                    android.util.Log.d(
                                        "TFLITE_COMPONENT",
                                        "Normalized ID: $detectedId"
                                    )


                                    // ==================================
                                    // CONFIDENCE CHECK
                                    // ==================================

                                    if (result.confidence < com.autolearn.ar.data.PreferencesManager.confidenceThreshold(context)) {

                                        isAnalyzing = false

                                        analysisMessage =
                                            "Component not recognized clearly. " +
                                                    "Please move closer and try again."

                                        return@launch
                                    }


                                    // ==================================
                                    // LABEL CHECK
                                    // ==================================

                                    if (detectedId == null) {

                                        isAnalyzing = false

                                        analysisMessage =
                                            "Component not recognized."

                                        return@launch
                                    }


                                    // ==================================
                                    // FIND IDENTIFIED COMPONENT
                                    // ==================================

                                    val detectedComponent =
                                        altoComponents.firstOrNull {

                                            it.id.equals(
                                                detectedId,
                                                ignoreCase = true
                                            )
                                        }


                                    if (detectedComponent == null) {

                                        isAnalyzing = false

                                        analysisMessage =
                                            "Detected '$detectedId', " +
                                                    "but this component is not configured."

                                        return@launch
                                    }


                                    // ==================================
                                    // FIND TASK FOR COMPONENT
                                    // ==================================

                                    val detectedTask =
                                        altoTasks.firstOrNull {

                                            it.componentId.equals(
                                                detectedComponent.id,
                                                ignoreCase = true
                                            )
                                        }


                                    if (detectedTask == null) {

                                        isAnalyzing = false

                                        analysisMessage =
                                            "No practical task configured for " +
                                                    detectedComponent.name

                                        return@launch
                                    }


                                    // ==================================
                                    // SAVE IDENTIFIED COMPONENT + TASK
                                    // ==================================

                                    selectedComponentId =
                                        detectedComponent.id

                                    selectedTaskId =
                                        detectedTask.id

                                    detectedConfidence =
                                        result.confidence

                                    resumeStepIndex = 0
                                    resumeVerifiedSteps = emptyList()
                                    pausedSession = null
                                    InProgressStore.finish(context)

                                    completedSteps =
                                        emptyList()

                                    wrongAttempts = 0

                                    verificationTimeMillis = 0L

                                    finalScore = 0

                                    manuallySkippedSteps =
                                        emptySet()

                                    isAnalyzing = false

                                    analysisMessage = null

                                    DailyGoalStore.markScanCompleted(context)
                                    coroutineScope.launch {
                                        withContext(Dispatchers.IO) {
                                            DailyGoalStore.refreshFromHistory(context)
                                        }
                                    }

                                    // ==================================
                                    // OPEN COMPONENT IDENTIFY SCREEN
                                    // ==================================

                                    navController.navigate(
                                        "component_detail"
                                    ) {

                                        popUpTo("camera") {
                                            inclusive = true
                                        }

                                        launchSingleTop = true
                                    }


                                } catch (e: Exception) {


                                    android.util.Log.e(
                                        "TFLITE_COMPONENT",
                                        "Classification failed",
                                        e
                                    )


                                    isAnalyzing = false


                                    analysisMessage =
                                        "Identification failed: " +
                                                (
                                                        e.message
                                                            ?: "Unknown error"
                                                        )
                                }
                            }
                        }
                    )
                }


                // ==========================================
                // COMPONENT IDENTIFIED SCREEN
                // ==========================================

                composable("component_detail") {

                    val component =
                        altoComponents.firstOrNull {
                            it.id.equals(
                                selectedComponentId,
                                ignoreCase = true
                            )
                        }

                    if (component != null) {

                        ComponentScreen(
                            component = component,
                            confidence = detectedConfidence,
                            onContinue = {
                                navController.navigate("verification") {
                                    launchSingleTop = true
                                }
                            }
                        )

                    } else {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Component details not found.")
                        }
                    }
                }


                // ==========================================
                // TASK RECOGNITION SCREEN
                // ==========================================

                composable("task_recognition") {

                    val task =
                        altoTasks.firstOrNull {
                            it.id.equals(
                                selectedTaskId,
                                ignoreCase = true
                            )
                        } ?: altoTasks.firstOrNull {
                            it.componentId.equals(
                                selectedComponentId,
                                ignoreCase = true
                            )
                        }

                    if (task != null) {

                        TaskRecognitionScreen(
                            task = task,
                            onContinue = {
                                navController.navigate("verification") {
                                    launchSingleTop = true
                                }
                            }
                        )

                    } else {

                        Box(modifier = Modifier.fillMaxSize()) {
                            Text(text = "Task details not found.")
                        }
                    }
                }


                // ==========================================
                // REAL-TIME VERIFICATION
                // ==========================================

                composable("verification") {


                    val task =
                        altoTasks.firstOrNull {

                            it.id.equals(
                                selectedTaskId,
                                ignoreCase = true
                            )
                        }


                    if (task != null) {


                        VerificationScreen(

                            task =
                                task,

                            completedSteps =
                                completedSteps,

                            initialStepIndex =
                                resumeStepIndex,

                            initialVerifiedSteps =
                                resumeVerifiedSteps,

                            onComplete = {
                                    results,
                                    attempts,
                                    timeMillis,
                                    score,
                                    manualSteps ->


                                // ==============================
                                // SAVE VERIFICATION RESULTS
                                // ==============================

                                completedSteps =
                                    results

                                wrongAttempts =
                                    attempts

                                verificationTimeMillis =
                                    timeMillis

                                finalScore =
                                    score

                                manuallySkippedSteps =
                                    manualSteps

                                pausedSession = null
                                resumeStepIndex = 0
                                resumeVerifiedSteps = emptyList()
                                InProgressStore.finish(context)


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "=================================="
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "VERIFICATION COMPLETED"
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "Completed steps: $results"
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "Wrong attempts: $attempts"
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "Time taken: $timeMillis ms"
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "Final score: $score"
                                )


                                android.util.Log.d(
                                    "VERIFICATION_RESULT",
                                    "Manual steps: $manualSteps"
                                )


                                // ==============================
                                // OPEN EVALUATION
                                // ==============================

                                navController.navigate(
                                    "evaluation"
                                ) {

                                    popUpTo("verification") {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            },
                            onLeave = {
                                navController.popBackStack()
                            }
                        )

                    } else {


                        // Fallback if task state is missing

                        Box(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            Text(
                                text =
                                    "Task not found."
                            )
                        }
                    }
                }


                // ==========================================
                // FINAL EVALUATION
                // ==========================================

                composable("evaluation") {


                    val task =
                        altoTasks.firstOrNull {

                            it.id.equals(
                                selectedTaskId,
                                ignoreCase = true
                            )
                        }


                    if (task != null) {


                        val warnings =
                            safetyRules[
                                selectedComponentId
                            ].orEmpty()


                        EvaluationScreen(

                            task =
                                task,

                            completedSteps =
                                completedSteps,

                            wrongAttempts =
                                wrongAttempts,

                            timeTakenMillis =
                                verificationTimeMillis,

                            finalScore =
                                finalScore,

                            manuallySkippedSteps =
                                manuallySkippedSteps,

                            safetyWarnings =
                                warnings,


                            // ==============================
                            // TRY AGAIN
                            // ==============================

                            onTryAgain = {


                                completedSteps =
                                    emptyList()

                                wrongAttempts = 0

                                verificationTimeMillis = 0L

                                finalScore = 0

                                manuallySkippedSteps =
                                    emptySet()


                                navController.navigate(
                                    "verification"
                                ) {

                                    popUpTo("evaluation") {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            },


                            // ==============================
                            // REVIEW THEORY (the loop!)
                            // ==============================

                            onReviewTheory = {

                                navController.navigate(
                                    "theory_detail/$selectedComponentId"
                                ) {

                                    popUpTo("evaluation") {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            },


                            // ==============================
                            // FINISH
                            // ==============================

                            onFinish = {


                                selectedComponentId = ""

                                selectedTaskId = ""

                                completedSteps =
                                    emptyList()

                                wrongAttempts = 0

                                verificationTimeMillis = 0L

                                finalScore = 0

                                manuallySkippedSteps =
                                    emptySet()

                                isAnalyzing = false

                                analysisMessage = null

                                clearResumeCheckpoint()


                                navController.navigate(
                                    "home"
                                ) {

                                    popUpTo(
                                        navController.graph.id
                                    ) {
                                        inclusive = true
                                    }

                                    launchSingleTop = true
                                }
                            }
                        )

                    } else {


                        // Fallback if task state is missing

                        Box(
                            modifier =
                                Modifier.fillMaxSize()
                        ) {

                            Text(
                                text =
                                    "Evaluation task not found."
                            )
                        }
                    }
                }
            }

            if (showBottomNavigation) {
                Spacer(modifier = Modifier.height(16.dp))
                FloatingAppBar(
                    navController = navController,
                    currentDestination = currentDestination
                )
            }
        }
    }
}


/*
 * ==========================================
 * NORMALIZE COMPONENT MODEL LABEL
 * ==========================================
 */
private fun normalizeTFLiteComponentId(
    rawLabel: String
): String? {

    val label =
        rawLabel
            .trim()
            .lowercase()
            .replace("-", "_")
            .replace(" ", "_")


    return when (label) {


        "air_filter",
        "airfilter",
        "air filter" -> {

            "air_filter"
        }


        "battery",
        "battery_terminal",
        "batteryterminal",
        "car_battery" -> {

            "battery_terminal"
        }


        "spark_plug",
        "sparkplug",
        "spark plug" -> {

            "spark_plug"
        }


        "engine_oil_dipstick",
        "oil_dipstick",
        "dipstick",
        "oil_filter",
        "oilfilter",
        "oil filter" -> {

            "engine_oil_dipstick"
        }


        else -> {

            null
        }
    }
}