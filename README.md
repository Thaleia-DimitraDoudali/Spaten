# Spaten

##A Spatio-temporal and Textual Big Data generator

###Data:
http://research.cslab.ece.ntua.gr/datasets/ikons/Spaten/

###How to set up and run Spaten:
1. Setup PostgreSQL according to the instructions/setup_vm.txt, importing Spaten's Source-Dataset
2. Run scripts/run_generator.sh on code/generator/target with the desired input parameters

###Run queries on the generated data:
1. Setup HBase over HDFS according to instructions/setup_hbase.txt 
2. Import Spaten-Dataset according to scripts/inset\_\*.sh 
5. Run mixed workload of concurrent queries scripts/chk-query.sh

