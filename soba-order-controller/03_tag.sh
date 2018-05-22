#!/usr/bin/env bash
. taginfo
sudo docker tag kazusato/soba-order-controller:${CONTROLLER_TAG} demoreg.azurecr.io/kazusato/soba-order-controller:${CONTROLLER_TAG}
