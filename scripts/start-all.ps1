param(
  [switch]$Rebuild
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