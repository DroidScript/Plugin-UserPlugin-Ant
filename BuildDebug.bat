
call ant debug
call apkbuilder %CD%/plugin/MyPlugin.jar -v -u -f %CD%/bin/classes.dex
del .\bin\myplugin-debug.apk
del .\bin\myplugin-debug-unaligned.apk
pause