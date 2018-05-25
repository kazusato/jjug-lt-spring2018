#!/usr/bin/env bash
  for i in `ls -1 testdata/*sobaorder*`; do kubectl apply -f $i; done
