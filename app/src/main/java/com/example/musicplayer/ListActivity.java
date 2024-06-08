package com.example.musicplayer;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.provider.MediaStore;
import android.view.View;

import java.util.ArrayList;
import java.util.Objects;

import com.example.musicplayer.databinding.ActivityListBinding;

public class ListActivity extends AppCompatActivity implements ItemClickListener{

    SongAdapter songAdapter;
    ArrayList<SongCard> songList = new ArrayList<>();
    ActivityResultLauncher<Intent> activityResultLauncher ;
    public static MP3PlayerService mediaPlayerService;
    boolean bounded;
    String currentSong = "";
    Storage storage = Storage.Storage();
    ActivityListBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_list);

        Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DISPLAY_NAME},
                MediaStore.Audio.Media.IS_MUSIC + " != 0",
                null,
                null);

        while (true) {
            assert cursor != null;
            if (!cursor.moveToNext()) break;
            SongCard songCard = new SongCard();
            songCard.songName = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            songCard.songName = songCard.songName.substring(0, songCard.songName.length() - 4);
            songList.add(songCard);
        }
        cursor.close();

        /* set songs to recycler */
        RecyclerView songRecycler = binding.songRecycler;
        songRecycler.setLayoutManager(new LinearLayoutManager(this));
        songAdapter = new SongAdapter(this, songList, this);
        songRecycler.setAdapter(songAdapter);

        /* activity results */
        activityResultLauncher = registerForActivityResult (
                new ActivityResultContracts.StartActivityForResult(), result -> {
                    if (result.getResultCode() == Activity.RESULT_OK){
                        Intent data = result.getData();
                        assert data != null;
                        currentSong = data.getStringExtra("currentSong");

                    }
                }
        );
    }

    @Override
    public void onStart(){
        super.onStart();

        /* set background colour */
        ConstraintLayout layout = binding.listLayout;
        layout.setBackgroundColor(Color.parseColor(storage.getBackgroundColour()));
    }

    public void onSettingsClick(View v){
        Intent intent = new Intent (ListActivity.this , SettingsActivity.class) ;
        startActivity(intent);
    }

    @Override
    public void onItemClick(String text){

        /* begin music service if song is new */
        if(!Objects.equals(text, currentSong)){
            Intent intent = new Intent(this, MP3PlayerService.class);
            String path = Environment.getExternalStorageDirectory().toString() + "/Music/" + text + ".mp3";

            intent.putExtra("path", path);
            intent.putExtra("name", text);
            intent.putExtra("playback", storage.getPlaybackSpeed());
            bindService(intent, connection, Context.BIND_AUTO_CREATE);
            startService(intent);
        }

        /* start music player */
        Intent intent = new Intent (ListActivity.this , MusicActivity.class) ;
        activityResultLauncher.launch(intent);

    }

    ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            MP3PlayerService.LocalBinder binder = (MP3PlayerService.LocalBinder) service;
            bounded = true;
            mediaPlayerService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bounded = false;
            mediaPlayerService.onDestroy();
            mediaPlayerService = null;
            finish();
        }
    };
}


