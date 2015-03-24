package com.jimome.mm.utils;

import java.util.Comparator;
import java.util.Map;


/**
 * Function:加密算法排序类
 * @author Angle
 *
 */
public class TariffComparator {
	public static class TariffMapComparator implements Comparator{
		@Override
		public int compare(Object o1, Object o2) {
			Map.Entry obj1 = (Map.Entry)o1;
			Map.Entry obj2 = (Map.Entry)o2;			
			return obj1.getKey().toString().compareTo(obj2.getKey().toString());
		}
	}
}
