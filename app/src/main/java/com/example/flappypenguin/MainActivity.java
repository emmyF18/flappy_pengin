package com.example.flappypenguin;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    final private Integer[] obstacleImagesList = {R.drawable.ice_obstacle, R.drawable.ice_obstacle2, R.drawable.snowman_obstacle};
    final private Integer[] penguinFlapLists = {R.drawable.penguin_sprite, R.drawable.snowman_obstacle};//TODO:change to flap penguin
    final Handler handler = new Handler();
    final int penguinFallSpeed = 4;
    final int penguinFlySpeed = 200;
    boolean gameOver = false;
    private ImageSwitcher countdownImageSwitcher;
    private ImageButton penguinImage;
    private ImageView countdownImage;
    private ImageView obstacleImage;
    private int countdownImagesPosition = 0;
    private int randomObstacle;
    private ImageSwitcher penguinSwitcher;
    private int height;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenForButton();
        countdownImageSwitcher = findViewById(R.id.countdown);
        countdownImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                countdownImage = new ImageView(MainActivity.this);
                countdownImage.setImageResource(countdownImagesList[countdownImagesPosition]);
                return countdownImage;
            }
        });
        countdownImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        countdownImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        startCountdown();

        obstacleImage = findViewById(R.id.obstacles);
        displayObstaclesRandomly();
    }

    // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
    // SOURCE: https://abhiandroid.com/ui/countdown-timer
    private void startCountdown() {
        new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                countdownImageSwitcher.setImageResource(countdownImagesList[countdownImagesPosition]);

                if (countdownImagesPosition < countdownImagesList.length) {
                    countdownImagesPosition++;
                }
            }

            @Override
            public void onFinish() {
                countdownImageSwitcher.setImageResource(countdownImagesList[countdownImagesList.length - 1]);
                movePenguinDown();
            }
        }.start();
    }

    //click code from https://www.mkyong.com/android/android-imagebutton-example/
    private void listenForButton() {
        penguinImage = findViewById(R.id.penguin);

        penguinImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                moveUp();
            }
        });
    }

    private void moveUp()
    {
        penguinImage = findViewById(R.id.penguin);
        height = findViewById(R.id.gameScreen).getHeight();
        if((penguinImage.getY() >= 0)&& !gameOver)
        {
            penguinImage.setY(penguinImage.getY() - penguinFlySpeed);
            Log.d("Y Value Up ", penguinImage.getY() + " Height: "+ height);
        }
        else
        {
            gameOver = true;
        }

    }

    // SOURCE: https://stackoverflow.com/questions/21559405/how-to-display-image-automatically-after-a-random-time
    private void displayObstaclesRandomly()
    {
        final Random random = new Random();
        randomObstacle = random.nextInt(obstacleImagesList.length);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                makeObstaclesScroll();
                obstacleImage.setImageResource(obstacleImagesList[randomObstacle]);
                handler.postDelayed(this, 5000);
                randomObstacle = random.nextInt(obstacleImagesList.length);
            }
        };
        handler.postDelayed(runnable, 5000);
    }

    // SOURCE: https://stackoverflow.com/questions/18352253/android-autoscroll-imageview
    private void makeObstaclesScroll()
    {
        Animation translateAnimation = new TranslateAnimation(TranslateAnimation.ABSOLUTE, 1500, TranslateAnimation.ABSOLUTE, -1500, TranslateAnimation.ABSOLUTE, 0f, TranslateAnimation.ABSOLUTE, 0f);
        translateAnimation.setDuration(5000);
        translateAnimation.setRepeatCount(-1);
        translateAnimation.setInterpolator(new LinearInterpolator());

        // obstacleImage.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        obstacleImage.startAnimation(translateAnimation);
        // obstacleImage.setLayerType(View.LAYER_TYPE_NONE, null);
    } // TODO: Fix lag

    //timer code example: https://examples.javacodegeeks.com/android/core/activity/android-timertask-example/
    private void movePenguinDown()
    {
        TimerTask timerTask = createPenguinTimer();
        Timer penguinDown = new Timer();
        penguinDown.schedule(timerTask,0, 10);
    }

    private TimerTask createPenguinTimer()
    {
         return new TimerTask()
         {
            @Override
            public void run()
            {
                handler.post(new Runnable()
                {
                    public void run()
                    {
                        penguinImage = findViewById(R.id.penguin);
                        height = findViewById(R.id.gameScreen).getHeight();
                        if((penguinImage.getY() < height-300) && !gameOver) //TODO: figure out why this is 300 off
                        {
                           penguinImage.setY(penguinImage.getY()+penguinFallSpeed);
                           //Log.d("Y Value Down ", penguinImage.getY() + " Height: "+ height);
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Game Over!", Toast.LENGTH_LONG).show();
                            gameOver = true;
                        }

                    }
                });
            }
        };

    }
}
