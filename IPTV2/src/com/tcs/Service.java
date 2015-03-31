package com.tcs;

public interface Service{
	void registerWithBroker();
	void hold(String key);
	void stop(String key);
	void resume(String key);
}