package com.bolaa.sleepingbar.model.wrapper;

import com.bolaa.sleepingbar.model.Friends;
import com.bolaa.sleepingbar.model.RankinglistItem;

import java.util.ArrayList;
import java.util.List;

public class FriendsWraper implements BeanWraper<Friends>{
	
	/**
	 * 
	 */
    public List<Friends> friend_list; //  当前页面所有的beans  order

    public int page_count;//页码总数


    @Override
    public int getItemsCount(){
    	return friend_list==null?0:friend_list.size();
    }
    
    @Override
    public List<Friends> getItems(){
    	if(friend_list==null){
            friend_list=new ArrayList<Friends>();
    	}
    	return friend_list;
    }
    
    @Override
    public int getTotalPage(){
    	return page_count;
    }
    
}
