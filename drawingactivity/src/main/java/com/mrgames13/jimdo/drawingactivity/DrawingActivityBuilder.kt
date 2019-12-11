package com.mrgames13.jimdo.drawingactivity

import android.app.Activity
import android.content.Intent
import com.mrgames13.jimdo.drawingactivity.DrawingActivity

class DrawingActivityBuilder private constructor(private val context: Activity) {
    private val intent: Intent = Intent(context, DrawingActivity::class.java)

    fun enableToast(enabled: Boolean): DrawingActivityBuilder {
        intent.putExtra(TOAST_ENABLED, enabled)
        return this
    }

    fun setTitle(title: String?): DrawingActivityBuilder {
        intent.putExtra(TITLE, title)
        return this
    }

    fun setDefaultUtility(defaultUtility: Int): DrawingActivityBuilder {
        intent.putExtra(DEFAULT_UTILITY, defaultUtility)
        return this
    }

    fun draw(requestCode: Int) {
        context.startActivityForResult(intent, requestCode)
    }

    companion object {
        // Constants
        const val TOAST_ENABLED = "ToastEnabled"
        const val TITLE = "Title"
        const val DEFAULT_UTILITY = "DefaultUtility"
        fun getInstance(context: Activity): DrawingActivityBuilder = DrawingActivityBuilder(context)
    }
}