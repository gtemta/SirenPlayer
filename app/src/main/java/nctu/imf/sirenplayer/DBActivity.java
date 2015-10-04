package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;

import java.util.Timer;


/**
 * Created by IMF-H-A on 2015/10/3.
 */
public class DBActivity extends Activity{
    //�O������
    private DBcontact dbcontact;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        processViews();
        // ���oIntent����
        Intent intent = getIntent();
        //�s�W
        dbcontact= new DBcontact();

    }


    private void processViews(){
    //onSubmit();

    }


    public void onSubmit(View view){

            // Ū���ϥΪ̿�J�����D�P���e
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





    }



