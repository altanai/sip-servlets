package com.tcs.voice;

import java.io.IOException;
import javax.servlet.*;
import javax.servlet.sip.*;
import javax.servlet.sip.Proxy;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.*;

public class CallSimple extends SipServlet
{
	private static SipFactory factory;
	private static SipApplicationSession sas;
	private static Proxy proxy;
	private URI uri;
	private URI uri2;
	
	public void init(ServletConfig config) throws ServletException
	{
		super.init(config);
		factory=(SipFactory)(this.getServletContext().getAttribute(SipServlet.SIP_FACTORY));
		sas=factory.createApplicationSession();
		
	}
	
	
	protected void doInvite(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException,java.lang.IllegalStateException,ServletParseException
	{
		System.out.println("Received an Invite Request: hello");
		uri=req.getFrom().getURI();
		uri2=req.getTo().getURI();
		//req.setRequestURI(uri2);
		System.out.println(uri2);
		proxy=req.getProxy(true);
		proxy.setRecordRoute(true);
		proxy.proxyTo(uri2);
		System.out.println("Sent an Invite Request");
	}
	protected void doProvisionalResponse(SipServletResponse resp) throws javax.servlet.ServletException,java.io.IOException
	{
		System.out.println("Received Ringing response");
		resp.addHeader("Alert-Info","http://100.1.1.15:9002/vxml/examples/helloworld.wav");
		proxy.proxyTo(uri);
		System.out.println("Forwarded the ringing response");
		
	}
	protected void doSuccessResponse(SipServletResponse resp) throws javax.servlet.ServletException,java.io.IOException
	{
		System.out.println("Received a success reponse");
		if(resp.getRequest().getFrom().getURI().equals(uri))
		proxy.proxyTo(uri);
		else
		proxy.proxyTo(uri2);
		System.out.println("Sent the response");
	}
	protected void doAck(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException
	{
		System.out.println("Received ack from sipphone");
		proxy.proxyTo(uri2);
		System.out.println("Forwarded the ack");
	}
	protected void doBye(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException
	{
		proxy=req.getProxy(true);
		System.out.println("Received a Bye request");
		if(req.getFrom().getURI().equals(uri))
		{
			proxy.proxyTo(uri2);
		}
		else
		{
			proxy.proxyTo(uri);
		}
		
		System.out.println("Forwarded the Bye request");
	}
	
}