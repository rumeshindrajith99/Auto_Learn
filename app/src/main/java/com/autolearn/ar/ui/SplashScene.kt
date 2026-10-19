package com.autolearn.ar.ui

/**
 * Splash scene script — EngiScan
 *
 * Scene: a workshop bay at first light. A compact engine-block mark
 * sits in frame. A service scan line travels the housing the same way
 * the live camera reticle reads a part. The course title settles, then
 * the bay holds for a beat and cuts to Home.
 *
 * Timing is milliseconds from t = 0.
 */
object SplashScene {
    const val TOTAL_MS = 2600
    const val EXIT_MS = 2200

    const val MARK_START_MS = 0
    const val MARK_END_MS = 450

    const val SCAN_START_MS = 280
    const val SCAN_END_MS = 1400

    const val TITLE_START_MS = 700
    const val TITLE_END_MS = 1500

    const val LINE_START_MS = 1100
    const val LINE_END_MS = 1800

    const val TITLE = "EngiScan"
    const val LINE = "Automobile servicing practicals"
}
