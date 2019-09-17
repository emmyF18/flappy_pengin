package com.example.flappypenguin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

public class MainActivity extends AppCompatActivity
{
    private Integer[] countdownImages = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start};
    private ImageSwitcher imageSwitcher;
    private int position = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageSwitcher = findViewById(R.id.countdown);
        imageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(countdownImages[position]);

                return imageView;
            }
        });
        imageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        imageSwitcher.setOutAnimation(this, android.R.anim.fade_out);

        // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples

        startCountdown();
    }

    private void startCountdown()
    {
        new CountDownTimer(3000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                imageSwitcher.setImageResource(countdownImages[position]);

                if (position < countdownImages.length)
                {
                    position++;
                }

                // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
            }

            @Override
            public void onFinish()
            {
                imageSwitcher.setImageResource(countdownImages[countdownImages.length - 1]);
            }
        }.start();
    }
    // SOURCE: https://abhiandroid.com/ui/countdown-timer
}
