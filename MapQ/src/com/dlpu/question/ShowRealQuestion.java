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

	// ���������Ϣ�ؼ�
	private TextView questionTitleTextView = null;
	private TextView questionPublishTimeTextView = null;
	private TextView questionContextTextView = null;
	private TextView questionUserTextView = null;

	// �ظ������Ϣ�ؼ�
	private TextView questionReplyTextView = null;// ��ʾ�ظ��Ŀؼ�
	private EditText myreplyEditText = null;
	private Button replySureButton = null;
	private Button replycancleButton = null;

	// private String questionReplypageString = "";// ��ʾ��ǰҳ���Լ��ܹ�ҳ��
	// private int replyinpage = 10;// Ĭ�ϵ�ÿҳ�ظ���Ŀ
	// private int currentPage = 1;// ��ǰ�ظ���ҳ��

	// �ӷ�������ȡ�����������Ϣ�ͻظ�ֵ
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

	// ��������Show_BaiduMap.class��ֵ
	private String wssidString = null;
	private String strUserIdString = null;

	// �û��ظ�������
	private String currentReply = null;

	// ��Ϣ��������
	private Handler getInfoHandler = null;
	private static final int WS_FAIL = -1;// ws����ʧ��
	private static final int WS_OUT_TIME = -2;// ws����

	private static final int GET_QUESTION_SUCCESS = 888;// �ɹ���ȡ������Ϣ
	private static final int CREATE_REPLY_NUM = 999;// �ѻ������ظ��������ź�
	private static final int GET_QUESTIONREPLY_SUCCESS = 777;// ��ȡ�������ȫ���ظ��ɹ��ź�
	private static final int SUCCESS_REPLY = 666;// �ظ��ɹ�
	private static final int FORBINDEEN_NULL_REPLY = -999;// ��ֹ�ջظ��ź�

	private boolean create_success_flag = false;// �Ƿ�ɹ�ʵ���������ʶ

	@SuppressLint("HandlerLeak")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.showrealquestion_activity);

		// ��ȡ������Ϣ��ִ��
		getInfoHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case WS_FAIL:
					Toast.makeText(ShowRealQuestion.this, "��������ʧ��,������",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case WS_OUT_TIME:
					Toast.makeText(ShowRealQuestion.this, "��¼�ѹ�ʱ,�����µ�¼",
							Toast.LENGTH_SHORT).show();
					break;

				case FORBINDEEN_NULL_REPLY:
					Toast.makeText(ShowRealQuestion.this, "������ظ�����",
							Toast.LENGTH_SHORT).show();
					break;
				case SUCCESS_REPLY:
					Toast.makeText(ShowRealQuestion.this, "�ظ��ɹ�,������������ʰ�~",
							Toast.LENGTH_SHORT).show();
					finish();
					break;
				case GET_QUESTIONREPLY_SUCCESS:
					// ��������ӵ��ı���
					String Showreply = "";
					for (int i = 0; i < ReplyNum; i++) {
						if (i == 0) {
							Showreply = "�û���:" + questionReplyUserStrings[i];
						} else {
							Showreply = Showreply + "\n" + "�û���:"
									+ questionReplyUserStrings[i];
						}
						Showreply = Showreply + "\n" + "����ʱ��:"
								+ questionReplyTimeStrings[i];
						Showreply = Showreply + "\n" + "��������:"
								+ questionRepalyContextStrings[i];
					}
					// int allpage = ReplyNum / replyinpage;
					// if (ReplyNum % replyinpage != 0) {// ����ҳ������
					// allpage++;
					// }
					// questionReplypageString = "����" + ReplyNum + "���ظ�,��ǰ��"
					// + currentPage + "ҳ,��" + allpage + "ҳ";
					// Ĭ����ʾ�ظ�
					// Showreply = Showreply + "\n" + questionReplypageString;
					questionReplyTextView.setText(Showreply);

					Toast.makeText(ShowRealQuestion.this, "��ʾ�ظ����",
							Toast.LENGTH_SHORT).show();// ����
					break;
				case GET_QUESTION_SUCCESS:
					// ��ʾ �������Ϣ
					questionTitleTextView.setText(questionTitle);
					questionUserTextView.setText(questionUserNameString);
					questionPublishTimeTextView
							.setText(questionPublicTimeString);
					questionContextTextView.setText(questionContextString);
					break;
				case CREATE_REPLY_NUM:
					// ʵ�����������С
					questionReplyUserStrings = new String[ReplyNum];
					questionReplyTimeStrings = new String[ReplyNum];
					questionRepalyContextStrings = new String[ReplyNum];
					create_success_flag = true;// ʵ�����ɹ�
					break;
				default:
					break;
				}
			}

		};

		// ʵ���������ؼ�
		questionReplyTextView = (TextView) findViewById(R.id.showrealquestion_reply);
		questionReplyTextView.setMovementMethod(ScrollingMovementMethod
				.getInstance());// ��������Թ���
		// questionReplyTextView.setScrollbarFadingEnabled(false);//���ù�����һֱ��ʾ
		questionTitleTextView = (TextView) findViewById(R.id.showrealquestion_questionTitle);
		questionUserTextView = (TextView)findViewById(R.id.showrealquestion_questionUser);
		questionPublishTimeTextView = (TextView) findViewById(R.id.showrealquestion_publishTime);
		questionContextTextView = (TextView) findViewById(R.id.showrealquestion_questionContext);
		replySureButton = (Button) findViewById(R.id.showrealquestion_surereply_button);
		replycancleButton = (Button) findViewById(R.id.showrealquestion_cancle);
		myreplyEditText = (EditText) findViewById(R.id.showrealquestion_myreply);

		// ��Ӱ�ť�¼�
		choise_button achoiseButton = new choise_button();
		replySureButton.setOnClickListener(achoiseButton);
		replycancleButton.setOnClickListener(achoiseButton);

		// ���������Ϣ
		questionId = getIntent().getStringExtra("strQuestionId");
		wssidString = getIntent().getStringExtra("wssid");
		strUserIdString = getIntent().getStringExtra("strUserId");

		// �����������ϸ���ݻ�ȡ�߳�
		GetRealQuestion_Thread aGetRealQuestion_Thread = new GetRealQuestion_Thread();
		Thread getThread = new Thread(aGetRealQuestion_Thread);
		getThread.start();

	}

	// ��ҳ��ť���ܼ���
	private class choise_button implements OnClickListener {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.showrealquestion_surereply_button:
				// ����ظ���ť�¼�
				currentReply = myreplyEditText.getText().toString();
				if (check_context()) {// �����߳�,��ӻظ�
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

	// ����WS��ȡ��ǰ�������Ϣ�߳�
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
					questionTitle = "���ʱ���:" + aGetRealQuestion_ws
							.getQuestionTitleString();
					questionPublicTimeString = "���ʷ���ʱ��: " + aGetRealQuestion_ws
							.getQuestionTimeString();
					questionContextString = "��������: " + aGetRealQuestion_ws
							.getQuestionContentString();

					GetUserName_Thread getUserName_Thread = new GetUserName_Thread();
					Thread getUserNameThread = new Thread(getUserName_Thread);
					getUserNameThread.start();

					ReplyNum = aGetRealQuestion_ws.getnum();// ��ȡ�ظ�������
					Message sendMessage = new Message();// ����ʵ���������ź�
					sendMessage.what = CREATE_REPLY_NUM;
					getInfoHandler.sendMessage(sendMessage);

					while (!create_success_flag) {// ʵ����δ�ɹ���ȴ�
						try {
							Thread.sleep(100);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					String[][] getAllReply = aGetRealQuestion_ws.getresult();// ��ȡȫ���Ļظ�
					for (int i = 0; i < ReplyNum; i++) {
						questionReplyUserStrings[i] = getAllReply[i][0];// ������Ϣ[0]λΪ�ظ����û���
						questionReplyTimeStrings[i] = getAllReply[i][1];// [1]λΪ�ظ���ʱ��
						questionRepalyContextStrings[i] = getAllReply[i][2];// [2]λΪ�ظ�������
					}

					Message getReplyMessage = new Message();// ���ͻظ���������ź�
					getReplyMessage.what = GET_QUESTIONREPLY_SUCCESS;
					getInfoHandler.sendMessage(getReplyMessage);
				} else {// ws��ʱ
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// ws����ʧ��
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}
		}

	}

	// ����WS�̻߳�ȡ�����ʵķ����û��û���
	private class GetUserName_Thread implements Runnable {

		@Override
		public void run() {
			GetUserName_ws getUserName_ws = new GetUserName_ws();
			Globalvar globalvar = new Globalvar();
			getUserName_ws.setTheHttpIpString(globalvar.getTheHttpIpString());
			getUserName_ws.GetUserName(questionUserString);

			if (getUserName_ws.get_getWS().equals("true")) {
				questionUserNameString = "�����ˣ�" + getUserName_ws.getResult();

				Message getQuestionMessage = new Message();// ���ͳɹ���ȡ���������ź�
				getQuestionMessage.what = GET_QUESTION_SUCCESS;
				getInfoHandler.sendMessage(getQuestionMessage);
			} else {
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}

		}

	}

	// ����ws���лظ����߳�
	private class AddComment_Thread implements Runnable {

		@Override
		public void run() {
			AddComment_ws addComment_ws = new AddComment_ws();
			Globalvar globalvar = new Globalvar();
			addComment_ws.setTheHttpIpString(globalvar.getTheHttpIpString());

			addComment_ws.AddComment(questionId, strUserIdString, currentReply,
					wssidString);
			if (addComment_ws.get_getWS().equals("true")) {
				if (addComment_ws.getResultString().equals("true")) {// �ظ���ӳɹ�
					Message getReplyMessage = new Message();// ���ͳɹ��ظ��ź�
					getReplyMessage.what = SUCCESS_REPLY;
					getInfoHandler.sendMessage(getReplyMessage);
				} else {// ��ӻظ�ʧ��
					Message sendMessage = new Message();
					sendMessage.what = WS_OUT_TIME;
					getInfoHandler.sendMessage(sendMessage);
				}
			} else {// ����wsʧ��
				Message sendMessage = new Message();
				sendMessage.what = WS_FAIL;
				getInfoHandler.sendMessage(sendMessage);
			}

		}

	}

	// ��⵱ǰ�ظ��ı��ĺϷ���
	private boolean check_context() {
		if (currentReply.length() < 1) {
			return false;
		}
		return true;
	}

}
