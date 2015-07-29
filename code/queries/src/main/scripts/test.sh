#!/bin/bash


[ -z "$HBASE_CLS" ] && export HBASE_CLS=$(hbase classpath)

JAR_FILE="target/thesis-queries-0.0.1-SNAPSHOT.jar"


CLASSPATH=$HBASE_CLS:$JAR_FILE

##echo $CLASSPATH
java -cp $CLASSPATH $1 $2
