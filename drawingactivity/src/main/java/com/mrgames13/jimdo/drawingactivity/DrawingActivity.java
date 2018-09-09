package com.mrgames13.jimdo.drawingactivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.raed.drawingview.BrushView;
import com.raed.drawingview.DrawingView;
import com.raed.drawingview.brushes.BrushSettings;
import com.raed.drawingview.brushes.Brushes;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;

import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;

public class DrawingActivity extends AppCompatActivity {

    //Constants
    private final int IMAGE_COMPRESSION_QUALITY = 85;
    private final int REQ_WRITE_EXTERNAL_STORAGE = 1;
    public static final String DRAWING_PATH = "DrawingPath";
    public static final int UTILITIY_PENCIL = 1;
    public static final int UTILITIY_ERASER = 2;
    public static final int UTILITIY_AIR_BRUSH = 3;
    public static final int UTILITIY_CALLIGRAPHY = 4;
    public static final int UTILITIY_PEN = 5;

    //Variables as objects
    private Resources res;
    private Toolbar toolbar;
    private Menu menu;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private DrawingView drawing_view;
    private ImageView color_preview;
    private TextView current_utility;
    private TextView current_size;
    private ImageView arrow;
    private BrushView brush_view;
    private ImageView color_preview_slide;
    private Button choose_color;
    private SeekBar size;
    private RadioButton pencil;
    private RadioButton eraser;
    private RadioButton airbrush;
    private RadioButton calligraphy;
    private RadioButton pen;
    private RadioButton background_color;
    private RadioButton background_image;
    private ImageView background_color_preview;
    private Button choose_background_color;
    private ImageView background_image_preview;
    private Button choose_background_image;
    private Button clear_image;
    private ColorPickerDialog color_picker;
    private int current_color = Color.BLACK;
    private int current_background_color = Color.parseColor("#eeeeee");

