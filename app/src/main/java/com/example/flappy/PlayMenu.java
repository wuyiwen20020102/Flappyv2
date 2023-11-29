package com.example.flappy;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import android.view.View.OnClickListener;

public class PlayMenu extends AppCompatActivity implements OnClickListener{
    private Button touchButton;
    private Button voiceButton;
    private int gameState;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.play_menu);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        touchButton = (Button) findViewById(R.id.touch);
        voiceButton = (Button) findViewById(R.id.voice);

        touchButton.setOnClickListener(this);
        voiceButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.touch) {
            gameState = GameProperty.GAME_TOUCH;
            openGame(gameState);
        }
        else if(v.getId() == R.id.voice){
            gameState = GameProperty.GAME_VOICE;
            openGame(gameState);
        }
    }

    public void openGame(int gameState){
        if(gameState == GameProperty.GAME_TOUCH) {
            GameView_Touch gameView = new GameView_Touch(this);
            setContentView(gameView);
        }
        else if(gameState == GameProperty.GAME_VOICE){
            GameView_Voice gameView = new GameView_Voice(this);
            setContentView(gameView);
        }
    }
}
