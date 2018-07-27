#!/bin/bash

ps -A -o %cpu | awk '{cpuload+=$1} END {print cpuload}'