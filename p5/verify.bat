@echo off

set "CLASSPATH=%cd%\gson-2.11.0.jar;."
java -classpath "%CLASSPATH%" Verify %*
