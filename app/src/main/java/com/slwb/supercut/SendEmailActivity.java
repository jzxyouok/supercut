package com.slwb.supercut;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class SendEmailActivity extends Activity {
	private Button send,back; 
	private EditText subject; 
	private EditText body;
    private int num;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sendmail);
        
        send = (Button) findViewById(R.id.send); 
        subject = (EditText) findViewById(R.id.subject); 
        body = (EditText) findViewById(R.id.body); 
        
       
        send.setOnClickListener(new View.OnClickListener() 
        {
            @Override 
            public void onClick(View v) 
            { 
                // TODO Auto-generated method stub                    
                try 
                { 
                	 mailSenderInfo mailInfo = new mailSenderInfo();    
                     /*
                      mailInfo.setMailServerHost("pop.126.com");    
                     mailInfo.setMailServerPort("110");    
                     mailInfo.setValidate(true);    
                     mailInfo.setUserName("supercut@126.com");  //你的邮箱地址  
                     mailInfo.setPassword("supercut.slwb");//您的邮箱密码    
                     mailInfo.setFromAddress("supercut@126.com");    
                     mailInfo.setToAddress("supercut@126.com");   
                      */
                     mailInfo.setMailServerHost("smtp.qq.com");    
                     mailInfo.setMailServerPort("25");    
                     mailInfo.setValidate(true);    
                     mailInfo.setUserName("229340465@qq.com");  //你的邮箱地址  
                     mailInfo.setPassword("15923458260");//您的邮箱密码    
                     mailInfo.setFromAddress("229340465@qq.com");    
                     mailInfo.setToAddress("229340465@qq.com");    
                     mailInfo.setSubject(subject.getText().toString());    
                     mailInfo.setContent(body.getText().toString());    
                     
                        //这个类主要来发送邮件   
                     simpleMailSender sms = new simpleMailSender();   
                     sms.sendTextMail(mailInfo);//发送文体格式    
                      //sms.sendHtmlMail(mailInfo);//发送html格式 

                    Intent f = new Intent();
     				f.setClass(SendEmailActivity.this, WorkStation.class);
     				startActivity(f);
                } 
                catch (Exception e) { 
                    Log.e("SendMail", e.getMessage(), e); 
                    System.out.println("发送失败，一会再试吧");
                }
            } 
        }); 
        
        back = (Button) findViewById(R.id.back_mail); 
        back.setOnClickListener(new View.OnClickListener() 
        {
            @Override 
            public void onClick(View v) 
            { 
                // TODO Auto-generated method stub       
            	finish();
            }
        });
    }
    
}