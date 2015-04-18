/*
  Parental Control  v2.0
  
  Purpose : Parental Control after fetching values from database 
  Author  : Altanai Bisht
  Author  : http://altanaitelecom.wordpress.com
  Author  : TCSL
  Date    : Feb 2013
 */

package com.tcs;
import java.io.IOException;
import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;;


public class ParentalControl extends SipServlet
{
	SipApplicationSession sas;
	private SipFactory factory;
	SipServletResponse response1;
	SipServletResponse response2;
	SipServletRequest request1;
	SipSession session;
	SipSession session1;
	SipSession session2;

  	public void init(ServletConfig config) throws ServletException
  	{
  			System.out.println("Entered INIT");
			super.init(config);
			factory = (SipFactory) config.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			sas=factory.createApplicationSession();
			System.out.println("Exit INIT");
  	}


  	protected void doRequest(SipServletRequest req)
  	{

		try
		{
			if(req.getMethod().equals("INVITE"))
			{
				System.out.println("Got Invite from child- test 2 ");

				request1=req;
				session1=req.getSession();

				SipServletResponse resp1=req.createResponse(180);
				resp1.setHeader("Alert-Info","http://10.1.5.15:180/helloworld.wav");
				resp1.send();
				System.out.println("send ringing resposne to child ");
				SipServletRequest req1=factory.createRequest(sas,"INVITE","sip:control@10.1.5.15:5061","sip:bob1@10.1.5.15:5061");
				req1.send();
				session=req1.getSession();

				System.out.println("send invite to parent and Exiting Invite Method");
			}
		
			else if(req.getMethod().equals("REFER"))
			{
				System.out.println("Entered Refer");
		
						if(req.getHeader("Refer-To").contains("sip:1@10.1.5.15:5061"))
						{
							System.out.println(" Refer- yes");
							SipServletRequest req1=factory.createRequest(sas,"INVITE","sip:control@10.1.5.15:5061","sip:13@10.1.5.20:5070");
							req1.setContent(request1.getContent(),request1.getContentType());
							req1.send();
							session2=req1.getSession();
						}
						
						else if(req.getHeader("Refer-To").contains("sip:2@10.1.5.15:5061"))
						{
							System.out.println(" Refer - No");
							SipServletRequest req1=factory.createRequest(sas,"INVITE","sip:control@10.1.5.15:5061","sip:14@10.1.5.20:5070");
							req1.setContent(request1.getContent(),request1.getContentType());
							req1.send();
							session2=req1.getSession();
						}
				
				req.createResponse(200).send();
				session.createRequest("BYE").send();

			}
			else if(req.getMethod().equals("BYE") && req.getFrom().toString().contains("10.1.5.20:5070"))
			{
				req.createResponse(200).send();
				session1.createRequest("BYE").send();

			}
			else if(req.getMethod().equals("BYE") && req.getFrom().toString().contains("bob2"))
			{
				req.createResponse(200).send();
				session2.createRequest("BYE").send();

			}
			else if(req.getMethod().equals("BYE"))
			{
				req.createResponse(200).send();
			}
			else if(req.getMethod().equals("ACK"))
			{
				System.out.println("Received Ack");
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void doSuccessResponse(SipServletResponse resp)
  	{
		//System.out.println(resp.getTo().toString());
		
		if(resp.getRequest().getMethod().equals("INVITE") && resp.getTo().toString().contains("sip:bob1"))
		{
			System.out.println("Received Success Response for INVITE");

			try
			{
				response1=resp;
				SipServletRequest req2=factory.createRequest(sas,"INVITE","sip:control@10.1.5.15:5061","sip:5@10.1.5.15:5070");
				req2.setContent(resp.getContent(),resp.getContentType());
				System.out.println("Request to Voxpilot:\n"+req2);
				req2.send();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			System.out.println("Sent Ack for success Response");
		}
		
		else if(resp.getRequest().getMethod().equals("INVITE") && resp.getTo().toString().contains("sip:5@10.1.5.15:5070"))
		{
			try
			{
				SipServletRequest ack1=response1.createAck();
				ack1.setContent(resp.getContent(),resp.getContentType());
				resp.createAck().send();
				ack1.send();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}
		
		else if(resp.getRequest().getMethod().equals("INVITE") && resp.getTo().toString().contains("10.1.5.20:5070"))
		{
			try
			{
				response2=resp;
				SipServletResponse resp1=request1.createResponse(200,"OK");
				resp1.setContent(resp.getContent(),resp.getContentType());
				resp1.send();
				resp.createAck().send();
			}
			catch(Exception e)
			{
				System.out.println(e);
			}
		}

	}




}