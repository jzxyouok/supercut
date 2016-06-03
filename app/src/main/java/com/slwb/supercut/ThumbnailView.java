package com.slwb.supercut;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by bear on 11/28/2015.
 */
public class ThumbnailView extends View
{

    public float       width = 720;
    public float       height = 1080;
    //
    private Thumbnail   thumbnail;
    private float       thumbnailHeight;
    private ArrayList<String> videoFiles = new ArrayList<String>();//视频文件名序列
    private String      moveOutPicFile = null;//被拖下去的文件名

    private ArrayList<Bitmap> thumbnailPictures = new ArrayList<Bitmap>();//视频条

    private ArrayList<PointF> thumbnailPicsCut = new ArrayList<PointF>();//每段视频起始点

    private Paint       paint = new Paint();

    private float       xTouch = - 100; //手指的坐标
    private float       yTouch = - 100;//手指坐标
    //
    private float       xLeft = width / 2;//第一个视频条的左上角坐标
    private float       yTop = height / 2;

    private boolean     startMoveX = false;//是否左右滑动
    private float       xTouchPrev = -100;//手指上一帧的坐标
    private float       yTouchPrev = -100;

    private boolean     canMoveY=false;//是否可以拖下
    private boolean     startMoveY = false;//是否被拖下
    private int         moveOutPicIndex = -1;//被拖下的是哪一个视频段
    private Bitmap      moveOutPic = null;//被拖下的视频条
    private float       moveOutX;//被拖下来的视频的坐标
    private float       moveOutY;
    private long        timeMoveLeftRightPrev = 0;//在屏幕坐标或者右边停止的时间

    //
    private PointF      moveOutPicsCut;//被拖下来的视频条的起始点

    private float       deletWidth,deletHeight;//垃圾桶显示的坐标
    private boolean     isDeletPressed=false;
    private boolean     isDeleted=false;
    private Bitmap      delet;
    //
    private GestureDetector gestureDetector;//识别长按

    //
    private boolean     movieCut = false;//是否在剪辑状态
    private int         movieCutIndex = -1;//当前剪辑的视频
    private boolean     movieCutBegin = false;//是否在编辑左端点
    private boolean     movieCutEnd = false;//是否在编辑右端点
    private float       frameOnCut=0;

    //双手缩放
    private float       oldDist,newDist;
    private boolean     isZoom=false;//是否需要缩放，当双指处于屏幕是打开
    private int         mode=0;
    private float       scale=1;//缩放比例

    //
    public ThumbnailView(Context context, AttributeSet attrs) {

        super(context, attrs);

        thumbnail = new Thumbnail();

        startTimer();

        gestureDetector = new GestureDetector(getContext(), new GestureListener());

    }

    //添加视频
    void addVideo(String fileName)
    {
        //
        fileName=fileName.substring(1, fileName.length() - 1);
        videoFiles.add(fileName);
        thumbnail.setWH(width,height);//设置视频条的显示高度
        thumbnailPictures.add(thumbnail.getThumbnail(fileName));
        thumbnailPicsCut.add( new PointF(0, thumbnailPictures.get(thumbnailPictures.size() - 1).getWidth()) );
    }
    void addVideo(List<String> fileName)//添加视频
    {
        //
        int  n=fileName.size();
        int  i;
        for (i=0;i<n;i++){
            String Name=((fileName.get(i)).toString()).substring(1, ((fileName.get(i)).toString()).length());
            videoFiles.add(Name);
            //
            thumbnail.setWH(width,height);
            thumbnailPictures.add( thumbnail.getThumbnail( Name ) );
            thumbnailPicsCut.add(new PointF(0, thumbnailPictures.get(thumbnailPictures.size() - 1).getWidth()));
        }

    }

    void update(){
        thumbnailPictures.clear();
        thumbnailPicsCut.clear();
        addVideo(videoFiles);
    }

    //清除视频
    void clearVideo()
    {
        videoFiles.clear();
        thumbnailPictures.clear();
        thumbnailPicsCut.clear();
    }

