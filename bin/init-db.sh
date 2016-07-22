#!/bin/bash

# 复制配置文件
mkdir -p /tmp/ik/
cp sysconfig.properties /tmp/ik/
# 初始化数据库
mysql -h localhost -uroot -p < ./apps.sql
echo "init db successfully"