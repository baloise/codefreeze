set JAVA_HOME=C:\Users\Public\dev\java-1.8.0-openjdk-1.8.0.232-1.b09.redhat.windows.x86_64
set MAVEN_HOME=C:\Program Files\JetBrains\IntelliJ IDEA 2020.1.2\plugins\maven\lib\maven3

set PATH=%MAVEN_HOME%\bin;%JAVA_HOME%\bin;%PATH%

cd ..
mvn -B release:clean release:prepare
