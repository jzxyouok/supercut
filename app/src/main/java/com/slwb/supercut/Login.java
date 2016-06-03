package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class Login extends Activity
{
	private SharedPreferences share ;
    private SharedPreferences.Editor edit ;
    private EditText username,password;
    private Button denglu;
    private CheckBox check;
    private String user,pass;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		
		share = super.getSharedPreferences("persondata", MODE_PRIVATE);//实例化  
		//share=PreferenceManager.getDefaultSharedPreferences(this);//实例化SharedPreferences对象
		username=(EditText) findViewById(R.id.username_zhuce);
		password=(EditText) findViewById(R.id. password);
		denglu=(Button) findViewById(R.id. denglu);
		check=(CheckBox) findViewById(R.id. remember);//记住密码
		
		boolean judge=share .getBoolean("remember_password", false);//获取judge中的值，如果没有值则默认为false
        
        if(judge){
              String username_text= share.getString( "username", "" );
              String password_text= share.getString( "password", "" );
               username.setText(username_text);
               password.setText(password_text);
               check.setChecked( true);
       }
        denglu.setOnClickListener( new OnClickListener(){//登录时的数据处理
        	public void onClick(View source)
			{
        		  String username_text= username.getText().toString();
                  String password_text= password.getText().toString();
                 user=share.getString( "username", "" );
                 pass=share.getString( "password", "" );
        		if(username_text.equals(user )&&password_text.equals(pass)){
                    edit= share.edit();//实例化SharedPreferences的操作对象，使他可以操作数据的增删改查
                    if(check .isChecked()){
                           edit.putBoolean( "remember_password", true );//添加数据
                           edit.putString( "username", username_text);
                           edit.putString( "password", password_text);
                   } else{
                           edit.clear();//如果checkbox没有打钩，则进行消除数据
                   }
                    edit.apply();
                   Intent intent = new Intent(Login.this ,WorkStation.class);
                   startActivity(intent);
                   finish();
             } else{
                   Toast. makeText(Login.this, "账号或者密码错误", Toast.LENGTH_SHORT).show();
             }
			}
        });
		
		
		// 获取应用程序中的个人信息按钮
		Button bn_register = (Button) findViewById(R.id.button_register);
		// 为个人信息按钮绑定事件监听器
		bn_register.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View source)
			{
				// 创建需要启动的Activity对应的Intent
				Intent intent = new Intent(Login.this,Register.class);
				// 启动intent对应的Activity
				startActivity(intent);
			}
		});
	}

}