package nctu.imf.sirenplayer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;


/**
 * Created by Jason on 2015/10/2.
 */
public class PlayerActivity extends YouTubeBaseActivity
        implements YouTubePlayer.OnInitializedListener {

    public YouTubePlayerView playerView;
    public YouTubePlayer player;
    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private AudioManager audioManager;


    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);

        setContentView(R.layout.activity_player);

        playerView = (YouTubePlayerView)findViewById(R.id.youtube_view);
        playerView.initialize(DeveloperKey.DEVELOPER_KEY, PlayerActivity.this);
    }

    @Override
    public void onResume() {
        super.onResume();

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter("my-event"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String result = intent.getStringExtra("result");
            Log.d("receiver", "Got message: " + result);
            Toast.makeText(PlayerActivity.this,result.toUpperCase(),Toast.LENGTH_SHORT).show();
            switch (result.toUpperCase()){
                case "播放":
                    player.play();
                    break;
                case "暫停":
                    player.pause();
                    break;
                case "聲音變大":
                    audioManager.getMode();
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
                    break;
                case "聲音變小":
                    audioManager.getMode();
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
                    break;
                case "震動":
                    audioManager.getMode();
                    audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                    break;
                case "關閉":
                    Intent pIntent=new Intent();
                    pIntent.setAction("nctu.imf.sirenplayer.MainService");
                    pIntent.setPackage(getPackageName());
                    stopService(pIntent);
                    PlayerActivity.this.finish();
                    break;
            }
        }
    };


    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                                        YouTubeInitializationResult errorReason) {
        if (errorReason.isUserRecoverableError()) {
            errorReason.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage = String.format("There was an error initializing the YouTubePlayer", errorReason.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider, YouTubePlayer youtubeplayer,
                                        boolean restored) {
        player=youtubeplayer;
        if(!restored){
            player.loadVideo("QDg4a2azcAg");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}