    //移除第index个视频
    public void remove(int index){
        //
        moveOutPicFile = videoFiles.remove(index);
        //
        moveOutPicsCut = thumbnailPicsCut.remove(index);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //

        paint.setColor(Color.BLUE);

        width = getMeasuredWidth();
        height = getMeasuredHeight();


        //画垃圾桶
        Resources res = getResources();
        Bitmap deletDF = BitmapFactory.decodeResource(res, R.drawable.deleteyw);
        Bitmap deletPS =BitmapFactory.decodeResource(res, R.drawable.deleteb);
        if(isDeletPressed){
            delet=deletPS;
        }else
        delet=deletDF;
        deletWidth= (width*8/10);
        deletHeight=(height*8/10);
        canvas.drawBitmap(delet,deletWidth,deletHeight,paint);

        //画略缩图
        if(thumbnailPictures.size() > 0) {
            yTop = height / 2 - thumbnailPictures.get(0).getHeight() / 2;
        }

        //
        if(thumbnailPictures.size() > 0)
        {
            //
            float nextX = xLeft;
            thumbnailHeight = thumbnailPictures.get(0).getHeight();

            for(int i = 0; i < thumbnailPictures.size(); i++) {

                if(i == 0) {

                    canvas.drawBitmap(thumbnailPictures.get(i), xLeft, yTop, paint);

                    //
                    // fill cut area已经剪辑掉的部分
                    paint.setColor(Color.RED);
                    paint.setAlpha(100);

                    canvas.drawRect(xLeft, yTop,
                            xLeft + thumbnailPicsCut.get(i).x,
                            yTop + thumbnailHeight, paint);

                    canvas.drawRect( xLeft + thumbnailPicsCut.get(i).y, yTop,
                            xLeft + thumbnailPictures.get(i).getWidth(),
                            yTop + thumbnailHeight ,paint);

                    //绘制视频条左右端的敏感区
                    paint.setColor(Color.GREEN);
                    paint.setAlpha(150);
                    paint.setStrokeWidth(20);
                    if(movieCut == true && movieCutBegin == true && movieCutIndex == i)
                    {
                        paint.setColor(Color.RED);
                        paint.setAlpha(150);
                    }
                    //
                    canvas.drawLine(xLeft + thumbnailPicsCut.get(i).x + 10,
                            yTop - 10,
                            xLeft + thumbnailPicsCut.get(i).x + 10,
                            yTop + thumbnailHeight + 10,
                            paint);

                    //
                    paint.setColor(0xFF38B0DE);
                    paint.setAlpha(150);
                    paint.setStrokeWidth(20);
                    if(movieCut == true && movieCutEnd == true && movieCutIndex == i)
                    {
                        paint.setColor(Color.RED);
                        paint.setAlpha(150);
                    }
                    canvas.drawLine(xLeft + thumbnailPicsCut.get(i).y - 10,
                            yTop - 10,
                            xLeft + thumbnailPicsCut.get(i).y - 10,
                            yTop + thumbnailHeight + 10,
                            paint);
                }
                else
                {
                    nextX += thumbnailPictures.get(i-1).getWidth();
                    canvas.drawBitmap(thumbnailPictures.get(i), nextX, yTop, paint);

                    // 填充剪辑掉的区块
                    paint.setColor(Color.RED);
                    paint.setAlpha(100);
                    canvas.drawRect( nextX, yTop,
                            nextX + thumbnailPicsCut.get(i).x,
                            yTop + thumbnailHeight ,paint);
                    canvas.drawRect(nextX + thumbnailPicsCut.get(i).y, yTop,
                            nextX + thumbnailPictures.get(i).getWidth(),
                            yTop + thumbnailHeight, paint);

                    //
                    paint.setColor(Color.GREEN);
                    paint.setAlpha(150);
                    paint.setStrokeWidth(20);
                    if(movieCut == true && movieCutBegin == true && movieCutIndex == i)
                    {
                        paint.setColor(Color.RED);
                        paint.setAlpha(150);
                    }

                    canvas.drawLine(nextX + thumbnailPicsCut.get(i).x + 10,
                            yTop - 10,
                            nextX + thumbnailPicsCut.get(i).x + 10,
                            yTop + thumbnailHeight + 10,
                            paint);

                    paint.setColor(0xFF38B0DE);
                    paint.setAlpha(150);
                    paint.setStrokeWidth(20);
                    if(movieCut == true && movieCutEnd == true && movieCutIndex == i)
                    {
                        paint.setColor(Color.RED);
                        paint.setAlpha(150);
                    }
                    canvas.drawLine(nextX + thumbnailPicsCut.get(i).y - 10,
                            yTop - 10,
                            nextX + thumbnailPicsCut.get(i).y - 10,
                            yTop + thumbnailHeight + 10,
                            paint);


                    paint.setColor(Color.GREEN);
                    paint.setStrokeWidth(3);
                    canvas.drawLine(nextX, yTop - 10,
                            nextX, yTop + thumbnailHeight + 10, paint);
                }

            }

        }

        //画拖下的视频条
        if(startMoveY&&!isDeletPressed&&moveOutPic!=null)
        {
            canvas.drawBitmap(moveOutPic, moveOutX, moveOutY-thumbnailHeight, paint);
        }
        paint.setAlpha(255);
        //画中线
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(2);
        canvas.drawLine(width/2,
                height / 5,
                width/2,
                height - height / 5,
                paint);


    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //
        gestureDetector.onTouchEvent(event);
        //
        xTouch = event.getX();
        yTouch = event.getY();

        float thumbnailHeight = 120;
        if(thumbnailPictures.size() > 0) {
            thumbnailHeight = thumbnailPictures.get(0).getHeight();
        }

        switch (event.getAction())
        {
            case MotionEvent.ACTION_DOWN:
            {
                mode=1;

                {
                    startMoveX = true;

                    xTouchPrev = event.getX();

                    yTouchPrev = event.getY();

                    //
                    float offsetX = event.getX() - xLeft;
                    for(int i = 0; i < thumbnailPictures.size(); i++) {

                        offsetX -= thumbnailPictures.get(i).getWidth();
                        if(offsetX < 0)
                        {
                            moveOutPicIndex = i;
                            break;
                        }
                    }
                    //
                }

            }
            break;

            //当有多个手指触摸屏幕时执行
            case MotionEvent.ACTION_POINTER_DOWN:
            {
                mode+=1;
                oldDist = spacing(event);
                isZoom=true;
                Log.e("oldDist:",oldDist+"");

            }
            break;

            case MotionEvent.ACTION_POINTER_UP:
            {
                mode-=1;
            }
            break;

            //
            case MotionEvent.ACTION_MOVE:
            {

                if(startMoveX == true)
                {
                    float moveDis = event.getX() - xTouchPrev;

                    xLeft += moveDis;

                    xTouchPrev = event.getX();

                    //
                    float offsetX = xLeft;
                    for(int i = 0; i < thumbnailPictures.size(); i++) {
                        offsetX += thumbnailPictures.get(i).getWidth();
                    }
                    if(offsetX < 10)
                    {
                        xLeft -= moveDis;
                    }
                    if( xLeft >= width - 10 )
                    {
                        xLeft = width - 10;
                    }
                }

                //
                if(startMoveX == true && startMoveY == false &&
                        (event.getY() < yTop || event.getY() > yTop + thumbnailPictures.get(0).getHeight())
                        &&canMoveY==true)
                {
                    startMoveX  = false;
                    startMoveY  = true;
                    canMoveY    = false;
                    if(moveOutPicIndex >= 0)
                    {
                        //
                        float nextX = xLeft;
                        for(int i = 0; i < moveOutPicIndex; i++) {
                            nextX += thumbnailPictures.get(i).getWidth();
                        }
                        //
                        moveOutPic = thumbnailPictures.remove(moveOutPicIndex);

                        moveOutPicFile = videoFiles.remove(moveOutPicIndex);

                        //
                        moveOutPicsCut = thumbnailPicsCut.remove(moveOutPicIndex);

                        //
                        moveOutX = nextX;
                        moveOutY = event.getY() - moveOutPic.getHeight() / 2;

                        //
                        // 移动视频剪辑
                        float offsetX = xLeft;
                        //int   currPicIndex = -1;
                        for(int i = 0; i < thumbnailPictures.size(); i++) {
                            offsetX += thumbnailPictures.get(i).getWidth();
                        }
                        if( offsetX < width / 2 )
                        {
                            xLeft = xLeft + ( width / 2 - offsetX );
                        }
                        else if( xLeft > width / 2)
                        {
                            xLeft = width / 2;
                        }

                    }
                }

                if(startMoveY == true) {
                    float moveXDis = event.getX() - xTouchPrev;
                    moveOutX += moveXDis;

                    float moveYDis = event.getY() - yTouchPrev;
                    moveOutY += moveYDis;

                    xTouchPrev = event.getX();
                    yTouchPrev = event.getY();
                }

                if(movieCut == true)
                {
                    startMoveX = false;
                    startMoveY = false;


                    frameOnCut=event.getX();

                    //
                    float moveXDis = event.getX() - xTouchPrev;

                    //
                    if(movieCutBegin == true)
                    {
                        float xCut = thumbnailPicsCut.get(movieCutIndex).x;

                        xCut += moveXDis;
                        if(xCut < 0)
                        {
                            xCut = 0;
                        }
                        else if(xCut > thumbnailPicsCut.get(movieCutIndex).y - 20 * 2)
                        {
                            xCut = thumbnailPicsCut.get(movieCutIndex).y - 20 * 2;
                        }
                        thumbnailPicsCut.get(movieCutIndex).x = xCut;
                    }
                    else
                    {
                        float yCut = thumbnailPicsCut.get(movieCutIndex).y;

                        yCut += moveXDis;
                        if(yCut < thumbnailPicsCut.get(movieCutIndex).x + 20 * 2)
                        {
                            yCut = thumbnailPicsCut.get(movieCutIndex).x + 20 * 2;
                        }
                        else if(yCut > thumbnailPictures.get(movieCutIndex).getWidth())
                        {
                            yCut = thumbnailPictures.get(movieCutIndex).getWidth();
                        }
                        thumbnailPicsCut.get(movieCutIndex).y = yCut;
                    }


                    xTouchPrev = event.getX();
                    yTouchPrev = event.getY();
                }

                //缩放视频
                if(mode>=2){
                    newDist=spacing(event);
                    Log.e("newDist:",""+newDist);
                    scale=newDist/oldDist;
                    oldDist=newDist;

                        Log.e("scale:",""+scale);
                        thumbnail.setInterval(scale);
                }

                //删除视频
                if(event.getX()>=deletWidth&&event.getX()<=deletWidth+delet.getWidth()){
                    if(event.getY()>=deletHeight&&event.getY()<=deletHeight+delet.getHeight()){
                        isDeletPressed=true;

                    }else isDeletPressed=false;

                }else isDeletPressed=false;

                //是否停留在了删除状态
                if(isDeletPressed&&startMoveY){
                    isDeleted=true;
                }else if(startMoveY&&!isDeletPressed){
                    isDeleted=false;
                }

            }
            break;
            case MotionEvent.ACTION_UP:
            {
                isDeletPressed=false;
                mode=0;
                isZoom=false;
                //
                startMoveX = false;

                //
                if(startMoveY == true)
                {
                    //
                    //
                    float offsetX = xLeft;
                    int   currPicIndex = -1;
                    for(int i = 0; i < thumbnailPictures.size(); i++) {

                        offsetX += thumbnailPictures.get(i).getWidth();

                        if(offsetX >= width / 2)
                        {
                            currPicIndex = i;
                            break;
                        }
                    }

                    if(!isDeleted) {


                        if (currPicIndex >= 0) {
                            if ((offsetX - width / 2) - thumbnailPictures.get(currPicIndex).getWidth() / 2 > 0) {
                                thumbnailPictures.add(currPicIndex, moveOutPic);

                                videoFiles.add(currPicIndex, moveOutPicFile);

                                thumbnailPicsCut.add(currPicIndex, moveOutPicsCut);

                            } else {
                                thumbnailPictures.add(currPicIndex + 1, moveOutPic);

                                videoFiles.add(currPicIndex + 1, moveOutPicFile);

                                thumbnailPicsCut.add(currPicIndex + 1, moveOutPicsCut);
                            }
                        } else {

                            thumbnailPictures.add(moveOutPic);

                            videoFiles.add(moveOutPicFile);

                            thumbnailPicsCut.add(moveOutPicsCut);
                        }
                    }

                    //
                    startMoveY = false;

                    moveOutPicIndex = -1;

                }
                //
                movieCut = false;

                xTouchPrev = -1;
                yTouchPrev = -1;

                xTouch = -100;
                yTouch = -100;
            }
            break;
        }


        //
        invalidate();

        return true;
        //return super.onTouchEvent(event);

    }

