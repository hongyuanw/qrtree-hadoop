package tree;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import common.Item;
import common.Record;
import common.Rectangle;

public class RTree implements Serializable {

	private static final long serialVersionUID = 1L;

	private Node root = new LeafNode();// 开始的根节点同时为叶子节点
	private Rectangle quadTreeMbr; // R树的所有数据集在一个四叉树节点下，需要指明这个mbr

	public Rectangle getQuadTreeMbr() {
		return quadTreeMbr;
	}

	public void setQuadTreeMbr(Rectangle quadTreeMbr) {
		this.quadTreeMbr = quadTreeMbr;
	}

	/**
	 * 向R树中插入一条空间数据记录,分为两个过程，而这两个过程都是递归的 1、查询出记录应该插入的叶子节点的地方，可能改叶子节点已经满了
	 * 2、向这个叶子节点中插入记录，如果满了则分裂
	 * 
	 * @param record
	 */
	public void insert(Record record) {
		LeafNode leaf = chooseLeaf(root, record.getMbr());
		insertToLeaf(leaf, record);
	}

	/**
	 * 依据"最小面积覆盖原则"，挑选最合适的叶子节点
	 */
	private LeafNode chooseLeaf(Node start, Rectangle mbr) {
		if (start.isLeaf()) {
			return (LeafNode) start;
		}

		// 按照插入后面积增量最小的原则挑选矩形，增量相同挑选面积较小的矩形，面积也相同的话就不用重新选择
		int bestIndex = 0; // 最佳选择子空间的索引
		double areaCursubSpace = start.getMbrArray()[bestIndex].getArea();
		double areaInc = Rectangle.getNewMbr(start.getMbrArray()[bestIndex],
				mbr).getArea()
				- areaCursubSpace;

		for (int i = 1; i < start.getNum(); i++) {
			double areaCursubSpaceTmp = start.getMbrArray()[i].getArea();
			double areaIncTmp = Rectangle
					.getNewMbr(start.getMbrArray()[i], mbr).getArea()
					- areaCursubSpaceTmp;
			if (areaIncTmp < areaInc
					|| (areaIncTmp == areaInc && areaCursubSpace > areaCursubSpaceTmp)) {
				bestIndex = i;
				areaInc = areaIncTmp;
				areaCursubSpace = areaCursubSpaceTmp;
			}
		}

		// 进入索引子空间搜索叶子节点
		return chooseLeaf(((InternalNode) start).getChildArray()[bestIndex],
				mbr);
	}

	/**
	 * 将记录插入到挑选出的叶子节点中，期间可能会引起节点分裂
	 * 
	 * @param leaf
	 * @param record
	 */
	private void insertToLeaf(LeafNode leaf, Record record) {
		// 叶子节点未满可直接插入，同时需要逐层调整mbr
		if (!leaf.isFull()) {
			leaf.insertRecord(record);
			adjustTree(leaf, null);
			return;
		}

		// 叶节点满时，必须分裂叶节点，之后调整R树
		LeafNode newLeaf = leaf.split(record);
		adjustTree(leaf, newLeaf);

	}

	/**
	 * 调整R树，逐层地往上更新mbr，需要分裂时分裂，直到根节点为止
	 * 
	 * @param oldNode
	 * @param newNode
	 */
	private void adjustTree(Node oldNode, Node newNode) {
		// 找到父节点指向oldNode的项，之后更新其中的mbr
		InternalNode parent = (InternalNode) oldNode.getParent();

		// 到达root时，没有newNode不需要更新，有newNode需要生成新的root并增高一层
		if (parent == null) {
			if (newNode == null) {
				return;
			} else {
				root = new InternalNode();
				root.setLevel(oldNode.getLevel()+1);
				((InternalNode) root).insertItem(new Item(oldNode.getMbr(),
						oldNode));
				((InternalNode) root).insertItem(new Item(newNode.getMbr(),
						newNode));
				
				oldNode.setParent(root);
				newNode.setParent(root);
				return;
			}
		}

		// parent不为空，即为正常的InternalNode
		parent.getMbrArray()[parent.getIndexOfChild(oldNode)] = oldNode
				.getMbr();
		if(newNode == null) {
			adjustTree(parent, null);
		} else {
			if (!parent.isFull()) {
				parent.insertItem(new Item(newNode.getMbr(), newNode));
				newNode.setParent(parent);
				adjustTree(parent, null);
			} else {
				InternalNode newSplitInternalNode = parent.split(new Item(newNode
						.getMbr(), newNode));
				adjustTree(parent, newSplitInternalNode); // 重新递归地调整这两个内部节点
			}
		}
	}

	/**
	 * 根据给定的矩形范围，搜索出该范围下所有的空间目标记录,并保存在容器中
	 * 
	 * @param range
	 * @return
	 */
	public List<Record> search(Rectangle range) {
		List<Record> list = new ArrayList<Record>();
		recursionSearch(root, range, list);
		return list;
	}

	// 辅助的递归搜索算法
	private void recursionSearch(Node start, Rectangle range, List<Record> list) {
		if(start.isLeaf()) {
			for (int i = 0; i < start.getNum(); i++) {
				if(start.getMbrArray()[i].overlap(range)) {
					list.add(new Record(start.getMbrArray()[i], ((LeafNode)start).getObjArray()[i]));
				}
			}
			return;
		}
		
		//目录矩形节点，一一比较是否有重叠,有重叠就s继续进入索引子空间搜索
		for(int i=0; i<start.getNum(); i++) {
			if(start.getMbrArray()[i].overlap(range)) {
				recursionSearch(((InternalNode)start).getChildArray()[i], range, list);                                                                                                                                                             
			}
		}
	}

}
