/*
 * Copyright © Marc Auberer 2020. All rights reserved
 */

package com.chillibits.drawingactivity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.view.*
import android.widget.SeekBar
import android.widget.SeekBar.OnSeekBarChangeListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.raed.drawingview.brushes.Brushes
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import droidninja.filepicker.FilePickerBuilder
import droidninja.filepicker.FilePickerConst.KEY_SELECTED_MEDIA
import droidninja.filepicker.FilePickerConst.REQUEST_CODE_PHOTO
import kotlinx.android.synthetic.main.activity_drawing.*
import kotlinx.android.synthetic.main.toolbar.*
import net.margaritov.preference.colorpicker.ColorPickerDialog
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class DrawingActivity : AppCompatActivity() {

    // Variables as objects
    private var menu: Menu? = null
    private var currentColor = Color.BLACK
    private var currentBackgroundColor = Color.parseColor("#EEEEEE")

    // Variables
    private var pressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_drawing)

        //Initialize Toolbar
        toolbar.title = if (intent.hasExtra(DrawingActivityBuilder.TITLE) && intent.getStringExtra(DrawingActivityBuilder.TITLE) != "")
            intent.getStringExtra(DrawingActivityBuilder.TITLE)
        else getString(R.string.drawing)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (Build.VERSION.SDK_INT >= 21) window.statusBarColor = darkenColor(ContextCompat.getColor(this, R.color.colorPrimary))

        //Initialize SlidingUpPanel
        slidingLayout.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View, slideOffset: Float) {
                arrow.rotation = slideOffset * 180.0f
            }

            override fun onPanelStateChanged(panel: View, previousState: PanelState, newState: PanelState) {}
        })

        //Initialize DrawingView
        drawing_view.setUndoAndRedoEnable(true)
        drawing_view.brushSettings.selectedBrushSize = 0.25f
        drawing_view.setOnDrawListener {
            menu?.findItem(R.id.action_undo)?.isEnabled = true
            menu?.findItem(R.id.action_undo)?.icon?.alpha = 255
            menu?.findItem(R.id.action_redo)?.isEnabled = false
            menu?.findItem(R.id.action_redo)?.icon?.alpha = 130
            menu?.findItem(R.id.action_done)?.isEnabled = true
            menu?.findItem(R.id.action_done)?.icon?.alpha = 255
        }
        drawing_view.clear()

        //Initialize Preview
        current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.pen))
        current_size.text = String.format(getString(R.string.current_size), 25)
        arrow.setOnClickListener {
            slidingLayout.panelState = if (slidingLayout.panelState == PanelState.EXPANDED) PanelState.COLLAPSED else PanelState.EXPANDED
        }

        //Initialize BrushView
        brush_view.setDrawingView(drawing_view)
        val settings = drawing_view.brushSettings
        choose_color.setOnClickListener {
            val colorPicker = ColorPickerDialog(this@DrawingActivity, currentColor)
            colorPicker.alphaSliderVisible = false
            colorPicker.hexValueEnabled = true
            colorPicker.setOnColorChangedListener { color ->
                currentColor = color
                color_preview.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                color_preview_slide.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                settings.color = color
            }
            colorPicker.show()
        }
        size.setOnSeekBarChangeListener(object : OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, i: Int, b: Boolean) {
                settings.selectedBrushSize = i / 100.0f
                current_size.text = String.format(getString(R.string.current_size), i)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
        utility_pencil.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.selectedBrush = Brushes.PENCIL
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.pencil))
            }
        }
        utility_eraser.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.selectedBrush = Brushes.ERASER
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.eraser))
            }
        }
        utility_airbrush.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.selectedBrush = Brushes.AIR_BRUSH
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.air_brush))
            }
        }
        utility_calligraphy.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.selectedBrush = Brushes.CALLIGRAPHY
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.calligraphy))
            }
        }
        utility_pen.setOnCheckedChangeListener { _, b ->
            if (b) {
                settings.selectedBrush = Brushes.PEN
                settings.selectedBrushSize = size.progress / 100.0f
                current_utility.text = String.format(getString(R.string.current_utility), getString(R.string.pen))
            }
        }
        if (intent.getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, UTILITY_PENCIL) == UTILITY_ERASER) utility_eraser.isChecked = true
        if (intent.getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, UTILITY_PENCIL) == UTILITY_AIR_BRUSH) utility_airbrush.isChecked = true
        if (intent.getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, UTILITY_PENCIL) == UTILITY_CALLIGRAPHY) utility_calligraphy.isChecked = true
        if (intent.getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, UTILITY_PENCIL) == UTILITY_PEN) utility_pen.isChecked = true

        //Background
        background_color.setOnCheckedChangeListener { _, b ->
            background_color_preview.isEnabled = b
            choose_background_color.isEnabled = b
        }
        background_image.setOnCheckedChangeListener { _, b ->
            background_image_preview.isEnabled = b
            choose_background_image.isEnabled = b
        }
        choose_background_color.setOnClickListener {
            ColorPickerDialog(this@DrawingActivity, currentBackgroundColor).run {
                alphaSliderVisible = false
                hexValueEnabled = true
                setOnColorChangedListener { color ->
                    currentBackgroundColor = color
                    background_color_preview.setColorFilter(color, PorterDuff.Mode.SRC_IN)
                    drawing_view.drawingBackground = color
                    slidingLayout.panelState = PanelState.COLLAPSED
                }
                show()
            }
        }
        choose_background_image.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this@DrawingActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                chooseBackgroundImage()
            } else {
                ActivityCompat.requestPermissions(this@DrawingActivity, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQ_WRITE_EXTERNAL_STORAGE)
            }
        }
        //Clear image
        clear.setOnClickListener { clearDrawing(true) }
        //Show toast
        if (intent.hasExtra(DrawingActivityBuilder.TOAST_ENABLED)) {
            if (intent.getBooleanExtra(DrawingActivityBuilder.TOAST_ENABLED, true)) {
                Toast.makeText(this, getString(R.string.drawing_instructions), Toast.LENGTH_SHORT).run {
                    setGravity(Gravity.CENTER, 0, 0)
                    show()
                }
            }
        } else {
            Toast.makeText(this, getString(R.string.drawing_instructions), Toast.LENGTH_SHORT).run {
                setGravity(Gravity.CENTER, 0, 0)
                show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        this.menu = menu
        menu.run {
            menuInflater.inflate(R.menu.menu_drawing, this)
            findItem(R.id.action_undo).icon.alpha = 130
            findItem(R.id.action_redo).icon.alpha = 130
            findItem(R.id.action_done).icon.alpha = 130
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finishWarning()
            R.id.action_done -> {
                val b = drawing_view.exportDrawing()
                val file = File(cacheDir, "drawing")
                if (file.exists()) file.delete()
                try {
                    FileOutputStream(file).run {
                        b.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, this)
                        flush()
                        close()
                    }
                } catch (ignored: Exception) {}
                Intent().run {
                    putExtra(DRAWING_PATH, file.absolutePath)
                    setResult(Activity.RESULT_OK, this)
                }
                finish()
            }
            R.id.action_undo -> {
                drawing_view.undo()
                updateToolbarButtonStates()
            }
            R.id.action_redo -> {
                drawing_view.redo()
                updateToolbarButtonStates()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun updateToolbarButtonStates() {
        menu?.run {
            findItem(R.id.action_undo)?.run {
                isEnabled = !drawing_view.isUndoStackEmpty
                icon?.alpha = if (drawing_view.isUndoStackEmpty) 130 else 255
            }
            findItem(R.id.action_redo)?.run {
                isEnabled = !drawing_view.isRedoStackEmpty
                icon?.alpha = if (drawing_view.isRedoStackEmpty) 130 else 255
            }
            findItem(R.id.action_done)?.run {
                isEnabled = !drawing_view.isUndoStackEmpty
                icon?.alpha = if (drawing_view.isUndoStackEmpty) 130 else 255
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (slidingLayout.panelState == PanelState.EXPANDED || slidingLayout.panelState == PanelState.DRAGGING) {
                slidingLayout.panelState = PanelState.COLLAPSED
            } else {
                finishWarning()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Clears the contents of the drawing. You can optionally show a warning to the user
     *
     * @param warning Show a warning to the user before clearing the drawing
     */
    fun clearDrawing(warning: Boolean) {
        if (warning) {
            AlertDialog.Builder(this)
                .setTitle(R.string.drawing)
                .setMessage(R.string.warning_clear)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes) { _, _ -> clear() }
                .show()
        } else clear()
    }

    private fun clear() {
        drawing_view.clear()
        updateToolbarButtonStates()
        slidingLayout.panelState = PanelState.COLLAPSED
    }

    private fun chooseBackgroundImage() {
        FilePickerBuilder.instance
            .setMaxCount(1)
            .enableCameraSupport(true)
            .enableVideoPicker(false)
            .pickPhoto(this)
    }

    private fun finishWarning() {
        if (!drawing_view.isUndoStackEmpty) {
            if (!pressedOnce) {
                pressedOnce = true
                Toast.makeText(this, R.string.press_again_to_discard_drawing, Toast.LENGTH_SHORT).show()
                Handler().postDelayed({ pressedOnce = false }, 2500)
            } else {
                pressedOnce = false
                onBackPressed()
            }
        } else {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null) {
            val paths = data.getStringArrayListExtra(KEY_SELECTED_MEDIA)!!
            val b = loadImageFromPath(paths[0])
            if(b != null) {
                AlertDialog.Builder(this)
                    .setTitle(R.string.drawing)
                    .setMessage(R.string.warning_background_image)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes) { _, _ ->
                        drawing_view.setBackgroundImage(b)
                        background_image_preview.setImageBitmap(b)
                        menu?.findItem(R.id.action_undo)?.isEnabled = false
                        menu?.findItem(R.id.action_undo)?.icon?.alpha = 130
                        menu?.findItem(R.id.action_redo)?.isEnabled = false
                        menu?.findItem(R.id.action_redo)?.icon?.alpha = 130
                        menu?.findItem(R.id.action_done)?.isEnabled = false
                        menu?.findItem(R.id.action_done)?.icon?.alpha = 130
                        slidingLayout.panelState = PanelState.COLLAPSED
                    }
                    .show()
            } else {
                Toast.makeText(this, R.string.error_loading_image, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQ_WRITE_EXTERNAL_STORAGE && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) chooseBackgroundImage()
    }

    private fun loadImageFromPath(path: String): Bitmap? {
        return try {
            return BitmapFactory.decodeStream(FileInputStream(path))
        } catch (ignored: Exception) {
            null
        }
    }

    private fun darkenColor(color: Int): Int {
        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        hsv[2] *= 0.8f
        return Color.HSVToColor(hsv)
    }

    companion object {
        const val DRAWING_PATH = "DrawingPath"
        private const val IMAGE_COMPRESSION_QUALITY = 85
        private const val REQ_WRITE_EXTERNAL_STORAGE = 1
        const val UTILITY_PENCIL = 1
        const val UTILITY_ERASER = 2
        const val UTILITY_AIR_BRUSH = 3
        const val UTILITY_CALLIGRAPHY = 4
        const val UTILITY_PEN = 5
        val instance = DrawingActivity()
    }
}