::K:
::cd K:\workspace\qrtree\bin
::jar -cvf qrtree.jar *
::pscp -pw tiger qrtree.jar grid@h1:/home/grid/qrtree/jars/
D:
pscp -pw sdb3309 -r qrtree sdb@10.196.80.20:/home/sdb/

