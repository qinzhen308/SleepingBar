package com.bolaa.medical.model;

import java.io.Serializable;

public class Voucher implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int voucher_id;
	public String voucher_code;
	public String voucher_title;
	public String voucher_desc;
	public int voucher_t_gc_id;
	public long voucher_start_date;
	public long voucher_end_date;
	public float voucher_price;
	public float voucher_limit;
	public int voucher_state;
	public String voucher_order_id;
	public int voucher_store_id;
	public String store_name;
	public int store_id;
	public String store_domain;
	public String voucher_t_customimg;
	public String gc_name;
	public String desc;

	public boolean isSelected;// 是否被选中，本地的

}
