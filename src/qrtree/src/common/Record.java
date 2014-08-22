package common;

public class Record {
	private Rectangle mbr; // 空间实体的mbr
	private double obj; // 空间实体的标示
	
	public Record() {
	}

	public Record(Rectangle mbr, double obj) {
		this.mbr = mbr;
		this.obj = obj;
	}

	public Rectangle getMbr() {
		return mbr;
	}
	public void setMbr(Rectangle mbr) {
		this.mbr = mbr;
	}
	public Double getObj() {
		return obj;
	}
	public void setObj(Double obj) {
		this.obj = obj;
	}

	@Override
	public String toString() {
		return "Record [mbr=" + mbr + ", obj=" + obj + "]";
	}
	
}
