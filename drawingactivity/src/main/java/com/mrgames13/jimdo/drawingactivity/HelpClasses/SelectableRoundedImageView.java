package com.mrgames13.jimdo.drawingactivity.HelpClasses;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Matrix.ScaleToFit;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.mrgames13.jimdo.drawingactivity.R.styleable;

public class SelectableRoundedImageView extends AppCompatImageView {
    public static final String TAG = "SelectableRoundedImageView";
    private int mResource;
    private static final ImageView.ScaleType[] sScaleTypeArray;
    private ImageView.ScaleType mScaleType;
    private float mLeftTopCornerRadius;
    private float mRightTopCornerRadius;
    private float mLeftBottomCornerRadius;
    private float mRightBottomCornerRadius;
    private float mBorderWidth;
    private static final int DEFAULT_BORDER_COLOR = -16777216;
    private ColorStateList mBorderColor;
    private boolean isOval;
    private Drawable mDrawable;
    private float[] mRadii;

    public SelectableRoundedImageView(Context context) {
        super(context);
        this.mResource = 0;
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mLeftTopCornerRadius = 0.0F;
        this.mRightTopCornerRadius = 0.0F;
        this.mLeftBottomCornerRadius = 0.0F;
        this.mRightBottomCornerRadius = 0.0F;
        this.mBorderWidth = 0.0F;
        this.mBorderColor = ColorStateList.valueOf(-16777216);
        this.isOval = false;
        this.mRadii = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
    }

    public SelectableRoundedImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SelectableRoundedImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mResource = 0;
        this.mScaleType = ImageView.ScaleType.FIT_CENTER;
        this.mLeftTopCornerRadius = 0.0F;
        this.mRightTopCornerRadius = 0.0F;
        this.mLeftBottomCornerRadius = 0.0F;
        this.mRightBottomCornerRadius = 0.0F;
        this.mBorderWidth = 0.0F;
        this.mBorderColor = ColorStateList.valueOf(-16777216);
        this.isOval = false;
        this.mRadii = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
        TypedArray a = context.obtainStyledAttributes(attrs, styleable.SelectableRoundedImageView, defStyle, 0);
        int index = a.getInt(styleable.SelectableRoundedImageView_android_scaleType, -1);
        if (index >= 0) {
            this.setScaleType(sScaleTypeArray[index]);
        }

