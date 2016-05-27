package com.bolaa.medical.model.wrapper;

import java.util.ArrayList;
import java.util.List;

import com.bolaa.medical.model.Hospital;

public class HospitalWraper implements BeanWraper<Hospital>{
	
	/**
	 * 
	 */
    public List<Hospital> hospital_list; //  当前页面所有的beans  hospital
    public Extend filter;
    
    
    
    @Override
    public int getItemsCount(){
    	return hospital_list==null?0:hospital_list.size();
    }
    
    @Override
    public List<Hospital> getItems(){
    	if(hospital_list==null){
    		hospital_list=new ArrayList<Hospital>();
    	}
    	return hospital_list;
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
