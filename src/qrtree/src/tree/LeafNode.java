package tree;

import java.util.ArrayList;
import java.util.List;

import common.Record;
import common.Rectangle;

public class LeafNode extends Node {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Double objArray[] = new Double[MAX]; // 叶子节点的底层数据为空间数据实体的标示

	
	
	
	public Double[] getObjArray() {
		return objArray;
	}

	public void setObjArray(Double[] objArray) {
		this.objArray = objArray;
	}

	@Override
	public boolean isLeaf() {
		return true;
	}

	public boolean insertRecord(Record record) {
		if (isFull()) {
			return false; // 叶节点已满，插入失败
		}

		mbrArray[num] = record.getMbr();
		objArray[num] = record.getObj();
		num++;

		return true;
	}

	/**
	 * 叶子节点分裂时，选取两个相差最远的种子形成各自独立的两组，之后将剩余的数据依次加入两组
	 * 
	 * @param record
	 * @return
	 */
	public LeafNode split(Record record) {
		LeafNode newLeaf = new LeafNode();

		// 1、取出MAX+1个记录
		List<Record> list = new ArrayList<Record>();
		list.add(record);
		for (int i = 0; i < MAX; i++) {
			list.add(new Record(mbrArray[i], objArray[i]));
		}
		reset();

		// 2、 挑选种子,原则是种子组合的死空间最大
		Record seed1 = list.get(0), seed2 = list.get(1);
		double deadSpaceMax = Rectangle.getNewMbr(
				list.get(0).getMbr(), list.get(1).getMbr()).getArea()
				- list.get(0).getMbr().getArea()
				- list.get(1).getMbr().getArea();
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				double deadSpace = Rectangle.getNewMbr(list.get(i).getMbr(),
						list.get(j).getMbr()).getArea()
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
		insertRecord(seed1);
		newLeaf.insertRecord(seed2);
		list.remove(seed1);
		list.remove(seed2);

		// 4、将剩下的MAX+1-2个项依据面积增量差值最大挑选出优先插入项，再依据最小面积增量原则添加到节点中
		Rectangle leafMbr = mbrArray[num - 1];
		Rectangle newLeafMbr = newLeaf.getMbrArray()[newLeaf.getNum() - 1];
		
		
		//循环一次插入一个记录到两个节点中的一个，插入一个之后相应的节点的mbr要更新
		while (list.size() != 0) {
			// 当有一个节点的数目达到MAX+1-MIN,为了使另一个节点至少满足MIN个，必须将剩余节点统统插入另一个节点
			if (num == MAX + 1 - MIN) {
				for (Record rec : list) {
					newLeaf.insertRecord(rec);
				}
				break;
			}
			if (newLeaf.getNum() == MAX + 1 - MIN) {
				for (Record rec : list) {
					insertRecord(rec);
				}
				break;
			}

			// 逐个比对数据项，找出最应当首先插入的数据项
			double areaIncDiffMax = 0;		//最大面积增量差
			Record willInsert = null;
			boolean isNewLeaf = false;		//待插入节点的新旧标记
			for (Record recordTmp : list) {
				double areaInc1 = Rectangle.getNewMbr(leafMbr, recordTmp.getMbr()).getArea() - leafMbr.getArea(); 
				double areaInc2 = Rectangle.getNewMbr(newLeafMbr, recordTmp.getMbr()).getArea() - newLeafMbr.getArea();
				double areaIncDiff = Math.abs(areaInc1-areaInc2);
				if(areaIncDiff >= areaIncDiffMax) {
					areaIncDiffMax = areaIncDiff;
					willInsert = recordTmp;
					if(areaInc1 < areaInc2) {
						isNewLeaf = false;
					} else {
						isNewLeaf = true;
					}
				}
			}
			
			//插入一个数据项,之后更新这两个节点mbr
			if(isNewLeaf) {
				newLeaf.insertRecord(willInsert);
				newLeafMbr = Rectangle.getNewMbr(newLeafMbr, willInsert.getMbr());
			} else {
				insertRecord(willInsert);
				leafMbr = Rectangle.getNewMbr(leafMbr, willInsert.getMbr());
			}
			
			list.remove(willInsert);
		}

		return newLeaf;
	}

	@Override
	public void reset() {
		for (int i = 0; i < num; i++) {
			mbrArray[i] = null;
			objArray[i] = null;
		}
		num = 0;
	}
}
