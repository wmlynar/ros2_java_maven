#!/bin/sh

mkdir -p target/classes
find ../../../build_isolated/ -name '*.so*' -exec cp {} target/classes  \;
