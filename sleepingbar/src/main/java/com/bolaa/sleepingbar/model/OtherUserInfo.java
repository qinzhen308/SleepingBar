package com.bolaa.sleepingbar.model;

import java.io.Serializable;
import java.util.List;

/**
 * 其他用户
 * 
 * @author paul
 * 
 */
public class OtherUserInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	public String avatar;// 用户头像
	public String care_num;// 好友人数
	public String nick_name;// 昵称
	public String to_care_num;// 关注他的人数
	public String user_id;// 会员ID
	public String user_name;// 用户名
	public String medal_count;// 勋章总数
	public String sleep_fund;// 睡眠基金
	public String walk_total;// 步行总数

	public List<Medal> user_medal;// 勋章



	

}
