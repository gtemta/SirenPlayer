package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent sIntent;
        setContentView(R.layout.activity_main);
        sIntent = new Intent();
        sIntent.setAction("nctu.imf.sirenplayer.MainService");
        sIntent.setPackage(getPackageName());
        startService(sIntent);
        Intent aIntent;
        aIntent=new Intent();
        aIntent.setClass(MainActivity.this, MapsActivity.class);
        aIntent.setPackage(getPackageName());
        startActivity(aIntent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
