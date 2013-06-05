package com.tcs;

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

import java.util.logging.Logger;
import java.util.logging.Level;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

public class Musichold extends SipServlet{

	 private Logger  logger = Logger.getLogger("Musichold");

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


	public void init(ServletConfig config)
	throws ServletException
	{
																		 logger.log(Level.INFO, "------init-----");
		super.init(config);
		context=config.getServletContext();
		factory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		sessionwhole= factory.createApplicationSession();

																		logger.log(Level.INFO, "context "+ context);			
																		logger.log(Level.INFO,"factory "+ factory);
																		logger.log(Level.INFO,"session whole "+ sessionwhole.toString());
	}

	public void doInvite(SipServletRequest request)
	throws javax.servlet.ServletException,
			java.io.IOException,
			java.lang.IllegalStateException,
			ServletParseException
	{
																		logger.log(Level.INFO,"\n doinvite function");

			uricaller=request.getFrom().getURI();
			uricallee=request.getTo().getURI();

			strcaller=request.getFrom().getURI().toString();
			strcallee=request.getTo().getURI().toString();
																		logger.log(Level.INFO,"caller: "+strcaller);
																		logger.log(Level.INFO,"callee: "+strcallee);

			requestcopy=request;

			sessioncaller2b2bua= request.getSession();
																		logger.log(Level.INFO,"session caller to b2bua  "+sessioncaller2b2bua.toString());
			responsecaller = request.createResponse(180);
			responsecaller.send();

			requestcallee=factory.createRequest(sessionwhole,"INVITE",strcaller,strcallee);
			requestcallee.setContent(request.getContent(),request.getContentType());
			requestcallee.send();
																		logger.log(Level.INFO,"invite send to callee");
			sessionb2bua2callee=requestcallee.getSession();
																		logger.log(Level.INFO,"session  b2bua to callee  "+sessionb2bua2callee.toString());
																		logger.log(Level.INFO," exit invite ");


	}

    public void doResponse(SipServletResponse response)
	throws ServletException, IOException
	{
																		logger.log(Level.INFO,"\n doresponse function "+ response.getStatus() );
		if(response.getStatus() ==200){
			responsecopy=response;

			requestcallee= response.createAck();
			requestcallee.send();
																		logger.log(Level.INFO," ack send to callee ");


			responsecaller = requestcopy.createResponse(200);
			responsecaller.setContent(responsecopy.getContent(),responsecopy.getContentType());
			responsecaller.send();

																		logger.log(Level.INFO," 200 ok send to caller ");

																		logger.log(Level.INFO," exit doresponse function");


	}
	}



}
