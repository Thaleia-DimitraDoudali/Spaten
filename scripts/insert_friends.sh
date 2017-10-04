#!/bin/bash
export HBASE_CLASSPATH=$(/home/ubuntu/hbase-0.94.27/bin/hbase classpath)
nohup java -cp java -cp $HBASE_CLASSPATH:../thesis-queries-0.0.1-SNAPSHOT.jar hbase_schema.FriendsTable $1 9464 32 > insert_friends.out &

