package com.bolaa.medical.model;

import java.io.Serializable;
import java.util.ArrayList;

public class WithdrawPageInfo implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public String account_bank;
	public String account_name;
	public String account_number;
	public ArrayList<Bank> bank_list;
	public float user_money;
	
	public class Bank implements Serializable{
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public String ename;
		public String excolumn;
		public String id;
		public String is_open;
		public String logo;
		public String name;
		public String sort_order;
	}

}
