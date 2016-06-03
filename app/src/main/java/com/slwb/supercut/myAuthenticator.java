package com.slwb.supercut;

import javax.mail.Authenticator;
import javax.mail.PasswordAuthentication;

public class myAuthenticator extends Authenticator 
{
	    String userName=null;   
	    String password=null;   
	        
	    public myAuthenticator()
	    {   
	    }   
	    
	    public myAuthenticator(String username, String password) 
	    {
	        this.userName = username;    
	        this.password = password;    
	    }    
	    
	    protected PasswordAuthentication getPasswordAuthentication()
	    {   
	        return new PasswordAuthentication(userName, password);   
	    }  
}
