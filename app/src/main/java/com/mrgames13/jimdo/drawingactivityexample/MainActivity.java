package com.mrgames13.jimdo.drawingactivityexample;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.mrgames13.jimdo.drawingactivity.DrawingActivity;
import com.mrgames13.jimdo.drawingactivity.DrawingActivityBuilder;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    //Constants
    private final int REQ_DRAWING = 10001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button open_drawing_activity = findViewById(R.id.open_drawing_activity);
        open_drawing_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawingActivityBuilder.getInstance(MainActivity.this)
                        .draw(REQ_DRAWING);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_DRAWING && resultCode == RESULT_OK && data != null) {
            String drawing_path = data.getStringExtra(DrawingActivity.DRAWING_PATH);
            Toast.makeText(this, drawing_path, Toast.LENGTH_LONG).show();
        }
    }
}
