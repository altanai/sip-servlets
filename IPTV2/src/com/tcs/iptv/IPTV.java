package com.tcs.iptv;
import com.tcs.*;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.*;


public class IPTV extends SipServlet implements Service
{
	SipApplicationSession sas;
	private SipFactory factory;
  	private static final Log log = LogFactory.getLog(IPTV.class);
  	String serviceID="IPTV";
  	SipServletRequest incoming;
  	int nextReqID=1;
  	protected HashMap incomingRequests = new HashMap();
  	protected ArrayList<Call> runningSessions = new ArrayList<Call>();
  	long timer;
  	ServletContext context;
  	SipServletRequest ack;
  	protected HashMap callStates = new HashMap();
  	protected HashMap callLegs = new HashMap();
  	protected Boolean ready=true;
  	CallLeg leg;
  	int check=0;
  	public void init(ServletConfig config) throws ServletException
  	{
  			System.out.println("Entered INIT");
  			log.info("Init.");
			super.init(config);
			factory = (SipFactory) config.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
			sas=factory.createApplicationSession();
			context=config.getServletContext();
			context.setAttribute("servlet",this);
			registerWithBroker();
			System.out.println("Exit INIT");
  	}
  	public void registerWithBroker()
  	{
  		try
  		{
  			
  			System.out.println("Register request craeted");
  			SipServletRequest register=factory.createRequest(sas,"MESSAGE","sip:iptv@100.1.1.15:5061","sip:100.1.1.9:3686");
  			register.setHeader("ServiceID",serviceID);
  			register.setHeader("Type","Register");
  			register.send();
  			System.out.println("Register req. sent");
  			}
  		
  		catch(Exception e)
  		{
  			System.out.println("IPTV:113 ");
  		}
  	}
  	protected void doRequest(SipServletRequest req)
  	{
  		try
  		{
  			synchronized(ready){ 
  			ready=false;
  			if(req.getMethod().equals("INVITE") && req.isInitial()){
  				incomingRequests.put(""+nextReqID, req);
  				SipServletRequest query=factory.createRequest(sas,"MESSAGE","sip:iptv@100.1.1.15:5061","sip:100.1.1.9:3686");
  				query.setHeader("ReqID",""+nextReqID++);
  				query.setHeader("Type","Query");
  				query.setHeader("User","bob");
  				query.send();
  				runningSessions.clear();
  			}
  				else if(req.getMethod().equals("INVITE") && !(req.isInitial()))
  			{
  				System.out.println("Got a  reinvite request");
  				req.createResponse(487).send();
  				System.out.println("Terminated the reinvite request");
  			}
  			else if(req.getMethod().equals("ACK") && (Integer.parseInt(req.getHeader("CSeq").split(" ")[0])>1)){
  				System.out.println("Received acknowledgement for reinvite");
  				
  			}
  			else if(req.getMethod().equals("MESSAGE")){
  				String user=req.getHeader("User");
  				if(req.getHeader("Type").equalsIgnoreCase("Stop"))
  				{
  					System.out.println("Stop request received");
  					stop(user);
  					req.createResponse(200).send();
  				}
  				else if(req.getHeader("Type").equalsIgnoreCase("Pause"))
  				{
  					System.out.println("Pause request received");
  					req.createResponse(200).send();
  					hold(user);
  					
  				}
  				if(req.getHeader("Type").equalsIgnoreCase("Continue"))
  				{
  					System.out.println("Resume request received");
  					resume(user);
  					req.createResponse(200).send();
  				}
  			}
  			else if(req.getMethod().equals("ACK")){
  				
  				ack.setContent(req.getContent(),req.getContentType());
  				ack.send();
  				((CallLeg)callLegs.get(req.getCallId())).transferReq(req);
  				System.out.println("Session Id in ACK:"+req.getSession().getId());
  			}
  			else{
  				((CallLeg)callLegs.get(req.getCallId())).transferReq(req);
  			}
  			ready=true;
  			}
  				
  			}
  			
  		
  		catch(Exception e)
  		{
  			System.out.println(e);
  		}
  	}
  	 protected void doResponse(SipServletResponse res)
  	 {
  	 	try
  	 	{
  	 		synchronized(ready){ 
  			ready=false;
  	 		if(res.getRequest().getMethod().equals("MESSAGE"))
  	 		
  	 		{
				if(res.getContent()!= null && res.getContent().equals("OK"))
				{
					System.out.println("Handling in OK REsponse for QUERY MESSAGE");
  					SipServletRequest req=factory.createRequest(sas,"INVITE","sip:iptv@100.1.1.15:5061","sip:1@100.1.1.15:5070");
  					String reqId=res.getHeader("ReqID");
  					SipServletRequest incoming=(SipServletRequest)incomingRequests.get(reqId); 
  					req.setContent(incoming.getContent(), incoming.getContentType());
  					CallLeg leg1=new CallLeg(incoming,factory,log);
					CallLeg leg2=new CallLeg(req,factory,log);
					leg1.otherLeg=leg2;
					leg2.otherLeg=leg1;
					
					leg1.state="Invited";
					callLegs.put(leg1.session.getAttribute("CallID"),leg1);
					callLegs.put(leg2.session.getAttribute("CallID"),leg2);
					
					leg2.startCall();
					Call call=new Call();
					call.add("bob",leg1);
					System.out.println(leg1.session.getId());
					runningSessions.add(call);
					System.out.println("Handled OK response for query message");
					System.out.println("session id for bob="+leg1.session.getId());
				
				} 
				else if(res.getContent()!= null && res.getContent().equals("Stop"))
  				{
  					System.out.println("Received stop for query request");
  					String user=res.getHeader("User");
  					stop(user);
  				}
				else if(res.getContent()!= null && res.getContent().equals("Pause"))
  				{	
  					System.out.println("Received pause for query request");
  					String user=res.getHeader("User");
  					hold(user);
  				} 
  				else if(res.getContent()!= null && res.getContent().equals("Continue"))
  				{
  					System.out.println("Received continue for query request");
  				}			
  	 		}
  	 		else if(res.getRequest().getMethod().equals("INVITE") && (res.getStatus()<300 && res.getStatus()>199))
  	 		{
  	 			System.out.println("Received OK response for invite");
  	 			ack=res.createAck();
  	 			if(res.getRequest().getHeader("reinvite")!=null && res.getRequest().getHeader("reinvite").equals("yes"))
  	 			{
  	 				check++;
  	 				ack.setContent(res.getContent(),res.getContentType());
  					ack.send();
  					System.out.println("sent ack");
  					if(check==2)
  					{
  						leg.session.createRequest("BYE").send();
  						check=0;
  					}
  				}
  	 			((CallLeg)callLegs.get(res.getRequest().getCallId())).transferRes(res);
  			}
  			else if(res.getRequest().getMethod().equals("BYE"))
  			{
  				leg.session.invalidate();
  			}
  			
  			ready=true;
  			}
  			
  	 	}
  	 	catch(Exception e)
  	 	{
  	 		System.out.println("IPTV 158:"+e.toString());
  	 	}
  	 }
  	 public void hold(String user)
  	 {
  	 	
  	 	try
  	 	{
  	 		CallLeg leg1;
  	 		CallLeg leg2;
  	 		System.out.println("Received hold request");
      		for(Call c: runningSessions)    
  	 		{
  	 			if(c.getUserName().equals(user))
  	 			{
  	 				System.out.println("entered hold function for "+c.getUserName());
  	 				
  	 				c.calculateTimeGap();
  	 				leg1=c.getCallLeg();
  	 				leg2=leg1.otherLeg;
  	 				System.out.println("Session Id in hold:"+leg1.session.getId());
  	 				SipServletRequest newCallerReq=leg1.session.createRequest("INVITE");
  	 				newCallerReq.setHeader("reinvite","yes");
  	 				System.out.println("Created reinvite req");
  	 				SipServletRequest newMediaReq=factory.createRequest(sas,"INVITE","sip:iptv@100.1.1.15:5061","sip:2@100.1.1.15:5070");
  	 				newMediaReq.setHeader("reinvite","yes");
  	 				CallLeg callerLeg=new CallLeg(newCallerReq,factory,log);
  	 				CallLeg mediaLeg=new CallLeg(newMediaReq,factory,log);
  	 				callLegs.put(callerLeg.session.getAttribute("CallID"),callerLeg);
  	 				callLegs.put(mediaLeg.session.getAttribute("CallID"),mediaLeg);
  	 				callerLeg.otherLeg=mediaLeg;
  	 				mediaLeg.otherLeg=callerLeg;
  	 				mediaLeg.startCall();
  	 				leg=leg2;
  	 				System.out.println("Completed hold function");
  	 				
  	 			}
  	 			
  	 		}
  	 		
  	 		
  	 	}
  	 	catch(Exception e)
  	 	{
  	 		System.out.println("IPTV 260"+ e.toString());
  	 	}
  	 }
  	 
