#!/bin/bash


[ -z "$LOCAL_CLS" ] && export LOCAL_CLS=$(echo lib/*.jar | tr ' ' ':')

JAR_FILE="generator/thesis-maven-0.0.1-SNAPSHOT.jar"

CLASSPATH=$LOCAL_CLS:$JAR_FILE

##echo $CLASSPATH
nohup java -Djava.net.preferIPv6Addresses=true -cp $CLASSPATH launch.Generator -userIdStart $1 -userIdEnd $2 -chkNumMean 5 -chkNumStDev 2 -chkDurMean 2 -chkDurStDev "0.1" -dist "500.0" -maxDist "50000.0" -startTime 9 -endTime 23 -startDate "01-01-2015" -endDate "03-01-2015" -outCheckIns "output/check-ins_$1-$2.csv" -outTraces "output/gps-traces_$1-$2.csv" -outMaps "output/daily-maps_$1-$2.csv" > output/out_$1-$2 &
