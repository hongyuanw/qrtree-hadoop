#! /bin/bash
for f in /usr/hadoop/hadoop-*.jar; do
CLASSPATH=$CLASSPATH:$f
done

for f in /usr/hadoop/lib/*.jar; do
CLASSPATH=$CLASSPATH:$f
done

flag=yes
while [ "$flag" != "no" ]; do
	read -p "intput your range rectangle like (1,2,3,4): " range

	#开始计时
	start=$(date +%s%N)
	java -cp /home/sdb/qrtree/jars/qrtree.jar:$CLASSPATH userinterface.Query hdfs://10.196.80.20:9001 hdfs://10.196.80.20:9000 hdfs://10.196.80.20:9000/user/sdb/qrtree/rtrees hdfs://10.196.80.20:9000/user/sdb/qrtree/subspaces $range
	end=$(date +%s%N)
	dif=$[(end-start)/1000000]
	echo "search time: $[dif]ms"
	read -p "continue? say 'yes' or 'no':" flag
	echo $flag
done 
exit 0