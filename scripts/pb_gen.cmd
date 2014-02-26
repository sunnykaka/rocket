@echo off

rem 运行之前请将protoc.exe所在路径加入PATH

set BASEDIR=G:/opensource/rocket
set SRC_PATH=%BASEDIR%/src/main/java/com/rpg
set OUT_PATH=%BASEDIR%/src/main/java

protoc --proto_path=%SRC_PATH% --java_out=%OUT_PATH% %SRC_PATH%/rocket/domain/pb/*.proto
rem %SRC_PATH%/rocket/message/pb/*.proto