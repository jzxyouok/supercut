package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class PersonInformation extends Activity {

	private Button fankui;
    private SharedPreferences.Editor edit ;
	private SharedPreferences share;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		
		share = super.getSharedPreferences("persondata",  MODE_PRIVATE);  
		edit= share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
		 String name=share.getString("username", "个人信息");
		 String phone=share.getString("phone", "电话号码");
		
		// 获取应用程序中的个人信息按钮
		Button bn_Personal_Information = (Button) findViewById(R.id.button_Personal_Information);
		bn_Personal_Information.setText(name);
		// 为个人信息按钮绑定事件监听器
		bn_Personal_Information.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View source)
			{
				// 创建需要启动的Activity对应的Intent
				Intent intent = new Intent(PersonInformation.this,Login.class);
				// 启动intent对应的Activity
				startActivity(intent);
			}
		});
		
		Button bn_Telephone_Number=(Button)findViewById(R.id.button_Telephone_Number);
		bn_Telephone_Number.setText(phone);
	
		fankui = (Button) findViewById(R.id.button_Advance_Feedback);//意见反馈
		fankui.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View source)
			{
				// 创建需要启动的Activity对应的Intent
				Intent intent = new Intent(PersonInformation.this,SendEmailActivity.class);
				// 启动intent对应的Activity
				startActivity(intent);
			}
		});
		Button bn_Quit=(Button)findViewById(R.id.button_Quit);
		bn_Quit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// 创建需要启动的Activity对应的Intent
				Intent intent = new Intent(PersonInformation.this,Register.class);
				// 启动intent对应的Activity
				startActivity(intent);
			}
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_work, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}