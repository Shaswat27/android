package com.example.shaswat.mobilevo;

/**
 * Created by shaswat on 30/4/16.
 */
public class LibVisodo {
    static {
        //System.loadLibrary("opencv_java3");
        System.loadLibrary("visodo");
    }
    public static native String start(long matAddrRgba,long afterPic,int i);

    public static native String init(long firstPic,long secondPic);
}