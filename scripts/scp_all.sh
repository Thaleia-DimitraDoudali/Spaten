#!/bin/bash

for ip in 141 195 250 251 252 253 254 7 8 11 12 13 14 15 16 222 17 18 19 218 20 21 22 24 25 26 27 28 29 30 31 32 51 52 53 54 55 56 57 58 59 60;
do
	echo scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/ dataset/
	scp -r ubuntu@192.168.5.${ip}:/home/ubuntu/output/ /media/thaleia/7ED0-4D4D/
done


