package com.slwb.supercut;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by bear on 16-3-9.
 */
public class ShowView extends SurfaceView implements SurfaceHolder.Callback{

    private SurfaceHolder holder;
    private Thumbnail thumbnail;
    public  String showpath=null;
    private Bitmap showBitmap;
    private int LineOnFrame=20;

    public  ShowView(Context context, AttributeSet attrs){
        super(context,attrs);
        holder = this.getHolder();
        holder.addCallback(this);
        thumbnail=new Thumbnail();
    }

    public void setShowpath(String ShowViewpath){
        showpath=ShowViewpath;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
        // TODO Auto-generated method stub

    }

    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        // TODO Auto-generated method stub
        new Thread(){
            public void run() {
                while(true){
                    Canvas c = holder.lockCanvas(new Rect(0, 0, 200, 200));
                    Paint  p =new Paint();
                    //
                    showBitmap=thumbnail.getFrameOn(LineOnFrame);
                    holder.unlockCanvasAndPost(c);

                    try {
                        Thread.sleep(20);
                    } catch (Exception e) {
                    }
                }
            };
        }.start();
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub

    }
}

