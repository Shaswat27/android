package com.example.shaswat.accelerometer;

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

    private double ax = 0.0; //acceleration in x direction
    private double ay = 0.0; //acceleration in y direction
    private double az = 0.0; //acceleration in z direction
    private double mx = 0.0; //magnetic field in x direction
    private double my = 0.0; //magnetic field in y direction
    private double mz = 0.0; //magentic field in z direction
    private double f = 10.0; //frequency of publishing data
    private long lastUpdate_a; //last update time to maintain frequency
    private SensorManager sens_man; //instance of the sensor manager
    private Sensor accel; //instance of accelerometer sensor

    //text views
    //for accelerometer - m/s^2
    private TextView xview;
    private TextView yview;
    private TextView zview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        sens_man = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accel = sens_man.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sens_man.registerListener(this, accel, SensorManager.SENSOR_DELAY_NORMAL);

        //set up display for accelerometer readings
        xview = (TextView) findViewById(R.id.acx);
        yview = (TextView) findViewById(R.id.acy);
        zview = (TextView) findViewById(R.id.acz);
        xview.setText("Acceleration in X direction = " + this.ax);
        yview.setText("Acceleration in Y direction = " + this.ay);
        zview.setText("Acceleration in Z direction = " + this.az);

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

        if (a.getType() == Sensor.TYPE_ACCELEROMETER) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate_a) > 1/f) {
                //update acceleration values
                this.ax = x;
                this.ay = y;
                this.az = z;

                lastUpdate_a = curTime;

                //update the displayed values
                xview.setText("Acceleration in X direction = " + this.ax);
                yview.setText("Acceleration in Y direction = " + this.ay);
                zview.setText("Acceleration in Z direction = " + this.az);
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
