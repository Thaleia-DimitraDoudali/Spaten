#!/bin/bash


[ -z "$HBASE_CLS" ] && export HBASE_CLS=$(hbase classpath)
[ -z "$LOCAL_CLS" ] && export LOCAL_CLS=$(echo target/lib/*.jar | tr ' ' ':')

JAR_FILE="target/thesis-maven-0.0.1-SNAPSHOT.jar"


CLASSPATH=$HBASE_CLS:$LOCAL_CLS:$JAR_FILE

##echo $CLASSPATH
java -cp $CLASSPATH $1 $2 $3 $4 $5
