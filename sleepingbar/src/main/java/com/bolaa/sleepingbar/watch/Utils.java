/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.bolaa.sleepingbar.watch;

import java.text.SimpleDateFormat;
import java.util.*;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;

/**
 * This class includes a small subset of standard GATT attributes for demonstration purposes.
 */
public class Utils {

    private static HashMap<Integer, String> serviceTypes = new HashMap();
    static {
        // Sample Services.
    	serviceTypes.put(BluetoothGattService.SERVICE_TYPE_PRIMARY, "PRIMARY");
    	serviceTypes.put(BluetoothGattService.SERVICE_TYPE_SECONDARY, "SECONDARY");
    }
    
    public static String getServiceType(int type){
    	return serviceTypes.get(type);
    }
    

    //-------------------------------------------    
    private static HashMap<Integer, String> charPermissions = new HashMap();
    static {
    	charPermissions.put(0, "UNKNOW");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ, "READ");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE, "WRITE");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
    	charPermissions.put(BluetoothGattCharacteristic.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");	
    }
    
    public static String getCharPermission(int permission){
    	return getHashMapValue(charPermissions,permission);
    }
    //-------------------------------------------    
    private static HashMap<Integer, String> charProperties = new HashMap();
    static {
    	
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_BROADCAST, "BROADCAST");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_EXTENDED_PROPS, "EXTENDED_PROPS");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_INDICATE, "INDICATE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_NOTIFY, "NOTIFY");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_READ, "READ");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_SIGNED_WRITE, "SIGNED_WRITE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE, "WRITE");
    	charProperties.put(BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE, "WRITE_NO_RESPONSE");
    }
    
    public static String getCharPropertie(int property){
    	return getHashMapValue(charProperties,property);
    }
    
    //--------------------------------------------------------------------------
    private static HashMap<Integer, String> descPermissions = new HashMap();
    static {
    	descPermissions.put(0, "UNKNOW");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ, "READ");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED, "READ_ENCRYPTED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_READ_ENCRYPTED_MITM, "READ_ENCRYPTED_MITM");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE, "WRITE");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED, "WRITE_ENCRYPTED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_ENCRYPTED_MITM, "WRITE_ENCRYPTED_MITM");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED, "WRITE_SIGNED");
    	descPermissions.put(BluetoothGattDescriptor.PERMISSION_WRITE_SIGNED_MITM, "WRITE_SIGNED_MITM");
    }
    
    public static String getDescPermission(int property){
    	return getHashMapValue(descPermissions,property);
    }
    
    
    private static String getHashMapValue(HashMap<Integer, String> hashMap,int number){
    	String result =hashMap.get(number);
    	if(TextUtils.isEmpty(result)){
    		List<Integer> numbers = getElement(number);
    		result="";
    		for(int i=0;i<numbers.size();i++){
    			result+=hashMap.get(numbers.get(i))+"|";
    		}
    	}
    	return result;
    }

    /**
     * 位运算结果的反推函数10 -> 2 | 8;
     */
    static private List<Integer> getElement(int number){
    	List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < 32; i++){
            int b = 1 << i;
            if ((number & b) > 0) 
            	result.add(b);
        }
        
        return result;
    }
    
    
    public static String bytesToHexString(byte[] src){
        StringBuilder stringBuilder = new StringBuilder("");  
        if (src == null || src.length <= 0) {  
            return null;  
        }  
        for (int i = 0; i < src.length; i++) {  
            int v = src[i] & 0xFF;  
            String hv = Integer.toHexString(v);  
            if (hv.length() < 2) {  
                stringBuilder.append(0);  
            }  
            stringBuilder.append(hv);  
        }  
        return stringBuilder.toString();  
    }

    //临时test
    public static void main(String[] args){
//        char[] bytes={97, 99, 101, 98, 97, 110, 100, 45, 88, 54, 45, 65, 65, 48, 65};
//        byte[] bytes={-7, 87, 69, -121, -3, 0, 0, 1, -5, 21};//
//        byte[] bytes={-7, 87, 69, -110, -55, 0, 0, 2, 23, 9};//f9574592c90000021709----2016-05-25 07:55:53
//        byte[] bytes={-7, 87, 69, -110, -55, 0, 0, 2, 24, 10};
        byte[] bytes={-7, 87, 69, -104, 76, 0, 0, 2, 34, -99};
//        byte[] bytes={-2, 87, 69, -108, 112, 0, 0, 0, -62, 96};//睡眠的
        System.out.println(String.valueOf(bytes));
//        System.out.print(byteToBit((byte) 97));
//        System.out.print(Arrays.toString(hexStringToBytes("3131")));
        System.out.println(bytesToHexString(bytes));
        for(int i=1;i<bytes.length;i++){
            System.out.println(i+"--"+bytes[i]);
        }

//        Date date=new Date(1464174589L*1000);
//        Date date=new Date(1464177353L*1000);
        Date date=new Date(1464177776L*1000);//睡眠
        System.out.println(new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(date));

    }

    public static String byteToBit(byte paramByte) {
        return "" + (byte)(paramByte >> 7 & 0x1) + (byte)(paramByte >> 6 & 0x1) + (byte)(paramByte >> 5 & 0x1) + (byte)(paramByte >> 4 & 0x1) + (byte)(paramByte >> 3 & 0x1) + (byte)(paramByte >> 2 & 0x1) + (byte)(paramByte >> 1 & 0x1) + (byte)(paramByte >> 0 & 0x1);
    }

    public static byte[] hexStringToBytes(String paramString) {
        byte[] paramStringb=null;
        if ((paramString == null) || (paramString.equals(""))) {
            paramStringb = null;
            return paramStringb;
        }
        paramString = paramString.toUpperCase();
        int j = paramString.length() / 2;
        char[] arrayOfChar = paramString.toCharArray();
        byte[] arrayOfByte = new byte[j];
        int i = 0;
        while (true) {
            paramStringb = arrayOfByte;
            if (i >= j)
                break;
            int k = i * 2;
            arrayOfByte[i] = ((byte)(charToByte(arrayOfChar[k]) << 4 | charToByte(arrayOfChar[(k + 1)])));
            i += 1;
        }
        return paramStringb;
    }

    private static byte charToByte(char paramChar)
    {
        return (byte)"0123456789ABCDEF".indexOf(paramChar);
    }


    public static int[] bytesToIntArray(byte[] src){
        if(src==null||src.length==0){
            return null;
        }
        int cmd=src[0]&0xffffffff;
        int data1=0;
        int data2=0;
        if(src.length>=5){
            data1=((src[1]<<24)&0xff000000)|((src[2]<<16)&0x00ff0000)|((src[3]<<8)&0x0000ff00)|((src[4]&0x000000ff));
        }
        if(src.length>=9){
            data2=((src[5]<<24)&0xff000000)|((src[6]<<16)&0x00ff0000)|((src[7]<<8)&0x0000ff00)|((src[8]&0x000000ff));
        }
        return new int[]{cmd,data1,data2};
    }

}
