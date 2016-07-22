#!/bin/bash

if [ $# -ne 2 ];
then
    echo "`basename ${0}`:usage: filename_to_parse filter_filename"
	exit 1
fi
filename=$1
filter_file=$2
echo $filename
echo $filter_file
java -Xmn128m -Xms512m -Xmx1024m -jar ik-analyzer.jar $filename $filter_file