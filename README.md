# Action Recognition Module
 
Part of **AutoLearn AR (EngiScan)**: the AR-Based Intelligent Automobile Repair Learning and Evaluation System (research project **RP26-IT-049**, SLIIT).
 
This module recognises, from the live phone camera, **which stage of a maintenance task the student has reached**. The result is used to verify each step automatically and to feed the performance score.
 
---
 
## Table of contents
 
- [Overview](#overview)
- [How it works](#how-it-works)
- [Tasks and stage classes](#tasks-and-stage-classes)
- [Dataset](#dataset)
- [Model and training](#model-and-training)
- [On-device pipeline](#on-device-pipeline)
- [Step verification](#step-verification)
- [Scoring](#scoring)
- [Key files](#key-files)
- [Using or replacing the model](#using-or-replacing-the-model)
- [Technology](#technology)
- [Known limitations](#known-limitations)
- [Future work](#future-work)
- [Author](#author)
---
 
## Overview
 
| Item | Detail |
|------|--------|
| Purpose | Recognise the current stage of a maintenance task and verify steps |
| Approach | Per-frame image classification |
| Classes | 16 (4 tasks x 4 stages) |
| Input | 224 x 224 RGB camera frames |
| Model format | Quantised TensorFlow Lite, 335.7 KB |
| Reported accuracy | 95.24% (training notebook) |
| Prediction rate | About one prediction every 400 ms |
| Verification rule | Expected stage held for 3 continuous seconds |
| Runtime | Fully on-device, no network needed |
 
## How it works
 
```
Camera (CameraX ImageAnalysis)
        |
        |  one frame about every 400 ms
        v
Bitmap -> resize 224 x 224 -> TFLite model
        |
        v
Prediction(label, confidence)
        |
        v
Map label -> step index for the active task
        |
        v
Expected step held for 3 s?  -- no --> reset hold timer
        |
       yes
        v
Step verified -> advance to next step -> after step 4: evaluation and score
```
 
## Tasks and stage classes
 
Each task is an ordered sequence of four stages. Each stage is one class.
 
| # | Task | Stage classes (in order) |
|---|------|--------------------------|
| 01 | Air filter inspection | air filter closed, air filter opening, inspection, reinstall |
| 02 | Battery terminal | battery connected, negative unclamped, positive unclamped, removed |
| 03 | Engine oil dipstick inspection | dipstick inserted, dipstick pulled, wiped clean, level inspected |
| 04 | Spark plug replacement | ignition boot on, boot removed, plug loosened, new plug seated |
 
## Dataset
 
| Property | Value |
|----------|-------|
| Tasks | 4 |
| Stages per task | 4 |
| Classes | 16 |
| Images per class | 150 |
| Images per task | 600 |
| Total images | 2,400 |
| Balance | Perfectly balanced |
| Labelling | By folder: `task/stage/image.jpg` |
 
No bounding boxes are required because the module performs classification, not detection.
 
## Model and training
 
The model was trained in Google Colab (`AutoLearn AR - Action Recognition test.ipynb`).
 
1. Load the 16-class dataset.
2. Apply data augmentation to the training split.
3. Fine-tune a MobileNet-family network for about 45 epochs.
4. Plot training and validation accuracy and loss.
5. Convert to a quantised TensorFlow Lite model.
Exported files:
 
| File | Content |
|------|---------|
| `action_model.tflite` | Quantised stage classifier (335.7 KB) |
| `action_labels.json` | Class names in model output order |
| `step_mapping.json` | Ordered stages for each task |
| `component_mapping.json` | Link between each task and its component |
 
## On-device pipeline
 
1. **CameraX** binds an `ImageAnalysis` use case to the camera preview.
2. A timestamp check skips frames that arrive less than 400 ms after the last analysed frame.
3. Each `ImageProxy` (RGBA_8888) is converted to a `Bitmap`.
4. `TFLiteClassifier` resizes the bitmap to 224 x 224, fills a float32 `ByteBuffer`, runs the TensorFlow Lite interpreter and returns the class with the highest score as a `Prediction(label, confidence)`.
5. The prediction is passed to the Verification screen.
## Step verification
 
`VerificationScreen` runs a coroutine loop that:
 
1. Reads the latest prediction.
2. Ignores it if its confidence is below the acceptance threshold (0.15).
3. Maps the label to a step index for the active task.
4. Starts or continues the hold timer when the mapped step equals the expected step; otherwise resets the timer.
5. Verifies the step when the timer reaches **3 seconds**, then advances to the next step.
6. After the fourth step, passes time, confidence and reset count to the evaluation engine.
A progress indicator fills during the hold and resets when the expected stage is lost.
 
## Scoring
 
| Component | Weight |
|-----------|--------|
| Task completion | 40% |
| Time taken (baseline 30 s) | 25% |
| Average model confidence (stability) | 20% |
| Precision (penalty for resets) | 15% |
 
## Key files
 
| File | Role |
|------|------|
| `TFLiteClassifier.kt` | Loads the model and labels; classifies one frame |
| `CameraPreview.kt` | CameraX preview and analysis, throttling, bitmap conversion |
| `VerificationScreen.kt` | Step mapping, hold timer, progress UI, scoring inputs |
| `EvaluationEngine.kt` | Final score and report |
| `assets/action_model.tflite` | Quantised stage classifier |
| `assets/action_labels.json` | Class names |
| `assets/step_mapping.json` | Stage order per task |
| `assets/component_mapping.json` | Task to component links |
 
## Using or replacing the model
 
1. Copy the new `.tflite` file and the JSON files into `app/src/main/assets/`.
2. Keep the label order identical to the model output order.
3. Confirm that the input rescaling used in training matches the rescaling inside the exported graph.
4. Rebuild and run on a device, then test each stage class.
## Technology
 
| Area | Technology |
|------|------------|
| Training | Python, TensorFlow/Keras, Google Colab |
| Model format | TensorFlow Lite (quantised) |
| Backbone | MobileNet family |
| App | Kotlin, Jetpack Compose |
| Camera | CameraX |
| Inference | TensorFlow Lite Interpreter (CPU) |
 
## Known limitations
 
- Per-frame classification recognises the visual end-state of a step, not the motion that produced it.
- Training and validation curves differ strongly, with unstable validation accuracy and loss, so the 95.24% figure needs confirmation on an independent, session-split test set.
- The dataset has 150 images per class from limited capture conditions.
- The live acceptance threshold (0.15) is permissive for a 16-class classifier.
- A single contradictory frame resets the 3-second hold.
- Frame conversion uses a manual pixel loop and inference runs on the CPU without a hardware delegate.
- The verification mapping covers all four tasks; the models bundled in `assets/` determine which tasks verify automatically in a given build.
## Future work
 
- Bundle trained models for all four tasks, or one shared 16-class model with a task selector.
- Replace the strict hold with a majority-vote or tolerance window.
- Add hand-landmark tracking to verify motion as well as appearance.
- Use a session-based train, validation and test split with an independent test set.
- Calibrate confidence thresholds on a clean validation set.
- Use a GPU or NNAPI delegate and faster image conversion.
- Run a controlled user study of learning outcomes.
## Author
 
**Jayalath S.P.S.C.K.** (IT22231864)
Group RP26-IT-049 · B.Sc. (Hons) in Information Technology · Sri Lanka Institute of Information Technology
Supervisor: Ms. Anjalee Gamage
 
