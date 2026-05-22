param(
  [switch]$Rebuild,
  [switch]$SkipLiveKit
)

$root = (Resolve-Path (Join-Path $PSScriptRoot ".."))

function Start-ServiceIfExists {
  param([string]$Name)
  $svc = Get-Service -Name $Name -ErrorAction SilentlyContinue
  if ($null -ne $svc) {
    if ($svc.Status -ne "Running") {
      try {
        Start-Service -Name $Name -ErrorAction Stop
        Write-Host "Started service: $Name"
      } catch {
        Write-Warning "Failed to start service: $Name. Run PowerShell as Administrator if needed."
      }
    }
  }
}

Start-ServiceIfExists -Name "MySQL80"
Start-ServiceIfExists -Name "rediszt3"

$env:LIVEKIT_API_KEY = if ($env:LIVEKIT_API_KEY) { $env:LIVEKIT_API_KEY } else { "devkey" }
$env:LIVEKIT_API_SECRET = if ($env:LIVEKIT_API_SECRET) { $env:LIVEKIT_API_SECRET } else { "secret" }
$env:LIVEKIT_WS_URL = if ($env:LIVEKIT_WS_URL) { $env:LIVEKIT_WS_URL } else { "ws://127.0.0.1:7880" }

if (-not $SkipLiveKit) {
  $livekitScript = Join-Path $PSScriptRoot "start-livekit.ps1"
  & $livekitScript
  if ($LASTEXITCODE -ne 0) {
    Write-Warning "LiveKit was not started. Install it with scripts\install-livekit.ps1, or pass -SkipLiveKit to use an external LiveKit service."
  }
}

$backendJar = Join-Path $root "ruoyi-backend\ruoyi-admin\target\ruoyi-admin.jar"
if ($Rebuild -or !(Test-Path $backendJar)) {
  Write-Host "Building backend..."
  Push-Location (Join-Path $root "ruoyi-backend")
  & mvn -DskipTests -pl ruoyi-admin -am package
  if ($LASTEXITCODE -ne 0) {
    Pop-Location
    throw "Maven build failed."
  }
  Pop-Location
}

$java = $null
$javaHomeExe = if ($env:JAVA_HOME) { Join-Path $env:JAVA_HOME "bin\java.exe" } else { $null }
if ($javaHomeExe -and (Test-Path $javaHomeExe)) {
  $java = $javaHomeExe
} elseif (Test-Path "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe") {
  $java = "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"
} else {
  $java = "java"
}

$frontendDir = Join-Path $root "ruoyi-ui"

Start-Process -FilePath $java -WorkingDirectory $root -ArgumentList "-jar", $backendJar
Start-Process -FilePath "powershell" -WorkingDirectory $frontendDir -ArgumentList "-NoExit", "-Command", "if (!(Test-Path node_modules)) { npm install }; npm run dev"

Write-Host "Backend: http://localhost:8081"
Write-Host "Frontend: http://localhost:80"
Write-Host "LiveKit: $($env:LIVEKIT_WS_URL)"
