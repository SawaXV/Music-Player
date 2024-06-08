package com.example.musicplayer;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class MP3PlayerService extends Service {
    private final MP3Player mp3Player = new MP3Player();
    private String songName = "?";
    private final IBinder binder = new LocalBinder();
    private static final String CHANNEL_ID = "SongChannel";
    private static final int NOTIFICATION_ID = 1;

    /* Bind methods to get instance */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        MP3PlayerService getService() {
            return MP3PlayerService.this;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String path = intent.getStringExtra("path");
        String newSong = intent.getStringExtra("name");
        int pbs = intent.getIntExtra("playback", 1);

        /* if any existing song is playing */
            songName = newSong;
            mp3Player.stop();
            mp3Player.load(path, pbs);

            createNotificationChannel();
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("Music Player")
                    .setContentText("Currently playing: " + songName)
                    .setSmallIcon(R.drawable.headphones)
                    .build();
            startForeground(NOTIFICATION_ID, notification);

            return START_NOT_STICKY;


    }

    /* Return whether song is playing or paused */
    public MP3Player.MP3PlayerState status(){
        return mp3Player.getState();
    }

    /* Play/Pause/Stop song service */
    public void playSong(){
            mp3Player.play();

    }

    /* Pause song */
    public void pauseSong(){
            mp3Player.pause();
    }

    /* Stop song */
    public void stopSong(){
        mp3Player.stop();
    }

    /* Set playback speed */
    public void setPlaybackSpeed(int val){
        mp3Player.setPlaybackSpeed(val);
    }

    /* Get the total length of the song */
    public int getDuration(){
        return mp3Player.getDuration();
    }

    /* Get the current song timestamp */
    public int getCurrentPosition(){
        return mp3Player.getProgress();
    }

    /* Get song name */
    public String getSongName(){
        return songName;
    }

    /* Create notification for current song */
    private void createNotificationChannel(){
        CharSequence name = "Music Service";
        String description = "Track that a song is currently playing";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    @Override
    public void onDestroy(){
        mp3Player.stop();
        super.onDestroy();
    }
}