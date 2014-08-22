/**
 * 基础的矩形类,(xMin,yMin)为左下角坐标，(xMax,yMax)为右上角坐标
 * 当左下角和右上角坐标相同时即为点
 */
package common;

import java.io.Serializable;
import java.util.StringTokenizer;

public class Rectangle implements Serializable {

	private static final long serialVersionUID = 1L;

	private double xMin;
	private double yMin;
	private double xMax;
	private double yMax;

	public Rectangle() {

	}

	public Rectangle(double xMin, double yMin, double xMax, double yMax) {
		this.xMin = xMin;
		this.xMax = xMax;
		this.yMin = yMin;
		this.yMax = yMax;
	}

	public double getxMin() {
		return xMin;
	}

	public void setxMin(double xMin) {
		this.xMin = xMin;
	}

	public double getxMax() {
		return xMax;
	}

	public void setxMax(double xMax) {
		this.xMax = xMax;
	}

	public double getyMin() {
		return yMin;
	}

	public void setyMin(double yMin) {
		this.yMin = yMin;
	}

	public double getyMax() {
		return yMax;
	}

	public void setyMax(double yMax) {
		this.yMax = yMax;
	}

	@Override
	public String toString() {
		return "(" + xMin + "," + yMin + "," + xMax + "," + yMax + ")";
	}

	// 从字符串中解析出矩形
	public static Rectangle getRectFromString(String str) {
		Rectangle tmp = new Rectangle();
		String newStr = str.substring(1, str.length() - 1);

		StringTokenizer tokenizer = new StringTokenizer(newStr, ",");
		tmp.setxMin(Double.parseDouble(tokenizer.nextToken()));
		tmp.setyMin(Double.parseDouble(tokenizer.nextToken()));
		tmp.setxMax(Double.parseDouble(tokenizer.nextToken()));
		tmp.setyMax(Double.parseDouble(tokenizer.nextToken()));

		return tmp;
	}

	// 判断该矩形是不是包含某矩形
	public boolean contain(Rectangle rect) {
		if (xMin <= rect.getxMin() && xMax >= rect.getxMax()
				&& yMin <= rect.getyMin() && yMax >= rect.getyMax())
			return true;
		else
			return false;
	}

	//判断该矩形是不是和某矩形重叠
	public boolean overlap(Rectangle rect) {
		//比较x、y范围是否同时有交集即可
		if(!(xMin >= rect.xMax || xMax <= rect.xMin))
		{
			if(!(yMin >= rect.yMax || yMax <= rect.yMin))
				return true;
		}
		return false;
	}
	
	// 两个矩形合并之后的mbr
	public static Rectangle getNewMbr(Rectangle rect1, Rectangle rect2) {
		Rectangle mbr = new Rectangle();
		mbr.setxMax(rect1.getxMax() > rect2.getxMax() ? rect1.getxMax() : rect2
				.getxMax());
		mbr.setyMax(rect1.getyMax() > rect2.getyMax() ? rect1.getyMax() : rect2
				.getyMax());
		mbr.setxMin(rect1.getxMin() < rect2.getxMin() ? rect1.getxMin() : rect2
				.getxMin());
		mbr.setyMin(rect1.getyMin() < rect2.getyMin() ? rect1.getyMin() : rect2
				.getyMin());
		return mbr;
	}

	// 获得矩形面积
	public double getArea() {
		return (xMax - xMin) * (yMax - yMin);
	}
}
