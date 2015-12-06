package nctu.imf.sirenplayer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


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

    public DBcontact dBcontact=new DBcontact();

    public static  final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    COMMAND + " TEXT NOT NULL, " +
                    TIME + " INTEGER NOT NULL, " +
                    LAT + " INTEGER, "+
                    LNG +" INTEGER )";


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
        result.Set_Time(cursor.getString(3));
//        result.set_Lat(cursor.getDouble(4));
//        result.set_Lng(cursor.getDouble(5));

//********Unsure
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
        DBcontact RS1= new DBcontact(0,"SampleTest1forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS2= new DBcontact(0,"SampleTest2forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS3= new DBcontact(0,"SampleTest3forDB",dBcontact.get_Time(),25.047924, 121.517081);
        DBcontact RS4= new DBcontact(0,"SampleTest4forDB",dBcontact.get_Time(),25.047924, 121.517081);
        insert(RS1);
        insert(RS2);
        insert(RS3);
        insert(RS4);
    }



    //private MenuItem add

}
