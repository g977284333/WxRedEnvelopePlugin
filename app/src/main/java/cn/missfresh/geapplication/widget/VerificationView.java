package cn.missfresh.geapplication.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

import cn.missfresh.geapplication.R;

/**
 * Created by Wu_youming on 2016-02-25日 15:05.
 * Everyday is another day, keep going.
 */
public class VerificationView extends View {

    private int textSize;
    private static final int DEFAULT_TEXT_SIZE = 14;
    private int verificationLength;
    private static final int DEFAULT_VERIFICATION_LENGTH = 4;
    private int fresh_duration;
    private static final int DEFAULT_FRESH_DURATION = 2 * 1000;

    private String verification = "";
    private static final String code = "1234567890abcdefghijkmnpqrstuvwxyzABCDEFGHIJKLMNPQRSTUVWXYZ";


    private Random random = new Random();

    private Paint mPaint = new Paint();

    private int[] spacings;

    private Handler mHandler = null;
    private Runnable mRunnable;

    public VerificationView(Context context) {
        super(context);
        init();
    }

    public VerificationView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VerificationView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.VerificationView, defStyleAttr, 0);
        textSize = a.getDimensionPixelSize(R.styleable.VerificationView_textSize, DEFAULT_TEXT_SIZE);
        verificationLength = a.getInteger(R.styleable.VerificationView_verification_length, DEFAULT_VERIFICATION_LENGTH);
        fresh_duration = a.getInteger(R.styleable.VerificationView_fresh_duration, DEFAULT_FRESH_DURATION);
        a.recycle();
        init();

    }

    private void init() {
        spacings = new int[verificationLength];
        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                randomString();
                postInvalidate();
                mHandler.postAtTime(mRunnable, SystemClock.uptimeMillis() + fresh_duration);
            }
        };
        mHandler.post(mRunnable);
    }

    /**
     * 获取验证码的字符串
     *
     * @return
     */
    public String getVerification() {
        return verification;
    }

    private void setVerification(String verification) {
        this.verification = verification;
    }

    private void randomString() {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < verificationLength; i++) {
            builder.append(code.toCharArray()[random.nextInt(code.length())]);
            int space = random.nextInt(textSize / 2);
            spacings[i] = random.nextBoolean() ? space : -space;
        }
        setVerification(builder.toString());
    }

    /**
     * 更新验证码字符串
     */
    public void update() {
        mHandler.removeCallbacks(mRunnable);
        mHandler.post(mRunnable);
    }

    /**
     * 设置验证码更新的周期
     *
     * @param duration millisecond
     */
    public void setDuration(int duration) {
        this.fresh_duration = duration;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        for (int i = 0; i < 10; i++) {
            mPaint = new Paint();
            mPaint.setColor(Color.argb(random.nextInt(128), random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(3);
            canvas.drawLine(random.nextInt(getWidth()), random.nextInt(getHeight()), random.nextInt(getWidth()), random.nextInt(getHeight()), mPaint);
        }
        int space = getPaddingLeft();
        for (int i = 0; i < verification.length(); i++) {
            mPaint = new Paint();
            mPaint.setAntiAlias(true);
            mPaint.setTextSize(textSize);
            mPaint.setColor(Color.argb(255, random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            mPaint.setStrokeJoin(Paint.Join.ROUND);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeWidth(3);
            space = space + textSize + spacings[i];
            canvas.drawText(verification, i, i + 1, space, textSize * 3 / 2 + spacings[i], mPaint);
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        int space = 0;
        for (int i = 0; i < spacings.length; i++) {
            space = space + spacings[0];
        }

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else {

            int desired = getPaddingLeft() + textSize * verificationLength * 3 / 2 + space + getPaddingRight();
            width = desired;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else {
            int desired = getPaddingTop() + textSize * 2 + getPaddingBottom();
            height = desired;

        }


        setMeasuredDimension(width, height);

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null)
            mHandler.removeCallbacks(mRunnable);
    }
}
