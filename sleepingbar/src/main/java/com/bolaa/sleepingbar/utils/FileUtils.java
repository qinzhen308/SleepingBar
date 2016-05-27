package com.bolaa.sleepingbar.utils;

import android.os.Environment;

import com.core.framework.develop.LogUtil;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils 
{
	private String SDPATH;
	@SuppressWarnings("unused")
	public String getSDPATH()
	{
		return SDPATH;
	}
	
	public FileUtils()
	{
		//�õ���ǰ�ⲿ�洢�豸��Ŀ¼
		SDPATH = Environment.getExternalStorageDirectory() + "/";
	}
	
	public File createSDFile(String fileName) throws IOException
	{
		File file = new File(SDPATH + fileName);
		LogUtil.d("----创建文件"+file.createNewFile());
		return file;
	}
	
	/*
	 * ��SD���ϴ���Ŀ¼
	 * */
	public File createSDDir(String dirName)
	{
		File dir = new File(SDPATH + dirName);
		dir.mkdir();
		
		return dir;
		
		
	}
	
	/*
	 * �ж�SD�����ļ����Ƿ����
	 * 
	 * */
	public boolean isFileExist(String fileName)
	{
		File file = new File(SDPATH + fileName);
		return file.exists();
	}
	
	/*
	 * ��һ��InputStream���������д�뵽SD����
	 * */
	public File write2SDFromINput(String path,String fileName,InputStream input)
	{
		File file = null;
		OutputStream output = null;
		try
		{
			
			 byte[] arr = new byte[1];
	          ByteArrayOutputStream baos = new ByteArrayOutputStream();
	          BufferedOutputStream bos = new BufferedOutputStream(baos);
	          int n = input.read(arr);
	          while (n > 0) {
	            bos.write(arr);
	            n = input.read(arr);
	          }
	          bos.close();
	          
	          
	          
	          createSDDir(path);
			  file = createSDFile(path + fileName);
			  output = new FileOutputStream(file);
			  output.write(baos.toByteArray());
			  
			  output.flush();
			  baos.close();
			/*
			createSDDir(path);
			file = createSDFile(path + fileName);
			output = new FileOutputStream(file);
			byte buffer[] = new byte[10 * 1024*1024];
			while((input.read(buffer)) != -1)
			{
				output.write(buffer);
			}
			output.flush();
			*/
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				
				output.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return file;
	}
	
	
}






































