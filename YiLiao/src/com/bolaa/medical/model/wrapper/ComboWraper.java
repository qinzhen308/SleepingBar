package com.bolaa.medical.model.wrapper;

import com.bolaa.medical.model.Combo;

import java.util.ArrayList;
import java.util.List;

public class ComboWraper implements BeanWraper<Combo>{
	
	/**
	 * 
	 */
    public List<Combo> package_list; //  当前页面所有的beans
    public Extend filter;//暂时不是分页加载，所以没有
    
    
    
    @Override
    public int getItemsCount(){
    	return package_list==null?0:package_list.size();
    }
    
    @Override
    public List<Combo> getItems(){
    	if(package_list==null){
            package_list=new ArrayList<Combo>();
    	}
    	return package_list;
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
