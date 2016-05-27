package com.bolaa.medical.model.wrapper;

import com.bolaa.medical.model.Evaluate;

import java.util.ArrayList;
import java.util.List;

public class EvaluateWraper implements BeanWraper<Evaluate>{
	
	/**
	 * 
	 */
    public List<Evaluate> comment_list; //  当前页面所有的beans
    public Extend filter;
    
    
    
    @Override
    public int getItemsCount(){
    	return comment_list==null?0:comment_list.size();
    }
    
    @Override
    public List<Evaluate> getItems(){
    	if(comment_list==null){
            comment_list=new ArrayList<Evaluate>();
    	}
    	return comment_list;
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
