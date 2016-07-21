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

	private String[][] result = new String[100][3];// 默认回复上限100
	private String statusString = null;// 获取情况
	private int numberforreply = 0;
	private String questionuseridString = null;// 该问题的发问用户
	private String questionTitleString = null;// 发问的问题
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

	// WebService调用,获取用户登录信息
	public void getRealQuestion(String strQuestionID, String wssid) {
		// String endPoint 为方便开发和测试时使用(届时应当除去)

		// 命名空间
		String nameSpace = "http://mapq.com.cn/";
		// 方法
		String methodName = "GetQuestion";
		// endPoint
		String endPoint = this.theHttpIpString + "/ws/PublishServer.asmx";
		// soapAction
		String soapAction = "http://mapq.com.cn/GetQuestion";

		// 指定WebService的命名空间和调用的方法
		SoapObject rpcObject = new SoapObject(nameSpace, methodName);

		// 设置需要调用WebService接口需要传入的5个参数strLoginName,nCredentialForm,strPasswd,strLatitude,strLongitude
		rpcObject.addProperty("strQuestionId", strQuestionID);
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
		boolean flag = true;// 当前判断提问信息
		boolean flag1 = false;// 判断是否是一个提问内容
		boolean flag2 = false;// 判定是否是一个回复内容
		for (int i = 0, j = 0; i < contentString.length; i++) {
			// 获取提问信息
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

				// 提取该问题的用户回复信息
				if (contentString[i].equals("strUserName")) {
					// 提取回复用户用户名

					this.result[j][0] = contentString[i + 1];// 第j个回复获取回复的用户名
					this.numberforreply++;// 回复数目加一
				} else if (contentString[i].equals("dtCommentTime")) {
					// 获取回复时间
					this.result[j][1] = contentString[i + 1];
				} else if (contentString[i].equals("strCommentContext")) {
					// 获取回复内容
					this.result[j][2] = "";//开始取值
					flag2 = true;
				} else if (flag2) {
					if (contentString[i].equals("Comment")) {
						flag2 = false;// 回复内容结束
						j++;// 下一条回复开始
						continue;
					}
					this.result[j][2] = this.result[j][2] + contentString[i];// 补全回复
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
