package com;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.*;
import javax.servlet.sip.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class VoiceCall extends SipServlet{
	public ServletContext context;
	SipApplicationSession sas;
	public SipFactory factory;
	SipServletRequest incoming;
  	public static final Log log = LogFactory.getLog(VoiceCall.class);
	 public void init(ServletConfig config) {
	 	try{
		log.info("Init.");
		super.init(config);

		factory = (SipFactory) getServletContext().getAttribute(
				SipServlet.SIP_FACTORY);
		sas=factory.createApplicationSession();
		context=config.getServletContext();
		context.setAttribute("servlet",this);
		//registerWithBroker();
	 	}
	 	catch(Exception e){
	 		log.info("VoiceCall:27 "+e.toString());
	 	}
	}
	
	public void doRequest(SipServletRequest req){
		try{
			if(req.getMethod().equals("INVITE") && req.isInitial()){
			SipServletRequest req2=factory.createRequest(sas,"MESSAGE","sip:ser@100.1.1.15:5061","sip:100.1.1.15:2519");
			req2.setHeader("Type","Query");
			req2.send();
			incoming=req;
		}
		else{
			((CallLeg)req.getSession().getAttribute("CALL_LEG")).transferReq(req);
		}
		}
		catch(Exception e){
			log.info("VoiceCall:39 "+e.toString());
		}
	}
	
	public void doResponse(SipServletResponse res){
		try{
		if(res.getRequest().getMethod().equals("MESSAGE") && res.getContent().equals("OK")){			
		System.out.println("Message Reply Recieved "+ res.getContent());
		SipServletRequest req=factory.createRequest(sas,"INVITE","sip:ser@100.1.1.15:5061","sip:1@100.1.1.15:5070");
		CallLeg leg1=new CallLeg(incoming, factory, log);
		CallLeg leg2=new CallLeg(req, factory, log);
		leg1.state="Invited";
		leg1.otherLeg=leg2;
		leg2.otherLeg=leg1;
		leg2.startCall();
		}
		else
		((CallLeg)res.getSession().getAttribute("CALL_LEG")).transferRes(res);
		}
		catch(Exception e){
			log.info("VoiceCall:44 "+e.toString());
		}
	}
}