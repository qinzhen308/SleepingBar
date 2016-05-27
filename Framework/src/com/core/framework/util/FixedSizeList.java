package com.core.framework.util;

import java.util.ArrayList;

/**
 * Created by suwg on 2015/2/26.
 */

//固定大小的数组，如果超出删除最早添加的，
//    具有排重功能
public class FixedSizeList<E> extends ArrayList<E> {


    int capacity;
    public FixedSizeList(int capacity) {
        super(capacity);
        this.capacity=capacity;
    }

    @Override
    public boolean add(E object) {
        if(this.contains(object))return false;
        if(this.size()>=capacity){
            this.remove(0);
        }
        super.add(object);
        return true;
    }
}
