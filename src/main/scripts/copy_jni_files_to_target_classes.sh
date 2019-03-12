#!/bin/sh

CODENAME=`lsb_release -cs`

mkdir -p target/classes/$CODENAME
find ~/ros2_java_ws/build_isolated/ -name 'lib*.so*' -exec cp {} target/classes/$CODENAME  \;
