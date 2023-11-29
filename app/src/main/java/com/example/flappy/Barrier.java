package com.example.flappy;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.util.Vector;

public class Barrier {
    private Bitmap barrier;
    private int x, y;
    private int screenW, screenH;
    private int type;
    private boolean isOver = false;
    private boolean isTake = false;
    private static int speed;

    public Barrier(Bitmap barrier, int x, int y, int screenW, int screenH, int type) {
        this.barrier = barrier;
        this.x = x;
        this.y = y;
        this.screenW = screenW;
        this.screenH = screenH;
        this.type = type;
        speed = GameProperty.barrierSpeed;
    }

    public void draw(Canvas canvas, Paint paint) {
        canvas.drawBitmap(barrier,x,y,paint);
    }

    public void logic() {
        x-=speed;
        if(x+barrier.getWidth()<=screenW/6 && type == GameProperty.BARRIER_NORMAL && isTake == false){
            isOver=true;
        }
    }

    public static void increaseSpeed(){
        GameProperty.barrierSpeed += 2;
        speed = GameProperty.barrierSpeed;
    }

    public boolean isCollide(Bird bird){
        if(bird.getXCoordinate() + bird.getElementAtBitmap(bird.getState()).getWidth()<x){//bird did not collide on the right
            return false;
        }
        else if(bird.getYCoordinate()>y+barrier.getHeight()){//bird did not collide on the top
            return false;
        }
        else if(bird.getYCoordinate()+bird.getElementAtBitmap(bird.getState()).getHeight()<y) {//bird did not collide on the bottom
            return false;
        }
        else if(bird.getXCoordinate()>barrier.getWidth()+x){//bird did not collide on the left
            return false;
        }
        return true;
    }

    public int getType(){
        return type;
    }

    public void setIsOver(boolean tf){
        isOver =  tf;
    }

    public boolean getIsOver(){
        return isOver;
    }

    public void setIsTake(boolean tf) {
        isTake = tf;
    }

    public int getX(){ return x; }
}
