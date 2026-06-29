@echo off
set JAVA_HOME=C:\Program Files\Java\jdk-21.0.11
echo 🚀 Starting Nestify Build and Install...
echo 📱 Checking connected devices...
adb devices
echo.
echo 🏗️ Building APK offline and installing (Fast Iteration)...
call gradlew.bat installDebug --offline
if %ERRORLEVEL% EQU 0 (
    echo.
    echo ✅ Build Successful! Launching app...
    adb shell am start -n com.nhbhuiyan.nestify/.MainActivity
) else (
    echo.
    echo ⚠️ Offline build failed or new dependencies found. Trying online build...
    call gradlew.bat installDebug
    if %ERRORLEVEL% EQU 0 (
        echo.
        echo ✅ Online Build Successful! Launching app...
        adb shell am start -n com.nhbhuiyan.nestify/.MainActivity
    ) else (
        echo.
        echo ❌ Build Failed. Please check the errors above.
    )
)
pause
