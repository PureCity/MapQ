package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class GetUser_ws {

	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = "";
	private String getWS = "false";

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String bSuccess = "";
	private String strUid = "";
	private String strUserName = "";
	private String strStateInfo = "";

	public String getbSuccess() {
		return bSuccess;
	}

	public String getStrUid() {
		return strUid;
	}

	public String getStrUserName() {
		return strUserName;
	}

	public String getStrStateInfo() {
		return strStateInfo;
	}

	// WebService调用,获取用户登录信息
	public void getUserLoginInfo(String strLoginName, String nCredentialForm, String strPasswd, String strLatitude, String strLongitude) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "UserLogin";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/UserServer.asmx";
		// String endPoint = "http://10.0.2.2:8081/ws/UserServer.asmx";//
		// 在安卓虚拟机上调试访问本机IP
		// soapAction
		String soapAction = "http://mapq.com.cn/UserLogin";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strLoginName", strLoginName);
		rpcObject.addProperty("nCredentialForm", nCredentialForm);
		rpcObject.addProperty("strPasswd", strPasswd);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);

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
		// 产生的原字符串类似:AnyType{bSuccess=false; strUid=dsafdfadfad;
		// strUserName=admin; strStateInfo=err_pw;}
		String[] string_1 = aString.split("\\{");
		String[] string_2 = string_1[1].split("\\}");
		String[] string_3 = string_2[0].split("; ");// 分隔分号和空格
		String[][] string_4 = new String[4][2];
		String[] stringNameStrings = new String[4];
		stringNameStrings[0] = "bSuccess";
		stringNameStrings[1] = "strUid";
		stringNameStrings[2] = "strUserName";
		stringNameStrings[3] = "strStateInfo";
		for (int i = 0; i < string_3.length && i < 4; i++) {
			string_4[i] = string_3[i].split("=");
		}

		for (int i = 0; i < string_3.length && i < 4; i++) {
			for (int j = 0; j < stringNameStrings.length; j++) {

				if (string_4[i][0].equals(stringNameStrings[j])) {
					switch (j) {
					case 0:
						this.bSuccess = string_4[i][1];
						break;
					case 1:
						this.strUid = string_4[i][1];
						break;
					case 2:
						this.strUserName = string_4[i][1];
						break;
					case 3:
						this.strStateInfo = string_4[i][1];
						break;
					default:
						break;
					}
				}
			}
		}

	}

}
