package com.bolaa.medical.listener;

import android.widget.CheckBox;

/**
 * 收货地址
 * 
 * @author jjj
 * 
 * @time 2015-12-28
 */
public interface AddressListener {
	public void addressDelete(int position);

	public void addressUpdate(int position);

	public void setDefault(int position, CheckBox box);
}
