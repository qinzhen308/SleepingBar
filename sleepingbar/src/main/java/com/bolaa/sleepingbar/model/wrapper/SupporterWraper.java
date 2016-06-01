package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Supporter;

import java.util.ArrayList;
import java.util.List;

public class SupporterWraper implements BeanWraper<Supporter>{
	
	/**
	 * 
	 */
    public List<Supporter> rank_list; //  当前页面所有的beans  order

    public int page_count;//页码总数
    public int my_funds_count;
    
    @Override
    public int getItemsCount(){
    	return rank_list==null?0:rank_list.size();
    }
    
    @Override
    public List<Supporter> getItems(){
    	if(rank_list==null){
            rank_list=new ArrayList<Supporter>();
    	}
    	return rank_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    	
    }
    
}
