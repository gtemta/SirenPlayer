package nctu.imf.sirenplayer;

import android.app.Service;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Jason on 2015/10/1.
 */
public class MainService extends Service{
    private static final String TAG="MainService";
    private SpeechRecognizer speechRecognizer;
    private Intent intent;
    private listener myListener;
    private DBcontact commandword;
    private DbDAO dbDAO;
    private static boolean serviceIsOn=false;


    @Override
    public void onCreate() {
        super.onCreate();
        serviceIsOn=true;
        myListener=new listener();
        intent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, this.getPackageName());
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainService.this);
        speechRecognizer.setRecognitionListener(myListener);
        speechRecognizer.startListening(intent);
        dbDAO= new DbDAO(getApplicationContext());

    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
        speechRecognizer=null;
        stopSelf();
        serviceIsOn=false;
        super.onDestroy();
    }

    private void restartSpeechRecognizer() {
        Log.d(TAG, "restartSpeechRecognizer");
        if (speechRecognizer==null)return;
        speechRecognizer.stopListening();
        speechRecognizer.cancel();
        speechRecognizer.destroy();
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(MainService.this);
        speechRecognizer.setRecognitionListener(new listener());
        speechRecognizer.startListening(intent);
    }

    class listener implements RecognitionListener {
        @Override
        public void onReadyForSpeech(Bundle params) {
            Log.i(TAG,"onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.i(TAG,"onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
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
            Log.i(TAG,"onError:"+error);
            switch (error){
                case 1:
                    Log.d(TAG,"Network operation timed out.");
                    break;
                case 2:
                    Log.d(TAG,"Other network related errors.");
                    break;
                case 3:
                    Log.d(TAG,"Audio recording error.");
                    break;
                case 4:
                    Log.d(TAG,"Server sends error status.");
                    break;
                case 5:
                    Log.d(TAG,"Other client side errors.");
                    break;
                case 6:
                    Log.d(TAG, "No speech input.");
                    break;
                case 7:
                    Log.d(TAG, "No recognition result matched.");
                    break;
                case 8:
                    Log.d(TAG,"RecognitionService busy.");
                    break;
                case 9:
                    Log.d(TAG,"Insufficient permissions");
                    break;
            }
            restartSpeechRecognizer();

        }

        @Override
        public void onResults(Bundle results) {
            Log.i(TAG,"onResults:");
            ArrayList data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            String str= (String) data.get(0);
            Log.d(TAG,str);
            sendResult(str);
            Log.d(TAG, "words:" +str);
            commandword = new DBcontact(0,str,dbDAO.dBcontact.getLocaleDatetime());
            dbDAO.winsert(commandword);
            Log.i(TAG, "What:"+str);
            if (str.toUpperCase()=="KILL"){
                MainService.this.stopSelf();
            }
            restartSpeechRecognizer();
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.i(TAG,"onPartialResults");
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.i(TAG, "onEvent");
        }
    }

    private void sendResult(String str) {
        Intent intent = new Intent("my-event");
        intent.putExtra("result", str);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    public static boolean getServiceIsOn(){
        return serviceIsOn;
    }
}
