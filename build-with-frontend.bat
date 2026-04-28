@echo off
REM ============================================================================
REM Windows 批处理脚本 - 构建前端 + Android Gradle 构建
REM ============================================================================
REM 
REM **功能说明**：
REM 这个批处理脚本用于在 Windows 上一次性完成整个项目的构建：
REM 1. 先构建前端 React/TypeScript 代码
REM 2. 然后执行 Android Gradle 构建
REM 
REM **与 PowerShell 版本的区别**：
REM - build-full.ps1: PowerShell 版本（功能更强大）
REM - build-with-frontend.bat: 批处理版本（兼容性更好）
REM 
REM **使用方法**：
REM ```cmd
REM REM 默认构建
REM build-with-frontend.bat
REM 
REM REM 传递参数给 Gradle
REM build-with-frontend.bat assembleRelease
REM ```
REM ============================================================================
REM ==================== 构建信息输出 ====================
echo ========================================
echo Building Frontend...
echo ========================================

REM ==================== 切换到 frontend 目录 ====================
REM %~dp0: 当前脚本所在目录
cd /d "%~dp0frontend"
REM ==================== 执行前端构建 ====================
REM 调用 pnpm run build:android 命令
call pnpm run build:android

REM 检查错误码：如果失败则退出
if errorlevel 1 (
    echo Frontend build failed!
    exit /b 1
)

REM ==================== 返回项目根目录 ====================
cd ..
REM ==================== 执行 Gradle 构建 ====================
echo ========================================
echo Running Gradle Build...
echo ========================================
REM ==================== 调用 Gradle Wrapper ====================
REM gradlew.bat: Gradle Wrapper 脚本（自动下载和管理 Gradle 版本）
REM %*: 将所有命令行参数传递给 Gradle（如 assembleRelease, --info 等）
gradlew.bat %*
