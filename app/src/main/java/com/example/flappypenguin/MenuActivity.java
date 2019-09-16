package com.example.flappypenguin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    private void goToGameScreen()
    {
        final Intent game = new Intent(this, MainActivity.class);
    }
}
