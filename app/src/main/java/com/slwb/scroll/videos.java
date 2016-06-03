package com.slwb.scroll;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.os.Environment;

public class videos {

	private List<String> lstFile =new ArrayList<String>(); //结果 List
	boolean isIterative=true;
	String path=Environment.getExternalStorageDirectory() + "/剪影/作品/";
	
	public  String[] imageUrls=new String[]{};//
	
	public String[] getlist() {
		GetFiles(path,"png",isIterative);
		
		//List<string>转string[]
		String[] strArr = new String[lstFile.size()];
		lstFile.toArray(strArr);
		imageUrls=strArr;
		return imageUrls ; 
	}
	
	public void GetFiles(String Path, String Extension,boolean IsIterative) //搜索目录，扩展名，是否进入子文件夹
	{
		
	   File[] files =new File(Path).listFiles();
		for (File f : files) {
			if (f.isFile()) {
				if (f.getPath().substring(f.getPath().length() - Extension.length()).equals(Extension)) //判断扩展名
					lstFile.add(f.getPath());

				if (!IsIterative)
					break;
			} else if (f.isDirectory() && !f.getPath().contains("/.")) //忽略点文件（隐藏文件/文件夹）
				GetFiles(f.getPath(), Extension, IsIterative);
		}
	} 
}
