package nctu.imf.sirenplayer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by IMF-H-A on 2015/10/3.
 */
public class MyDBHelper extends SQLiteOpenHelper {
    // ��Ʈw�W��
    public static final String DATABASE_NAME = "mydata.db";
    // ��Ʈw�����A��Ƶ��c���ܪ��ɭԭn���o�ӼƦr�A�q�`�O�[�@
    public static final int VERSION = 1;
    // ��Ʈw����A�T�w������ܼ�
    private static SQLiteDatabase database;

    // �غc�l�A�b�@�몺���γ����ݭn�ק�
    public MyDBHelper(Context context, String name, CursorFactory factory,
                      int version) {
        super(context,name,factory,version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DbDAO.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // �R���즳�����
        db.execSQL("DROP TABLE IF EXISTS" + DbDAO.TABLE_NAME);

        // �I�sonCreate�إ߷s�������
        onCreate(db);
    }

    // �ݭn��Ʈw������I�s�o�Ӥ�k�A�o�Ӥ�k�b�@�몺���γ����ݭn�ק�
    public static SQLiteDatabase getDatabase(Context context) {
        if (database == null || !database.isOpen()) {
            database = new MyDBHelper(context, DATABASE_NAME,
                    null, VERSION).getWritableDatabase();
        }

        return database;
    }




}
