package com.simple.container.ui.notifications;

import android.widget.Button;

import java.nio.Buffer;

public class ExtraItem {
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getInsBtn() {
        return insBtn;
    }

    public void setInsBtn(String insBtn) {
        this.insBtn = insBtn;
    }

    public String getRmBtn() {
        return rmBtn;
    }

    public void setRmBtn(String rmBtn) {
        this.rmBtn = rmBtn;
    }

    public boolean isBtn1() {
        return btn1;
    }

    public void setBtn1(boolean btn1) {
        this.btn1 = btn1;
    }

    public boolean isBtn2() {
        return btn2;
    }

    public void setBtn2(boolean btn2) {
        this.btn2 = btn2;
    }

    private String insBtn;
    private String rmBtn;
    private boolean btn1;
    private boolean btn2;


    public ExtraItem(String text,String insBtn,String rmBtn,boolean btn1,boolean btn2){
        this.text=text;
        this.insBtn=insBtn;
        this.rmBtn=rmBtn;
        this.btn1=btn1;
        this.btn2=btn2;
    }



}
