@echo off
cd /d "%~dp0\.."

echo ==========================================
echo Vehicle Service Management System
echo ==========================================

set "JAVA_EXE=C:\Users\Indian\AppData\Local\Programs\Eclipse Adoptium\jdk-21.0.12.101-hotspot\bin\java.exe"

if not exist "backend\bin\com\vehicle\Main.class" (
    echo ERROR: Main.class not found. Run compile.bat first.
    pause
    exit /b 1
)

"%JAVA_EXE%" -cp "backend\bin;backend\lib/*" com.vehicle.Main

pause