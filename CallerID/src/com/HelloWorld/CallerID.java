package com.HelloWorld;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.sip.*;
import java.io.*;
import javax.servlet.sip.SipFactory;
import javax.servlet.sip.SipServlet;
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CallerID extends SipServlet {

   private SipFactory factory;
   private SipApplicationSession sas;
   private Proxy proxy;
   private URI uri;
   private URI uri2;

public void init(ServletConfig config) throws ServletException
   	{
		super.init(config);
		factory = (SipFactory) getServletContext().getAttribute(SipServlet.SIP_FACTORY);
	}

protected void doInvite(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException,java.lang.IllegalStateException,ServletParseException
	{
		System.out.println("Received an Invite Request:\n"+req);
		sas = factory.createApplicationSession();
		uri=req.getFrom().getURI();
		uri2=req.getTo().getURI();
		req.addHeader("Alert-Info","http://10.1.5.15:9002/audio/"+getFileName());
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
	//	resp.addHeader("Alert-Info","http://localhost:9002/vxml/examples/helloworld.wav");
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

public String getFileName(){
		System.out.println("In File Name function");
		File dir = new File( "C:\\Voxpilot\\vxmlinterpreter\\www\\audio" );
		System.out.println(dir.isDirectory());
		File af=null, list[]=dir.listFiles();
		long mod=list[0].lastModified();
		for(int i=0; i<list.length; i++){
		File f=list[i];
		long md=f.lastModified();
		System.out.println("In loop");
		if(mod<=md){
		mod=md; af=f;
		}

		}
		System.out.println(af.getName());
		return af.getName();
	}

}
