package com.bolaa.medical.model;

import java.util.List;

import android.R.string;

/**
 * 预约实体
 * @author paulz
 *
 */
public class BookInDay {
	
	public String day_time_end;
	public String day_time_start;
	public int h_id;
	public int ha_id;
	public List<Book> info_list;
	public int statu;// 0代表关闭 1代表启动
	
	
	
	public class Book{
		public int can_do;//该时间段能否预约   0代表预约饱满 1代表可以预约 2代表已预约
		public String end_time;//2:30
		public String end_time_name;//上午
		public String ha_o_id;//该时间段的ID
		public int haved_num;//上午
		public int limit_num;//改时间段限制预约的人数
		public String start_time;//1:30
		public String start_time_name;//上午
	}
	

}
