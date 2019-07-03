#!/bin/bash

SPACE=$1

EXECUTOR=task-executor-0.1.jar
curl -o ${EXECUTOR} http://localhost:8282/spaces/${SPACE}/file/${EXECUTOR}/download
java -jar ${EXECUTOR} &

PID=$!
trap "kill -9 ${PID} && exit " SIGHUP SIGINT SIGTERM

while :
do
    sleep 1
done
