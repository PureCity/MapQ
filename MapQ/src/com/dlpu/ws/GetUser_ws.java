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

	// WebService����,��ȡ�û���¼��Ϣ
	public void getUserLoginInfo(String strLoginName, String nCredentialForm, String strPasswd, String strLatitude, String strLongitude) {
		// String endPoint Ϊ���㿪���Ͳ���ʱʹ��(��ʱӦ����ȥ)

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "UserLogin";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/UserServer.asmx";
		// String endPoint = "http://10.0.2.2:8081/ws/UserServer.asmx";//
		// �ڰ�׿������ϵ��Է��ʱ���IP
		// soapAction
		String soapAction = "http://mapq.com.cn/UserLogin";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strLoginName", strLoginName);
		rpcObject.addProperty("nCredentialForm", nCredentialForm);
		rpcObject.addProperty("strPasswd", strPasswd);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);

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
		// ������ԭ�ַ�������:AnyType{bSuccess=false; strUid=dsafdfadfad;
		// strUserName=admin; strStateInfo=err_pw;}
		String[] string_1 = aString.split("\\{");
		String[] string_2 = string_1[1].split("\\}");
		String[] string_3 = string_2[0].split("; ");// �ָ��ֺźͿո�
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
