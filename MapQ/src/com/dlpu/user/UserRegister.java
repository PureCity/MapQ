package com.dlpu.user;

import java.security.NoSuchAlgorithmException;

import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.tool.CreateCode;
import com.dlpu.tool.SecurityMD5;
import com.dlpu.ws.CheckEmailExists_ws;
import com.dlpu.ws.CheckUserExists_ws;
import com.dlpu.ws.RegisterUser_ws;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
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
import android.widget.TextView;
import android.widget.Toast;

public class UserRegister extends ActionBarActivity {

	Globalvar setHttp = new Globalvar();

	// �����û��ķ�����Ϣ
	private Handler getInfoHandler = null;
	private static final int SEND_INFO_TO_SERVER = 0;// �������������Ϣ
	private static final int SUCCESSFUL_GET_WEBSERVICE = 1;// �ɹ��ӷ����������Ϣ
	private static final int SUCCESSFUL_REG = 2;// ע��ɹ�
	private static final int NAME_CAN_USE = 3;// ���ƿ���

	private static final int PW_NOT_LIKE = -1;// �������벻ƥ��,����λ��С��6�����20
	private static final int NAME_ILLEGE = -2;// �û������Ϸ�
	private static final int NAME_CNAT_BE_USE = -3;// �û����Ѵ���
	private static final int EMAIL_USED = -4;// ������ʹ��
	private static final int EMAiL_ILLEGE = -5;// ���䲻�Ϸ�
	private static final int SYS_REGNAME = -6;// ע��ʧ��,ʧ��ֵ�û���
	private static final int REG_NAME_FAIL = -7;// δ����û����Ƿ����
	private static final int REG_INFO = -8;// δ��д����ע����Ϣ
	private static final int SYS_REGPW = -9;// ע��ʧ��,ʧ��ֵ����
	private static final int SYS_ERR = -10;// ע��ʧ��,δ֪����
	private static final int CODE_ERR = -11;// ��֤�����
	private static final int FAIL_WS = -12;// ��ȡ��������Ϣʧ��

	// Ĭ�ϵĿؼ�����
	private EditText userregister_inputNameEditText = null;
	private EditText userregister_inputPassword_1 = null;
	private EditText userregister_inputPassword_2 = null;
	private EditText userregister_inputEmailEditText = null;
	private EditText userregister_inputcodeEditText = null;
	private Button userregister_checkUserNameButton = null;
	private Button userregister_sureregisterButton = null;
	private Button userregister_clearButton = null;
	private Button userregister_calceButton = null;
	private TextView userregister_rcodeTextView = null;

	// �ֲ�����
	boolean canreg_name = false;// δ����û����Ƿ����ʱ,���ܳɹ����ȷ��ע�ᰴť����ע��
	boolean canreg_info = false;// ��Ϣδ��д����ʱ,���ܳɹ����ȷ��ע�ᰴť����ע��

