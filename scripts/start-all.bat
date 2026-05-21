@echo off
setlocal

cd /d "%~dp0.." >nul 2>&1
set "ROOT=%CD%"

rem Try to start services (requires admin)
net start MySQL80 >nul 2>&1
net start rediszt3 >nul 2>&1

rem Resolve Java executable
set "JAVA_EXE=java"
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
if exist "C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe" set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.19.10-hotspot\bin\java.exe"

set "BACKEND_JAR=%ROOT%\ruoyi-backend\ruoyi-admin\target\ruoyi-admin.jar"
if not exist "%BACKEND_JAR%" (
  echo Backend jar not found: %BACKEND_JAR%
  echo Run: mvn -DskipTests -pl ruoyi-admin -am package
  goto :EOF
)

echo ROOT=%ROOT%
echo JAVA_EXE=%JAVA_EXE%
echo BACKEND_JAR=%BACKEND_JAR%

start "ruoyi-backend" cmd /k ""%JAVA_EXE%" -jar "%BACKEND_JAR%""
start "ruoyi-frontend" cmd /k cd /d "%ROOT%\ruoyi-ui" ^&^& if not exist node_modules call npm install ^&^& call npm run dev

echo Backend: http://localhost:8081
echo Frontend: http://localhost:80

endlocal
