package com.example.flappypenguin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

=======

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    final private Integer[] obstacleImagesList = {R.drawable.ice_obstacle, R.drawable.ice_obstacle2, R.drawable.snowman_obstacle};
    final private Integer[] penguinFlapLists = {R.drawable.penguin_sprite, R.drawable.snowman_obstacle};//TODO:change to flap penguin
    final Handler handler = new Handler();
    final float penguinFallSpeed = 3.2f;
    final int penguinFlySpeed = 250;
    boolean gameOver = false;
    private ImageSwitcher countdownImageSwitcher;
    private ImageButton penguinImage;
    private ImageView countdownImage;
    private ImageView obstacleImage;
    private int countdownImagesPosition = 0;
    private int randomObstacle;
    private ImageSwitcher penguinSwitcher;
    private int height;
    private ObjectAnimator scrollAnimator;
    private boolean pause;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listenForButton();

        countdownImageSwitcher = findViewById(R.id.countdown);
        countdownImageSwitcher.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
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

        displayPauseScreen();

        gameOver = false;
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
                movePenguinDown();
                countdownImageSwitcher.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    //click code from https://www.mkyong.com/android/android-imagebutton-example/
    private void listenForButton()
    {
        penguinImage = findViewById(R.id.penguin);

        penguinImage.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                moveUp();
            }
        });
    }

    private void moveUp()
    {
        penguinImage = findViewById(R.id.penguin);
        height = findViewById(R.id.gameScreen).getHeight();
        if(!gameOver)
        {
            Log.d("Y Value Up ", (penguinImage.getY()-penguinFlySpeed >= 10)+"");

            if(penguinImage.getY()-penguinFlySpeed >= 10) {
                penguinImage.setY(penguinImage.getY() - penguinFlySpeed);

            }
            else
            {
                //penguinImage.getY()-10;
                gameOver = true;
            }
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
            public void run()
            {
                obstacleImage.setImageResource(obstacleImagesList[randomObstacle]);
                handler.postDelayed(this, 7000);
                makeObstaclesScroll();
                randomObstacle = random.nextInt(obstacleImagesList.length);
            }
        };
        handler.postDelayed(runnable, 7000);
    }

    // SOURCE: https://stackoverflow.com/questions/10621439/how-to-animate-scroll-position-how-to-scroll-smoothly
    private void makeObstaclesScroll()
    {
        scrollAnimator = ObjectAnimator.ofInt(obstacleImage, "scrollX", -1500, 1500);
        scrollAnimator.setDuration(7000);

        scrollAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });

        scrollAnimator.start();
    }

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
                            gameOver = true;
                        }

                    }
                });
            }
        };

    }

    // SOURCE: https://stackoverflow.com/questions/26294781/display-image-after-button-click-in-android
    private void displayPauseScreen()
    {
        ImageButton pauseButton = findViewById(R.id.pause_button);

        pauseButton.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    scrollAnimator.pause();
                }

                ImageView pauseMenu = findViewById(R.id.pause_menu);
                pauseMenu.setVisibility(View.VISIBLE);
            }
        });
    }
}
