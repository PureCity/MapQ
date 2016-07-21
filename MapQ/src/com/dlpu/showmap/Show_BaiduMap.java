package com.dlpu.showmap;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MKMapStatus;
import com.baidu.mapapi.map.MKMapStatusChangeListener;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PoiOverlay;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKShareUrlResult;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.dlpu.global.Globalvar;
import com.dlpu.mapq.R;
import com.dlpu.question.GetUserAllQuestion;
import com.dlpu.question.ShowRealQuestion;
import com.dlpu.question.UserAskQuestion;
import com.dlpu.ws.GetQuestionAround_ws;
import com.dlpu.ws.UserLogout_ws;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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

@SuppressLint("ResourceAsColor")
public class Show_BaiduMap extends ActionBarActivity {

	// 地图视图控件
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	// 视图类型判定
	boolean ViewisState = false;
	boolean ViewTraffic = false;
	// 地理位置控件
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	// 百度地图搜索服务控件
	MKSearch mMKsearch = new MKSearch();
	BaiduSearchListener BaiduSearch = new BaiduSearchListener();
	// 地图点击事件监听
	MKMapTouchListener mapTouchListener = null;
	MKMapStatusChangeListener mapStatusChangelistener = null;

	protected static final int MENU_SHOWMENUE = Menu.FIRST;// 返回菜单的ID
	protected static final int MENU_MYPLACE = Menu.FIRST + 4;// 我的位置
	protected static final int MENU_CHANGE_STATE = Menu.FIRST + 1;// 改为卫星视图
	protected static final int MENU_TRAFFIC = Menu.FIRST + 2;// 交通信息图层

	// 基本控件设置
	private TextView userTextView = null;// 显示用户名称的控件
	private EditText baiduMap_searchcity_EditText = null;// 搜索城市控件
	private EditText baiduMap_search_EditText = null;// 搜索框控件
	private Button baiduMap_search_Button = null;// 点击搜索控件
	private TextView baiduMap_showmyquestionButton = null;

	// 从LoginActivity获取到的登录信息
	private String strUid = null;
	private String strUserName = null;
	private String strStateInfo = null;

	// 当前显示的问题标题
	PopupOverlay pop = null;// 存放当前问题标题的处理事件pop
	TextView currentQuestionTextView = null;// 当前问题标题
	String currentQuestionID = null;// 当前问题的ID
	private boolean refislhQuestion = false;// 是否允许刷新周边问题

	// 相关信号
	private static final int LOGOUT_WS_SUCCESS = 1;// 调用注销用户WS成功
	// private static final int GET_NEAR_QUESTION_WS_SUCCESS = 2;// 调用获取附近问题WS成功

	private static final int LOGOUT_WS_FAIL = -1;// 注销用户的WS调用失败
	private static final int GET_NEAR_QUESTION_WS_FAIL = -2;// 调用获取附近问题WS失败

	private static final int WS_OUT_OF_TIMW = -99;// 登录过期

	private Handler getInfoHandler = null;

	// 地图指令控制
	private boolean findMyPlace = false;// 判断是否定位并移动到我的位置
	private boolean findMyPlaceNow = false;// 确保服务启动,用于允许开始同步定位

	// 配置网络连接
	Globalvar httpvalGlobalvar = new Globalvar();

	// 地图中心位置,默认为北京
	private String centerLongitudeString = "116";
	private String centerLatitudeSring = "39";
	// 用户长按的点
	private String clickLongitudeString = null;
	private String clickLatitudeString = null;

	private String SearchRange = "200";// 默认获取半径为111范围内的提问(跟随地图放大级别改变)

