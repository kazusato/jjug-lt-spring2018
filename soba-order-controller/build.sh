#!/usr/bin/env bash
cp ../build/libs/jjug-lt-spring2018-all.jar .
docker build -t kazusato/soba-order-controller:0.1.4 --force-rm .
