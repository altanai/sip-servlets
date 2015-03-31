package com;
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
	initial=req;
	session=req.getSession();
	currentReq=req;
	currentRes=null;
	factory=fact;
	session.setAttribute("CALL_LEG",this);
	session.setAttribute("Type","Call");
	state="Prepared";
	this.log=log;
}


public void startCall(){
	try{
		initial.send();
	state="Started";
	System.out.println("Started Call\n");
	}
	catch(Exception e){
		log.info("CallLeg:37 "+e.toString());
	}
}

public void transferReq(SipServletRequest req) throws Exception{
	currentReq=req;
	try{
	if(req.getMethod().equals("ACK")){
	SipServletRequest ack=otherLeg.currentRes.createAck();
	ack.setContent(req.getContent(), req.getContentType());
	ack.send();
	state="Connected";
	otherLeg.state="Connected";
	System.out.println("Request Recieved Ack");
	}
	else if(req.getMethod().equals("BYE")){
	req.createResponse(200).send();
	otherLeg.session.createRequest("BYE").send();
	session.invalidate();
	System.out.println("Request Recieved Bye");

	}
	}
	catch(Exception e){
		log.info("Line 55CallLeg: "+e.toString());
	}
}

public void transferRes(SipServletResponse res) throws Exception{
	currentRes=res;
	try{
		if(res.getStatus()>=200 && res.getStatus()<300){
			if(currentReq.getMethod().equals("BYE")){
				session.invalidate();
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
				SipServletResponse res1=otherLeg.initial.createResponse(res.getStatus());
				res1.setContent(res.getContent(),res.getContentType());
				res1.send();
			}
		}
	}
	catch(Exception e){
		log.info("Line 83CallLeg: "+e.toString());
	}

}

public void stopLeg()throws Exception{
	session.createRequest("BYE").send();
}

}