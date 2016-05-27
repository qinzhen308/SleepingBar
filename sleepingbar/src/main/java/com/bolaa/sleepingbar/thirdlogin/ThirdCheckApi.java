package com.bolaa.sleepingbar.thirdlogin;

public class ThirdCheckApi {
	
	private static ThirdCheckApi instance;

	public static ThirdCheckApi getInstance(){
		if(instance==null){
			synchronized (instance) {
				if(instance==null){
					instance=new ThirdCheckApi();
				}
			}
		}

		return instance;
	}
	
	public void bind(){
		
	}
	
	
}
