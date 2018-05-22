#!/usr/bin/env bash
. taginfo
cp ../build/libs/jjug-lt-spring2018-all.jar .
sudo docker build -t kazusato/soba-order-controller:${CONTROLLER_TAG} --force-rm .
