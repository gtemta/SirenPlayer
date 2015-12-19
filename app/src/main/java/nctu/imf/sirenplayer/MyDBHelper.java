package nctu.imf.sirenplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by IMF on 2015/11/21.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "todo";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "todos";
    private static SQLiteDatabase database;
    public MyDBHelper(Context context) {
        //透過建構子MyDBHelper直接呼叫父類別建構子來建立參數的資料庫
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(DbDAO.CREATE_TABLE);
        sqLiteDatabase.execSQL(DbDAO.CREATE_TABLE2);
        Log.d("MyDBHelper","db setup");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS");
        onCreate(sqLiteDatabase);
    }
    public MyDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                      int version) {
        super(context, name, factory, version);
    }
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, DATABASE_VERSION).getWritableDatabase();
        }

        return database;
    }
}
