package com.example.flappypenguin;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ViewSwitcher;

import androidx.appcompat.app.AppCompatActivity;

import com.daasuu.ei.Ease;
import com.daasuu.ei.EasingInterpolator;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    final private Integer[] countdownImagesList = {R.drawable.countdown_3, R.drawable.countdown_2, R.drawable.countdown_1, R.drawable.countdown_start, R.drawable.blank};
    final private Integer[] obstacleImagesList = {R.drawable.ice_obstacle, R.drawable.ice_obstacle2, R.drawable.snowman_obstacle};
    final Handler handler = new Handler();
    private ImageSwitcher countdownImageSwitcher;
    private ImageButton penguinImage;
    private ImageView imageView;
    private int countdownImagesPosition = 0;
    private int randomTimer;
    private int randomObstacle;
    private ImageView background;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // SOURCE: https://www.tutlane.com/tutorial/android/android-imageswitcher-with-examples
        listenForButton();

        countdownImageSwitcher = findViewById(R.id.countdown);
        countdownImageSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                imageView = new ImageView(MainActivity.this);
                imageView.setImageResource(countdownImagesList[countdownImagesPosition]);

                return imageView;
            }
        });

        countdownImageSwitcher.setInAnimation(this, android.R.anim.fade_in);
        countdownImageSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        startCountdown();

        imageView = findViewById(R.id.obstacles);
        //displayObstaclesRandomly();

        moveUp();
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

    private void listenForButton() {
        penguinImage = findViewById(R.id.penguin);

        penguinImage.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                penguinImage.setY(penguinImage.getY() - 100);
            }

        });
    }

    //click code from https://www.mkyong.com/android/android-imagebutton-example/
    private void moveUp() {
        penguinImage = findViewById(R.id.penguin);

        while (penguinImage.getY() > findViewById(R.id.gameBackground).getBottom()) {
            //bounceUp(penguinImage);
            penguinImage.setY(penguinImage.getY() + 50);
        }
    }

    //animation code from: https://sproutsocial.com/insights/bounce-animation-for-android/
    private void bounceUp(View targetView) {
        ObjectAnimator movePenguinUp = ObjectAnimator.ofFloat(targetView, "translationY", 0, 50, 25);
        movePenguinUp.setInterpolator(new EasingInterpolator(Ease.BOUNCE_IN_OUT));
        movePenguinUp.setStartDelay(500);
        movePenguinUp.setDuration(1500);
        movePenguinUp.start();
    }

    // SOURCE: https://stackoverflow.com/questions/21559405/how-to-display-image-automatically-after-a-random-time
    private void displayObstaclesRandomly() {
        final Random random = new Random();
        randomTimer = random.nextInt(4000 - 3000) + 3000;
        randomObstacle = random.nextInt(obstacleImagesList.length);

        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                imageView.setImageResource(obstacleImagesList[randomObstacle]);
                handler.postDelayed(this, randomTimer);
                randomObstacle = random.nextInt(obstacleImagesList.length);
            }
        };
        handler.postDelayed(runnable, randomTimer);
    }

    //timer code example: https://examples.javacodegeeks.com/android/core/activity/android-timertask-example/
    private void movePenguinDown() {
        TimerTask timerTask = createPenguinTimer();
        Timer penguinDown = new Timer();
        penguinDown.schedule(timerTask,0, 10);
    }
    private TimerTask createPenguinTimer() {
         return new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        penguinImage = findViewById(R.id.penguin);
                        //while(penguinImage.getY() > background.getHeight())
                        //{
                           penguinImage.setY(penguinImage.getY()+1);
                            Log.d("Y Value: ", penguinImage.getY() + "");
                        //}
                        //Toast toast = Toast.makeText(getApplicationContext(), "Y: " +penguinImage.getY(), Toast.LENGTH_SHORT);
                        //toast.show();
                    }
                });
            }
        };

    }

}
