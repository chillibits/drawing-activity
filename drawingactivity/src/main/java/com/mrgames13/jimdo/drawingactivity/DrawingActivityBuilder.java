package com.mrgames13.jimdo.drawingactivity;

import android.app.Activity;
import android.content.Intent;

public class DrawingActivityBuilder {

    // Constants
    static final String TOAST_ENABLED = "ToastEnabled";
    static final String TITLE = "Title";
    static final String DEFAULT_UTILITY = "DefaultUtility";

    // Variables as objects
    private Activity context;
    private Intent intent;

    private DrawingActivityBuilder(Activity context) {
        this.context = context;
        this.intent = new Intent(context, DrawingActivity.class);
    }

    public static DrawingActivityBuilder getInstance(Activity context) {
        return new DrawingActivityBuilder(context);
    }

    public DrawingActivityBuilder enableToast(boolean enabled) {
        intent.putExtra(TOAST_ENABLED, enabled);
        return this;
    }

    public DrawingActivityBuilder setTitle(String title) {
        intent.putExtra(TITLE, title);
        return this;
    }

    public DrawingActivityBuilder setDefaultUtility(int defaultUtility) {
        intent.putExtra(DEFAULT_UTILITY, defaultUtility);
        return this;
    }

    public void draw(int requestCode) {
        context.startActivityForResult(intent, requestCode);
    }
}