    //开始计时
    void startTimer()
    {
        Timer timer = new Timer();
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg){

                switch(msg.what){
                    case 1:

                        long timeCurr = System.currentTimeMillis();

                        onTimer(timeCurr);

                        break;
                }
                super.handleMessage(msg);
            }
        };

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Message msg =new Message();
                msg.what = 1;

                handler.sendMessage(msg);
            }
        };

        timer.schedule(task, 0, 1000/30);
    }

    //
    void onTimer(long timeCurr)
    {
        long t = timeCurr - timeMoveLeftRightPrev;
        if(startMoveY == true) {

            if( xTouch < width / 4 && t > 1000)
            {
                timeMoveLeftRightPrev = System.currentTimeMillis();
                //Log.e("touch", "left");

                //
                // move background movie clips
                //
                float offsetX = xLeft;
                int   currPicIndex = -1;
                for(int i = 0; i < thumbnailPictures.size(); i++) {

                    offsetX += thumbnailPictures.get(i).getWidth();

                    if(offsetX >= width / 2)
                    {
                        currPicIndex = i;
                        break;
                    }
                }

                if(currPicIndex >= 0)
                {
                    xLeft = xLeft + ( thumbnailPictures.get(currPicIndex).getWidth() - (offsetX - width / 2) );

                    invalidate();
                }
            }
            else if( xTouch > width / 4 * 3 && t > 1000)
            {
                timeMoveLeftRightPrev = System.currentTimeMillis();
                //Log.e("touch", "right");

                //
                // move background movie clips
                //
                float offsetX = xLeft;
                int   currPicIndex = -1;
                for(int i = 0; i < thumbnailPictures.size(); i++) {

                    offsetX += thumbnailPictures.get(i).getWidth();

                    if(offsetX > width / 2)
                    {
                        currPicIndex = i;
                        break;
                    }
                }
                if(currPicIndex >= 0)
                {
                    xLeft = xLeft - (offsetX - width / 2);

                    invalidate();
                }

            }
        }

    }


    //
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        //

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            //Log.e("GestureListener", "onLongPress");

            float picX = xLeft;

            for (int i = 0; i < thumbnailPictures.size(); i++)
            {
                float picWidth = thumbnailPictures.get(i).getWidth();
                float picHeight = thumbnailPictures.get(i).getHeight();

                float cursorX = thumbnailPicsCut.get(i).x;
                float cursorY = thumbnailPicsCut.get(i).y;
                if(e.getX() > picX + cursorX - 60 && e.getX() < picX + cursorX + 60
                        && e.getY() > yTop && e.getY() < yTop + picHeight )
                {
                    movieCut = true;

                    movieCutIndex = i;
                    movieCutBegin = true;
                    movieCutEnd = false;
                    Log.e("GestureListener", "onLongPressLeft" + movieCutBegin);

                    invalidate();
                    break;
                }
                else if(e.getX() > picX + cursorY - 60 && e.getX() < picX + cursorY + 60
                        && e.getY() > yTop && e.getY() < yTop + picHeight )
                {
                    movieCut = true;

                    movieCutIndex = i;
                    movieCutBegin = false;
                    movieCutEnd = true;

                    Log.e("GestureListener", "onLongPressRight" + movieCutEnd);

                    invalidate();
                    break;
                }

                picX += picWidth;
            }
            if(e.getY() > yTop && e.getY() < yTop + thumbnailHeight){
                canMoveY=true;
            }

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {

            isDeletPressed=false;
            return super.onSingleTapUp(e);
        }


    }// end class GestureListener

    public void setWH(int Width,int Height){
        width=Width;
        height=Height;
        xLeft = Width / 2;//第一个视频条的左上角坐标
        yTop = Height / 2;
    }

    //
    public ArrayList<String> getVideoFiles() {//获取剪辑序列
        return videoFiles;
    }


    public ArrayList<PointF> getThumbnailPicsCut() {//获取剪辑点
        return thumbnailPicsCut;
    }

    public int getLineOnFrame(){
        double LineOnFrame=20;
        int sec=thumbnail.getDuration(getShowpath());
        float p,length=0;
        int i;

        float thWidth= thumbnailPictures.get(getCurrVideo()).getWidth();

        for(i=0;i<getCurrVideo();i++){
            length+=thumbnailPictures.get(i).getWidth();
        }
        Log.e("framwOnCut:",""+frameOnCut);

        if(movieCut){
            p=((frameOnCut-xLeft)-length)/thWidth;
        }else
        p=((width-2*xLeft)/2-length)/thWidth;

        LineOnFrame=(p*sec);
        return (int)LineOnFrame;
    }
    public String getShowpath(){
        int i=getCurrVideo();
        String showpath;
        if(i>=0) {
            showpath = getVideoFiles().get(i);
            return showpath;
        }else return null;
    }

    //获取当前剪辑的视频
    public int getCurrVideo() {
        float offsetX = xLeft;
        int   currPicIndex = -1;

        for(int i = 0; i < thumbnailPictures.size(); i++) {

            offsetX += thumbnailPictures.get(i).getWidth();

            if(offsetX >= width / 2)
            {
                currPicIndex = i;
                break;
            }
        }
        if(movieCut){
            currPicIndex = movieCutIndex;
        }
        return currPicIndex;
    }

    //得到剪辑表
    public List<String> getCutxml(){
        List<String> cutxml=new ArrayList<String>();
        //打印地址
        DecimalFormat decimal = new DecimalFormat("#.##");
        String cut=null;
        for(int i = 0; i < videoFiles.size(); i++)
        {
            String result1 = decimal.format( thumbnailPicsCut.get(i).x / thumbnailPictures.get(i).getWidth() );
            String result2 = decimal.format( thumbnailPicsCut.get(i).y / thumbnailPictures.get(i).getWidth() );

            cut=videoFiles.get(i);
            cutxml.add(cut);
            cutxml.add(result1);
            cutxml.add(result2);
        }
        return cutxml;
    }

    //计算两点之间的距离
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return FloatMath.sqrt(x * x + y * y);
    }

}