package com.slwb.supercut;

import android.text.format.Time;

public class stringUtil {//格式化字符的类

    	public static String trim(String s,char c) {
    		  int i = s.length();// 字符串最后一个字符的位置
    		  int j = 0;// 字符串第一个字符
    		  int k = 0;// 中间变量
    		  char[] arrayOfChar = s.toCharArray();// 将字符串转换成字符数组
    		  while ((j < i) && (arrayOfChar[(k + j)] <= c))
    		   ++j;// 确定字符串前面的空格数
    		  while ((j < i) && (arrayOfChar[(k + i - 1)] <=c))
    		   --i;// 确定字符串后面的空格数
    		  return (((j > 0) || (i < s.length())) ? s.substring(j, i) : s);// 返回去除字符c后的字符串
    		 }

	public String RemoveBothCharacters(String FilePath){//去除首尾字符
		String path=null;
		path=FilePath.substring(1, FilePath.length() - 1);//格式化路径
		return path;
	}
	public String getFileName(String pathandname){  //获取视频路径的文件名

		int start=pathandname.lastIndexOf("/");
		int end=pathandname.lastIndexOf(".");
		if(start!=-1 && end!=-1){
			return pathandname.substring(start+1,end);
		}else{
			return null;
		}

	}

	public String getNameByTime(){
		Time time = new Time("GMT+8");
		time.setToNow();
		int year = time.year;
		int month = time.month;
		int day = time.monthDay;
		int minute = time.minute;
		int hour = time.hour;
		int sec = time.second;
		String name;
		name="/剪影/作品/supercut"+year + month + day +  hour + minute +sec +".mp4";
		return name;
	}


}