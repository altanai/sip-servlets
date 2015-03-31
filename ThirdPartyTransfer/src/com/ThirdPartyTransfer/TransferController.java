/**
 * @(#)TransferController.java
 *
 *
 * @author 
 * @version 1.00 2010/7/29
 */
package com.ThirdPartyController;
import javax.servlet.*;
import javax.servlet.sip.*;
import java.io.*;
import java.util.HashMap;

public class TransferController extends SipServlet {

public SipFactory fact;
public SipApplicationSession sas;

   public void init(ServletConfig config) throws ServletException
   {
   	fact=(SipFactory)config.getServletContext.getAttribute(SipServlet.SIP_FACTORY);
   	sas=fact.createApplicationSession();
   }
   protected void doInvite(SipServletRequest req) throws ServletException,IOException
   {
   	SipServletRequest req2=fact.createRequest(sas,"INVITE",req.getFrom(),"to");
   	req2.send();
   }
   protected void doSuccessResponse(SipServlet)
   
    
    
}