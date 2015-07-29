#!/bin/bash

##Usage: <input file> <userNo> <regionsNo>

export HBASE_CLASSPATH=$(/root/hbase-0.94.27/bin/hbase classpath)
nohup java -cp java -cp $HBASE_CLASSPATH hbase_schema.CheckInsTable $1 9464 32 > insert_checkIns.out &
