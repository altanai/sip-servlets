/*
  Call screening with Google Calendar 
  
  Purpose : To Screen user calls if he is unavailable ie marked with meeting / appointment etc 
   			 on his calendar for the time period when the call arrives 
  Author  : Altanai Bisht
  Author  : http://altanaitelecom.wordpress.com
  Date    : Feb 2013
*/

package com.tcs;

import javax.servlet.*;
import javax.servlet.sip.*;
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
import javax.servlet.sip.SipServletRequest;
import javax.servlet.sip.SipServletResponse;
import javax.servlet.sip.SipURI;
import javax.servlet.sip.SipServletMessage;

/* java net */
import java.net.*;
import java.net.HttpURLConnection;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;

/* java io  */
import java.io.*;
import java.io.IOException;

/*util classes */
import java.util.*;
import java.util.Date;

/* mail classes*/
import javax.mail.*;
import javax.mail.Message.RecipientType;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.PasswordAuthentication;

/*security */
import java.security.Security;

/* date and time calsses */
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/* logger classes */
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.log4j.*;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.SimpleLayout;


/*  calendar api classes */
import com.google.gdata.client.*;
import com.google.gdata.client.calendar.CalendarService;
//import com.google.gdata.client.calendar.*;
import com.google.gdata.data.*;
import com.google.gdata.data.acl.*;
import com.google.gdata.data.calendar.*;
import com.google.gdata.data.extensions.*;
import com.google.gdata.util.*;
import com.google.gdata.client.calendar.CalendarQuery;

/* sample .util is a esp jar */
import sample.util.*;

public class GoogleCalendar extends SipServlet{

	//static final Logger logger = Logger.getLogger("CA");
	static Logger logger = Logger.getLogger(GoogleCalendar.class);

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

	SipServletRequest request;
	SipServletResponse response;
	SipServletRequest requestcopy;
	SipServletResponse responsecopy;

	SipServletRequest requestcallee;
	SipServletRequest requestcaller;
	SipServletResponse responsecaller;
	SipServletResponse responsecallee;

	// date and time parameters
	String date ;
	String longtime ;
	String time;
	String microtime;
	String currdate;
	String currtime;
	URL feedUrl=null;

// Arryl;ist to store calandra values
 String[][] caltime=new String[10][3];  // calandar hr , min , sec
 String[][] systime=new String[10][3];	// systwem hour , min , sec

String b[][]=new String[10][2];	// current time and datye
String a[][]=new String[10][3];	// calenday time and date

//String cdate[][]=new String[][];
String ctime;
String cevent;
String matchresult="none";
int i=0;
 String status="free";
 //String status2=null;




// Email parameters
	private static final String SMTP_HOST_NAME = "smtp.gmail.com";
	private static final String SMTP_PORT = "465";
	private static final String emailSubjectTxt = "Missed calle alert during scheduled event on google calendar ";
	private static final String emailFromAddress = "tcscns@gmail.com";//Your mail-id which goes as From Address
	private static final String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
	private static final String sendTo =  "tcscns1@gmail.com";//Addresses of recipients
	//String result = null;
    String urlStr="";

	public void init(ServletConfig config)
	throws ServletException
	{
																		System.out.println(" try ::::::");
		//BasicConfigurator.configure();
		try{
				FileAppender fileappender = new FileAppender(new PatternLayout(),"E://log4j/output.txt");
				logger.addAppender(fileappender);
				logger.debug("debug - altanai");
		}
		catch( Exception e){
			System.out.println("error");
		}
																		System.out.println("\n initiate func- application google calender");
																		System.out.println("=========================================");
		super.init(config);
		context=config.getServletContext();
		factory = (SipFactory) context.getAttribute(SipServlet.SIP_FACTORY);
		sessionwhole= factory.createApplicationSession();

																		System.out.println("context "+ context);																	System.out.println("factory "+ factory);
																		System.out.println("session whole "+ sessionwhole.toString());
	}

