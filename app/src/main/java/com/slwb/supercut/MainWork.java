
		package com.slwb.supercut;

		import android.app.Activity;
		import android.content.BroadcastReceiver;
		import android.content.Context;
		import android.content.Intent;
		import android.content.IntentFilter;
		import android.content.SharedPreferences;
		import android.graphics.Bitmap;
		import android.graphics.Canvas;
		import android.graphics.Color;
		import android.graphics.Paint;
		import android.graphics.PorterDuff;
		import android.graphics.PorterDuffXfermode;
		import android.graphics.Rect;
		import android.graphics.drawable.BitmapDrawable;
		import android.media.MediaMetadataRetriever;
		import android.media.MediaScannerConnection;
		import android.net.Uri;
		import android.os.Bundle;
		import android.os.Environment;
		import android.util.Log;
		import android.view.Display;
		import android.view.Gravity;
		import android.view.LayoutInflater;
		import android.view.SurfaceHolder;
		import android.view.SurfaceView;
		import android.view.View;
		import android.view.View.OnClickListener;
		import android.view.WindowManager;
		import android.view.WindowManager.LayoutParams;
		import android.widget.Button;
		import android.widget.PopupWindow;
		import android.widget.RelativeLayout;
		import android.widget.Toast;

		import com.slwb.mediachooser.MediaChooser;
		import com.slwb.mediachooser.activity.HomeFragmentActivity;

		import java.io.File;
		import java.util.ArrayList;
		import java.util.List;

public class MainWork extends Activity {

