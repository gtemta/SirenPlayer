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
    //記錄物件
    private DBcontact dbcontact;

    @Override
    protected void  onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_db);

        processViews();
        // 取得Intent物件
        Intent intent = getIntent();
        //新增
        dbcontact= new DBcontact();

    }


    private void processViews(){
    //onSubmit();

    }


    public void onSubmit(View view){

            // 讀取使用者輸入的標題與內容
            String Command =dbcontact.get_Command();
            String ConfirmCode = dbcontact.get_Confirm();
            long Time =dbcontact.get_Time();



            // 取得回傳資料用的Intent物件
            Intent result = getIntent();

            //設定回傳的記事物件
            result.putExtra("Command",Command );
            result.putExtra("confirmcode", ConfirmCode);
            result.putExtra("Time",Time);

            // 設定回應結果為確定
            setResult(Activity.RESULT_OK, result);
        // 結束
        finish();
    }





    }



