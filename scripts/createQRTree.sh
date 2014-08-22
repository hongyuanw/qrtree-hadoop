#! /bin/bash
echo "running..."
echo "create quad tree ..."
start=$(date +%s)
hadoop jar /home/sdb/qrtree/jars/qrtree.jar mapreduce.CreateQuadTree qrtree/data qrtree/subspaces 

echo "create R-tree ..."
hadoop jar /home/sdb/qrtree/jars/qrtree.jar mapreduce.CreateRTree qrtree/subspaces qrtree/rtrees

end=$(date +%s)
dif=$[end-start]
echo "run time: $[dif]s"