	//设置特效的图标显示
	private static final 	int[] ITEM_DRAWABLES = {R.drawable.cutwy,
			R.drawable.titlewy, R.drawable.lvjingwy, R.drawable.deletewy};
	private Button 	ok, back, get, play, help,button_up;
	private SurfaceView 	surface;
	private SurfaceHolder 	holder;
	private Bitmap 	showBitmap,showBitmapCatch;
	private int 	viewWidth, viewHeight;
	private Rect rectmap,rectview;
	private Thumbnail thumbnail=new Thumbnail();
	private int 	screenWidth,screenHeight;
	private int 	LineOnFrame=20,LineOnFrameBefor=10;
	private int 	IT = 11;
	private int 	ITC[] = {15, 16, 17, 18};
	private String 	hits[] = {"剪切", "字幕", "滤镜", "删除"};
	private String 	filepath = null;
	private String 	showpath = null;
	private RelativeLayout 	tools;
	private MediaMetadataRetriever 	retriever;
	private editWithJava 	ewj = new editWithJava();        //剪辑的函数调用
	private List<String> 	listFile = new ArrayList<String>();   //最终视频List
	private boolean isVideoFilesNull = true;
	private ThumbnailView 	thumbnailView;
	private stringUtil 	su = new stringUtil();//格式化字符的类
	private String 	outsrc = null;//输出地址
	private finalCompose finalCompose;
	private boolean isHelp = false;
	private SharedPreferences.Editor edit;
	private SharedPreferences 	share;
	private RelativeLayout 	helplayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.video_edit);

		share = super.getSharedPreferences("persondata", MODE_PRIVATE);
		edit = share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查

		WindowManager windowManager = getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		screenWidth = display.getWidth();
		screenHeight = display.getHeight();

		thumbnailView = (ThumbnailView) findViewById(R.id.thumbnailView);
		thumbnailView.setWH(screenWidth, screenHeight);
		System.out.println("Width+Height:" + thumbnailView.width + "+" +
				thumbnailView.height);



		/*
		//向上弹出工具栏
		button_up=(Button)findViewById(R.id.button_up);
		button_up.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				pop();
				}

		});
		*/

		//帮助浮层
		helplayout = (RelativeLayout) findViewById(R.id.helpLayout);
		helplayout.setAlpha(0);
		help = (Button) findViewById(R.id.help);
		help.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				if (isHelp) {
					helplayout.setAlpha(0);
					isHelp = false;
				} else if (!isHelp) {
					helplayout.setAlpha(1);
					isHelp = true;
				}
			}
		});

		//
		ok = (Button) findViewById(R.id.ok);
		ok.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				int n = thumbnailView.getVideoFiles().size();
				listFile = thumbnailView.getVideoFiles();
				finalCompose = new finalCompose();
				finalCompose.setFinalCompose(thumbnailView.getCutxml(), n);
				finalCompose.Merge();

				//ewj.merge(listFile, finalCompose.outsrc);
				updateGallery(finalCompose.outsrc);//更新媒体库
				outsrc = finalCompose.outsrc;
				Intent f = new Intent();
				f.setClass(MainWork.this, WorkStation.class);
				edit.putString("outSrc", outsrc);
				edit.commit();    //提交数据保存
				startActivity(f);

			}
		});
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});

		//选择素材
		get = (Button) findViewById(R.id.get);
		get.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {

				Intent intent = new Intent(MainWork.this, HomeFragmentActivity.class);
				startActivity(intent);
			}
		});

		IntentFilter videoIntentFilter = new IntentFilter(MediaChooser.VIDEO_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(videoBroadcastReceiver, videoIntentFilter);

		IntentFilter imageIntentFilter = new IntentFilter(MediaChooser.IMAGE_SELECTED_ACTION_FROM_MEDIA_CHOOSER);
		registerReceiver(imageBroadcastReceiver, imageIntentFilter);

		//播放预览
		surface = (SurfaceView) findViewById(R.id.showing);
		holder=surface.getHolder();
		holder.addCallback(new DoShowing());

		retriever = new MediaMetadataRetriever();


		play = (Button) findViewById(R.id.play);

		play.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (isVideoFilesNull == false) {
					try {

					} catch (Exception e) {
						e.printStackTrace();
					}
					System.out.println("showPath:" + showpath);
					if (showpath != null) {

					}
				}
			}
		});

		newfolder();//创建"剪影"文件夹


	}

	BroadcastReceiver videoBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			//String path;
			listFile = intent.getStringArrayListExtra("list");
			//获取略略缩图
			thumbnailView.addVideo(listFile);
			thumbnailView.invalidate();//刷新画面
			isVideoFilesNull = false;

		}
	};
	BroadcastReceiver imageBroadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			filepath = intent.getStringArrayListExtra("list").toString();
			filepath = filepath.substring(1, filepath.length() - 1);

		}
	};

	//弹出菜单
	public void pop() {

		tools = (RelativeLayout) findViewById(R.id.tools);
		LayoutInflater inflater = LayoutInflater.from(this);
		// 引入窗口配置文件
		View view = inflater.inflate(R.layout.tools, null);
		// 创建PopupWindow对象
		final PopupWindow pop = new PopupWindow(view, LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT, false);
		pop.setBackgroundDrawable(new BitmapDrawable());
		//设置点击窗口外边窗口消失
		pop.setOutsideTouchable(true);
		// 设置此参数获得焦点，否则无法点击
		pop.setFocusable(true);
		pop.setAnimationStyle(R.style.AnimationFade);

		if (pop.isShowing()) {// 隐藏窗口，如果设置了点击窗口外小时即不需要此方式隐藏
			pop.dismiss();
		} else {
			// 显示窗口
			pop.showAtLocation(findViewById(R.id.tools), Gravity.BOTTOM, 0, 0);
		}




	}

	class DoShowing implements SurfaceHolder.Callback{
		@Override
		public void surfaceChanged(final SurfaceHolder holder, int format, final int width,
								   int height) {
			// TODO Auto-generated method stub
			new Thread() {
				public void run() {
					while (true) {
						// TODO Auto-generated method stub
						Canvas c = holder.lockCanvas();
						showpath = thumbnailView.getShowpath();
						//2.开画
						Paint p = new Paint();
						p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));

						Paint text=new Paint();
						text.setColor(Color.BLUE);
						text.setTextSize(20);
						if (c != null && showpath != null) {
							//显示在画布上
							LineOnFrame=thumbnailView.getLineOnFrame();

							try{
								thumbnail.setPath(showpath);
								showBitmap = thumbnail.getFrameOn(LineOnFrame);
								showBitmapCatch=showBitmap;
								rectmap  = new Rect(0,0,showBitmap.getWidth(),showBitmap.getHeight());
								//预览视频的高度
								int h = (int)((showBitmap.getHeight()*surface.getWidth())/(float)showBitmap.getWidth());
								//
								rectview = new Rect(0,(int) ((surface.getHeight()-h)/2.0),surface.getWidth(),
										(int)((surface.getHeight()-h)/2.0)+h);
							}catch (NullPointerException e){
								showBitmap=showBitmapCatch;
								rectmap	 = new Rect(0,0,surface.getWidth(),surface.getHeight());
								rectview=rectmap;
							}
								//
								c.drawPaint(p);
								p.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC));
								c.drawBitmap(showBitmap, rectmap, rectview,p);
								LineOnFrameBefor=LineOnFrame;

							//3. 解锁画布   更新提交屏幕显示内容
							holder.unlockCanvasAndPost(c);
							try {
								Thread.sleep(1000/30);
							} catch (Exception e) {
							}
						} else if (c != null) {
							holder.unlockCanvasAndPost(c);
						}
					}
				}
			}.start();
		}

		@Override
		public void surfaceCreated(final  SurfaceHolder holder) {

		}
		@Override
		public void surfaceDestroyed(SurfaceHolder holder) {
			// TODO Auto-generated method stub

		}
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(imageBroadcastReceiver);
		unregisterReceiver(videoBroadcastReceiver);
		super.onDestroy();
	}

	public void newfolder() {
		try {
			File file = new File(Environment.getExternalStorageDirectory()  + "/剪影/作品");
			if (!file.exists()) {
				file.mkdirs();
			}

			File file2 = new File(Environment.getExternalStorageDirectory()  + "/剪影/视频池");
			if (!file2.exists()) {
				file2.mkdirs();
			}
		}
		catch (Exception e) {

			this.runOnUiThread(new Runnable() {
				public void run() {
					Toast.makeText(getBaseContext(), "保存失败", Toast.LENGTH_SHORT)
							.show();
				}
			});
			e.printStackTrace();
		}
	}
	//filename是我们的文件全名，包括后缀
	private void updateGallery(String filename)
	{
		MediaScannerConnection.scanFile(this,
				new String[]{filename}, null,
				new MediaScannerConnection.OnScanCompletedListener() {
					public void onScanCompleted(String path, Uri uri) {
						Log.i("ExternalStorage", "Scanned " + path + ":");
						Log.i("ExternalStorage", "-> uri=" + uri);
					}
				});
	}
}
