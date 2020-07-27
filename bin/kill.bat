rem 关闭名称为 teclan-auto-update 的程序
@echo off
set name=teclan-auto-update
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