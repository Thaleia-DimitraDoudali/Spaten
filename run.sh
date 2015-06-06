#!/bin/bash

start=63
step=12
end=$((start+step))

#--Runner 4--
ssh ubuntu@192.168.5.251 << EOF
./generator/test.sh $start $end
EOF

#--Runner 5--
start=$((end+1))
end=$((start+step))
ssh ubuntu@192.168.5.252 << EOF
./generator/test.sh $start $end
EOF
