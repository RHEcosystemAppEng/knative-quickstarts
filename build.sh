#!/bin/bash
if [ "$#" -ne 1 ]; then
    echo "Illegal number of parameters. Run as $0 <image namespace>"
    exit 1
fi

imageNS=$1
echo "Building in NS ${imageNS}"
oc process -p=IMAGE_NAMESPACE=${imageNS} -f config/build | oc apply -f -

oc get build --watch &
PID=$!

while :
do
    sleep 1
    pending=$(oc get build --no-headers| grep -vc Complete)
    echo "Still ${pending} builds"
    if [ $pending -eq 0 ]
    then
        break
    fi
done

echo "Killing $PID"
kill $PID