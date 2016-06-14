package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Topic;

import java.util.ArrayList;
import java.util.List;

public class TopicWraper implements BeanWraper<Topic>{
	
	/**
	 * 
	 */
    public List<Topic> topic_list; //  当前页面所有的beans  order

    public int page_count;//页码总数


    @Override
    public int getItemsCount(){
    	return topic_list==null?0:topic_list.size();
    }
    
    @Override
    public List<Topic> getItems(){
    	if(topic_list==null){
            topic_list=new ArrayList<Topic>();
    	}
    	return topic_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }
    
}
