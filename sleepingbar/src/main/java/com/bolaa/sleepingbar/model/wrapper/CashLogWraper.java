package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.CashLog;

import java.util.ArrayList;
import java.util.List;

public class CashLogWraper implements BeanWraper<CashLog>{
	
	/**
	 * 
	 */
    public List<CashLog> apply_cash_list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    @Override
    public int getItemsCount(){
    	return apply_cash_list==null?0:apply_cash_list.size();
    }
    
    @Override
    public List<CashLog> getItems(){
    	if(apply_cash_list==null){
            apply_cash_list=new ArrayList<CashLog>();
    	}
    	return apply_cash_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }
    
}
