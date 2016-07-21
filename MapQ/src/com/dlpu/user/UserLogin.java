package com.dlpu.user;

import java.security.NoSuchAlgorithmException;

import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.showmap.Show_BaiduMap;
import com.dlpu.tool.SecurityMD5;
import com.dlpu.ws.GetUser_ws;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserLogin extends ActionBarActivity {

	private EditText usernameEditText = null;
	private EditText userPassword = null;
	private Button sureLoginButton = null;
	private Button registerButton = null;
	private Button cacleButton = null;

	private Handler getInfoHandler = null;
	private static final int SEND_INFO_TO_SERVER = 0;
	private static final int FAIL_GET_WS = -3;// ��ȡWSʧ��
	private static final int SUCCESSFUL_LOGIN = 2;
	private static final int FAIL_LOGIN = -1;
	private static final int INPUT_ERROR = -4;

	private String userID = null;
	private String successfulUserName = null;
	private String userSessionString = null;

	Globalvar setHttp = new Globalvar();

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userlogin_activity);

		// ��ò����е�ָ���ؼ�
		usernameEditText = (EditText) findViewById(R.id.userlogin_loginname);
		userPassword = (EditText) findViewById(R.id.userlogin_password);
		sureLoginButton = (Button) findViewById(R.id.userlogin_sureloginButton);
		registerButton = (Button) findViewById(R.id.userlogin_registeruserButton);
		cacleButton = (Button) findViewById(R.id.userlogin_cacleLoginButton);

		menue_listener aListener = new menue_listener();
		sureLoginButton.setOnClickListener(aListener);
		registerButton.setOnClickListener(aListener);
		cacleButton.setOnClickListener(aListener);

		// �û���Ϣ��������
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SEND_INFO_TO_SERVER:
					Toast.makeText(getApplicationContext(), "�����������������,���Ժ�", Toast.LENGTH_SHORT).show();
					break;
				case FAIL_GET_WS:
					Toast.makeText(getApplicationContext(), "��ȡ��������Ϣʧ��,������������", Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_LOGIN:
					Toast.makeText(getApplicationContext(), "��¼�ɹ�,׼����ת����...", Toast.LENGTH_SHORT).show();
					Intent userLoginIntent = new Intent(UserLogin.this, Show_BaiduMap.class);
					userLoginIntent.putExtra("comStyle", "user");// �û�����(һ���û�)
					userLoginIntent.putExtra("strUid", userID);
					userLoginIntent.putExtra("strUserName", successfulUserName);
					userLoginIntent.putExtra("strStateInfo", userSessionString);
					startActivity(userLoginIntent);// ��ת���ٶȵ�ͼActivity
					break;
				case FAIL_LOGIN:
					Toast.makeText(getApplicationContext(), "��¼ʧ��,�û��������ڻ��������,������ϸ���,������һ��", Toast.LENGTH_SHORT).show();
				case INPUT_ERROR:
					Toast.makeText(getApplicationContext(), "�����û����������ʽ����ȷ", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

	}

	// ��̨�߳�,���ڵ���WebServiceƥ�����ݿ��е��û�
	// ���ڰ�׿�汾�����������߳��з�������
	private class getuser_ws implements Runnable {

		@Override
		public void run() {

			String usernameString = usernameEditText.getText().toString();// ���������û���
			String password = userPassword.getText().toString();// ������������

			if (usernameString.length() <= 3 || password.length() <= 3) {
				Message inputerrorMessage = new Message();
				inputerrorMessage.what = INPUT_ERROR;
				getInfoHandler.sendMessage(inputerrorMessage);
				return;
			}

			// ���������3��MD5����
			SecurityMD5 aMd5 = new SecurityMD5();
			String newpassword = null;
			try {
				newpassword = aMd5.getMD5(password);
				newpassword = aMd5.getMD5(newpassword);
				newpassword = aMd5.getMD5(newpassword);
			} catch (NoSuchAlgorithmException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// ���Բ鿴3��MD5���ܺ����������(��Ҫɾ��)
			Log.d("MD5-3's secret", newpassword);

			// �����̷߳�����ʾ�û�������������������ݵ��ź�
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			GetUser_ws aGetUser_ws = new GetUser_ws();// ����WebService��
			aGetUser_ws.setTheHttpIpString(setHttp.getTheHttpIpString());// �����Ͳ����ڼ�ʹ�ã������л�����
			aGetUser_ws.getUserLoginInfo(usernameString, "0", newpassword, "0", "0");// ����WebService

			// ��ȡ���صĲ���
			String bSuccess = aGetUser_ws.getbSuccess();
			String strUid = aGetUser_ws.getStrUid();
			String strUserName = aGetUser_ws.getStrUserName();
			String strStateInfo = aGetUser_ws.getStrStateInfo();

			if (aGetUser_ws.get_getWS().equals("true")) {
			} else {
				Message aMessage = new Message();
				aMessage.what = FAIL_GET_WS;
				getInfoHandler.sendMessage(aMessage);
				return;
			}

			// ���Բ鿴��ȡ����WebService��Ϣ
			Log.d("getWS-bSuccess", bSuccess);
			Log.d("getWS-strUid", strUid);
			Log.d("getWS-strUserName", strUserName);
			Log.d("getWS-strStateInfo", strStateInfo);

			if (bSuccess.equals("true")) {// �ɹ���¼,���͵�¼�ɹ��ź�
				// ���ص�¼�û����û�ID���û����ͻỰID,׼���������ٶȵ�ͼActivity
				userID = strUid;
				successfulUserName = strUserName;
				userSessionString = strStateInfo;

				Message sucessfulLoginMessage = new Message();
				sucessfulLoginMessage.what = SUCCESSFUL_LOGIN;
				getInfoHandler.sendMessage(sucessfulLoginMessage);
			} else if (bSuccess.equals("false")) {// ��¼ʧ��,����ʧ���ź�
				Message failLoginMessage = new Message();
				failLoginMessage.what = FAIL_LOGIN;
				getInfoHandler.sendMessage(failLoginMessage);
			}

		}

	}

	private class menue_listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.userlogin_sureloginButton:
				// ����WebService�����߳�
				getuser_ws aWs = new getuser_ws();
				Thread aThread = new Thread(aWs);
				aThread.start();
				break;
			case R.id.userlogin_registeruserButton:
				// ǰ��ע�����
				Intent gotoUserRegisterIntent = new Intent(getApplicationContext(), UserRegister.class);
				startActivity(gotoUserRegisterIntent);
				break;
			case R.id.userlogin_cacleLoginButton:
				// ������ǰActivity
				Leave_Program();
				break;
			default:
				break;
			}

		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Leave_Program();
		}
		return super.onKeyDown(keyCode, event);
	}

	// �˳�����
	private void Leave_Program() {
		// �����˳��Ի���
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// ���öԻ������
		isExit.setTitle("MapQ��ܰ����");
		// ���öԻ�����Ϣ
		isExit.setMessage("ȷ��ȡ����¼ô?");
		// ���ѡ��ť��ע�����
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "ȷ��", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "��,��Ҫ��¼", BackListener);
		// ��ʾ�Ի���
		isExit.show();
	}

	/** �����Ի��������button����¼� */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};

	// ��ǰ�˵�ѡ���Ϊ�����Ͳ���ʹ��
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	// ��ǰ�˵����ܽ�Ϊ�����Ͳ���ʹ��
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return true;
	}

}
