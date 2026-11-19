@echo off
echo === 1. JAVA VERSION ===
java -version 2>&1
echo === 2. JAVAC VERSION ===
javac -version 2>&1
echo === 3. PYTHON VERSION ===
python --version 2>&1
echo === 4. MYSQL CLI ===
where mysql 2>&1
echo === 5. PORT 3306 CHECK ===
powershell -Command "$c = Test-NetConnection -ComputerName localhost -Port 3306 -WarningAction SilentlyContinue; if ($c.TcpTestSucceeded) { Write-Host 'Port 3306 is OPEN' } else { Write-Host 'Port 3306 is CLOSED' }"
echo === 6. JDBC DRIVER CHECK ===
if exist backend\lib\mysql-connector-j*.jar (
    echo MySQL JDBC driver found in backend\lib
) else (
    echo MySQL JDBC driver not found in backend\lib
)
