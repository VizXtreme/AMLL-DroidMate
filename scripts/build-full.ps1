#!/usr/bin/env pwsh
# Build frontend and then run Gradle build

param(
    [Parameter(ValueFromRemainingArguments = $true)]
    [string[]]$GradleArgs
)

$ErrorActionPreference = "Stop"
$ScriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$ProjectRoot = Split-Path -Parent $ScriptDir

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Building Frontend..." -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Set-Location (Join-Path $ProjectRoot "frontend")
& pnpm run build:android
if ($LASTEXITCODE -ne 0) {
    Write-Host "✗ Frontend build failed!" -ForegroundColor Red
    exit 1
}

Write-Host "`n========================================" -ForegroundColor Cyan
Write-Host "  Running Gradle Build..." -ForegroundColor Cyan
Write-Host "========================================`n" -ForegroundColor Cyan

Set-Location $ProjectRoot
& ".\gradlew.bat" @GradleArgs