    //Variables
    private boolean pressedOnce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);

        //Get calling intent
        Intent i = getIntent();

        //Initialize Resources
        res = getResources();

        //Initialize Toolbar
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle((i.hasExtra(DrawingActivityBuilder.TITLE) && !i.getStringExtra(DrawingActivityBuilder.TITLE).equals("")) ? i.getStringExtra(DrawingActivityBuilder.TITLE) : getString(R.string.drawing));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if(Build.VERSION.SDK_INT >= 21) getWindow().setStatusBarColor(darkenColor(res.getColor(R.color.colorPrimary)));

        //Initialize SlidingUpPanel
        slidingUpPanelLayout = findViewById(R.id.slidingLayout);
        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                arrow.setRotation(slideOffset * 180.0f);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {}
        });

        //Initialize DrawingView
        drawing_view = findViewById(R.id.drawing_view);
        drawing_view.setUndoAndRedoEnable(true);
        drawing_view.getBrushSettings().setSelectedBrushSize(0.25f);
        drawing_view.setOnDrawListener(new DrawingView.OnDrawListener() {
            @Override
            public void onDraw() {
                menu.findItem(R.id.action_undo).setEnabled(true);
                menu.findItem(R.id.action_undo).getIcon().setAlpha(255);
                menu.findItem(R.id.action_redo).setEnabled(false);
                menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                menu.findItem(R.id.action_done).setEnabled(true);
                menu.findItem(R.id.action_done).getIcon().setAlpha(255);
            }
        });
        drawing_view.clear();

        //Initialize Preview
        color_preview = findViewById(R.id.color_preview);
        current_utility = findViewById(R.id.current_utility);
        current_utility.setText(getString(R.string.current_utility_) + " " + getString(R.string.pen));
        current_size = findViewById(R.id.current_size);
        current_size.setText(getString(R.string.current_size_) + " 25%");
        arrow = findViewById(R.id.arrow);
        arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                slidingUpPanelLayout.setPanelState(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED ? SlidingUpPanelLayout.PanelState.COLLAPSED : SlidingUpPanelLayout.PanelState.EXPANDED);
            }
        });

        //Initialize BrushView
        brush_view = findViewById(R.id.brush_view);
        brush_view.setDrawingView(drawing_view);

        final BrushSettings settings = drawing_view.getBrushSettings();

        color_preview_slide = findViewById(R.id.color_preview_slide);
        choose_color = findViewById(R.id.choose_color);
        choose_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker = new ColorPickerDialog(DrawingActivity.this, current_color);
                color_picker.setAlphaSliderVisible(false);
                color_picker.setHexValueEnabled(true);
                color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_color = color;
                        color_preview.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        color_preview_slide.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        settings.setColor(color);
                    }
                });
                color_picker.show();
            }
        });
        size = findViewById(R.id.size);
        size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                settings.setSelectedBrushSize(i / 100.0f);
                current_size.setText(res.getString(R.string.current_size_) + " " + String.valueOf(i) + "%");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        pencil = findViewById(R.id.utility_pencil);
        pencil.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    settings.setSelectedBrush(Brushes.PENCIL);
                    settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                    current_utility.setText(getString(R.string.pencil));
                }
            }
        });
        eraser = findViewById(R.id.utility_eraser);
        eraser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    settings.setSelectedBrush(Brushes.ERASER);
                    settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                    current_utility.setText(getString(R.string.eraser));
                }
            }
        });
        airbrush = findViewById(R.id.utility_airbrush);
        airbrush.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    settings.setSelectedBrush(Brushes.AIR_BRUSH);
                    settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                    current_utility.setText(getString(R.string.air_brush));
                }
            }
        });
        calligraphy = findViewById(R.id.utility_calligraphy);
        calligraphy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    settings.setSelectedBrush(Brushes.CALLIGRAPHY);
                    settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                    current_utility.setText(getString(R.string.calligraphy));
                }
            }
        });
        pen = findViewById(R.id.utility_pen);
        pen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    settings.setSelectedBrush(Brushes.PEN);
                    settings.setSelectedBrushSize(size.getProgress() / 100.0f);
                    current_utility.setText(getString(R.string.pen));
                }
            }
        });

        if(getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, DrawingActivity.UTILITIY_PENCIL) == DrawingActivity.UTILITIY_ERASER) eraser.setChecked(true);
        if(getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, DrawingActivity.UTILITIY_PENCIL) == DrawingActivity.UTILITIY_AIR_BRUSH) airbrush.setChecked(true);
        if(getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, DrawingActivity.UTILITIY_PENCIL) == DrawingActivity.UTILITIY_CALLIGRAPHY) calligraphy.setChecked(true);
        if(getIntent().getIntExtra(DrawingActivityBuilder.DEFAULT_UTILITY, DrawingActivity.UTILITIY_PENCIL) == DrawingActivity.UTILITIY_PEN) pen.setChecked(true);

        //Background
        background_color = findViewById(R.id.background_color);
        background_color_preview = findViewById(R.id.background_color_preview);
        choose_background_color = findViewById(R.id.choose_background_color);
        background_image = findViewById(R.id.background_image);
        background_image_preview = findViewById(R.id.background_image_preview);
        choose_background_image = findViewById(R.id.choose_background_image);
        background_color.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                background_color_preview.setEnabled(b);
                choose_background_color.setEnabled(b);
            }
        });
        background_image.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                background_image_preview.setEnabled(b);
                choose_background_image.setEnabled(b);
            }
        });
        choose_background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                color_picker = new ColorPickerDialog(DrawingActivity.this, current_background_color);
                color_picker.setAlphaSliderVisible(false);
                color_picker.setHexValueEnabled(true);
                color_picker.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
                    @Override
                    public void onColorChanged(int color) {
                        current_background_color = color;
                        background_color_preview.setColorFilter(color, android.graphics.PorterDuff.Mode.SRC_IN);
                        drawing_view.setDrawingBackground(color);
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                });
                color_picker.show();
            }
        });
        choose_background_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(DrawingActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    chooseBackgroundImage();
                } else {
                    ActivityCompat.requestPermissions(DrawingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQ_WRITE_EXTERNAL_STORAGE);
                }
            }
        });

        //Clear image
        clear_image = findViewById(R.id.clear);
        clear_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clear();
            }
        });

        //Show toast
        if(i.hasExtra(DrawingActivityBuilder.TOAST_ENABLED)) {
            if(i.getBooleanExtra(DrawingActivityBuilder.TOAST_ENABLED, true)) {
                Toast toast = Toast.makeText(this,getString(R.string.drawing_instructions), Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        } else {
            Toast toast = Toast.makeText(this,getString(R.string.drawing_instructions), Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.menu_drawing, menu);
        menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
        menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
        menu.findItem(R.id.action_done).getIcon().setAlpha(130);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finishWarning();
        } else if(id == R.id.action_done) {
            Bitmap b = drawing_view.exportDrawing();

            File file = new File(getCacheDir(), "drawing");
            if (file.exists()) file.delete();
            try {
                FileOutputStream out = new FileOutputStream(file);
                b.compress(Bitmap.CompressFormat.JPEG, IMAGE_COMPRESSION_QUALITY, out);
                out.flush();
                out.close();
            } catch (Exception e) {}

            Intent i = new Intent();
            i.putExtra(DRAWING_PATH, file.getAbsolutePath());

            setResult(RESULT_OK, i);
            finish();
        } else if(id == R.id.action_undo) {
            drawing_view.undo();
            menu.findItem(R.id.action_undo).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_undo).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_redo).setEnabled(!drawing_view.isRedoStackEmpty());
            menu.findItem(R.id.action_redo).getIcon().setAlpha(drawing_view.isRedoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_done).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_done).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
        } else if(id == R.id.action_redo) {
            drawing_view.redo();
            menu.findItem(R.id.action_undo).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_undo).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_redo).setEnabled(!drawing_view.isRedoStackEmpty());
            menu.findItem(R.id.action_redo).getIcon().setAlpha(drawing_view.isRedoStackEmpty() ? 130 : 255);
            menu.findItem(R.id.action_done).setEnabled(!drawing_view.isUndoStackEmpty());
            menu.findItem(R.id.action_done).getIcon().setAlpha(drawing_view.isUndoStackEmpty() ? 130 : 255);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING) {
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            } else {
                finishWarning();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void clear() {
        AlertDialog d = new AlertDialog.Builder(this)
                .setTitle(R.string.drawing)
                .setMessage(R.string.warning_clear)
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        drawing_view.clear();
                        menu.findItem(R.id.action_undo).setEnabled(false);
                        menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
                        menu.findItem(R.id.action_redo).setEnabled(false);
                        menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                        menu.findItem(R.id.action_done).setEnabled(false);
                        menu.findItem(R.id.action_done).getIcon().setAlpha(130);
                        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    }
                })
                .create();
        d.show();
    }

    private void chooseBackgroundImage() {
        FilePickerBuilder.getInstance()
                .setMaxCount(1)
                .enableCameraSupport(true)
                .enableVideoPicker(false)
                .pickPhoto(DrawingActivity.this);
    }

    private void finishWarning() {
        if(!drawing_view.isUndoStackEmpty()) {
            if (!pressedOnce) {
                pressedOnce = true;
                Toast.makeText(DrawingActivity.this, R.string.press_again_to_discard_drawing, Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pressedOnce = false;
                    }
                }, 2500);
            } else {
                pressedOnce = false;
                onBackPressed();
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == FilePickerConst.REQUEST_CODE_PHOTO && resultCode == RESULT_OK && data != null) {
            final ArrayList<String> paths = data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA);
            final Bitmap b = loadImageFromPath(paths.get(0));

            AlertDialog d = new AlertDialog.Builder(this)
                    .setTitle(R.string.drawing)
                    .setMessage(R.string.warning_background_image)
                    .setNegativeButton(R.string.cancel, null)
                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            drawing_view.setBackgroundImage(b);
                            background_image_preview.setImageBitmap(b);

                            menu.findItem(R.id.action_undo).setEnabled(false);
                            menu.findItem(R.id.action_undo).getIcon().setAlpha(130);
                            menu.findItem(R.id.action_redo).setEnabled(false);
                            menu.findItem(R.id.action_redo).getIcon().setAlpha(130);
                            menu.findItem(R.id.action_done).setEnabled(false);
                            menu.findItem(R.id.action_done).getIcon().setAlpha(130);

                            slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                        }
                    })
                    .create();
            d.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQ_WRITE_EXTERNAL_STORAGE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) chooseBackgroundImage();
    }

    private Bitmap loadImageFromPath(String path) {
        Bitmap b = null;
        try{
            InputStream in = new FileInputStream(path);
            b = BitmapFactory.decodeStream(in);
        } catch (Exception e) {}
        return b;
    }

    private int darkenColor(int color) {
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        hsv[2] *= 0.8f;
        return Color.HSVToColor(hsv);
    }

    public static DrawingActivity getInstance() {
        return new DrawingActivity();
    }
}
