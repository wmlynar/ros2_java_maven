#!/bin/sh

mkdir -p target/classes/natives/linux_64/
find ~/ros2_java_ws/build_isolated/ -name '*.so*' -exec cp {} target/classes/natives/linux_64/  \;
