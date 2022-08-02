#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters. Run as $0 <application namespace>"
    exit 1
fi

appNS=$1
echo "Configuring demo infra in NS ${appNS}"
oc process -p=APP_NAMESPACE=${appNS} -f config/app | oc apply -f -

oc get all -n ${appNS} -l app=demo,layer=app