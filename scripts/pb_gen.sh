#!/bin/sh

# 运行之前请将protoc所在路径加入PATH

BASEDIR=~/devel/github/rocket
SRC_PATH=$BASEDIR/src/main/java/com/rpg
OUT_PATH=$BASEDIR/src/main/java

protoc --proto_path=$SRC_PATH --java_out=$OUT_PATH $SRC_PATH/rocket/domain/pb/*.proto $SRC_PATH/rocket/message/pb/*.proto