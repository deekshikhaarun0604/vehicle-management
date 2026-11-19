@echo off
echo Running Automated Test Suite...
java -cp "vehicle-system.jar;bin;lib/*" com.vehicle.test.TestSuite
pause
