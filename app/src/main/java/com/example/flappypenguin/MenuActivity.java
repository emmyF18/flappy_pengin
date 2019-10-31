package com.example.flappypenguin;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;

public class MenuActivity extends AppCompatActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void goToGameScreen(View view)
    {
        final Intent game = new Intent(MenuActivity.this, MainActivity.class);
        startActivity(game);
    }

    public void goToScoresScreen(View view)
    {

    }
}
