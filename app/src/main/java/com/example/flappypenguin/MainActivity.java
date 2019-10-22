package com.example.flappypenguin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    final private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    final private Integer[] obstacleImagesList = {R.drawable.ice_obstacle, R.drawable.ice_obstacle2, R.drawable.snowman_obstacle};
    final private Integer[] penguinFlapLists = {R.drawable.penguin_sprite, R.drawable.penguin_falling};
    final Handler handler = new Handler();
    final float penguinFallSpeed = 3.3f;
    final int penguinFlySpeed = 235;
    boolean gameOver = false;
    private ImageSwitcher countdownImageSwitcher;
    private ImageView countdownImage;
    private ImageView obstacleImage;
    private int countdownImagesPosition = 0;
    private int randomObstacle;
    private ImageSwitcher penguin;
    private int height;
    private ObjectAnimator scrollAnimator;
    private boolean canMoveUp = false;
    private ImageButton exitButton;
    private ImageView pauseMenu;
    private TimerTask timerTask;
    private boolean isPaused = false;

    /*
    Todo:Play-test notes:
        when the penguin gets to low on the screen it makes it hard to go back up (fixed)
        Falls a bit to fast (fixed?)
        can move penguin during countdown (fixed)
        obstacles are hard to get over ( especially the snowman)
     */

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listenForTouch();
        displayPauseScreen();
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
        penguin =  findViewById(R.id.penguin);
        penguin.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                ImageView imgVw = new ImageView(MainActivity.this);

                imgVw.setImageResource(R.drawable.penguin_sprite);
                return imgVw;
            }
        });
        penguin.setInAnimation(this, android.R.anim.fade_in);
        penguin.setOutAnimation(this, android.R.anim.fade_out);
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
                canMoveUp = true;
                createMoveDownTimer();

                countdownImageSwitcher.setImageResource(countdownImagesList[countdownImagesList.length - 1]);
                countdownImageSwitcher.setVisibility(View.INVISIBLE);
            }
        }.start();
    }

    //touch stuff from https://developer.android.com/reference/android/view/View.OnTouchListener and https://stackoverflow.com/questions/11690504/how-to-use-view-ontouchlistener-instead-of-onclick
    private void listenForTouch()
    {
        penguin = findViewById(R.id.penguin);
        ConstraintLayout gameScreen = findViewById(R.id.gameScreen);

        gameScreen.setOnTouchListener(handleTouch);
    }

    private View.OnTouchListener handleTouch = new View.OnTouchListener()
    {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent)
        {
            view.performClick();
            moveUp();

            return false;
        }
    };

    private void moveUp()
    {
        penguin = findViewById(R.id.penguin);
        height = findViewById(R.id.gameScreen).getHeight();

        if (!gameOver && canMoveUp)
        {
            if (penguin.getY() - penguinFlySpeed >= 5)
            {
                penguin.setY(penguin.getY() - penguinFlySpeed);
                Log.i("penguinY", penguin.getY() + "");
                penguin.setImageResource(R.drawable.countdown_1);
            }
            else
            {
                penguin.setY(5);
                gameOver();
            }
        }
    }

    //alert code: https://www.geeksforgeeks.org/android-alert-dialog-box-and-how-to-create-it/
    private void gameOver()
    {
        //todo:figure out why alert is causing lag
        gameOver = true;
        penguin.setImageResource(R.drawable.penguin_sprite);
        goToMenuScreen();
    }

    // SOURCE: https://stackoverflow.com/questions/21559405/how-to-display-image-automatically-after-a-random-time
    private void displayObstaclesRandomly()
    {
        final Random random = new Random();
        randomObstacle = random.nextInt(obstacleImagesList.length);
        obstacleImage = findViewById(R.id.obstacles);

        final Runnable runnable = new Runnable()
        {
            @Override
            public void run()
            {
                if (isPaused)
                {
                    obstacleImage.removeCallbacks(this);
                }
                else
                {
                    obstacleImage.setImageResource(obstacleImagesList[randomObstacle]);
                    obstacleImage.postDelayed(this, 7000);
                    makeObstaclesScroll();
                    randomObstacle = random.nextInt(obstacleImagesList.length);
                }
            }
        };
        obstacleImage.postDelayed(runnable, 7000);
    }

    // SOURCE: https://stackoverflow.com/questions/10621439/how-to-animate-scroll-position-how-to-scroll-smoothly
    private void makeObstaclesScroll()
    {
        scrollAnimator = ObjectAnimator.ofInt(obstacleImage, "scrollX", -1500, 1500);
        scrollAnimator.setDuration(7000);

        scrollAnimator.addListener(new Animator.AnimatorListener()
        {
            @Override
            public void onAnimationStart(Animator animator)
            {

            }

            @Override
            public void onAnimationEnd(Animator animator)
            {

            }

            @Override
            public void onAnimationCancel(Animator animator)
            {

            }

            @Override
            public void onAnimationRepeat(Animator animator)
            {

            }
        });

        scrollAnimator.start();
    }

    //timer code example: https://examples.javacodegeeks.com/android/core/activity/android-timertask-example/
    private void createMoveDownTimer()
    {
        timerTask = movePenguinDown();
        Timer penguinDown = new Timer();

        penguinDown.schedule(timerTask, 0, 10);
    }

    private TimerTask movePenguinDown()
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
                        penguin = findViewById(R.id.penguin);
                        height = findViewById(R.id.gameScreen).getHeight();

                        if ((penguin.getY() < height - 200) && !gameOver)
                        {
                            penguin.setY(penguin.getY() + penguinFallSpeed);
                            penguin.setImageResource(R.drawable.penguin_sprite);

                        } else
                        {
                            gameOver();
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
                timerTask.cancel();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    scrollAnimator.pause();
                    isPaused = true;
                }

                pauseMenu = findViewById(R.id.pause_menu);
                pauseMenu.setVisibility(View.VISIBLE);

                exitButton = findViewById(R.id.exit_button);
                exitButton.setVisibility(View.VISIBLE);
                exitButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        pauseMenu.setVisibility(View.INVISIBLE);
                        exitButton.setVisibility(View.INVISIBLE);

                        createMoveDownTimer();

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {
                            scrollAnimator.resume();
                            isPaused = false;
                            displayObstaclesRandomly();
                        }
                    }
                });
            }
        });
    }

    private void goToMenuScreen()
    {
        finish();
    }
}
