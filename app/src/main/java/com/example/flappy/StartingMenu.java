package com.example.flappy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;

import static android.Manifest.permission.RECORD_AUDIO;


public class StartingMenu extends AppCompatActivity implements OnClickListener{
    private Button playButton;
    private Button optionButton;
    private Button quitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.starting_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (ContextCompat.checkSelfPermission(this, RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{RECORD_AUDIO}, 1);
        }

        playButton = (Button) findViewById(R.id.play);
        optionButton = (Button) findViewById(R.id.option);
        quitButton = (Button) findViewById(R.id.quit);


        playButton.setOnClickListener(this);
        optionButton.setOnClickListener(this);
        quitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.play) {
            openPlay();
        }
        else if(v.getId() == R.id.option){

        }
        else if(v.getId() == R.id.quit){
            quit();
        }
    }

    public void openPlay(){
        Intent playActivity = new Intent(StartingMenu.this, PlayMenu.class);
        startActivity(playActivity);
    }

    public void openOption(){}
    public void quit(){
        finish();
    }

}