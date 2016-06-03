package com.slwb.supercut;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.media.MediaMetadataRetriever;

public class Thumbnail {//略缩图
	private Bitmap picture;
    private MediaMetadataRetriever retriever;
    private float   thumbnail_height=90;
    private int     seconds,num;
    private float   interval=4000;//提取帧的时间间隔，单位为毫秒
    private float   intervalScale=1.0f;//缩放比例

    //
    public void setPath(String filepath){
        retriever= new MediaMetadataRetriever();
        retriever.setDataSource(filepath);

    }

    public void setWH(float Width,float Height){
        float  pixel=Width*Height;
        if(pixel<=(800*480)){
            thumbnail_height=60.0f;
        }else if(pixel<=1280*720){
            thumbnail_height=130.0f;
        }else {
            thumbnail_height=180.0f;
        }

    }

	public Bitmap getThumbnail(String filename){

        retriever= new MediaMetadataRetriever();
        retriever.setDataSource(filename);  
        // 取得视频的长度(单位为毫秒)  
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);

        // 取得视频的长度(单位为豪秒)
        seconds = Integer.valueOf(time);
        picture = retriever.getFrameAtTime(1,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);//获取第一秒的图片
        picture=zoomImage(picture);//将第一秒的图片的大小转换为160*90
        
        // 得到某一时刻的bitmap比如第二秒,第三秒
            for (int i = 1; i <=seconds; i+=interval*intervalScale) {
                Bitmap bitmap = retriever.getFrameAtTime(i*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
                bitmap=zoomImage(bitmap);//将每一秒的图片的大小转换为160*90
                picture=ToConformBitmap(picture,bitmap);
            }

		return picture;
	}

    public void setInterval(float scale){
        intervalScale=scale;
    }
    public Bitmap getFrameOn(int OntimeMs){
        Bitmap frame;

        frame=retriever.getFrameAtTime(OntimeMs*1000,MediaMetadataRetriever.OPTION_CLOSEST_SYNC);
        return frame;
    }
    public int getDuration(String filepath){
        MediaMetadataRetriever retriever= new MediaMetadataRetriever();
        retriever.setDataSource(filepath);
        String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        //获得想要的毫秒
        num = Integer.valueOf(time);
        return num;
    }

	
	//将两张图片合成一张图片
	private Bitmap ToConformBitmap(Bitmap background, Bitmap foreground) {
        if( background == null ) {  
            return null;  
         }  
  
         int bgWidth = background.getWidth();  
         int bgHeight = background.getHeight();
         //int bgHeight = 90;  
         int fgWidth = foreground.getWidth();  
         int fgHeight = foreground.getHeight();
         //create the new blank bitmap 创建一个新的和SRC长度宽度一样的位图   
         Bitmap newbmp = Bitmap.createBitmap(bgWidth+fgWidth, bgHeight, Config.RGB_565); 
         Canvas cv = new Canvas(newbmp);  
         //draw bg into  
         cv.drawBitmap(background, 0, 0, null);//在 0，0坐标开始画入bg  
         //draw fg into  
         cv.drawBitmap(foreground, bgWidth, 0, null);//在 0，0坐标开始画入fg ，可以从任意位置画入
         //save all clip  
         cv.save(Canvas.ALL_SAVE_FLAG);//保存  
         //store  
         cv.restore();//存储  
         return newbmp; 
    }
	
	
	//缩放图片的方法
	public Bitmap zoomImage(Bitmap bgimage) {	 
		 
		// 获取这个图片的宽和高		 
		int width = bgimage.getWidth(); 	 
		int height = bgimage.getHeight();
				 
		// 创建操作图片用的matrix对象		 
		Matrix matrix = new Matrix(); 
		 
		// 计算缩放率，新尺寸除原始尺寸		 
		//float scaleWidth = ((float) 160) / width; 		 
		float scaleHeight = thumbnail_height / height;
	 
		// 缩放图片动作 
		matrix.postScale(scaleHeight, scaleHeight);  
		Bitmap bitmap = Bitmap.createBitmap(bgimage, 0, 0,width,height,  matrix, true); 
		
		return bitmap;
	}
	
}
