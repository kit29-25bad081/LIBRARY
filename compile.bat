@echo off
title Compile Digital Library Management System
echo ========================================================
echo Compiling Digital Library Management System
echo ========================================================

REM Check if Maven is in PATH, otherwise use installed path
where mvn >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Users\user\apache-maven-3.9.6\bin\mvn.cmd" (
        set "PATH=C:\Users\user\apache-maven-3.9.6\bin;%PATH%"
    )
)
if "%JAVA_HOME%"=="" (
    if exist "C:\Users\user\jdk-17.0.20.1+1" (
        set "JAVA_HOME=C:\Users\user\jdk-17.0.20.1+1"
    )
)

mvn clean compile package -DskipTests
echo.
echo Compilation completed.
pause
