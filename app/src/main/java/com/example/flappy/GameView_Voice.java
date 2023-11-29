package com.example.flappy;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import androidx.annotation.NonNull;


import java.util.Random;
import java.util.Vector;


public class GameView_Voice extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private AudioRecorder audioRecorder;
    private SurfaceHolder holder;
    private Resources resources;
    private Thread th;
    private boolean flag;
    private Canvas canvas;
    private Paint paint;
    private Bitmap bg;
    private Rect bgRect;
    private Bird bird;
    private Vector<Barrier> barriers;
    private static int GameState = GameProperty.GAME_ING;
    private int score;
    private int lastSpeedIncreaseScore = 0;
    private int volumePitch;
    private long lastVolumeCheckTime = 0;


    public GameView_Voice(Context context) {
        super(context);
        holder = getHolder();
        resources = getResources();
        holder.addCallback(this);
        setFocusable(true);
        volumePitch = 0;
        audioRecorder = new AudioRecorder();
        audioRecorder.getNoiseLevel();
    }




    //start when created
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {
        initGame(); //initialize game
    }


    public void initGame() {
        bg = BitmapFactory.decodeResource(resources, GameProperty.GAME_BG); //initialize the background
        th = new Thread(this); //initialize thread
        bgRect = new Rect(0, 0, getWidth(), getHeight());
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(100);
        initBird();//initialize the bird
        initBarrier();
        score = 0;
        flag = true;
        th.start();
    }


    private void initBird() {
        Bitmap hutaoDown = BitmapFactory.decodeResource(resources, GameProperty.HUTAO_DOWN);
        Bitmap hutaoNormal = BitmapFactory.decodeResource(resources, GameProperty.HUTAO_NORMAL);
        Bitmap hutaoUp = BitmapFactory.decodeResource(resources, GameProperty.HUTAO_UP);
        Bitmap[] bitmaps = new Bitmap[]{hutaoDown, hutaoNormal, hutaoUp};
        bird = new Bird(bitmaps, getWidth(), getHeight());
    }


    private void initBarrier() {
        barriers = new Vector<>();
        for (int i = 0; i < 4; i++) {
            Random random = new Random();
            Bitmap barrier01 = BitmapFactory.decodeResource(resources, GameProperty.BARRIER_UP);
            Bitmap barrier02 = BitmapFactory.decodeResource(resources, GameProperty.BARRIER_DOWN);
            int ratio = random.nextInt(4) + 2;
            int k = random.nextInt(getHeight() / 8) + getHeight() / ratio;
            int gap = random.nextInt(300) - 150;
            Barrier barrierUp = new Barrier(barrier01, getWidth() / 2 + getWidth() / 2 * i + 1000, k + gap + 500, getWidth(), getWidth(), GameProperty.BARRIER_NORMAL);
            Barrier barrierDown = new Barrier(barrier02, getWidth() / 2 + getWidth() / 2 * i + 1000, k + gap - 500, getWidth(), getWidth(), GameProperty.BARRIER_NORMAL);
            barriers.add(barrierUp);
            barriers.add(barrierDown);
            for (int j = 1; j < 23; j++) {
                Bitmap barrierZ = BitmapFactory.decodeResource(resources, GameProperty.BARRIER);
                Barrier barrierup = new Barrier(barrierZ, getWidth() / 2 + getWidth() / 2 * i + 999, k + gap - 500 - barrierZ.getHeight() * j  , getWidth(), getWidth(), GameProperty.BARRIER_SPECIAL);
                Barrier barrierdown = new Barrier(barrierZ, getWidth()  / 2 + getWidth() / 2 * i + 1000, k + gap + 500 + barrierZ.getHeight() * j, getWidth(), getWidth(), GameProperty.BARRIER_SPECIAL);
                barriers.add(barrierup);
                barriers.add(barrierdown);
            }
        }
    }


    private void addBarrier(){
        Random random = new Random();
        Bitmap barrier01 = BitmapFactory.decodeResource(resources, GameProperty.BARRIER_UP);
        Bitmap barrier02 = BitmapFactory.decodeResource(resources, GameProperty.BARRIER_DOWN);
        int ratio = random.nextInt(4) + 2;
        int k = random.nextInt(getHeight() / 8) + getHeight() / ratio;
        int gap = random.nextInt(300) - 150;
        int nextBarrierXPosition;
        if (barriers.size() > 0) {
            Barrier lastBarrier = barriers.lastElement();
            nextBarrierXPosition = getWidth() / 4 + lastBarrier.getX();
        } else {
            nextBarrierXPosition = 0; // Position of the first barrier
        }


        Barrier barrierUp = new Barrier(barrier01, getWidth() / 4 + nextBarrierXPosition, k + gap + 500, getWidth(), getWidth(), GameProperty.BARRIER_NORMAL);
        Barrier barrierDown = new Barrier(barrier02, getWidth() / 4 + nextBarrierXPosition, k + gap - 500, getWidth(), getWidth(), GameProperty.BARRIER_NORMAL);
        barriers.add(barrierUp);
        barriers.add(barrierDown);
        for (int j = 1; j < 23; j++) {
            Bitmap barrierZ = BitmapFactory.decodeResource(resources, GameProperty.BARRIER);
            Barrier barrierup = new Barrier(barrierZ, getWidth() / 4 + nextBarrierXPosition - 1, k + gap - 500 - barrierZ.getHeight() * j, getWidth(), getWidth(), GameProperty.BARRIER_SPECIAL);
            Barrier barrierdown = new Barrier(barrierZ, getWidth()  / 4 + nextBarrierXPosition, k + gap + 500 + barrierZ.getHeight() * j, getWidth(), getWidth(), GameProperty.BARRIER_SPECIAL);
            barriers.add(barrierup);
            barriers.add(barrierdown);
        }
    }


    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {


    }


    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {


    }


    @Override
    public void run() {
        while (flag) {
            volumePitch = audioRecorder.getMvolume(); //Update user voice decibel
            Log.d("Volume", "Volume" + volumePitch);


            myDraw();//Draw function
            logic();//Draw logic
            voiceControl(volumePitch);


            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }


    public void logic() {
        bird.logic();
        for (int i = 0; i < barriers.size(); i++) { //the barrier will always move to left, so it looks like the bird is moving to right
            Barrier barrier = barriers.elementAt(i);
            barrier.logic();


            if (barrier.getX() < 0) {
                barriers.remove(i);
                i--; // Adjust the loop index after removing an element
            }


            if (barriers.size() <= 138) {
                addBarrier();
            }
        }


        if ((score / 2) % 5 == 0 && (score / 2) != 0 && lastSpeedIncreaseScore != score / 2) {//change the barrier speed for every 10 scores
            Barrier.increaseSpeed();
            lastSpeedIncreaseScore = score / 2;
        }


        logicCollisions();
        isOver();
    }


    private void isOver() {
        for (int i = 0; i < barriers.size(); i++) {
            Barrier barrier = barriers.elementAt(i);
            if (barrier.getIsOver() && barrier.getType() == GameProperty.BARRIER_NORMAL) {
                score++;
                barrier.setIsOver(false);
                barrier.setIsTake(true);
            }
        }
    }


    private void myDraw() {
        try {
            canvas = holder.lockCanvas();
            canvas.drawBitmap(bg, null, bgRect, paint);
            switch (GameState) {
                case GameProperty.GAME_ING: //Game continues
                    bird.draw(canvas, paint);
                    for (int i = 0; i < barriers.size(); i++) {
                        Barrier barrier = barriers.elementAt(i);
                        barrier.draw(canvas, paint);
                    }


                    canvas.drawText("Score:" + score / 2, 80, 100, paint);
                    break;
                case GameProperty.GAME_LOSS://Game ends
                    canvas.drawText("You Lose", getWidth() / 2 - 200, getHeight() / 2, paint);
                    flag = false;
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            holder.unlockCanvasAndPost(canvas);
        }
    }




    public boolean voiceControl(int volume) {
        if (volume >= 90) {
            bird.setState(2);
            bird.toUpVoice();//function of bird going up
        } else if (volume < 90) {
            bird.setState(0);
        } else {
            bird.setState(1);
        }
        return true;
    }


    public boolean logicCollisions() {
        for (int i = 0; i < barriers.size(); i++) {
            Barrier barrier = barriers.elementAt(i);
            if (barrier.isCollide(bird)) {
                GameState = GameProperty.GAME_LOSS;
                return true;
            }
        }
        return false;
    }


    public int getScore(){ return score; }


}
