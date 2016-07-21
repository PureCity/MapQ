package com.dlpu.question;

import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.showmap.Show_BaiduMap;
import com.dlpu.ws.DeleteQuestion_ws;
import com.dlpu.ws.GetUserAllQuestion_ws;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class GetUserAllQuestion extends ActionBarActivity {

	// 接收到的信息
	private String strUidString = null;
	private String wssidString = null;

	// 利用WS获取到的问题信息
	private String DIV_QUESTION_TAG = "MapQ_QuestionDivTag__divdiffentQuestion.;";// 分割问题的标识
	private String DIV_QUESTION_ID_TITLE_TAG = "MapQ_QuestionDivTag__divQuestionIdandTitle.;";// 将问题分割成id和标题的标识
	private String[][] userQuestionStrings = null;// 获取并拆分出来的问题
	private int questionNum = 0;// 问题的数量
	private int currentpostion = 0;

	// 信息反馈信号
	private Handler getInfoHandler = null;
	private static final int WS_FAIL = -1;// ws调用失败
	private static final int WS_OUT_TIME = -2;// WS过期
	private static final int CREATE_BASIC_UNIT = 999;// 实例化基本数组和控件信号
	private static final int SHOW_QUESTIONS = 888;// 显示所有问题的信号
	private static final int DEL_TRUE = 777;// 成功删除某问题信号
	private static final int DEL_BEGIN = 666;// 正在删除问题

	private boolean createfinish = false;

	// 基本控件
	private Button leaveButton = null;
	// 需要动态加载的基本控件
	private ListView questionListView = null;// 存放问题的list

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.getuserallquestion_activity);

		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case WS_FAIL:
					Toast.makeText(GetUserAllQuestion.this, "网络连接失败,请检查您的网络", Toast.LENGTH_SHORT).show();
					break;
				case WS_OUT_TIME:
					Toast.makeText(GetUserAllQuestion.this, "登录已过时", Toast.LENGTH_SHORT).show();
					break;
				case CREATE_BASIC_UNIT:
					// 实例化基本数组和控件
					userQuestionStrings = new String[questionNum][2];
					createfinish = true;
					break;
				case SHOW_QUESTIONS:
					questionAdapter theAdapter = new questionAdapter(GetUserAllQuestion.this);
					questionListView.setAdapter(theAdapter);
					break;
				case DEL_TRUE:
					Toast.makeText(GetUserAllQuestion.this, "成功删除问题,请在地图界面查看", Toast.LENGTH_SHORT).show();
					Intent aIntent = new Intent(GetUserAllQuestion.this, Show_BaiduMap.class);
					setResult(0, aIntent);
					finish();
					break;
				case DEL_BEGIN:
					Toast.makeText(GetUserAllQuestion.this, "正在从服务器删除问题,请耐心等候", Toast.LENGTH_LONG).show();
					break;
				default:
					break;
				}
			}

		};

		// 获取从Show_BaiduMap传递来的信息
		strUidString = getIntent().getStringExtra("strUserId");
		wssidString = getIntent().getStringExtra("wssid");

		// 实例化基本控件
		questionListView = (ListView) findViewById(R.id.getuserallquestion_QuestionLayout);
		leaveButton = (Button) findViewById(R.id.getuserallquestion_cancle);

		// 添加监听事件
		choise_menue achoiseMenue = new choise_menue();
		leaveButton.setOnClickListener(achoiseMenue);

		getUserAllQuestion_Thread agetAllQuestion_Thread = new getUserAllQuestion_Thread();
		Thread geThread = new Thread(agetAllQuestion_Thread);
		geThread.start();

	}

	// 问题列表
	private class questionAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater layoutInflater;

		public questionAdapter(Context context) {
			this.context = context;
			layoutInflater = LayoutInflater.from(this.context);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return userQuestionStrings.length;
		}

		@Override
		public Object getItem(int i) {
			// TODO Auto-generated method stub
			return userQuestionStrings[i][1];
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final int currentLine = position;

			if (convertView == null) {
				convertView = layoutInflater.inflate(R.layout.allquestionlayoutview, null);
			}

			TextView titleTextView = (TextView) convertView.findViewById(R.id.getallquestion_title);
			TextView delTextView = (TextView) convertView.findViewById(R.id.getallquestion_del);
			titleTextView.setTextSize(22);
			delTextView.setTextSize(22);

			titleTextView.setText(userQuestionStrings[position][1]);
			delTextView.setText("删除");

			showrealquestionButton showrealquestion = new showrealquestionButton();
			showrealquestion.setQuestionIdString(userQuestionStrings[position][0]);
			titleTextView.setOnClickListener(showrealquestion);

			delTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					currentpostion = currentLine;
					sure_deal();
				}

			});

			return convertView;
		}

	}

	// 用户按键事件监听
	private class choise_menue implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.getuserallquestion_cancle:
				Intent aIntent = new Intent(GetUserAllQuestion.this, Show_BaiduMap.class);
				setResult(0, aIntent);
				finish();
				break;

			default:
				break;
			}
		}
	}

	private class showrealquestionButton implements OnClickListener {

		private String questionIdString = null;

		public void setQuestionIdString(String questionid) {
			this.questionIdString = questionid;
		}

		@Override
		public void onClick(View arg0) {
			Intent showquestionIntent = new Intent(GetUserAllQuestion.this, ShowRealQuestion.class);
			showquestionIntent.putExtra("strQuestionId", questionIdString);
			showquestionIntent.putExtra("wssid", wssidString);
			showquestionIntent.putExtra("strUserId", strUidString);
			startActivity(showquestionIntent);
		}

	}

	// 获取用户所有问题的线程
	private class getUserAllQuestion_Thread implements Runnable {

		@Override
		public void run() {
			Globalvar globalvar = new Globalvar();
			GetUserAllQuestion_ws getUserAllQuestion_ws = new GetUserAllQuestion_ws();
			getUserAllQuestion_ws.setTheHttpIpString(globalvar.getTheHttpIpString());

			getUserAllQuestion_ws.GetUserAllQuestion(strUidString, wssidString);
			if (getUserAllQuestion_ws.get_getWS().equals("true")) {
				if (getUserAllQuestion_ws.getStatues().equals("success")) {

					questionNum = getUserAllQuestion_ws.getNum();
					Message sendMessage = new Message();
					sendMessage.what = CREATE_BASIC_UNIT;
					getInfoHandler.sendMessage(sendMessage);
					while (!createfinish) {
						try {
							Thread.sleep(50);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String getString = getUserAllQuestion_ws.getResult();
					String[] string1 = getString.split(DIV_QUESTION_TAG);
					for (int i = 0, j = 0; i < string1.length; i++) {
						String[] tempString = string1[i].split(DIV_QUESTION_ID_TITLE_TAG);
						// for (int k = 0; k < tempString.length; k++) {
						// Log.d("tempString", "value="+tempString[k]);//test
						// }
						if (tempString[0] != null && tempString.length >= 2) {
							userQuestionStrings[j][0] = tempString[0];
							userQuestionStrings[j][1] = i + ". " + tempString[1];
							j++;
						}
					}
					Message showMessage = new Message();// 发送显示信号
					showMessage.what = SHOW_QUESTIONS;
					getInfoHandler.sendMessage(showMessage);

				} else {// ws过时
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// WS调用失败
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}
		}

	}

	// 确认删除
	private void sure_deal() {

		// 创建退出对话框
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// 设置对话框标题
		isExit.setTitle("MapQ温馨提醒");
		// 设置对话框消息
		isExit.setMessage("确认删除该问问?");
		// 添加选择按钮并注册监听
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "确认", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "取消", BackListener);
		// 显示对话框
		isExit.show();
	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				deleteQuestion_Thread delQuestion_Thread = new deleteQuestion_Thread();
				delQuestion_Thread.setButton(currentpostion);
				Thread delThread = new Thread(delQuestion_Thread);
				delThread.start();
				// 提示开始删除
				Message sendMessage = new Message();
				sendMessage.what = DEL_BEGIN;
				getInfoHandler.sendMessage(sendMessage);
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	// 调用删除WS
	private class deleteQuestion_Thread implements Runnable {

		private int button = 0;

		public void setButton(int num) {
			this.button = num;
		}

		@Override
		public void run() {
			String delQuestionId = userQuestionStrings[button][0];
			DeleteQuestion_ws deleteQuestion_ws = new DeleteQuestion_ws();
			Globalvar globalvar = new Globalvar();
			deleteQuestion_ws.setTheHttpIpString(globalvar.getTheHttpIpString());

			deleteQuestion_ws.DeleteQuestion(delQuestionId, wssidString);
			if (deleteQuestion_ws.get_getWS().equals("true")) {
				if (deleteQuestion_ws.getResult().equals("true")) {
					// 成功删除某问题
					Message suredelMessage = new Message();
					suredelMessage.what = DEL_TRUE;
					getInfoHandler.sendMessage(suredelMessage);
				} else {// ws过时
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// WS调用失败
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}

		}

	}

}
