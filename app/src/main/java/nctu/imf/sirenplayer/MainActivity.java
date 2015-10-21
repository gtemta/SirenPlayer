package nctu.imf.sirenplayer;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    Intent sIntent;
    Intent aIntent;
    private static final String TAG="MainActivity";

    //temp
    private Button btnDB;
    private static final int NOTI_ID =100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sIntent = new Intent();
        sIntent.setAction("nctu.imf.sirenplayer.MainService");
        sIntent.setPackage(getPackageName());
        startService(sIntent);
        showNotification("程式執行中....");
        aIntent=new Intent();
        aIntent.setClass(MainActivity.this, PlayerActivity.class);
        aIntent.setPackage(getPackageName());
        startActivity(aIntent);
        MainActivity.this.finish();
        btnDB = (Button)findViewById(R.id.btn_db);
        btnDB.setOnClickListener(btnDBOnClick);

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

    //temp
    private View.OnClickListener btnDBOnClick =
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it =new Intent();
                    it.setClass(MainActivity.this,DBActivity.class);
                    startActivity(it);
                }
            };


}