  	 public void resume(String user)
  	 {
  	 	try
  	 	{
  	 		CallLeg leg1;
  	 		CallLeg leg2;
      		for(Call c: runningSessions)    
  	 		{
  	 			if(c.getUserName().equals(user))
  	 			{
  	 				leg1=c.getCallLeg();
  	 				leg2=leg1.otherLeg;
  	 				SipServletRequest req1=leg1.session.createRequest("INVITE");
  	 				CallLeg leg3=new CallLeg(req1,factory,log);
  	 				SipServletRequest req2=factory.createRequest(sas,"INVITE","sip:iptv@100.1.1.15:5061","sip:1@100.1.1.15:5070");
  	 				CallLeg leg4=new CallLeg(req2,factory,log);
  	 				leg3.otherLeg=leg4;
  	 				leg4.otherLeg=leg3;
  	 				leg2.currentReq=leg2.session.createRequest("BYE");
  	 				leg2.currentReq.send();
  	 				leg3.startCall();
  	 				c.setCallLeg(leg3);
  	 				timer=c.returnTimeGap();
  	 				getServletContext().setAttribute("timer",timer);
  	 				break;
  	 			}
  	 		}
  	 		
  	 		
  	 	}
  	 	catch(Exception e)
  	 	{
  	 	}
  	 }
  	 public void stop(String user)
  	 {
  	 	try
  	 	{
  	 		for(Call c:runningSessions)
  			{
  				if(c.getUserName().equals(user))
  	 			{
  	 				CallLeg leg1=c.getCallLeg();
  	 				CallLeg leg2=leg1.otherLeg;
  	 				SipServletRequest req1=leg1.session.createRequest("BYE");
  	 				leg1.currentReq=req1;
  	 				req1.send();
  	 				SipServletRequest req2=leg2.session.createRequest("BYE");
  	 				leg2.currentReq=req2;
  	 				req2.send();
  	 				SipServletRequest update=factory.createRequest(sas,"MESSAGE","sip:iptv@100.1.1.15:5061","sip:100.1.1.15:5061");
					update.setHeader("ServiceID",serviceID);
					update.setHeader("Type","Remove");
					update.setHeader("User",user);
					update.send();
  	 				break;
  	 				
  	 			}
  			}
  	 	}
  	 	catch(Exception e)
  	 	{
  	 		
  	 	}
  	 }
}