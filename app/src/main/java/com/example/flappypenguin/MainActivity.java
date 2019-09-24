package com.example.flappypenguin;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.DialogInterface;
import android.os.Bundle;
import android.content.Intent;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;
import android.view.View.OnClickListener;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

public class MainActivity extends AppCompatActivity
{
    private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    private ImageSwitcher countdownImageSwitcher;
    private ImageButton penguinImage;
    private int countdownImagesPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
        listenForButton();
        countdownImageSwitcher = findViewById(R.id.countdown);
        countdownImageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                ImageView imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(countdownImagesList[countdownImagesPosition]);

                return imageView;
            }
        });

        countdownImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        countdownImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        startCountdown();

        moveDown();
    }

    // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
    // SOURCE: https://abhiandroid.com/ui/countdown-timer
    private void startCountdown()
    {
        new CountDownTimer(4000, 1000)
        {
            @Override
            public void onTick(long millisUntilFinished)
            {
                countdownImageSwitcher.setImageResource(countdownImagesList[countdownImagesPosition]);

                if (countdownImagesPosition < countdownImagesList.length)
                {
                    countdownImagesPosition++;
                }
            }

            @Override
            public void onFinish()
            {
                countdownImageSwitcher.setImageResource(countdownImagesList[countdownImagesList.length - 1]);
            }
        }.start();
    }

    private void listenForButton()
    {
        penguinImage = findViewById(R.id.penguin);

        penguinImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                penguinImage.setY(penguinImage.getY() - 50);
            }

        });
    }

    //click code from https://www.mkyong.com/android/android-imagebutton-example/
    private void moveDown()
    {
        penguinImage = findViewById(R.id.penguin);

        while(penguinImage.getY() > findViewById(R.id.gameBackground).getBottom())
        {
            bounceUp(penguinImage);
           // imageButton.setY(imageButton.getY() + 50);
        }
    }

    private void bounceUp(View targetView)
    {
        ObjectAnimator movePenguinUp = ObjectAnimator.ofFloat(targetView,"translationY",0, 50, 25);
        movePenguinUp.setInterpolator(new EasingInterpolator(Ease.BOUNCE_IN_OUT));
        movePenguinUp.setStartDelay(500);
        movePenguinUp.setDuration(1500);
        movePenguinUp.start();
    }
}
