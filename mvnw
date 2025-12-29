#!/bin/sh
# Maven Wrapper
BASEDIR=$(dirname "$0")
MAVEN_PROJECTBASEDIR="$(cd "$BASEDIR" && pwd)"
MAVEN_WRAPPER_DIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
JAVA_EXEC="${JAVA_HOME:-}/bin/java"
if [ ! -x "$JAVA_EXEC" ]; then
  JAVA_EXEC=java
fi
exec "$JAVA_EXEC" -classpath "$MAVEN_WRAPPER_DIR/maven-wrapper.jar" -Dmaven.multiModuleProjectDirectory="$MAVEN_PROJECTBASEDIR" org.apache.maven.wrapper.MavenWrapperMain "$@"
