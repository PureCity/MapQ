package com.dlpu.mapq;

import com.dlpu.showmap.Show_BaiduMap;
import com.dlpu.user.UserLogin;
import com.dlpu.version.About_App;

import android.support.v7.app.ActionBarActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends ActionBarActivity {
	

	private static Button loginButton = null;
	private static Button tryButton = null;
	private static Button aboutButton = null;
	private static Button exitButton = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		
		setContentView(R.layout.activity_main);

		loginButton = (Button) findViewById(R.id.menue_login);
		tryButton = (Button) findViewById(R.id.menue_try);
		aboutButton = (Button) findViewById(R.id.menue_about);
		exitButton = (Button) findViewById(R.id.menue_exit);

		menue_listenerClass listener = new menue_listenerClass();
		loginButton.setOnClickListener(listener);
		tryButton.setOnClickListener(listener);
		aboutButton.setOnClickListener(listener);
		exitButton.setOnClickListener(listener);

	}
	
	private class menue_listenerClass implements OnClickListener {
		// �˵�ѡ���¼�����
		@Override
		public void onClick(View v) {
			int theId = v.getId();
			switch (theId) {
			case R.id.menue_login:
				// ��ת����¼�����¼�
				Intent aIntent = new Intent();
				aIntent.setClass(MainActivity.this, UserLogin.class);
				startActivity(aIntent);
				break;
			case R.id.menue_try:
				// ��ת��ֱ�����ý���
				Intent testIntent = new Intent();
				testIntent.setClass(MainActivity.this, Show_BaiduMap.class);
				testIntent.putExtra("comStyle", "testUser");//�û�����(�����û�)
				startActivity(testIntent);
				// testIntent.putExtra("user", "MapQ_q");
				break;
			case R.id.menue_about:
				// ��ת������MapQ����
				Intent aboutIntent = new Intent(MainActivity.this,About_App.class);
				startActivity(aboutIntent);
				break;
			case R.id.menue_exit:
				// �����˳�������ʾ��
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
			isExit.setMessage("Ҫ��ʱ�뿪��ô?");
			// ���ѡ��ť��ע�����
			isExit.setButton(AlertDialog.BUTTON_POSITIVE, "�뿪", BackListener);
			isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "����", BackListener);
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

	
}
