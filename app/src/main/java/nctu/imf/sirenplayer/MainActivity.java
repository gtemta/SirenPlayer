package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainActivity extends Activity {
    Intent sIntent;
    Intent aIntent;
    private static final String TAG="MainActivity";
    //DB
    private DbDAO dbDAO;

    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    // 儲存所有記事本的List物件
    private List<DBcontact> records;
    // 選單項目物件
    private MenuItem add_record,search_record,revert_record,delete_record;
    // 已選擇項目數量
    private int selectedCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sIntent = new Intent();
        sIntent.setAction("nctu.imf.sirenplayer.MainService");
        sIntent.setPackage(getPackageName());
        startService(sIntent);
        aIntent=new Intent();
        aIntent.setClass(MainActivity.this, PlayerActivity.class);
        aIntent.setPackage(getPackageName());
        startActivity(aIntent);
        MainActivity.this.finish();
        //// ListView
        // 加入範例資料
        records = new ArrayList<DBcontact>();
        records.add(new DBcontact(1, "Test for Db command 1", "True", new Date().getTime()));
        records.add(new DBcontact(2, "Test for Db command 2", "False", new Date().getTime()));
        records.add(new DBcontact(3, "Test for Db command 3", "True", new Date().getTime()));

        // 建立自定Adapter物件
        dbAdapter = new DBAdapter(this, R.layout.db_item, records);


        //建立資料庫物件
        dbDAO = new DbDAO(getApplicationContext());
        Log.i(TAG, "DB setup ");
        // 如果資料庫是空的，就建立一些範例資料
        // 這是為了方便測試用的，完成應用程式以後可以拿掉
        if (dbDAO.getCount() == 0){
            dbDAO.sample();
        }

        //取得所有記事資料
        records = dbDAO.getAll();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            DBcontact dBcontact = (DBcontact) data.getExtras().getSerializable(
                    "net.macdidi.myandroidtutorial.Item");
                // 新增記事資料到資料庫
                dBcontact = dbDAO.insert(dBcontact);
                records.add(dBcontact);



    }
}
