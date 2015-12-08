package nctu.imf.sirenplayer;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private MenuItem add_record,search_record,delete_record;
    // 已選擇項目數量
    private int selectedCount = 0;
    private Button toMap;
    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);


        DBactstart();

        dbcontact= new DBcontact();
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
                dbDAO.close();
               DBActivity.this.finish();
            }
        });
        records = dbDAO.getAll();
        dbAdapter =new DBAdapter(this,R.layout.db_item,records);
        list_records=(ListView)findViewById(R.id.db_listView);
        list_records.setAdapter(dbAdapter);
        Log.i(TAG, "Get records  " + dbDAO.getCount());
        list_records.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent =new Intent();
                intent.setClass(DBActivity.this, MapsActivity.class);
                Bundle bundle =new Bundle();
                bundle.putDouble("Record_Latitude",dbDAO.dBcontact.get_Lat());
                bundle.putDouble("Recrord_Longitude", dbDAO.dBcontact.get_Lng());
                intent.putExtras(bundle);
                startActivity(intent);
                dbDAO.close();
                DBActivity.this.finish();
            }
        });
        list_records.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, final long id) {
                new AlertDialog.Builder(DBActivity.this)
                        .setTitle("確認刪除?")
                        .setMessage("刪除第" + (position+1)+"項紀錄?" )
                        .setPositiveButton("是", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i(TAG, "Delete Listview item   " + position);
                                records.remove(position);
                                //ListView Delete
                                Log.i(TAG, "Delete DB item   " + dbAdapter.get(position).getId());
                                dbDAO.delete(dbAdapter.get(position).getId());
                                //****************DB delete
                                dbAdapter.notifyDataSetChanged();
                                Log.i(TAG, "Delete compelete");
                            }
                        }).setNegativeButton("否", null).show();
                return true;
            }
        });



    }


//private void processMenu(DBcontact dBcontact){
//    if (dbcontact != null){
//        dbcontact.setSelected(!dbcontact.isSelected());
//        if (dbcontact.isSelected()){
//            selectedCount++;
//        }
//        else {
//            selectedCount--;
//        }
//    }
//    add_record.setVisible(selectedCount == 0);
//    search_record.setVisible(selectedCount == 0);
//    delete_record.setVisible(selectedCount>0);
//}

//    public void onSubmit(){
//            String Command =dbcontact.get_Command();
//            String Time =dbcontact.get_Time();
//            Intent result = getIntent();
//
//            result.putExtra("Command",Command );
//            result.putExtra("Time", Time);
//
//            setResult(Activity.RESULT_OK, result);
//                finish();
//    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        DBcontact dBcontact = (DBcontact) data.getExtras().getSerializable(
                "SirenPlayer.DBcontact");

        dBcontact = dbDAO.insert(dBcontact);
        records.add(dBcontact);
        dbAdapter.notifyDataSetChanged();
    }


    public void DBactstart() {
        Log.i(TAG, "DBactivity Start Up");
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu){
//        menu.add(0, Menu.FIRST, 0, "delete item");
//        return super.onCreateOptionsMenu(menu);
//    }
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item){
//        switch (item.getItemId()){
//            case Menu.FIRST :
////                dbAdapter.removeItem();
//                dbAdapter.notifyDataSetChanged();
//                break;
//
//
//        }
//        return  super.onOptionsItemSelected(item);
//    }




}



