/**
 * @Call_pickup.java
 *
 *
 * @author - Ashutosh Bhatt
 * @version 1.00 2012/5/21
 */
package tcs.com;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.sip.*;
import javax.servlet.sip.Proxy;
import javax.servlet.http.HttpSession;
import java.util.*;

import javax.servlet.sip.*;
import java.io.*;
import java.util.HashMap;

public class Call_pickup extends SipServlet {

		private TimerService timerService;
		private String TIMER_ID = "NOTIFY_TIMEOUT_TIMER";
		private static ServletContext context;
		private static SipFactory factory;
		private static SipApplicationSession sas;
		private static Proxy proxy;
		private URI uri;
		private URI uri2;


	public void init(ServletConfig config) throws ServletException
	{	super.init(config);
		factory=(SipFactory)(this.getServletContext().getAttribute(SipServlet.SIP_FACTORY));
		sas=factory.createApplicationSession();
		context=config.getServletContext();
		try
			{
				timerService = (TimerService)getServletContext().getAttribute("javax.servlet.sip.TimerService");
			}
	    catch(Exception e)
	    	{
	    		log ("Exception initializing the servlet "+ e);
	        }
	}

	protected void doInvite(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException,java.lang.IllegalStateException,ServletParseException
	{
		if (request.isInitial())
	     {
		   	System.out.println("Received an Invite Request: hello ashu");
		   	uri  =req.getFrom().getURI();
			uri2 =req.getTo().getURI();
			req.setRequestURI(uri2);
			System.out.println(uri2);
			proxy=req.getProxy(true);
			proxy.setRecordRoute(true);
			proxy.proxyTo(uri2);
			System.out.println("Sent an Invite Request");
	     }

	}
	protected void doSubscribe(SipServletRequest req) throws ServletException, IOException
	{
		req.createResponse(200).send();
	    req.getSession().createRequest("NOTIFY").send();
	    ServletTimer notifyTimeoutTimer = timerService.createTimer(req.getApplicationSession(), 3000,false, null);
	    req.getApplicationSession().setAttribute(TIMER_ID,notifyTimeoutTimer);

	}

	/*protected void doResponse(SipServletResponse res ) throws javax.servlet.ServletException,java.io.IOException
		{
			if(res.getRequest().getMethod().equals("BUSY") && res.getContent().equals("486"))
			{

			}
	}*/


	protected void doSuccessResponse(SipServletResponse res) throws javax.servlet.ServletException, java.io.IOException
	{
			int status=0;

			if(status == 0)
			  {
				if (res.getMethod().equals("NOTIFY"))
					 	{ ServletTimer notifyTimeoutTimer = (ServletTimer)(res.getApplicationSession().getAttribute(TIMER_ID));
					 		if (notifyTimeoutTimer != null)
					 		{
								notifyTimeoutTimer.cancel();
					 	  	 	res.getApplicationSession().removeAttribute(TIMER_ID);
					 		}
			 			}
		      }
		    else(status != 0)
		    {
    			System.out.println("Received a success reponse");
				if(resp.getRequest().getFrom().getURI().equals(uri))
		    	{
					proxy.proxyTo(uri);
				}
				else()
				{
					proxy.proxyTo(uri2);
					System.out.println("Sent the response");
				}
			}
	 }

	protected void doAck(SipServletRequest req) throws javax.servlet.ServletException,java.io.IOException
	{
	 	System.out.println("Received ack from sipphone");
		proxy.proxyTo(uri2);
		System.out.println("Forwarded the ack");
	}


	public void timeout(ServletTimer timer)
	{ // This indicates that the timer has fired because a 200 to
		    // NOTIFY was not received. Here you can take any timeout
		    // action.
	        // .........
		timer.getApplicationSession().removeAttribute("NOTIFY_TIMEOUT_TIMER");
	}

	protected void doNotify (SipServletRequest req) throws ServletException, IOException
	{
	  	req.createResponse(200).send();
	  	req.getSession().createRequest("NOTIFY").send();
	  	ServletTimer notifyTimeoutTimer = timerService.createTimer(req.getApplicationSession(), 3000, false, null);
	  	req.getApplicationSession().setAttribute(TIMER_ID, notifyTimeoutTimer);
	}

	protected void doProvisionalResponse(SipServletResponse resp) throws javax.servlet.ServletException,java.io.IOException
	{
		System.out.println("Received Ringing response");
		resp.addHeader("Alert-Info","http://100.1.1.15:9002/vxml/examples/helloworld.wav");
		proxy.proxyTo(uri);
		System.out.println("Forwarded the ringing response");

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