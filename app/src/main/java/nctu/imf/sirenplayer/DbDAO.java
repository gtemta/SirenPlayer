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
// ��ƥ\�����O
public class DbDAO {
    // ���W��
    public static final String TABLE_NAME = "Record";

    // �s��������W�١A�T�w����
    public static final String KEY_ID = "_id";

    // �䥦������W��
    public static final String COMMAND = "command";
    public static final String CONFIRM = "confirm";
    public static final String TIME = "time";


    //�إߪ�檺SQL���O
    public static  final String CREATE_TABLE =
            "CREATE TABLE" + TABLE_NAME + " (" +
                    KEY_ID + "INTEGER PRIMARY KEY AUTOINCREMENT, "+
                    COMMAND + "TEXT NOT NULL, " +
                    CONFIRM + "TEXT ,"  +
                    TIME + "INTEGER NOT NULL  )";

    //��Ʈw����
    private SQLiteDatabase db;

    //�غc�l
    public DbDAO(Context context)
    {
        db = MyDBHelper.getDatabase(context);
    }

    //������Ʈw
    public  void close(){
        db.close();
    }


    //�s�W�Ѽƫ��w������
    public  DBcontact insert(DBcontact dBcontact){
        // �إ߷ǳƷs�W��ƪ�ContentValues����
        ContentValues cv = new ContentValues();

        // �[�JContentValues����]�˪��s�W���
        // �Ĥ@�ӰѼƬO���W�١A �ĤG�ӰѼƬO��쪺���
        cv.put(COMMAND,dBcontact.get_Command());
        cv.put(TIME,dBcontact.getLocaleDatetime());
        cv.put(CONFIRM,dBcontact.get_Confirm());

        // �s�W�@����ƨè��o�s��
        // �Ĥ@�ӰѼƬO���W��
        // �ĤG�ӰѼƬO�S�����w���Ȫ��w�]��
        // �ĤT�ӰѼƬO�]�˷s�W��ƪ�ContentValues����
        long id = db.insert(TABLE_NAME, null, cv);

        // �]�w�s��
        dBcontact.Setid(id);
        // �^�ǵ��G
        return dBcontact;

    }

    // �ק�Ѽƫ��w������
    public boolean update(DBcontact item) {
        // �إ߷ǳƭק��ƪ�ContentValues����
        ContentValues cv = new ContentValues();

        // �[�JContentValues����]�˪��ק���
        // �Ĥ@�ӰѼƬO���W�١A �ĤG�ӰѼƬO��쪺���
        cv.put(COMMAND,item.get_Command());
        cv.put(TIME,item.getLocaleDatetime());
        cv.put(CONFIRM,item.get_Confirm());

        // �]�w�ק��ƪ����󬰽s��
        // �榡���u���W�١׸�ơv
        String where = KEY_ID + "=" + item.getId();

        // ����ק��ƨæ^�ǭק諸��Ƽƶq�O�_���\
        return db.update(TABLE_NAME, cv, where, null) > 0;

    }

    // �R���Ѽƫ��w�s�������
    public boolean delete(long id){
        // �]�w���󬰽s���A�榡���u���W��=��ơv
        String where = KEY_ID + "=" + id;
        // �R�����w�s����ƨæ^�ǧR���O�_���\
        return db.delete(TABLE_NAME, where, null) > 0;
    }

    // Ū���Ҧ��O�Ƹ��
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

    // ��Cursor�ثe����ƥ]�ˬ�����
    public DBcontact getRecord(Cursor cursor) {
        // �ǳƦ^�ǵ��G�Ϊ�����
        DBcontact result = new DBcontact();

        result.Setid(cursor.getLong(0));
        result.Set_Command(cursor.getString(1));
        result.Set_Time(cursor.getLong(2));
        result.Set_Confirm(cursor.getString(3));

        // �^�ǵ��G
        return result;
    }
    // ���o��Ƽƶq
    public int getCount() {
        int result = 0;
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME, null);

        if (cursor.moveToNext()) {
            result = cursor.getInt(0);
        }

        return result;
    }


    //�إ߽d�Ҹ��
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


    private DBAdapter dbAdapter;
    private List<DBcontact> dBcontacts;

    //private MenuItem add

}
