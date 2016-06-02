package com.bolaa.sleepingbar.parser.gson;

import java.io.Serializable;

public class BaseObject<T> implements Serializable {
	
	public final static int STATUS_OK=1;
	public final static int STATUS_FAILED=0;
	public final static int STATUS_EXPIRE=-1;//用户登录过期，需要清除登录状态或者重新登录

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String info;
	public String token;
	public T data;
}
