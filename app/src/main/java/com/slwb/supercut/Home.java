package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.slwb.bannerview.CircleFlowIndicator;
import com.slwb.bannerview.ViewFlow;

import java.util.ArrayList;
/*
**这是首页
*/
public class Home extends Activity  {
	private ViewFlow mViewFlow;
	private Button  newProject,workstation;
	private CircleFlowIndicator mFlowIndicator;
	private SharedPreferences.Editor edit ;
	private SharedPreferences cutxml ;
	private SharedPreferences share ;
	private SharedPreferences.Editor edit2 ;
	private ArrayList<String> imageUrlList = new ArrayList<String>();
	private ArrayList<String> linkUrlArray= new ArrayList<String>();

	private ArrayList<String> titleList= new ArrayList<String>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.home);
		initView();

		cutxml = super.getSharedPreferences("cutxml", MODE_PRIVATE);  //保存剪辑顺序
		edit= cutxml.edit();	//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
		edit.putInt("num", 0);
		edit.commit();

		share = super.getSharedPreferences("persondata", MODE_PRIVATE);//实例化
		edit2=share.edit();
		edit2.putString("outSrc",null);
		edit2.commit();

		imageUrlList
				.add("http://g.hiphotos.baidu.com/image/pic/item/b219ebc4b74543a904d3ba6b1d178a82b8011448.jpg");
		imageUrlList
				.add("http://b.hiphotos.baidu.com/image/pic/item/242dd42a2834349b8a7292cccbea15ce36d3be07.jpg");
		imageUrlList
				.add("http://a.hiphotos.baidu.com/image/pic/item/ac6eddc451da81cbf1a6b9c35066d0160924317f.jpg");
		imageUrlList
				.add("http://d.hiphotos.baidu.com/image/pic/item/f2deb48f8c5494ee0359a5452ff5e0fe99257e77.jpg");

		initBanner(imageUrlList);


		newProject = (Button) findViewById(R.id.newproject);
		newProject.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent f = new Intent();
				f.setClass(Home.this, MainWork.class);
				startActivity(f);
            }
		});
		
		workstation = (Button) findViewById(R.id.zimu);
		workstation.setOnClickListener(new OnClickListener() { 
            public void onClick(View v) { 
            	Intent f = new Intent();
				f.setClass(Home.this, WorkStation.class);
				startActivity(f);
            }
		});
		}
	   
	   private void initView() {
			mViewFlow = (ViewFlow) findViewById(R.id.viewflow);
			mFlowIndicator = (CircleFlowIndicator) findViewById(R.id.viewflowindic);
		}

		private void initBanner(ArrayList<String> imageUrlList) {
			mViewFlow.setAdapter(new imagePagerAdapter(this, imageUrlList).setInfiniteLoop(true));
			mViewFlow.setmSideBuffer(imageUrlList.size());  	// 实际图片张数，
																// 我的ImageAdapter实际图片张数为3
			
			mViewFlow.setFlowIndicator(mFlowIndicator);
			
			mViewFlow.setTimeSpan(4500);
			mViewFlow.setSelection(imageUrlList.size() * 1000); // 设置初始位置
			mViewFlow.startAutoFlowTimer(); // 启动自动播放
		}
	
}