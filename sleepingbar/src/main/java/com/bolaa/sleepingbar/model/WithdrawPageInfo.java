package com.bolaa.sleepingbar.model;

import java.io.Serializable;
import java.util.ArrayList;

public class WithdrawPageInfo{

	public String user_id;
	public ArrayList<Bank> bank_array;
	public double user_money;
	
	public class Bank{

		public String id;
		public String name;

		@Override
		public String toString() {
			return name;
		}
	}

}
