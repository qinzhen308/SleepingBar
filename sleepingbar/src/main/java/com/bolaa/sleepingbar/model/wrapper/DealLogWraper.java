package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.DealLog;

import java.util.ArrayList;
import java.util.List;

public class DealLogWraper implements BeanWraper<DealLog>{
	
	/**
	 * 
	 */
    public List<DealLog> list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<DealLog> getItems(){
    	if(list==null){
            list=new ArrayList<DealLog>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    	
    }
    
}
