package com.dlpu.question;

import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.ws.CreateQuestion_ws;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class UserAskQuestion extends ActionBarActivity {

	// 接收基本信息
	private String strUserid = null;
	private String strLatitude = null;
	private String strLongitude = null;
	private String wssid = null;

	// 有待上传的信息
	private String questionTitleString = null;
	private String questionContextString = null;

	// 基本控件
	private EditText questionTitleEditText = null;
	private EditText questionContextEditText = null;
	private Button questionsubmitButton = null;
	private Button questionResetButton = null;
	private Button questionCancleButton = null;

	// 信息反馈
	private Handler getInfoHandler = null;
	private static final int WS_FAIL = -1;// ws调用失败
	private static final int WS_OUT_TIME = -2;// ws过期
	private static final int SUCCESS_CREATE = 1;//成功添加问题信号
	private static final int CHECK_FAIL = -3;//文本合法性检查失败

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.useraskquestion_activity);

		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WS_FAIL:
					Toast.makeText(UserAskQuestion.this, "网络连接失败", Toast.LENGTH_SHORT).show();
					break;
				case WS_OUT_TIME:
					Toast.makeText(UserAskQuestion.this, "登录已过期,请重新登录", Toast.LENGTH_SHORT).show();
					break;
				case SUCCESS_CREATE:
					Toast.makeText(UserAskQuestion.this, "问问已发布", Toast.LENGTH_SHORT).show();
					finish();
					break;
				case CHECK_FAIL:
					Toast.makeText(UserAskQuestion.this, "请检查输入", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

		// 获取相关信息
		strUserid = getIntent().getStringExtra("strUserid");
		strLatitude = getIntent().getStringExtra("strLatitude");
		strLongitude = getIntent().getStringExtra("strLongitude");
		wssid = getIntent().getStringExtra("wssid");

		// 实例化基本控件
		questionTitleEditText = (EditText) findViewById(R.id.useraskquestion_titleedit);
		questionContextEditText = (EditText) findViewById(R.id.useraskquestion_contextedit);
		questionsubmitButton = (Button) findViewById(R.id.useraskquestion_submit);
		questionResetButton = (Button) findViewById(R.id.useraskquestion_reset);
		questionCancleButton = (Button) findViewById(R.id.useraskquestion_cancle);
		
		//添加按键监听
		choise_button achoise = new choise_button();
		questionsubmitButton.setOnClickListener(achoise);
		questionResetButton.setOnClickListener(achoise);
		questionCancleButton.setOnClickListener(achoise);
		

	}
	
	//按钮事件
	private class choise_button implements OnClickListener{

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.useraskquestion_submit:
				questionTitleString = questionTitleEditText.getText().toString();
				questionContextString = questionContextEditText.getText().toString();
				if (check_Edit()) {
					CreateQuestion_Thread createQuestion_Thread = new CreateQuestion_Thread();
					Thread createThread = new Thread(createQuestion_Thread);
					createThread.start();
				}else{//文本输入有问题
					Message sendMessage = new Message();
					sendMessage.what = CHECK_FAIL;
					getInfoHandler.sendMessage(sendMessage);
				}			
				break;
			case R.id.useraskquestion_reset:
				questionContextEditText.setText("");
				questionTitleEditText.setText("");
				break;
			case R.id.useraskquestion_cancle:
				finish();
				break;
			default:
				break;
			}
		}
		
	}

	// 调用创建问题线程
	private class CreateQuestion_Thread implements Runnable {

		@Override
		public void run() {
			Globalvar globalvar = new Globalvar();
			CreateQuestion_ws createQuestion_ws = new CreateQuestion_ws();
			createQuestion_ws
					.setTheHttpIpString(globalvar.getTheHttpIpString());

			createQuestion_ws.CreateQuestion(strUserid, strLatitude,
					strLongitude, questionTitleString, questionContextString,
					wssid);
			
			if (createQuestion_ws.get_getWS().equals("true")) {
				if (createQuestion_ws.getResult().equals("err_wssid_timeout")) {
					//添加问题失败,用户登录过期
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}else{//提问发布成功
					Message sendMessage = new Message();
					sendMessage.what = SUCCESS_CREATE;
					getInfoHandler.sendMessage(sendMessage);
				}
			}else{
				//ws调用失败
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}
		}

	}
	
	
	private boolean check_Edit(){
		if (questionTitleString.length() < 1) {
			return false;
		}
		if (questionContextString.length() < 1) {
			return false;
		}
		return true;
	}

}
