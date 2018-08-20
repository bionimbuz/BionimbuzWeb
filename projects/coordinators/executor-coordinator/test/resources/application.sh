#!/bin/bash

cat $1 > $3
cat $2 >> $3

echo "Execution time: `date`" >> $3

echo "Extra file execution time: `date`" > $4

