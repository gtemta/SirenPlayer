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

public class DbDAO {

    public static final String TABLE_NAME = "Record";


    public static final String KEY_ID = "_id";


    public static final String COMMAND = "command";
    public static final String CONFIRM = "confirm";
    public static final String TIME = "time";



    public static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    COMMAND + " TEXT NOT NULL, " +
                    CONFIRM + " TEXT ,"  +
                    TIME + " INTEGER NOT NULL  )";


    private SQLiteDatabase db;


    public DbDAO(Context context)
    {
        db = MyDBHelper.getDatabase(context);
    }


    public  void close(){
        db.close();
    }



    public DBcontact insert(DBcontact dBcontact){

        ContentValues cv = new ContentValues();


        cv.put(COMMAND,dBcontact.get_Command());
        cv.put(TIME,dBcontact.getLocaleDatetime());
        cv.put(CONFIRM,dBcontact.get_Confirm());

        long id = db.insert(TABLE_NAME, null, cv);
         dBcontact.Setid(id);
         return dBcontact;

    }


    public boolean update(DBcontact item) {

        ContentValues cv = new ContentValues();
        cv.put(COMMAND,item.get_Command());
        cv.put(TIME,item.getLocaleDatetime());
        cv.put(CONFIRM,item.get_Confirm());

        String where = KEY_ID + "=" + item.getId();


        return db.update(TABLE_NAME, cv, where, null) > 0;

    }


    public boolean delete(long id){

        String where = KEY_ID + "=" + id;

        return db.delete(TABLE_NAME, where, null) > 0;
    }


    public List<DBcontact> getAll() {
        List<DBcontact> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE_NAME, null,null,null,null,null,null);

        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }

        cursor.close();
        return result;
    }


    public DBcontact getRecord(Cursor cursor) {

        DBcontact result = new DBcontact();

        result.Setid(cursor.getLong(0));
        result.Set_Command(cursor.getString(1));
        result.Set_Time(cursor.getLong(2));
        result.Set_Confirm(cursor.getString(3));


        return result;
    }

    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }



    public void sample(){
        DBcontact RS1= new DBcontact(0,"SampleTest1forDB","True" ,new Date().getTime());
        DBcontact RS2= new DBcontact(0,"SampleTest2forDB","False",new Date().getTime());
        DBcontact RS3= new DBcontact(0,"SampleTest3forDB","False",new Date().getTime());
        DBcontact RS4= new DBcontact(0,"SampleTest4forDB","False",new Date().getTime());

        insert(RS1);
        insert(RS2);
        insert(RS3);
        insert(RS4);
    }



    //private MenuItem add

}