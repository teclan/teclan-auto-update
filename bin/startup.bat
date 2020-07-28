rem 启动对应的程序

rem 复制jar包、配置文件
xcopy ..\tmp\*.jar .\ /y/s/e/i
xcopy ..\tmp\*.json .\ /y/s/e/i
xcopy ..\tmp\*.bat .\ /y/s/e/i
java -jar teclan-auto-update-0.0.1.jar