@ECHO off
SET /p runOpt="Run REPL [r] or a script file [insert filepath]: "

call mvn initialize
call mvn compile
call cls

IF %runOpt%==r (
    java -cp "target\classes;lib/*" com.taylorscript.main.TaylorScript
) ELSE (
    java -cp "target\classes;lib/*" com.taylorscript.main.TaylorScript %runOpt%
    pause >nul
)
