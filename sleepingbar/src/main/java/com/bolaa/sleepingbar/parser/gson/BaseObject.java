package com.bolaa.sleepingbar.parser.gson;

import java.io.Serializable;

public class BaseObject<T> implements Serializable {
	
	public final static int STATUS_OK=100;
	public final static int STATUS_FAILED=0;
	public final static int STATUS_EXPIRE=402;//用户登录过期，需要清除登录状态或者重新登录

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String msg;
	public String token;
	public T data;
}
