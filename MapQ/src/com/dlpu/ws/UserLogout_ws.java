package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class UserLogout_ws {
	
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = "";
	private String getWS = "true";//��WebServiceû�з���ֵ,Ĭ�ϵ��ü��ɹ�ע��
	
	public String get_getWS(){
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}
	

	// WebService����,��ȡ�û���¼��Ϣ
	public void Kill_strWSSID(String strWSSID) {
		// String endPoint Ϊ���㿪���Ͳ���ʱʹ��(��ʱӦ����ȥ)

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "UserLogout";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/UserServer.asmx";
		// String endPoint = "http://10.0.2.2:8081/ws/UserServer.asmx";//
		// �ڰ�׿������ϵ��Է��ʱ���IP
		// soapAction
		String soapAction = "http://mapq.com.cn/UserLogout";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strWSSID", strWSSID);

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
		Log.i(DIV_TAG, "WSSID is end");
		
	}

	
	
}
