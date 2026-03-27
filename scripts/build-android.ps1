#!/usr/bin/env pwsh
# AMLL Android 构建脚本 - 包含完整性验证

$ErrorActionPreference = "Stop"

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  AMLL Frontend Build for Android" -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

# 获取项目根目录
$ProjectRoot = Split-Path -Parent $PSScriptRoot
$FrontendDist = Join-Path $ProjectRoot "frontend/dist"
$AndroidAssets = Join-Path $ProjectRoot "app/src/main/assets/amll"

# 步骤 1: 执行 Vite 构建
Write-Host "[1/4] Building frontend with Vite..." -ForegroundColor Yellow
Set-Location (Join-Path $ProjectRoot "frontend")
& pnpm exec vite build
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Build failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ Build completed`n" -ForegroundColor Green

# 步骤 2: 复制到 Android assets
Write-Host "[2/4] Copying files to Android assets..." -ForegroundColor Yellow
Copy-Item -Path "$FrontendDist/*" -Destination $AndroidAssets -Force
Copy-Item -Path "$ProjectRoot/frontend/index.html" -Destination "$AndroidAssets/index.html" -Force
Write-Host "✓ Files copied`n" -ForegroundColor Green

# 步骤 3: 验证文件完整性
Write-Host "[3/4] Verifying file integrity..." -ForegroundColor Yellow
$RequiredFiles = @("amll.bundle.js", "frontend.css")
$AllMatched = $true

foreach ($File in $RequiredFiles) {
    $SourcePath = Join-Path $FrontendDist $File
    $DestPath = Join-Path $AndroidAssets $File
    
    if (Test-Path $SourcePath -PathType Leaf) {
        $SourceSize = (Get-Item $SourcePath).Length
        $DestSize = (Get-Item $DestPath).Length
        
        if ($SourceSize -eq $DestSize) {
            Write-Host "  ✓ $File ($SourceSize bytes)" -ForegroundColor Green
        } else {
            Write-Host "  ✗ $File MISMATCH! Source: $SourceSize, Dest: $DestSize" -ForegroundColor Red
            $AllMatched = $false
        }
    } else {
        Write-Host "  ✗ $File NOT FOUND in source" -ForegroundColor Red
        $AllMatched = $false
    }
}

# 额外检查 index.html
$IndexHtmlDest = Join-Path $AndroidAssets "index.html"
if (Test-Path $IndexHtmlDest -PathType Leaf) {
    Write-Host "  ✓ index.html (copied)" -ForegroundColor Green
} else {
    Write-Host "  ✗ index.html NOT copied" -ForegroundColor Red
    $AllMatched = $false
}

if (-not $AllMatched) {
    Write-Host "`n✗ File verification failed!" -ForegroundColor Red
    exit 1
}
Write-Host "✓ All files verified`n" -ForegroundColor Green

# 步骤 4: 清理临时测试文件
Write-Host "[4/4] Cleaning up test files..." -ForegroundColor Yellow
$TestFiles = @("check_brackets.py", "check_brackets_smart.py", "last_50_lines.txt")
foreach ($TestFile in $TestFiles) {
    $TestFilePath = Join-Path $AndroidAssets $TestFile
    if (Test-Path $TestFilePath) {
        Remove-Item $TestFilePath -Force
        Write-Host "  Removed: $TestFile" -ForegroundColor Gray
    }
}
Write-Host "✓ Cleanup completed`n" -ForegroundColor Green

# 完成
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "  ✓ Build & Deploy Successful!" -ForegroundColor Green
Write-Host "========================================`n" -ForegroundColor Cyan
Write-Host "Next steps:" -ForegroundColor Yellow
Write-Host "  1. Clean Android project: cd app; ./gradlew clean" -ForegroundColor White
Write-Host "  2. Rebuild in Android Studio" -ForegroundColor White
Write-Host "  3. Run on device/emulator" -ForegroundColor White
Write-Host ""
