#!/bin/sh

mkdir -p target/classes
find ~/ros2_java_ws/build_isolated/ -name '*.so*' -exec cp {} target/classes  \;
