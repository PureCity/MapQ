package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class AddComment_ws {

	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = "";
	private String getWS = "false";
	private String resultString = null;// 添加评论结果,成功则返回true

	public String getResultString() {
		return resultString;
	}

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	// WebService调用,获取用户登录信息
	public void AddComment(String strQuestionId, String strUserId, String strCommentContext, String wssid) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "AddComment";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/AddComment";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strQuestionId", strQuestionId);
		rpcObject.addProperty("strUserId", strUserId);
		rpcObject.addProperty("strCommentContext", strCommentContext);
		rpcObject.addProperty("wssid", wssid);

		// 生成调用WebService方法的SOAP请求信息,并指定SOAP版本
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
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
			return;// 调用失败立刻结束
		}

		SoapObject getObject = (SoapObject) envelope.bodyIn;
		if (getObject == null) {// 获取WebService失败
			Log.d("Httperror", "Httperror");
			return;
		} else {
			this.getWS = "true";
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
		String[] string6 = string5.split("=");

		// for (int i = 0; i < string6.length; i++) {
		// Log.d("content", string6[i] + "|" + i);
		// }
		//
		this.resultString = string6[string6.length - 1];
	}

}
