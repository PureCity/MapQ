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

	// 给予用户的反馈信息
	private Handler getInfoHandler = null;
	private static final int SEND_INFO_TO_SERVER = 0;// 向服务器发送信息
	private static final int SUCCESSFUL_GET_WEBSERVICE = 1;// 成功从服务器获得信息
	private static final int SUCCESSFUL_REG = 2;// 注册成功
	private static final int NAME_CAN_USE = 3;// 名称可用

	private static final int PW_NOT_LIKE = -1;// 两次密码不匹配,或者位数小于6或大于20
	private static final int NAME_ILLEGE = -2;// 用户名不合法
	private static final int NAME_CNAT_BE_USE = -3;// 用户名已存在
	private static final int EMAIL_USED = -4;// 邮箱已使用
	private static final int EMAiL_ILLEGE = -5;// 邮箱不合法
	private static final int SYS_REGNAME = -6;// 注册失败,失败值用户名
	private static final int REG_NAME_FAIL = -7;// 未检测用户名是否可用
	private static final int REG_INFO = -8;// 未填写完整注册信息
	private static final int SYS_REGPW = -9;// 注册失败,失败值密码
	private static final int SYS_ERR = -10;// 注册失败,未知错误
	private static final int CODE_ERR = -11;// 验证码错误
	private static final int FAIL_WS = -12;// 获取服务器信息失败

	// 默认的控件声明
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

	// 局部控制
	boolean canreg_name = false;// 未检查用户名是否可用时,不能成功点击确认注册按钮进行注册
	boolean canreg_info = false;// 信息未填写完整时,不能成功点击确认注册按钮进行注册

	// 验证码
	CreateCode aCreateCode = new CreateCode();// 初始化验证码类
	private String checkCode = "1+1=";

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.userregister_activity);

		// 用户信息反馈处理
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SEND_INFO_TO_SERVER:
					Toast.makeText(getApplicationContext(), "向服务器请求数据中,请稍后",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_GET_WEBSERVICE:
					Toast.makeText(getApplicationContext(), "从服务器获取信息完成",
							Toast.LENGTH_SHORT).show();
					break;
				case NAME_CAN_USE:
					Toast.makeText(getApplicationContext(), "该用户名可以使用",
							Toast.LENGTH_SHORT).show();
					canreg_name = true;//设置可以点击确认注册按钮
					break;
				case NAME_CNAT_BE_USE:
					Toast.makeText(getApplicationContext(), "该用户名已存在",
							Toast.LENGTH_SHORT).show();
					break;
				case REG_NAME_FAIL:
					Toast.makeText(getApplicationContext(), "请先检查该用户名是否可用",
							Toast.LENGTH_SHORT).show();
					break;
				case REG_INFO:
					Toast.makeText(getApplicationContext(), "请填写完整注册信息",
							Toast.LENGTH_SHORT).show();
					break;
				case NAME_ILLEGE:
					Toast.makeText(getApplicationContext(),
							"用户名不合法,请不要使用特殊字符,且保证位数在3-20位之间",
							Toast.LENGTH_SHORT).show();
					break;
				case EMAIL_USED:
					Toast.makeText(getApplicationContext(), "注册失败,该邮箱已被注册",
							Toast.LENGTH_SHORT).show();
					break;
				case EMAiL_ILLEGE:
					Toast.makeText(getApplicationContext(), "邮箱不合法长度3-20之内",
							Toast.LENGTH_SHORT).show();
					break;
				case PW_NOT_LIKE:
					Toast.makeText(getApplicationContext(), "两次密码不匹配,长度6-20之内",
							Toast.LENGTH_SHORT).show();
					break;
				case CODE_ERR:
					Toast.makeText(getApplicationContext(), "验证码错误,请重试",
							Toast.LENGTH_SHORT).show();
					aCreateCode.updateCode();// 刷新新的验证码
					checkCode = aCreateCode.getCodeString();
					Log.d("checkCode", checkCode);
					userregister_rcodeTextView.setText(checkCode.charAt(0)+" "
							+ checkCode.charAt(1)+" " + checkCode.charAt(2)+" "
							+ checkCode.charAt(3));
					break;
				case SYS_REGNAME:
					Toast.makeText(getApplicationContext(), "注册失败,用户名问题,请反馈",
							Toast.LENGTH_SHORT).show();
					break;
				case SYS_REGPW:
					Toast.makeText(getApplicationContext(), "注册失败,密码问题,请反馈",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_REG:
					Toast.makeText(getApplicationContext(),
							"注册成功,请登录,享受MapQ从现在开始...", Toast.LENGTH_SHORT)
							.show();
					finish();
					break;
				case SYS_ERR:
					Toast.makeText(getApplicationContext(),
							"注册失败,未知错误,请确保网络连接正常", Toast.LENGTH_SHORT).show();
					break;
				case FAIL_WS:
					Toast.makeText(getApplicationContext(),
							"获取服务器信息失败,请检查您的网络", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

		// 获取对应控件
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

		// 添加按钮监听
		menue_listener amenListener = new menue_listener();
		userregister_checkUserNameButton.setOnClickListener(amenListener);
		userregister_sureregisterButton.setOnClickListener(amenListener);
		userregister_clearButton.setOnClickListener(amenListener);
		userregister_calceButton.setOnClickListener(amenListener);

		// 设置初始验证码
		aCreateCode.updateCode();
		checkCode = aCreateCode.getCodeString();
		Log.d("checkCode", checkCode);
		userregister_rcodeTextView.setText(checkCode.charAt(0) +" "
				+ checkCode.charAt(1) +" " + checkCode.charAt(2) +" "
				+ checkCode.charAt(3));

	}

	// 后台线程,用于调用WebService匹配数据库中的用户
	// 由于安卓版本不允许在主线程中访问网络
	private class CheckUserExists implements Runnable {

		@Override
		public void run() {
			String get_strUserName = userregister_inputNameEditText.getText()
					.toString();

			// 给主线程发送提示用户正在向服务器传递数据的信号
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			CheckUserExists_ws theCheckUserExists_ws = new CheckUserExists_ws();// 调用WebService
			theCheckUserExists_ws.setTheHttpIpString(setHttp
					.getTheHttpIpString());// 测试,网络切换
			theCheckUserExists_ws.CheckUser(get_strUserName);

			if (theCheckUserExists_ws.get_getWS().equals("false")) {// WS调用失败
				Message aMessage = new Message();
				aMessage.what = FAIL_WS;
				getInfoHandler.sendMessage(aMessage);
				return;
			}

			String result = theCheckUserExists_ws.getCheckUsrExistsResult();// 获得服务器的结果
			if (result.equals("true")) {// 已存在该用户名
				Message nameExistsMessage = new Message();
				nameExistsMessage.what = NAME_CNAT_BE_USE;
				getInfoHandler.sendMessage(nameExistsMessage);
			} else if (result.equals("false")) {// 名称不存在,可以被注册
				Message nameCanUserMessage = new Message();
				nameCanUserMessage.what = NAME_CAN_USE;
				getInfoHandler.sendMessage(nameCanUserMessage);
			}

		}

	}

	// 检验验证码是否输入正确
	private boolean checktheCode() {
		String getCodeString = userregister_inputcodeEditText.getText()
				.toString();
		if (getCodeString.equalsIgnoreCase(checkCode)) {
			return true;
		} else {
			return false;
		}
	}

	// 检验邮箱填写是否合法
	private boolean checkEmailIllege() {// 邮箱字符需包含"@",不允许其他特殊字符,长度大于3,小于50
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

	// 判断两次密码是否一致
	private boolean judgePassword() {// 密码必须大于6位,小于等于20位,字符任意,两次输入必须一致
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

			// 给主线程发送提示用户正在向服务器传递数据的信号
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			CheckEmailExists_ws theCheckEmailExists_ws = new CheckEmailExists_ws();
			theCheckEmailExists_ws.setTheHttpIpString(setHttp
					.getTheHttpIpString());// 切换网络
			theCheckEmailExists_ws.CheckEmail(get_strEmailString);

			if (theCheckEmailExists_ws.get_getWS().equals("false")) {// WS调用失败
				Message aMessage = new Message();
				aMessage.what = FAIL_WS;
				getInfoHandler.sendMessage(aMessage);
				return;
			}

			String resultString = theCheckEmailExists_ws
					.getCheckEmailExistsResult();
			if (resultString.equals("true")) {// 已经存在该邮箱
				Message emailUsedMessage = new Message();
				emailUsedMessage.what = EMAIL_USED;
				getInfoHandler.sendMessage(emailUsedMessage);// 发送邮箱已使用的信号
			} else {// 一切检查合格,调用WS新增用户
				String get_strUserName = userregister_inputNameEditText
						.getText().toString();
				RegisterUser_ws registerUser_ws = new RegisterUser_ws();
				registerUser_ws
						.setTheHttpIpString(setHttp.getTheHttpIpString());
				String pw1 = userregister_inputPassword_1.getText().toString();
				SecurityMD5 aSecurityMD5 = new SecurityMD5();
				String Password = null;
				try {// 密码通过3次加密
					Password = aSecurityMD5.getMD5(pw1);
					Password = aSecurityMD5.getMD5(Password);
					Password = aSecurityMD5.getMD5(Password);
				} catch (NoSuchAlgorithmException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.d("Password", Password);
				registerUser_ws.RegUser(get_strUserName, Password,
						get_strEmailString, "0", "0");// 调用WS，新增用户

				if (registerUser_ws.get_getWS().equals("false")) {// WS调用失败
					Message aMessage = new Message();
					aMessage.what = FAIL_WS;
					getInfoHandler.sendMessage(aMessage);
					return;
				}

				String rssultString = registerUser_ws.getRegNewUserResult();
				if (rssultString.equals("success")) {// 注册成功
					Message regSuccessSendMessage = new Message();
					regSuccessSendMessage.what = SUCCESSFUL_REG;
					getInfoHandler.sendMessage(regSuccessSendMessage);
				} else if (rssultString.equals("err_password")) {// 注册密码错误
					Message regpwdSendMessage = new Message();
					regpwdSendMessage.what = SYS_REGPW;
					getInfoHandler.sendMessage(regpwdSendMessage);
				} else if (rssultString.equals("err_username")) {// 用户名错误
					Message regpNameSendMessage = new Message();
					regpNameSendMessage.what = SYS_REGNAME;
					getInfoHandler.sendMessage(regpNameSendMessage);
				} else {// 未知错误
					Message regpNoneSendMessage = new Message();
					regpNoneSendMessage.what = SYS_ERR;
					getInfoHandler.sendMessage(regpNoneSendMessage);
				}

			}

		}

	}

	// 检查输入的用户名是否合法
	private boolean checkUserNameIllege(String nameString) {// 用户名大于3位，小于20位，不得包含特殊字符
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
				if (checkUserNameIllege(get_strUserName)) {// 检查用户名是否合法,合法则启动WebService
					// 启动WebService服务线程,检测该用户名是否已经存在
					CheckUserExists aCheckUserExists = new CheckUserExists();
					Thread checkUserNameThread = new Thread(aCheckUserExists);
					checkUserNameThread.start();
				} else {// 用户名不合法
					Message err_nameMessage = new Message();
					err_nameMessage.what = NAME_ILLEGE;
					getInfoHandler.sendMessage(err_nameMessage);
				}
				break;
			case R.id.userregister_clear_Button:
				// 将当前页面的编辑框内容清空
				userregister_inputNameEditText.setText("");
				userregister_inputEmailEditText.setText("");
				userregister_inputPassword_1.setText("");
				userregister_inputPassword_2.setText("");
				userregister_inputcodeEditText.setText("");
				break;
			case R.id.userregister_cacle_Button:
				// 结束当前Activity
				Leave_Program();
				break;
			case R.id.userregister_sureregister_Button:
				if (canreg_name) {// 用户名检测成功
					// 检查两次密码是否一致
					if (judgePassword()) {// 两次密码一致
						if (checktheCode()) {// 检验验证码是否匹配
							if (checkEmailIllege()) {// 检测邮箱是否合法
								// 启动线程,检查邮箱是否存在
								CheckEmailExists aCheckEmailExists = new CheckEmailExists();
								Thread checkEmailThread = new Thread(
										aCheckEmailExists);
								checkEmailThread.start();
							} else {// 邮箱不合法
								Message EmailillegeMessage = new Message();
								EmailillegeMessage.what = EMAiL_ILLEGE;
								getInfoHandler.sendMessage(EmailillegeMessage);
							}
						} else {// 验证码不正确
							Message codeMessage = new Message();
							codeMessage.what = CODE_ERR;
							getInfoHandler.sendMessage(codeMessage);
						}
					} else {// 两次密码不匹配
						Message pwMessage = new Message();
						pwMessage.what = PW_NOT_LIKE;
						getInfoHandler.sendMessage(pwMessage);
					}
				} else {// 未检测用户名是否可用
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

	// 退出程序
	private void Leave_Program() {
		// 创建退出对话框
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// 设置对话框标题
		isExit.setTitle("MapQ温馨提醒");
		// 设置对话框消息
		isExit.setMessage("确定取消注册么?");
		// 添加选择按钮并注册监听
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "确定", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "不,我要注册", BackListener);
		// 显示对话框
		isExit.show();
	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	// 当前菜单选项仅为开发和测试使用
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		return super.onCreateOptionsMenu(menu);
	}

	// 当前菜单功能仅为开发和测试使用
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		return true;
	}

}
