package test;

import java.util.List;

import tree.RTree;

import common.Record;
import common.Rectangle;

public class RTreeTest {
	public static void main(String[] args) {
		RTree rtree = new RTree();
		rtree.setQuadTreeMbr(new Rectangle(0, 0, 1000, 1000));
		rtree.insert(new Record(new Rectangle(129.96, 84.87, 225.09, 85.04),
				0.0));
		rtree.insert(new Record(new Rectangle(175.75, 33.31, 212.93, 69.36),
				1.0));
		rtree.insert(new Record(new Rectangle(144.23, 227.28, 160.66, 241.01),
				2.0));
		rtree.insert(new Record(new Rectangle(79.57, 100.48, 105.47, 206.54),
				3.0));
		rtree.insert(new Record(new Rectangle(22.52, 142.32, 47.52, 214.51),
				3.0));
		rtree.insert(new Record(new Rectangle(110.42, 221.55, 172.9, 227.89),
				3.0));
		rtree.insert(new Record(new Rectangle(5.41, 145.47, 133.66, 218.26),
				3.0));
		rtree.insert(new Record(new Rectangle(20.76, 56.31, 85.22, 201.26), 3.0));
		rtree.insert(new Record(new Rectangle(125.31, 49.33, 181.53, 205.95),
				4.0));
		rtree.insert(new Record(new Rectangle(15.64, 33.81, 136.58, 115.13),
				5.0));
		rtree.insert(new Record(new Rectangle(38.12, 63.81, 208.05, 181.02),
				6.0));
		rtree.insert(new Record(new Rectangle(73.98, 106.29, 145.52, 242.31),
				7.0));
		rtree.insert(new Record(new Rectangle(36.62, 210.18, 188.08, 213.23),
				8.0));
		rtree.insert(new Record(new Rectangle(114.34, 133.79, 232.96, 136.21),
				9.0));
		rtree.insert(new Record(new Rectangle(38.69, 11.65, 96.48, 209.42),
				10.0));
		rtree.insert(new Record(new Rectangle(152.03, 30.7, 246.1, 31.04), 11.0));
		rtree.insert(new Record(new Rectangle(129.01, 117.47, 243.01, 118.02),
				12.0));
		rtree.insert(new Record(new Rectangle(70.44, 62.56, 249.19, 244.65),
				13.0));
		rtree.insert(new Record(new Rectangle(89.4, 27.67, 217.08, 82.73), 14.0));
		rtree.insert(new Record(new Rectangle(130.61, 155.52, 186.13, 184.96),
				15.0));
		rtree.insert(new Record(new Rectangle(131.44, 27.34, 176.26, 68.26),
				16.0));
		rtree.insert(new Record(new Rectangle(134.99, 32.57, 230.79, 239.0),
				17.0));
		rtree.insert(new Record(new Rectangle(2.6, 147.34, 225.81, 190.77),
				18.0));
		rtree.insert(new Record(new Rectangle(21.7, 8.17, 31.48, 189.76), 19.0));
		rtree.insert(new Record(new Rectangle(73.66, 129.38, 178.64, 184.9),
				20.0));
		rtree.insert(new Record(new Rectangle(90.38, 85.64, 144.35, 127.97),
				21.0));
		rtree.insert(new Record(new Rectangle(69.18, 16.78, 179.82, 59.91),
				22.0));
		rtree.insert(new Record(new Rectangle(54.65, 211.6, 144.5, 222.05),
				23.0));
		rtree.insert(new Record(new Rectangle(27.2, 25.67, 221.35, 216.89),
				24.0));
		rtree.insert(new Record(new Rectangle(69.79, 61.16, 127.95, 157.82),
				25.0));
		rtree.insert(new Record(new Rectangle(88.62, 26.86, 93.76, 223.17),
				26.0));
		rtree.insert(new Record(new Rectangle(109.06, 7.09, 228.44, 96.25),
				27.0));
		rtree.insert(new Record(new Rectangle(32.68, 68.72, 248.12, 239.61),
				28.0));
		rtree.insert(new Record(new Rectangle(245.36, 184.43, 246.3, 239.15),
				29.0));
		rtree.insert(new Record(new Rectangle(40.82, 50.19, 211.97, 97.38),
				30.0));

		// 测试搜索范围能力
		List<Record> list = null;
		Rectangle range = new Rectangle(245.36, 184.43, 246.3, 239.15);
		list = rtree.search(range);

		System.out.println(rtree.getQuadTreeMbr());
		System.out.println("范围" + range + "下的空间目标有：" + list);
	}
}
