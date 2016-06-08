package com.core.framework.util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EntitySort<T> implements Comparator<T> {

	 public static boolean ASC = true;// 升序  
	    public static boolean DESC = false;// 降序  
	    private boolean sortFlg;// 排序升降序flg  
	    private String sortKey;// 比较的字段  
	    private List<T> list;//比较的list  
	  
	    public EntitySort(List<T> list, String sortKey, Boolean sortFlg) {  
	        this.list = list;  
	        this.sortKey = sortKey;  
	        this.sortFlg = sortFlg;  
	    }  
	  
	    public EntitySort(List<T> list, String sortKey) {  
	        this.list = list;  
	        this.sortKey = sortKey;  
	    }  
	  
	    public List<T> sort() {  
	        Collections.sort(list, this);// 调用Collections.sort方法，第二个参数this，表示使用本class下的compare方法（策略模式？）  
	  
	        // sorted list  
	        return this.list;  
	    }  
	  
	    // 排序规则实现  
	    public int compare(T sortOne, T sortTow) {
	  
	        StringBuffer methodName = new StringBuffer();  
	        methodName.append("get");  
	        methodName.append(sortKey.substring(0, 1).toUpperCase());  
	        methodName.append(sortKey.substring(1));  
	  
	        String keyOne = null;  
	        String keyTow = null;  
	  
	        try {  
	            keyOne = (String) sortOne.getClass()  
	                    .getMethod(methodName.toString(), null)  
	                    .invoke(sortOne, null);// 利用反射机制动态取得T下的字段值。
	  
	            if (keyOne == null) {  
	                keyOne = "";  
	            }  
	            keyTow = (String) sortOne.getClass()  
	                    .getMethod(methodName.toString(), null)  
	                    .invoke(sortTow, null);  
	  
	            if (keyTow == null) {  
	                keyOne = "";  
	            }  
	        } catch (Exception e) {  
	  
	        }  
	  
	        if (sortFlg) {// 升降序判断  
	            return keyOne.compareTo(keyTow);  
	        } else {  
	            return -(keyOne.compareTo(keyTow));  
	        }  
	    }  

}
