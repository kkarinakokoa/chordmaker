package edu.skku.kimjuyeob.sequenceractivity;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private View decorView;
    private int uiOption;

    private Slot notexistingslot;
    private int tempo;

    private Timer timer;
    //private TimerTask task;
    private SoundPool sound;
    private int[] samples;

    MajorScale Scale;
    String[] Names;
    String[] Triads;

    Button[] chordbtn;
    Button rbtn, cbtn, lbtn;
    Button qbtn, hbtn, wbtn;
    Button delbtn;
    Button playbtn;

    int _focused;
    Drawable bgcolor;
    int chordclicked=Color.rgb(45,45,45);

    Boolean _added;
    Boolean _delclicked;
    Boolean _isplaying;

    LinearLayout sequencer;
    ArrayList<Slot> slots;
    ArrayList<Integer> harmony;
    int chordsequence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        decorView = getWindow().getDecorView();
        uiOption = getWindow().getDecorView().getSystemUiVisibility();
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH )
            uiOption |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN )
            uiOption |= View.SYSTEM_UI_FLAG_FULLSCREEN;
        if( Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT )
            uiOption |= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        decorView.setSystemUiVisibility( uiOption );

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        tempo=120; // 80 bpm
        sound=new SoundPool(1, AudioManager.STREAM_MUSIC,0);
        _isplaying=false;

        sequencer=findViewById(R.id.seq);
        Scale=new MajorScale(0);
        Names=new String[12];
        set_Names(Scale.get_sharpmode());
        Triads=new String[7];
        set_Triads();

        notexistingslot=new Slot(MainActivity.this);
        slots=new ArrayList<>();
        harmony=new ArrayList<>(); // -1: No start of chords


        _focused=-1;
        _added=false;
        _delclicked=false;

        chordbtn= new Button[]{findViewById(R.id.btni), findViewById(R.id.btnii), findViewById(R.id.btniii), findViewById(R.id.btniv), findViewById(R.id.btnv), findViewById(R.id.btnvi), findViewById(R.id.btnvii), findViewById(R.id.btnmute)};
        set_ChordLabels();

        setbtnclicklisteners();

        samples=new int[37];
        loadsound();

        chordsequence=0;

        playbtn=findViewById(R.id.playbtn);
        playbtn.setText("Play");
        playbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(harmony.size()>0) {
                    if (!_isplaying) {
                        TimerTask task=new TimerTask() {
                            @Override
                            public void run() {
                                if(chordsequence==harmony.size()) chordsequence=0;
                                if(harmony.get(chordsequence)!=-1) {
                                    switch (harmony.get(chordsequence)) {
                                        case 0:
                                            sound.play(samples[Scale.Scale[0]], 1, 1, 0, 0, 1);
                                            break;
                                        case 1:
                                            sound.play(samples[12 + Scale.Scale[1]], 1, 1, 0, 0, 1);
                                            break;
                                        case 2:
                                            sound.play(samples[12 + Scale.Scale[2]], 1, 1, 0, 0, 1);
                                            break;
                                        case 3:
                                            sound.play(samples[Scale.Scale[3]], 1, 1, 0, 0, 1);
                                            break;
                                        case 4:
                                            sound.play(samples[Scale.Scale[4]], 1, 1, 0, 0, 1);
                                            break;
                                        case 5:
                                            sound.play(samples[12 + Scale.Scale[5]], 1, 1, 0, 0, 1);
                                            break;
                                        case 6:
                                            sound.play(samples[24 + Scale.Scale[6]], 1, 1, 0, 0, 1);
                                            break;
                                        case 7:
                                            sound.play(samples[36], 1, 1, 0, 0, 1);
                                            break;
                                    }
                                }
                                chordsequence++;
                            }
                        };
                        for (int i = 0; i < 8; i++) chordbtn[i].setVisibility(View.INVISIBLE);
                        lbtn.setVisibility(View.INVISIBLE);
                        rbtn.setVisibility(View.INVISIBLE);
                        delbtn.setVisibility(View.INVISIBLE);
                        playbtn.setText("Stop");
                        _isplaying = true;
                        if(timer==null) timer=new Timer();
                        timer.schedule(task, 500, 60000 / tempo);
                    } else {
                        for (int i = 0; i < 8; i++) chordbtn[i].setVisibility(View.VISIBLE);
                        lbtn.setVisibility(View.VISIBLE);
                        rbtn.setVisibility(View.VISIBLE);
                        delbtn.setVisibility(View.VISIBLE);
                        playbtn.setText("Play");
                        _isplaying = false;
                        chordsequence = 0;
                        timer.cancel();
                        timer=null;
                    }
                }
            }
        });

        delbtn=findViewById(R.id.delbtn);
        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(harmony.size()>0) {
                    if (!_delclicked) {
                        _delclicked = true;
                        for (int i = 0; i < 8; i++) chordbtn[i].setVisibility(View.INVISIBLE);
                        lbtn.setVisibility(View.INVISIBLE);
                        cbtn.setVisibility(View.INVISIBLE);
                        rbtn.setVisibility(View.INVISIBLE);
                        playbtn.setVisibility(View.INVISIBLE);
                        for (int j = 0; j < harmony.size(); j++) {
                            if (harmony.get(j) != -1) {
                                slots.get(j).setClickable(true);
                                slots.get(j).setBackgroundColor(Color.rgb(145, 0, 145));
                            }
                        }
                    } else {
                        _delclicked=false;
                        for (int i = 0; i < 8; i++) chordbtn[i].setVisibility(View.VISIBLE);
                        lbtn.setVisibility(View.VISIBLE);
                        rbtn.setVisibility(View.VISIBLE);
                        playbtn.setVisibility(View.VISIBLE);
                        int index=-1; long max=0;
                        for (int j = 0; j < harmony.size(); j++) {
                            if (harmony.get(j) != -1) {
                                if(slots.get(j).get_num()==notexistingslot.getClicked_num())
                                    index=j;
                            }
                        }
                        if(index!=-1) {
                            harmony.remove(index);
                            while(harmony.size()>index) {
                                if(harmony.get(index)==-1)
                                    harmony.remove(index);
                                else break;
                            }
                        }
                        notexistingslot.setClicked_num(-1);
                        for(int i=0; i<8; i++) chordbtn[i].setBackground(bgcolor);
                        rearrange();
                        recommend();
                    }
                }
            }
        });

        qbtn=findViewById(R.id.btnquater); hbtn=findViewById(R.id.btnhalf); wbtn=findViewById(R.id.btnwhole);
        qbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                harmony.add(_focused);
                qbtn.setVisibility(View.INVISIBLE); hbtn.setVisibility(View.INVISIBLE); wbtn.setVisibility(View.INVISIBLE);
                _added=false;
                cbtn.setText("Add");
                for(int i=0; i<8; i++) {
                    chordbtn[i].setBackground(bgcolor);
                    chordbtn[i].setClickable(true);
                }
                rearrange();
                cbtn.setVisibility(View.INVISIBLE);
                delbtn.setVisibility(View.VISIBLE);
                playbtn.setVisibility(View.VISIBLE);
                lbtn.setVisibility(View.VISIBLE);
                rbtn.setVisibility(View.VISIBLE);
                recommend();
            }
        });
        hbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                harmony.add(_focused);
                harmony.add(-1);
                qbtn.setVisibility(View.INVISIBLE); hbtn.setVisibility(View.INVISIBLE); wbtn.setVisibility(View.INVISIBLE);
                _added=false;
                cbtn.setText("Add");
                for(int i=0; i<8; i++) {
                    chordbtn[i].setBackground(bgcolor);
                    chordbtn[i].setClickable(true);
                }
                rearrange();
                cbtn.setVisibility(View.INVISIBLE);
                delbtn.setVisibility(View.VISIBLE);
                playbtn.setVisibility(View.VISIBLE);
                lbtn.setVisibility(View.VISIBLE);
                rbtn.setVisibility(View.VISIBLE);
                recommend();
            }
        });
        wbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                harmony.add(_focused);
                harmony.add(-1);
                harmony.add(-1);
                harmony.add(-1);
                qbtn.setVisibility(View.INVISIBLE); hbtn.setVisibility(View.INVISIBLE); wbtn.setVisibility(View.INVISIBLE);
                _added=false;
                cbtn.setText("Add");
                for(int i=0; i<8; i++) {
                    chordbtn[i].setBackground(bgcolor);
                    chordbtn[i].setClickable(true);
                }
                rearrange();
                cbtn.setVisibility(View.INVISIBLE);
                delbtn.setVisibility(View.VISIBLE);
                playbtn.setVisibility(View.VISIBLE);
                lbtn.setVisibility(View.VISIBLE);
                rbtn.setVisibility(View.VISIBLE);
                recommend();
            }
        });
        qbtn.setVisibility(View.INVISIBLE); hbtn.setVisibility(View.INVISIBLE); wbtn.setVisibility(View.INVISIBLE);


        lbtn=findViewById(R.id.lbtn); cbtn=findViewById(R.id.cbtn); rbtn=findViewById(R.id.rbtn);
        cbtn.setVisibility(View.INVISIBLE);
        bgcolor=lbtn.getBackground();

        rearrange();

        cbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!_added) {
                    _added=true;
                    cbtn.setText("Cancel");
                    for(int i=0; i<8; i++) {
                        chordbtn[i].setClickable(false);
                        if(i!=_focused)
                            chordbtn[i].setBackgroundColor(Color.rgb(45, 45, 45));
                        else
                            chordbtn[i].setBackground(bgcolor);
                    }
                    lbtn.setVisibility(View.INVISIBLE);
                    rbtn.setVisibility(View.INVISIBLE);
                    delbtn.setVisibility(View.INVISIBLE);
                    adding();
                } else {
                    _added=false;
                    /*for(int i=0; i<slots.size(); i++) {
                        //slots.get(i).timer.cancel();
                        slots.get(i)._init();
                    }*/
                    cbtn.setText("Add");
                    for(int i=0; i<8; i++) {
                        if(i==_focused)
                            chordbtn[i].setBackgroundColor(Color.rgb(45, 45, 45));
                        else
                            chordbtn[i].setBackground(bgcolor);
                        chordbtn[i].setClickable(true);
                    }
                    lbtn.setVisibility(View.VISIBLE);
                    rbtn.setVisibility(View.VISIBLE);
                    recommend();
                }
            }
        });

        lbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transpose_step(1);
                if(_focused!=-1) {
                    chordbtn[_focused].setBackground(bgcolor);
                    _focused=-1;
                }
                cbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.VISIBLE);
                rearrange();
            }
        });
        rbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                transpose_step(-1);
                if(_focused!=-1) {
                    chordbtn[_focused].setBackground(bgcolor);
                    _focused=-1;
                }
                cbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.VISIBLE);
                rearrange();
            }
        });

    }

    private void loadsound() {
        samples[0]=sound.load(MainActivity.this,R.raw.piano0,0);
        samples[1]=sound.load(MainActivity.this,R.raw.piano1,0);
        samples[2]=sound.load(MainActivity.this,R.raw.piano2,0);
        samples[3]=sound.load(MainActivity.this,R.raw.piano3,0);
        samples[4]=sound.load(MainActivity.this,R.raw.piano4,0);
        samples[5]=sound.load(MainActivity.this,R.raw.piano5,0);
        samples[6]=sound.load(MainActivity.this,R.raw.piano6,0);
        samples[7]=sound.load(MainActivity.this,R.raw.piano7,0);
        samples[8]=sound.load(MainActivity.this,R.raw.piano8,0);
        samples[9]=sound.load(MainActivity.this,R.raw.piano9,0);
        samples[10]=sound.load(MainActivity.this,R.raw.piano10,0);
        samples[11]=sound.load(MainActivity.this,R.raw.piano11,0);

        samples[12+0]=sound.load(MainActivity.this,R.raw.piano0m,0);
        samples[12+1]=sound.load(MainActivity.this,R.raw.piano1m,0);
        samples[12+2]=sound.load(MainActivity.this,R.raw.piano2m,0);
        samples[12+3]=sound.load(MainActivity.this,R.raw.piano3m,0);
        samples[12+4]=sound.load(MainActivity.this,R.raw.piano4m,0);
        samples[12+5]=sound.load(MainActivity.this,R.raw.piano5m,0);
        samples[12+6]=sound.load(MainActivity.this,R.raw.piano6m,0);
        samples[12+7]=sound.load(MainActivity.this,R.raw.piano7m,0);
        samples[12+8]=sound.load(MainActivity.this,R.raw.piano8m,0);
        samples[12+9]=sound.load(MainActivity.this,R.raw.piano9m,0);
        samples[12+10]=sound.load(MainActivity.this,R.raw.piano10m,0);
        samples[12+11]=sound.load(MainActivity.this,R.raw.piano11m,0);

        samples[12+12+0]=sound.load(MainActivity.this,R.raw.piano0m,0);
        samples[12+12+1]=sound.load(MainActivity.this,R.raw.piano1m,0);
        samples[12+12+2]=sound.load(MainActivity.this,R.raw.piano2m,0);
        samples[12+12+3]=sound.load(MainActivity.this,R.raw.piano3m,0);
        samples[12+12+4]=sound.load(MainActivity.this,R.raw.piano4m,0);
        samples[12+12+5]=sound.load(MainActivity.this,R.raw.piano5m,0);
        samples[12+12+6]=sound.load(MainActivity.this,R.raw.piano6m,0);
        samples[12+12+7]=sound.load(MainActivity.this,R.raw.piano7m,0);
        samples[12+12+8]=sound.load(MainActivity.this,R.raw.piano8m,0);
        samples[12+12+9]=sound.load(MainActivity.this,R.raw.piano9m,0);
        samples[12+12+10]=sound.load(MainActivity.this,R.raw.piano10m,0);
        samples[12+12+11]=sound.load(MainActivity.this,R.raw.piano11m,0);

        samples[36]=sound.load(MainActivity.this,R.raw.mute,0);
    }
    private void recommend() {
        boolean _found=false;
        for(int i=harmony.size()-1; i>=0; i--) {
            if((harmony.get(i)==-1)||(harmony.get(i)==7)) continue;
            switch (harmony.get(i)) {
                case 0:
                    chordbtn[1].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[2].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[3].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[4].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[5].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    break;
                case 1:
                    chordbtn[2].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[4].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[5].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    boolean chain=false;
                    for(int j=i-1; j>=0; j--) {
                        if ((harmony.get(j) == -1) || (harmony.get(j) == 7)) continue;
                        else if (harmony.get(j) == 2) chain = true;
                        else if ((harmony.get(j) == 3) && chain)
                            chordbtn[0].setBackgroundColor(Color.rgb(160, 0, 180));
                        else break;
                    }
                    break;
                case 2:
                    chordbtn[3].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[5].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    for(int j=i-1; j>=0; j--) {
                        if((harmony.get(j)==-1)||(harmony.get(j)==7)) continue;
                        else if(harmony.get(j)==3)
                            chordbtn[1].setBackgroundColor(Color.rgb(160,0,180));
                        else break;
                    }
                    break;
                case 3:
                    chordbtn[4].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[1].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[0].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    chordbtn[2].setBackgroundColor(Color.rgb(160,0,180));
                    break;
                case 4:
                    chordbtn[0].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[1].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[2].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[5].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    break;
                case 5:
                    chordbtn[1].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[2].setBackgroundColor(Color.rgb(00,160,0));
                    chordbtn[3].setBackgroundColor(Color.rgb(00,160,0));
                    _found=true;
                    break;
                case 6:
                    _found=true;
                    break;
            }
            if(_found) break;
        }
    }
    private void rearrange() {
        if(harmony.size()==0) {
            slots.clear();
            sequencer.removeAllViews();
            Slot newslot=new Slot(MainActivity.this);
            newslot.set_num(0);
            newslot.setText("1.1");
            slots.add(newslot);
            sequencer.addView(slots.get(0));
        } else {
            slots.clear();
            sequencer.removeAllViews();
            for(int j=0; j<harmony.size(); j++) {
                Slot newslot=new Slot(MainActivity.this);
                newslot.set_num(j);
                if(harmony.get(j)!=-1) {
                    if(harmony.get(j)==7) {
                        newslot.setText("Mute");
                    }
                    else newslot.setText(Names[Scale.Scale[harmony.get(j)]] + Triads[harmony.get(j)]);
                }
                else {
                    int period=j/4+1;
                    int sub=j%4+1;
                    newslot.setText(Integer.toString(period)+"."+Integer.toString(sub));
                }
                slots.add(newslot);
                sequencer.addView(slots.get(j));
            }
        }
    }
    private void adding() {
        qbtn.setVisibility(View.VISIBLE); hbtn.setVisibility(View.VISIBLE); wbtn.setVisibility(View.VISIBLE);
    }
    private void transpose_step(int n) {
        if(n==1) Scale.transpose_up();
        else if(n==-1) Scale.transpose_down();
        set_Names(Scale.get_sharpmode());
        set_ChordLabels();
    }
    private void set_ChordLabels() {
        for(int i=0; i<7; i++)
            chordbtn[i].setText(Names[Scale.Scale[i]]+Triads[i]);
        chordbtn[7].setText("Mute");
    }
    private void set_Names(boolean sharpmode) {
        Names[0]="C"; Names[2]="D"; Names[4]="E"; Names[5]="F"; Names[7]="G"; Names[9]="A"; Names[11]="B";
        if(sharpmode) {
            Names[1]="C#"; Names[3]="D#"; Names[6]="F#"; Names[8]="G#"; Names[10]="A#";
        } else {
            Names[1]="Db"; Names[3]="Eb"; Names[6]="Gb"; Names[8]="Ab"; Names[10]="Bb";
        }
    }
    private void set_Triads() {
        Triads[0]=Triads[3]=Triads[4]=""; Triads[1]=Triads[2]=Triads[5]="m"; Triads[6]="dim";
    }
    private void setbtnclicklisteners() {
        chordbtn[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=0;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[Scale.Scale[0]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=1;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[12 + Scale.Scale[1]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=2;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[12 + Scale.Scale[2]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=3;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[Scale.Scale[3]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=4;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[Scale.Scale[4]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[5].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=5;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[12 + Scale.Scale[5]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[6].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=6;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[24 + Scale.Scale[6]], 1, 1, 0, 0, 1);
            }
        });
        chordbtn[7].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(_focused!=-1)
                    chordbtn[_focused].setBackground(bgcolor);
                _focused=7;
                cbtn.setText("Add");
                cbtn.setVisibility(View.VISIBLE);
                delbtn.setVisibility(View.INVISIBLE);
                playbtn.setVisibility(View.INVISIBLE);
                recommend();
                v.setBackgroundColor(chordclicked);
                sound.play(samples[36], 1, 1, 0, 0, 1);
            }
        });
    }
}