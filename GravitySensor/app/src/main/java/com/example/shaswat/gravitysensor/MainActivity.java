package com.example.shaswat.gravitysensor;

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

    private double gx = 0.0; //gravity vector in x direction
    private double gy = 0.0; //gravity vector in y direction
    private double gz = 0.0; //gravity vector in z direction
    private double f = 10.0; //frequency of publishing data
    private long lastUpdate_g; //last update time to maintain frequency
    private SensorManager sens_man; //instance of the sensor manager
    private Sensor gravity; //instance of accelerometer sensor

    //text views
    //for gravity sensor - m/s^2
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
        gravity = sens_man.getDefaultSensor(Sensor.TYPE_GRAVITY);
        sens_man.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);

        //set up display for accelerometer readings
        xview = (TextView) findViewById(R.id.grx);
        yview = (TextView) findViewById(R.id.gry);
        zview = (TextView) findViewById(R.id.grz);
        xview.setText("Gravity in X direction = " + this.gx);
        yview.setText("Gravity in Y direction = " + this.gy);
        zview.setText("Gravity in Z direction = " + this.gz);

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

        if (a.getType() == Sensor.TYPE_GRAVITY) {
            double x = event.values[0];
            double y = event.values[1];
            double z = event.values[2];

            long curTime = System.currentTimeMillis();

            if ((curTime - lastUpdate_g) > 1/f) {
                //update acceleration values
                this.gx = x;
                this.gy = y;
                this.gz = z;

                lastUpdate_g = curTime;

                //update the displayed values
                xview.setText("Gravity in X direction = " + this.gx);
                yview.setText("Gravity in Y direction = " + this.gy);
                zview.setText("Gravity in Z direction = " + this.gz);
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
        sens_man.registerListener(this, gravity, SensorManager.SENSOR_DELAY_NORMAL);
    }
}

