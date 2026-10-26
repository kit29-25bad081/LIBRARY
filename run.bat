@echo off
title Digital Library Management System
echo ========================================================
echo Starting Digital Library Management System (Java Swing)
echo ========================================================

REM Check if java is in PATH, otherwise try Temurin JDK path
where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    if exist "C:\Users\user\jdk-17.0.20.1+1\bin\java.exe" (
        set "PATH=C:\Users\user\jdk-17.0.20.1+1\bin;%PATH%"
    )
)

REM Run using jar if built, otherwise from target/classes with classpath
if exist "target\digital-library-management-system-1.0.0.jar" (
    java -jar "target\digital-library-management-system-1.0.0.jar"
) else (
    java -cp "target\classes;lib\*;target\lib\*" com.digitallibrary.Main
)

pause