	public void doInvite(SipServletRequest request)
	throws javax.servlet.ServletException,
			java.io.IOException,
			java.lang.IllegalStateException,
			ServletParseException
	{
																		System.out.println("\n doinvite function");
																		System.out.println("===========================");
			status="free";
			i=0;
			uricaller=request.getFrom().getURI();
			uricallee=request.getTo().getURI();

			strcaller=request.getFrom().getURI().toString();
			strcallee=request.getTo().getURI().toString();
																		System.out.println("caller: "+strcaller);
			try
			{														System.out.println("callee: "+strcallee);
	   			main();
	   																System.out.println("value of event"+status);
	   		// calling calendar
			}
			catch(Exception e)
			{
				System.out.println("Exception ");
			}

																		System.out.println(" match result12345 : "+ status);
	   		if(status.equalsIgnoreCase("busy"))
	   		{
					responsecaller = request.createResponse(486);
					responsecaller.send();

					//sendmail();
			}
			else if(status.equalsIgnoreCase("free"))
			{
					requestcopy=request;

					sessioncaller2b2bua= request.getSession();
																		System.out.println("session caller to b2bua  "+sessioncaller2b2bua.toString());
					responsecaller = request.createResponse(180);
					responsecaller.send();

					requestcallee=factory.createRequest(sessionwhole,"INVITE",strcaller,strcallee);
					requestcallee.setContent(request.getContent(),request.getContentType());
					requestcallee.send();
																		System.out.println("invite send to callee");
					sessionb2bua2callee=requestcallee.getSession();
																		System.out.println("session  b2bua to callee  "+sessionb2bua2callee.toString());
			}
			else
			{
																		System.out.println(" calender result is neither none nor match ");
			}
																		System.out.println("===========================");
																		System.out.println(" exit invite ");

	}
/* function do response */
    public void doResponse(SipServletResponse response)
	throws ServletException, IOException
	{
																		System.out.println("\n doresponse function "+ response.getStatus() );
		if(response.getStatus() ==200)
		{
			responsecopy=response;

			requestcallee= response.createAck();
			requestcallee.send();
																		System.out.println(" ack send to callee ");
			responsecaller = requestcopy.createResponse(200);
			responsecaller.setContent(response.getContent(),response.getContentType());
			responsecaller.send();

																		System.out.println(" 200 ok send to caller ");

																		System.out.println(" exit doresponse function");
		}
	}
/* function bye */
public void doBye(SipServletRequest request)
	throws javax.servlet.ServletException,
			java.io.IOException,
			java.lang.IllegalStateException,
			ServletParseException
	{
		if(request.getFrom().getURI().toString().equalsIgnoreCase(strcaller))
		{
																	System.out.println(" Bye geneerated from caller ");
				responsecaller = request.createResponse(200);
				responsecaller.send();

				requestcallee=factory.createRequest(sessionwhole,"BYE",strcaller,strcallee);
				requestcallee.setContent(request.getContent(),request.getContentType());
				requestcallee.send();
		}
		else if(request.getFrom().getURI().toString().equalsIgnoreCase(strcallee))
		{
																	System.out.println(" Bye geneerated from callee ");
				responsecallee = request.createResponse(200);
				responsecallee.send();

				requestcaller=factory.createRequest(sessionwhole,"BYE",strcallee,strcaller);
				requestcaller.setContent(request.getContent(),request.getContentType());
				requestcaller.send();
		}
		else
		{
																	System.out.println(" Bye geneerated from neither thej caller nor the calle ");
		}
	}


