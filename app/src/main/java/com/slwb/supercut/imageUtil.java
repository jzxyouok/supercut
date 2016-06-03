package com.slwb.supercut;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
public class imageUtil {  
       //放大缩小图片  
	public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h){  
		int width = bitmap.getWidth();  
		int height = bitmap.getHeight();  
		Matrix matrix = new Matrix();  
		float scaleWidht = ((float)w / width);  
		float scaleHeight = ((float)h / height);  
		matrix.postScale(scaleWidht, scaleHeight);  
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);  
		return newbmp;  
		}  
	//将Drawable转化为Bitmap  
	public static Bitmap drawableToBitmap(Drawable drawable){  
		int width = drawable.getIntrinsicWidth();  
		int height = drawable.getIntrinsicHeight();  
		Bitmap bitmap = Bitmap.createBitmap(width, height,  
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888  
						: Bitmap.Config.RGB_565);  
		Canvas canvas = new Canvas(bitmap);  
		drawable.setBounds(0,0,width,height);  
		drawable.draw(canvas);  
		return bitmap;         
		}  
       
	//获得圆角图片的方法  
	public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
				.getHeight(), Config.ARGB_8888);  
		Canvas canvas = new Canvas(output);  
		final int color = 0xff424242;  
		final Paint paint = new Paint();  
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
		final RectF rectF = new RectF(rect);  
		paint.setAntiAlias(true);  
		canvas.drawARGB(0, 0, 0, 0);  
		paint.setColor(color);  
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
		canvas.drawBitmap(bitmap, rect, rect, paint);  
		return output;  
		}  
}  