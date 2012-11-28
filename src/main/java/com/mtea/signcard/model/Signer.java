package com.mtea.signcard.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 签到者
 * @author macrotea@qq.com
 * @date 2012-11-10 下午4:23:56
 * @version 1.0
 * @note
 */
public class Signer {
	
	private String name;
	
	private String password;
	
	private List<Integer> weekList=new ArrayList<Integer>();
	
	private List<String> emailList=new ArrayList<String>();
	
	private boolean enable = true;
	
	private boolean master = false;
	
	public Signer() {
		super();
	}

	public Signer(String name) {
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public List<Integer> getWeekList() {
		return weekList;
	}

	public void setWeekList(List<Integer> weekList) {
		this.weekList = weekList;
	}

	public List<String> getEmailList() {
		return emailList;
	}

	public void setEmailList(List<String> emailList) {
		this.emailList = emailList;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}
	
	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	@Override
	public String toString() {
		return "Signer [name=" + name + ", weekList=" + weekList + ", emailList=" + emailList + ", enable=" + enable + ", master=" + master + "]";
	}

}
