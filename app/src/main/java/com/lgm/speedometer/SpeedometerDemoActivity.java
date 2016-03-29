package com.lgm.speedometer;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lgm.speedometerlib.Speedometer;

public class SpeedometerDemoActivity extends AppCompatActivity {
	Speedometer speedometer;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_speedometer_demo);
		speedometer = (Speedometer) findViewById(R.id.speedometer_view);
		speedometer.addColor(Color.BLUE);
		speedometer.addColor(Color.RED);
		speedometer.addColor(Color.CYAN);
		speedometer.addColor(Color.GREEN);
		speedometer.addColor(Color.MAGENTA);
	}
}
