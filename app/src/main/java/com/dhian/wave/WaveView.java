package com.dhian.wave;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.animation.*;
import java.util.*;
import android.view.animation.*;
import android.os.*;
import android.content.*;
import android.preference.*;
import android.widget.*;
import android.util.*;

public class WaveView extends View {
    /**
     * +------------------------+
     * |<--wave length->        |______
     * |   /\          |   /\   |  |
     * |  /  \         |  /  \  | amplitude
     * | /    \        | /    \ |  |
     * |/      \       |/      \|__|____
     * |        \      /        |  |
     * |         \    /         |  |
     * |          \  /          |  |
     * |           \/           | water level
     * |                        |  |
     * |                        |  |
     * +------------------------+__|____
     */
    private static float level;
    private static final float DEFAULT_AMPLITUDE_RATIO = 0.05f;
    private static final float DEFAULT_WAVE_LENGTH_RATIO = 1.0f;
    private static final float DEFAULT_WATER_LEVEL_RATIO = 0.5f;
    private static final float DEFAULT_WAVE_SHIFT_RATIO = 0.0f;
    public static final ShapeType DEFAULT_WAVE_SHAPE = ShapeType.SQUARE;

    public enum ShapeType {
        SQUARE
        }

    // if true, the shader will display the wave
    private boolean mShowWave;

    // shader containing repeated waves
    private BitmapShader mWaveShader;
    // shader matrix
    private Matrix mShaderMatrix;
    // paint to draw wave
    private Paint mViewPaint;
    // paint to draw border
    private Paint mBorderPaint;

    private float mDefaultAmplitude;
    private float mDefaultWaterLevel;
    private float mDefaultWaveLength;
    private double mDefaultAngularFrequency;

    private float mAmplitudeRatio = DEFAULT_AMPLITUDE_RATIO;
    private float mWaveLengthRatio = DEFAULT_WAVE_LENGTH_RATIO;
    private float mWaterLevelRatio = DEFAULT_WATER_LEVEL_RATIO;
    private float mWaveShiftRatio = DEFAULT_WAVE_SHIFT_RATIO;

    private int mBehindWaveColor;
    private int mFrontWaveColor;
    private ShapeType mShapeType = DEFAULT_WAVE_SHAPE;

    private AnimatorSet mAnimatorSet;
    private Runnable mTicker;
    private Handler mHandler;
    private int warna;
    private boolean start;
  

    public WaveView(final Context context, AttributeSet attrs) {
        super(context, attrs);
        changeSettings();  
        init();
        initAnimation();
             
    }
    
    private void changeSettings(){
        mHandler = new Handler();
        mTicker = new Runnable() {
			public void run() {
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getContext());
                warna = pref.getInt("wave_color",0x543AE6F5);
                mFrontWaveColor = warna;
                mBehindWaveColor = adjustAlpha(warna,0.4f);
                int h = 100 - (pref.getInt("wave_level",30));           
                level = (float)h/100;
                start = pref.getBoolean("wave_start",true);
                if(start == true){
                    WaveView.this.setShowWave(true);
                    if (mAnimatorSet != null) {
                        mAnimatorSet.start();
                    }
                }else{
                    WaveView.this.setShowWave(false);
                }
                
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };
        mTicker.run();
    }
    public int adjustAlpha(int color, float factor) {
        int alpha = Math.round(Color.alpha(color) * factor);
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return Color.argb(alpha, red, green, blue);
    }

    private void init() {
        mShaderMatrix = new Matrix();
        mViewPaint = new Paint();
        mViewPaint.setAntiAlias(true);
    }

    public float getWaveShiftRatio() {
        return mWaveShiftRatio;
    }

    /**
     * Shift the wave horizontally according to <code>waveShiftRatio</code>.
     *
     * @param waveShiftRatio Should be 0 ~ 1. Default to be 0.
     *                       <br/>Result of waveShiftRatio multiples width of WaveView is the length to shift.
     */
    public void setWaveShiftRatio(float waveShiftRatio) {
        if (mWaveShiftRatio != waveShiftRatio) {
            mWaveShiftRatio = waveShiftRatio;
            invalidate();
        }
    }

    public float getWaterLevelRatio() {
        return mWaterLevelRatio;
    }

    /**
     * Set water level according to <code>waterLevelRatio</code>.
     *
     * @param waterLevelRatio Should be 0 ~ 1. Default to be 0.5.
     *                        <br/>Ratio of water level to WaveView height.
     */
    public void setWaterLevelRatio(float a) {
        if (mWaterLevelRatio != a) {
            mWaterLevelRatio = a;
            invalidate();
        }
    }

    public float getAmplitudeRatio() {
        return mAmplitudeRatio;
    }

    /**
     * Set vertical size of wave according to <code>amplitudeRatio</code>
     *
     * @param amplitudeRatio Default to be 0.05. Result of amplitudeRatio + waterLevelRatio should be less than 1.
     *                       <br/>Ratio of amplitude to height of WaveView.
     */
    public void setAmplitudeRatio(float amplitudeRatio) {
        if (mAmplitudeRatio != amplitudeRatio) {
            mAmplitudeRatio = amplitudeRatio;
            invalidate();
        }
    }

    public float getWaveLengthRatio() {
        return mWaveLengthRatio;
    }

    /**
     * Set horizontal size of wave according to <code>waveLengthRatio</code>
     *
     * @param waveLengthRatio Default to be 1.
     *                        <br/>Ratio of wave length to width of WaveView.
     */
    public void setWaveLengthRatio(float waveLengthRatio) {
        mWaveLengthRatio = waveLengthRatio;
    }

    public boolean isShowWave() {
        return mShowWave;
    }

