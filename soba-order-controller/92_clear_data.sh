#!/usr/bin/env bash
for i in `kubectl get sobaorders | grep -v ^NAME | awk '{print $1}'`; do kubectl delete sobaorder $i; done
