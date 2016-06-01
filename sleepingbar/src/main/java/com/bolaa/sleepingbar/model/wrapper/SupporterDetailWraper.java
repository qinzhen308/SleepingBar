package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.model.SupporterDetail;

import java.util.ArrayList;
import java.util.List;

public class SupporterDetailWraper implements BeanWraper<SupporterDetail>{
	
	/**
	 * 
	 */
    public List<SupporterDetail> list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    public String nick_name;
    public String user_id;
    public String avatar;

    @Override
    public int getItemsCount(){
    	return list==null?0:list.size();
    }
    
    @Override
    public List<SupporterDetail> getItems(){
    	if(list==null){
            list=new ArrayList<SupporterDetail>();
    	}
    	return list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    	
    }
    
}
