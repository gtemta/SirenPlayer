package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by IMF-H-A on 2015/10/3.
 */
public class DBActivity extends Activity{
    //�O������
    private DBcontact dbcontact;
    //DB
    private DbDAO dbDAO;
    private static final String TAG="DBActivity";
    // ListView使用的自定Adapter物件
    private DBAdapter dbAdapter;
    // 儲存所有記事本的List物件
    private List<DBcontact> records;
    // 選單項目物件
    private MenuItem add_record,search_record,revert_record,delete_record;
    private ListView record_list ;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        processViews();
        // ���oIntent����
        Intent intent = getIntent();
        //�s�W
        dbcontact= new DBcontact();
        //// ListView
        // 加入範例資料
        records = new ArrayList<DBcontact>();
        records.add(new DBcontact(1, "Test for ListView command 1", "True", new Date().getTime()));
        records.add(new DBcontact(2, "Test for ListView command 2", "False", new Date().getTime()));
        records.add(new DBcontact(3, "Test for ListView command 3", "True", new Date().getTime()));

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
        dbAdapter =new DBAdapter(this,R.layout.db_item,records);
        record_list.setAdapter(dbAdapter);

    }


    private void processViews(){
        //onSubmit();

    }


    public void onSubmit(){

            // 讀取使用者輸入的標題與內容
            String Command =dbcontact.get_Command();
            String ConfirmCode = dbcontact.get_Confirm();
            long Time =dbcontact.get_Time();



            // ���o�^�Ǹ�ƥΪ�Intent����
            Intent result = getIntent();

            //�]�w�^�Ǫ��O�ƪ���
            result.putExtra("Command",Command );
            result.putExtra("confirmcode", ConfirmCode);
            result.putExtra("Time",Time);

            // �]�w�^�����G���T�w
            setResult(Activity.RESULT_OK, result);
        // ����
        finish();
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        DBcontact dBcontact = (DBcontact) data.getExtras().getSerializable(
                "SirenPlayer.DBcontact");
        // 新增記事資料到資料庫
        dBcontact = dbDAO.insert(dBcontact);
        records.add(dBcontact);
    }




    }



