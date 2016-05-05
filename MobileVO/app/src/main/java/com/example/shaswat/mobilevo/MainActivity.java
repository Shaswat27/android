package com.example.shaswat.mobilevo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements CvCameraViewListener, View.OnClickListener {

    private ImageView show;

    private String TAG = "MainActivity";
    private CameraBridgeViewBase mOpenCvCameraView;
    public Mat mRgba;
    private Mat firstPic;
    private Mat secondPic;
    private Mat afterPic;
    private Bitmap bitmap;
    private boolean first = true;
    private boolean second = false;
    private boolean third = false;
    private int i = 2, xj = 0, yj = 0;

    private static final File StitchImageDir = new File(Environment.getExternalStorageDirectory()+ "/Trajectory/");
    private static final String mImageExt = ".jpeg";
    public final Handler mHandler = new Handler();

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    System.loadLibrary("visodo");
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                }
                break;
                default: {
                    super.onManagerConnected(status);
                }
                break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (ImageView) findViewById(R.id.tv_show);
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.color_blob_detection_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        IntentFilter filter = new IntentFilter();
        filter.addAction("bitmap");
        registerReceiver(dynamicReceiver, filter);
//		show.setVisibility(View.GONE);

        Button libButton =(Button) findViewById(R.id.button);
        libButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.button:
                togglePanaroma(v);
                break;
            default:
                break;
        }
    }

    private void showPic() {
        show.setImageBitmap(bitmap);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        Log.i("PAUSE", "Camera View Started");

        mRgba = Imgcodecs.imread(StitchImageDir.getPath() + "trajectory" + mImageExt);
        if(mRgba.empty()) mRgba = new Mat(height, width, CvType.CV_8UC4);

//		bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
    }

    @Override
    public void onCameraViewStopped() {
        mRgba.release();
    }


    @Override
    public void onPause() {
        super.onPause();

        Log.i("PAUSE", "paused");

        if(!StitchImageDir.exists())
            StitchImageDir.mkdir();
        Imgcodecs.imwrite(StitchImageDir.getPath() + "trajectory" + mImageExt, mRgba);

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    @Override
    public void onResume() {
        super.onResume();

        Log.i("PAUSE", "resume");

        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG,
                    "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this,
                    mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    public void onDestroy() {
        super.onDestroy();

        Log.i("PAUSE", "destroy");

        File file = new File(StitchImageDir.getPath() + "trajectory" + mImageExt);
        file.delete();

        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        unregisterReceiver(dynamicReceiver);
    }

    private BroadcastReceiver dynamicReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(MainActivity.this, "Success", Toast.LENGTH_SHORT)
                    .show();
            show.setImageBitmap(bitmap);
        }

    };

    private Bitmap getDiskBitmap(String pathString)
    {
        Bitmap bitmap = null;
        try
        {
            File file = new File(pathString);
            if(file.exists())
            {
                bitmap = BitmapFactory.decodeFile(pathString);
            }
        } catch (Exception e)
        {
        }


        return bitmap;
    }

    @Override
    public Mat onCameraFrame(Mat inputFrame) {
        if (first) {



//			Bitmap firstBitmap = getDiskBitmap("/storage/emulated/0/image/000000.jpg");
            firstPic = inputFrame;
//			Utils.bitmapToMat(firstBitmap, firstPic);
//			Mat d = new Mat(firstPic.rows(), firstPic.cols(), CvType.CV_32F);
//			firstPic.convertTo(d, CvType.CV_32F);
            first = false;
            second = true;
            Log.i(TAG, firstPic.type()+""+firstPic.channels()+firstPic.rows()+firstPic.cols()+firstPic);
            Log.i(TAG, "first success");

            //addRadioButtons(20,20);
            //addRadioButtons(100,200);
        }
        if (second) {
//			Bitmap secondBitmap
//			= getDiskBitmap("/storage/emulated/0/image/000001.jpg");
            secondPic = inputFrame;
            Log.i(TAG, secondPic.type()+""+secondPic.channels()+secondPic);

//			secondBitmap = Bitmap.createBitmap(inputFrame.width(), inputFrame.height(), Bitmap.Config.RGB_565);




//			Utils.matToBitmap(secondPic, secondBitmap);
//			Utils.bitmapToMat(secondBitmap, secondPic);
//			Mat d = inputFrame.rgba();
//			Log.i(TAG, d.type()+""+d.channels()+d);
//			d.convertTo(secondPic, CvType.CV_8U,1/255.0);
            second = false;
            third = true;
//			Log.i(TAG, secondPic.type()+""+secondPic.channels()+secondPic);
//			Log.i(TAG, d.type()+""+d.channels()+d);
            LibVisodo.init(firstPic.getNativeObjAddr(),
                    secondPic.getNativeObjAddr());
            Log.i(TAG, "second success");
        }
        if (third) {


            afterPic = inputFrame;
            bitmap = Bitmap.createBitmap(afterPic.width(), afterPic.height(),Bitmap.Config.ARGB_8888);
            Utils.matToBitmap(afterPic, bitmap);
            if(bitmap!=null){
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        showPic();
                        Log.i(TAG,"show pic");
                    }
                });
            }

