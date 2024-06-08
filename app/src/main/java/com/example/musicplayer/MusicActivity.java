package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.example.musicplayer.databinding.ActivityMusicBinding;

public class MusicActivity extends AppCompatActivity {

    MP3PlayerService musicPlayer = ListActivity.mediaPlayerService;
    ActivityMusicBinding binding;
    Storage storage = Storage.Storage();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_music);

        Toolbar settingsToolbar = binding.toolbar.toolbar;
        settingsToolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent();
            intent.putExtra("currentSong", musicPlayer.getSongName());
            setResult(Activity.RESULT_OK, intent) ;
            finish();
        });
    }

    @Override
    public void onStart(){
        super.onStart();

        /* set background colour */
        ConstraintLayout layout = binding.musicLayout;
        layout.setBackgroundColor(Color.parseColor(storage.getBackgroundColour()));

        /* set playback speed text */
        TextView playbackSpeed = binding.playbackSpeed;
        playbackSpeed.setText(String.format("%sx", storage.getPlaybackSpeed()));

        /* display song name */
        TextView songName = binding.songName;
        songName.setText(musicPlayer.getSongName());

        /* display progress */
        setProgressBar(binding.progressBar);
    }

    /* Play/Pause the song */
    public void onPlay(View v){
        if(musicPlayer.status() == MP3Player.MP3PlayerState.PAUSED){ // play
            musicPlayer.playSong();
        }
    }

    public void onPause(View v){
        if(musicPlayer.status() == MP3Player.MP3PlayerState.PLAYING){ // pause
            musicPlayer.pauseSong();
        }
    }

    /* Stop song */
    public void onStop(View v){
        musicPlayer.stopSong();
        musicPlayer.stopService(new Intent(this, MP3PlayerService.class));
        Intent intent = new Intent();
        intent.putExtra("currentSong", "");
        setResult(Activity.RESULT_OK, intent) ;
        finish();
    }

    /* Display song duration */
    private void setProgressBar(ProgressBar progressBar) {
        Handler handler = new Handler(Looper.getMainLooper());

        progressBar.setMax(musicPlayer.getDuration());
        progressBar.setProgress(musicPlayer.getCurrentPosition());

        new Thread(new Runnable(){
            @Override
            public void run() {
                if (musicPlayer.status() != MP3Player.MP3PlayerState.STOPPED){

                    progressBar.setMax(musicPlayer.getDuration());
                    progressBar.setProgress(musicPlayer.getCurrentPosition());

                    handler.postDelayed(this, 1000);
                }
            }
        }).start();
    }

}
