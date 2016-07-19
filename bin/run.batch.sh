#!/bin/bash

#rm -rf /tmp/ik/*
#mkdir -p /tmp/ik
#cp tags /tmp/ik/
#cp json/*.json /tmp/ik/
touch /tmp/ik/tags-seg
touch /tmp/ik/analyzer-out.txt
touch /tmp/ik/app-tags-out.txt

files=(out.app.sgp-gp-01.json out.app.sgp-gp-02.json out.app.sgp-gp-03.json out.app.sgp-gp-04.json out.app.sgp-gp-05.json)
for file in ${files[@]}; do
    java -Xmn640m -Xms2560m -Xmx5120m -jar ik-analyzer.jar /tmp/ik/$file /tmp/ik/app-tags-out-$file.txt
done
