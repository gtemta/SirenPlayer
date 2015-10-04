package nctu.imf.sirenplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IMF-H-A on 2015/10/3.
 */
// 資料功能類別
public class DbDAO {
    // 表格名稱
    public static final String TABLE_NAME = "Record";

    // 編號表格欄位名稱，固定不變
    public static final String KEY_ID = "_id";

    // 其它表格欄位名稱
    public static final String COMMAND = "command";
    public static final String CONFIRM = "confirm";
    public static final String TIME = "time";


    //建立表格的SQL指令
    public static  final String CREATE_TABLE =
            "CREATE TABLE" + TABLE_NAME + " (" +
                    KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COMMAND + "TEXT NOT NULL, " +
                    CONFIRM + "TEXT ,"  +
                    TIME + "INTEGER NOT NULL  )";

    //資料庫物件
    private SQLiteDatabase db;

    //建構子
    public DbDAO(Context context)
    {
        db = MyDBHelper.getDatabase(context);
    }

    //關閉資料庫
    public  void close(){
        db.close();
    }


    //新增參數指定的物件
    public  DBcontact insert(DBcontact dBcontact){
        // 建立準備新增資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的新增資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COMMAND,dBcontact.get_Command());
        cv.put(TIME,dBcontact.getLocaleDatetime());
        cv.put(CONFIRM,dBcontact.get_Confirm());

        // 新增一筆資料並取得編號
        // 第一個參數是表格名稱
        // 第二個參數是沒有指定欄位值的預設值
        // 第三個參數是包裝新增資料的ContentValues物件
        long id = db.insert(TABLE_NAME, null, cv);

        // 設定編號
        dBcontact.Setid(id);
        // 回傳結果
        return dBcontact;

    }

    // 修改參數指定的物件
    public boolean update(DBcontact item) {
        // 建立準備修改資料的ContentValues物件
        ContentValues cv = new ContentValues();

        // 加入ContentValues物件包裝的修改資料
        // 第一個參數是欄位名稱， 第二個參數是欄位的資料
        cv.put(COMMAND,item.get_Command());
        cv.put(TIME,item.getLocaleDatetime());
        cv.put(CONFIRM,item.get_Confirm());

        // 設定修改資料的條件為編號
        // 格式為「欄位名稱＝資料」
        String where = KEY_ID + "=" + item.getId();

        // 執行修改資料並回傳修改的資料數量是否成功
        return db.update(TABLE_NAME, cv, where, null) > 0;

    }

    // 刪除參數指定編號的資料
    public boolean delete(long id){
        // 設定條件為編號，格式為「欄位名稱=資料」
        String where = KEY_ID + "=" + id;
        // 刪除指定編號資料並回傳刪除是否成功
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // 讀取所有記事資料
    public List<DBcontact> getAll() {
        List<DBcontact> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null, null, null, null, null, null, null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }

    // 把Cursor目前的資料包裝為物件
    public DBcontact getRecord(Cursor cursor) {
        // 準備回傳結果用的物件
        DBcontact result = new DBcontact();

        result.Setid(cursor.getLong(0));
        result.Set_Command(cursor.getString(1));
        result.set_Time(cursor.getLong(2));
        result.set_Confirm(cursor.getString(3));

        // 回傳結果
        return result;
    }
    // 取得資料數量
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }


    //建立範例資料
    public void sample(){
        DBcontact RS1= new DBcontact(0,"SampleTest1forDB","True",new Date().getTime());
        DBcontact RS2= new DBcontact(0,"SampleTest2forDB","False",new Date().getTime());
        DBcontact RS3= new DBcontact(0,"SampleTest3forDB","False",new Date().getTime());
        DBcontact RS4= new DBcontact(0,"SampleTest4forDB","False",new Date().getTime());

        insert(RS1);
        insert(RS2);
        insert(RS3);
        insert(RS4);
    }


    private DBAdapter dbAdapter;
    private List<DBcontact> dBcontacts;

    //private MenuItem add

}
