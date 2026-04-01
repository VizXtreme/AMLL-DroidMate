#!/usr/bin/env pwsh
# ============================================================================
# AMLL Android 构建脚本 - 包含完整性验证
# ============================================================================
#
# **功能说明**：
# 这个 PowerShell 脚本负责将前端 React/TypeScript 代码编译并部署到 Android 项目中。
# 它是 DroidMate 项目构建流程的核心部分，确保 AMLL (Apple Music Like Lyrics)
# 歌词渲染引擎能够正确集成到 Android WebView 中使用。
#
# **主要步骤**：
# 1. 使用 Vite 构建前端代码
# 2. 复制构建产物到 Android assets 目录
# 2.5. 将 CSS 内联嵌入到 HTML 中（减少 HTTP 请求）
# 3. 验证文件完整性（防止复制错误）
# 4. 清理临时测试文件
#
# **使用方法**：
# ```powershell
# # 从 frontend 目录运行
# pnpm run build:android
# 
# # 或直接执行
# powershell -ExecutionPolicy Bypass -File scripts/build-android.ps1
# ```
# ============================================================================

# 停止执行模式：遇到错误立即停止
$ErrorActionPreference = "Stop"

# 打印漂亮的标题头
Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  AMLL Frontend Build for Android" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# ==================== 路径配置 ====================
# 获取项目根目录（PSScriptRoot 是当前脚本所在目录）
$ProjectRoot = Split-Path -Parent $PSScriptRoot

# 前端构建产物输出目录
$FrontendDist = Join-Path $ProjectRoot "frontend/dist"

# Android assets 目标目录（最终部署位置）
$AndroidAssets = Join-Path $ProjectRoot "app/src/main/assets/amll"

# ============================================================================
# 步骤 1: 执行 Vite 构建
# ============================================================================
Write-Host "[1/4] Building frontend with Vite..." -ForegroundColor Yellow
Set-Location (Join-Path $ProjectRoot "frontend")

# 调用 pnpm 执行 vite build 命令
& pnpm exec vite build
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build completed`n" -ForegroundColor Green

# ============================================================================
# 步骤 2: 复制到 Android assets
# ============================================================================
Write-Host "[2/4] Copying files to Android assets..." -ForegroundColor Yellow

# 复制所有构建产物（CSS、JS bundle、source map 等）
Copy-Item -Path "$FrontendDist/*" -Destination $AndroidAssets -Force

# 单独复制 index.html（HTML 入口文件）
Copy-Item -Path "$ProjectRoot/frontend/index.html" -Destination "$AndroidAssets/index.html" -Force

Write-Host "✓ Files copied`n" -ForegroundColor Green

# ============================================================================
# 步骤 2.5: 执行 embed-css.js 将 CSS 嵌入到 HTML 中
# ============================================================================
Write-Host "[2.5/4] Embedding CSS into HTML files..." -ForegroundColor Yellow
Set-Location (Join-Path $ProjectRoot "frontend")

# 调用 Node.js 脚本处理 CSS 内联
# 这样可以减少 HTTP 请求，提升 WebView 加载速度
& node "scripts/embed-css.js"
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ CSS embed failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ CSS embedded`n" -ForegroundColor Green

# ============================================================================
# 步骤 3: 验证文件完整性
# ============================================================================
Write-Host "[3/4] Verifying file integrity..." -ForegroundColor Yellow

# 必需的文件列表
$RequiredFiles = @("amll.bundle.js", "frontend.css")
$AllMatched = $true

foreach ($File in $RequiredFiles) {
    $SourcePath = Join-Path $FrontendDist $File
    $DestPath = Join-Path $AndroidAssets $File
    
    if (Test-Path $SourcePath -PathType Leaf) {
        # 获取源文件和目标文件的大小
        $SourceSize = (Get-Item $SourcePath).Length
        $DestSize = (Get-Item $DestPath).Length
        
        if ($SourceSize -eq $DestSize) {
            # ✅ 文件大小一致，复制成功
            Write-Host "  ✓ $File ($SourceSize bytes)" -ForegroundColor Green
        } else {
            # ❌ 文件大小不匹配，可能复制失败
            Write-Host "  ✗ $File MISMATCH! Source: $SourceSize, Dest: $DestSize" -ForegroundColor Red
            $AllMatched = $false
        }
    } else {
        # ❌ 源文件不存在
        Write-Host "  ✗ $File NOT FOUND in source" -ForegroundColor Red
        $AllMatched = $false
    }
}

# 额外检查 index.html 是否存在
$IndexHtmlDest = Join-Path $AndroidAssets "index.html"
if (Test-Path $IndexHtmlDest -PathType Leaf) {
    Write-Host "  ✓ index.html (copied)" -ForegroundColor Green
} else {
    Write-Host "  ✗ index.html NOT copied" -ForegroundColor Red
    $AllMatched = $false
}

# 如果任何文件验证失败，终止构建
if (-not $AllMatched) {
    Write-Host "`n✗ File verification failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ All files verified`n" -ForegroundColor Green

# ============================================================================
# 步骤 4: 清理临时测试文件
# ============================================================================
Write-Host "[4/4] Cleaning up test files..." -ForegroundColor Yellow

# 需要清理的测试文件列表（开发过程中可能遗留的文件）
$TestFiles = @("check_brackets.py", "check_brackets_smart.py", "last_50_lines.txt")
foreach ($TestFile in $TestFiles) {
    $TestFilePath = Join-Path $AndroidAssets $TestFile
    if (Test-Path $TestFilePath) {
        Remove-Item $TestFilePath -Force
        Write-Host "  Removed: $TestFile" -ForegroundColor Gray
    }
}
Write-Host "✓ Cleanup completed`n" -ForegroundColor Green

# ============================================================================
# 完成！显示后续步骤提示
# ============================================================================
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ✓ Build & Deploy Successful!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan

Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Clean Android project: cd app; ./gradlew clean" -ForegroundColor White
Write-Host "  2. Rebuild in Android Studio" -ForegroundColor White
Write-Host "  3. Run on device/emulator" -ForegroundColor White
Write-Host ""
