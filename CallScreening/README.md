SIP Servlets 
==============

# Call Screening
Call screening service blocks a list of users from calling a subscriber, on date or/and time basis who is subscribed to this service, and Find-Me-Follow-Me service allows a user to be reached at more that one location in round robin fashion.
Interaction logic first calls the Call Screening service to find if the caller is screened by the callee or not, and based on
the response it may forward the call to FMFM service or rejects the call attempt. The two services are independent of each 
other and could be orchestrated using the service broker to act as one single service logic.


More application descriptions are included here [Telecom SIP application summary]


[Telecom SIP application summary]:https://altanaitelecom.wordpress.com/2014/01/15/applications-for-telecom-providers-that-cater-to-sip/
[Music on hold SIP Servlet Application ]:https://altanaitelecom.wordpress.com/2013/07/17/music-on-hold/
