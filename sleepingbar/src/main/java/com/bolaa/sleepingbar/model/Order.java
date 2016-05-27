package com.bolaa.sleepingbar.model;

import java.io.Serializable;

public class Order implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String add_time;
	public String address;
	public String banner_img;
	public String check_time;
	public String day_time;
	public String report_time;//出报告的时间
	public String end_time;
	public String end_time_name;
	public String from_way;
	public String from_way_str;
	public String h_id;
	public String ha_o_id;
	public String health_statu;
	public String health_statu_str;
	public String hospital_name;
	public String link_man;
	public String mobile_phone;
	public String order_amount;
	public String order_id;
	public String order_sn;
	public int order_statu; //0-4
	public String order_statu_str;// [0]="已预约"; [1]="已体检"; [2]="已出报告"; [3]="已过期"; [4]="已取消";
	public String health_statu_str_en;//
	public String pay_id;
	public String pay_name;
	public int pay_statu;
	public String pay_statu_str;
	public String pay_time;
	public String real_name;
	public String region;
	public String start_time;
	public String start_time_name;
	public String tel;
	public String user_id;
	public String user_name;
	public String report_url;
	public int report_type;

	public operable operable_list;

	//------结算页面或者订单去支付页面的相关字段----
	public float bonus;//优惠券抵扣的金额
//	public String bonus_id_arr;//如果预约使用的 多个优惠券 用逗号","分隔
	public float surplus;//	预约使用的余额


	public class operable{
		public boolean pay;//true 状态为 去支付，可以点击去支付
		public boolean cancle;
	}
}
