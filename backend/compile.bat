
@echo off
setlocal

echo ============================================================
echo   Compiling Vehicle Service Management System
echo ============================================================

REM Always execute from the backend directory
cd /d "%~dp0"

if not exist bin mkdir bin

REM Generate Java source file list
dir /s /b src\*.java > sources.txt

if not exist sources.txt (
    echo [ERROR] Source list could not be created.
    exit /b 1
)

REM Compile all Java files
javac -d bin @sources.txt

if errorlevel 1 (
    echo [ERROR] Compilation failed.
    del sources.txt
    exit /b 1
)

echo [SUCCESS] Java compilation completed.

REM Create executable JAR
jar -cfe vehicle-system.jar com.vehicle.Main -C bin .

if errorlevel 1 (
    echo [ERROR] JAR creation failed.
    del sources.txt
    exit /b 1
)

del sources.txt

echo [SUCCESS] vehicle-system.jar created.
echo ============================================================

endlocal
