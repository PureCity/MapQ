package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;
import android.util.Log;

public class CreateQuestion_ws {
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = null;
	private String getWS = "false";

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String Result = null;
	
	public String getResult() {
		return Result;
	}

	// WebService调用,获取用户登录信息
	public void CreateQuestion(String strUserId, String strLatitude,
			String strLongitude, String strTitle,String strContext, String wssid) {

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "CreateQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/CreateQuestion";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strUserId", strUserId);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);
		rpcObject.addProperty("strTitle", strTitle);
		rpcObject.addProperty("strContext", strContext);
		rpcObject.addProperty("wssid", wssid);

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpcObject;

		// 设置是否调用的是dotNet开发的WebService
		envelope.dotNet = true;
		// 等价于envelope.bodyOut = rpcObject;
		envelope.setOutputSoapObject(rpcObject);

		HttpTransportSE transportSE = new HttpTransportSE(endPoint);

		try {
			// 调用WebService
			transportSE.call(soapAction, envelope);
		} catch (Exception e) {
			return;//调用失败立刻结束
		}
		
		if (envelope.bodyIn instanceof SoapFault) {
		    String str= ((SoapFault) envelope.bodyIn).faultstring;
		    Log.i("", str);
		}
		
		SoapObject getObject = (SoapObject) envelope.bodyIn;
		if (getObject == null) {// 获取WebService失败
			Log.d("Httperror", "Httperror");

			return;
		} else {
			this.getWS = "true";
			Log.d("Http", getObject + "");// 测试
		}
		String aString = getObject.getProperty(0).toString();

		Log.i(DIV_TAG, aString);
		divString(aString);// 分隔字符串

	}

	// 将获取到的字符串分隔处理
	private void divString(String aString) {
		String string1 = aString.replaceAll("anyType", "\\|");
		String string2 = string1.replaceAll("\\{", "\\|");
		String string3 = string2.replaceAll("\\}", "\\|");
		String string4 = string3.replaceAll("; ", "\\|");
		String string5 = string4.replaceAll("\\|", "=");
		String [] string6 = string5.split("=");
		
//		for (int i = 0; i < string6.length; i++) {
//			Log.d("content", string6[i] + "|" + i);
//		}
		
		this.Result = string6[string6.length - 1];
	}
}
