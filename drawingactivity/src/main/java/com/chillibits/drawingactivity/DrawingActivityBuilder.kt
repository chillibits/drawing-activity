/*
 * Copyright © Marc Auberer 2021. All rights reserved
 */

package com.chillibits.drawingactivity

import android.app.Activity
import android.content.Intent
import androidx.annotation.StringRes

class DrawingActivityBuilder private constructor(private val activity: Activity) {
    private val intent: Intent = Intent(activity, DrawingActivity::class.java)

    /**
     * Enables a toast with the caption 'Draw within the white area'.
     *
     * @param enabled Choose whether the toast is enabled or not
     */
    fun enableToast(enabled: Boolean): DrawingActivityBuilder {
        intent.putExtra(TOAST_ENABLED, enabled)
        return this
    }

    /**
     * Sets the toolbar title of the DrawingActivity.
     *
     * @param title Title string
     */
    fun setTitle(title: String?): DrawingActivityBuilder {
        intent.putExtra(TITLE, title)
        return this
    }

    /**
     * Sets the toolbar title of the DrawingActivity.
     *
     * @param titleResourceId Title string resource id
     */
    fun setTitle(@StringRes titleResourceId: Int): DrawingActivityBuilder {
        intent.putExtra(TITLE, activity.getString(titleResourceId))
        return this
    }

    /**
     * Sets the utility at activity launch time.
     *
     * @param defaultUtility One of the following: DrawingActivity.UTILITIY_PEN,
     * DrawingActivity.UTILITIY_AIR_BRUSH, DrawingActivity.UTILITIY_CALLIGRAPHY,
     * DrawingActivity.UTILITIY_ERASER, DrawingActivity.UTILITIY_PENCIL
     */
    fun setDefaultUtility(defaultUtility: Int): DrawingActivityBuilder {
        intent.putExtra(DEFAULT_UTILITY, defaultUtility)
        return this
    }

    /**
     * Launches the DrawingActivity. Let's draw!
     *
     * @param requestCode Request code to retrieve the drawing result in onActivityResult
     */
    fun draw(requestCode: Int) {
        activity.startActivityForResult(intent, requestCode)
    }

    companion object {
        // Constants
        const val TOAST_ENABLED = "ToastEnabled"
        const val TITLE = "Title"
        const val DEFAULT_UTILITY = "DefaultUtility"

        @JvmStatic
        fun getInstance(context: Activity): DrawingActivityBuilder = DrawingActivityBuilder(context)
    }
}