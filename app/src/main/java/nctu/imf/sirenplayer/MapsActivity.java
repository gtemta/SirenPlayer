package nctu.imf.sirenplayer;

import android.content.BroadcastReceiver;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import android.support.design.widget.FloatingActionButton;

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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

/**
 * Created by jason on 15/11/13.
 * reference
 * http://www.cheng-min-i-taiwan.blogspot.tw/2013/04/google-maps-android-api-v2-android.html
 */

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback
        ,ComponentCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks
        ,com.google.android.gms.location.LocationListener{
    private static final String TAG="MapsActivity";
    private static GoogleMap mMap;
    ArrayList<LatLng> markerLatLng;
    private static boolean isFirstStart=true;
    private static boolean isNavigating=false;
    private static boolean isFocusAutocompleteView=false;

    //=====location====
    // Google API用戶端物件
    private GoogleApiClient googleApiClient;
    // Location請求物件
    private LocationRequest locationRequest;
    // 記錄目前最新的位置
    private static Location currentLocation;
    private LocationManager locationManager;
    // 顯示目前與儲存位置的標記物件
    private Marker currentMarker, itemMarker;
    private FloatingActionButton sw2DB;
    private FloatingActionButton startSpeech;
    private DbDAO dbDAO;
    private DBcontact searchword;


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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
            }
        });
        dbDAO= new DbDAO(getApplicationContext());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);
        // 建立Google API用戶端物件
        configGoogleApiClient();
        // 建立Location請求物件
        configLocationRequest();
        markerLatLng=new ArrayList<>();
        mAutocompleteView = (AutoCompleteTextView)findViewById(R.id.autocomplete_places);
        mAutocompleteView.setOnItemClickListener(mAutocompleteClickListener);
        mAdapter = new PlaceAutocompleteAdapter(this, googleApiClient, BOUND_TAIWAN,null);
        mAutocompleteView.setAdapter(mAdapter);
        sw2DB =(FloatingActionButton)findViewById(R.id.fab);
        sw2DB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setClass(MapsActivity.this, DBActivity.class);
                startActivity(intent);
                MapsActivity.this.onPause();

            }
        });
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
        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
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
            if(currentLocation!=null){
                double deltaLat = location.getLatitude()-currentLocation.getLatitude();  //y +north
                double deltaLon = location.getLongitude()-currentLocation.getLongitude();  //x +east
                float ctrlBearing = (float)Math.atan2(deltaLat,deltaLon);
                Log.d("Bearing",ctrlBearing+" degrees");
                moving(latLng,ctrlBearing,65.5f,18f);
            }
        }
        // 移動地圖到目前的位置
        if(isFirstStart){
            moveMap(latLng);
            isFirstStart=false;
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
        moveMap(taiwan);

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

                    // Creating MarkerOptions
                    MarkerOptions options = new MarkerOptions();

                    // Setting the position of the marker
                    options.position(latLng);

                    /**
                     * 起始及終點位置符號顏色
                     */
                    if (markerLatLng.size() == 1) {
                        options.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //起點符號顏色
                        // 設定目前位置的標記
                        if (currentMarker == null) {
                            currentMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                        } else {
                            currentMarker.setPosition(latLng);
                        }
                        mNavigation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latLng);
                    } else if (markerLatLng.size() == 2) {
                        options.icon(BitmapDescriptorFactory
                                .defaultMarker(BitmapDescriptorFactory.HUE_RED)); //終點符號顏色
                        mNavigation(markerLatLng.get(0), latLng);
                    }

                    // Add new marker to the Google Map Android API V2
                    mMap.addMarker(options);

                }
            });
        }
    }

    private void mNavigation(LatLng origin,LatLng dest){
        // Checks, whether start and end locations are captured
        LatLng fromLatLng = origin;
        LatLng toLatLng = dest;

        String url = getDirectionsUrl(fromLatLng, toLatLng);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions
        // API
        downloadTask.execute(url);
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
            moveMap(TAIPEI101);
        }
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
        //setUpMapIfNeeded();

        // 連線到Google API用戶端
        if (!googleApiClient.isConnected() && currentMarker != null) {
            googleApiClient.connect();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

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
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String result = intent.getStringExtra("result");
            Log.d("receiver", "Got message: " + result);
            Toast.makeText(MapsActivity.this, result.toUpperCase(), Toast.LENGTH_SHORT).show();
            CaseSelect(result);
        }
    };

    /*****************發送url至google取得路徑方法*******************/
    private String getDirectionsUrl(LatLng origin, LatLng dest) {

        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;

        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;

        String parameters = str_origin + "&" + str_dest ;

        String output = "json";

        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;

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
            final String placeId = item.getPlaceId();
            final CharSequence primaryText = item.getPrimaryText(null);

            Log.i(TAG, "Autocomplete item selected: " + primaryText);

            /*
             Issue a request to the Places Geo Data API to retrieve a Place object with additional
             details about the place.
              */
            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi.getPlaceById(googleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);

            Log.i(TAG, "Called getPlaceById to get Place details for " + placeId);
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
                Log.e(TAG, "Place query did not complete. Error: " + places.getStatus().toString());
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
            Log.d(TAG, String.valueOf(formatPlaceDetails(getResources(), place.getName(),
                    place.getId(), place.getAddress(), place.getPhoneNumber(),
                    place.getWebsiteUri())));
//            searchword = new  DBcontact(0,place.getName().toString(),dbDAO.dBcontact.getLocaleDatetime());
//            dbDAO.insert(searchword);
            LatLng latlng=place.getLatLng();
            Log.d(TAG, "LatLng:" + String.valueOf(latlng));
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            builder.include(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()));
            builder.include(latlng);
            LatLngBounds bounds=builder.build();
            int padding = 50; // offset from edges of the map in pixels
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, padding));
            addMarker(latlng, String.valueOf(place.getName())
                    , String.valueOf(place.getAddress()));
            mNavigation(new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()), latlng);


            // Display the third party attributions if set.
            final CharSequence thirdPartyAttribution = places.getAttributions();
            if (thirdPartyAttribution != null) {
                Log.d(TAG, String.valueOf(Html.fromHtml(thirdPartyAttribution.toString())));
            }

            Log.i(TAG, "Place details received: " + place.getName());

            places.release();
        }
    };

    private static Spanned formatPlaceDetails(Resources res, CharSequence name, String id,
                                              CharSequence address, CharSequence phoneNumber, Uri websiteUri) {
        return Html.fromHtml(res.getString(R.string.place_details, name, id, address, phoneNumber,
                websiteUri));

    }

    public void CaseSelect(String caseSelect){
        if (isFocusAutocompleteView){
            mAutocompleteView.setText(caseSelect);
            isFocusAutocompleteView=false;
        }
        switch (caseSelect){
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
                break;
            case "結束":
            case "關閉":
            case "離開":
                finish();
                break;
            case "導航":
            case "開始導航":
                isNavigating=true;
                break;
            case "停止導航":
            case "取消導航":
            case "終止導航":
            case "結束導航":
                isNavigating=false;
                moveMap(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                mAutocompleteView.setText("");
                break;
            case "清除地圖":
                mMap.clear();
                break;
            case "重新搜尋":
                mAutocompleteView.setText("");
            case "搜尋":
            case "尋找":
                isFocusAutocompleteView=true;
                break;
            case "經由":
                break;
            case "清除":
                mAutocompleteView.setText("");
                break;
            case "放大":
                if (mMap.getCameraPosition().zoom>=mMap.getMaxZoomLevel())break;
                mMap.animateCamera(CameraUpdateFactory.zoomTo(mMap.getCameraPosition().zoom + 1));
                break;
            case "縮小":
                if (mMap.getCameraPosition().zoom<=mMap.getMinZoomLevel())break;
                mMap.animateCamera( CameraUpdateFactory.zoomTo( mMap.getCameraPosition().zoom - 1 ) );
                break;
        }
    }
}