package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Order;

import java.util.ArrayList;
import java.util.List;

public class OrderWraper implements BeanWraper<Order>{
	
	/**
	 * 
	 */
    public List<Order> order_list; //  当前页面所有的beans  order
    public Extend filter;
    
    
    
    @Override
    public int getItemsCount(){
    	return order_list==null?0:order_list.size();
    }
    
    @Override
    public List<Order> getItems(){
    	if(order_list==null){
    		order_list=new ArrayList<Order>();
    	}
    	return order_list;
    }
    
    @Override
    public int getTotalPage(){
    	return filter!=null?filter.page_count:0;
    	
    }
    
    public class  Extend{
    	public int page_size;//当前每页条数
    	public int page_count;//页码总数
    	public int page;
    }

}
