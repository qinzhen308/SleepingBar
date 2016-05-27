package com.bolaa.sleepingbar.parser.gson;

import java.io.Serializable;
import java.util.List;

public class BaseObjectList<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public int status;
	public String msg;
	public String token;
	public List<T> data;
}
