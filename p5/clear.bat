@echo off

set "TARGET_DIR=%cd%"

echo removing .class files under %TARGET_DIR% ...

for /r "%TARGET_DIR%" %%f in (*.class) do (
    echo del %%f
    del "%%f"
)

echo done clearup
pause
