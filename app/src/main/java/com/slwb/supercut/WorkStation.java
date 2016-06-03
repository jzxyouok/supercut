package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class WorkStation extends Activity implements SurfaceHolder.Callback {
	/* 头像文件 */
	private static final String IMAGE_FILE_NAME = "temp_head_image.jpg";
	 /* 请求识别码 */
	private static final int CODE_GALLERY_REQUEST = 0xa0;
	private static final int CODE_CAMERA_REQUEST = 0xa1;
	private static final int CODE_RESULT_REQUEST = 0xa2;
	// 裁剪后图片的宽(X)和高(Y),100 X 100的正方形。
	private static int output_X = 100;
	private static int output_Y = 100;
	private Button headImage = null;
	private SharedPreferences share ;
	private TextView name;
	private Button back;
	private SurfaceView surface;
	private SurfaceHolder surfaceHolder;
	private MediaPlayer player;
	RelativeLayout layout;
	String  outFileSrc=null;
	Button head;
	int height,width;
	int Width_screen;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.work_station);
		//设置原型图案
		setupViews(); 
		//
		String path =Environment.getExternalStorageDirectory()+"/剪影/视频池";
		File file =new File(path);
		deleteFile(file);			//清空视频池的文件
//屏幕的宽度,高度
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Width_screen = dm.widthPixels;

		surface=(SurfaceView)findViewById(R.id.sowInWorkStation);
		share = super.getSharedPreferences("persondata", MODE_PRIVATE);//实例化
		outFileSrc=share.getString("outSrc",null);

		name=(TextView) findViewById(R.id.name);
		name.setText(share.getString("username", "昵称"));

		back = (Button) findViewById(R.id.back_workstation);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View source) {

				finish();
			}
		});
		
		Button settings = (Button) findViewById(R.id.setting_workstation);
		settings.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View source) {
				// 创建需要启动的Activity对应的Intent
				Intent f = new Intent(WorkStation.this, PersonInformation.class);
				// 启动intent对应的Activity
				startActivity(f);
			}
		});

		surfaceHolder = surface.getHolder();//SurfaceHolder是SurfaceView的控制接口
		surfaceHolder.addCallback(this);
		if(outFileSrc!=null) {
			MediaMetadataRetriever retriever = new MediaMetadataRetriever();
			retriever.setDataSource(outFileSrc);
			width = Integer.valueOf(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH));
			height = Integer.valueOf(retriever.extractMetadata(android.media.MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT));
			//因为这个类实现了SurfaceHolder.Callback接口，所以回调参数直接this
			if (width > height) {
				float y = 0, x = 0;
				x = (float)height / width;
				width = Width_screen;
				y = Width_screen * x;
				height = (int) y;

			}
			System.out.println("视频比例为"+width+":"+height);
			surfaceHolder.setFixedSize(width, height);//显示的分辨率,不设置为视频默认
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);//Surface类型
			System.out.println("即将预览:" + outFileSrc);
		}
		//播放预览
		head=(Button)findViewById(R.id.head);
		head.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (outFileSrc != null) {
					player.start();
				}
				System.out.println("正在播放:" + outFileSrc);
			}
		});


	}
	/*
	public void customList(View source)
	{		
		new AlertDialog.Builder(this)
		// 设置对话框的标题
		.setTitle("更换头像")
		// 为对话框设置一个“确定”按钮
		.setPositiveButton("图片" , new OnClickListener()
		{
			public void onClick(DialogInterface dialog,int which)
			{
				// 此处可执行本地图片替换头像
				choseHeadImageFromGallery();
				}

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
			}
			})
			// 为对话框设置一个“取消”按钮
			.setNegativeButton("拍照", new OnClickListener()
			{
				public void onClick(DialogInterface dialog,int which)
				{
					//此处可执行拍照替换头像
					choseHeadImageFromCameraCapture();
				}

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					
				}
			})
			// 创建、并显示对话框
			.create()
			.show();
	}
*/
	private void setupViews(){  
		Button mImageView01 = (Button)findViewById(R.id.head);  
		//获取壁纸返回值是Drawable  
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.gerenw);    //这里是头像的图片 
		
		 //缩放图片  
		Bitmap zoomBitmap = imageUtil.zoomBitmap(bitmap, 100	, 100);  
		//获取圆角图片  
		Bitmap roundBitmap = imageUtil.getRoundedCornerBitmap(zoomBitmap, 50.0f);  
        //这里可以让Bitmap再转化为Drawable  
		//      Drawable roundDrawable = new BitmapDrawable(roundBitmap);         
		//      Drawable reflectDrawable = new BitmapDrawable(reflectBitmap);         
		//      mImageView01.setBackgroundDrawable(roundDrawable);  
		//      mImageView02.setBackgroundDrawable(reflectDrawable);                  
		
		BitmapDrawable bd1=new BitmapDrawable(roundBitmap);
		mImageView01.setBackgroundDrawable(bd1);
		} 
	
	// 从本地相册选取图片作为头像 
	private void choseHeadImageFromGallery() {
		Intent intentFromGallery = new Intent();
		// 设置文件类型
		intentFromGallery.setType("image/*");
		intentFromGallery.setAction(Intent.ACTION_GET_CONTENT);
		startActivityForResult(intentFromGallery, CODE_GALLERY_REQUEST);
		}
	// 启动手机相机拍摄照片作为头像
	private void choseHeadImageFromCameraCapture() {
		Intent intentFromCapture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// 判断存储卡是否可用，存储照片文件
		if (hasSdcard()) {
			intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory(), IMAGE_FILE_NAME)));
			}
		startActivityForResult(intentFromCapture, CODE_CAMERA_REQUEST);
		}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
		// 用户没有进行有效的设置操作，返回
		if (resultCode == RESULT_CANCELED) {
			Toast.makeText(getApplication(), "取消", Toast.LENGTH_LONG).show();
			return; 
		}
		switch (requestCode) {
		case CODE_GALLERY_REQUEST:
			cropRawPhoto(intent.getData());break;
		
		case CODE_CAMERA_REQUEST:
			if (hasSdcard()) {
				File tempFile = new File(
						Environment.getExternalStorageDirectory(),
						IMAGE_FILE_NAME);
				cropRawPhoto(Uri.fromFile(tempFile));
			}	
			else {
				Toast.makeText(getApplication(), "没有SDCard!", Toast.LENGTH_LONG).show(); 
			}
			break;
		
		case CODE_RESULT_REQUEST:
			if (intent != null) {
				setImageToHeadView(intent);
			}             break;
		} 
		super.onActivityResult(requestCode, resultCode, intent); 
	}
	
	//裁剪原始的图片
	public void cropRawPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 设置裁剪 
		intent.putExtra("crop", "true");
		// aspectX , aspectY :宽高的比例 
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1); 
		// outputX , outputY : 裁剪图片宽高       
		intent.putExtra("outputX", output_X);
		intent.putExtra("outputY", output_Y);
		intent.putExtra("return-data", true);
		startActivityForResult(intent, CODE_RESULT_REQUEST);
	}
	
	//提取保存裁剪之后的图片数据，并设置头像部分的View
	private void setImageToHeadView(Intent intent) {
		Bundle extras = intent.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			BitmapDrawable bd=new BitmapDrawable(photo);
			headImage.setBackgroundDrawable(bd);
		}
	}
	
	//检查设备是否存在SDCard的工具方法
	public static boolean hasSdcard() {
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			// 有存储的SDCard
			return true;
			}
		else 
		{ return false; }
	}

	public void deleteFile(File file) {
		if (file.exists()) { // 判断文件是否存在
			if (file.isFile()) { // 判断是否是文件
				file.delete(); // delete()方法 你应该知道 是删除的意思;
			} else if (file.isDirectory()) { // 否则如果它是一个目录
				File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
				for (int i = 0; i < files.length; i++) { // 遍历目录下所有的文件
					this.deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
				}
			}
			file.delete();
		} else {
			System.out.println("视频池文件不存在");
		}
	}
	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
	}


	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
//必须在surface创建后才能初始化MediaPlayer,否则不会显示图像
		player=new MediaPlayer();
		player.setAudioStreamType(AudioManager.STREAM_MUSIC);
		player.setDisplay(surfaceHolder);
		//设置显示视频显示在SurfaceView上
		try {
			player.setDataSource(outFileSrc);
			player.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(outFileSrc!=null){

			if(player.isPlaying()){
				player.stop();
			}
			player.release();
		}
		//Activity销毁时停止播放，释放资源。不做这个操作，即使退出还是能听到视频播放的声音
	}
}