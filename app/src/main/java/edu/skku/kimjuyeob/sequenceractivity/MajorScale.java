package edu.skku.kimjuyeob.sequenceractivity;


public class MajorScale {
    private int tonic;
    private boolean sharpmode;
    public Integer[] Scale;
    public MajorScale(int init) {
        tonic=init;
        set_sharpmode();
        Scale=new Integer[]{tonic,tonic+2,tonic+4,tonic+5,tonic+7,tonic+9,tonic+11};
        normalize();
    }
    public void transpose_up() {
        for(int i=0; i<7; i++) Scale[i]++;
        normalize();
        tonic=Scale[0];
        set_sharpmode();
    }
    public void transpose_down() {
        for(int i=0; i<7; i++) Scale[i]--;
        normalize();
        tonic=Scale[0];
        set_sharpmode();
    }
    private void normalize() {
        for(int i=0; i<7; i++)
            if (Scale[i] == -1) Scale[i] = 11;
            else if (Scale[i] >= 12) Scale[i] %= 12;
    }
    private void set_sharpmode() {
        switch (tonic) {
            case 0:
            case 2:
            case 4:
            case 7:
            case 9:
            case 11:
                sharpmode=true;
                break;
            default:
                sharpmode=false;
        }
    }
    public boolean get_sharpmode() {
        return sharpmode;
    }
}
