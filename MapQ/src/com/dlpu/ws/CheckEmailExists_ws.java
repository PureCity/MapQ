package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class CheckEmailExists_ws {
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = "";
	private String getWS = "false";

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String CheckEmailExistsResult = null;

	public String getCheckEmailExistsResult() {
		return CheckEmailExistsResult;
	}

	// WebService调用,获取用户登录信息
	public void CheckEmail(String strEmail) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "CheckEmailExists";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/UserServer.asmx";
		// String endPoint = "http://10.0.2.2:8081/ws/UserServer.asmx";//
		// 在安卓虚拟机上调试访问本机IP
		// soapAction
		String soapAction = "http://mapq.com.cn/CheckEmailExists";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strEmail", strEmail);

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

		SoapObject getObject = (SoapObject) envelope.bodyIn;
		if (getObject == null) {// 获取WebService失败
			Log.d("Httperror", "Httperror");
			return;
		}else {
			this.getWS = "true";
		}

		String aString = getObject.getProperty(0).toString();

		Log.i(DIV_TAG, aString);
		this.CheckEmailExistsResult = aString;

	}
}
