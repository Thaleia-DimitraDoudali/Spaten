#!/bin/bash

for ip in 141 195 250 251 252 253 254;
do
	echo scp -r generator/ ubuntu@192.168.5.${ip}:/home/ubuntu/generator/
	scp -r generator/ ubuntu@192.168.5.${ip}:/home/ubuntu/
done

