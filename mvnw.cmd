@echo off
for %%i in ("%~dp0.") do set MAVEN_PROJECTBASEDIR=%%~fi
set MAVEN_WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
set MAVEN_WRAPPER_JAR=%MAVEN_WRAPPER_DIR%\maven-wrapper.jar
if not exist "%MAVEN_WRAPPER_JAR%" (
  echo Maven wrapper jar not found.
  exit /b 1
)
set JAVA_EXEC=%JAVA_HOME%\bin\java
if not exist "%JAVA_EXEC%" set JAVA_EXEC=java
"%JAVA_EXEC%" -classpath "%MAVEN_WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*
