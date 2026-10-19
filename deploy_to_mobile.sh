#!/bin/bash

# ==========================================
# AutoLearnAR - Deploy to Mobile Device
# ==========================================
# Builds the debug APK, installs it on the
# connected physical device, and launches it.
# ==========================================

set -e

# Android SDK & JDK paths
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
SDK_DIR="$HOME/Library/Android/sdk"
ADB="$SDK_DIR/platform-tools/adb"
PROJECT_DIR="$(cd "$(dirname "$0")" && pwd)"

# App package name
PACKAGE="com.autolearn.ar"

echo ""
echo "=========================================="
echo "  AutoLearnAR — Deploy to Mobile"
echo "=========================================="
echo ""

# ------------------------------------------
# 1. Find connected physical device
# ------------------------------------------

echo "🔍 Looking for connected device..."

DEVICE=$($ADB devices | grep -v "emulator" | grep "device$" | head -1 | awk '{print $1}')

if [ -z "$DEVICE" ]; then
    echo "❌ No physical device found. Please connect your phone via USB and enable USB debugging."
    exit 1
fi

export ANDROID_SERIAL="$DEVICE"

DEVICE_NAME=$($ADB -s "$DEVICE" shell getprop ro.product.model 2>/dev/null || echo "Unknown")
echo "✅ Device found: $DEVICE_NAME ($DEVICE)"
echo ""

# ------------------------------------------
# 2. Build & Install
# ------------------------------------------

echo "🔨 Building and installing debug APK..."
echo ""

cd "$PROJECT_DIR"
bash gradlew installDebug 2>&1

echo ""
echo "✅ APK installed successfully"
echo ""

# ------------------------------------------
# 3. Launch the app
# ------------------------------------------

echo "🚀 Launching AutoLearnAR..."

$ADB -s "$DEVICE" shell monkey -p "$PACKAGE" -c android.intent.category.LAUNCHER 1 > /dev/null 2>&1

echo "✅ App launched on $DEVICE_NAME"
echo ""
echo "=========================================="
echo "  Done! Check your phone."
echo "=========================================="
echo ""
