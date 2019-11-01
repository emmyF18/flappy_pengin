package com.example.flappypenguin;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
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

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    final private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    final private Integer[] obstacleImagesList = {R.drawable.ice_obstacle, R.drawable.ice_obstacle2, R.drawable.snowman_obstacle};
    final private Integer[] penguinFlapList = {R.drawable.penguin_sprite, R.drawable.penguin_falling};
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
    private ImageButton resumeButton;
    private ImageView pauseMenu;
    private boolean isPaused = false;
    private int penguinFalling = 0;
    private ImageView pauseLogo;
    private ImageButton menuButton;
    private ImageButton restartButton;
    private ImageButton scoresButton;
    private TimerTask timerTask;
    private String highScoresFileName;
    private int highScore;
    private Collision collision;


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

        // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
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

        gameOver = false;
        penguin = findViewById(R.id.penguin);
        penguin.setFactory(new ViewSwitcher.ViewFactory()
        {
            @Override
            public View makeView()
            {
                ImageView imgVw = new ImageView(MainActivity.this);

                imgVw.setImageResource(penguinFlapList[penguinFalling]);
                return imgVw;
            }
        });

        collision = new Collision(obstacleImage, penguin);
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
                displayObstaclesRandomly();

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

        if (!gameOver && canMoveUp)// && !collision.ReportCollision())
        {
            if (penguin.getY() - penguinFlySpeed >= 5)
            {
                penguin.setY(penguin.getY() - penguinFlySpeed);
                penguinFalling = 0;
                penguin.setImageResource(penguinFlapList[penguinFalling]);
            } else
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
                    // SOURCE: https://stackoverflow.com/questions/23357124/android-how-to-pause-and-resume-runnable-thread
                    obstacleImage.removeCallbacks(this);
                } else
                {
                    obstacleImage.setImageResource(obstacleImagesList[randomObstacle]);
                    obstacleImage.postDelayed(this, 8000);
                    randomObstacle = random.nextInt(obstacleImagesList.length);
                    makeObstaclesScroll();
                }
            }
        };

        obstacleImage.postDelayed(runnable, 2000);
    }

    // SOURCE: https://stackoverflow.com/questions/10621439/how-to-animate-scroll-position-how-to-scroll-smoothly
    private void makeObstaclesScroll()
    {
        scrollAnimator = ObjectAnimator.ofInt(obstacleImage, "scrollX", -1500, 1500);
        scrollAnimator.setDuration(8000);

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
        TimerTask timerTask = movePenguinDown();
        Timer penguinDown = new Timer();

        penguinDown.schedule(timerTask, 0, 10);
    }

    private TimerTask movePenguinDown()
    {
        final Handler handler = new Handler();

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
                        if ((penguin.getY() < height - 200) && !gameOver) // && !collision.ReportCollision())
                        {
                            penguin.setY(penguin.getY() + penguinFallSpeed);
                            penguinFalling = 1;
                            penguin.setImageResource(penguinFlapList[penguinFalling]);

                        }
                        else
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
                isPaused = true;

                timerTask.cancel();

                // SOURCE: https://developer.android.com/reference/android/os/Build.VERSION_CODES
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                {
                    // SOURCE: https://developer.android.com/reference/android/animation/Animator
                    scrollAnimator.pause();
                }

                isPaused = true;

                pauseMenu = findViewById(R.id.pause_menu);
                pauseMenu.setVisibility(View.VISIBLE);

                pauseLogo = findViewById(R.id.pause_logo);
                pauseLogo.setVisibility(View.VISIBLE);

                scoresButton = findViewById(R.id.scores_button);
                scoresButton.setVisibility(View.VISIBLE);
                scoresButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        final Intent game = new Intent(MainActivity.this, HighScores.class);
                        startActivity(game);
                    }
                });

                menuButton = findViewById(R.id.home_button);
                menuButton.setVisibility(View.VISIBLE);
                menuButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        goToMenuScreen();
                    }
                });

                restartButton = findViewById(R.id.restart_button);
                restartButton.setVisibility(View.VISIBLE);
                restartButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        // SOURCE: https://stackoverflow.com/questions/1397361/how-do-i-restart-an-android-activity
                        recreate();
                    }
                });

                resumeButton = findViewById(R.id.resume_button);
                resumeButton.setVisibility(View.VISIBLE);
                resumeButton.setOnClickListener(new OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        isPaused = false;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
                        {
                            https://developer.android.com/reference/android/animation/Animator
                            scrollAnimator.resume();
                            displayObstaclesRandomly();
                        }

                        pauseMenu.setVisibility(View.INVISIBLE);
                        resumeButton.setVisibility(View.INVISIBLE);
                        pauseLogo.setVisibility(View.INVISIBLE);
                        restartButton.setVisibility(View.INVISIBLE);
                        menuButton.setVisibility(View.INVISIBLE);
                        scoresButton.setVisibility(View.INVISIBLE);

                        createMoveDownTimer();
                    }
                });
            }
        });
    }

    /*public int getFinalScore()
    {
        return highScore;
    }*/

    /*private void writeHighScore(int finalScore)
    {
        try
        {
            PrintStream output = new PrintStream(this.openFileOutput(highScoresFileName, this.MODE_PRIVATE));
            output.println(finalScore + "");
            output.close();
        }
        catch (IOException e)
        {
            Log.i("highScore", "Write failed");
        }
    }*/

    /*private int readHighScore()
    {
        int currentHighScore = 0;

        try
        {
            Scanner scanner = new Scanner(this.openFileInput(highScoresFileName));

            while (scanner.hasNextInt())
            {
                currentHighScore = scanner.nextInt();
            }

            scanner.close();
        }
        catch (IOException e)
        {
            Log.i("highScore", "Read failed");
        }

        return currentHighScore;
    }*/

    /*private void eraseHighScore()
    {
        new File(this.getFilesDir(), highScoresFileName).delete();
    }*/

    public void goToMenuScreen()
    {
        finish();
    }

}