	@SuppressLint({ "NewApi", "HandlerLeak" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 实例化基本地图
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(null);

		setContentView(R.layout.show_baidumap_activity);
		// 实例化基本控件
		userTextView = (TextView) findViewById(R.id.show_username);
		baiduMap_searchcity_EditText = (EditText) findViewById(R.id.baidumap_search_city);
		baiduMap_search_EditText = (EditText) findViewById(R.id.baiduMap_search_EditText);
		baiduMap_search_Button = (Button) findViewById(R.id.baiduMap_search_Button);
		baiduMap_showmyquestionButton = (TextView) findViewById(R.id.show_baiduMap_showmyquestion);
		currentQuestionTextView = new TextView(this);

		// 添加按钮的事件监听
		Button_choise BaiduMap_Button_choise = new Button_choise();
		baiduMap_search_Button.setOnClickListener(BaiduMap_Button_choise);
		baiduMap_showmyquestionButton.setOnClickListener(BaiduMap_Button_choise);

		mMKsearch.init(mBMapMan, BaiduSearch);// 初始化检索

		getInfoHandler = new Handler() {// 基本控件加载完毕后,启用信息通道

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case LOGOUT_WS_FAIL:// 调用注销用户的WS失败
					Toast.makeText(Show_BaiduMap.this, "由于网络问题用户注销失败,请直接退出程序", Toast.LENGTH_SHORT).show();
					break;
				case LOGOUT_WS_SUCCESS:
					Toast.makeText(Show_BaiduMap.this, "注销成功", Toast.LENGTH_SHORT).show();
					break;
				case GET_NEAR_QUESTION_WS_FAIL:
					Toast.makeText(Show_BaiduMap.this, "获取附近问题失败", Toast.LENGTH_SHORT).show();
					break;
				// case GET_NEAR_QUESTION_WS_SUCCESS:
				// Toast.makeText(Show_BaiduMap.this, "成功获取周边问题",
				// Toast.LENGTH_SHORT).show();
				// break;
				case WS_OUT_OF_TIMW:
					Toast.makeText(Show_BaiduMap.this, "抱歉,您登录过期,请退出并重新登陆", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

		// 用户信息加载
		/******************************************************************************************/
		String userType = getIntent().getStringExtra("comStyle");
		if (userType.equals("user")) {// 等待strUid,strUserName,strStateInfo发送过来
			strUid = getIntent().getStringExtra("strUid");
			strUserName = getIntent().getStringExtra("strUserName");
			strStateInfo = getIntent().getStringExtra("strStateInfo");
			userTextView.setText("欢迎您  小Q成员：" + strUserName);
		} else if (userType.equals("testUser")) {
			userTextView.setText("请登陆已获得更好的体验");
			baiduMap_showmyquestionButton.setText("");// 非登录用户取消"我的问问"选项
		}
		/******************************************************************************************/

		// 地图图形管理控件
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.setBuiltInZoomControls(true);

		mLocationClient = new LocationClient(getApplicationContext()); // 声明LocationClient类
		mLocationClient.registerLocationListener(myListener); // 注册监听函数
		setBaiduMapOption();// 设置地图相关参数

		// 注册百度地图点击事件监听
		BaiduMap_Listener baiduMap_Listener = new BaiduMap_Listener();
		mMapView.regMapTouchListner(baiduMap_Listener);
		// 注册百度地图状态事件监听
		BaiduMap_StatusListener baiduMap_StatusListener = new BaiduMap_StatusListener();
		mMapView.regMapStatusChangeListener(baiduMap_StatusListener);

		// 默认初始化显示北京
		// 设置启用内置的缩放控件
		MapController mMapController = mMapView.getController();
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(12);// 设置地图zoom级别

		// 启动位置监听
		if (mLocationClient != null && !mLocationClient.isStarted()) {
			findMyPlace = true;// 默认定位(采取异步定位方式)
			mLocationClient.requestLocation();
			mLocationClient.start();
			Toast.makeText(this, "正在自动定位,建议打开GPS", Toast.LENGTH_SHORT).show();
		}
		Log.d("mLocationClient", "start");

		findMyPlaceNow = true;// 允许手动同步定位
	}

	// 菜单选项
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SHOWMENUE, 0, "返回");
		menu.add(0, MENU_MYPLACE, 0, "我的位置");
		menu.add(0, MENU_CHANGE_STATE, 0, "卫星视图");
		return super.onCreateOptionsMenu(menu);
	}

	// 菜单选项功能
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHOWMENUE:
			// 结束当前Activity
			Leave_Map();
			break;
		case MENU_CHANGE_STATE:
			// 卫星视图
			if (ViewisState == false) {
				mMapView.setSatellite(true);
				mMapView.refresh();
				ViewisState = true;
			} else {
				mMapView.setSatellite(false);
				mMapView.refresh();
				ViewisState = false;
			}
			break;
		case MENU_MYPLACE:
			// 移动到我当前的位置(测试使用)
			if (findMyPlaceNow) {
				Toast.makeText(Show_BaiduMap.this, "开始定位...", Toast.LENGTH_SHORT).show();
				MoveMyLoaction();
			}
			break;
		case MENU_TRAFFIC:
			// 交通信息图层
			if (ViewTraffic == false) {
				mMapView.setTraffic(true);
				mMapView.refresh();
				ViewTraffic = true;
			} else {
				mMapView.setTraffic(false);
				mMapView.refresh();
				ViewTraffic = false;
			}
			break;
		default:
			break;
		}
		return true;
	}

	// 按键监听
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Leave_Map();
		}
		return super.onKeyDown(keyCode, event);
	}

	// 覆写本Activity的基本生命进程方法
	@Override
	protected void onDestroy() {
		mMapView.destroy();
		// if (mBMapMan != null) {
		// mBMapMan.destroy();
		// mBMapMan = null;
		// }
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		mMapView.onPause();
		mBMapMan.stop();
		if (mLocationClient != null) {
			mLocationClient.stop();
		}
		super.onPause();
	}

	@Override
	protected void onResume() {
		mMapView.onResume();
		mBMapMan.start();
		if (mLocationClient != null) {
			mLocationClient.start();
		}
		super.onResume();
	}

	// 用户发表问题返回获取结果
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ClearMyLoaction();// 刷新地图上问题操作
		// 发表问题成功,刷新周边问题
		refislhQuestion = true;
		// 直接使用已经确认的地图中心开始开始刷新周边问题
		importQuestion();
		refislhQuestion = false;

	}

	// 注销用户线程
	private class UserLogout implements Runnable {

		@Override
		public void run() {
			UserLogout_ws logout_ws = new UserLogout_ws();
			logout_ws.setTheHttpIpString(httpvalGlobalvar.getTheHttpIpString());

			logout_ws.Kill_strWSSID(strStateInfo);
			String resultString = logout_ws.get_getWS();
			if (resultString.equals("false")) {// 发送失败结束消息
				Message failMessage = new Message();
				failMessage.what = LOGOUT_WS_FAIL;
				getInfoHandler.sendMessage(failMessage);
			} else if (resultString.equals("true")) {
				Message successMessage = new Message();
				successMessage.what = LOGOUT_WS_SUCCESS;
				getInfoHandler.sendMessage(successMessage);
			}

		}

	}

	// 获取周边提问调用WS线程
	private class GetNearQusetion_ws_class implements Runnable {

		@Override
		public void run() {
			GetQuestionAround_ws getQuestionAround = new GetQuestionAround_ws();
			getQuestionAround.setTheHttpIpString(httpvalGlobalvar.getTheHttpIpString());

			getQuestionAround.getNearQuestion(strUid, centerLatitudeSring, centerLongitudeString, SearchRange, strStateInfo);

			String resultString = getQuestionAround.get_getWS();
			if (resultString.equals("false")) {
				Message failMessage = new Message();
				failMessage.what = GET_NEAR_QUESTION_WS_FAIL;
				getInfoHandler.sendMessage(failMessage);
			} else {// 成功获取问题
				// Message successMessage = new Message();
				// successMessage.what = GET_NEAR_QUESTION_WS_SUCCESS;
				// getInfoHandler.sendMessage(successMessage);
				if (getQuestionAround.getStatusString().equals("success")) {
					// mMapView.getOverlays().clear();// 清除已有的覆盖物
					String[][] questionsStrings = getQuestionAround.getresult();
					ShowQuestionInMap(questionsStrings);// 在地图上显示问题
				} else {// ws过期
					Message ws_outMessage = new Message();
					ws_outMessage.what = WS_OUT_OF_TIMW;
					getInfoHandler.sendMessage(ws_outMessage);
				}
			}
		}

	}

	/** 监听对话框里面的button点击事件 */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "确认"按钮退出程序
				mBMapMan.stop();// 停止后台百度地图继续定位
				// 调用注销ws
				if (strUid != null) {// 登录用户注销
					UserLogout logout_ws = new UserLogout();
					Thread logoutThread = new Thread(logout_ws);
					logoutThread.start();
				}
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "取消"第二个按钮取消对话框
				break;
			default:
				break;
			}
		}
	};

	// 实现百度地图的BDLocationListener接口
	private class MyLocationListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null) {
				Log.e("onReceiveLocation", "location is null");
				return;
			}
			StringBuffer sb = new StringBuffer(256);
			sb.append("time : ");
			sb.append(location.getTime());
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			sb.append("\nlontitude : ");
			sb.append(location.getLongitude());
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			if (location.getLocType() == BDLocation.TypeGpsLocation) {
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
			}

			Log.d("getLocation", sb.toString());
			if (findMyPlace) {// 异步定位到当前地理位置
				MoveToMyLocation(location);
				findMyPlace = false;
			}
			mLocationClient.stop();// 定位成功后不需要后台持续定位
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	// 地图内按钮监听
	private class Button_choise implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.baiduMap_search_Button:
				// 检索地点服务
				String placeString = baiduMap_search_EditText.getText().toString();
				String cityString = baiduMap_searchcity_EditText.getText().toString();
				if (placeString == null || placeString.equals("") || placeString.length() < 2) {
					// 却保输入内容,否则会造成百度地图LBS搜索无响应,后台服务无响应
					Toast.makeText(Show_BaiduMap.this, "请输入详细的关键词", Toast.LENGTH_SHORT).show();
					return;
				} else {
					Toast.makeText(Show_BaiduMap.this, "开始搜索: " + placeString, Toast.LENGTH_SHORT).show();
					mMKsearch.poiSearchInCity(cityString, placeString);// 开始搜索
				}
				break;
			case R.id.show_baiduMap_showmyquestion:
				// 跳转至我的问问界面
				if (strUid != null) {
					Intent myquestionIntent = new Intent(Show_BaiduMap.this, GetUserAllQuestion.class);
					myquestionIntent.putExtra("strUserId", strUid);
					myquestionIntent.putExtra("wssid", strStateInfo);
					if (pop != null) {
						pop.hidePop();
					}
					startActivityForResult(myquestionIntent, 0);
				}
				break;
			default:
				break;
			}
		}

	}

	// 百度地图检索服务事件监听
	private class BaiduSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int error) {
			if (error != 0) {
				Toast.makeText(Show_BaiduMap.this, "抱歉,查询失败--百度地图", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(Show_BaiduMap.this, "查询地点成功--百度地图", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// 查询得到的目标,并全部反馈给地图上显示
			if (error != 0 || res == null) {
				Toast.makeText(Show_BaiduMap.this, "抱歉，查询失败--百度地图", Toast.LENGTH_LONG).show();
				return;
			}
			PoiOverlay overlay = new PoiOverlay(Show_BaiduMap.this, mMapView);
			overlay.setData(res.getAllPoi());// 得到所有符合条件的点
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(overlay);
			// mMapView.invalidate();
			mMapView.refresh();
			// 移动到查询到的位置
			for (MKPoiInfo info : res.getAllPoi()) {
				if (info.pt != null) {
					mMapView.getController().animateTo(info.pt);
					break;// 只跳转到第一个结果处
				}
			}
			Toast.makeText(Show_BaiduMap.this, "查询完毕", Toast.LENGTH_SHORT).show();
			refislhQuestion = true;// 设置重新加载问题
		}

		@Override
		public void onGetShareUrlResult(MKShareUrlResult arg0, int arg1, int arg2) {

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub

		}

	}

	// 覆写百度地图覆盖物监听类
	private class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}

		// 点击item触发事件
		@Override
		protected boolean onTap(int index) {
			currentQuestionTextView.setText(getItem(index).getTitle());// 获取问题标题
			currentQuestionTextView.setTextColor(R.color.Question_Title_background);
			currentQuestionID = getItem(index).getSnippet();// 获取当前问题ID
			PopQuestionoverlay aPopQuestionoverlay = new PopQuestionoverlay();
			pop = new PopupOverlay(mMapView, aPopQuestionoverlay);
			pop.showPopup(currentQuestionTextView, getItem(index).getPoint(), 32);
			return super.onTap(index);
		}

		// 点击Overlay触发事件
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			if (pop != null) {
				pop.hidePop();
			}
			mMapView.removeView(currentQuestionTextView);// 清除弹出的文本
			return super.onTap(arg0, arg1);
		}

	}

	// 覆写百度地图问题覆盖物点击事件监听
	private class PopQuestionoverlay implements PopupClickListener {

		@Override
		public void onClickedPopup(int index) {
			if (currentQuestionID != null) {

				Intent showquestionIntent = new Intent(Show_BaiduMap.this, ShowRealQuestion.class);
				showquestionIntent.putExtra("strQuestionId", currentQuestionID);
				showquestionIntent.putExtra("wssid", strStateInfo);
				showquestionIntent.putExtra("strUserId", strUid);
				startActivity(showquestionIntent);
			}
		}

	}

	// 百度地图点击事件监听
	private class BaiduMap_Listener implements MKMapTouchListener {

		@Override
		public void onMapClick(GeoPoint arg0) {
			// 地图单击事件
		}

		@Override
		public void onMapDoubleClick(GeoPoint arg0) {
			// 地图双击事件

		}

		@Override
		public void onMapLongClick(GeoPoint point) {
			// 长按地图事件
			// 发起提问
			if (strUid != null) {// 登录用户才有提问功能
				clickLatitudeString = (point.getLatitudeE6() / 1E6) + "";
				clickLongitudeString = (point.getLongitudeE6() / 1E6) + "";
				Intent askquestionIntent = new Intent(Show_BaiduMap.this, UserAskQuestion.class);
				askquestionIntent.putExtra("strUserid", strUid);
				askquestionIntent.putExtra("strLatitude", clickLatitudeString);
				askquestionIntent.putExtra("strLongitude", clickLongitudeString);
				askquestionIntent.putExtra("wssid", strStateInfo);
				startActivityForResult(askquestionIntent, 0);
			}
		}
	}

	// 百度地图状态监听
	private class BaiduMap_StatusListener implements MKMapStatusChangeListener {

		@Override
		public void onMapStatusChange(MKMapStatus mapStatus) {
			// float zoom = mapStatus.zoom; //地图缩放等级
			// int overlooking = mapStatus.overlooking; //地图俯视角度
			// int rotate = mapStatus.rotate; //地图旋转角度
			GeoPoint targetGeo = mapStatus.targetGeo; // 中心点的地理坐标
			if (targetGeo != null) {
				double oldcetnerLongitudeString = Double.parseDouble(centerLongitudeString);
				double oldcenterLatitudeSring = Double.parseDouble(centerLatitudeSring);
				if (oldcetnerLongitudeString - (targetGeo.getLongitudeE6() / 1E6) < -1) {
					// 地图移动超过1个经度或纬度后才重新加载问题(防止地图问题持续后台加载)
					centerLatitudeSring = (targetGeo.getLatitudeE6() / 1E6) + "";
					centerLongitudeString = (targetGeo.getLongitudeE6() / 1E6) + "";
					importQuestion();
				} else if (oldcetnerLongitudeString - (targetGeo.getLongitudeE6() / 1E6) > 1) {
					centerLatitudeSring = (targetGeo.getLatitudeE6() / 1E6) + "";
					centerLongitudeString = (targetGeo.getLongitudeE6() / 1E6) + "";
					importQuestion();
				} else if (oldcenterLatitudeSring - (targetGeo.getLatitudeE6() / 1E6) > 1) {
					centerLatitudeSring = (targetGeo.getLatitudeE6() / 1E6) + "";
					centerLongitudeString = (targetGeo.getLongitudeE6() / 1E6) + "";
					importQuestion();
				} else if (oldcenterLatitudeSring - (targetGeo.getLatitudeE6() / 1E6) < -1) {
					centerLatitudeSring = (targetGeo.getLatitudeE6() / 1E6) + "";
					centerLongitudeString = (targetGeo.getLongitudeE6() / 1E6) + "";
					importQuestion();
				} else if (refislhQuestion) {
					centerLatitudeSring = (targetGeo.getLatitudeE6() / 1E6) + "";
					centerLongitudeString = (targetGeo.getLongitudeE6() / 1E6) + "";
					importQuestion();
					refislhQuestion = false;
				}
			}
			Log.d("value of currentCenter", centerLatitudeSring);
			Log.d("value of currentCenter", centerLongitudeString);
		}

	}

	// 设置百度地图相关调用参数
	private void setBaiduMapOption() {
		// 设置定位的模式,类型
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// 采用gps定位
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式(高精度)
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(60000);// 设置发起定位请求的间隔时间为60000ms
		option.setIsNeedAddress(true);// 返回的定位结果包含地址信息
		option.setNeedDeviceDirect(true);// 返回的定位结果包含手机机头的方向
		option.disableCache(false);// 允许地图缓存功能
		option.setPoiNumber(7);// 最多返回POI的个数
		option.setPoiDistance(1000);// poi查询距离
		mLocationClient.setLocOption(option);
	}

	// 同步定位(刷新地图)
	private void ClearMyLoaction() {
		mMapView.getOverlays().clear();// 清除已有的覆盖物
		// 设置启用内置的缩放控件
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// 获取当前地理位置信息
		BDLocation myBdLocation = mLocationClient.getLastKnownLocation();
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);

		mMapView.refresh();
		// Toast.makeText(getApplicationContext(), "定位成功",
		// Toast.LENGTH_SHORT).show();
		refislhQuestion = true;// 刷新问题

	}

	// 将当前地图图案移动至我的位置(同步定位)
	private void MoveMyLoaction() {
		mMapView.getOverlays().clear();// 清除已有的覆盖物
		// 设置启用内置的缩放控件
		MapController mMapController = mMapView.getController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// 获取当前地理位置信息
		BDLocation myBdLocation = mLocationClient.getLastKnownLocation();
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(15);// 设置地图zoom级别
		mMapController.animateTo(point);
		mMapView.refresh();
		Toast.makeText(getApplicationContext(), "定位成功", Toast.LENGTH_SHORT).show();
		refislhQuestion = true;// 刷新问题

	}

	// 当当前地图图案移动至我的位置(异步定位)
	private void MoveToMyLocation(BDLocation mylocation) {
		mMapView.getOverlays().clear();// 清除已有的覆盖物
		MapController mMapController = mMapView.getController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// 获取当前地理位置信息
		BDLocation myBdLocation = mylocation;
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		// 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度 * 1E6)
		mMapController.setCenter(point);// 设置地图中心点
		mMapController.setZoom(15);// 设置地图zoom级别
		mMapController.animateTo(point);
		mMapView.refresh();
		refislhQuestion = true;// 刷新问题
	}

	// 在地图上显示所有的问题
	private void ShowQuestionInMap(String[][] q) {

		int num = 0;
		for (int i = 0; i < q.length; i++) {
			if (q[i][0] == null) {
				break;
			} else {
				num++;
			}
		}
		// Drawable myquestionDrawable = getResources().getDrawable(
		// R.drawable.myquestion_logo);// 我的提问图标
		Drawable otherquestionDrawable = getResources().getDrawable(R.drawable.otherquestion_logo);// 其它提问的图标
		OverlayItem[] questionItem = new OverlayItem[num];
		for (int i = 0; i < num; i++) {
			Double lat = Double.parseDouble(q[i][4]);
			Double lon = Double.parseDouble(q[i][3]);
			GeoPoint qPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
			questionItem[i] = new OverlayItem(qPoint, q[i][1], q[i][0]);// 传入对应的问题标题和问题ID
			questionItem[i].setMarker(otherquestionDrawable);

		}
		MyOverlay itmeMyOverlay = new MyOverlay(otherquestionDrawable, mMapView);
		mMapView.getOverlays().add(itmeMyOverlay);
		for (int i = 0; i < num; i++) {
			itmeMyOverlay.addItem(questionItem[i]);
		}
		mMapView.refresh();

	}

	// 启动线程,加载周边问题函数
	private void importQuestion() {
		// 加载附近问题
		if (strUid != null) {// 用户不为空
			GetNearQusetion_ws_class aGetNearQusetion_ws_class = new GetNearQusetion_ws_class();
			Thread getQuestionThread = new Thread(aGetNearQusetion_ws_class);
			getQuestionThread.start();
		}
	}

	// 退出当前地图
	private void Leave_Map() {

		// 创建退出对话框
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// 设置对话框标题
		isExit.setTitle("MapQ温馨提醒");
		// 设置对话框消息
		isExit.setMessage("返回上一个界面?");
		// 添加选择按钮并注册监听
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "返回", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "留下", BackListener);
		// 显示对话框
		isExit.show();
	}

}
