package com.example.shaswat.imu;

import android.content.Context;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorManager;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    //in m/s^2
    private double ax = 0.0; //acceleration in x direction
    private double ay = 0.0; //acceleration in y direction
    private double az = 0.0; //acceleration in z direction

    //in m/s^2
    private double gx = 0.0; //gravity in x direction using filter
    private double gy = 0.0; //gravity in y direction using filter
    private double gz = 0.0; //gravity in z direction using filter

    //in m/s^2
    private double gsx = 0.0; //gravity in x direction using sensor
    private double gsy = 0.0; //gravity in x direction using sensor
    private double gsz = 0.0; //gravity in x direction using sensor

    //in uT
    private double mx = 0.0; //magnetic field in x direction (normalized)
    private double my = 0.0; //magnetic field in y direction (normalized)
    private double mz = 0.0; //magnetic field in z direction (normalized)

    private double f = 10.0; //frequency of publishing data
    private long lastUpdate_a; //last update time to maintain frequency
    private SensorManager sens_man; //instance of the sensor manager
    private Sensor accel; //instance of accelerometer sensor
    private Sensor grav; //instance of gravity sensor
    private Sensor mag; //instance of magnetic sensor

    //text views
    //for accelerometer - m/s^2
    private TextView aview_x;
    private TextView aview_y;
    private TextView aview_z;

    //for filtered gravity readings - m/s^2
    private TextView gfview_x;
    private TextView gfview_y;
    private TextView gfview_z;

    //for gravity sensor readings - m/s^2
    private TextView gview_x;
    private TextView gview_y;
    private TextView gview_z;

    //for magnetometer readings (normalized) - uT
    private TextView mview_x;
    private TextView mview_y;
    private TextView mview_z;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sens_man = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        accel = sens_man.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sens_man.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        grav = sens_man.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sens_man.registerListener(this, grav, SensorManager.SENSOR_DELAY_NORMAL);

        mag = sens_man.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sens_man.registerListener(this, mag, SensorManager.SENSOR_DELAY_NORMAL);

        //set up display for accelerometer readings
        aview_x = (TextView) findViewById(R.id.acx);
        aview_y = (TextView) findViewById(R.id.acy);
        aview_z = (TextView) findViewById(R.id.acz);
        aview_x.setText("X direction : " + this.ax);
        aview_y.setText("Y direction : " + this.ay);
        aview_z.setText("Z direction : " + this.az);

        //set up display for filtered gravity readings
        gfview_x = (TextView) findViewById(R.id.g1_x);
        gfview_y = (TextView) findViewById(R.id.g1_y);
        gfview_z = (TextView) findViewById(R.id.g1_z);
        gfview_x.setText("X direction : " + this.gx);
        gfview_y.setText("Y direction : " + this.gy);
        gfview_z.setText("Z direction : " + this.gz);

        //set up display for gravity sensor readings
        gview_x = (TextView) findViewById(R.id.g2_x);
        gview_y = (TextView) findViewById(R.id.g2_y);
        gview_z = (TextView) findViewById(R.id.g2_z);
        gview_x.setText("X direction : " + this.gsx);
        gview_y.setText("Y direction : " + this.gsy);
        gview_z.setText("Z direction : " + this.gsz);

        //set up display for magnetometer readings
        mview_x = (TextView) findViewById(R.id.mg_x);
        mview_y = (TextView) findViewById(R.id.mg_y);
        mview_z = (TextView) findViewById(R.id.mg_z);
        mview_x.setText("X direction : " + this.mx);
        mview_y.setText("Y direction : " + this.my);
        mview_z.setText("Z direction : " + this.mz);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor a = event.sensor;

        double alpha = 0.8;

        synchronized (this) {

            switch (a.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    double x = event.values[0];
                    double y = event.values[1];
                    double z = event.values[2];

                    long curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate_a) > 0) {
                        //update acceleration values
                        this.ax = x;
                        this.ay = y;
                        this.az = z;

                        //update filtered gravity values
                        this.gx = alpha * (this.gx) + (1.0 - alpha) * (this.ax);
                        this.gy = alpha * (this.gy) + (1.0 - alpha) * (this.ay);
                        this.gz = alpha * (this.gz) + (1.0 - alpha) * (this.az);

                        lastUpdate_a = curTime;

                        //update the displayed values
                        aview_x.setText("X direction : " + this.ax);
                        aview_y.setText("Y direction : " + this.ay);
                        aview_z.setText("Z direction : " + this.az);

                        //update gravity values
                        gfview_x.setText("X direction : " + this.gx);
                        gfview_y.setText("Y direction : " + this.gy);
                        gfview_z.setText("Z direction : " + this.gz);
                    }
                    break;

                case Sensor.TYPE_GRAVITY:
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate_a) > 0) {
                        //update gravity values
                        this.gsx = x;
                        this.gsy = y;
                        this.gsz = z;

                        lastUpdate_a = curTime;

                        //update the displayed values
                        gview_x.setText("X direction : " + this.gsx);
                        gview_y.setText("Y direction : " + this.gsy);
                        gview_z.setText("Z direction : " + this.gsz);
                    }
                    break;

                case Sensor.TYPE_MAGNETIC_FIELD:
                    x = event.values[0];
                    y = event.values[1];
                    z = event.values[2];

                    curTime = System.currentTimeMillis();

                    if ((curTime - lastUpdate_a) > 0) {
                        //update acceleration values
                        this.mx = x/(x+y+z);
                        this.my = y/(x+y+z);
                        this.mz = z/(x+y+z);

                        lastUpdate_a = curTime;

                        //update the displayed values
                        mview_x.setText("X direction : " + this.mx);
                        mview_y.setText("Y direction : " + this.my);
                        mview_z.setText("Z direction : " + this.mz);
                    }
                    break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

        //nothing as of yet, maybe needed when using kalman filter
    }

    protected void onPause() {
        super.onPause();
        sens_man.unregisterListener(this);
    }

    protected void onResume() {
        super.onResume();
        sens_man.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

