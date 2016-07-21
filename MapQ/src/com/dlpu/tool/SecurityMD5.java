package com.dlpu.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityMD5 {

	public String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// 加密
		return getString(m);
	}
	//修正后的MD5加密后的编码（适应数据库以及HTML5网站端）
	private String getString(byte[] b) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			if((b[i] & 0xff) < 0x10){
				sb.append("0");
			}
			sb.append(Long.toString(b[i] & 0xff,16));
		}
		return sb.toString();
	}
	
}
