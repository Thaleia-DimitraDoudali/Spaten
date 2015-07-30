# thesis

##Performance evaluation of social networking services using a spatiotemporal and textual Big Data generator

###Generator:
1. Setup PostgreSQL according to the instructions/setup_vm.txt, importing dataset/items.json 
2. Run scripts/run_generator.sh on code/generator/target with the desired input parameters

###Queries:
1. Setup HBase over HDFS according to instructions/setup_hbase.txt 
2. Import dataset/friends.net according to scripts/insert_friends.sh
3. Import dataset/check-ins.out according to scripts/insert_checkIns.sh
4. Import dataset/trces.out according to scripts/insert_gpsTraces.sh
5. Run mixed workload of concurrent queries scripts/chk-query.sh

(dataset folder includes indictive source and generated data files)
