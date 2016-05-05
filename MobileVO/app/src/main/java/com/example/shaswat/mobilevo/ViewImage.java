package com.example.shaswat.mobilevo;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by shaswat on 2/5/16.
 */
public class ViewImage extends Activity {

    ImageView imageView1;
    private int xj;
    private int yj;

    private static File StitchImageDir = new File(Environment.getExternalStorageDirectory()+ "/panoStitchIm");

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle b = getIntent().getExtras();
        this.xj = b.getInt("xj");
        this.yj = b.getInt("yj");

        setContentView(R.layout.activity_view);
        imageView1=(ImageView)findViewById(R.id.imageView1);
        Bitmap bmap = BitmapFactory.decodeFile(StitchImageDir.getPath() + xj + "_" + yj + ".jpeg");
        imageView1.setImageBitmap(bmap);
    }

}


