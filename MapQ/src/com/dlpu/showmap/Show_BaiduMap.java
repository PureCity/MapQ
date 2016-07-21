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

	// ��ͼ��ͼ�ؼ�
	BMapManager mBMapMan = null;
	MapView mMapView = null;
	// ��ͼ�����ж�
	boolean ViewisState = false;
	boolean ViewTraffic = false;
	// ����λ�ÿؼ�
	public LocationClient mLocationClient = null;
	public BDLocationListener myListener = new MyLocationListener();
	// �ٶȵ�ͼ��������ؼ�
	MKSearch mMKsearch = new MKSearch();
	BaiduSearchListener BaiduSearch = new BaiduSearchListener();
	// ��ͼ����¼�����
	MKMapTouchListener mapTouchListener = null;
	MKMapStatusChangeListener mapStatusChangelistener = null;

	protected static final int MENU_SHOWMENUE = Menu.FIRST;// ���ز˵���ID
	protected static final int MENU_MYPLACE = Menu.FIRST + 4;// �ҵ�λ��
	protected static final int MENU_CHANGE_STATE = Menu.FIRST + 1;// ��Ϊ������ͼ
	protected static final int MENU_TRAFFIC = Menu.FIRST + 2;// ��ͨ��Ϣͼ��

	// �����ؼ�����
	private TextView userTextView = null;// ��ʾ�û����ƵĿؼ�
	private EditText baiduMap_searchcity_EditText = null;// �������пؼ�
	private EditText baiduMap_search_EditText = null;// ������ؼ�
	private Button baiduMap_search_Button = null;// ��������ؼ�
	private TextView baiduMap_showmyquestionButton = null;

	// ��LoginActivity��ȡ���ĵ�¼��Ϣ
	private String strUid = null;
	private String strUserName = null;
	private String strStateInfo = null;

	// ��ǰ��ʾ���������
	PopupOverlay pop = null;// ��ŵ�ǰ�������Ĵ����¼�pop
	TextView currentQuestionTextView = null;// ��ǰ�������
	String currentQuestionID = null;// ��ǰ�����ID
	private boolean refislhQuestion = false;// �Ƿ�����ˢ���ܱ�����

	// ����ź�
	private static final int LOGOUT_WS_SUCCESS = 1;// ����ע���û�WS�ɹ�
	// private static final int GET_NEAR_QUESTION_WS_SUCCESS = 2;// ���û�ȡ��������WS�ɹ�

	private static final int LOGOUT_WS_FAIL = -1;// ע���û���WS����ʧ��
	private static final int GET_NEAR_QUESTION_WS_FAIL = -2;// ���û�ȡ��������WSʧ��

	private static final int WS_OUT_OF_TIMW = -99;// ��¼����

	private Handler getInfoHandler = null;

	// ��ͼָ�����
	private boolean findMyPlace = false;// �ж��Ƿ�λ���ƶ����ҵ�λ��
	private boolean findMyPlaceNow = false;// ȷ����������,��������ʼͬ����λ

	// ������������
	Globalvar httpvalGlobalvar = new Globalvar();

	// ��ͼ����λ��,Ĭ��Ϊ����
	private String centerLongitudeString = "116";
	private String centerLatitudeSring = "39";
	// �û������ĵ�
	private String clickLongitudeString = null;
	private String clickLatitudeString = null;

	private String SearchRange = "200";// Ĭ�ϻ�ȡ�뾶Ϊ111��Χ�ڵ�����(�����ͼ�Ŵ󼶱�ı�)

	@SuppressLint({ "NewApi", "HandlerLeak" })
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// ʵ����������ͼ
		mBMapMan = new BMapManager(getApplication());
		mBMapMan.init(null);

		setContentView(R.layout.show_baidumap_activity);
		// ʵ���������ؼ�
		userTextView = (TextView) findViewById(R.id.show_username);
		baiduMap_searchcity_EditText = (EditText) findViewById(R.id.baidumap_search_city);
		baiduMap_search_EditText = (EditText) findViewById(R.id.baiduMap_search_EditText);
		baiduMap_search_Button = (Button) findViewById(R.id.baiduMap_search_Button);
		baiduMap_showmyquestionButton = (TextView) findViewById(R.id.show_baiduMap_showmyquestion);
		currentQuestionTextView = new TextView(this);

		// ��Ӱ�ť���¼�����
		Button_choise BaiduMap_Button_choise = new Button_choise();
		baiduMap_search_Button.setOnClickListener(BaiduMap_Button_choise);
		baiduMap_showmyquestionButton.setOnClickListener(BaiduMap_Button_choise);

		mMKsearch.init(mBMapMan, BaiduSearch);// ��ʼ������

		getInfoHandler = new Handler() {// �����ؼ�������Ϻ�,������Ϣͨ��

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case LOGOUT_WS_FAIL:// ����ע���û���WSʧ��
					Toast.makeText(Show_BaiduMap.this, "�������������û�ע��ʧ��,��ֱ���˳�����", Toast.LENGTH_SHORT).show();
					break;
				case LOGOUT_WS_SUCCESS:
					Toast.makeText(Show_BaiduMap.this, "ע���ɹ�", Toast.LENGTH_SHORT).show();
					break;
				case GET_NEAR_QUESTION_WS_FAIL:
					Toast.makeText(Show_BaiduMap.this, "��ȡ��������ʧ��", Toast.LENGTH_SHORT).show();
					break;
				// case GET_NEAR_QUESTION_WS_SUCCESS:
				// Toast.makeText(Show_BaiduMap.this, "�ɹ���ȡ�ܱ�����",
				// Toast.LENGTH_SHORT).show();
				// break;
				case WS_OUT_OF_TIMW:
					Toast.makeText(Show_BaiduMap.this, "��Ǹ,����¼����,���˳������µ�½", Toast.LENGTH_SHORT).show();
					break;
				default:
					break;
				}
			}

		};

		// �û���Ϣ����
		/******************************************************************************************/
		String userType = getIntent().getStringExtra("comStyle");
		if (userType.equals("user")) {// �ȴ�strUid,strUserName,strStateInfo���͹���
			strUid = getIntent().getStringExtra("strUid");
			strUserName = getIntent().getStringExtra("strUserName");
			strStateInfo = getIntent().getStringExtra("strStateInfo");
			userTextView.setText("��ӭ��  СQ��Ա��" + strUserName);
		} else if (userType.equals("testUser")) {
			userTextView.setText("���½�ѻ�ø��õ�����");
			baiduMap_showmyquestionButton.setText("");// �ǵ�¼�û�ȡ��"�ҵ�����"ѡ��
		}
		/******************************************************************************************/

		// ��ͼͼ�ι���ؼ�
		mMapView = (MapView) findViewById(R.id.map);
		mMapView.setBuiltInZoomControls(true);

		mLocationClient = new LocationClient(getApplicationContext()); // ����LocationClient��
		mLocationClient.registerLocationListener(myListener); // ע���������
		setBaiduMapOption();// ���õ�ͼ��ز���

		// ע��ٶȵ�ͼ����¼�����
		BaiduMap_Listener baiduMap_Listener = new BaiduMap_Listener();
		mMapView.regMapTouchListner(baiduMap_Listener);
		// ע��ٶȵ�ͼ״̬�¼�����
		BaiduMap_StatusListener baiduMap_StatusListener = new BaiduMap_StatusListener();
		mMapView.regMapStatusChangeListener(baiduMap_StatusListener);

		// Ĭ�ϳ�ʼ����ʾ����
		// �����������õ����ſؼ�
		MapController mMapController = mMapView.getController();
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		GeoPoint point = new GeoPoint((int) (39.915 * 1E6), (int) (116.404 * 1E6));
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);// ���õ�ͼ���ĵ�
		mMapController.setZoom(12);// ���õ�ͼzoom����

		// ����λ�ü���
		if (mLocationClient != null && !mLocationClient.isStarted()) {
			findMyPlace = true;// Ĭ�϶�λ(��ȡ�첽��λ��ʽ)
			mLocationClient.requestLocation();
			mLocationClient.start();
			Toast.makeText(this, "�����Զ���λ,�����GPS", Toast.LENGTH_SHORT).show();
		}
		Log.d("mLocationClient", "start");

		findMyPlaceNow = true;// �����ֶ�ͬ����λ
	}

	// �˵�ѡ��
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_SHOWMENUE, 0, "����");
		menu.add(0, MENU_MYPLACE, 0, "�ҵ�λ��");
		menu.add(0, MENU_CHANGE_STATE, 0, "������ͼ");
		return super.onCreateOptionsMenu(menu);
	}

	// �˵�ѡ���
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_SHOWMENUE:
			// ������ǰActivity
			Leave_Map();
			break;
		case MENU_CHANGE_STATE:
			// ������ͼ
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
			// �ƶ����ҵ�ǰ��λ��(����ʹ��)
			if (findMyPlaceNow) {
				Toast.makeText(Show_BaiduMap.this, "��ʼ��λ...", Toast.LENGTH_SHORT).show();
				MoveMyLoaction();
			}
			break;
		case MENU_TRAFFIC:
			// ��ͨ��Ϣͼ��
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

	// ��������
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Leave_Map();
		}
		return super.onKeyDown(keyCode, event);
	}

	// ��д��Activity�Ļ����������̷���
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

	// �û��������ⷵ�ػ�ȡ���
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		ClearMyLoaction();// ˢ�µ�ͼ���������
		// ��������ɹ�,ˢ���ܱ�����
		refislhQuestion = true;
		// ֱ��ʹ���Ѿ�ȷ�ϵĵ�ͼ���Ŀ�ʼ��ʼˢ���ܱ�����
		importQuestion();
		refislhQuestion = false;

	}

	// ע���û��߳�
	private class UserLogout implements Runnable {

		@Override
		public void run() {
			UserLogout_ws logout_ws = new UserLogout_ws();
			logout_ws.setTheHttpIpString(httpvalGlobalvar.getTheHttpIpString());

			logout_ws.Kill_strWSSID(strStateInfo);
			String resultString = logout_ws.get_getWS();
			if (resultString.equals("false")) {// ����ʧ�ܽ�����Ϣ
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

	// ��ȡ�ܱ����ʵ���WS�߳�
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
			} else {// �ɹ���ȡ����
				// Message successMessage = new Message();
				// successMessage.what = GET_NEAR_QUESTION_WS_SUCCESS;
				// getInfoHandler.sendMessage(successMessage);
				if (getQuestionAround.getStatusString().equals("success")) {
					// mMapView.getOverlays().clear();// ������еĸ�����
					String[][] questionsStrings = getQuestionAround.getresult();
					ShowQuestionInMap(questionsStrings);// �ڵ�ͼ����ʾ����
				} else {// ws����
					Message ws_outMessage = new Message();
					ws_outMessage.what = WS_OUT_OF_TIMW;
					getInfoHandler.sendMessage(ws_outMessage);
				}
			}
		}

	}

	/** �����Ի��������button����¼� */
	DialogInterface.OnClickListener BackListener = new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int which) {
			switch (which) {
			case AlertDialog.BUTTON_POSITIVE:// "ȷ��"��ť�˳�����
				mBMapMan.stop();// ֹͣ��̨�ٶȵ�ͼ������λ
				// ����ע��ws
				if (strUid != null) {// ��¼�û�ע��
					UserLogout logout_ws = new UserLogout();
					Thread logoutThread = new Thread(logout_ws);
					logoutThread.start();
				}
				finish();
				break;
			case AlertDialog.BUTTON_NEGATIVE:// "ȡ��"�ڶ�����ťȡ���Ի���
				break;
			default:
				break;
			}
		}
	};

	// ʵ�ְٶȵ�ͼ��BDLocationListener�ӿ�
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
			if (findMyPlace) {// �첽��λ����ǰ����λ��
				MoveToMyLocation(location);
				findMyPlace = false;
			}
			mLocationClient.stop();// ��λ�ɹ�����Ҫ��̨������λ
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	// ��ͼ�ڰ�ť����
	private class Button_choise implements OnClickListener {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.baiduMap_search_Button:
				// �����ص����
				String placeString = baiduMap_search_EditText.getText().toString();
				String cityString = baiduMap_searchcity_EditText.getText().toString();
				if (placeString == null || placeString.equals("") || placeString.length() < 2) {
					// ȴ����������,�������ɰٶȵ�ͼLBS��������Ӧ,��̨��������Ӧ
					Toast.makeText(Show_BaiduMap.this, "��������ϸ�Ĺؼ���", Toast.LENGTH_SHORT).show();
					return;
				} else {
					Toast.makeText(Show_BaiduMap.this, "��ʼ����: " + placeString, Toast.LENGTH_SHORT).show();
					mMKsearch.poiSearchInCity(cityString, placeString);// ��ʼ����
				}
				break;
			case R.id.show_baiduMap_showmyquestion:
				// ��ת���ҵ����ʽ���
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

	// �ٶȵ�ͼ���������¼�����
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
				Toast.makeText(Show_BaiduMap.this, "��Ǹ,��ѯʧ��--�ٶȵ�ͼ", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(Show_BaiduMap.this, "��ѯ�ص�ɹ�--�ٶȵ�ͼ", Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		public void onGetPoiResult(MKPoiResult res, int type, int error) {
			// ��ѯ�õ���Ŀ��,��ȫ����������ͼ����ʾ
			if (error != 0 || res == null) {
				Toast.makeText(Show_BaiduMap.this, "��Ǹ����ѯʧ��--�ٶȵ�ͼ", Toast.LENGTH_LONG).show();
				return;
			}
			PoiOverlay overlay = new PoiOverlay(Show_BaiduMap.this, mMapView);
			overlay.setData(res.getAllPoi());// �õ����з��������ĵ�
			mMapView.getOverlays().clear();
			mMapView.getOverlays().add(overlay);
			// mMapView.invalidate();
			mMapView.refresh();
			// �ƶ�����ѯ����λ��
			for (MKPoiInfo info : res.getAllPoi()) {
				if (info.pt != null) {
					mMapView.getController().animateTo(info.pt);
					break;// ֻ��ת����һ�������
				}
			}
			Toast.makeText(Show_BaiduMap.this, "��ѯ���", Toast.LENGTH_SHORT).show();
			refislhQuestion = true;// �������¼�������
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

	// ��д�ٶȵ�ͼ�����������
	private class MyOverlay extends ItemizedOverlay<OverlayItem> {

		public MyOverlay(Drawable arg0, MapView arg1) {
			super(arg0, arg1);
		}

		// ���item�����¼�
		@Override
		protected boolean onTap(int index) {
			currentQuestionTextView.setText(getItem(index).getTitle());// ��ȡ�������
			currentQuestionTextView.setTextColor(R.color.Question_Title_background);
			currentQuestionID = getItem(index).getSnippet();// ��ȡ��ǰ����ID
			PopQuestionoverlay aPopQuestionoverlay = new PopQuestionoverlay();
			pop = new PopupOverlay(mMapView, aPopQuestionoverlay);
			pop.showPopup(currentQuestionTextView, getItem(index).getPoint(), 32);
			return super.onTap(index);
		}

		// ���Overlay�����¼�
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			if (pop != null) {
				pop.hidePop();
			}
			mMapView.removeView(currentQuestionTextView);// ����������ı�
			return super.onTap(arg0, arg1);
		}

	}

	// ��д�ٶȵ�ͼ���⸲�������¼�����
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

	// �ٶȵ�ͼ����¼�����
	private class BaiduMap_Listener implements MKMapTouchListener {

		@Override
		public void onMapClick(GeoPoint arg0) {
			// ��ͼ�����¼�
		}

		@Override
		public void onMapDoubleClick(GeoPoint arg0) {
			// ��ͼ˫���¼�

		}

		@Override
		public void onMapLongClick(GeoPoint point) {
			// ������ͼ�¼�
			// ��������
			if (strUid != null) {// ��¼�û��������ʹ���
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

	// �ٶȵ�ͼ״̬����
	private class BaiduMap_StatusListener implements MKMapStatusChangeListener {

		@Override
		public void onMapStatusChange(MKMapStatus mapStatus) {
			// float zoom = mapStatus.zoom; //��ͼ���ŵȼ�
			// int overlooking = mapStatus.overlooking; //��ͼ���ӽǶ�
			// int rotate = mapStatus.rotate; //��ͼ��ת�Ƕ�
			GeoPoint targetGeo = mapStatus.targetGeo; // ���ĵ�ĵ�������
			if (targetGeo != null) {
				double oldcetnerLongitudeString = Double.parseDouble(centerLongitudeString);
				double oldcenterLatitudeSring = Double.parseDouble(centerLatitudeSring);
				if (oldcetnerLongitudeString - (targetGeo.getLongitudeE6() / 1E6) < -1) {
					// ��ͼ�ƶ�����1�����Ȼ�γ�Ⱥ�����¼�������(��ֹ��ͼ���������̨����)
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

	// ���ðٶȵ�ͼ��ص��ò���
	private void setBaiduMapOption() {
		// ���ö�λ��ģʽ,����
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true);// ����gps��λ
		option.setLocationMode(LocationMode.Hight_Accuracy);// ���ö�λģʽ(�߾���)
		option.setCoorType("bd09ll");// ���صĶ�λ����ǰٶȾ�γ�ȣ�Ĭ��ֵgcj02
		option.setScanSpan(60000);// ���÷���λ����ļ��ʱ��Ϊ60000ms
		option.setIsNeedAddress(true);// ���صĶ�λ���������ַ��Ϣ
		option.setNeedDeviceDirect(true);// ���صĶ�λ��������ֻ���ͷ�ķ���
		option.disableCache(false);// �����ͼ���湦��
		option.setPoiNumber(7);// ��෵��POI�ĸ���
		option.setPoiDistance(1000);// poi��ѯ����
		mLocationClient.setLocOption(option);
	}

	// ͬ����λ(ˢ�µ�ͼ)
	private void ClearMyLoaction() {
		mMapView.getOverlays().clear();// ������еĸ�����
		// �����������õ����ſؼ�
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// ��ȡ��ǰ����λ����Ϣ
		BDLocation myBdLocation = mLocationClient.getLastKnownLocation();
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);

		mMapView.refresh();
		// Toast.makeText(getApplicationContext(), "��λ�ɹ�",
		// Toast.LENGTH_SHORT).show();
		refislhQuestion = true;// ˢ������

	}

	// ����ǰ��ͼͼ���ƶ����ҵ�λ��(ͬ����λ)
	private void MoveMyLoaction() {
		mMapView.getOverlays().clear();// ������еĸ�����
		// �����������õ����ſؼ�
		MapController mMapController = mMapView.getController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// ��ȡ��ǰ����λ����Ϣ
		BDLocation myBdLocation = mLocationClient.getLastKnownLocation();
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);// ���õ�ͼ���ĵ�
		mMapController.setZoom(15);// ���õ�ͼzoom����
		mMapController.animateTo(point);
		mMapView.refresh();
		Toast.makeText(getApplicationContext(), "��λ�ɹ�", Toast.LENGTH_SHORT).show();
		refislhQuestion = true;// ˢ������

	}

	// ����ǰ��ͼͼ���ƶ����ҵ�λ��(�첽��λ)
	private void MoveToMyLocation(BDLocation mylocation) {
		mMapView.getOverlays().clear();// ������еĸ�����
		MapController mMapController = mMapView.getController();
		MyLocationOverlay myLocationOverlay = new MyLocationOverlay(mMapView);
		myLocationOverlay.enableCompass();
		LocationData locData = new LocationData();
		// ��ȡ��ǰ����λ����Ϣ
		BDLocation myBdLocation = mylocation;
		locData.latitude = myBdLocation.getLatitude();
		locData.longitude = myBdLocation.getLongitude();
		locData.direction = myBdLocation.getDirection();
		myLocationOverlay.setData(locData);
		mMapView.getOverlays().add(myLocationOverlay);
		// �õ�mMapView�Ŀ���Ȩ,�����������ƺ�����ƽ�ƺ�����
		GeoPoint point = new GeoPoint((int) (locData.latitude * 1E6), (int) (locData.longitude * 1E6));
		// �ø����ľ�γ�ȹ���һ��GeoPoint����λ��΢�� (�� * 1E6)
		mMapController.setCenter(point);// ���õ�ͼ���ĵ�
		mMapController.setZoom(15);// ���õ�ͼzoom����
		mMapController.animateTo(point);
		mMapView.refresh();
		refislhQuestion = true;// ˢ������
	}

	// �ڵ�ͼ����ʾ���е�����
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
		// R.drawable.myquestion_logo);// �ҵ�����ͼ��
		Drawable otherquestionDrawable = getResources().getDrawable(R.drawable.otherquestion_logo);// �������ʵ�ͼ��
		OverlayItem[] questionItem = new OverlayItem[num];
		for (int i = 0; i < num; i++) {
			Double lat = Double.parseDouble(q[i][4]);
			Double lon = Double.parseDouble(q[i][3]);
			GeoPoint qPoint = new GeoPoint((int) (lat * 1E6), (int) (lon * 1E6));
			questionItem[i] = new OverlayItem(qPoint, q[i][1], q[i][0]);// �����Ӧ��������������ID
			questionItem[i].setMarker(otherquestionDrawable);

		}
		MyOverlay itmeMyOverlay = new MyOverlay(otherquestionDrawable, mMapView);
		mMapView.getOverlays().add(itmeMyOverlay);
		for (int i = 0; i < num; i++) {
			itmeMyOverlay.addItem(questionItem[i]);
		}
		mMapView.refresh();

	}

	// �����߳�,�����ܱ����⺯��
	private void importQuestion() {
		// ���ظ�������
		if (strUid != null) {// �û���Ϊ��
			GetNearQusetion_ws_class aGetNearQusetion_ws_class = new GetNearQusetion_ws_class();
			Thread getQuestionThread = new Thread(aGetNearQusetion_ws_class);
			getQuestionThread.start();
		}
	}

	// �˳���ǰ��ͼ
	private void Leave_Map() {

		// �����˳��Ի���
		AlertDialog isExit = new AlertDialog.Builder(this).create();
		// ���öԻ������
		isExit.setTitle("MapQ��ܰ����");
		// ���öԻ�����Ϣ
		isExit.setMessage("������һ������?");
		// ���ѡ��ť��ע�����
		isExit.setButton(AlertDialog.BUTTON_POSITIVE, "����", BackListener);
		isExit.setButton(AlertDialog.BUTTON_NEGATIVE, "����", BackListener);
		// ��ʾ�Ի���
		isExit.show();
	}

}
