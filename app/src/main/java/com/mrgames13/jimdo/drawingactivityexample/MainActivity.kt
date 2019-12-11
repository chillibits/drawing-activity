package com.mrgames13.jimdo.drawingactivityexample

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mrgames13.jimdo.drawingactivity.DrawingActivity
import com.mrgames13.jimdo.drawingactivity.DrawingActivityBuilder
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    // Constants
    private val REQ_DRAWING = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        open_drawing_activity.setOnClickListener {
            DrawingActivityBuilder.getInstance(this@MainActivity).draw(REQ_DRAWING)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_DRAWING && resultCode == Activity.RESULT_OK)
            Toast.makeText(this, data?.getStringExtra(DrawingActivity.DRAWING_PATH), Toast.LENGTH_LONG).show()
    }
}