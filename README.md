SIP Servlets 
==============

To run these applications it is required that both the WebLogic Sip Server™ and the Voxpilot VoiceXML Media Server (VMS) have already been installed and setup.
some of these application may also require Brekeke or any other Registrar for SIP register requets . Few appliation also require databse management . 

**Applications in this Git Repo include**

### Hello World Proxy Server
It is a simple sample SIP servlet creation which makes a converged HTTP SIP application . Prints Hello World when a Call comes and proxies it ahead.

### Find Me follow Me 

### IPTV Applicatiosn

#### Parental Control
Parental control : A master control on the video on demand and IPTV sessions played by the child user . Parnet receives a live preview of media content ( such as trailer of movie , or random 2 minute snippet of ongoing IP channel ) child wants to view . Parent can view the media content on his SIP phone and decide whether to go ahead with request or block the show from child.

The differentiating factors of a realtime IPTV parental control application from any other parental control solution is that it doesnt not enforce the parent  to review and restricts beforehand . Parent can review the channel / content settings at realtime ie as and when the child requests . Therefore it Saves the time involved in making beforehand channel settings . 

Parents can customise the solution to meet their requirnements such as realtime notification to parent can be send via
- Email ( channel link and summary ) 
- IM ( Instant message ) with reply option 
- Call and Video preview  with DTMF option
- 
#### session mobility
Next is one case wherein if one wants to migrate a live session from one device lets say desktop computer to another device say mobile phone in a manner that the session runs only from the point left on the earlier device , then voice call continuity solves it for you . 

### Video Call
Simple SIP servlet application to initiate a SIP service / application. Handle the doRequest and doResponse generated after
SIPRequest and SIPResponse . Alos describes appening the call log to file for keeping trcak of calls . 

### Call Screening
Call screening service blocks a list of users from calling a subscriber, on date or/and time basis , on pre - selected preferrence basis , on reported spam basis etc .

#### Enhanced Call screening
Interaction logic first calls the Call Screening service to find if the caller is screened by the callee or not, and based on the response it may forward the call to FMFM ( Find -me -Follow - me ) service or rejects the call attempt. The two services are independent of each other and could be orchestrated using the service broker to act as one single service logic.

#### Calendar Based Call Screening
The Call gets screen based on the calender enteries made by the user such as "Booked for meeting", "In a conference ".  

### Music on Hold 

The application Music on Hold , was developed aiming to play a musical tone while ano one of the calling party puts the other on hold . More information [Music on hold SIP Servlet Application ]

### Echo Servlet
echoes instant messages sent by Windows Messenger.

### Gaming Server
--tbd

More application descriptions are included here [Telecom SIP application summary]

### Geolocation with google Latitude 
The application is aimed to optimise the assignments of tasks to field works and engineers , with features such as user location reporting and  location tracking of inventory at real-time .
The  application shall provide valuable field insights to management, ensure effective deployment of field personnel and empowers them to complete their activities while automating location reporting .It demonstrates the process of  mapping the users or assets on a map by their devices so as to make a visual interpretation of their presence , calls and accessibility . 

### SIP Message delivery via Email and SMS
This Application allows the user to receive the text of the message that was sent to him on the instant messenger to his phone and, as e-mail to his mailbox if he is unavailable at the instant messenger. 

*References*

[Telecom SIP application summary]:https://altanaitelecom.wordpress.com/2014/01/15/applications-for-telecom-providers-that-cater-to-sip/
[Music on hold SIP Servlet Application ]:https://altanaitelecom.wordpress.com/2013/07/17/music-on-hold/
