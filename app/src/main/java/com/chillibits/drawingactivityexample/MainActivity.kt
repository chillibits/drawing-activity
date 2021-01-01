/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.drawingactivityexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chillibits.drawingactivity.DrawingActivity
import com.chillibits.drawingactivity.DrawingActivityBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Constants
    private val REQ_DRAWING = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        openDrawingActivity.setOnClickListener {
            DrawingActivityBuilder.getInstance(this@MainActivity)
                .enableToast(true)
                .setTitle(R.string.drawing)
                .setDefaultUtility(DrawingActivity.UTILITY_PEN)
                .draw(REQ_DRAWING)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_DRAWING && resultCode == Activity.RESULT_OK)
            Toast.makeText(this, data?.getStringExtra(DrawingActivity.DRAWING_PATH), Toast.LENGTH_LONG).show()
    }
}