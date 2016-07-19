#!/bin/bash

rm -rf /tmp/ik/*
mkdir -p /tmp/ik
cp tags /tmp/ik/
cp apps.json /tmp/ik/
touch /tmp/ik/tags-seg
touch /tmp/ik/analyzer-out.txt
touch /tmp/ik/app-tags-out.txt

java -Xmn32m -Xms128m -Xmx256m -jar ik-analyzer.jar /tmp/ik/apps.json /tmp/ik/app-tags-out.txt