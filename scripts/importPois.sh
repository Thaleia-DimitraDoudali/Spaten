#!/bin/bash


[ -z "$LOCAL_CLS" ] && export LOCAL_CLS=$(echo lib/*.jar | tr ' ' ':')

JAR_FILE="poisToDB/thesis-maven-0.0.1-SNAPSHOT.jar"

CLASSPATH=$LOCAL_CLS:$JAR_FILE

##echo $CLASSPATH
java -cp $CLASSPATH db.PoisToDB $1

