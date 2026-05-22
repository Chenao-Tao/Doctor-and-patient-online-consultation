param(
  [string]$InstallDir,
  [string]$Version = "1.12.0"
)

$ErrorActionPreference = "Stop"
$root = Resolve-Path (Join-Path $PSScriptRoot "..")
if ([string]::IsNullOrWhiteSpace($InstallDir)) {
  $InstallDir = Join-Path $root "tools\livekit"
}

New-Item -ItemType Directory -Force -Path $InstallDir | Out-Null

$assetName = $null
$assetUrl = $null

try {
  $release = Invoke-RestMethod -Uri "https://api.github.com/repos/livekit/livekit/releases/latest" -Headers @{
    "User-Agent" = "ruoyi-consultation-livekit-installer"
  }

  $asset = $release.assets |
    Where-Object { $_.name -match "windows_amd64\.zip$" } |
    Select-Object -First 1

  if ($asset) {
    $assetName = $asset.name
    $assetUrl = $asset.browser_download_url
  }
} catch {
  Write-Warning "GitHub release API is unavailable, falling back to v$Version direct download."
}

if (-not $assetUrl) {
  $assetName = "livekit_$($Version)_windows_amd64.zip"
  $assetUrl = "https://github.com/livekit/livekit/releases/download/v$Version/$assetName"
}

$zipPath = Join-Path $InstallDir $assetName
Write-Host "Downloading $assetName..."
Invoke-WebRequest -Uri $assetUrl -OutFile $zipPath

$extractDir = Join-Path $InstallDir "extract"
if (Test-Path $extractDir) {
  Remove-Item -Recurse -Force $extractDir
}
New-Item -ItemType Directory -Force -Path $extractDir | Out-Null
Expand-Archive -Force -Path $zipPath -DestinationPath $extractDir

$exe = Get-ChildItem -Path $extractDir -Recurse -Filter "livekit-server.exe" | Select-Object -First 1
if ($null -eq $exe) {
  throw "Downloaded archive does not contain livekit-server.exe."
}

$target = Join-Path $InstallDir "livekit-server.exe"
Copy-Item -Force -Path $exe.FullName -Destination $target
Remove-Item -Recurse -Force $extractDir

Write-Host "LiveKit Server installed: $target"
Write-Host "Run: scripts\start-livekit.ps1"
