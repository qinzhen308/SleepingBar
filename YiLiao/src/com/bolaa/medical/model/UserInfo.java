package com.bolaa.medical.model;

import java.io.Serializable;
import java.security.PublicKey;

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
	public String blood;// 血型
	public String id_card;// 身份证
	public String mobile_phone;// 手机号
	public String pay_points;// 支付积分
	public String rank_name;// 会员等级
	public String rank_points;// 会员等级积分
	public String real_name;// 真实姓名
	public String sex;// 血型==性别
	public String user_id;// 会员ID
	public String user_name;// 用户名
	public String user_rank;// 会员等级id
	
	
	public String member_qqopenid;// 三方凭证 qq
	public String member_wxopenid;// 三方凭证 微信
	public String member_sinaopenid;// 三方凭证 新浪微博
	
	public ThirdUser thirdUser;//三方登录用户信息
	

	

}
