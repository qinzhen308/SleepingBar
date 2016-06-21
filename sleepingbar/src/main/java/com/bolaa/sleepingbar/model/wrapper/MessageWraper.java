package com.bolaa.sleepingbar.model.wrapper;


import com.bolaa.sleepingbar.model.Message;

import java.util.ArrayList;
import java.util.List;

public class MessageWraper implements BeanWraper<Message>{
	
	/**
	 * 
	 */
    public List<Message> message_list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    @Override
    public int getItemsCount(){
    	return message_list==null?0:message_list.size();
    }
    
    @Override
    public List<Message> getItems(){
    	if(message_list==null){
            message_list=new ArrayList<Message>();
    	}
    	return message_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    	
    }
    
}
