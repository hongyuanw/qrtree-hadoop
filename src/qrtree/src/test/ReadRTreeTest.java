package test;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import tree.RTree;

import common.Record;
import common.Rectangle;

public class ReadRTreeTest {

	public static void main(String[] args) throws Exception {
		FileSystem fs = FileSystem.get(new Configuration());
		String fileURI = "hdfs://192.168.56.102:9000/user/grid/qrtree/rtrees/(0.0,250.0,250.0,500.0).rt";
		InputStream input = fs.open(new Path(fileURI));
		
		ObjectInputStream ois = new ObjectInputStream(input);
		RTree rtree = (RTree)ois.readObject();
		List<Record> list = rtree.search(new Rectangle(32.68, 347.94, 130.99, 457.9));
		System.out.println(list);
		System.out.println("获得rtree："+rtree.getQuadTreeMbr());
	}

}
