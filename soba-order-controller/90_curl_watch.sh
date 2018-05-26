#!/usr/bin/env bash

if [ $# -lt 1 ]
then
  echo usage: $0 from_resurce_version
  exit 1
fi

from=$1
  curl "http://localhost:8001/apis/kazusato.local/v1alpha1/sobaorders?watch=1&resourceVersion=${from}"
