package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Supporter;
import com.bolaa.sleepingbar.model.TopicComments;

import java.util.ArrayList;
import java.util.List;

public class CommentsWraper implements BeanWraper<TopicComments>{
	
	/**
	 * 
	 */
    public List<TopicComments> comment_list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    @Override
    public int getItemsCount(){
    	return comment_list==null?0:comment_list.size();
    }
    
    @Override
    public List<TopicComments> getItems(){
    	if(comment_list==null){
            comment_list=new ArrayList<TopicComments>();
    	}
    	return comment_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    	
    }
    
}
