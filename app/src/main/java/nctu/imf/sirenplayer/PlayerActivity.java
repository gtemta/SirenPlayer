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
    private String TAG="PlayerActivity";

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
        audioManager.getMode();

        setContentView(R.layout.activity_player);

        playerView= new YouTubePlayerView(PlayerActivity.this);
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
                case "全螢幕":
                    player.setFullscreen(true);
                    break;
                case "大聲":
                    audioManager.getMode();
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    audioManager.adjustVolume(AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    break;
                case "小聲":
                    audioManager.getMode();
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    audioManager.adjustVolume(AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    break;
                case "靜音":
                    audioManager.getMode();
                    audioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
                    break;
                case "閉嘴":
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
        Log.d(TAG,"onInitializationSuccess");
        player=youtubeplayer;
        player.setPlayerStyle(YouTubePlayer.PlayerStyle.MINIMAL);
        player.setPlayerStateChangeListener(playerStateChangeListener);
        player.setPlaybackEventListener(playbackEventListener);
        if(!restored){
            player.loadVideo("QDg4a2azcAg");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult");
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent pIntent=new Intent();
        pIntent.setAction("nctu.imf.sirenplayer.MainService");
        pIntent.setPackage(getPackageName());
        stopService(pIntent);
        PlayerActivity.this.finish();
    }

    private YouTubePlayer.PlaybackEventListener playbackEventListener = new YouTubePlayer.PlaybackEventListener() {

        @Override
        public void onBuffering(boolean arg0) {
            Log.d(TAG,"onBuffering");
        }

        @Override
        public void onPaused() {
            Log.d(TAG,"onPaused");
        }

        @Override
        public void onPlaying() {
            Log.d(TAG,"onPlaying");
        }

        @Override
        public void onSeekTo(int arg0) {
            Log.d(TAG,"onSeekTo");
        }

        @Override
        public void onStopped() {
            Log.d(TAG,"onStopped");
        }

    };

    private YouTubePlayer.OnFullscreenListener FullscreenListener = new YouTubePlayer.OnFullscreenListener() {
        @Override
        public void onFullscreen(boolean var1) {
            Log.d(TAG,"onFullscreen");
        }
    };

    private YouTubePlayer.PlayerStateChangeListener playerStateChangeListener = new YouTubePlayer.PlayerStateChangeListener() {

        @Override
        public void onAdStarted() {
            Log.d(TAG,"onAdStarted");
        }

        @Override
        public void onError(YouTubePlayer.ErrorReason arg0) {
            Log.d(TAG,"onError");
        }

        @Override
        public void onLoaded(String arg0) {
            Log.d(TAG,"onLoaded");
        }

        @Override
        public void onLoading() {
            Log.d(TAG,"onLoading");
        }

        @Override
        public void onVideoEnded() {
            Log.d(TAG,"onVideoEnded");
        }

        @Override
        public void onVideoStarted() {
            Log.d(TAG,"onVideoStarted");
        }
    };
}