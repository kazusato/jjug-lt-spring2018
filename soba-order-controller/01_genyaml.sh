#!/usr/bin/env bash
. taginfo

filenames="01_pod_soba_order_controller.yaml 10_deploy_soba_order_controller.yaml"

for i in $filenames
do
    envsubst \
     < template/${i}.template \
     > ${i}
done
