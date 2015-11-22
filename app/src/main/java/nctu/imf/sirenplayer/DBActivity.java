package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by IMF-H-A on 2015/10/3.
 */
public class DBActivity extends Activity{
    private DBcontact dbcontact;
    //DB
    private DbDAO dbDAO;
    private static final String TAG="DBActivity";
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    // 儲存所有記事本的List物件
    private List<DBcontact> records;
    // 選單項目物件
    private ListView list_records;
    private MenuItem add_record,search_record,revert_record,delete_record;
    private Button toMap;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        DBactstart();
        processViews();
       Intent intent = getIntent();
          dbcontact= new DBcontact();

        records = new ArrayList<DBcontact>();
        records.add(new DBcontact(1, "Test for ListView command 1", "True", new Date().getTime()));
        records.add(new DBcontact(2, "Test for ListView command 2", "False", new Date().getTime()));
        records.add(new DBcontact(3, "Test for ListView command 3", "True", new Date().getTime()));
        Log.i(TAG, "DB Example join");



        dbDAO = new DbDAO(getApplicationContext());
        Log.i(TAG, "DB setup ");

        if (dbDAO.getCount() == 0){
            dbDAO.sample();
        }
        toMap =(Button)findViewById(R.id.back2map);
        toMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent =new Intent();
                intent.setClass(DBActivity.this,MapsActivity.class);
                startActivity(intent);
               DBActivity.this.finish();

            }
        });
        records = dbDAO.getAll();
        dbAdapter =new DBAdapter(this,R.layout.db_item,records);
        list_records=(ListView)findViewById(R.id.db_listView);
        list_records.setAdapter(dbAdapter);

        Log.i(TAG, "Get records");
    }


    private void processViews(){
        //onSubmit();

    }


    public void onSubmit(){

            String Command =dbcontact.get_Command();
            String ConfirmCode = dbcontact.get_Confirm();
            long Time =dbcontact.get_Time();



            Intent result = getIntent();

            result.putExtra("Command",Command );
            result.putExtra("confirmcode", ConfirmCode);
            result.putExtra("Time",Time);


            setResult(Activity.RESULT_OK, result);
                finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        DBcontact dBcontact = (DBcontact) data.getExtras().getSerializable(
                "SirenPlayer.DBcontact");

        dBcontact = dbDAO.insert(dBcontact);
        records.add(dBcontact);
    }


    public void DBactstart() {
        Log.i(TAG, "DBactivity Start Up");
    }

    }


