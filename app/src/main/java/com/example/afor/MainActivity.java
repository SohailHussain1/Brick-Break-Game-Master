package com.example.afor;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private BrickBreakerGameView gameView;
    private Handler handler;

    private final int FRAME_RATE = 30; // Adjust the frame rate as desired

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gameView = new BrickBreakerGameView(this, null);
        setContentView(gameView);

        handler = new Handler();
        // Start the game loop
        gameLoop.run();


    }

    private Runnable gameLoop = new Runnable() {
        @Override
        public void run() {
            gameView.updateGame();
            handler.postDelayed(this, 1000 / FRAME_RATE);
        }
    };

}