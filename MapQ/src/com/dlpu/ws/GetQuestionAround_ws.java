package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.SoapFault;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class GetQuestionAround_ws {
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = null;
	private String getWS = "false";

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String[][] result = new String[100][5];// Ĭ��һ��ֻ��ȡ100�����ڵ�ֵ
	private String statusString = null;// ��ȡ���
	private int numberforquestion = 0;

	public String[][] getresult() {
		return this.result;
	}

	public String getStatusString() {
		return this.statusString;
	}

	public int getnum() {
		return this.numberforquestion;
	}

	// WebService����,��ȡ�û���¼��Ϣ
	public void getNearQuestion(String strUserID, String strLatitude,
			String strLongitude, String nSearchRange, String wssid) {
		// String endPoint Ϊ���㿪���Ͳ���ʱʹ��(��ʱӦ����ȥ)

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "GetNearQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/GetNearQuestion";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strUserId", strUserID);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);
		rpcObject.addProperty("nSearchRange", nSearchRange);
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

		String[] testString = aString.split("; ");
		String[] first_Strings = testString[0].split("=");// first_Strings[1]��ΪWS��StrstateInfo��ֵ
		int num = (testString.length - 2) / 7;// (testString.length-2)/7������ĸ���
		Log.d("num", num + "");
		String[][] resultStrings = new String[num][5];
		if (first_Strings[1].equals("success")) {
			for (int j = 0, i = 0, k = 0; j < testString.length - 2; j++) {
				if (j % 7 == 1 || j % 7 == 2 || j % 7 == 3 || j % 7 == 4
						|| j % 7 == 5) {
					String[] temp = testString[j].split("=");
					resultStrings[i][k] = temp[temp.length - 1];
					k++;
					if (k >= 5) {
						k = 0;
						i++;
					}
				}
			}

		} else {
			Log.d("StrstateInfo", first_Strings[1]);
		}
		numberforquestion = num;
		this.statusString = first_Strings[1];
		if (this.statusString.equals("success")) {
			for (int i = 0; i < num; i++) {
				for (int j = 0; j < 5; j++) {
					this.result[i][j] = resultStrings[i][j];
				}
			}
		}

	}
}
