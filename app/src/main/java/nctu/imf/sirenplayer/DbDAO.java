package nctu.imf.sirenplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by IMF-H-A on 2015/10/3.
 */

public class DbDAO {

    public static final String TABLE_NAME = "Record";
    public static final String KEY_ID = "_id";
    public static final String COMMAND = "command";
    public static final String TIME = "time";
    public static final String LAT = "lat";
    public static final String LNG = "lng";
    private static final String TAG="DBDAO";
    public DBcontact dBcontact=new DBcontact();

    public static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COMMAND + " TEXT NOT NULL, " +
                    TIME + " INTEGER NOT NULL, " +
                    LAT + " INTEGER, "+
                    LNG +" INTEGER)";

    public static final String TABLE2_NAME = "WRecord";
    public static final String WORDS_ID = "_wid";
    public static final String WORDS = "words";
    public static final String WTIME = "wtime";
    public static  final String CREATE_TABLE2 =
            "CREATE TABLE " + TABLE2_NAME + " (" +
                    WORDS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    WORDS + " TEXT NOT NULL, " +
                    WTIME + " INTEGER NOT NULL)";

    public  DBcontact winsert(DBcontact dBcontact){

        ContentValues cv = new ContentValues();
        cv.put(WORDS,dBcontact.get_Words());
        cv.put(WTIME,dBcontact.get_Time());


        long id = db.insert(TABLE2_NAME, null, cv);
        dBcontact.Setid(id);
        return dBcontact;
    }

    private static SQLiteDatabase db;


    public DbDAO(Context context)
    {
        db = MyDBHelper.getDatabase(context);

    }

    public  void close(){
        db.close();
    }



    public  DBcontact insert(DBcontact dBcontact){

        ContentValues cv = new ContentValues();
        cv.put(COMMAND,dBcontact.get_Command());
        cv.put(TIME,dBcontact.get_Time());
        cv.put(LAT,dBcontact.get_Lat());
        cv.put(LNG,dBcontact.get_Lng());

         long id = db.insert(TABLE_NAME, null, cv);
         dBcontact.Setid(id);
         return dBcontact;
    }

//
//    public boolean update(DBcontact item) {
//        ContentValues cv = new ContentValues();
//        cv.put(COMMAND,item.get_Command());
//        cv.put(TIME,item.getLocaleDatetime());
//
//        String where = KEY_ID + "=" + item.getId();
//
//        return db.update(TABLE_NAME, cv, where, null) > 0;
//    }
//

    public boolean delete(long id){
        String where = KEY_ID + "=" + id;
        return db.delete(TABLE_NAME, where, null) > 0;
    }
    public void clean(){
        db.delete(TABLE_NAME,null,null);
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
        result.Set_Time(cursor.getString(2));
        result.set_Lat(cursor.getDouble(3));
        result.set_Lng(cursor.getDouble(4));

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
    public int getWCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE2_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }
    public List<DBcontact> getWAll() {
        List<DBcontact> result = new ArrayList<>();
        Cursor cursor = db.query(
                TABLE2_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            result.add(getRecord(cursor));
        }
        cursor.close();
        return result;
    }



    public void sample(){
        DBcontact RS1= new DBcontact(0,"SampleTest1forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS2= new DBcontact(0,"SampleTest2forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS3= new DBcontact(0,"SampleTest3forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS4= new DBcontact(0,"SampleTest4forDB",dBcontact.get_Time(),25.047924, 121.517081);
        insert(RS1);
        insert(RS2);
        insert(RS3);
        insert(RS4);
    }

}
