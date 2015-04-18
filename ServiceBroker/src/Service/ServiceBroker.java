/*
 * Purpose : Provides tasks for building IMS Application called Service Broker .
 * Author  : Altanai Bisht	
 * Author  : http://altanaitelecom.wordpress.com
 * Author  : Telecom Research and Development
 * Date    : Jun 2012
 */

package Service;

import javax.servlet.*;
import javax.servlet.sip.*;
import java.io.IOException;
import java.util.*;
import javax.servlet.sip.SipFactory;

public class ServiceBroker extends SipServlet
{
	protected ArrayList services=new ArrayList();
	protected ArrayList<User> occupiedUsers=new ArrayList<User>();
	private String user;
	private String currentService,requestedService,usedService;
	private String decision[]=new String[2],decision1,decision2;
	private SipFactory factory;
	protected SipApplicationSession sas;
	protected ServletContext context;
	Scenario s=new Scenario();
	int done=0;
	public void init(ServletConfig config) throws ServletException
	{
		context=config.getServletContext();
		factory=(SipFactory)config.getServletContext().getAttribute(SipServlet.SIP_FACTORY);
		sas=factory.createApplicationSession();
	}
	public void doRequest(SipServletRequest req)
	{
		
		try
		{	System.out.println("Request Recieved");
			System.out.println("Request Recieved2");
			if(req.getHeader("Type").equals("Register"))
			{
				System.out.println("recieved register");
				services.add(req.getHeader("ServiceID"));	
				context.setAttribute("services",services);
				System.out.println(req.getHeader("ServiceID"));
				SipServletResponse resp=req.createResponse(200);
					
				resp.send();
				System.out.println("sent response for register query");
			}
			else if(req.getHeader("Type").equals("Query"))
			{
				System.out.println("recieved query");
				requestedService=req.getHeader("ServiceID");
				user=req.getHeader("User");
				for(User u:occupiedUsers)
				{
					System.out.println("entered occupied users");
					if(u.getUserName().equals(user))
					{
						System.out.println("Matched a particular user in the liost of occupied users");
						String address;
						currentService=u.getService();
					decision=s.findDecision(currentService,requestedService);
						decision1="pause";//decision1=(String)getServletContext().getAttribute(decision[0]);
						decision2="continue";//decision2=(String)getServletContext().getAttribute(decision[1]);
						if(decision1.equals("stop"))
						{
							System.out.println("Enetered stop for current service");
							address=(String)s.findAddress(currentService);
							SipServletRequest reqs=factory.createRequest(sas,"MESSAGE","sip:100.1.1.15:5061",address);
							reqs.setHeader("Type","Stop");
							reqs.setHeader("User",user);
							reqs.send();
							System.out.println("sent the request to stop");
						}
						else if(decision1.equals("pause"))
						{
							System.out.println("Enetered pause for current service");
							address=(String)s.findAddress(currentService);
							SipServletRequest reqs=factory.createRequest(sas,"MESSAGE","sip:100.1.1.15:5061",address);
							reqs.setHeader("Type","Pause");
							reqs.setHeader("User",user);
							reqs.send();
							System.out.println("sent the request to pause");
						}	
						else if(decision1.equals("continue"))
						{
							/*address=(String)s.findAddress(currentService);
							SipServletRequest reqs=factory.createRequest(sas,"MESSAGE","sip:100.1.1.15:5061",address);
							reqs.setHeader("Type","Continue");
							reqs.setHeader("User",user);
							reqs.send();*/
						}
						if(decision2.equals("stop"))
						{
							System.out.println("Eneterd stop for requested service");
								SipServletResponse resp=req.createResponse(487);
								resp.setHeader("ReqID",req.getHeader("ReqID"));
								resp.setContent("Stop",req.getContentType());
								resp.setHeader("User",user);
								resp.send();
								System.out.println("sent response");
						}
						else if(decision2.equals("pause"))
						{
							System.out.println("Eneterd pause for requested service");
							SipServletResponse resp=req.createResponse(200);
							resp.setHeader("ReqID",req.getHeader("ReqID"));
							resp.setContent("Pause",req.getContentType());
							resp.setHeader("User",user);
							resp.send();
							System.out.println("sent response");
						}
						else if(decision2.equals("continue"))
						{
							System.out.println("Eneterd continue for requested service");
							SipServletResponse resp=req.createResponse(200);
							resp.setHeader("ReqID",req.getHeader("ReqID"));
							resp.setContent("OK","text/plain");
							resp.send();
							System.out.println("sent response");
						}
						
					done=1;		
					break;
					}
				}
				if(done==0)
				{
						System.out.println("generating response");
						SipServletResponse resp=req.createResponse(200);
						resp.setHeader("ReqID",req.getHeader("ReqID"));
						resp.setContent("OK","text/plain");
						resp.send();
						System.out.println("sent response");
				}
				done=0;
					
			}
			else if(req.getHeader("Type").equals("Add"))
			{
				System.out.println("Got an add request");
				usedService=req.getHeader("ServiceID");
				Iterator i=req.getHeaders("User");
				while(i.hasNext())
				{
					int done1=0;
					String user=(String)i.next();
					for(User u:occupiedUsers)
					{
						
						if(u.getUserName().equals(user))
						{
							System.out.println("Added service for the existing user");
							u.addNewService(usedService);
							done1=1;
							break;
						}
					}
					if(done1==0)
					{
						System.out.println("Added service for the new user");
						User u=new User(user,usedService);
						occupiedUsers.add(u);
					}
				}
				System.out.println("No.of users occupied are");
				for(User u:occupiedUsers)
				{
					System.out.println(u.getUserName());
				}
				SipServletResponse resp=req.createResponse(200);
				
				resp.send();
				System.out.println("sent response for add query");
			}
			else if(req.getHeader("Type").equals("Remove"))
			{
				System.out.println("Got an add request");
				usedService=req.getHeader("ServiceID");
				Iterator i=req.getHeaders("User");
				while(i.hasNext())
				{
					String user=(String)i.next();
					for(User u:occupiedUsers)
					{
						if(u.getUserName().equals(user))
						{
							if(u.noOfService()>1)
							{
								System.out.println("Removed the service from the users list of services");
								u.removeService(usedService);
							}
							else
							{
								System.out.println("Removed the user from the list of occupied users");
								int index=occupiedUsers.indexOf(u);
								occupiedUsers.remove(index);
							}
							break;
						}
					}
					
				}
				System.out.println("No of users still occupied are");
				for(User u:occupiedUsers)
				{
					System.out.println(u.getUserName());
				}
				SipServletResponse resp=req.createResponse(200);
			
				resp.send();
				System.out.println("sent response for remove query");
				
			}
				
				
				
		}
		catch(Exception e)
		{
			
		}
	}
	
	public void doMessage(SipServletRequest req){
		System.out.println("Message Recieved");
		
	}
}