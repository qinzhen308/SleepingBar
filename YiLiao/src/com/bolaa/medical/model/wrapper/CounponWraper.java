package com.bolaa.medical.model.wrapper;

import com.bolaa.medical.model.Coupon;

import java.util.ArrayList;
import java.util.List;

public class CounponWraper implements BeanWraper<Coupon>{
	
	/**
	 * 
	 */
    public List<Coupon> bonus_list; //  当前页面所有的beans
    public Extend filter;
    
    
    
    @Override
    public int getItemsCount(){
    	return bonus_list==null?0:bonus_list.size();
    }
    
    @Override
    public List<Coupon> getItems(){
    	if(bonus_list==null){
            bonus_list=new ArrayList<Coupon>();
    	}
    	return bonus_list;
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
