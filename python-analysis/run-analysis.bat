@echo off
echo =====================================================================
echo  Running Vehicle Service Data Analysis & Visualization (Python)
echo =====================================================================

python "%~dp0analysis.py"

echo.
echo Process complete. High-resolution figures generated in:
echo %~dp0output\
pause
