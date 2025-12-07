@echo off
setlocal
set "SERVER_DIR=C:\Users\allon\MinecraftDev\servers\forge\sao-test"
set "JAVA_EXE=C:\Program Files\Eclipse Adoptium\jdk-17.0.17.10-hotspot\bin\java.exe"
set "ARG_USER=%SERVER_DIR%\user_jvm_args.txt"
set "ARG_WIN=%SERVER_DIR%\libraries\net\minecraftforge\forge\1.20.1-47.3.22\win_args.txt"

echo === SAO server start ===
echo JAVA: %JAVA_EXE%
echo SERVER: %SERVER_DIR%
echo ARG_USER: %ARG_USER%
echo ARG_WIN: %ARG_WIN%

if not exist "%JAVA_EXE%" (
  echo [ERROR] JAVA_EXE not found.
  pause
  exit /b 1
)
if not exist "%SERVER_DIR%" (
  echo [ERROR] SERVER_DIR not found.
  pause
  exit /b 1
)
if not exist "%ARG_USER%" (
  echo [ERROR] user_jvm_args.txt not found.
  pause
  exit /b 1
)
if not exist "%ARG_WIN%" (
  echo [ERROR] win_args.txt not found.
  pause
  exit /b 1
)

pushd "%SERVER_DIR%"
"%JAVA_EXE%" @%ARG_USER% @%ARG_WIN% --nogui
set EXITCODE=%ERRORLEVEL%
popd
echo Java exited with code %EXITCODE%.
pause
exit /b %EXITCODE%
