package nctu.imf.sirenplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;


/**
 * Created by IMF-H-A on 2015/10/3.
 */
public class DBActivity extends Activity{

    private DBcontact dbcontact;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        processViews();
        // ���oIntent����
        Intent intent = getIntent();
        dbcontact= new DBcontact();

    }


    private void processViews(){


    }

/*
    public void onSubmit(View view){

            // Ū���ϥΪ̿�J�����D�P���e
            String Command = DBcontact..getText().toString();
            String contentText = content_text.getText().toString();

            // ���o�^�Ǹ�ƥΪ�Intent����
            Intent result = getIntent();
            // �]�w���D�P���e
            result.putExtra("titleText", titleText);
            result.putExtra("contentText", contentText);

            // �]�w�^�����G���T�w
            setResult(Activity.RESULT_OK, result);
        // ����
        finish();
    }
*/




    }



