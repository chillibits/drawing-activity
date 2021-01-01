/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.drawingactivityexample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.chillibits.drawingactivity.DrawingActivity;
import com.chillibits.drawingactivity.DrawingActivityBuilder;

public class MainActivityJava extends AppCompatActivity {
    // Constants
    private final int REQ_DRAWING = 10001;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        Button openDrawingActivity = findViewById(R.id.openDrawingActivity);
        openDrawingActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawingActivityBuilder.getInstance(MainActivityJava.this)
                    .enableToast(true)
                    .setTitle(R.string.drawing)
                    .setDefaultUtility(DrawingActivity.UTILITY_PEN)
                    .draw(REQ_DRAWING);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_DRAWING && resultCode == Activity.RESULT_OK)
            Toast.makeText(this, data.getStringExtra(DrawingActivity.DRAWING_PATH), Toast.LENGTH_LONG).show();
    }
}
