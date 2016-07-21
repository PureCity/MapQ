package com.dlpu.tool;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecurityMD5 {

	public String getMD5(String val) throws NoSuchAlgorithmException {
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(val.getBytes());
		byte[] m = md5.digest();// ����
		return getString(m);
	}
	//�������MD5���ܺ�ı��루��Ӧ���ݿ��Լ�HTML5��վ�ˣ�
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
