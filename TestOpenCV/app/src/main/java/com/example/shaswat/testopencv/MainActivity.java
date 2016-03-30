package com.example.shaswat.testopencv;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    public native int convertNativeGray(long matAddrCurr, long matAddrPrev, long matAddrDisplay);

    private Mat mCurr;
    private Mat mPrev;
    private Mat mDisplay;
    private int firstRun = 1;
    private int controlFrameRate = 0;

    private CameraBridgeViewBase mOpenCvCameraView;
    private static final String TAG = "OCVSample::SDK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.HelloOpenCvView);
        mOpenCvCameraView.setVisibility(SurfaceView.VISIBLE);
        mOpenCvCameraView.setCvCameraViewListener(this);
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    System.loadLibrary("nativegray");
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
        mCurr = new Mat();
        mPrev = new Mat();
        mDisplay = new Mat();
    }

    public void onCameraViewStopped() {
    }

    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        /*if(this.controlFrameRate == 10000)
        {
            this.controlFrameRate = 0;
        }*/

        this.mCurr = inputFrame.rgba();

        if(this.firstRun == 1) {
            this.mPrev = inputFrame.rgba();
            this.firstRun = 0;
        }

        if(this.firstRun == 0) {
            this.mDisplay = this.mPrev;
            convertNativeGray(mCurr.getNativeObjAddr(), mPrev.getNativeObjAddr(), mDisplay.getNativeObjAddr());
        }

        this.mPrev = this.mCurr;

        //this.controlFrameRate = this.controlFrameRate + 1;

        return mDisplay;
    }

    public void toggleIMUandCamera(View view) {
            Intent intent = new Intent(this, IMUActivity.class);
            startActivity(intent);
    }

}
