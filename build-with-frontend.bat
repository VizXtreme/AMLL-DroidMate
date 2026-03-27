@echo off
REM Build frontend and then Gradle build
echo ========================================
echo Building Frontend...
echo ========================================

cd /d "%~dp0frontend"
call pnpm run build:android
if errorlevel 1 (
    echo Frontend build failed!
    exit /b 1
)

cd ..
echo ========================================
echo Running Gradle Build...
echo ========================================
gradlew.bat %*
