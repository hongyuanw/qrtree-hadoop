package tree;

import java.io.Serializable;

import common.Rectangle;

public abstract class Node implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final int MAX = 512; // 最多数据项
	public static final int MIN = 256; // 最少数据项

	protected Node parent; // 父节点
	protected int num; // 当前数据项数量
	protected Rectangle mbrArray[] = new Rectangle[MAX]; // 数据项为mbr
	protected int level;	//节点所在层数，主要便于调试

	public abstract boolean isLeaf();

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}

	public int getNum() {
		return num;
	}

	public void setNumItems(int num) {
		this.num = num;
	}

	public Rectangle[] getMbrArray() {
		return mbrArray;
	}

	public void setMbrArray(Rectangle[] mbrArray) {
		this.mbrArray = mbrArray;
	}


	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	// 判断节点是否已满
	public boolean isFull() {
		return num == MAX;
	}

	//清除节点中的数据
	public abstract void reset();
	
	//获得本节点的mbr
	public Rectangle getMbr() {
		Rectangle mbr = mbrArray[0];
		for (int i = 1; i < num; i++) {
			mbr = Rectangle.getNewMbr(mbr, mbrArray[i]);
		}
		return mbr;
	}
}
