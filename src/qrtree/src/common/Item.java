package common;

import tree.Node;

public class Item {
	private Rectangle mbr; 		//目录矩形
	private Node childNode; 	//指向一个子节点，可能是中间节点或者叶子节点
	
	public Item(Rectangle mbr, Node childNode) {
		this.mbr = mbr;
		this.childNode = childNode;
	}
	
	public Rectangle getMbr() {
		return mbr;
	}
	public void setMbr(Rectangle mbr) {
		this.mbr = mbr;
	}
	public Node getChildNode() {
		return childNode;
	}
	public void setChildNode(Node childNode) {
		this.childNode = childNode;
	}
	
	
}