    public void setShowWave(boolean showWave) {
        mShowWave = showWave;
    }

    //public void setBorder(int width, int color) {
    //  if (mBorderPaint == null) {
    //     mBorderPaint = new Paint();
    //     mBorderPaint.setAntiAlias(true);
    //   mBorderPaint.setStyle(Style.STROKE);
    // }
    // mBorderPaint.setColor(color);
    // mBorderPaint.setStrokeWidth(width);

    //  invalidate();
    //}

    public void setWaveColor(int behindWaveColor, int frontWaveColor) {
        mBehindWaveColor = behindWaveColor;
        mFrontWaveColor = frontWaveColor;

        // need to recreate shader when color changed
        mWaveShader = null;
        createShader();
        invalidate();
    }

    public void setShapeType(ShapeType shapeType) {
        mShapeType = shapeType;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        createShader();
    }
    
    

    /**
     * Create the shader with default waves which repeat horizontally, and clamp vertically
     */
    private void createShader() {
        
        
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;
        
        mDefaultAngularFrequency = 2.0f * Math.PI / DEFAULT_WAVE_LENGTH_RATIO / width;
        mDefaultAmplitude = height * DEFAULT_AMPLITUDE_RATIO;
        mDefaultWaterLevel = height * DEFAULT_WATER_LEVEL_RATIO;
        mDefaultWaveLength = width;

        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint wavePaint = new Paint();
        wavePaint.setStrokeWidth(2);
        wavePaint.setAntiAlias(true);

        // Draw default waves into the bitmap
        // y=Asin(ωx+φ)+h
        final int endX = width + 1;
        final int endY = height + 1;

        float[] waveY = new float[endX];

        wavePaint.setColor(mBehindWaveColor);
        for (int beginX = 0; beginX < endX; beginX++) {
            double wx = beginX * mDefaultAngularFrequency;
            float beginY = (float) (mDefaultWaterLevel + mDefaultAmplitude * Math.sin(wx));
            canvas.drawLine(beginX, beginY, beginX, endY, wavePaint);

            waveY[beginX] = beginY;
        }

        wavePaint.setColor(mFrontWaveColor);
        final int wave2Shift = (int) (mDefaultWaveLength / 4);
        for (int beginX = 0; beginX < endX; beginX++) {
            canvas.drawLine(beginX, waveY[(beginX + wave2Shift) % endX], beginX, endY, wavePaint);
        }

        // use the bitamp to create the shader
        mWaveShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.CLAMP);
        mViewPaint.setShader(mWaveShader);
                
    }
    
    

    @Override
    protected void onDraw(Canvas canvas) {
        
        // modify paint shader according to mShowWave state
        if (mShowWave && mWaveShader != null) {
            // first call after mShowWave, assign it to our paint
            if (mViewPaint.getShader() == null) {
                mViewPaint.setShader(mWaveShader);
            }
            
            // sacle shader according to mWaveLengthRatio and mAmplitudeRatio
            // this decides the size(mWaveLengthRatio for width, mAmplitudeRatio for height) of waves
            mShaderMatrix.setScale(
                mWaveLengthRatio / DEFAULT_WAVE_LENGTH_RATIO,
                mAmplitudeRatio / DEFAULT_AMPLITUDE_RATIO,
                0,
                mDefaultWaterLevel);
            // translate shader according to mWaveShiftRatio and mWaterLevelRatio
            // this decides the start position(mWaveShiftRatio for x, mWaterLevelRatio for y) of waves
            mShaderMatrix.postTranslate(
                mWaveShiftRatio * getWidth(),
                (level- mWaterLevelRatio) * getHeight());

            // assign matrix to invalidate the shader
            mWaveShader.setLocalMatrix(mShaderMatrix);

            float borderWidth = mBorderPaint == null ? 0f : mBorderPaint.getStrokeWidth();
            switch (mShapeType) {

                case SQUARE:

                    canvas.drawRect(borderWidth, borderWidth, getWidth() - borderWidth,
                                    getHeight() - borderWidth, mViewPaint);
                    break;
            }
        } else {
            mViewPaint.setShader(null);
        }
        
    }
    private void initAnimation() {
        List<Animator> animators = new ArrayList<>();

        // horizontal animation.
        // wave waves infinitely.
        ObjectAnimator waveShiftAnim = ObjectAnimator.ofFloat(
            this, "waveShiftRatio", 0f, 1f);
        waveShiftAnim.setRepeatCount(ValueAnimator.INFINITE);
        waveShiftAnim.setDuration(1000);
        waveShiftAnim.setInterpolator(new LinearInterpolator());
        animators.add(waveShiftAnim);

        // vertical animation.
        // water level increases from 0 to center of WaveView
        ObjectAnimator waterLevelAnim = ObjectAnimator.ofFloat(
            this, "waterLevelRatio", 0f, 0.5f);
        waterLevelAnim.setDuration(4000);
        waterLevelAnim.setInterpolator(new DecelerateInterpolator());
        animators.add(waterLevelAnim);

        // amplitude animation.
        // wave grows big then grows small, repeatedly
        ObjectAnimator amplitudeAnim = ObjectAnimator.ofFloat(
            this, "amplitudeRatio", 0.0001f, 0.05f);
        amplitudeAnim.setRepeatCount(ValueAnimator.INFINITE);
        amplitudeAnim.setRepeatMode(ValueAnimator.REVERSE);
        amplitudeAnim.setDuration(5000);
        amplitudeAnim.setInterpolator(new LinearInterpolator());
        animators.add(amplitudeAnim);

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.playTogether(animators);

        
    }

   
    

    
    
    
}
