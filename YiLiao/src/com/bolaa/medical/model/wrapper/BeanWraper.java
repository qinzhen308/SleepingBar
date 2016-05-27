package com.bolaa.medical.model.wrapper;

import java.util.List;

/**
* 结构：
* {
* 	"state":0
* 	"message":"success"
* 	"content":{
*				"total_page":10
*				"items":[{},{},{}...] 
* 			}
* }
* 这种结构的content解析为此实体
 */
public interface BeanWraper<T>{


    
    public int getItemsCount();
    
    public List<T> getItems();
    
    public int getTotalPage();
    
    
}

