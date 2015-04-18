/*
 * Purpose : Provides tasks for building IMS Application called Service Broker .
 * Author  : Altanai Bisht	
 * Author  : http://altanaitelecom.wordpress.com
 * Author  : Telecom Research and Development
 * Date    : Jun 2012
 */

package Service;

class Scenario
{
	public String[] findDecision(String currentService,String requestedService)
	{
		String decision[]=new String[2];
		if(currentService.equals("VoiceCall") && requestedService.equals("IPTV"))
		{
			decision[0]="task1";
			decision[1]="task2";
		}
		else if(currentService.equals("IPTV") && requestedService.equals("VoiceCall"))
		{
			decision[0]="task4";
			decision[1]="task3";
		}
		return decision;
	}
	public String findAddress(String service)
	{
		String address="";
		if(service.equals("VoiceCall"))
		{
			address="sip:ser@100.1.1.10:5061";			
		}
		else if(service.equals("IPTV"))
		{
			address="sip:iptv@100.1.1.5:5061";
		}
		return address;		
	}
}