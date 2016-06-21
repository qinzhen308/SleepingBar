package com.bolaa.sleepingbar.model;

import java.io.Serializable;

/**
 * 用户实体
 * 
 * @author paul
 * 
 */
public class UserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public int is_login;// 是否登录
	
	public String avatar;// 用户头像
	
	public String birthday;// 生日
	public String got_fund;// 已领取基金
	public String height;// 身高
	public int is_hidden_coord;// 是否隐藏地理位置  	1隐藏 0不隐藏
	public int is_open_fund;// 	是否公开睡眠基金  1公开 0不公开
	public int is_runking;// 是否参与基金排行  1参与 0不参与
	public String nick_name;// 昵称
	public String sex;// 性别
	public String user_id;// 会员ID
	public String user_name;// 用户名
	public String user_money;// 用户余额
	public String sleep_fund;// 睡眠基金
	public String weight;// 体重

	
	public String member_qqopenid;// 三方凭证 qq
	public String member_wxopenid;// 三方凭证 微信
	public String member_sinaopenid;// 三方凭证 新浪微博
	
	public ThirdUser thirdUser;//三方登录用户信息
	

	

}
