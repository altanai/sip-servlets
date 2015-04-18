/*
  Registrar  v2.0
  
  Purpose : Registrar to manage and store SIP Register Requests into Database 
  Author  : Altanai Bisht
  Author  : http://altanaitelecom.wordpress.com
  Date    : Feb 2013
*/


package com.tcs;

import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.sip.Proxy;
import javax.servlet.sip.ServletParseException;
import javax.servlet.sip.SipApplicationSession;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipSession;
import javax.servlet.sip.URI;

import java.io.IOException;

import javax.servlet.*;
import javax.servlet.sip.*;

import java.util.*;

import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import javax.servlet.sip.SipURI;
import javax.servlet.sip.SipServletMessage;



public class Registrar extends SipServlet {

	Connection conn;
	String url;
	String user;
	String password;
	Statement st;
	ResultSet rs;


	public SipFactory factory = null;
	public String server = null;
	public int port = 0;
	public String transport = null;

	public SipApplicationSession sessionwhole;
	public SipSession sessioncaller2b2bua;
	public SipSession sessionb2bua2callee;

	public static ServletContext context;
	public static Proxy proxy;

	public URI uricaller;
	public String strcaller;
	public URI uricallee;
	public String strcallee;

	public URI urisender;
	public URI urireceiver;
	public String strsender;
	public String strreceiver;

	int invitecounter=0;
	int ackcounter=0;
	int successcounter=0;

	SipServletRequest request;
	SipServletResponse response;
	SipServletRequest requestcopy;
	SipServletResponse responsecopy;

	SipServletRequest requestcallee;
	SipServletResponse responsecaller;


	String screenedname;
	String screeneduri;

	public void init(ServletConfig config)
	throws ServletException
	{
		
																		System.out.println("\n initiate function");
		super.init(config);
		context=config.getServletContext();
		factory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		sessionwhole= factory.createApplicationSession();
																		System.out.println("\n");
																		System.out.println("******************************************************");
																		System.out.println("*             Application session                    *");
																		System.out.println("******************************************************");
																		System.out.println("\n context "+ context);																	System.out.println("factory "+ factory);
																		System.out.println("\n session whole "+ sessionwhole.toString());
																		System.out.println("******************************************************");
	}

//................................................................................................................................................


	public void doRegister(SipServletRequest req)
  	{																	System.out.println("\n Register Request method : "+req.getMethod());
		try{															System.out.println("Request From : "+ req.getFrom().toString());
			req.createResponse(200).send();
		}catch(Exception e ){
			System.out.println("do register exception ");
		}
	}



}
