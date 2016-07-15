package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.RankinglistItem;

import java.util.ArrayList;
import java.util.List;

public class RankinglistItemWraper implements BeanWraper<RankinglistItem>{
	
	/**
	 * 
	 */
    public List<RankinglistItem> rank_list; //  当前页面所有的beans  order

    public int page_count;//页码总数

    public int my_sleep_rank;
    public int my_praise_num;
    public float my_sleep_fund;
    public String my_nickname;
    public String my_avatar;

    @Override
    public int getItemsCount(){
    	return rank_list==null?0:rank_list.size();
    }
    
    @Override
    public List<RankinglistItem> getItems(){
    	if(rank_list==null){
            rank_list=new ArrayList<RankinglistItem>();
    	}
    	return rank_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }
    
}
