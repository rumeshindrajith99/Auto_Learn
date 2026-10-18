# AutoLearn AR (EngiScan)
 
**AR-Based Intelligent Automobile Repair Learning and Evaluation System**
Research project **RP26-IT-049** · Sri Lanka Institute of Information Technology (SLIIT)
 
An Android app that teaches automotive maintenance through the phone camera. It **identifies an engine component, guides the student step by step with AR-style overlays, recognises which stage of the task the student has reached, verifies each step, warns about safety, scores the attempt, and answers questions through an AI tutor**. The vision models run on the device, so the core learning flow works without internet.
 
> Package: `com.autolearn.ar` · App label: **EngiScan** · Kotlin + Jetpack Compose
 
---
 
## Table of contents
 
- [Why this project](#why-this-project)
- [Features](#features)
- [System overview](#system-overview)
- [Modules in detail](#modules-in-detail)
- [Supported tasks](#supported-tasks)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Models and datasets](#models-and-datasets)
- [App screens](#app-screens)
- [Privacy and security](#privacy-and-security)
- [Known limitations](#known-limitations)
- [Roadmap](#roadmap)
- [Team and credits](#team-and-credits)
- [License](#license)
---
 
## Why this project
 
In workshop training, one instructor supervises many students, so mistakes (wrong order, skipped steps, unsafe handling) are often noticed late. Paper manuals and videos do not observe the learner, and most AR maintenance apps only show instructions without checking the result. AutoLearn AR closes the loop: it **sees** what the learner is doing, **confirms** each step, and **gives feedback and a score**, using only a smartphone.
 
## Features
 
| Area | What the app does |
|------|-------------------|
| Component identification | Recognises the engine component in view with an on-device TensorFlow Lite classifier |
| Task recognition | Maps the identified component to its maintenance task using rule-based logic |
| Instructions and theory | Ordered steps, theory content and practical job information for each task |
| AR-style guidance | Custom 3D-to-2D projection engine draws guidance and motion cues over the camera preview |
| Action recognition | A second on-device model recognises the current stage of the task |
| Task verification | A step is verified when the expected stage is held for 3 seconds; the task advances automatically |
| Safety warnings | Step-specific safety notes shown during the task |
| Performance score | Weighted score from completion, time, model confidence and precision, plus a final evaluation report |
| Score history | Stores previous attempts locally |
| AI tutor chatbot | Gemini-powered Q&A grounded in the current task's theory content |
 
## System overview
 
```
                    +----------------------------+
 Camera (CameraX) ->| Component identification   |-> task selected
                    | (MobileNetV2, TFLite)      |
                    +----------------------------+
                                 |
                    +----------------------------+
                    | Task recognition + data    |-> steps, theory, safety
                    +----------------------------+
                                 |
        +------------------------+------------------------+
        |                        |                        |
 AR-style guidance      Action recognition          AI tutor chatbot
 (Spatial3DEngine,      (TFLite, ~400 ms)           (Gemini, text only)
  PartGuidanceEngine)           |
                       Verification screen
                       (map label -> step, hold 3 s)
                                |
                       Evaluation engine -> score, report, history
```
 
## Modules in detail
 
### 1. Component identification
 
- On-device image classifier (`MLClassifier`) with a MobileNetV2-style TFLite model.
- Classes: `air_filter`, `battery_terminal`, `engine_oil_dipstick`, `spark_plug`.
- Uses a confidence threshold so uncertain frames are ignored.
### 2. Task recognition and task data
 
- `TaskRecognizer` finds the task for the identified component by rule-based lookup.
- Static Kotlin data files hold tasks, theory content, practical job data, component data and safety notes (`TaskData`, `TheoryContent`, `PracticalJobData`, `ComponentData`, `SafetyData`).
### 3. AR-style guidance
 
- `Spatial3DEngine` and `PartGuidanceEngine` implement their own pitch/yaw rotation and perspective projection and draw cuboids and animated motion arcs (for example "lift", "unclip", "rotate") on a Compose `Canvas`.
- This is lightweight guidance, not world-tracked AR: it does not use ARCore or follow the physical part.
### 4. Action recognition and verification
 
- Per-frame image classification of the **stage** of the task (four ordered stages per task), 224 x 224 input, about one prediction every 400 ms.
- Quantised TFLite model (335.7 KB), exported with `action_labels.json`, `step_mapping.json` and `component_mapping.json`.
- `VerificationScreen` maps each predicted label to a step and requires **3 continuous seconds** of the expected stage before verifying it.
### 5. Evaluation and scoring
 
`EvaluationEngine` and the score calculator combine four components:
 
| Component | Weight |
|-----------|--------|
| Task completion | 40% |
| Time taken (baseline 30 s) | 25% |
| Average model confidence (stability) | 20% |
| Precision (penalty for resets) | 15% |
 
`ScoreHistoryManager` keeps previous results on the device.
 
### 6. AI tutor chatbot
 
- `ChatbotService` calls the Gemini API (via OkHttp) with a system prompt that includes the current task's theory and safety-first instructions.
- It sends text only, never camera frames.
## Supported tasks
 
| # | Task | Stages (in order) |
|---|------|-------------------|
| 01 | Air filter inspection | air filter closed, air filter opening, inspection, reinstall |
| 02 | Battery terminal | battery connected, negative unclamped, positive unclamped, removed |
 
## Tech stack
 
| Area | Technology |
|------|------------|
| Language and UI | Kotlin, Jetpack Compose, Material 3 |
| Navigation | Navigation-Compose |
| Camera | CameraX (core, camera2, lifecycle, view) |
| On-device ML | TensorFlow Lite (CPU interpreter) |
| Cloud AI | Gemini API via OkHttp, JSON via `org.json` |
| Model training | Python, TensorFlow/Keras, Google Colab |
| Build | Gradle Kotlin DSL, Android Studio |
 
## Project structure
 
```
R26-IT-049/
  app/
    build.gradle.kts
    src/main/
      AndroidManifest.xml
      assets/                  .tflite models and label files
      java/com/autolearn/ar/
        ai/                    MLClassifier, TaskRecognizer, EvaluationEngine,
                               Spatial3DEngine, PartGuidanceEngine, ChatbotService ...
        ui/                    Compose screens, CameraPreview, TFLiteClassifier ...
        navigation/            AppNavigation.kt
        data/ model/           task, component, theory, safety and score data
  build.gradle.kts
  settings.gradle.kts
  README.md
```
 
## Getting started
 
### Prerequisites
 
- Android Studio (recent stable version)
- An Android device (recommended) or an emulator with a camera
- A Gemini API key (only needed for the chatbot)
### Run
 
1. Clone the repository:
```bash
   git clone https://github.com/sahanchanuka24/R26-IT-049.git
   cd R26-IT-049
```
 
3. Open the project in Android Studio and let Gradle sync. Keep the supplied Gradle and plugin versions unless you have a reason to upgrade.
4. Run on a device and grant the camera permission.
> Never commit API keys. Keep them in `local.properties` or a secrets manager.
 
## Models and datasets
 
| Model | Purpose | Notes |
|-------|---------|-------|
| Component model (`component_model.tflite`) | Identify the component | Labels in `labels.txt` |
| Action model (`.tflite` + `action_labels.json`) | Recognise the task stage | 16 classes, 335.7 KB, 95.24% accuracy reported in the notebook |
 
**Action recognition dataset:** 4 tasks x 4 stages = 16 classes, **150 images per class, 2,400 images**, labelled by folder.
 
The action model was trained in Google Colab (notebook: `AutoLearn AR - Action Recognition test.ipynb`): load the dataset, augment the training split, fine-tune a MobileNet-family network for about 45 epochs, and export a quantised TFLite model plus the label and mapping JSON files. To change a model, copy the new `.tflite` and JSON files into `app/src/main/assets/` and keep the label order identical to the model output order.
 
## App screens
 
| Screen | Purpose |
|--------|---------|
| Home | Entry point and navigation |
| Camera | Live preview and component identification |
| Task | Instructions, theory and safety information |
| Verification | Live step verification with hold progress |
| Evaluation | Final score and report |
| History | Previous attempts and scores |
| Chatbot | AI tutor Q&A |
| Account | User and app settings |
 
## Privacy and security
 
- Vision inference runs entirely on the device; camera frames are not stored or uploaded.
- The chatbot sends text questions only.
- API keys are read from `local.properties` at build time and kept out of source control. A key compiled into an APK can be extracted, so use a restricted key or a small backend proxy for any public release.
## Known limitations
 
- **Action recognition sees appearance, not motion.** A finished state shown to the camera can be credited as a completed step.
- **Validation curves in the action model training were unstable**, so the 95.24% figure should be confirmed on an independent, session-split test set.
- **Datasets are small** (150 images per action class) and captured in limited conditions.
- **Low live acceptance threshold** (0.15) for action verification, which is permissive for a 16-way classifier.
- **Model coverage:** the verification mapping has branches for all four tasks, but confirm which task models are bundled in `assets/` for your build.
- **AR guidance is not world-tracked**; overlays follow a fixed projection, not the physical part.
- **Task and theory content is static** in Kotlin data files.
- **Cleanup items:** an older `ARScreen` (different labels, 0.70 threshold, 5-frame consensus) is not wired into navigation; `DemoComponentDetector` and the Gemini-vision `ComponentAnalyzer` are unused; TensorFlow Lite is declared twice in the Gradle file with different versions; comments about input rescaling contradict each other and should be checked against the exported model.
## Roadmap
 
- [ ] Bundle action models for all four tasks (or one shared 16-class model with a task selector)
- [ ] Majority-vote or tolerance window in place of the strict 3 s hold
- [ ] ARCore or marker-free tracking so overlays stay on the part
- [ ] Session-based train/validation/test split and an independent test set
- [ ] GPU or NNAPI delegate and faster frame conversion
- [ ] Move task content to editable files or a backend
- [ ] Controlled user study of learning outcomes
- [ ] Remove dead code and unify dependencies
## Team and credits
 
Group **RP26-IT-049**, B.Sc. (Hons) in Information Technology, SLIIT.
 
- Action recognition and task verification: **Jayalath S.P.S.C.K.** (IT22231864)
- Supervisor: **Ms. Anjalee Gamage**
Built with TensorFlow Lite, CameraX, Jetpack Compose
 
## License
 
This project was developed for academic purposes at SLIIT. All rights reserved by the authors unless a license file states otherwise.
 
