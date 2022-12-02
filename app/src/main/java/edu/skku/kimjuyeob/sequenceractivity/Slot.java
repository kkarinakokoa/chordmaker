package edu.skku.kimjuyeob.sequenceractivity;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class Slot extends Button {
    static int clicked_num=-1;

    private long _clickedtime;
    private int i=0;
    private int num;
    private boolean _allocated;
    private int notallocatedcolor=Color.rgb(45,45,45);
    private int notallocatedcolorfortext=Color.rgb(170,170,170);
    private int passmecolor=Color.rgb(200,200,200);
    private int[] passmecolorfortext;
    private int[] pickmecolor;
    private int pickmecolorfortext=Color.rgb(255,255,255);
    //public Timer timer;
    public Slot(Context context) {
        super(context);
        _init();
        pickmecolor = new int[]{Color.rgb(145,0,145),Color.rgb(145,145,0),Color.rgb(0,145,145)};
        passmecolorfortext = new int[]{Color.rgb(200,0,0),Color.rgb(200,200,0),Color.rgb(0,0,200)};
        set_clickedtime();
        this.setAllCaps(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_num=num;
            }
        });
        //timer = new Timer();
    }
    public Slot(Context context, AttributeSet attrs) {
        super(context, attrs);
        _init();
        pickmecolor = new int[]{Color.rgb(145,0,145),Color.rgb(145,145,0),Color.rgb(0,145,145)};
        passmecolorfortext = new int[]{Color.rgb(200,0,0),Color.rgb(200,200,0),Color.rgb(0,0,200)};
        set_clickedtime();
        this.setAllCaps(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_num=num;
            }
        });
        //timer = new Timer();
    }
    public Slot(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        _init();
        pickmecolor = new int[]{Color.rgb(145,0,145),Color.rgb(145,145,0),Color.rgb(0,145,145)};
        passmecolorfortext = new int[]{Color.rgb(200,0,0),Color.rgb(200,200,0),Color.rgb(0,0,200)};
        set_clickedtime();
        this.setAllCaps(false);
        this.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                clicked_num=num;
            }
        });
        //timer = new Timer();
    }

    public void set_clickedtime() {
        _clickedtime=0;
    }
    public long get_clickedtime() {
        return _clickedtime;
    }
    public void set_num(int n) {
        num=n;
    }
    public int get_num() {
        return num;
    }
    public void _init() {
        _allocated=false;
        this.setBackgroundColor(notallocatedcolor);
        this.setTextColor(notallocatedcolorfortext);
        this.setClickable(false);
    }
    public void pickme() {
        this.setClickable(true);
        if(_allocated) {
            this.setBackgroundColor(passmecolor);
            this.setTextColor(passmecolorfortext[0]);
            /*TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    setBackgroundColor(passmecolor);
                    setTextColor(passmecolorfortext[i]);
                    i=(i+1)%3;
                }
            };
            timer.schedule(timerTask,0,1000);*/
        } else {
            this.setBackgroundColor(pickmecolor[0]);
            this.setTextColor(pickmecolorfortext);
            /*TimerTask timerTask=new TimerTask() {
                @Override
                public void run() {
                    setBackgroundColor(pickmecolor[i]);
                    setTextColor(pickmecolorfortext);
                    i=(i+1)%3;
                }
            };
            timer.schedule(timerTask,0,1000);*/
        }
    }
    public void allocated(String text) {
        _allocated=true;
        this.setText(text);
    }
    public int getClicked_num() {
        return clicked_num;
    }
    public void setClicked_num(int n) {
        clicked_num=n;
    }
}
