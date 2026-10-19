package com.autolearn.ar

import android.os.Bundle
import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.ProcessLifecycleOwner
import com.autolearn.ar.data.InProgressStore
import com.autolearn.ar.data.ResumeNotifier
import com.autolearn.ar.navigation.AppNavigation
import com.autolearn.ar.ui.theme.EngiTheme

class MainActivity : ComponentActivity() {

    private var resumeGeneration by mutableIntStateOf(0)

    private val appLifecycleObserver = LifecycleEventObserver { _, event ->
        when (event) {
            Lifecycle.Event.ON_STOP -> {
                val session = InProgressStore.load(this) ?: return@LifecycleEventObserver
                ResumeNotifier.show(this, session)
            }
            Lifecycle.Event.ON_START -> {
                ResumeNotifier.cancel(this)
            }
            else -> Unit
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLifecycleObserver)
        enableEdgeToEdge()
        setContent {
            EngiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation(
                        launchIntent = intent,
                        resumeGeneration = resumeGeneration
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        ProcessLifecycleOwner.get().lifecycle.removeObserver(appLifecycleObserver)
        super.onDestroy()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        if (intent.getBooleanExtra(ResumeNotifier.EXTRA_RESUME_PRACTICAL, false)) {
            resumeGeneration++
        }
    }
}
