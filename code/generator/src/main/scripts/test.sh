#!/bin/bash


[ -z "$LOCAL_CLS" ] && export LOCAL_CLS=$(echo target/lib/*.jar | tr ' ' ':')

JAR_FILE="target/thesis-maven-0.0.1-SNAPSHOT.jar"

CLASSPATH=$LOCAL_CLS:$JAR_FILE

##echo $CLASSPATH
java -cp $CLASSPATH launch.Generator -userIdStart 1 -userIdEnd 1 -chkNumMean 3 -chkNumStDev 3 -chkDurMean 2 -chkDurStDev "0.5" -dist "2000.0" -maxDist "100000.0" -startTime 9 -endTime 18 -startDate "02-05-2015" -endDate "03-05-2015" -outCheckIns "check-ins.csv" -outTraces "gps-traces.csv" -outMaps "daily-maps.csv"