        this.mLeftTopCornerRadius = (float)a.getDimensionPixelSize(styleable.SelectableRoundedImageView_sriv_left_top_corner_radius, 0);
        this.mRightTopCornerRadius = (float)a.getDimensionPixelSize(styleable.SelectableRoundedImageView_sriv_right_top_corner_radius, 0);
        this.mLeftBottomCornerRadius = (float)a.getDimensionPixelSize(styleable.SelectableRoundedImageView_sriv_left_bottom_corner_radius, 0);
        this.mRightBottomCornerRadius = (float)a.getDimensionPixelSize(styleable.SelectableRoundedImageView_sriv_right_bottom_corner_radius, 0);
        if (this.mLeftTopCornerRadius >= 0.0F && this.mRightTopCornerRadius >= 0.0F && this.mLeftBottomCornerRadius >= 0.0F && this.mRightBottomCornerRadius >= 0.0F) {
            this.mRadii = new float[]{this.mLeftTopCornerRadius, this.mLeftTopCornerRadius, this.mRightTopCornerRadius, this.mRightTopCornerRadius, this.mRightBottomCornerRadius, this.mRightBottomCornerRadius, this.mLeftBottomCornerRadius, this.mLeftBottomCornerRadius};
            this.mBorderWidth = (float)a.getDimensionPixelSize(styleable.SelectableRoundedImageView_sriv_border_width, 0);
            if (this.mBorderWidth < 0.0F) {
                throw new IllegalArgumentException("border width cannot be negative.");
            } else {
                this.mBorderColor = a.getColorStateList(styleable.SelectableRoundedImageView_sriv_border_color);
                if (this.mBorderColor == null) {
                    this.mBorderColor = ColorStateList.valueOf(-16777216);
                }

                this.isOval = a.getBoolean(styleable.SelectableRoundedImageView_sriv_oval, false);
                a.recycle();
                this.updateDrawable();
            }
        } else {
            throw new IllegalArgumentException("radius values cannot be negative.");
        }
    }

    protected void drawableStateChanged() {
        super.drawableStateChanged();
        this.invalidate();
    }

    public ImageView.ScaleType getScaleType() {
        return this.mScaleType;
    }

    public void setScaleType(ImageView.ScaleType scaleType) {
        super.setScaleType(scaleType);
        this.mScaleType = scaleType;
        this.updateDrawable();
    }

    public void setImageDrawable(Drawable drawable) {
        this.mResource = 0;
        this.mDrawable = SelectableRoundedImageView.SelectableRoundedCornerDrawable.fromDrawable(drawable, this.getResources());
        super.setImageDrawable(this.mDrawable);
        this.updateDrawable();
    }

    public void setImageBitmap(Bitmap bm) {
        this.mResource = 0;
        this.mDrawable = SelectableRoundedImageView.SelectableRoundedCornerDrawable.fromBitmap(bm, this.getResources());
        super.setImageDrawable(this.mDrawable);
        this.updateDrawable();
    }

    public void setImageResource(int resId) {
        if (this.mResource != resId) {
            this.mResource = resId;
            this.mDrawable = this.resolveResource();
            super.setImageDrawable(this.mDrawable);
            this.updateDrawable();
        }

    }

    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        this.setImageDrawable(this.getDrawable());
    }

    private Drawable resolveResource() {
        Resources rsrc = this.getResources();
        if (rsrc == null) {
            return null;
        } else {
            Drawable d = null;
            if (this.mResource != 0) {
                try {
                    d = rsrc.getDrawable(this.mResource);
                } catch (NotFoundException var4) {
                    Log.w("SRIV", "Unable to find resource: " + this.mResource, var4);
                    this.mResource = 0;
                }
            }

            return SelectableRoundedImageView.SelectableRoundedCornerDrawable.fromDrawable(d, this.getResources());
        }
    }

    private void updateDrawable() {
        if (this.mDrawable != null) {
            ((SelectableRoundedImageView.SelectableRoundedCornerDrawable)this.mDrawable).setScaleType(this.mScaleType);
            ((SelectableRoundedImageView.SelectableRoundedCornerDrawable)this.mDrawable).setCornerRadii(this.mRadii);
            ((SelectableRoundedImageView.SelectableRoundedCornerDrawable)this.mDrawable).setBorderWidth(this.mBorderWidth);
            ((SelectableRoundedImageView.SelectableRoundedCornerDrawable)this.mDrawable).setBorderColor(this.mBorderColor);
            ((SelectableRoundedImageView.SelectableRoundedCornerDrawable)this.mDrawable).setOval(this.isOval);
        }
    }

    public float getCornerRadius() {
        return this.mLeftTopCornerRadius;
    }

    public void setCornerRadiiDP(float leftTop, float rightTop, float leftBottom, float rightBottom) {
        float density = this.getResources().getDisplayMetrics().density;
        float lt = leftTop * density;
        float rt = rightTop * density;
        float lb = leftBottom * density;
        float rb = rightBottom * density;
        this.mRadii = new float[]{lt, lt, rt, rt, rb, rb, lb, lb};
        this.updateDrawable();
    }

    public float getBorderWidth() {
        return this.mBorderWidth;
    }

    public void setBorderWidthDP(float width) {
        float scaledWidth = this.getResources().getDisplayMetrics().density * width;
        if (this.mBorderWidth != scaledWidth) {
            this.mBorderWidth = scaledWidth;
            this.updateDrawable();
            this.invalidate();
        }
    }

    public int getBorderColor() {
        return this.mBorderColor.getDefaultColor();
    }

    public void setBorderColor(int color) {
        this.setBorderColor(ColorStateList.valueOf(color));
    }

    public ColorStateList getBorderColors() {
        return this.mBorderColor;
    }

    public void setBorderColor(ColorStateList colors) {
        if (!this.mBorderColor.equals(colors)) {
            this.mBorderColor = colors != null ? colors : ColorStateList.valueOf(-16777216);
            this.updateDrawable();
            if (this.mBorderWidth > 0.0F) {
                this.invalidate();
            }

        }
    }

    public boolean isOval() {
        return this.isOval;
    }

    public void setOval(boolean oval) {
        this.isOval = oval;
        this.updateDrawable();
        this.invalidate();
    }

    static {
        sScaleTypeArray = new ImageView.ScaleType[]{ImageView.ScaleType.MATRIX, ImageView.ScaleType.FIT_XY, ImageView.ScaleType.FIT_START, ImageView.ScaleType.FIT_CENTER, ImageView.ScaleType.FIT_END, ImageView.ScaleType.CENTER, ImageView.ScaleType.CENTER_CROP, ImageView.ScaleType.CENTER_INSIDE};
    }

    static class SelectableRoundedCornerDrawable extends Drawable {
        private static final String TAG = "SelectableRoundedCornerDrawable";
        private static final int DEFAULT_BORDER_COLOR = -16777216;
        private RectF mBounds = new RectF();
        private RectF mBorderBounds = new RectF();
        private final RectF mBitmapRect = new RectF();
        private final int mBitmapWidth;
        private final int mBitmapHeight;
        private final Paint mBitmapPaint;
        private final Paint mBorderPaint;
        private BitmapShader mBitmapShader;
        private float[] mRadii = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
        private float[] mBorderRadii = new float[]{0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F};
        private boolean mOval = false;
        private float mBorderWidth = 0.0F;
        private ColorStateList mBorderColor = ColorStateList.valueOf(-16777216);
        private ImageView.ScaleType mScaleType;
        private Path mPath;
        private Bitmap mBitmap;
        private boolean mBoundsConfigured;

        public SelectableRoundedCornerDrawable(Bitmap bitmap, Resources r) {
            this.mScaleType = ImageView.ScaleType.FIT_CENTER;
            this.mPath = new Path();
            this.mBoundsConfigured = false;
            this.mBitmap = bitmap;
            this.mBitmapShader = new BitmapShader(bitmap, TileMode.CLAMP, TileMode.CLAMP);
            if (bitmap != null) {
                this.mBitmapWidth = bitmap.getScaledWidth(r.getDisplayMetrics());
                this.mBitmapHeight = bitmap.getScaledHeight(r.getDisplayMetrics());
            } else {
                this.mBitmapWidth = this.mBitmapHeight = -1;
            }

            this.mBitmapRect.set(0.0F, 0.0F, (float)this.mBitmapWidth, (float)this.mBitmapHeight);
            this.mBitmapPaint = new Paint(1);
            this.mBitmapPaint.setStyle(Style.FILL);
            this.mBitmapPaint.setShader(this.mBitmapShader);
            this.mBorderPaint = new Paint(1);
            this.mBorderPaint.setStyle(Style.STROKE);
            this.mBorderPaint.setColor(this.mBorderColor.getColorForState(this.getState(), -16777216));
            this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
        }

        public static SelectableRoundedImageView.SelectableRoundedCornerDrawable fromBitmap(Bitmap bitmap, Resources r) {
            return bitmap != null ? new SelectableRoundedImageView.SelectableRoundedCornerDrawable(bitmap, r) : null;
        }

        public static Drawable fromDrawable(Drawable drawable, Resources r) {
            if (drawable != null) {
                if (drawable instanceof SelectableRoundedImageView.SelectableRoundedCornerDrawable) {
                    return drawable;
                }

                if (drawable instanceof LayerDrawable) {
                    LayerDrawable ld = (LayerDrawable)drawable;
                    int num = ld.getNumberOfLayers();

                    for(int i = 0; i < num; ++i) {
                        Drawable d = ld.getDrawable(i);
                        ld.setDrawableByLayerId(ld.getId(i), fromDrawable(d, r));
                    }

                    return ld;
                }

                Bitmap bm = drawableToBitmap(drawable);
                if (bm != null) {
                    return new SelectableRoundedImageView.SelectableRoundedCornerDrawable(bm, r);
                }

                Log.w("SRIV", "Failed to create bitmap from drawable!");
            }

            return drawable;
        }

        public static Bitmap drawableToBitmap(Drawable drawable) {
            if (drawable == null) {
                return null;
            } else if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable)drawable).getBitmap();
            } else {
                int width = Math.max(drawable.getIntrinsicWidth(), 2);
                int height = Math.max(drawable.getIntrinsicHeight(), 2);

                Bitmap bitmap;
                try {
                    bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
                    Canvas canvas = new Canvas(bitmap);
                    drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    drawable.draw(canvas);
                } catch (IllegalArgumentException var5) {
                    var5.printStackTrace();
                    bitmap = null;
                }

                return bitmap;
            }
        }

        public boolean isStateful() {
            return this.mBorderColor.isStateful();
        }

        protected boolean onStateChange(int[] state) {
            int newColor = this.mBorderColor.getColorForState(state, 0);
            if (this.mBorderPaint.getColor() != newColor) {
                this.mBorderPaint.setColor(newColor);
                return true;
            } else {
                return super.onStateChange(state);
            }
        }

        private void configureBounds(Canvas canvas) {
            Rect clipBounds = canvas.getClipBounds();
            Matrix canvasMatrix = canvas.getMatrix();
            if (ImageView.ScaleType.CENTER == this.mScaleType) {
                this.mBounds.set(clipBounds);
            } else if (ImageView.ScaleType.CENTER_CROP == this.mScaleType) {
                this.applyScaleToRadii(canvasMatrix);
                this.mBounds.set(clipBounds);
            } else if (ImageView.ScaleType.FIT_XY == this.mScaleType) {
                Matrix m = new Matrix();
                m.setRectToRect(this.mBitmapRect, new RectF(clipBounds), ScaleToFit.FILL);
                this.mBitmapShader.setLocalMatrix(m);
                this.mBounds.set(clipBounds);
            } else if (ImageView.ScaleType.FIT_START != this.mScaleType && ImageView.ScaleType.FIT_END != this.mScaleType && ImageView.ScaleType.FIT_CENTER != this.mScaleType && ImageView.ScaleType.CENTER_INSIDE != this.mScaleType) {
                if (ImageView.ScaleType.MATRIX == this.mScaleType) {
                    this.applyScaleToRadii(canvasMatrix);
                    this.mBounds.set(this.mBitmapRect);
                }
            } else {
                this.applyScaleToRadii(canvasMatrix);
                this.mBounds.set(this.mBitmapRect);
            }

        }

        private void applyScaleToRadii(Matrix m) {
            float[] values = new float[9];
            m.getValues(values);

            for(int i = 0; i < this.mRadii.length; ++i) {
                this.mRadii[i] /= values[0];
            }

        }

        private void adjustCanvasForBorder(Canvas canvas) {
            Matrix canvasMatrix = canvas.getMatrix();
            float[] values = new float[9];
            canvasMatrix.getValues(values);
            float scaleFactorX = values[0];
            float scaleFactorY = values[4];
            float translateX = values[2];
            float translateY = values[5];
            float newScaleX = this.mBounds.width() / (this.mBounds.width() + this.mBorderWidth + this.mBorderWidth);
            float newScaleY = this.mBounds.height() / (this.mBounds.height() + this.mBorderWidth + this.mBorderWidth);
            canvas.scale(newScaleX, newScaleY);
            if (ImageView.ScaleType.FIT_START != this.mScaleType && ImageView.ScaleType.FIT_END != this.mScaleType && ImageView.ScaleType.FIT_XY != this.mScaleType && ImageView.ScaleType.FIT_CENTER != this.mScaleType && ImageView.ScaleType.CENTER_INSIDE != this.mScaleType && ImageView.ScaleType.MATRIX != this.mScaleType) {
                if (ImageView.ScaleType.CENTER == this.mScaleType || ImageView.ScaleType.CENTER_CROP == this.mScaleType) {
                    canvas.translate(-translateX / (newScaleX * scaleFactorX), -translateY / (newScaleY * scaleFactorY));
                    canvas.translate(-(this.mBounds.left - this.mBorderWidth), -(this.mBounds.top - this.mBorderWidth));
                }
            } else {
                canvas.translate(this.mBorderWidth, this.mBorderWidth);
            }

        }

        private void adjustBorderWidthAndBorderBounds(Canvas canvas) {
            Matrix canvasMatrix = canvas.getMatrix();
            float[] values = new float[9];
            canvasMatrix.getValues(values);
            float scaleFactor = values[0];
            float viewWidth = this.mBounds.width() * scaleFactor;
            this.mBorderWidth = this.mBorderWidth * this.mBounds.width() / (viewWidth - 2.0F * this.mBorderWidth);
            this.mBorderPaint.setStrokeWidth(this.mBorderWidth);
            this.mBorderBounds.set(this.mBounds);
            this.mBorderBounds.inset(-this.mBorderWidth / 2.0F, -this.mBorderWidth / 2.0F);
        }

        private void setBorderRadii() {
            for(int i = 0; i < this.mRadii.length; ++i) {
                if (this.mRadii[i] > 0.0F) {
                    this.mBorderRadii[i] = this.mRadii[i];
                    this.mRadii[i] -= this.mBorderWidth;
                }
            }

        }

        public void draw(Canvas canvas) {
            canvas.save();
            if (!this.mBoundsConfigured) {
                this.configureBounds(canvas);
                if (this.mBorderWidth > 0.0F) {
                    this.adjustBorderWidthAndBorderBounds(canvas);
                    this.setBorderRadii();
                }

                this.mBoundsConfigured = true;
            }

            if (this.mOval) {
                if (this.mBorderWidth > 0.0F) {
                    this.adjustCanvasForBorder(canvas);
                    this.mPath.addOval(this.mBounds, Direction.CW);
                    canvas.drawPath(this.mPath, this.mBitmapPaint);
                    this.mPath.reset();
                    this.mPath.addOval(this.mBorderBounds, Direction.CW);
                    canvas.drawPath(this.mPath, this.mBorderPaint);
                } else {
                    this.mPath.addOval(this.mBounds, Direction.CW);
                    canvas.drawPath(this.mPath, this.mBitmapPaint);
                }
            } else if (this.mBorderWidth > 0.0F) {
                this.adjustCanvasForBorder(canvas);
                this.mPath.addRoundRect(this.mBounds, this.mRadii, Direction.CW);
                canvas.drawPath(this.mPath, this.mBitmapPaint);
                this.mPath.reset();
                this.mPath.addRoundRect(this.mBorderBounds, this.mBorderRadii, Direction.CW);
                canvas.drawPath(this.mPath, this.mBorderPaint);
            } else {
                this.mPath.addRoundRect(this.mBounds, this.mRadii, Direction.CW);
                canvas.drawPath(this.mPath, this.mBitmapPaint);
            }

            canvas.restore();
        }

        public void setCornerRadii(float[] radii) {
            if (radii != null) {
                if (radii.length != 8) {
                    throw new ArrayIndexOutOfBoundsException("radii[] needs 8 values");
                } else {
                    for(int i = 0; i < radii.length; ++i) {
                        this.mRadii[i] = radii[i];
                    }

                }
            }
        }

        public int getOpacity() {
            return this.mBitmap != null && !this.mBitmap.hasAlpha() && this.mBitmapPaint.getAlpha() >= 255 ? -1 : -3;
        }

        public void setAlpha(int alpha) {
            this.mBitmapPaint.setAlpha(alpha);
            this.invalidateSelf();
        }

        public void setColorFilter(ColorFilter cf) {
            this.mBitmapPaint.setColorFilter(cf);
            this.invalidateSelf();
        }

        public void setDither(boolean dither) {
            this.mBitmapPaint.setDither(dither);
            this.invalidateSelf();
        }

        public void setFilterBitmap(boolean filter) {
            this.mBitmapPaint.setFilterBitmap(filter);
            this.invalidateSelf();
        }

        public int getIntrinsicWidth() {
            return this.mBitmapWidth;
        }

        public int getIntrinsicHeight() {
            return this.mBitmapHeight;
        }

        public float getBorderWidth() {
            return this.mBorderWidth;
        }

        public void setBorderWidth(float width) {
            this.mBorderWidth = width;
            this.mBorderPaint.setStrokeWidth(width);
        }

        public int getBorderColor() {
            return this.mBorderColor.getDefaultColor();
        }

        public void setBorderColor(int color) {
            this.setBorderColor(ColorStateList.valueOf(color));
        }

        public ColorStateList getBorderColors() {
            return this.mBorderColor;
        }

        public void setBorderColor(ColorStateList colors) {
            if (colors == null) {
                this.mBorderWidth = 0.0F;
                this.mBorderColor = ColorStateList.valueOf(0);
                this.mBorderPaint.setColor(0);
            } else {
                this.mBorderColor = colors;
                this.mBorderPaint.setColor(this.mBorderColor.getColorForState(this.getState(), -16777216));
            }

        }

        public boolean isOval() {
            return this.mOval;
        }

        public void setOval(boolean oval) {
            this.mOval = oval;
        }

        public ImageView.ScaleType getScaleType() {
            return this.mScaleType;
        }

        public void setScaleType(ImageView.ScaleType scaleType) {
            if (scaleType != null) {
                this.mScaleType = scaleType;
            }
        }
    }
}
