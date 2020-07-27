@echo off
set name=teclan-auto-update-0.0.1.jar
echo char : %char%
for /f "usebackq tokens=1-2" %%a in (`jps -l ^| findstr %name%`) do (
		echo find process %%a %%b
		set pid=%%a
		set image_name=%%b
)
echo now will kill process : pid %pid%, image_name %image_name%
rem pause
rem 根据进程ID，kill进程
taskkill /f /pid %pid%