#!/bin/bash

nohup java -cp thesis-maven-0.0.1-SNAPSHOT.jar friends.AlterGraph $1 $2 > gen_graph.out &
