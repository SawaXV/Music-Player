package com.example.musicplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.databinding.DataBindingUtil;

import com.example.musicplayer.databinding.ActivitySettingsBinding;

import java.util.Arrays;

public class SettingsActivity extends AppCompatActivity {

    MP3PlayerService musicPlayer = ListActivity.mediaPlayerService;
    Storage storage = Storage.Storage();

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this,R.layout.activity_settings);

        ConstraintLayout layout = binding.settingsLayout;
        layout.setBackgroundColor(Color.parseColor(storage.getBackgroundColour()));

        /* set background colours */
        Spinner bgSpinner = binding.bgSpinner;
        String[] bgs = getResources().getStringArray(R.array.colours);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, bgs);
        bgSpinner.setAdapter(adapter);


        bgSpinner.setSelection(Arrays.asList(getResources().getStringArray(R.array.colours)).indexOf(storage.getBackgroundColour()));
        binding.pbsInput.setText(Integer.toString(storage.getPlaybackSpeed()));

        Toolbar settingsToolbar = binding.toolbar.toolbar;
        settingsToolbar.setNavigationOnClickListener(v -> finish());
    }

    public void onSave(View v){
        Spinner bgSpinner = binding.bgSpinner;
        TextView pbsInput = binding.pbsInput;

        if(pbsInput.getText().toString().equals("") || pbsInput.getText().toString().equals("0")){
            pbsInput.setText("1");
        }

        String selectedBg = bgSpinner.getSelectedItem().toString();
        int selectedSpeed = Integer.parseInt(pbsInput.getText().toString());

        /* prevent speed <= 0 */
        if(selectedSpeed <= 0 ){
            selectedSpeed = 1;
        }

        /* set values */
        storage.setBackgroundColour(selectedBg);
        storage.setPlaybackSpeed(selectedSpeed);

        ConstraintLayout layout = binding.settingsLayout;
        layout.setBackgroundColor(Color.parseColor(storage.getBackgroundColour()));

        /* account for music player playing */
        if(musicPlayer != null){
            if(musicPlayer.status() == MP3Player.MP3PlayerState.PAUSED){
                musicPlayer.setPlaybackSpeed(selectedSpeed);
                musicPlayer.pauseSong();
            }
            else{
                musicPlayer.setPlaybackSpeed(selectedSpeed);
            }
        }
    }
}