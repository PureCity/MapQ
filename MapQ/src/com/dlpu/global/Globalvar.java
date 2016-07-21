package com.dlpu.global;

public class Globalvar {

	private String theHttpIpString = null;

	public Globalvar() {
//		this.theHttpIpString = "http://10.0.2.2:8081";
		 this.theHttpIpString = "http://www.zhongbinfan.com/mapq";
		// this.theHttpIpString = "http://192.168.159.1:8081";
		// this.theHttpIpString = "http://210.30.49.103:8081";
	}

	public String getTheHttpIpString() {
		return theHttpIpString;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	public void setTheHttpIp_AVD() {
		this.theHttpIpString = "http://10.0.2.2:8081";
	}

	public void setTheHttpIp_LocalNet() {
		this.theHttpIpString = "http://192.168.137.1:8081";
	}

	public void setTheHttpIp_Internet() {
		this.theHttpIpString = "http://210.30.49.103:8081";
	}
}
