/**
 * 测试Rectangle类，尤其是getRectFromString函数
 */
package test;

import common.Rectangle;

public class RectangleTest {

	public static void main(String[] args) {
		Rectangle rect  = new Rectangle(1.5,2.4,3,4);
		String rectStr = rect.toString();
		System.out.println(rectStr);
		
		Rectangle rect1 = Rectangle.getRectFromString(rectStr);
		System.out.println(rect1);
		
		System.out.println(rect.contain(new Rectangle(1.5,2.4,3,4)));
	}

}
