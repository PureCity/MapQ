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
	private static final int FAIL_GET_WS = -3;// 获取WS失败
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

		// 获得布局中的指定控件
		usernameEditText = (EditText) findViewById(R.id.userlogin_loginname);
		userPassword = (EditText) findViewById(R.id.userlogin_password);
		sureLoginButton = (Button) findViewById(R.id.userlogin_sureloginButton);
		registerButton = (Button) findViewById(R.id.userlogin_registeruserButton);
		cacleButton = (Button) findViewById(R.id.userlogin_cacleLoginButton);

		menue_listener aListener = new menue_listener();
		sureLoginButton.setOnClickListener(aListener);
		registerButton.setOnClickListener(aListener);
		cacleButton.setOnClickListener(aListener);

		// 用户信息反馈处理
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case SEND_INFO_TO_SERVER:
					Toast.makeText(getApplicationContext(), "向服务器请求数据中,请稍后", Toast.LENGTH_SHORT).show();
					break;
				case FAIL_GET_WS:
					Toast.makeText(getApplicationContext(), "获取服务器信息失败,请检查您的网络", Toast.LENGTH_SHORT).show();
					break;
				case SUCCESSFUL_LOGIN:
					Toast.makeText(getApplicationContext(), "登录成功,准备跳转界面...", Toast.LENGTH_SHORT).show();
					Intent userLoginIntent = new Intent(UserLogin.this, Show_BaiduMap.class);
					userLoginIntent.putExtra("comStyle", "user");// 用户类型(一般用户)
					userLoginIntent.putExtra("strUid", userID);
					userLoginIntent.putExtra("strUserName", successfulUserName);
					userLoginIntent.putExtra("strStateInfo", userSessionString);
					startActivity(userLoginIntent);// 跳转至百度地图Activity
					break;
				case FAIL_LOGIN:
					Toast.makeText(getApplicationContext(), "登录失败,用户名不存在或密码错误,请您仔细检查,再重试一遍", Toast.LENGTH_SHORT).show();
				case INPUT_ERROR:
					Toast.makeText(getApplicationContext(), "您的用户名或密码格式不正确", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

	}

	// 后台线程,用于调用WebService匹配数据库中的用户
	// 由于安卓版本不允许在主线程中访问网络
	private class getuser_ws implements Runnable {

		@Override
		public void run() {

			String usernameString = usernameEditText.getText().toString();// 获得输入的用户名
			String password = userPassword.getText().toString();// 获得输入的密码

			if (usernameString.length() <= 3 || password.length() <= 3) {
				Message inputerrorMessage = new Message();
				inputerrorMessage.what = INPUT_ERROR;
				getInfoHandler.sendMessage(inputerrorMessage);
				return;
			}

			// 对密码进行3次MD5加密
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

			// 调试查看3次MD5加密后的密码数据(需要删除)
			Log.d("MD5-3's secret", newpassword);

			// 给主线程发送提示用户正在向服务器传递数据的信号
			Message beginSendMessage = new Message();
			beginSendMessage.what = SEND_INFO_TO_SERVER;
			getInfoHandler.sendMessage(beginSendMessage);

			GetUser_ws aGetUser_ws = new GetUser_ws();// 加载WebService类
			aGetUser_ws.setTheHttpIpString(setHttp.getTheHttpIpString());// 开发和测试期间使用，用于切换网络
			aGetUser_ws.getUserLoginInfo(usernameString, "0", newpassword, "0", "0");// 调用WebService

			// 获取返回的参数
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

			// 调试查看获取到的WebService信息
			Log.d("getWS-bSuccess", bSuccess);
			Log.d("getWS-strUid", strUid);
			Log.d("getWS-strUserName", strUserName);
			Log.d("getWS-strStateInfo", strStateInfo);

			if (bSuccess.equals("true")) {// 成功登录,发送登录成功信号
				// 记载登录用户的用户ID，用户名和会话ID,准备发送至百度地图Activity
				userID = strUid;
				successfulUserName = strUserName;
				userSessionString = strStateInfo;

				Message sucessfulLoginMessage = new Message();
				sucessfulLoginMessage.what = SUCCESSFUL_LOGIN;
				getInfoHandler.sendMessage(sucessfulLoginMessage);
			} else if (bSuccess.equals("false")) {// 登录失败,发送失败信号
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
				// 启动WebService服务线程
				getuser_ws aWs = new getuser_ws();
				Thread aThread = new Thread(aWs);
				aThread.start();
				break;
			case R.id.userlogin_registeruserButton:
				// 前往注册界面
				Intent gotoUserRegisterIntent = new Intent(getApplicationContext(), UserRegister.class);
				startActivity(gotoUserRegisterIntent);
				break;
			case R.id.userlogin_cacleLoginButton:
				// 结束当前Activity
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

	// 退出程序
	private void Leave_Program() {
		// 创建退出对话框
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// 设置对话框标题
		isExit.setTitle("MapQ温馨提醒");
		// 设置对话框消息
		isExit.setMessage("确定取消登录么?");
		// 添加选择按钮并注册监听
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "确定", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "不,我要登录", BackListener);
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
