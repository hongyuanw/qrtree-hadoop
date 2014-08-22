package tree;

import java.util.ArrayList;
import java.util.List;

import common.Item;
import common.Rectangle;

public class InternalNode extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Node childArray[] = new Node[MAX]; // 子节点引用数组

	@Override
	public boolean isLeaf() {
		return false;
	}

	public Node[] getChildArray() {
		return childArray;
	}

	public void setChildArray(Node[] childArray) {
		this.childArray = childArray;
	}

	@Override
	public void reset() {
		for (int i = 0; i < num; i++) {
			mbrArray[i] = null;
			childArray[i] = null;
		}
		num = 0;
	}

	public boolean insertItem(Item item) {
		if (isFull()) {
			return false; // 节点已满，插入失败
		}

		mbrArray[num] = item.getMbr();
		childArray[num] = item.getChildNode();
		num++;

		return true;
	}

	/**
	 * 内部节点分裂时，选取两个相差最远的种子形成各自独立的两组，之后将剩余的数据依次加入两组
	 * 
	 * @param Item
	 * @return
	 */
	public InternalNode split(Item item) {
		InternalNode newInternal = new InternalNode();
		newInternal.setLevel(level);

		// 1、取出MAX+1个记录
		List<Item> list = new ArrayList<Item>();
		list.add(item);
		for (int i = 0; i < MAX; i++) {
			list.add(new Item(mbrArray[i], childArray[i]));
		}
		reset();

		// 2、 挑选种子,原则是种子组合的死空间最大,同时死空间的值可能为负数
		Item seed1 = list.get(0), seed2 = list.get(1);
		double deadSpaceMax = Rectangle.getNewMbr(
				list.get(0).getMbr(), list.get(1).getMbr()).getArea()
				- list.get(0).getMbr().getArea()
				- list.get(1).getMbr().getArea();
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				double deadSpace = Rectangle.getNewMbr(
						list.get(i).getMbr(), list.get(j).getMbr()).getArea()
						- list.get(i).getMbr().getArea()
						- list.get(j).getMbr().getArea();
				if (deadSpace > deadSpaceMax) {
					deadSpaceMax = deadSpace;
					seed1 = list.get(i);
					seed2 = list.get(j);
				}
			}
		}

		// 3、将种子插入到两个节点中，同时删除容器中的种子
		insertItem(seed1);
		seed1.getChildNode().setParent(this);
		newInternal.insertItem(seed2);
		seed2.getChildNode().setParent(newInternal);
		list.remove(seed1);
		list.remove(seed2);

		// 4、将剩下的MAX+1-2个项依据面积增量差值最大挑选出优先插入项，再依据最小面积增量原则添加到节点中
		Rectangle InternalMbr = mbrArray[num - 1];
		Rectangle newInternalMbr = newInternal.getMbrArray()[newInternal
				.getNum() - 1];

		// 循环一次插入一个记录到两个节点中的一个，插入一个之后相应的节点的mbr要更新
		while (list.size() != 0) {
			// 当有一个节点的数目达到MAX+1-MIN,为了使另一个节点至少满足MIN个，必须将剩余节点统统插入另一个节点
			if (num == MAX + 1 - MIN) {
				for (Item rec : list) {
					newInternal.insertItem(rec);
					rec.getChildNode().setParent(newInternal);
				}
				break;
			}
			if (newInternal.getNum() == MAX + 1 - MIN) {
				for (Item rec : list) {
					insertItem(rec);
					rec.getChildNode().setParent(this);
				}
				break;
			}

			// 逐个比对数据项，找出最应当首先插入的数据项
			double areaIncDiffMax = 0; // 最大面积增量差
			Item willInsert = null;
			boolean isNewInternal = false; // 待插入节点的新旧标记
			for (Item ItemTmp : list) {
				double areaInc1 = Rectangle.getNewMbr(InternalMbr,
						ItemTmp.getMbr()).getArea()
						- InternalMbr.getArea();
				double areaInc2 = Rectangle.getNewMbr(newInternalMbr,
						ItemTmp.getMbr()).getArea()
						- newInternalMbr.getArea();
				double areaIncDiff = Math.abs(areaInc1 - areaInc2);
				if (areaIncDiff >= areaIncDiffMax) {
					areaIncDiffMax = areaIncDiff;
					willInsert = ItemTmp;
					if (areaInc1 < areaInc2) {
						isNewInternal = false;
					} else {
						isNewInternal = true;
					}
				}
			}

			// 插入一个索引项,同时不要更新索引子节点的父节点引用，同时更新两个节点的mbr
			if (isNewInternal) {
				newInternal.insertItem(willInsert);
				willInsert.getChildNode().setParent(newInternal);
				newInternalMbr = Rectangle.getNewMbr(newInternalMbr,
						willInsert.getMbr());
			} else {
				insertItem(willInsert);
				willInsert.getChildNode().setParent(this);
				InternalMbr = Rectangle.getNewMbr(InternalMbr,
						willInsert.getMbr());
			}

			list.remove(willInsert);
		}

		return newInternal;
	}

	// 找出子节点在父节点中的位置
	public int getIndexOfChild(Node childNode) {
		for (int i = 0; i < num; i++) {
			if (childArray[i] == childNode) {
				return i;
			}
		}
		return -1;
	}
}