	// ��֤��
	CreateCode aCreateCode = new CreateCode();// ��ʼ����֤����
	private String checkCode = "1+1=";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userregister_activity);

		// �û���Ϣ��������
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SEND_INFO_TO_SERVER:
					Toast.makeText(getApplicationContext(), "�����������������,���Ժ�",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_GET_WEBSERVICE:
					Toast.makeText(getApplicationContext(), "�ӷ�������ȡ��Ϣ���",
							Toast.LENGTH_SHORT).show();
					break;
				case NAME_CAN_USE:
					Toast.makeText(getApplicationContext(), "���û�������ʹ��",
							Toast.LENGTH_SHORT).show();
					canreg_name = true;//���ÿ��Ե��ȷ��ע�ᰴť
					break;
				case NAME_CNAT_BE_USE:
					Toast.makeText(getApplicationContext(), "���û����Ѵ���",
							Toast.LENGTH_SHORT).show();
					break;
				case REG_NAME_FAIL:
					Toast.makeText(getApplicationContext(), "���ȼ����û����Ƿ����",
							Toast.LENGTH_SHORT).show();
					break;
				case REG_INFO:
					Toast.makeText(getApplicationContext(), "����д����ע����Ϣ",
							Toast.LENGTH_SHORT).show();
					break;
				case NAME_ILLEGE:
					Toast.makeText(getApplicationContext(),
							"�û������Ϸ�,�벻Ҫʹ�������ַ�,�ұ�֤λ����3-20λ֮��",
							Toast.LENGTH_SHORT).show();
					break;
				case EMAIL_USED:
					Toast.makeText(getApplicationContext(), "ע��ʧ��,�������ѱ�ע��",
							Toast.LENGTH_SHORT).show();
					break;
				case EMAiL_ILLEGE:
					Toast.makeText(getApplicationContext(), "���䲻�Ϸ�����3-20֮��",
							Toast.LENGTH_SHORT).show();
					break;
				case PW_NOT_LIKE:
					Toast.makeText(getApplicationContext(), "�������벻ƥ��,����6-20֮��",
							Toast.LENGTH_SHORT).show();
					break;
				case CODE_ERR:
					Toast.makeText(getApplicationContext(), "��֤�����,������",
							Toast.LENGTH_SHORT).show();
					aCreateCode.updateCode();// ˢ���µ���֤��
					checkCode = aCreateCode.getCodeString();
					Log.d("checkCode", checkCode);
					userregister_rcodeTextView.setText(checkCode.charAt(0)+" "
							+ checkCode.charAt(1)+" " + checkCode.charAt(2)+" "
							+ checkCode.charAt(3));
					break;
				case SYS_REGNAME:
					Toast.makeText(getApplicationContext(), "ע��ʧ��,�û�������,�뷴��",
							Toast.LENGTH_SHORT).show();
					break;
				case SYS_REGPW:
					Toast.makeText(getApplicationContext(), "ע��ʧ��,��������,�뷴��",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_REG:
					Toast.makeText(getApplicationContext(),
							"ע��ɹ�,���¼,����MapQ�����ڿ�ʼ...", Toast.LENGTH_SHORT)
							.show();
					finish();
					break;
				case SYS_ERR:
					Toast.makeText(getApplicationContext(),
							"ע��ʧ��,δ֪����,��ȷ��������������", Toast.LENGTH_SHORT).show();
					break;
				case FAIL_WS:
					Toast.makeText(getApplicationContext(),
							"��ȡ��������Ϣʧ��,������������", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

		// ��ȡ��Ӧ�ؼ�
		userregister_inputNameEditText = (EditText) findViewById(R.id.userregister_name);
		userregister_inputPassword_1 = (EditText) findViewById(R.id.userregister_password_1);
		userregister_inputPassword_2 = (EditText) findViewById(R.id.userregister_password_2);
		userregister_inputEmailEditText = (EditText) findViewById(R.id.userregister_email);
		userregister_inputcodeEditText = (EditText) findViewById(R.id.userregister_code_input);
		userregister_rcodeTextView = (TextView) findViewById(R.id.userregister_code);
		userregister_checkUserNameButton = (Button) findViewById(R.id.userregister_checkName_button);
		userregister_sureregisterButton = (Button) findViewById(R.id.userregister_sureregister_Button);
		userregister_clearButton = (Button) findViewById(R.id.userregister_clear_Button);
		userregister_calceButton = (Button) findViewById(R.id.userregister_cacle_Button);

		// ��Ӱ�ť����
		menue_listener amenListener = new menue_listener();
		userregister_checkUserNameButton.setOnClickListener(amenListener);
		userregister_sureregisterButton.setOnClickListener(amenListener);
		userregister_clearButton.setOnClickListener(amenListener);
		userregister_calceButton.setOnClickListener(amenListener);

		// ���ó�ʼ��֤��
		aCreateCode.updateCode();
		checkCode = aCreateCode.getCodeString();
		Log.d("checkCode", checkCode);
		userregister_rcodeTextView.setText(checkCode.charAt(0) +" "
				+ checkCode.charAt(1) +" " + checkCode.charAt(2) +" "
				+ checkCode.charAt(3));

	}

	// ��̨�߳�,���ڵ���WebServiceƥ�����ݿ��е��û�
	// ���ڰ�׿�汾�����������߳��з�������
	private class CheckUserExists implements Runnable {

		@Override
		public void run() {
			String get_strUserName = userregister_inputNameEditText.getText()
					.toString();

			// �����̷߳�����ʾ�û�������������������ݵ��ź�
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			CheckUserExists_ws theCheckUserExists_ws = new CheckUserExists_ws();// ����WebService
			theCheckUserExists_ws.setTheHttpIpString(setHttp
					.getTheHttpIpString());// ����,�����л�
			theCheckUserExists_ws.CheckUser(get_strUserName);

			if (theCheckUserExists_ws.get_getWS().equals("false")) {// WS����ʧ��
				Message aMessage = new Message();
				aMessage.what = FAIL_WS;
				getInfoHandler.sendMessage(aMessage);
				return;
			}

			String result = theCheckUserExists_ws.getCheckUsrExistsResult();// ��÷������Ľ��
			if (result.equals("true")) {// �Ѵ��ڸ��û���
				Message nameExistsMessage = new Message();
				nameExistsMessage.what = NAME_CNAT_BE_USE;
				getInfoHandler.sendMessage(nameExistsMessage);
			} else if (result.equals("false")) {// ���Ʋ�����,���Ա�ע��
				Message nameCanUserMessage = new Message();
				nameCanUserMessage.what = NAME_CAN_USE;
				getInfoHandler.sendMessage(nameCanUserMessage);
			}

		}

	}

	// ������֤���Ƿ�������ȷ
	private boolean checktheCode() {
		String getCodeString = userregister_inputcodeEditText.getText()
				.toString();
		if (getCodeString.equalsIgnoreCase(checkCode)) {
			return true;
		} else {
			return false;
		}
	}

	// ����������д�Ƿ�Ϸ�
	private boolean checkEmailIllege() {// �����ַ������"@",���������������ַ�,���ȴ���3,С��50
		boolean flag = false;
		String getEmailString = userregister_inputEmailEditText.getText()
				.toString();
		if (getEmailString.length() <= 3 || getEmailString.length() >= 50) {
			return false;
		}
		for (int i = 0; i < getEmailString.length(); i++) {
			if (getEmailString.charAt(i) == '@') {
				flag = true;
			}
		}
		if (flag) {
			char[] err_code = { '-', '+', '*', '\\', '|', ',', '&', '%', '$',
					'#', '!', '^', '{', '}', '[', ']', '~', '(', ')', '?', '<',
					'>', ':', '\'', '\"', ';', ' ' };
			for (int i = 0; i < getEmailString.length(); i++) {
				for (int j = 0; j < err_code.length; j++) {
					if (getEmailString.charAt(i) == err_code[j]) {
						Log.d("checkEmailIllegge", "end in false");
						return false;
					}
				}
			}
			return true;
		} else {
			return false;
		}
	}

	// �ж����������Ƿ�һ��
	private boolean judgePassword() {// ����������6λ,С�ڵ���20λ,�ַ�����,�����������һ��
		String pw1 = userregister_inputPassword_1.getText().toString();
		String pw2 = userregister_inputPassword_2.getText().toString();
		if (pw1.length() < 6 || pw2.length() < 6) {
			return false;
		}
		if (pw1.length() >= 20 || pw2.length() >= 20) {
			return false;
		}
		if (pw1.equals(pw2)) {
			return true;
		} else {
			return false;
		}
	}

	private class CheckEmailExists implements Runnable {

		@Override
		public void run() {
			String get_strEmailString = userregister_inputEmailEditText
					.getText().toString();

			// �����̷߳�����ʾ�û�������������������ݵ��ź�
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			CheckEmailExists_ws theCheckEmailExists_ws = new CheckEmailExists_ws();
			theCheckEmailExists_ws.setTheHttpIpString(setHttp
					.getTheHttpIpString());// �л�����
			theCheckEmailExists_ws.CheckEmail(get_strEmailString);

			if (theCheckEmailExists_ws.get_getWS().equals("false")) {// WS����ʧ��
				Message aMessage = new Message();
				aMessage.what = FAIL_WS;
				getInfoHandler.sendMessage(aMessage);
				return;
			}

			String resultString = theCheckEmailExists_ws
					.getCheckEmailExistsResult();
			if (resultString.equals("true")) {// �Ѿ����ڸ�����
				Message emailUsedMessage = new Message();
				emailUsedMessage.what = EMAIL_USED;
				getInfoHandler.sendMessage(emailUsedMessage);// ����������ʹ�õ��ź�
			} else {// һ�м��ϸ�,����WS�����û�
				String get_strUserName = userregister_inputNameEditText
						.getText().toString();
				RegisterUser_ws registerUser_ws = new RegisterUser_ws();
				registerUser_ws
						.setTheHttpIpString(setHttp.getTheHttpIpString());
				String pw1 = userregister_inputPassword_1.getText().toString();
				SecurityMD5 aSecurityMD5 = new SecurityMD5();
				String Password = null;
				try {// ����ͨ��3�μ���
					Password = aSecurityMD5.getMD5(pw1);
					Password = aSecurityMD5.getMD5(Password);
					Password = aSecurityMD5.getMD5(Password);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Password", Password);
				registerUser_ws.RegUser(get_strUserName, Password,
						get_strEmailString, "0", "0");// ����WS�������û�

				if (registerUser_ws.get_getWS().equals("false")) {// WS����ʧ��
					Message aMessage = new Message();
					aMessage.what = FAIL_WS;
					getInfoHandler.sendMessage(aMessage);
					return;
				}

				String rssultString = registerUser_ws.getRegNewUserResult();
				if (rssultString.equals("success")) {// ע��ɹ�
					Message regSuccessSendMessage = new Message();
					regSuccessSendMessage.what = SUCCESSFUL_REG;
					getInfoHandler.sendMessage(regSuccessSendMessage);
				} else if (rssultString.equals("err_password")) {// ע���������
					Message regpwdSendMessage = new Message();
					regpwdSendMessage.what = SYS_REGPW;
					getInfoHandler.sendMessage(regpwdSendMessage);
				} else if (rssultString.equals("err_username")) {// �û�������
					Message regpNameSendMessage = new Message();
					regpNameSendMessage.what = SYS_REGNAME;
					getInfoHandler.sendMessage(regpNameSendMessage);
				} else {// δ֪����
					Message regpNoneSendMessage = new Message();
					regpNoneSendMessage.what = SYS_ERR;
					getInfoHandler.sendMessage(regpNoneSendMessage);
				}

			}

		}

	}

	// ���������û����Ƿ�Ϸ�
	private boolean checkUserNameIllege(String nameString) {// �û�������3λ��С��20λ�����ð��������ַ�
		Log.d("checkUserNameIllegge", "beagin");
		char[] err_code = { '-', '+', '*', '\\', '|', '.', ',', '&', '%', '$',
				'#', '!', '^', '{', '}', '[', ']', '~', '(', ')', '?', '<',
				'>', ':', '\'', '\"', ';', ' ' };
		if (nameString.length() <= 3 || nameString.length() >= 20) {
			return false;
		}
		for (int i = 0; i < nameString.length(); i++) {
			for (int j = 0; j < err_code.length; j++) {
				if (nameString.charAt(i) == err_code[j]) {
					Log.d("checkUserNameIllegge", "end in false");
					return false;
				}
			}
		}
		Log.d("checkUserNameIllegge", "end in ture");
		return true;
	}

	private class menue_listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.userregister_checkName_button:
				String get_strUserName = userregister_inputNameEditText
						.getText().toString();
				if (checkUserNameIllege(get_strUserName)) {// ����û����Ƿ�Ϸ�,�Ϸ�������WebService
					// ����WebService�����߳�,�����û����Ƿ��Ѿ�����
					CheckUserExists aCheckUserExists = new CheckUserExists();
					Thread checkUserNameThread = new Thread(aCheckUserExists);
					checkUserNameThread.start();
				} else {// �û������Ϸ�
					Message err_nameMessage = new Message();
					err_nameMessage.what = NAME_ILLEGE;
					getInfoHandler.sendMessage(err_nameMessage);
				}
				break;
			case R.id.userregister_clear_Button:
				// ����ǰҳ��ı༭���������
				userregister_inputNameEditText.setText("");
				userregister_inputEmailEditText.setText("");
				userregister_inputPassword_1.setText("");
				userregister_inputPassword_2.setText("");
				userregister_inputcodeEditText.setText("");
				break;
			case R.id.userregister_cacle_Button:
				// ������ǰActivity
				Leave_Program();
				break;
			case R.id.userregister_sureregister_Button:
				if (canreg_name) {// �û������ɹ�
					// ������������Ƿ�һ��
					if (judgePassword()) {// ��������һ��
						if (checktheCode()) {// ������֤���Ƿ�ƥ��
							if (checkEmailIllege()) {// ��������Ƿ�Ϸ�
								// �����߳�,��������Ƿ����
								CheckEmailExists aCheckEmailExists = new CheckEmailExists();
								Thread checkEmailThread = new Thread(
										aCheckEmailExists);
								checkEmailThread.start();
							} else {// ���䲻�Ϸ�
								Message EmailillegeMessage = new Message();
								EmailillegeMessage.what = EMAiL_ILLEGE;
								getInfoHandler.sendMessage(EmailillegeMessage);
							}
						} else {// ��֤�벻��ȷ
							Message codeMessage = new Message();
							codeMessage.what = CODE_ERR;
							getInfoHandler.sendMessage(codeMessage);
						}
					} else {// �������벻ƥ��
						Message pwMessage = new Message();
						pwMessage.what = PW_NOT_LIKE;
						getInfoHandler.sendMessage(pwMessage);
					}
				} else {// δ����û����Ƿ����
					Message canreg_name_errMessage = new Message();
					canreg_name_errMessage.what = REG_NAME_FAIL;
					getInfoHandler.sendMessage(canreg_name_errMessage);
				}
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
		isExit.setMessage("ȷ��ȡ��ע��ô?");
		// ���ѡ��ť��ע�����
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "ȷ��", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "��,��Ҫע��", BackListener);
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
