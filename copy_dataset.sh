#!/bin/bash

for ip in 141 195 250 251 252 253 254 7 8 11 12 13 14 15 16 222 17 18 19 218 20 21 22 24 25 26 27 28 29 30 31 32;
do
	echo scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/ dataset/
	scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/check-ins_* dataset/check-ins/
	scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/gps-traces_* dataset/gps-traces/
	scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/daily-maps_* dataset/daily-maps/
done

