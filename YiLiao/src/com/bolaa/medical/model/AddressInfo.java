package com.bolaa.medical.model;

import java.io.Serializable;

/**
 * 收货地址实体
 * 
 * @author jjj
 * 
 * @time 2015-12-11
 */
public class AddressInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String address_id;// 地址ID
	private String member_id;// 用户ID
	private String true_name;// 收货人真实姓名
	private String area_id;// 区县ID
	private String city_id;// 城市ID
	private String province_id;// 省份ID
	private String area_info;// 地区信息
	private String tel_phone;// 固定电话
	private String address;// 详细地址
	private String mob_phone;// 手机号码
	private String card_no;// 身份证号
	private String is_default;// 默认地址:1,普通地址:0)

	public String getAddress_id() {
		return address_id;
	}

	public void setAddress_id(String address_id) {
		this.address_id = address_id;
	}

	public String getMember_id() {
		return member_id;
	}

	public void setMember_id(String member_id) {
		this.member_id = member_id;
	}

	public String getTrue_name() {
		return true_name;
	}

	public void setTrue_name(String true_name) {
		this.true_name = true_name;
	}

	public String getArea_id() {
		return area_id;
	}

	public void setArea_id(String area_id) {
		this.area_id = area_id;
	}

	public String getCity_id() {
		return city_id;
	}

	public void setCity_id(String city_id) {
		this.city_id = city_id;
	}

	public String getProvince_id() {
		return province_id;
	}

	public void setProvince_id(String province_id) {
		this.province_id = province_id;
	}

	public String getArea_info() {
		return area_info;
	}

	public void setArea_info(String area_info) {
		this.area_info = area_info;
	}

	public String getTel_phone() {
		return tel_phone;
	}

	public void setTel_phone(String tel_phone) {
		this.tel_phone = tel_phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMob_phone() {
		return mob_phone;
	}

	public void setMob_phone(String mob_phone) {
		this.mob_phone = mob_phone;
	}

	public String getCard_no() {
		return card_no;
	}

	public void setCard_no(String card_no) {
		this.card_no = card_no;
	}

	public String getIs_default() {
		return is_default;
	}

	public void setIs_default(String is_default) {
		this.is_default = is_default;
	}

}
