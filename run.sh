#!/bin/bash

start=8835
step=14
end=$((start+step))

#--Runner 1--
ssh ubuntu@192.168.5.141 << EOF
./generator/test.sh $start $end
echo ./generator/test.sh $start $end
EOF

for ip in 195 250 251 252 253 254 7 8 11 12 13 14 15 16 222 17 18 19 218 20 21 22 24 25 26 27 28 29 30 31 32 51 52 53 54 55 56 57 58 59 60;
do
	start=$((end+1))
	end=$((start+step))
	sleep 5
	echo ./generator/test.sh $start $end 192.168.5.${ip}
	ssh ubuntu@192.168.5.${ip} << EOF
	./generator/test.sh $start $end 
EOF
done

