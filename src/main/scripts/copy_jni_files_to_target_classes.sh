#!/bin/sh

mkdir -p target/classes/
find ~/ros2_java_ws/build_isolated/ -name 'lib*.so*' -exec cp {} target/classes/  \;
