package nctu.imf.sirenplayer;

import android.app.ActionBar;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Jason on 2015/10/1.
 */
public class MainService extends Service{
    private static final String TAG="MainService";
    SpeechRecognizer speechRecognizer;
    ImageButton imgBtn=null;
    WindowManager windowManager=null;
    WindowManager.LayoutParams wmLayoutParams;
    private int xLast=0;
    private int yLast=0;
    private int xC=100;
    private int yC=100;
//    TimeThread timeThread;
    private boolean isSpeaking =false;
    private boolean needRestart =false;
    private boolean isMoving= false;
    private boolean isFirst =true;
    Intent intent;
    Timer timer;
    int countDown=5;
    int overTime=0;
    @Override
    public void onCreate() {
        super.onCreate();
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);

        createBTN();
        timer = new Timer();
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(MainService.this);
        speechRecognizer.setRecognitionListener(new listener());
        speechRecognizer.startListening(intent);
        timer.schedule(timerTask, 1000, 1000);
//        timeThread=new TimeThread();
//        timeThread.start();
    }

    private TimerTask timerTask=new TimerTask() {
        @Override
        public void run() {
            if(needRestart){//得到onError,onResults
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        restartSpeechRecognizer();
                        Log.i(TAG, "restartSpeechRecognizer #0");
                    }
                });
            }
            else if(!isSpeaking) {//沒在講話
                countDown--;
                if(countDown<=0){
                    myHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            restartSpeechRecognizer();
                            Log.i(TAG, "restartSpeechRecognizer #1");
                        }
                    });
                }
            }else if(isSpeaking && countDown>0){//正在講話，且未超時
                countDown--;
                if(countDown<0){
                    overTime++;
                }
            }else if (overTime>3) {//已超時
                myHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        restartSpeechRecognizer();
                        Log.i(TAG, "restartSpeechRecognizer #2");
                    }
                });
                overTime=0;
            } else {
                //noMore
            }
            Message msg=new Message();
            msg.what=countDown;
            myHandler.sendMessage(msg);
        }
    };

    private Handler myHandler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    break;
                case 1:
                    imgBtn.setImageResource(R.drawable.number1);
                    break;
                case 2:
                    imgBtn.setImageResource(R.drawable.number2);
                    break;
                case 3:
                    imgBtn.setImageResource(R.drawable.number3);
                    break;
                case 4:
                    imgBtn.setImageResource(R.drawable.number4);
                    break;
                case 5:
                    imgBtn.setImageResource(R.drawable.number5);
                    break;
            }
            super.handleMessage(msg);
        }
    };

/*
    class TimeThread extends Thread {
        @Override
        public void run() {
            while(!isSpeaking){
                try{
                    for (int i=5;i>0;i--){
                        Log.i(TAG, String.valueOf(i));
                        if(isSpeaking){
                            return;
                        }
                        Message msg=new Message();
                        msg.what=i;
                        myHandler.sendMessage(msg);
                        sleep(1000);
                    }
                }catch(Exception e) {
                    e.printStackTrace();
                }
                if(!isSpeaking){
                    //time out
                    //restartSpeechRecognizer();
                }else {
                    for (int j = 0; j < 3; j++) {
                        try {
                            sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        if (!isSpeaking ||j==2) {
                            //restartSpeechRecognizer();
                            return;
                        }
                    }
                }
            }
        }
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
//        if (timeThread!=null) {
//            timeThread.interrupt();
//            timeThread=null;
//            Log.i(TAG,"timeThread.interrupt()");
//        }
        Intent intent=new Intent();
        intent.setAction("nctu.imf.sirenplayer.MainService");
        intent.setPackage(getPackageName());
        stopService(intent);
        speechRecognizer.destroy();
        timer.cancel();
        windowManager.removeView(imgBtn);
        super.onDestroy();
    }

    private void createBTN() {
        imgBtn = new ImageButton(getApplicationContext());
        imgBtn.setImageResource(R.drawable.number0);
        windowManager = (WindowManager)getApplicationContext().getSystemService(WINDOW_SERVICE);
        wmLayoutParams = new WindowManager.LayoutParams();
        wmLayoutParams.type=WindowManager.LayoutParams.TYPE_PHONE;
        wmLayoutParams.format= PixelFormat.RGBA_8888;
        wmLayoutParams.flags=WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL|WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        wmLayoutParams.gravity= Gravity.LEFT| Gravity.TOP;
        wmLayoutParams.width=WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.height=WindowManager.LayoutParams.WRAP_CONTENT;
        wmLayoutParams.x=xC;
        wmLayoutParams.y=yC;
        windowManager.addView(imgBtn, wmLayoutParams);

        imgBtn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isMoving = true;
                    if(!isFirst){
                        xC = xC + (int) event.getRawX() - xLast;
                        yC = yC + (int) event.getRawY() - yLast;
                        updateView(xC, yC);
                    }
                    xLast = (int) event.getRawX();
                    yLast = (int) event.getRawY();
                    isFirst=false;
                }
                if(event.getAction()==MotionEvent.ACTION_UP){
                    isMoving=false;
                    isFirst =true;
                }
                return false;
            }
        });
        imgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMoving){
                    //do something here
                }
            }
        });
        imgBtn.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (!isMoving) {
                        // 暫時切換至記錄
                    Intent i = new Intent(getApplicationContext(),DBActivity.class);
                    startActivity(i);
                   // MainService.this.onDestroy();
                }
                return true;
            }
        });
    }

    private void updateView(int x,int y) {
        wmLayoutParams.x=x;
        wmLayoutParams.y=y;
        windowManager.updateViewLayout(imgBtn, wmLayoutParams);
    }

    private void restartSpeechRecognizer(){
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
        speechRecognizer.setRecognitionListener(new listener());
        speechRecognizer.startListening(intent);
        needRestart=false;
        isSpeaking=false;
        countDown=5;
        overTime=0;
    }

    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i(TAG,"onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            isSpeaking=true;
            Log.i(TAG,"onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
//            Log.i(TAG,"onRmsChanged");
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.i(TAG,"onBufferReceived");
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(TAG,"onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            needRestart=true;
            isSpeaking=false;
            Log.i(TAG,"onError:"+error);
        }

        @Override
        public void onResults(Bundle results) {
            Log.i(TAG, "onResults");
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String str=(String)data.get(0);
            Toast.makeText(MainService.this,str,Toast.LENGTH_SHORT).show();
            Intent DBintent =new Intent();
            DBintent.setClass(MainService.this, DBActivity.class);
            Bundle bundle =new Bundle();
            bundle.putString("Command",str);
            //bundle.putString("Confirm",confirm);


            Log.i(TAG, str);
            needRestart=true;
            isSpeaking=false;
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.i(TAG,"onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.i(TAG,"onEvent");
        }
    }
}
