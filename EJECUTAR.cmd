@echo off
cd /d "%~dp0"
if not defined JAVA_HOME for /d %%J in ("%ProgramFiles%\Java\jdk-*") do if exist "%%~fJ\bin\java.exe" set "JAVA_HOME=%%~fJ"
echo Iniciando API - Biblioteca...
echo Deja esta ventana abierta mientras uses Postman.
call mvnw.cmd spring-boot:run
pause

