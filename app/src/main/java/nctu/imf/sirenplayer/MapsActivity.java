package nctu.imf.sirenplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jason on 15/11/13.
 * reference
 * http://www.cheng-min-i-taiwan.blogspot.tw/2013/04/google-maps-android-api-v2-android.html
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        ,ComponentCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
        ,com.google.android.gms.location.LocationListener{
    private static final String MapTag ="MapsActivity";
    private static GoogleMap mMap;
    ArrayList<LatLng> markerLatLng;
    private static boolean isFirstStart=true;
    private static boolean isNavigating=false;
    private static boolean isFocusAutocompleteView=false;
    private static int myMode=1;
    private final int DRIVE=1;
    private final int DRIVE_AVOID_HIGHWAY=21;


    /***************************DB******************************/
    private DBcontact dbcontact;
    //DB
    private DbDAO dbDAO;
    private static final String DBTag ="DBActivity";
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    // 儲存所有記事本的List物件
    private List<DBcontact> records;
    // 選單項目物件
    private ListView list_records;
    private Button toMap;
    private Button backToMap;
    private LinearLayout DBLayout;
    private LinearLayout helpLayout;
    private TextView helpTV;
    private static final int NOTI_ID =100;

    /***********************location**********************/
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private static Location currentLocation;
    private static Location nCurrentLocation;
    private static Location goLocation;
    private static LatLng goLatLng;
    private Marker currentMarker;
    private FloatingActionButton startSpeech;
    public boolean getGPSService = false;
    public boolean getLANService = false;


    /***************************Log******************************/
    private static boolean canShowLog =true;
    private TextView logTextView;

    /***************************Location******************************/
    final LatLng taiwan = new LatLng(25.033408, 121.564099);
    /**台北101*/
    final LatLng TAIPEI101 = new LatLng(25.033611, 121.565000);
    /**台北火車站*/
    final LatLng TAIPEI_TRAIN_STATION = new LatLng(25.047924, 121.517081);
    /**國立台灣博物館*/
    final LatLng NATIONAL_TAIWAN_MUSEUM = new LatLng(25.042902, 121.515030);
    /**墾丁*/
    final LatLng KENTING = new LatLng(21.946567, 120.798713);
    /**日月潭*/
    final LatLng ZINTUN = new LatLng(23.851676, 120.902008);
    final LatLng rec_Location = new LatLng(24.7855859,120.9986516);

    /***************************Traffic******************************/

    // 建立Google API用戶端物件
    private synchronized void configGoogleApiClient() {
        // Construct a GoogleApiClient for the {@link Places#GEO_DATA_API} using AutoManage
        // functionality, which automatically sets up the API client to handle Activity lifecycle
        // events. If your activity does not extend FragmentActivity, make sure to call connect()
        // and disconnect() explicitly.
        googleApiClient = new GoogleApiClient
                .Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    // 建立Location請求物件
    private void configLocationRequest() {
        locationRequest = new LocationRequest();
        // 設定讀取位置資訊的間隔時間為一秒（1000ms）
        locationRequest.setInterval(1000);
        // 設定讀取位置資訊最快的間隔時間為一秒（1000ms）
        locationRequest.setFastestInterval(1000);
        // 設定優先讀取高精確度的位置資訊（GPS）
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /***********************lifecycle*****************************/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null!=savedInstanceState){
            Log.d(MapTag,"savedInstanceState not null. get last location state.");
            currentLocation.setLatitude(savedInstanceState.getDouble("LAT"));
            currentLocation.setLongitude(savedInstanceState.getDouble("LNG"));
        }
        setContentView(R.layout.activity_main);
        testLocationProvider();
        testLanProvider();


        startSpeech = (FloatingActionButton) findViewById(R.id.start_speech);
        startSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, MainService.class);
//                intent.setAction("nctu.imf.sirenplayer.MainService");
                intent.setPackage(getPackageName());
                startService(intent);
                startSpeech.setEnabled(false);
                startSpeech.setVisibility(View.GONE);
                NotiCreate();
            }
        });

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);
        // 建立Google API用戶端物件
        configGoogleApiClient();
        // 建立Location請求物件
        configLocationRequest();

        Log.i(DBTag, "DB Start Up");
        DBLayout=(LinearLayout)findViewById(R.id.db_layout);
        helpLayout=(LinearLayout)findViewById(R.id.help_layout);
        list_records=(ListView)findViewById(R.id.db_listView);
        helpTV=(TextView)findViewById(R.id.help_text_view);
        helpTV.setMovementMethod(ScrollingMovementMethod.getInstance());
        //hint context
        helpTV.setText("====  語音指令提示 : ==== \n" );
        helpTV.append(" '結束語音','關閉語音','離開語音' : 關閉語音辨識系統。\n\n");
        helpTV.append(" '重新搜尋' : 重新搜尋想前往的地點。\n\n");
        helpTV.append(" '搜尋','尋找' : 開始搜尋想要前往的地點。\n\n");
        helpTV.append(" '清除' : 清除搜尋的文字欄。\n\n");
        helpTV.append(" '第x筆' : 選取第x筆的資料。\n\n");
        helpTV.append(" '開車' : 設定旅行模式為汽車。\n\n");
        helpTV.append(" '騎車','機車' : 設定旅行模式為機車。\n\n");
        helpTV.append(" '資料庫','紀錄','開啟資料庫','開啟歷史紀錄' : 檢視先前搜尋過的結果。\n\n");
        helpTV.append(" '關閉資料庫','關閉紀錄','關閉歷史紀錄' : 關閉檢視歷史紀錄的頁面。\n\n");
        helpTV.append(" '清除資料庫','清除紀錄','清除歷史紀錄' : 刪除所有歷史紀錄。\n\n");
        helpTV.append(" '導航','開始導航' : 進入導航模式。\n\n");
        helpTV.append(" '進階導航' : 進入google map 導航。\n\n");
        helpTV.append(" '停止導航','取消導航','終止導航','結束導航' : 結束導航模式。\n\n");
        helpTV.append(" '我的位置','定位','我的地點' : 移動畫面至您的所在地點。\n\n");
        helpTV.append(" '清除地圖' : 清理地圖上的標記。\n\n");
        helpTV.append(" '放大','縮小' : 調整視野的大小。\n\n");
        helpTV.append(" '關閉程式','離開程式','結束程式' : 關閉SirenPlayer。\n\n");
        helpTV.append("====  其他功能提示 : ==== \n\n");
        helpTV.append(" 您也可以透過點擊地圖規化導航路境。\n\n");
        helpTV.append(" 第一次點擊：我的位置往點擊位置。\n\n");
        helpTV.append(" 第二次點擊：第一次點擊位置往點擊位置。\n\n");
        helpTV.append(" 當您遠離目的地時將會有貼心提醒。");

        toMap=(Button)findViewById(R.id.back2map);
        backToMap=(Button)findViewById(R.id.back_to_map);

        dbDAO= new DbDAO(getApplicationContext());
        records = dbDAO.getAll();
        for(DBcontact record: records)
        {
            Log.d(DBTag, "ID in DB : " + record.getId());
        }

        dbAdapter =new DBAdapter(MapsActivity.this,R.layout.db_item,records);
        if(dbAdapter==null){
            Log.d(DBTag,"list_recorder is null");
        }else{
            list_records.setAdapter(dbAdapter);
            Log.i(DBTag, "Get records  " + dbDAO.getCount());
        }

        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DBLayout.setVisibility(View.GONE);
            }
        });
        backToMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpLayout.setVisibility(View.GONE);
            }
        });
        list_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                goLatLng=new LatLng(dbAdapter.get(position).get_Lat(), dbAdapter.get(position).get_Lng());

                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
                builder.include(goLatLng);
                LatLngBounds bounds=builder.build();
                int padding = 100; // offset from edges of the map in pixels
                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));

                addMarker(goLatLng, dbAdapter.get(position).get_Command(), "上次查詢時間:" + dbAdapter.get(position).get_Time());
                Log.d(DBTag, "Record list view position" + position);
                Log.e(DBTag, "LATLNG " + goLatLng);
                Log.e(DBTag, "location   " + currentLocation);
                Log.e(DBTag, "Lat " + currentLocation.getLatitude());
                Log.e(DBTag, "Lng " + currentLocation.getLongitude());


                if (goLatLng!=null){
                    Log.d(MapTag,"TEST "+dbAdapter.get(position).get_Command());
                    mNavigation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()),goLatLng);
                    Log.d(MapTag,"Navigation from current to "+dbAdapter.get(position).get_Command());
                }
                goLocation=new Location("goLocation");
                goLocation.setLatitude(goLatLng.latitude);
                goLocation.setLongitude(goLatLng.longitude);
                goLatLng=null;
                DBLayout.setVisibility(View.GONE);
            }
        });
        list_records.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("確認刪除?")
                        .setMessage("刪除'" + dbAdapter.get(position).get_Command() + "'?")
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.d(DBTag, "Delete id : " + id);
                                Log.d(DBTag, "number in DB : " + dbDAO.getCount());
                                Log.d(DBTag, "Delete dbAdapter get(position) id: " + dbAdapter.get(position).getId());
                                Log.d(DBTag, "dbadapter: " + dbAdapter.get(position).getId());
                                dbDAO.delete(dbAdapter.get(position).getId());
                                records.remove(position);
                                dbAdapter.notifyDataSetChanged();
                                Log.d(DBTag, "Delete compelete");
                            }
                        }).setNegativeButton("否", null).show();
                return true;
            }
        });

        Log.i(DBTag, "DB setup OK");

        markerLatLng=new ArrayList<>();
        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete_places);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUND_TAIWAN,null);
        mAutocompleteView.setAdapter(mAdapter);

        if (canShowLog){
            logTextView=(TextView)findViewById(R.id.log_text_view);
            logTextView.setVisibility(View.GONE);
        }
        Toast.makeText(this,"您好,建議您使用國語(台灣)進行語音操作",Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (currentLocation!=null){
            moveMap(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
        }else{
            moveMap(rec_Location);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("my-event"));

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected() && currentMarker != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (MainService.getServiceIsOn()){
            Intent intent = new Intent();
            intent.setClass(MapsActivity.this, MainService.class);
//                intent.setAction("nctu.imf.sirenplayer.MainService");
            intent.setPackage(getPackageName());
            stopService(intent);
            startSpeech.setEnabled(true);
            startSpeech.setVisibility(View.VISIBLE);
        }
        NotiClear();

        // 移除位置請求服務
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(
                    googleApiClient, (LocationListener) this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        // 移除Google API用戶端連線
        if (googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NotiClear();

//        int pid = android.os.Process.myPid();
//        android.os.Process.killProcess(pid);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putDouble("LAT", currentLocation.getLatitude());
        outState.putDouble("LNG", currentLocation.getLongitude());
    }

    /**************************util*************************/

    private void NotiCreate(){
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.map_icon)
                .setTicker("語音辨識中")
                .setContentTitle("SirenPlayer執行中...")
                .setContentText("語音執行中")
                .build();
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTI_ID, notification);
    }
    private void NotiClear(){
        ((NotificationManager)getSystemService(NOTIFICATION_SERVICE)).cancel(NOTI_ID);
    }

    private void testLocationProvider() {
        // TODO Auto-generated method stub

        try {
            LocationManager status = (LocationManager) (this.getSystemService(Context.LOCATION_SERVICE));
            if (status.isProviderEnabled(LocationManager.GPS_PROVIDER)|| status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                // 如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
                getGPSService = true; // 確認開啟定位服務

            } else {
                Toast.makeText(this, "定位服務尚未開啟，請開啟定位服務", Toast.LENGTH_LONG).show();
                AlertDialog.Builder ad = new AlertDialog.Builder(MapsActivity.this);
                ad.setTitle("您好,請至設定將'定位'功能開啟!! ");
                ad.setMessage("為有良好的應用程式體驗，請開啟定位服務" );
                ad.setNeutralButton("前往啟動定位服務",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                //不做任何事情 直接關閉對話方塊
                                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)); // 開啟設定頁面
                            }
                        });
                ad.show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void testLanProvider(){
        try {
            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connMgr != null) {
                NetworkInfo info = connMgr.getActiveNetworkInfo();
                NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                boolean isWifiConn = networkInfo.isConnected();
                networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
                boolean isMobileConn = networkInfo.isConnected();
                if(isWifiConn||isMobileConn){
                    getLANService = true;
                } else {
                    Toast.makeText(this, "網路服務尚未開啟，請開啟網路服務", Toast.LENGTH_LONG).show();
                    AlertDialog.Builder ad = new AlertDialog.Builder(MapsActivity.this);
                    ad.setTitle("您好,請至設定將'網路'功能開啟!! ");
                    ad.setMessage("為有良好的應用程式體驗，請開啟網路服務");
                    ad.setPositiveButton("啟動Wifi服務",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //不做任何事情 直接關閉對話方塊
                                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); // 開啟設定頁面
                                }
                            });
                    ad.setNegativeButton("啟動行動網路",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    //不做任何事情 直接關閉對話方塊
                                    startActivity(new Intent(Settings.ACTION_NETWORK_OPERATOR_SETTINGS)); // 開啟設定頁面
                                }
                            });
                    ad.show();
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ConnectionCallbacks
    @Override
    public void onConnected(Bundle bundle) {
        // 已經連線到Google Services
        // 啟動位置更新服務
        // 位置資訊更新的時候，應用程式會自動呼叫LocationListener.onLocationChanged
        LocationServices.FusedLocationApi.requestLocationUpdates(
                googleApiClient, locationRequest, (LocationListener) MapsActivity.this);
    }

    // ConnectionCallbacks
    @Override
    public void onConnectionSuspended(int i) {
        // Google Services連線中斷
        // int參數是連線中斷的代號

    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Google Services連線失敗
        // ConnectionResult參數是連線失敗的資訊
        int errorCode = connectionResult.getErrorCode();

        // 裝置沒有安裝Google Play服務
        if (errorCode == ConnectionResult.SERVICE_MISSING) {
            Toast.makeText(MapsActivity.this, R.string.google_play_service_missing, Toast.LENGTH_LONG).show();
        }
        Log.e(MapTag, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }

    // LocationListener
    @Override
    public void onLocationChanged(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        if(isNavigating){
            if(nCurrentLocation!=null){
                /********************go farther*******************/
                if(goLocation!=null){
                    float totalDistance=nCurrentLocation.distanceTo(goLocation);
                    float thisDistance=location.distanceTo(goLocation);
                    if (thisDistance-totalDistance>15){
                        Toast.makeText(this,"注意:可能正在遠離目的地",Toast.LENGTH_SHORT).show();
                    }else if (thisDistance-totalDistance>30){
                        Toast.makeText(this,"注意:您正嚴重地偏離目的地",Toast.LENGTH_SHORT).show();
                    }
                }
                float ctrlBearing=nCurrentLocation.bearingTo(location);
                float ctrlDistance=nCurrentLocation.distanceTo(location);
                if (ctrlDistance>10)
                {
                    if (canShowLog){
                        logTextView.setText("last:" + nCurrentLocation.getLatitude() + "," + nCurrentLocation.getLongitude() + "\n");
                        logTextView.append("current:" + location.getLatitude() + "," + location.getLongitude() + "\n");
                        logTextView.append("bearing:"+ctrlBearing+" ,distance:"+ctrlDistance);
                    }
                    Log.d(MapTag,"last:" + nCurrentLocation.getLatitude() + "," + nCurrentLocation.getLongitude());
                    Log.d(MapTag,"current:" + location.getLatitude() + "," + location.getLongitude());
                    Log.d(MapTag, "bearing:" + ctrlBearing + " ,distance:" + ctrlDistance);
                    moving(latLng, ctrlBearing, 65.5f, 18f);
                    nCurrentLocation=location;
                }
            }
        }
        // 移動地圖到目前的位置
        if(isFirstStart){
            moveMap(latLng);
            isFirstStart=false;
            nCurrentLocation=location;
        }
        // 位置改變
        // Location參數是目前的位置
        currentLocation = location;
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        if (mMap == null) {
            mMap = googleMap;
        }

        // Add a marker in Sydney and move the camera
//        mMap.addMarker(new MarkerOptions().position(taiwan).title("Taiwan"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(taiwan));
        setUpMap();
    }

    // 移動地圖到參數指定的位置
    private void moveMap(LatLng movePlace) {
        // 建立地圖攝影機的位置物件

        CameraPosition cameraPosition = new CameraPosition(movePlace,
                15f,
                0f,
                mMap.getCameraPosition().bearing);

        // 使用動畫的效果移動地圖
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    private void moving(LatLng movePlace,float moveBearing,float moveTilt,float moveZoom){
        CameraPosition currentPlace = new CameraPosition.Builder()
                .target(movePlace)
                .bearing(moveBearing)//30f
                .tilt(moveTilt)//65.6f
                .zoom(moveZoom)//18f
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(currentPlace));
    }

    private void setUpMap() {
        if (mMap!=null){
            // Enable MyLocation Button in the Map
            mMap.setMyLocationEnabled(true);

            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    // Adding new item to the ArrayList
                    markerLatLng.add(latLng);
                    if (markerLatLng.size() == 1) {
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mNavigation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latLng);
                        mMap.addMarker(options);
                    } else if (markerLatLng.size() == 2) {
                        MarkerOptions options = new MarkerOptions();
                        options.position(latLng);
                        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                        mNavigation(markerLatLng.get(0), latLng);
                        mMap.addMarker(options);
                    }else if (markerLatLng.size() == 3){
                        Toast.makeText(MapsActivity.this,"再輕觸地圖一次可以清除地圖",Toast.LENGTH_SHORT).show();
                    }else{
                        markerLatLng.clear();
                        mMap.clear();
                        currentMarker=null;
                    }
                }
            });
        }
    }

    private void mNavigation(LatLng origin,LatLng dest){
        if (currentMarker != null) {
            currentMarker.remove();
        }
        currentMarker = mMap.addMarker(new MarkerOptions()
                .title("我的地點")
                .position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE)));

        // Checks, whether start and end locations are captured
        LatLng fromLatLng = origin;
        LatLng toLatLng = dest;

        String url = getDirectionsUrl(fromLatLng, toLatLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions
        // API
        downloadTask.execute(url);
        mAutocompleteView.setText("");
    }

    // 在地圖加入指定位置與標題的標記
    private void addMarker(LatLng place, String title, String snippet) {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(R.drawable.marker);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(place)
                .title(title)
                .snippet(snippet);

        mMap.addMarker(markerOptions);
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String result = intent.getStringExtra("result");
            Log.d("receiver", "Got message: " + result);
            CaseSelect(result);
        }
    };

    /*****************發送url至google取得路徑方法*******************/
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String parameters = str_origin + "&" + str_dest ;

        String output = "json";

        String Mode = "driving";

        String url=null;

        switch (myMode){
            case DRIVE:
                url  = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters ;
                break;
            case DRIVE_AVOID_HIGHWAY:
                url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters +"&avoid=highways";
                break;
        }

        return url;
    }

    /*************從URL下載JSON資料的方法***************/
    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.connect();

            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {

            String data = "";

            try {
                data = downloadUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);

        }
    }

    public static GoogleMap getMaps(){
        return mMap;
    }

    /*****************************Autocomplete and search*******************************/

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */

    private PlaceAutocompleteAdapter mAdapter;

    private AutoCompleteTextView mAutocompleteView;

    private static final LatLngBounds BOUND_TAIWAN = new LatLngBounds(
            new LatLng(22, 120), new LatLng(25.3, 122));

    /**
     * Listener that handles selections from suggestions from the AutoCompleteTextView that
     * displays Place suggestions.
     * Gets the place id of the selected item and issues a request to the Places Geo Data API
     * to retrieve more details about the place.
     *
     * @see com.google.android.gms.location.places.GeoDataApi#getPlaceById(com.google.android.gms.common.api.GoogleApiClient,
     * String...)
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener
            = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            /*
             Retrieve the place ID of the selected item from the Adapter.
             The adapter stores each Place suggestion in a AutocompletePrediction from which we
             read the place ID and title.
              */
            final AutocompletePrediction item = mAdapter.getItem(position);
            Log.d(MapTag, "AutoComplete ID"+ mAdapter.getItem(position) );
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);
            Log.i(MapTag, "Autocomplete item selected: " + primaryText);
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
            Log.i(MapTag, "Called getPlaceById to get Place details for " + placeId);
        }
    };



    /**
     * Callback for results from a Places Geo Data API query that shows the first place result in
     * the details view on screen.
     */
    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback
            = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(PlaceBuffer places) {
            if (!places.getStatus().isSuccess()) {
                // Request did not complete successfully
                Log.e(MapTag, "Place query did not complete. Error: " + places.getStatus().toString());
                places.release();
                return;
            }
            if (markerLatLng.size() > 0) {
                markerLatLng.clear();
                mMap.clear();
            }
            // Get the Place object from the buffer.
            final Place place = places.get(0);

            // Format details of the place for display and show it in a TextView.
            Log.d(MapTag, String.valueOf(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri())));

            /************************intoDB************************/
            LatLng latlng=place.getLatLng();
            Log.d(MapTag, "# data in DB after insert:" + dbDAO.getCount());
            Log.d(MapTag, "LatLng:" + String.valueOf(latlng));
            dbcontact = new  DBcontact((dbDAO.getCount()+1)
                    ,place.getName().toString()
                    ,dbDAO.dBcontact.getLocaleDatetime()
                    ,latlng.latitude
                    ,latlng.longitude
            );
            dbDAO.insert(dbcontact);
            Log.d(MapTag, "# data in DB :" + dbDAO.getCount());
            Log.d(MapTag, "New Data ID"+dbcontact.getId() );
            Log.d(MapTag, "Insert to DB "+ place.getName().toString());
            Log.d(MapTag, "Lat " + latlng.latitude);
            Log.d(MapTag, "Lon " + latlng.longitude);

            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            builder.include(latlng);
            LatLngBounds bounds=builder.build();
            int padding = 100; // offset from edges of the map in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            addMarker(latlng, String.valueOf(place.getName())
                    , String.valueOf(place.getAddress()));
            mNavigation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latlng);

            //Close Keyboard
            InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(MapsActivity.this.getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);


            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution != null) {
                Log.d(MapTag, String.valueOf(Html.fromHtml(thirdPartyAttribution.toString())));
            }

            Log.i(MapTag, "Place details received: " + place.getName());
            Log.i(MapTag, "Place la&lngt received: " + place.getLatLng());
            goLatLng=place.getLatLng();
            goLocation=new Location("goLocation");
            goLocation.setLatitude(goLatLng.latitude);
            goLocation.setLongitude(goLatLng.longitude);

            places.release();
        }
    };
    private void tapOnRecord(int position)
    {
        list_records.performItemClick(list_records.getAdapter().getView(position, null, null)
                , position, list_records.getAdapter().getItemId(position));
        DBLayout.setVisibility(View.GONE);
    }
    private void  deleteRecord(int position){
        dbDAO.delete(dbAdapter.get(position).getId());
        records.remove(position);
        dbAdapter.notifyDataSetChanged();
        DBLayout.setVisibility(View.GONE);
    }



    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    public void CaseSelect(String caseSelect) {
        String toastStr = "" + caseSelect;
        if (caseSelect.startsWith("搜尋")||caseSelect.startsWith("尋找")){
            if (caseSelect.length()>2){
                String mySubstring=caseSelect.substring(2);
                mAutocompleteView.setText(mySubstring);
                isFocusAutocompleteView=false;
                logTextView.setText("caseSelect" + caseSelect + ",mySubstring:" + mySubstring);
                Log.d(MapTag,"caseSelect"+caseSelect+",mySubstring:"+mySubstring);
                toastStr += "\n您可以透過呼叫'第一筆'進行搜尋";
            }
        } else if (caseSelect.startsWith("重新搜尋")||caseSelect.startsWith("重新尋找")){
            if (caseSelect.length()>4){
                String mySubstring=caseSelect.substring(4);
                mAutocompleteView.setText(mySubstring);
                isFocusAutocompleteView=false;
                logTextView.setText("caseSelect"+caseSelect+",mySubstring:"+mySubstring);
                Log.d(MapTag,"caseSelect"+caseSelect+",mySubstring:"+mySubstring);
                toastStr+="\n您可以透過呼叫'第一筆'進行搜尋";
            }
        }else if (isFocusAutocompleteView){
            mAutocompleteView.setText(caseSelect);
            isFocusAutocompleteView=false;
            toastStr+="\n您可以透過呼叫'第一筆'進行搜尋";
        }
        int mSwitch=0;
        switch (caseSelect){
            case "哈囉":
                toastStr+="\n您好，請問您今天想去哪呢?";
                break;
            case "顯示除錯資訊":
            case "顯示除錯":
            case "開啟除錯":
            case "除錯":
                logTextView.setVisibility(View.VISIBLE);
                toastStr+="\n您可以呼叫'關閉除錯'以關閉除錯功能";
                break;
            case "隱藏除錯資訊":
            case "隱藏除錯":
            case "關閉除錯":
                logTextView.setVisibility(View.GONE);
                break;
            case "資料庫":
            case "歷史紀錄":
            case "紀錄":
            case "開啟資料庫":
            case "開啟歷史紀錄":
            case "開啟紀錄":
                records.clear();
                records.addAll(dbDAO.getAll());
                dbAdapter.notifyDataSetChanged();
                if (logTextView.getVisibility()==View.VISIBLE){
                    logTextView.setVisibility(View.GONE);
                }
                DBLayout.setVisibility(View.VISIBLE);
                toastStr+="\n您可以呼叫'關閉資料庫'以關閉資料庫功能";
                break;
            case "關閉資料庫":
            case "關閉歷史紀錄":
            case "關閉紀錄":
                DBLayout.setVisibility(View.GONE);
                break;
            case "幫助":
            case "協助":
            case "提示":
            case "開啟幫助":
            case "開啟協助":
            case "開啟提示":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    DBLayout.setVisibility(View.GONE);
                }
                helpLayout.setVisibility(View.VISIBLE);
                toastStr+="\n您可以呼叫'關閉提示'以關閉提示功能";
                break;
            case "關閉幫助":
            case "關閉提示":
            case "關閉協助":
                helpLayout.setVisibility(View.GONE);
                break;
            case "DB":
            case "BB":
            case "TVB":
            case "D1B":
            case "D1D":
            case "B1D":
            case "B1B":
            case "D1筆":
            case "第1B":
            case "第一筆":
                toastStr="第一筆";
            case "出發":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(0);
                }else{
                    mSwitch=1;
                }
                break;
            case "D2B":
            case "D2D":
            case "B2D":
            case "B2B":
            case "D2筆":
            case "第2B":
            case  "第二筆":
                toastStr="第二筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(1);
                }else{
                    mSwitch=2;
                }
                break;
            case "D3B":
            case "D3D":
            case "D3P":
            case "P3P":
            case "B3D":
            case "B3B":
            case "D3筆":
            case "13筆":
            case "第3B":
            case  "第三筆":
                toastStr="第三筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(2);
                }else{
                    mSwitch=3;
                }
                break;
            case "D4B":
            case "D4C":
            case "D4D":
            case "B4D":
            case "B4B":
            case "DCB":
            case "D4筆":
            case "14筆":
            case "14B":
            case "第4B":
            case  "第四筆":
            case  "第四壁":
            case  "第四集":
                toastStr="第四筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(3);
                }else{
                    mSwitch=4;
                }
                break;
            case "D5B":
            case "D5D":
            case "B5D":
            case "B5B":
            case "P5B":
            case "D5筆":
            case "第5B":
            case  "第五筆":
            case  "第五品":
                toastStr="第五筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(4);
                }else{
                    mSwitch=5;
                }
                break;
            case "D6B":
            case "D6D":
            case "BD":
            case "B6B":
            case "D6筆":
            case "第6B":
            case  "第六筆":
                toastStr="第六筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(5);
                }
                break;
            case "D7B":
            case "D7D":
            case "B7D":
            case "B7B":
            case "D7筆":
            case "第7B":
            case  "第七筆":
                toastStr="第七筆";
                if (DBLayout.getVisibility()==View.VISIBLE){
                    tapOnRecord(6);
                }
                break;
            case "刪除第一筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(0);
                }
                break;
            case "刪除第二筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(1);
                }
                break;
            case "刪除第三筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(2);
                }
                break;
            case "刪除第四筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(3);
                }
                break;
            case "刪除第五筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(4);
                }
                break;
            case "刪除第六筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(5);
                }
                break;
            case "刪除第七筆":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    deleteRecord(6);
                }
                break;
            case "閉嘴":
            case "結束語音":
            case "關閉語音":
            case "離開語音":
            case "停止語音":
            case "暫停語音":
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, MainService.class);
//                intent.setAction("nctu.imf.sirenplayer.MainService");
                intent.setPackage(getPackageName());
                stopService(intent);
                startSpeech.setEnabled(true);
                startSpeech.setVisibility(View.VISIBLE);
                NotiClear();
                toastStr+="\n您可以觸碰'開啟語音按鍵'以再次開啟語音功能";
                break;
            case "關閉":
            case "離開":
                if (DBLayout.getVisibility()==View.VISIBLE){
                    DBLayout.setVisibility(View.GONE);
                    break;
                }else if (helpLayout.getVisibility()==View.VISIBLE){
                    helpLayout.setVisibility(View.GONE);
                    break;
                }
            case "結束":
            case "關閉城市":
            case "結束城市":
            case "離開城市":
            case "離開程式":
            case "結束程式":
            case "關閉程式":
                finish();
                toastStr+="\n謝謝您的使用，祝您旅途愉快";
                break;
            case "導航":
            case "開始導航":
                moving(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()),0,65.5f, 18f);
                isNavigating=true;
                toastStr+="\n您可以呼叫'關閉導航' 來結束導航模式";
                break;
            case "進階導航":
                if (goLatLng!=null){
                    if (myMode==DRIVE){
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+goLatLng.latitude+","+goLatLng.longitude+"&avoid=tf");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }else if (myMode==DRIVE_AVOID_HIGHWAY){
                        Uri gmmIntentUri = Uri.parse("google.navigation:q="+goLatLng.latitude+","+goLatLng.longitude+"&avoid=thf");
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                        mapIntent.setPackage("com.google.android.apps.maps");
                        startActivity(mapIntent);
                    }
                    toastStr+="\n正在切換至google map導航,請稍候";
                }else{
                    toastStr+="\n請先指定目的地再使用進階導航";
                }
                goLatLng=null;
                break;
            case "停止導航":
            case "取消導航":
            case "終止導航":
            case "結束導航":
                isNavigating=false;
                moveMap(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                mAutocompleteView.setText("");
                toastStr+="\n您可以呼叫'開始導航'以再次開啟導航功能";
                break;
            case "清除地圖":
            case "清楚地圖":
                toastStr="清除地圖";
                mMap.clear();
                if (markerLatLng.size() > 0) {
                    markerLatLng.clear();
                }
                break;
            case "重新搜尋":
                mMap.clear();
                if (markerLatLng.size() > 0) {
                    markerLatLng.clear();
                }
                isFocusAutocompleteView=true;
                mAutocompleteView.setText("");
                toastStr+="\n您可以說出您想前往的地點";
                break;
            case "搜尋":
            case "尋找":
                isFocusAutocompleteView=true;
                toastStr+="\n您可以說出您想前往的地點";
                break;
            case "經由":
                toastStr+="\n功能實作中";
                break;
            case "清除":
                mAutocompleteView.setText("");
                break;
            case "放大":
                if (mMap.getCameraPosition().zoom>=mMap.getMaxZoomLevel())break;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom + 1));
                toastStr+="\n您可以呼叫'縮小'以調整地圖大小";
                break;
            case "縮小":
                if (mMap.getCameraPosition().zoom<=mMap.getMinZoomLevel())break;
                mMap.animateCamera( CameraUpdateFactory.zoomTo( mMap.getCameraPosition().zoom - 1 ) );
                toastStr+="\n您可以呼叫'放大'以調整地圖大小";
                break;
            case "立體":
                mMap.setBuildingsEnabled(true);
                Log.d(MapTag, "Setup 3D Map" );
                break;
            case "開車":
                myMode=DRIVE;
                break;
            case "騎車":
            case "機車":
            case "騎機車":
                myMode=DRIVE_AVOID_HIGHWAY;
                toastStr+="\n規劃路線時將為你避開高速公路";
                break;
            case "清除資料庫":
            case "清除資料":
            case "清除歷史紀錄":
            case "清除紀錄":
                dbDAO.clean();
                records.clear();
                dbAdapter.notifyDataSetChanged();
                break;
            case "我的位置":
            case "定位":
            case "我的地點":
                moving(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), 0, 0, 18f);
                break;
        }
        if (mSwitch!=0&&mAutocompleteView.length()>0){
            try{
                final AutocompletePrediction item = mAdapter.getItem(mSwitch-1);
                Log.d(MapTag, "AutoComplete ID"+ mAdapter.getItem(mSwitch-1) );
                final String placeId = item.getPlaceId();
                final CharSequence primaryText = item.getPrimaryText(null);
                Log.i(MapTag, "Autocomplete item selected: " + primaryText);
            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
                PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
                placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
                Log.i(MapTag, "Called getPlaceById to get Place details for " + placeId);


                //Close Keyboard
                InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(MapsActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }catch (Exception e){
                Log.d(MapTag,e.toString());
            }
        }
        Toast.makeText(MapsActivity.this, toastStr.toUpperCase(), Toast.LENGTH_SHORT).show();
    }
}