package nctu.imf.sirenplayer;

/**
 * Created by jason on 15/11/13.
 * reference
 * http://www.cheng-min-i-taiwan.blogspot.tw/2013/04/google-maps-android-api-v2-android.html
 */

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**************** 解析JSON格式 ********************/
public class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {
    GoogleMap maps=MapsActivity.getMaps();

    // Parsing the data in non-ui thread
    @Override
    protected List<List<HashMap<String, String>>> doInBackground(
            String... jsonData) {

        JSONObject jObject;
        List<List<HashMap<String, String>>> routes = null;

        try {
            jObject = new JSONObject(jsonData[0]);
            DirectionsJSONParser mParser;
            mParser = new DirectionsJSONParser();

            // Starts parsing data
            routes = mParser.parse(jObject);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return routes;
    }

    // Executes in UI thread, after the parsing process
    @Override
    protected void onPostExecute(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = null;
        PolylineOptions lineOptions = null;
        MarkerOptions markerOptions = new MarkerOptions();

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            points = new ArrayList<LatLng>();
            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }
            if(points.isEmpty()){
                continue;
            }else{
                lineOptions = new PolylineOptions();

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(5);  //導航路徑寬度
                lineOptions.color(Color.RED); //導航路徑顏色
            }
        }
        if (lineOptions!=null){
            // Drawing polyline in the Google Map for the i-th route
            maps.addPolyline(lineOptions);
        }else{
            Log.d("ParserTask","lineOptions is null, cannot add polyline");
        }
    }
}