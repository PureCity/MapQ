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
		// 菜单选项事件监听
		@Override
		public void onClick(View v) {
			int theId = v.getId();
			switch (theId) {
			case R.id.menue_login:
				// 跳转至登录界面事件
				Intent aIntent = new Intent();
				aIntent.setClass(MainActivity.this, UserLogin.class);
				startActivity(aIntent);
				break;
			case R.id.menue_try:
				// 跳转至直接试用界面
				Intent testIntent = new Intent();
				testIntent.setClass(MainActivity.this, Show_BaiduMap.class);
				testIntent.putExtra("comStyle", "testUser");//用户类型(体验用户)
				startActivity(testIntent);
				// testIntent.putExtra("user", "MapQ_q");
				break;
			case R.id.menue_about:
				// 跳转至关于MapQ界面
				Intent aboutIntent = new Intent(MainActivity.this,About_App.class);
				startActivity(aboutIntent);
				break;
			case R.id.menue_exit:
				// 弹出退出程序提示框
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
			isExit.setMessage("要暂时离开了么?");
			// 添加选择按钮并注册监听
			isExit.setButton(AlertDialog.BUTTON_POSITIVE, "离开", BackListener);
			isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "留下", BackListener);
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

	
}
