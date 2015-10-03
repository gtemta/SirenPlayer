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
        // 取得Intent物件
        Intent intent = getIntent();
        dbcontact= new DBcontact();

    }


    private void processViews(){


    }

/*
    public void onSubmit(View view){

            // 讀取使用者輸入的標題與內容
            String Command = DBcontact..getText().toString();
            String contentText = content_text.getText().toString();

            // 取得回傳資料用的Intent物件
            Intent result = getIntent();
            // 設定標題與內容
            result.putExtra("titleText", titleText);
            result.putExtra("contentText", contentText);

            // 設定回應結果為確定
            setResult(Activity.RESULT_OK, result);
        // 結束
        finish();
    }
*/




    }



