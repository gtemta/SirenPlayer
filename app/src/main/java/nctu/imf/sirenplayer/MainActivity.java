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
        sIntent = new Intent();
        sIntent.setAction("nctu.imf.sirenplayer.MainService");
        sIntent.setPackage(getPackageName());
        startService(sIntent);
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

    //底下未使用
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    private void showNotification(String sMsg){
        Notification noti = new Notification.Builder(this)
                .setSmallIcon(R.drawable.man)
                .setTicker(sMsg)
                .setContentTitle(getString(R.string.app_name))
                .setContentText(sMsg)
                .build();
        NotificationManager notificationManager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTI_ID, noti);
    }
}
