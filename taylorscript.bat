@ECHO off
SET /p runOpt="Run a script file [insert filepath]: "

call cls

IF %runOpt%==r (
    java -cp "target\classes" com.taylorscript.main.TaylorScript
) ELSE (
    java -cp "target\classes" com.taylorscript.main.TaylorScript %runOpt%
    pause >nul
)
