package com.example.flappypenguin;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Collision
{

    private ImageSwitcher penguin;
    private ImageView obstacles ;
    private boolean detectedCollision;
    public Collision( ImageView obstacles, ImageSwitcher penguin)
    {
        createCollisionTimer();
        this.penguin = penguin;
        this.obstacles = obstacles;
    }
    private void createCollisionTimer()
    {
        //TimerTask timerTask = detectCollision();
        //Timer penguinDown = new Timer();
        //penguinDown.schedule(timerTask, 0, 100);
    }
    private TimerTask detectCollision()
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
                        Rect R1 = new Rect();
                        penguin.getHitRect(R1);
                        Rect R2 = new Rect();
                        obstacles.getHitRect(R2);
                        if(Rect.intersects(R1,R2))
                        {
                            Log.e("collision","collision detected");
                            detectedCollision = true;
                        }
                        else
                        {
                            detectedCollision = false;
                        }

                    }
                });
            }
        };
    }
    public boolean ReportCollision()
    {
        return detectedCollision;
    }


}
