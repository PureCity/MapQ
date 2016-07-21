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

	private String[][] result = new String[100][5];// 默认一次只提取100个以内的值
	private String statusString = null;// 获取情况
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

	// WebService调用,获取用户登录信息
	public void getNearQuestion(String strUserID, String strLatitude,
			String strLongitude, String nSearchRange, String wssid) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "GetNearQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/GetNearQuestion";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strUserId", strUserID);
		rpcObject.addProperty("strLatitude", strLatitude);
		rpcObject.addProperty("strLongitude", strLongitude);
		rpcObject.addProperty("nSearchRange", nSearchRange);
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
			return;//调用失败立刻结束
		}
		
		if (envelope.bodyIn instanceof SoapFault) {
		    String str= ((SoapFault) envelope.bodyIn).faultstring;
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

		String[] testString = aString.split("; ");
		String[] first_Strings = testString[0].split("=");// first_Strings[1]即为WS中StrstateInfo的值
		int num = (testString.length - 2) / 7;// (testString.length-2)/7即问题的个数
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