//			saveBitmap(bitmap);
//			Bitmap thirdBitmap = getDiskBitmap("/storage/emulated/0/image/000002.png");
//			Utils.bitmapToMat(thirdBitmap, afterPic);
            try{
                String check = LibVisodo.start(mRgba.getNativeObjAddr(),
                        afterPic.getNativeObjAddr(),i);

                String[] splited = check.split("\\s+");

                xj = Integer.parseInt(splited[0]);
                yj = Integer.parseInt(splited[1]);

                Log.i("PAUSE", xj + " " + yj);
            }
            catch(Exception e){
                Log.i(TAG,"FAIL");
            }
            i++;
            Log.i(TAG, "third success");
        }
        return mRgba;
    }


    public void saveBitmap(Bitmap bm) {
        Log.e(TAG, "Bitmap");
        File f = new File("/storage/emulated/0/image/","000002.png");
        if (f.exists()) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            bm.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
            Log.i(TAG, "save bitmap");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void togglePanaroma(View view) {
        addRadioButtons(this.xj, this.yj);
        Intent intent = new Intent(this, Sample3Native.class);
        Bundle b = new Bundle();
        b.putInt("x", this.xj); //Your id
        b.putInt("y", this.yj);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

    public void toggleImageView(View view, Object tag) {
        Intent intent = new Intent(this, ViewImage.class);
        String id = tag.toString();
        String[] splited = id.split("\\s+");

        xj = Integer.parseInt(splited[0]);
        yj = Integer.parseInt(splited[1]);

        Bundle b = new Bundle();
        b.putInt("x", this.xj); //Your id
        b.putInt("y", this.yj);
        intent.putExtras(b); //Put your id to your next Intent
        startActivity(intent);
    }

    public void addRadioButtons( final int l, final int t ) {

        final Button myButton = new Button(this);
        myButton.setText("P");

        myButton.setTag(xj + " " + yj);

        final RelativeLayout ll = (RelativeLayout)findViewById(R.id.btn_layout);
        //final RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        myButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                //Toast.makeText(MainActivity.this, "Hello Friends", Toast.LENGTH_LONG).show();
                toggleImageView(v, myButton.getTag());

            }
        });

        runOnUiThread(new Runnable() {
            public void run() {

                RelativeLayout.LayoutParams rel_btn = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);

                rel_btn.leftMargin = l;
                rel_btn.topMargin = t;
                rel_btn.width = 50;
                rel_btn.height = 50;

                myButton.setLayoutParams(rel_btn);

                ll.addView(myButton);//, lp);


                ll.bringToFront();
                myButton.bringToFront();

                Log.i("PAUSE", "button");

                /*ll.setOrientation(LinearLayout.HORIZONTAL);

                rdbtn.setId(number);
                rdbtn.setText("HHH");
                //rdbtn.setOnClickListener();
                rdbtn.setX((float) 100.0);
                rdbtn.setY((float) 100.0);

                ll.addView(rdbtn);

                ((ViewGroup) findViewById(R.id.radiogroup)).addView(ll);
                ((ViewGroup) findViewById(R.id.radiogroup)).bringToFront();
                ((ViewGroup) findViewById(R.id.radiogroup)).setVisibility(View.VISIBLE);
                //ll.addView(rdbtn);*/
            }
        });



    }



}
