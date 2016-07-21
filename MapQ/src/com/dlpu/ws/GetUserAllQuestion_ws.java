package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class GetUserAllQuestion_ws {
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = null;
	private String getWS = "false";
	private String statues = null;// 获取状态
	private int num = 0;// 发布的问题数量

	public String get_getWS() {
		return this.getWS;
	}

	public String getStatues() {
		return statues;
	}

	public int getNum() {
		return num;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String Result = null;// 问题详情

	public String getResult() {
		return Result;
	}

	// WebService调用,获取用户登录信息
	public void GetUserAllQuestion(String strUserId, String wssid) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "GetUserAllQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/GetUserAllQuestion";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strUserId", strUserId);
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
			return;// 调用失败立刻结束
		}

		if (envelope.bodyIn instanceof SoapFault) {
			String str = ((SoapFault) envelope.bodyIn).faultstring;
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
		String[] string6 = string5.split("=");

		// for (int i = 0; i < string6.length; i++) {
		// Log.d("content", string6[i] + "|" + i);
		// }
		String divTwoQuestionString = "MapQ_QuestionDivTag__divdiffentQuestion.;";// 分割两问题
		String divQuestionIDTitleString = "MapQ_QuestionDivTag__divQuestionIdandTitle.;";// 分割问题id和标题
		this.Result = "";
		for (int i = 0; i < string6.length; i++) {
			if (string6[i].equals("strStateInfo")) {
				this.statues = string6[i + 1];// 获取是否成功取得所有问题的状态值
			} else if (string6[i].equals("strQuestionId")) {
				this.num++;
				this.Result = this.Result + divTwoQuestionString
						+ string6[i + 1];
			} else if (string6[i].equals("strTitle")) {
				this.Result = this.Result + divQuestionIDTitleString
						+ string6[i + 1];
			}
		}
	}
}
