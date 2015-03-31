package com.tcs.iptv;
import com.tcs.*;
import java.util.*;
public class Call
{
	protected CallLeg leg;
	protected String user;
	protected long startTimeMs;
	protected long taskTimeMs;
	protected long prevTimeGap;
	public void add(String user, CallLeg leg)
	{
		startTimeMs = System.currentTimeMillis( );
		prevTimeGap=startTimeMs;
		this.user=user;
		this.leg=leg;
	}
	public String getUserName()
	{
		return user;
	}
	public CallLeg getCallLeg()
	{
		return leg;
	}
	public void setCallLeg(CallLeg leg)
	{
		this.leg=leg;
	}
	public void calculateTimeGap()
	{
		taskTimeMs=(System.currentTimeMillis( ) - startTimeMs)+prevTimeGap;
	}
	public long returnTimeGap()
	{
		startTimeMs=System.currentTimeMillis();
		prevTimeGap=taskTimeMs;
		return taskTimeMs;
	}
		
}