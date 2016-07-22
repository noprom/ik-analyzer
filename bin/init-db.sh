#!/bin/bash

# 初始化数据库
mysql -h localhost -uroot -p < ./apps.sql
echo "init db successfully"