 package com.tcs.voice;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.sip.*;
import javax.servlet.sip.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpSession;
import java.util.*;

public class CallScreening extends SipServlet
{
	private static SipFactory factory;
	private static SipApplicationSession sas;
	private static Proxy proxy;
	private static ServletContext context;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		context=config.getServletContext();
		factory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		sas=factory.createApplicationSession();
	}
	
	protected void doInvite(SipServletRequest req) throws java.lang.IllegalArgumentException,java.lang.IllegalStateException,javax.servlet.ServletException,java.io.IOException
	{
		System.out.println("Received an Invite Request: CS");
		/*
		Iterator itr=sas.getSessions("HTTP");
		System.out.println("Iterator:   "+ itr+"\n\n");
		HttpSession hsession=(HttpSession)itr.next();;
		System.out.println(hsession + "\n");
		System.out.println(hsession.getAttribute("ScreenedUser"));
		*/
		String scuser=(String)context.getAttribute("ScreenedUser");
		System.out.println("Screened User:"+scuser);
		SipURI from=((SipURI)req.getFrom().getURI());
		if(from.getUser().equals(scuser))
		{
			req.createResponse(487).send();
			System.out.println("User is blocked");
			
		}
		else
		{
			//req.setRequestURI();
			System.out.println("User is not blocked New");
			Proxy px=req.getProxy(true);
			URI sburi=factory.createURI("sip:10.1.5.20:5090");
			px.proxyTo(sburi);
			System.out.println("User is not blocked");
		}
		
	}

	
	
}