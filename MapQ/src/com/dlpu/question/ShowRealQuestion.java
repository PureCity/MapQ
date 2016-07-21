package com.dlpu.question;

import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.ws.AddComment_ws;
import com.dlpu.ws.GetRealQuestion_ws;
import com.dlpu.ws.GetUserName_ws;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class ShowRealQuestion extends ActionBarActivity {

	// 问题相关信息控件
	private TextView questionTitleTextView = null;
	private TextView questionPublishTimeTextView = null;
	private TextView questionContextTextView = null;
	private TextView questionUserTextView = null;

	// 回复相关信息控件
	private TextView questionReplyTextView = null;// 显示回复的控件
	private EditText myreplyEditText = null;
	private Button replySureButton = null;
	private Button replycancleButton = null;

	// private String questionReplypageString = "";// 显示当前页码以及总共页码
	// private int replyinpage = 10;// 默认的每页回复数目
	// private int currentPage = 1;// 当前回复的页码

	// 从服务器获取到的问题的信息和回复值
	private String questionUserNameString = null;
	private String questionUserString = null;
	private String questionTitle = null;
	private String questionPublicTimeString = null;
	private String questionContextString = null;
	private String questionId = null;
	private String[] questionReplyUserStrings = null;
	private String[] questionReplyTimeStrings = null;
	private String[] questionRepalyContextStrings = null;
	private int ReplyNum = 0;

	// 接受来自Show_BaiduMap.class的值
	private String wssidString = null;
	private String strUserIdString = null;

	// 用户回复的内容
	private String currentReply = null;

	// 信息反馈处理
	private Handler getInfoHandler = null;
	private static final int WS_FAIL = -1;// ws调用失败
	private static final int WS_OUT_TIME = -2;// ws过期

	private static final int GET_QUESTION_SUCCESS = 888;// 成功获取问题信息
	private static final int CREATE_REPLY_NUM = 999;// 已获得问题回复数量的信号
	private static final int GET_QUESTIONREPLY_SUCCESS = 777;// 获取该问题的全部回复成功信号
	private static final int SUCCESS_REPLY = 666;// 回复成功
	private static final int FORBINDEEN_NULL_REPLY = -999;// 禁止空回复信号

	private boolean create_success_flag = false;// 是否成功实例化数组标识

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showrealquestion_activity);

		// 获取反馈信息并执行
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case WS_FAIL:
					Toast.makeText(ShowRealQuestion.this, "网络连接失败,请重试",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case WS_OUT_TIME:
					Toast.makeText(ShowRealQuestion.this, "登录已过时,请重新登录",
							Toast.LENGTH_SHORT).show();
					break;

				case FORBINDEEN_NULL_REPLY:
					Toast.makeText(ShowRealQuestion.this, "请输入回复内容",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESS_REPLY:
					Toast.makeText(ShowRealQuestion.this, "回复成功,请参与其它问问吧~",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case GET_QUESTIONREPLY_SUCCESS:
					// 将问题添加到文本中
					String Showreply = "";
					for (int i = 0; i < ReplyNum; i++) {
						if (i == 0) {
							Showreply = "用户名:" + questionReplyUserStrings[i];
						} else {
							Showreply = Showreply + "\n" + "用户名:"
									+ questionReplyUserStrings[i];
						}
						Showreply = Showreply + "\n" + "发表时间:"
								+ questionReplyTimeStrings[i];
						Showreply = Showreply + "\n" + "发表内容:"
								+ questionRepalyContextStrings[i];
					}
					// int allpage = ReplyNum / replyinpage;
					// if (ReplyNum % replyinpage != 0) {// 修正页码数量
					// allpage++;
					// }
					// questionReplypageString = "共有" + ReplyNum + "条回复,当前第"
					// + currentPage + "页,共" + allpage + "页";
					// 默认显示回复
					// Showreply = Showreply + "\n" + questionReplypageString;
					questionReplyTextView.setText(Showreply);

					Toast.makeText(ShowRealQuestion.this, "显示回复完毕",
							Toast.LENGTH_SHORT).show();// 测试
					break;
				case GET_QUESTION_SUCCESS:
					// 显示 问题的信息
					questionTitleTextView.setText(questionTitle);
					questionUserTextView.setText(questionUserNameString);
					questionPublishTimeTextView
							.setText(questionPublicTimeString);
					questionContextTextView.setText(questionContextString);
					break;
				case CREATE_REPLY_NUM:
					// 实例化各数组大小
					questionReplyUserStrings = new String[ReplyNum];
					questionReplyTimeStrings = new String[ReplyNum];
					questionRepalyContextStrings = new String[ReplyNum];
					create_success_flag = true;// 实例化成功
					break;
				default:
					break;
				}
			}

		};

		// 实例化基本控件
		questionReplyTextView = (TextView) findViewById(R.id.showrealquestion_reply);
		questionReplyTextView.setMovementMethod(ScrollingMovementMethod
				.getInstance());// 设置其可以滚动
		// questionReplyTextView.setScrollbarFadingEnabled(false);//设置滚动条一直显示
		questionTitleTextView = (TextView) findViewById(R.id.showrealquestion_questionTitle);
		questionUserTextView = (TextView)findViewById(R.id.showrealquestion_questionUser);
		questionPublishTimeTextView = (TextView) findViewById(R.id.showrealquestion_publishTime);
		questionContextTextView = (TextView) findViewById(R.id.showrealquestion_questionContext);
		replySureButton = (Button) findViewById(R.id.showrealquestion_surereply_button);
		replycancleButton = (Button) findViewById(R.id.showrealquestion_cancle);
		myreplyEditText = (EditText) findViewById(R.id.showrealquestion_myreply);

		// 添加按钮事件
		choise_button achoiseButton = new choise_button();
		replySureButton.setOnClickListener(achoiseButton);
		replycancleButton.setOnClickListener(achoiseButton);

		// 加载相关信息
		questionId = getIntent().getStringExtra("strQuestionId");
		wssidString = getIntent().getStringExtra("wssid");
		strUserIdString = getIntent().getStringExtra("strUserId");

		// 启动问题的详细内容获取线程
		GetRealQuestion_Thread aGetRealQuestion_Thread = new GetRealQuestion_Thread();
		Thread getThread = new Thread(aGetRealQuestion_Thread);
		getThread.start();

	}

	// 本页按钮功能监听
	private class choise_button implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.showrealquestion_surereply_button:
				// 点击回复按钮事件
				currentReply = myreplyEditText.getText().toString();
				if (check_context()) {// 启动线程,添加回复
					AddComment_Thread addComment_Thread = new AddComment_Thread();
					Thread addThread = new Thread(addComment_Thread);
					addThread.start();
				} else {
					Message sendMessage = new Message();
					sendMessage.what = FORBINDEEN_NULL_REPLY;
					getInfoHandler.sendMessage(sendMessage);
				}
				break;
			case R.id.showrealquestion_cancle:
				finish();
				break;
			default:
				break;
			}

		}

	}

	// 调用WS获取当前问题的信息线程
	private class GetRealQuestion_Thread implements Runnable {

		@Override
		public void run() {
			Globalvar aGlobalvar = new Globalvar();
			GetRealQuestion_ws aGetRealQuestion_ws = new GetRealQuestion_ws();
			aGetRealQuestion_ws.setTheHttpIpString(aGlobalvar
					.getTheHttpIpString());
			aGetRealQuestion_ws.getRealQuestion(questionId, wssidString);
			if (aGetRealQuestion_ws.get_getWS().equals("true")) {
				if (aGetRealQuestion_ws.getStatusString().equals("success")) {

					questionUserString = aGetRealQuestion_ws
							.getQuestionuseridString();
					questionTitle = "问问标题:" + aGetRealQuestion_ws
							.getQuestionTitleString();
					questionPublicTimeString = "问问发布时间: " + aGetRealQuestion_ws
							.getQuestionTimeString();
					questionContextString = "问问内容: " + aGetRealQuestion_ws
							.getQuestionContentString();

					GetUserName_Thread getUserName_Thread = new GetUserName_Thread();
					Thread getUserNameThread = new Thread(getUserName_Thread);
					getUserNameThread.start();

					ReplyNum = aGetRealQuestion_ws.getnum();// 获取回复的数量
					Message sendMessage = new Message();// 发送实例化请求信号
					sendMessage.what = CREATE_REPLY_NUM;
					getInfoHandler.sendMessage(sendMessage);

					while (!create_success_flag) {// 实例化未成功则等待
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String[][] getAllReply = aGetRealQuestion_ws.getresult();// 获取全部的回复
					for (int i = 0; i < ReplyNum; i++) {
						questionReplyUserStrings[i] = getAllReply[i][0];// 返回信息[0]位为回复的用户名
						questionReplyTimeStrings[i] = getAllReply[i][1];// [1]位为回复的时间
						questionRepalyContextStrings[i] = getAllReply[i][2];// [2]位为回复的内容
					}

					Message getReplyMessage = new Message();// 发送回复加载完毕信号
					getReplyMessage.what = GET_QUESTIONREPLY_SUCCESS;
					getInfoHandler.sendMessage(getReplyMessage);
				} else {// ws过时
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// ws调用失败
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}
		}

	}

	// 调用WS线程获取该问问的发问用户用户名
	private class GetUserName_Thread implements Runnable {

		@Override
		public void run() {
			GetUserName_ws getUserName_ws = new GetUserName_ws();
			Globalvar globalvar = new Globalvar();
			getUserName_ws.setTheHttpIpString(globalvar.getTheHttpIpString());
			getUserName_ws.GetUserName(questionUserString);

			if (getUserName_ws.get_getWS().equals("true")) {
				questionUserNameString = "发问人：" + getUserName_ws.getResult();

				Message getQuestionMessage = new Message();// 发送成功获取问题详情信号
				getQuestionMessage.what = GET_QUESTION_SUCCESS;
				getInfoHandler.sendMessage(getQuestionMessage);
			} else {
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}

		}

	}

	// 调用ws进行回复的线程
	private class AddComment_Thread implements Runnable {

		@Override
		public void run() {
			AddComment_ws addComment_ws = new AddComment_ws();
			Globalvar globalvar = new Globalvar();
			addComment_ws.setTheHttpIpString(globalvar.getTheHttpIpString());

			addComment_ws.AddComment(questionId, strUserIdString, currentReply,
					wssidString);
			if (addComment_ws.get_getWS().equals("true")) {
				if (addComment_ws.getResultString().equals("true")) {// 回复添加成功
					Message getReplyMessage = new Message();// 发送成功回复信号
					getReplyMessage.what = SUCCESS_REPLY;
					getInfoHandler.sendMessage(getReplyMessage);
				} else {// 添加回复失败
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// 调用ws失败
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}

		}

	}

	// 检测当前回复文本的合法性
	private boolean check_context() {
		if (currentReply.length() < 1) {
			return false;
		}
		return true;
	}

}
