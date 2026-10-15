@REM Wrapper for Windows
@echo off
setlocal enabledelayedexpansion

set MAVEN_HOME=%~dp0\.mvn
set M2_HOME=%MAVEN_HOME%
set PATH=%MAVEN_HOME%\bin;%PATH%

java -cp "%MAVEN_HOME%\lib\*" org.apache.maven.wrapper.MavenWrapperMain %*
