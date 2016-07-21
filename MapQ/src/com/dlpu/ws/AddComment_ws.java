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
	private String resultString = null;// ������۽��,�ɹ��򷵻�true

	public String getResultString() {
		return resultString;
	}

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	// WebService����,��ȡ�û���¼��Ϣ
	public void AddComment(String strQuestionId, String strUserId, String strCommentContext, String wssid) {
		// String endPoint Ϊ���㿪���Ͳ���ʱʹ��(��ʱӦ����ȥ)

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "AddComment";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/AddComment";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strQuestionId", strQuestionId);
		rpcObject.addProperty("strUserId", strUserId);
		rpcObject.addProperty("strCommentContext", strCommentContext);
		rpcObject.addProperty("wssid", wssid);

		// ���ɵ���WebService������SOAP������Ϣ,��ָ��SOAP�汾
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER10);
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
			return;// ����ʧ�����̽���
		}

		SoapObject getObject = (SoapObject) envelope.bodyIn;
		if (getObject == null) {// ��ȡWebServiceʧ��
			Log.d("Httperror", "Httperror");
			return;
		} else {
			this.getWS = "true";
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
		String[] string6 = string5.split("=");

		// for (int i = 0; i < string6.length; i++) {
		// Log.d("content", string6[i] + "|" + i);
		// }
		//
		this.resultString = string6[string6.length - 1];
	}

}
