package com.bolaa.medical.model;

import java.io.Serializable;

/**
 * 地区实体
 * 
 * @author paulz
 * 
 */
public class RegionInfo implements Serializable {
	/**
	 * 
	 */
	public static final long serialVersionUID = 1L;
	public int region_id;// 地区ID（主键）
	public int parent_id;// 该地区的父ID
	public String region_name;// 地区名称
	public int region_type;// 地区类型  0代表国家1省2市3区4街道
	

	
}
