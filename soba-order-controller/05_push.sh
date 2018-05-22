#!/usr/bin/env bash
. taginfo
sudo docker push demoreg.azurecr.io/kazusato/soba-order-controller:${CONTROLLER_TAG}
