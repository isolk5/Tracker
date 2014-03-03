package com.isol.app.tracker;

import android.app.Application;   
public class MyApplication extends Application {
	
	private int GroupID;
	private int PersonID;
	private String accountName;
	private String nickName;
	private int personZone;
	
	public int getPersonID() {
		return PersonID;
	}

	public void setPersonID(int personID) {
		PersonID = personID;
	}

	public int getGroupID() {
		return GroupID;
	}

	public void setGroupID(int groupID) {
		GroupID = groupID;
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	private static MyApplication instance = null; 

	@Override
	public void onCreate() {
		super.onCreate();   instance = this;
	}

	public static MyApplication getInstance() {
		return instance; 
	}

	public int getPersonZone() {
		return personZone;
	}

	public void setPersonZone(int personZone) {
		this.personZone = personZone;
	}
}