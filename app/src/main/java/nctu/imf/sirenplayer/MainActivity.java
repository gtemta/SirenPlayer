package nctu.imf.sirenplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends Activity {
    Intent sIntent;
    Intent aIntent;
    private static final int NOTI_ID =100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        sIntent = new Intent();
//        sIntent.setAction("nctu.imf.sirenplayer.MainService");
//        sIntent.setPackage(getPackageName());
//        startService(sIntent);
        aIntent=new Intent();
        aIntent.setClass(MainActivity.this, MapsActivity.class);
        aIntent.setPackage(getPackageName());
        startActivity(aIntent);
        MainActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
