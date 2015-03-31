package com.tcs;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CallLeg {
public SipServletRequest initial;
public SipServletRequest currentReq;
public SipServletResponse currentRes;
public SipSession session;
public SipFactory factory;
public CallLeg otherLeg;
public String state;
public Log log;


public CallLeg(SipServletRequest req, SipFactory fact, Log log){
	System.out.println("Entered Constructor of callleg");
	initial=req;
	session=req.getSession();
	currentReq=req;
	currentRes=null;
	factory=fact;
	state="Prepared";
	this.log=log;
	session.setAttribute("CallID",req.getCallId());
	session.setAttribute("Type","Call");
	System.out.println("Exiting Constructor of callleg");
}


public void startCall(){
	try
	{
		System.out.println("Entered startcall for callLeg");
		initial.send();
		state="Started";
		System.out.println("Started a Cal for callLeg\n");
	}
	catch(Exception e)
	{
		log.info("CallLeg:37 "+e.toString());
	}
}

public void transferReq(SipServletRequest req){
	currentReq=req;
	try{
	if(req.getMethod().equals("ACK")){
	System.out.println("Received ACK ");
	/*SipServletRequest ack=otherLeg.currentRes.createAck();
	System.out.println("Created ack");
	//System.out.println(otherLeg.currentRes);
	ack.setContent(req.getContent(), req.getContentType());
	System.out.println("Set the contents of ack");
	ack.send();*/
	System.out.println("sent ack");
	state="Connected";
	otherLeg.state="Connected";
	System.out.println("Processed ack");
	}
	else if(req.getMethod().equals("BYE")){
		System.out.println("Got a bye request");
	req.createResponse(200).send();
	otherLeg.session.createRequest("BYE").send();
	System.out.println("sent a bye request to other leg as well");
	//session.invalidate();

	}
	}
	catch(Exception e){
		log.info("Line 55CallLeg: "+e.toString());
	}
	
}

public void transferRes(SipServletResponse res){
	currentRes=res;
	try{
		if(res.getStatus()>=200 && res.getStatus()<300){
			if(currentReq.getMethod().equals("BYE")){
				System.out.println("Invalidated the session for 2nd leg");
				//session.invalidate();
				return;
			}
			if(otherLeg.state.equals("Prepared")){
				otherLeg.initial.setContent(res.getContent(),res.getContentType());
				otherLeg.startCall();
				log.info("Second Leg Started");
			}
			else if(otherLeg.state.equals("Started")){
				SipServletRequest req=otherLeg.currentRes.createAck();
				req.setContent(res.getContent(),res.getContentType());
				req.send();
				res.createAck().send();
				state="Connected";
				otherLeg.state="Connected";
			}
			else if(otherLeg.state.equals("Invited")){
				System.out.println("Delivering the OK response for Invite");
				SipServletResponse res1=otherLeg.initial.createResponse(res.getStatus());
				res1.setContent(res.getContent(),res.getContentType());
				res1.send();
				System.out.println("Delivered the OK response for INVITE");
			}
		}
	}
	catch(Exception e){
		log.info("Line 83CallLeg: "+e.toString());
	}


}

	public void stopLeg(){
	try
	{
		System.out.println("Stop leg in callleg");
			session.createRequest("BYE").send();
	}
	catch(Exception e)
	{
		
	}

}

}