#!/usr/bin/env pwsh
# ============================================================================
# 完整构建脚本 - 前端 + Android Gradle 构建
# ============================================================================
#
# **功能说明**：
# 这个脚本用于一次性完成整个项目的构建：
# 1. 先构建前端 React/TypeScript 代码
# 2. 然后执行 Android Gradle 构建
#
# **使用场景**：
# - CI/CD 自动化构建
# - 发布前的完整构建
# - 确保前后端同步更新
#
# **使用方法**：
# ```powershell
# # 默认构建（无参数）
# .\scripts\build-full.ps1
# 
# # 传递 Gradle 参数（如 assembleRelease）
# .\scripts\build-full.ps1 assembleRelease
# ```
# ============================================================================

# 接收所有传递给脚本的参数并传给 Gradle
param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs  # Gradle 构建参数（如 assembleRelease, assembleDebug 等）
)

# 错误处理模式：遇到错误立即停止
$ErrorActionPreference = "Stop"

# ==================== 路径配置 ====================
# 获取当前脚本所在目录
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
# 获取项目根目录（脚本目录的父目录）
$ProjectRoot = Split-Path -Parent $ScriptDir

# ============================================================================
# 第一阶段：构建前端
# ============================================================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Building Frontend..." -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 切换到 frontend 目录
Set-Location (Join-Path $ProjectRoot "frontend")

# 执行前端构建命令（调用 build-android.ps1）
& pnpm run build:android
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Frontend build failed!" -ForegroundColor Red
    exit 1
}

# ============================================================================
# 第二阶段：执行 Gradle 构建
# ============================================================================
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Running Gradle Build..." -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 切换回项目根目录
Set-Location $ProjectRoot

# 执行 Gradle 构建，传递所有参数
# @GradleArgs 展开参数数组（如 assembleRelease, --info 等）
& ".\gradlew.bat" @GradleArgs
