@echo off

set "CLASSPATH=%cd%\hamcrest-2.2.jar;%cd%\junit-4.13.2.jar;%cd%\gson-2.11.0.jar;."
javac -classpath "%CLASSPATH%" *.java
