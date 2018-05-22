#!/usr/bin/env bash
. taginfo

filename=01_pod_soba_order_controller.yaml

envsubst \
 < template/${filename}.template \
 > ${filename}

