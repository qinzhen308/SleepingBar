package com.bolaa.medical.model.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.bolaa.medical.model.Hospital;
import com.bolaa.medical.model.Score;

public class ScoreWraper implements BeanWraper<Score>{
	
	/**
	 * 
	 */
    public List<Score> account; //  当前页面所有的beans  hospital
    public Extend filter;
    
    
    
    @Override
    public int getItemsCount(){
    	return account==null?0:account.size();
    }
    
    @Override
    public List<Score> getItems(){
    	if(account==null){
    		account=new ArrayList<Score>();
    	}
    	return account;
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
