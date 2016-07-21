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

	// WebService����,��ȡ�û���¼��Ϣ
	public void CreateQuestion(String strUserId, String strLatitude,
			String strLongitude, String strTitle,String strContext, String wssid) {

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "CreateQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/CreateQuestion";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strUserId", strUserId);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);
		rpcObject.addProperty("strTitle", strTitle);
		rpcObject.addProperty("strContext", strContext);
		rpcObject.addProperty("wssid", wssid);

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER10);
		envelope.bodyOut = rpcObject;

		// �����Ƿ���õ���dotNet������WebService
		envelope.dotNet = true;
		// �ȼ���envelope.bodyOut = rpcObject;
		envelope.setOutputSoapObject(rpcObject);

		HttpTransportSE transportSE = new HttpTransportSE(endPoint);

		try {
			// ����WebService
			transportSE.call(soapAction, envelope);
		} catch (Exception e) {
			return;//����ʧ�����̽���
		}
		
		if (envelope.bodyIn instanceof SoapFault) {
		    String str= ((SoapFault) envelope.bodyIn).faultstring;
		    Log.i("", str);
		}
		
		SoapObject getObject = (SoapObject) envelope.bodyIn;
		if (getObject == null) {// ��ȡWebServiceʧ��
			Log.d("Httperror", "Httperror");

			return;
		} else {
			this.getWS = "true";
			Log.d("Http", getObject + "");// ����
		}
		String aString = getObject.getProperty(0).toString();

		Log.i(DIV_TAG, aString);
		divString(aString);// �ָ��ַ���

	}

	// ����ȡ�����ַ����ָ�����
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
