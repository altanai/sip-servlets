/*
 * Purpose : Provides tasks for building IMS Application called Service Broker .
 * Author  : Altanai Bisht	
 * Author  : http://altanaitelecom.wordpress.com
 * Author  : Telecom Research and Development
 * Date    : Jun 2012
 */

package Service;

import java.util.*;

class User
{
	protected String userName;
	protected ArrayList<String> currentLayoutActiveSession=new ArrayList<String>();
	public User(String userName,String currentService)
	{
		this.userName=userName;
		currentLayoutActiveSession.add(currentService);
	}
	public void addNewService(String newService)
	{
		currentLayoutActiveSession.add(newService);
	}
	public String getUserName()
	{
		return userName;
	}
	public String getService()
	{
		return currentLayoutActiveSession.get(0);
	}
	public int noOfService()
	{
		return currentLayoutActiveSession.size();
	}
	public void removeService(String service)
	{
		int i=currentLayoutActiveSession.indexOf(service);
		currentLayoutActiveSession.remove(i);
	}
}