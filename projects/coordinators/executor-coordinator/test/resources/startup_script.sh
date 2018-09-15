#!/bin/bash

SPACE=$1

COORDINATOR=executor-coordinator-0.1.jar
curl -o ${COORDINATOR} http://localhost:8282/spaces/${SPACE}/file/${COORDINATOR}/download
java -jar ${COORDINATOR} &

PID=$!
trap "kill -9 ${PID} && exit " SIGHUP SIGINT SIGTERM

while :
do
    sleep 1
done
