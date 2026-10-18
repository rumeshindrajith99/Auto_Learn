# AutoLearn AR (EngiScan)
 
**AR-Based Intelligent Automobile Repair Learning and Evaluation System**
Research project **RP26-IT-049** · Sri Lanka Institute of Information Technology (SLIIT)
 
An Android app that helps automotive students learn engine-bay maintenance. Point the phone at a component, follow AR-style guidance step by step, and let the app **recognise which stage of the task you are at, verify each step, and score your performance**, all on-device.
 
> Package: `com.autolearn.ar` · App label: **EngiScan** · Kotlin + Jetpack Compose
 
---
 
## Table of contents
 
- [Features](#features)
- [How it works](#how-it-works)
- [Action Recognition module](#action-recognition-module)
- [Tech stack](#tech-stack)
- [Project structure](#project-structure)
- [Getting started](#getting-started)
- [Dataset](#dataset)
- [Model training](#model-training)
- [Known limitations](#known-limitations)
- [Roadmap](#roadmap)
- [Team and credits](#team-and-credits)
- [License](#license)
---
 
## Features
 
- **Component identification**: on-device TensorFlow Lite classifier recognises the engine component in view.
- **Task instructions**: ordered steps, theory content and safety warnings for each task.
- **AR-style guidance**: custom 3D-to-2D projection engine draws guidance overlays on the camera preview (no ARCore dependency).
- **Action recognition and verification**: a second on-device model recognises the current stage of the task; a step is verified when the expected stage is held for 3 seconds.
- **Performance score**: weighted score from completion, time, model confidence and precision (resets), with a final evaluation report and score history.
- **AI tutor chatbot**: Gemini-powered Q&A grounded in the current task's theory content.
- **Offline-first**: both vision models run on the device; camera frames are never uploaded.
## How it works
 
```
Camera (CameraX)
   |
   +--> Component model (MobileNetV2, TFLite) --> selects the task
   |
   +--> Action model (TFLite, 224x224) every ~400 ms --> Prediction(label, confidence)
                                                              |
                          Verification screen: map label -> step, hold 3 s --> step verified
                                                              |
                                              Evaluation engine --> score + report
```
 
## Action Recognition module
 
This module answers: *"Which stage of the maintenance procedure is the student at right now?"*
 
- **Formulation:** per-frame image classification. Each task has four ordered stages, and each stage is a class.
- **Input:** 224 x 224 RGB frames from CameraX `ImageAnalysis`, throttled to one prediction about every 400 ms.
- **Model:** MobileNet-family network fine-tuned in Google Colab and exported as a quantised TensorFlow Lite file (**335.7 KB**).
- **Reported accuracy:** **95.24%** in the training notebook (see [limitations](#known-limitations) for how to read this figure).
- **Verification rule:** the predicted stage must match the expected step and stay matched for **3 continuous seconds**; a break resets the timer.
- **Exported artefacts:** `action_model.tflite`, `action_labels.json`, `step_mapping.json`, `component_mapping.json`.
### Tasks and stages
 
| # | Task | Stage classes (in order) |
|---|------|--------------------------|
| 01 | Air filter inspection | air filter closed, air filter opening, inspection, reinstall |
| 02 | Battery terminal | battery connected, negative unclamped, positive unclamped, removed |
| 03 | Engine oil dipstick inspection | dipstick inserted, dipstick pulled, wiped clean, level inspected |
| 04 | Spark plug replacement | ignition boot on, boot removed, plug loosened, new plug seated |
 
## Tech stack
 
| Area | Technology |
|------|------------|
| Language / UI | Kotlin, Jetpack Compose, Material 3 |
| Navigation | Navigation-Compose |
| Camera | CameraX (core, camera2, lifecycle, view) |
| On-device ML | TensorFlow Lite (CPU interpreter) |
| Model training | Python, TensorFlow/Keras, Google Colab |
| Cloud assistant | Gemini API via OkHttp (text only) |
| Build | Gradle Kotlin DSL, Android Studio |
 
## Project structure
 
```
app/src/main/
  java/com/autolearn/ar/
    ai/           TFLite classifiers, task recognition, evaluation, AR guidance, chatbot
    ui/           Compose screens (Camera, Verification, Evaluation, History, Chatbot ...)
    navigation/   AppNavigation.kt
    data/ model/  task, component, theory and safety data
  assets/         .tflite models and label files
```
 
Key files for the action recognition module:
 
| File | Role |
|------|------|
| `TFLiteClassifier.kt` | Loads the model and labels, classifies one frame |
| `CameraPreview.kt` | CameraX analysis, throttling, bitmap conversion |
| `VerificationScreen.kt` | Step mapping, hold timer, progress UI, scoring inputs |
| `EvaluationEngine.kt` | Final score and report |
 
## Getting started
 
### Prerequisites
 
- Android Studio (recent stable version)
- An Android device (recommended) or emulator with camera support
- A Gemini API key (only needed for the chatbot)
### Run
 
1. Clone the repository
```bash
   git clone <your-repo-url>
   cd AutoLearnAR_Project
```
2. Add your API key to `local.properties` (this file is git-ignored). Use the property name that `app/build.gradle.kts` reads:
```properties
   GEMINI_API_KEY=your_key_here
```
3. Open the project in Android Studio and let Gradle sync. Keep the supplied Gradle and plugin versions unless you have a reason to upgrade.
4. Run on a device and grant the camera permission.
> Never commit API keys. Keep them in `local.properties` or a secrets manager.
 
## Dataset
 
- 4 tasks x 4 stages = **16 classes**
- **150 images per class**, **2,400 images** in total (perfectly balanced)
- Labelled by folder (`task/stage/image.jpg`), no bounding boxes needed
- Capture details, split and augmentation: see the thesis or add them here: `[phone, vehicle, lighting, split]`
## Model training
 
The training notebook (`AutoLearn AR - Action Recognition test.ipynb`) does the following:
 
1. Loads the 16-class dataset and applies augmentation to the training split.
2. Fine-tunes a MobileNet-family network for about 45 epochs.
3. Plots training and validation accuracy and loss.
4. Converts the model to quantised TensorFlow Lite and writes the four artefacts listed above.
To use a new model, copy the `.tflite` and JSON files into `app/src/main/assets/` and make sure the label order matches the model output order.
 
## Known limitations
 
Being honest about these makes the project more useful to anyone building on it:
 
- **Appearance, not motion.** The classifier recognises the visual end-state of a step, not the motion that produced it, so presenting a finished state can be credited.
- **Unstable validation curves.** Training accuracy is high, but validation accuracy and loss fluctuate heavily, so the 95.24% figure should be confirmed on an independent, session-split test set.
- **Small dataset.** 150 images per class from limited capture conditions; performance on other vehicles, phones and lighting is untested.
- **Low acceptance threshold.** The live verification path uses a 0.15 confidence threshold, which is permissive for a 16-way classifier.
- **Model coverage.** The verification mapping has branches for all four tasks, but check which task models are actually bundled in `assets/` for your build.
- **Preprocessing check.** Confirm the rescaling used in training matches the rescaling inside the exported graph.
- **Cleanup items.** An older `ARScreen` (different label set, 0.70 threshold, 5-frame consensus) is not wired into navigation; TensorFlow Lite is declared twice in the Gradle file with different versions.
## Roadmap
 
- [ ] Train and bundle models for the battery, dipstick and spark plug tasks
- [ ] Majority-vote or tolerance window instead of a strict 3 s hold
- [ ] Hand-landmark check (for example MediaPipe) to verify motion
- [ ] Session-based train/validation/test split and an independent test set
- [ ] GPU or NNAPI delegate and faster frame conversion
- [ ] Controlled user study of learning outcomes
## Team and credits
 
Group **RP26-IT-049**, B.Sc. (Hons) in Information Technology, SLIIT.
 
- **Action Recognition module:** Jayalath S.P.S.C.K. (IT22231864)
- Other components: `[team member names and components]`
- Supervisor: `[name]`
Built with TensorFlow Lite, CameraX, Jetpack Compose and the Gemini API.
 
## License
 
`[Choose a license, for example MIT, and add a LICENSE file. Academic projects often keep the code private or all-rights-reserved until assessment is complete.]`
 
