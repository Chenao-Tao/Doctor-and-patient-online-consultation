param(
  [switch]$NoWindow
)

$ErrorActionPreference = "Stop"
$root = Resolve-Path (Join-Path $PSScriptRoot "..")

$candidates = @()
if ($env:LIVEKIT_SERVER_EXE) {
  $candidates += $env:LIVEKIT_SERVER_EXE
}
$candidates += Join-Path $root "tools\livekit\livekit-server.exe"

$pathCommand = Get-Command livekit-server -ErrorAction SilentlyContinue
if ($pathCommand) {
  $candidates += $pathCommand.Source
}

$serverExe = $candidates | Where-Object { $_ -and (Test-Path $_) } | Select-Object -First 1
if (-not $serverExe) {
  Write-Warning "LiveKit Server was not found."
  Write-Host "Install it with: powershell -ExecutionPolicy Bypass -File scripts\install-livekit.ps1"
  exit 1
}

$env:LIVEKIT_API_KEY = if ($env:LIVEKIT_API_KEY) { $env:LIVEKIT_API_KEY } else { "devkey" }
$env:LIVEKIT_API_SECRET = if ($env:LIVEKIT_API_SECRET) { $env:LIVEKIT_API_SECRET } else { "secret" }
$env:LIVEKIT_WS_URL = if ($env:LIVEKIT_WS_URL) { $env:LIVEKIT_WS_URL } else { "ws://127.0.0.1:7880" }

$logDir = Join-Path $root "logs"
New-Item -ItemType Directory -Force -Path $logDir | Out-Null
$stdoutLog = Join-Path $logDir "livekit.out.log"
$stderrLog = Join-Path $logDir "livekit.err.log"

if ($NoWindow) {
  Start-Process -FilePath $serverExe `
    -ArgumentList "--dev" `
    -WorkingDirectory $root `
    -WindowStyle Hidden `
    -RedirectStandardOutput $stdoutLog `
    -RedirectStandardError $stderrLog
} else {
  Start-Process -FilePath "powershell" -WorkingDirectory $root -ArgumentList @(
    "-NoExit",
    "-Command",
    "& '$serverExe' --dev"
  )
}

Write-Host "LiveKit Server: $($env:LIVEKIT_WS_URL)"
Write-Host "LiveKit API Key: $($env:LIVEKIT_API_KEY)"
Write-Host "LiveKit logs: $stdoutLog"