	public void main() throws IOException, ServiceException {
			   CalendarService myService = new CalendarService("map");
	            CalendarService service = new CalendarService("map");
	java.util.Date d=new java.util.Date();
	java.util.Date sysdate =null;
	int looptesting=0;


	System.out.println("--------"+"---------------hellocal1");
	            //Date d=new Date();
	            System.out.println(d);
	            SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd@HH:mm:ss");
	            String t11=df.format(d).toString();
	            //System.out.println(d);
	            int i=0;
	                    int k=0;
	                    int n=0;
	                    int l=0;
	                    int g=0;
	                    int ka=0;
	                    int na=0;
	                    int la=0;
	                    int ga=0;
	                    int ksy=0;
	                    int ksm=0;
	                    int ksd=0;
	                    int key=0;
	                    int kem=0;
	                    int ked=0;
	                    String value3=null;
	            Date r1=new Date();
	            //String r3=r1.po();
	             StringTokenizer date2=new StringTokenizer(t11,"@");
	             String b[][]=new String[1][2];
	             String b1[][]=new String[1][3];
	             String a[][]=new String[1][3];
	             String aa[][]=new String[1][3];

	             String value1;
	             String key1=null;
	             String value1a;
	             String key1a=null;
	             String syshour=null;
	             String sysmin=null;
	             String syssec=null;
	             int lengtrh;

	             while(date2.hasMoreElements())
	             {
	                 String key3=date2.nextToken();
	                 value3=date2.nextToken();

	            b[0][0]=key3;
	            b[0][1]=value3;
	            System.out.println(b[0][0]+"\t"+b[0][1]);


	             }
	             StringTokenizer datess=new StringTokenizer(b[0][0],"-");
	             {
	                 while(datess.hasMoreElements())
	                 {
	                     b1[0][0]=datess.nextToken();
	                     b1[0][1]=datess.nextToken();
	                     b1[0][2]=datess.nextToken();
	                 }
	             }
	             //System.out.println(b[0][0]+"\t"+b[0][1]);
	             StringTokenizer date3=new StringTokenizer(value3,":");
	             {
	                 syshour=date3.nextToken();
	                 sysmin=date3.nextToken();
	                 syssec=date3.nextToken();

	             }
	             System.out.println(b[0][0]+"\t"+syshour+"\t"+sysmin+"\t"+syssec);
	             System.out.println(b1[0][0]+"\t"+"                 startdate in year");
	             System.out.println(b1[0][1]+"\t"+"                 start date in month");
	             System.out.println(b1[0][2]+"\t"+"                  start date in day   ");





	            try {
	                myService.setUserCredentials("tcscns@gmail.com", "tcs@123456");
	            } catch (AuthenticationException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }

	            URL feedUrl = null;
	            try {


	                String po="http://www.google.com/calendar/feeds/";
	                String poo="tcscns@gmail.com";
	                String paa="/private/full";
	                String pl=po.concat(poo);
	                String p2=pl.concat(paa);



	                feedUrl = new URL(p2);
	            } catch (MalformedURLException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            CalendarEventFeed resultFeed = null;
	            try {
	                resultFeed = myService.getFeed(feedUrl, CalendarEventFeed.class);
	            } catch (IOException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            } catch (ServiceException e) {
	                // TODO Auto-generated catch block
	                e.printStackTrace();
	            }
	            lengtrh=resultFeed.getEntries().size();
	            String f[][]=new String[lengtrh][1];
	             String h[][]=new String[lengtrh][1];
	             String m[][]=new String[lengtrh][1];
	             String s[][]=new String[lengtrh][1];
	             String poi[][]=new String[lengtrh][1];
	             String poiy[][]=new String[lengtrh][1];
	             String poim[][]=new String[lengtrh][1];
	             String poid[][]=new String[lengtrh][1];
	             String fa[][]=new String[lengtrh][1];
	             String ha[][]=new String[lengtrh][1];
	             String ma[][]=new String[lengtrh][1];
	             String sa[][]=new String[lengtrh][1];
	             String poia[][]=new String[lengtrh][1];
	             String poiay[][]=new String[lengtrh][1];
	             String poiam[][]=new String[lengtrh][1];
	             String poiad[][]=new String[lengtrh][1];
	             int day1[]=new int[lengtrh];
	             int day2[]=new int[lengtrh];
	            System.out.println("Your calendars:");
	            System.out.println();
	            CalendarQuery myQuery = new CalendarQuery(feedUrl);



	              if (resultFeed.getEntries().size() > 0) {
	                  for (CalendarEventEntry event : resultFeed.getEntries()) {
	                    String id = event.getId();
	                    lengtrh=resultFeed.getEntries().size();
	                    String title = event.getTitle().getPlainText();
	                    System.out.println(lengtrh+"wow");
	                         f[i][0]=title;
	                         System.out.print(f[i][0]+"\t");
	                         //System.out.print(title);

	                         i++;


	                    for (When when : event.getTimes()) {
	                      DateTime startTime = when.getStartTime();
	                      //System.out.println(startTime);

	                      DateTime endTime = when.getEndTime();
	                      String t1a=endTime.toString();

	                      //System.out.println(startTime);

	                      //System.out.println(title);
	                      String t1=startTime.toString();

	                      StringTokenizer date=new StringTokenizer(t1,"T");
	                      while(date.hasMoreElements())
	                      {
	                          String keys=date.nextToken();
	                          String value=date.nextToken();
	                          //System.out.println(key+"::"+value);

	                          poi[g][0]=keys;

	                          System.out.print(poi[g][0]+"\t");
	                          StringTokenizer date4aaa=new StringTokenizer(poi[g][0],"-");
	                          while(date4aaa.hasMoreElements())
	                          {
	                              String houra=date4aaa.nextToken();
	                              String mina=date4aaa.nextToken();
	                              String seca=date4aaa.nextToken();

	                              //System.out.println(key1);
	                              //a[0][1]=key1;
	                              poiy[ksy][0]=houra;
	                              poim[ksy][0]=mina;
	                              poid[ksy][0]=seca;

	                              System.out.print(poiy[ksy][0]+"\t"+"---------------startyaer");
	                              System.out.print(poim[ksm][0]+"\t"+"---------------startmonth");
	                              System.out.println(poid[ksd][0]+"\t"+"---------------startday");


	                          }
	                          g++;
	                          ksy++;
	                          ksm++;
	                          ksd++;
	                          a[0][1]=value;



	                          StringTokenizer date1=new StringTokenizer(value,".");
	                          while(date1.hasMoreElements())
	                          {
	                              key1=date1.nextToken();
	                               value1=date1.nextToken();
	                              //System.out.println(key1);
	                              //a[0][1]=key1;
	                          }
	                          StringTokenizer date4=new StringTokenizer(key1,":");
	                          while(date4.hasMoreElements())
	                          {
	                              String hour=date4.nextToken();
	                              String min=date4.nextToken();
	                              String sec=date4.nextToken();

	                              //System.out.println(key1);
	                              //a[0][1]=key1;
	                              h[k][0]=hour;
	                              m[l][0]=min;
	                              s[n][0]=sec;

	                              System.out.print(h[k][0]+"\t");
	                              System.out.print(m[l][0]+"\t");
	                              System.out.println(s[n][0]+"\t");
	                              k++;
	                              l++;
	                              n++;
	                          }
	                          StringTokenizer datea=new StringTokenizer(t1a,"T");
	                          while(datea.hasMoreElements())
	                          {
	                              String keya=datea.nextToken();
	                              String valuea=datea.nextToken();
	                              //System.out.println(key+"::"+value);

	                              poia[ga][0]=keya;
	                              StringTokenizer date4aa=new StringTokenizer(poia[ga][0],"-");
	                              while(date4aa.hasMoreElements())
	                              {
	                                  String houraa=date4aa.nextToken();
	                                  String minaa=date4aa.nextToken();
	                                  String secaa=date4aa.nextToken();

	                                  //System.out.println(key1);
	                                  //a[0][1]=key1;
	                                  poiay[key][0]=houraa;
	                                  poiam[kem][0]=minaa;
	                                  poiad[ked][0]=secaa;

	                                  System.out.print(poiay[key][0]+"\t"+"---------------endyear");
	                                  System.out.print(poiam[kem][0]+"\t"+"---------------endmonth");
	                                  System.out.println(poiad[ked][0]+"\t"+"---------------endday");


	                              }

	                              System.out.print(poia[ga][0]+"\t");
	                              ga++;

	                              key++;
	                              kem++;
	                              ked++;
	                              aa[0][1]=valuea;



	                              StringTokenizer date1a=new StringTokenizer(valuea,".");
	                              while(date1a.hasMoreElements())
	                              {
	                                  key1a=date1a.nextToken();
	                                   value1a=date1a.nextToken();
	                                  //System.out.println(key1);
	                                  //a[0][1]=key1;
	                              }
	                              StringTokenizer date4af=new StringTokenizer(key1a,":");
	                              while(date4af.hasMoreElements())
	                              {
	                                  String houra=date4af.nextToken();
	                                  String mina=date4af.nextToken();
	                                  String seca=date4af.nextToken();

	                                  //System.out.println(key1);
	                                  //a[0][1]=key1;
	                                  ha[ka][0]=houra;
	                                  ma[la][0]=mina;
	                                  sa[na][0]=seca;

	                                  System.out.print("endTimeeee"+ha[ka][0]+"\t");
	                                  System.out.print(ma[la][0]+"\t");
	                                  System.out.println(sa[na][0]+"\t");

	                                  ka++;
	                                  la++;
	                                  na++;


	                              }



	                          //a[0][2]=title;

	                          //System.out.print(f+"\t");
	                          //System.out.print(h[k][0]+"\t");
	                          //System.out.println(m[l][0]+"\t");
	                         // System.out.println(m[l][0]);
	                      }
	                    }
	                  }
	                  }
	                  int syshour1=Integer.parseInt(syshour);
	                  int sysmin1=Integer.parseInt(sysmin);



	              for(int z=0;z<lengtrh;z++)
	              {
	                //System.out.print(f[z][0]+"\t");
	              }
	                 for(int run=0;run<lengtrh;run++)

	                 {
	                     int shr=Integer.parseInt(h[run][0]);
	                     int days=0;
	                     int temp=shr;

	                     int smin=Integer.parseInt(m[run][0]);

	                     int ehr=Integer.parseInt(ha[run][0]);
	                     int emin=Integer.parseInt(ma[run][0]);
	                     int syshr=Integer.parseInt(syshour);
	                     int sysmn=Integer.parseInt(sysmin);
	                     SimpleDateFormat format = new SimpleDateFormat("yy/MM/dd HH:mm:ss");
	                     for(int loop=0;loop<lengtrh;loop++)
	                     {

	                    	 GregorianCalendar cal1 = new GregorianCalendar();
	                    	 GregorianCalendar cal2 = new GregorianCalendar();
	                    	 GregorianCalendar cal3 = new GregorianCalendar();

	                     cal1.set(Integer.parseInt(poiy[run][0]),Integer.parseInt(poim[run][0]), Integer.parseInt(poid[run][0]));
	                     cal2.set(Integer.parseInt(poiay[run][0]),Integer.parseInt(poiam[run][0]), Integer.parseInt(poiad[run][0]));
	                     cal3.set(Integer.parseInt(b1[0][0]),Integer.parseInt(b1[0][1]), Integer.parseInt(b1[0][2]));
	                     java.util.Date d12=(java.util.Date)cal1.getTime();
	                     java.util.Date d13=(java.util.Date)cal2.getTime();
	                     java.util.Date d14=(java.util.Date)cal3.getTime();
	                     System.out.println("firstdate"+d12+"secnddate"+d13+"thirddate"+d14);
	                     day1[run]= (int)(d14.getTime() - d12.getTime()) / (1000 * 60 * 60 * 24);
	                     day2[run]= (int)(d13.getTime() - d12.getTime()) / (1000 * 60 * 60 * 24);
	                     System.out.println("no of days between start date of calender and system date"      +day1[run]+      "days between satrt and end"      +day2[run]);
	                      looptesting++;
	                     }


	                     //long milliseconds1 = calendar1.getTimeInMillis();
	                     //long milliseconds2 = calendar2.getTimeInMillis();
	                     //long diff = milliseconds2 - milliseconds1;
	                     int day=day1[run];//sysdate-start caldate
	                     int diffDays =day2[run];//callstartdate-callenddatw
	                     //.out.println("---------------problem starts--------");
	                     //////try{
	                      //sysdate = new  java.util.Date(syshour);
	                     //}
	                     //catch(Exception e)
	                    // {
	                         //e.printStackTrace();

	                    // }
	                     //java.util.Date  startdate= new  java.util.Date (h[run][0]);
	                     //java.util.Date  enddate= new  java.util.Date (ha[run][0]);
	                    //long df1=(startdate.getTime()-enddate.getTime()) / (24 * 60 * 60 * 1000);
	                    /*System.out.println("----------------"+df1);
	                     System.out.println("---------------problem ends--------");
	                      Calendar cal1 = Calendar.getInstance();
	                      cal1.setTime(sysdate);
	                      Calendar cal2 = Calendar.getInstance();
	                      cal2.setTime(startdate);
	                      Calendar cal3 = Calendar.getInstance();
	                      cal3.setTime(enddate);
	                      Calendar date = (Calendar)startdate.clone();
	                      long daysBetween = 0;
	                      while (date.before(enddate)) {
	                        date.add(Calendar.DAY_OF_MONTH, 1);
	                        daysBetween++;
	                      }
	                      System.out.println(daysBetween);*/


	                    while(diffDays>=0)
	                     {
							System.out.println("temp value"+temp);
								System.out.println("syshr value"+syshr);
	                        if(temp==syshr && ehr==syshr)
	                        {
	                            if(sysmn>emin)
	                            {
	                            System.out.println("free1111");
	                             return;
	                            }
	                        }
	                        if(temp==syshr && days==day)
	                         {
	                             System.out.println("busy");
	                             status="busy";
	                             return;
	                         }

	                         if(temp==23)
	                         {
	                             temp=0;
	                             days++;
	                             diffDays--;
	                         }
	                         else
	                             temp++;

	                         if(temp==ehr && diffDays==0)
	                         {
	                             System.out.println("free");
	                             //status="free";
	                             return;

	                         }


	                     }


	                    /*if((b[0][0].equals(poi[run][0])))
	                             {
	                                  int dif=ehr-shr;
	                                  System.out.println("------------------------------------------------------------------------"+dif);
	                                  if((dif)>=0)
	                                  {
	                                  for(int z=0;z<=dif;z++)
	                                  {


	                                      if((syshr)==(shr+z))

	                                      {
	                                          System.out.println("busy1");
	                                      }
	                                  }
	                             }
	                                  else if((syshr==ehr)&&(sysmn==emin))
	                                  {
	                                      System.out.println("busy2");
	                                  }

	                                  else if((syshr==shr)&&(sysmn==smin))
	                                  {
	                                      System.out.println("busy3");
	                                  }
	                                  else if(syshr==23 && ehr==00)
	                                  {
	                                      if((b[0][0].equals(poi[run][0])))
	                                      {
	                                        System.out.println("busy4");
	                                      }
	                                  }
	                                  else if(syshr==23 && ehr==01)
	                                  {
	                                      if(!((b[0][0].equals(poi[run][0]))))
	                                      {
	                                        System.out.println("busy4");
	                                      }
	                                  }

	                             }






	                      //if((b[0][0].equals(poi[run][0]))&&(syshour.equals(h[run][0])))
	                      //{


	                          //System.out.println(a[0][2]);
	                          //System.out.println("he is busy now");
	                         // break;
	                          //}

	                     // else

	                              //System.out.println("no any event this time he is free now you can call him");
	                             //break;
	                 }



	                      System.out.println("free now");

	                      */

	                 }


	}
	}
}




