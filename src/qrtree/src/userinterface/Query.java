package userinterface;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

import tree.RTree;
import common.Record;
import common.Rectangle;

public class Query {

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		conf.set("mapred.jop.tracker", args[0]);
		conf.set("fs.default.name", args[1]);
		FileSystem fs = FileSystem.get(conf);
		// String goAhead = "yes";

		Rectangle range = Rectangle.getRectFromString(args[4]);
		String fileRtree = args[2] + "/";

		// 先查询四叉树范围
		String basePath = args[3];
		while (true) {
			FileStatus[] listStatus = fs.globStatus(new Path(basePath + "/(*"));
			Rectangle bestRect = null; // 最佳匹配矩形
			FileStatus bestFile = null; // 最佳匹配矩形对应的文件
			for (FileStatus fileStatus : listStatus) {
				String fileName = fileStatus.getPath().getName();
				fileName = fileName.substring(0, fileName.indexOf(")") + 1);
				Rectangle rectTmp = Rectangle.getRectFromString(fileName);
				if (rectTmp.contain(range)) {
					if (bestRect == null) {
						bestRect = rectTmp;
						bestFile = fileStatus;
					} else if (bestRect.contain(rectTmp)) {
						bestRect = rectTmp; // 将范围更小的矩形替换顶层矩形
						bestFile = fileStatus;
					}
				}
			}
			if (bestRect == null) {
				System.out.println("input error!");
				break;
			}

			// 判断当前选择的文件还是目录，目录的情况下，需要继续进入子空间
			if (bestFile.isDir()) {
				basePath = bestFile.getPath().toString();
			} else {
				String fileName = bestFile.getPath().getName();
				fileName = fileName.substring(0, fileName.indexOf(")") + 1);
				fileRtree += fileName + ".rt";
				break;
			}
		}

		// 先载入R树，之后在R树中查询
		InputStream input = fs.open(new Path(fileRtree));
		ObjectInputStream ois = new ObjectInputStream(input);
		RTree rtree = (RTree) ois.readObject();
		List<Record> list = rtree.search(range);
		System.out.println("search result:");
		for (Record record : list) {
			System.out.println(record.getMbr() + "-" + record.getObj());
		}

	}

}
