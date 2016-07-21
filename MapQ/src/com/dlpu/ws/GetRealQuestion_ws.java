package com.dlpu.ws;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.util.Log;

public class GetRealQuestion_ws {
	private static final String DIV_TAG = "DIVTAG";

	private String theHttpIpString = null;
	private String getWS = "false";

	public String get_getWS() {
		return this.getWS;
	}

	public void setTheHttpIpString(String theHttpIpString) {
		this.theHttpIpString = theHttpIpString;
	}

	private String[][] result = new String[100][3];// Ĭ�ϻظ�����100
	private String statusString = null;// ��ȡ���
	private int numberforreply = 0;
	private String questionuseridString = null;// ������ķ����û�
	private String questionTitleString = null;// ���ʵ�����
	private String questionContentString = null;
	private String questionTimeString = null;
	
	public String getQuestionuseridString() {
		return questionuseridString;
	}

	public String getQuestionTitleString() {
		return questionTitleString;
	}

	public String getQuestionContentString() {
		return questionContentString;
	}

	public String getQuestionTimeString() {
		return questionTimeString;
	}

	public String[][] getresult() {
		return this.result;
	}

	public String getStatusString() {
		return this.statusString;
	}

	public int getnum() {
		return this.numberforreply;
	}

	// WebService����,��ȡ�û���¼��Ϣ
	public void getRealQuestion(String strQuestionID, String wssid) {
		// String endPoint Ϊ���㿪���Ͳ���ʱʹ��(��ʱӦ����ȥ)

		// �����ռ�
		String nameSpace = "http://mapq.com.cn/";
		// ����
		String methodName = "GetQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/GetQuestion";

		// ָ��WebService�������ռ�͵��õķ���
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// ������Ҫ����WebService�ӿ���Ҫ�����5������strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strQuestionId", strQuestionID);
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
		String theStrings = aString.replaceAll("anyType", "\\|");
		String theString1 = theStrings.replaceAll("\\{", "\\|");
		String theString2 = theString1.replaceAll("\\}", "\\|");
		String theString3 = theString2.replaceAll("; ", "\\|");
		String theString4 = theString3.replaceAll("\\|", "=");
		String theString5 = theString4.replaceAll(" ", "");
		String[] contentString = theString5.split("=");
//		for (int i = 0; i < contentString.length; i++) {
//			Log.d("content", contentString[i] + "|" + i);
//		}
		boolean flag = true;// ��ǰ�ж�������Ϣ
		boolean flag1 = false;// �ж��Ƿ���һ����������
		boolean flag2 = false;// �ж��Ƿ���һ���ظ�����
		for (int i = 0, j = 0; i < contentString.length; i++) {
			// ��ȡ������Ϣ
			if (flag) {
				if (contentString[i].equals("strStateInfo")) {
					this.statusString = contentString[i + 1];
				} else if (contentString[i].equals("strUserId")) {
					this.questionuseridString = contentString[i + 1];
				} else if (contentString[i].equals("strTitle")) {
					this.questionTitleString = contentString[i + 1];
				} else if (contentString[i].equals("strContext")) {
					this.questionContentString = contentString[i + 1];
					flag1 = true;
				} else if (contentString[i].equals("dtPublishTime")) {
					this.questionTimeString = contentString[i + 1];
				} else if (flag1) {
					this.questionContentString = contentString[i];
					if (contentString[i + 1].equals("dtPublishTime")) {
						flag1 = false;
					}
				}
				if (contentString[i].equals("CommentList")) {
					flag = false;
				}
			} else {

				// ��ȡ��������û��ظ���Ϣ
				if (contentString[i].equals("strUserName")) {
					// ��ȡ�ظ��û��û���

					this.result[j][0] = contentString[i + 1];// ��j���ظ���ȡ�ظ����û���
					this.numberforreply++;// �ظ���Ŀ��һ
				} else if (contentString[i].equals("dtCommentTime")) {
					// ��ȡ�ظ�ʱ��
					this.result[j][1] = contentString[i + 1];
				} else if (contentString[i].equals("strCommentContext")) {
					// ��ȡ�ظ�����
					this.result[j][2] = "";//��ʼȡֵ
					flag2 = true;
				} else if (flag2) {
					if (contentString[i].equals("Comment")) {
						flag2 = false;// �ظ����ݽ���
						j++;// ��һ���ظ���ʼ
						continue;
					}
					this.result[j][2] = this.result[j][2] + contentString[i];// ��ȫ�ظ�
				}
			}
		}
//		Log.d("statusString", this.statusString);
//		Log.d("questionseridString", questionuseridString);
//		Log.d("questionTitle", this.questionTitleString);
//		Log.d("questionTimeString", this.questionTimeString);
//		Log.d("questionContentString", this.questionContentString);
//		Log.d("num", numberforreply + "");
//		Log.d("questionTime", this.questionTimeString);
//		for (int i = 0; i < numberforreply; i++) {
//			Log.d("result[" + i + "][0]=", this.result[i][0]);
//			Log.d("result[" + i + "][1]=", this.result[i][1]);
//			Log.d("result[" + i + "][2]=", this.result[i][2]);
//		}

	}
}
